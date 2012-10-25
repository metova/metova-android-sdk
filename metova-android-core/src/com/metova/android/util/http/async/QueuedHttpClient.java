package com.metova.android.util.http.async;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
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
 *
 */
public class QueuedHttpClient {

    private static final String TAG = QueuedHttpClient.class.getSimpleName();

    private final BlockingQueue<AsyncHttpRequestBase> queue;
    private final ExecutorService executor;
    private final HttpClient httpClient;
    private final Class<? extends RetryStrategy> retryStrategyType;

    private boolean performDispatches;
    private Runnable dispatchRunnable;
    private Future<?> dispatchFuture;

    public QueuedHttpClient() {

        this( new LinkedBlockingQueue<AsyncHttpRequestBase>() );
    }

    public QueuedHttpClient(BlockingQueue<AsyncHttpRequestBase> queue) {

        this( new ThreadPool( 1 ), queue );
    }

    public QueuedHttpClient(ThreadPoolExecutor executor, BlockingQueue<AsyncHttpRequestBase> queue) {

        this( new DefaultHttpClient(), executor, queue, DefaultRetryStrategy.class );
    }

    /**
     * 
     * @param httpClient the HttpClient to use for outgoing requests. May not be null.
     * @param executor the ExecutorService to use for asynchronous request dispatching. May not be null.
     * @param queue the Queue on which unsent requests will be held. May not be null.
     * @param retryStrategyType the optional RetryStrategy type to be used for failed requests. May be null.
     */
    public QueuedHttpClient(final HttpClient httpClient, final ExecutorService executor, final BlockingQueue<AsyncHttpRequestBase> queue, final Class<? extends RetryStrategy> retryStrategyType) {

        Assertions.notNull( "queue", queue );
        Assertions.notNull( "executor", executor );
        Assertions.notNull( "httpClient", httpClient );

        this.queue = queue;
        this.executor = executor;
        this.httpClient = httpClient;
        this.retryStrategyType = retryStrategyType;

        if ( Log.isLoggable( TAG, Log.DEBUG ) ) {
            Log.d( TAG, "Constructed with queue=" + queue + "; executor=" + executor + "; httpClient=" + httpClient + "; retryStrategyType=" + retryStrategyType );
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
    public void submit( HttpRequestBase request ) {

        submit( request, null );
    }

    /**
     * Submits the {@link HttpUriRequest} for asynchronous dispatch. Optionally
     * allows specifying an {@link AsyncHttpResponseCallback}.
     * 
     * @param request the outgoing request to dispatch
     * @param callback the type to be instantiated and invoked to receive notifications when a response is received. May be null.
     */
    public void submit( HttpRequestBase request, Class<? extends AsyncHttpResponseCallback> callbackType ) {

        submit( new AsyncHttpRequestBase( request, callbackType ) );
    }

    public void submit( AsyncHttpRequestBase asyncRequest ) {

        final HttpUriRequest request = asyncRequest.getRequest();
        final Class<? extends AsyncHttpResponseCallback> callbackType = asyncRequest.getCallbackType();

        StringBuilder stringBuilder = new StringBuilder( "Request submitted: " );
        stringBuilder.append( request.getRequestLine() ).append( " to " ).append( request.getURI() );
        stringBuilder.append( ( callbackType == null ) ? " without callback" : " with callback" );
        Log.d( TAG, "Request submitted: " + request.getRequestLine() + " to " + request.getURI() );

        getQueue().add( asyncRequest );

        Log.d( TAG, "Current queue size: " + getQueue().size() );
    }

    public void start() {

        Log.d( TAG, "Received request to start dispatches. " + getQueue().size() + " items queued for dispatch." );
        synchronized (queue) {
            performDispatches = true;
            //start the task
            dispatchFuture = getExecutor().submit( dispatchRunnable );
            Log.d( TAG, "Successfully started dispatch task." );
        }
    }

    public void stop() {

        Log.d( TAG, "Received request to stop dispatches." );
        synchronized (queue) {
            performDispatches = false;
            //stop the task
            if ( dispatchFuture != null ) {

                dispatchFuture.cancel( true );
            }
            Log.d( TAG, "Successfully stopped dispatch task. Remaining queue items: " + getQueue().size() );
        }
    }

    protected void dispatch( final HttpRequestBase request, final Class<? extends AsyncHttpResponseCallback> callbackType ) {

        Log.d( TAG, "Attempting to dispatch request: " + request.getRequestLine() );

        RetryStrategy retryStrategy = null;

        if ( retryStrategyType != null ) {
            try {
                retryStrategy = retryStrategyType.newInstance();
            }
            catch (IllegalAccessException e) {
                Log.w( TAG, "Error encountered instantiating RetryStrategy type: " + retryStrategyType, e );
            }
            catch (InstantiationException e) {
                Log.w( TAG, "Error encountered instantiating RetryStrategy type: " + retryStrategyType, e );
            }
        }

        final Response response = dispatchWithRetries( request, retryStrategy );

        if ( response == null ) {

            Log.d( TAG, "Abandoning attempts to dispatch request " + request.getRequestLine() );
        }
        else {

            Log.d( TAG, "Completed request '" + request.getRequestLine() + "' with code: " + response.getStatusCode() );

            invokeCallback( callbackType, response );
        }
    }

    private Response dispatchWithRetries( HttpRequestBase request, RetryStrategy retryStrategy ) {

        boolean retry = true;
        Response response = null;
        while (retry) {

            Throwable throwable = null;
            try {

                response = HttpClients.execute( getHttpClient(), request );
                Log.d( TAG, "Received response " + response.getHttpResponse().getStatusLine() + " for request " + request.getRequestLine() );
            }
            catch (Throwable t) {

                Log.w( TAG, "Error encountered sending request.", t );
                throwable = t;
            }

            if ( response != null && response.isSuccessful() ) {

                retry = false;
            }
            else {

                retry = ( null == retryStrategy ) ? false : retryStrategy.onRetry( response, throwable );
            }
        }
        return response;
    }

    private void invokeCallback( Class<? extends AsyncHttpResponseCallback> callbackType, Response response ) {

        if ( callbackType != null ) {

            Log.d( TAG, "Invoking callback." );
            try {

                AsyncHttpResponseCallback callback = callbackType.newInstance();
                callback.onResponseReceived( response );
            }
            catch (IllegalAccessException e) {

                Log.e( TAG, "Error attempting to instantiate callback type.", e );
            }
            catch (InstantiationException e) {

                Log.e( TAG, "Error attempting to instantiate callback type.", e );
            }
        }
    }

    private class DispatchRunnable implements Runnable {

        public void run() {

            while (performDispatches) {

                try {

                    final AsyncHttpRequestBase asyncHttpUriRequest = getQueue().take();
                    final HttpRequestBase request = asyncHttpUriRequest.getRequest();
                    final Class<? extends AsyncHttpResponseCallback> callbackType = asyncHttpUriRequest.getCallbackType();

                    //prevent interruption while dispatching
                    synchronized (queue) {
                        dispatch( request, callbackType );
                    }
                }
                catch (InterruptedException e) {
                    /*
                     * This is not an error. We intend to interrupt this task via the stop() method
                     * whenever network connectivity is lost, so let's not throw up errors about it.
                     */
                    Log.d( TAG, "Interrupted while waiting for new dispatchable http request on queue." );
                }
                catch (Exception e) {
                    Log.e( TAG, "Failed to dispatch request.", e );
                }

            }
        }
    }

    public ExecutorService getExecutor() {

        return executor;
    }

    public HttpClient getHttpClient() {

        return httpClient;
    }

    public BlockingQueue<AsyncHttpRequestBase> getQueue() {

        return queue;
    }

}
