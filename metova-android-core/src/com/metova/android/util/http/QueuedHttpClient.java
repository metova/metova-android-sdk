package com.metova.android.util.http;

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

import com.metova.android.util.concurrent.ThreadPool;
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

    private final BlockingQueue<HttpUriRequest> queue;
    private final ExecutorService executor;
    private final HttpClient httpClient;

    private boolean performDispatches;
    private Runnable dispatchRunnable;
    private Future<?> dispatchFuture;

    public QueuedHttpClient() {

        this( new LinkedBlockingQueue<HttpUriRequest>() );
    }

    public QueuedHttpClient(BlockingQueue<HttpUriRequest> queue) {

        this( new ThreadPool( 1 ), queue );
    }

    public QueuedHttpClient(ThreadPoolExecutor executor, BlockingQueue<HttpUriRequest> queue) {

        this( new DefaultHttpClient(), executor, queue );
    }

    public QueuedHttpClient(final HttpClient httpClient, final ExecutorService executor, final BlockingQueue<HttpUriRequest> queue) {

        if ( queue == null ) {

            throw new NullPointerException( "'queue' argument may not be null." );
        }
        if ( executor == null ) {

            throw new NullPointerException( "'executor' argument may not be null." );
        }
        if ( httpClient == null ) {

            throw new NullPointerException( "'httpClient' argument may not be null." );
        }

        this.queue = queue;
        this.executor = executor;
        this.httpClient = httpClient;

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Constructed with queue=" + queue + "; executor=" + executor + "; httpClient=" + httpClient );
        }

        this.performDispatches = false;
        this.dispatchRunnable = new DispatchRunnable();
    }

    public void submit( HttpUriRequest request ) {

        /*
         * We should consider having a callback that clients can submit to be notified
         * when the request gets a response (and provide the response)
         */
        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Request submitted: " + request.getRequestLine() );
        }
        if ( Log.isLoggable( TAG, Log.VERBOSE ) ) {
            Log.v( TAG, "Full request: " + request );
        }
        queue.add( request );
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

    protected void dispatch( HttpUriRequest request ) {

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Attempting to dispatch request: " + request.getRequestLine() );
        }
        if ( Log.isLoggable( TAG, Log.VERBOSE ) ) {
            Log.v( TAG, "Full request: " + request );
        }

        final Response response = HttpClients.execute( httpClient, request );
        //TODO ASDK-116 need to do something with this response. clients provide callback?

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Completed http call with code: " + response.getStatusCode() );
        }
        if ( Log.isLoggable( TAG, Log.VERBOSE ) ) {
            Log.v( TAG, "Full response: " + response.getHttpResponse() );
        }
    }

    private class DispatchRunnable implements Runnable {

        public void run() {

            while (performDispatches) {
                try {
                    HttpUriRequest request = queue.take();
                    //prevent interruption while dispatching
                    synchronized (queue) {
                        dispatch( request );
                    }
                }
                catch (InterruptedException e) {
                    //TODO ASDK-116
                }

            }
        }
    }

}
