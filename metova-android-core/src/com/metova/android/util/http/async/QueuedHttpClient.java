package com.metova.android.util.http.async;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.metova.android.util.Assertions;
import com.metova.android.util.concurrent.ThreadPool;
import com.metova.android.util.http.HttpClients;
import com.metova.android.util.http.response.Response;

/**
 * HTTP client that places {@link HttpUriRequest}s on a {@link Queue} for asynchronous dispatch. Clients may provide their own {@link BlockingQueue}s, {@link ThreadPoolExecutor}s, and {@link HttpClient}s if desired.
 * <p/>
 * Requests will be dispatched in FIFO order. If no {@link ThreadPoolExecutor} is provided, a single-threaded {@link ThreadPool} is used, which guarantees that requests are dispatched serially.  
 * @author Ron Unger
 *
 */
public class QueuedHttpClient {

    private static final String TAG = QueuedHttpClient.class.getSimpleName();

    private final BlockingQueue<AsyncHttpUriRequest> queue;
    private final ExecutorService executor;
    private final HttpClient httpClient;

    private boolean performDispatches;
    private Runnable dispatchRunnable;
    private Future<?> dispatchFuture;

    public QueuedHttpClient() {

        this( new LinkedBlockingQueue<AsyncHttpUriRequest>() );
    }

    public QueuedHttpClient(BlockingQueue<AsyncHttpUriRequest> queue) {

        this( new ThreadPool( 1 ), queue );
    }

    public QueuedHttpClient(ThreadPoolExecutor executor, BlockingQueue<AsyncHttpUriRequest> queue) {

        this( new DefaultHttpClient(), executor, queue );
    }

    public QueuedHttpClient(final HttpClient httpClient, final ExecutorService executor, final BlockingQueue<AsyncHttpUriRequest> queue) {

        Assertions.notNull( "queue", queue );
        Assertions.notNull( "executor", executor );
        Assertions.notNull( "httpClient", httpClient );

        this.queue = queue;
        this.executor = executor;
        this.httpClient = httpClient;

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Constructed with queue=" + queue + "; executor=" + executor + "; httpClient=" + httpClient );
        }

        this.performDispatches = false;
        this.dispatchRunnable = new DispatchRunnable();
    }

    /**
     * Convenience method for {@link #submit(HttpUriRequest, AsyncHttpResponseCallback)} that 
     * passes {@code null} for the callback;
     * 
     * @param request
     */
    public void submit( HttpUriRequest request ) {

        submit( request, null );
    }

    /**
     * Submits the {@link HttpUriRequest} for asynchronous dispatch. Optionally
     * allows specifying an {@link AsyncHttpResponseCallback}.
     * 
     * @param request the outgoing request to dispatch
     * @param callback the callback to receive notifications when a response is received. May be null.
     */
    public void submit( HttpUriRequest request, AsyncHttpResponseCallback callback ) {

        submit( new AsyncHttpUriRequest( request, callback ) );
    }

    public void submit( AsyncHttpUriRequest asyncHttpUriRequest ) {

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {

            final HttpUriRequest request = asyncHttpUriRequest.getRequest();
            final AsyncHttpResponseCallback callback = asyncHttpUriRequest.getCallback();

            StringBuilder stringBuilder = new StringBuilder( "Request submitted: " );
            stringBuilder.append( request.getRequestLine() ).append( " to " ).append( request.getURI() );
            stringBuilder.append( ( callback == null ) ? " without callback" : " with callback" );
            Log.d( TAG, "Request submitted: " + request.getRequestLine() + " to " + request.getURI() );
        }

        //TODO save the callback
        queue.add( asyncHttpUriRequest );
    }

    public void start() {

        Log.d( TAG, "Received request to start dispatches." );
        synchronized (queue) {
            performDispatches = true;
            //start the task
            dispatchFuture = executor.submit( dispatchRunnable );
            Log.d( TAG, "Successfully started dispatch task." );
        }
    }

    public void stop() {

        Log.d( TAG, "Received request to stop dispatches." );
        synchronized (queue) {
            performDispatches = false;
            //stop the task
            dispatchFuture.cancel( true );
            Log.d( TAG, "Successfully stopped dispatch task. Remaining queue items: " + queue.size() );
        }
    }

    protected void dispatch( final HttpUriRequest request, final AsyncHttpResponseCallback callback ) {

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Attempting to dispatch request: " + request.getRequestLine() );
        }
        if ( Log.isLoggable( TAG, Log.VERBOSE ) ) {
            Log.v( TAG, "Full request: " + request );
        }

        final Response response = HttpClients.execute( httpClient, request );

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Completed http call with code: " + response.getStatusCode() );
            if ( callback != null ) {
                Log.d( TAG, "Invoking callback." );
            }
        }

        if ( callback != null ) {

            executor.submit( new Runnable() {

                @Override
                public void run() {

                    callback.onResponseReceived( response );
                }
            } );
        }
    }

    private class DispatchRunnable implements Runnable {

        public void run() {

            while (performDispatches) {

                try {

                    AsyncHttpUriRequest asyncHttpUriRequest = queue.take();
                    HttpUriRequest request = asyncHttpUriRequest.getRequest();
                    AsyncHttpResponseCallback callback = asyncHttpUriRequest.getCallback();

                    //prevent interruption while dispatching
                    synchronized (queue) {
                        dispatch( request, callback );
                    }
                }
                catch (InterruptedException e) {
                    //TODO ASDK-116
                }

            }
        }
    }

}
