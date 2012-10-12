package com.metova.android.util.http.async;

import org.apache.http.client.methods.HttpUriRequest;

import com.metova.android.util.Assertions;

public class AsyncHttpUriRequest {

    private final HttpUriRequest request;
    private final AsyncHttpResponseCallback callback;

    /**
     * Convenience constructor that invokes {@link #AsyncHttpUriRequest(HttpUriRequest, AsyncHttpResponseCallback)}
     * with a {@code null} callback.
     * 
     *  @param request the request to be dispatched
     */
    public AsyncHttpUriRequest(final HttpUriRequest request) {

        this( request, null );
    }

    /**
     * 
     * @param request the request to be dispatched
     * @param callback the optional callback to be notified when the request is answered
     */
    public AsyncHttpUriRequest(final HttpUriRequest request, final AsyncHttpResponseCallback callback) {

        Assertions.notNull( "request", request );
        this.request = request;

        this.callback = callback;
    }

    public AsyncHttpResponseCallback getCallback() {

        return callback;
    }

    public HttpUriRequest getRequest() {

        return request;
    }
}
