package com.metova.android.util.http.async;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import com.metova.android.util.Assertions;

public class AsyncHttpRequestBase {

    private final HttpRequestBase request;
    private final Class<? extends AsyncHttpResponseCallback> callbackType;

    /**
     * Convenience constructor that invokes {@link #AsyncHttpUriRequest(HttpUriRequest, AsyncHttpResponseCallback)}
     * with a {@code null} callback.
     * 
     *  @param request the request to be dispatched
     */
    public AsyncHttpRequestBase(final HttpRequestBase request) {

        this( request, null );
    }

    /**
     * Creates a new {@link AsyncHttpRequestBase}.
     * <p/>
     * This constructor allows the inclusion of a callback type (not required). This type
     * will be instantiated and invoked when a response occurs. This class obviously needs
     * to be stateless since instantiation will be controlled by the {@link QueuedHttpClient}.
     * The reason that this approach is taken is to allow persistence of {@link AsyncHttpRequestBase}s,
     * which would not be possible if an object instance were provided for a callback.
     * 
     * @param request the request to be dispatched
     * @param a type to be instantiated for callback
     */
    public AsyncHttpRequestBase(final HttpRequestBase request, final Class<? extends AsyncHttpResponseCallback> callbackType) {

        Assertions.notNull( "request", request );
        this.request = request;
        this.callbackType = callbackType;
    }

    public Class<? extends AsyncHttpResponseCallback> getCallbackType() {

        return callbackType;
    }

    public HttpRequestBase getRequest() {

        return request;
    }
}
