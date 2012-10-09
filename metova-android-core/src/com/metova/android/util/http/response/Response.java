package com.metova.android.util.http.response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 * A wrapper for {@link HttpResponse} objects which simplifies error handling 
 * for failed requests.
 */
public final class Response {

    private boolean failedRequest;
    private HttpResponse httpResponse;
    private String reason;

    /**
     * Determines if the response indicates a successful request.  A response is considered 
     * successful if it has not been explicitly marked as a failed request using 
     * {@link #setFailedRequest(boolean)}, and if the response code is 2XX.
     * 
     * @return true if the response indicates success.
     */
    public boolean isSuccessful() {

        if ( isFailedRequest() ) {
            return false;
        }

        return ( getStatusCode() >= 200 && getStatusCode() < 300 );
    }

    public int getStatusCode() {

        if ( isFailedRequest() ) {
            throw new IllegalStateException( "Request failed, so no statusCode is available." );
        }

        return getHttpResponse().getStatusLine().getStatusCode();
    }

    public HttpEntity getEntity() {

        if ( isFailedRequest() ) {
            throw new IllegalStateException( "Request failed, so no entity is available." );
        }

        return getHttpResponse().getEntity();
    }

    private boolean isFailedRequest() {

        return failedRequest;
    }

    public void setFailedRequest( boolean failedRequest ) {

        this.failedRequest = failedRequest;
    }

    public HttpResponse getHttpResponse() {

        if ( isFailedRequest() ) {
            throw new IllegalStateException( "Can not call getHttpResponse() on a failed request." );
        }

        return httpResponse;
    }

    public void setHttpResponse( HttpResponse httpResponse ) {

        if ( isFailedRequest() ) {
            throw new IllegalStateException( "Can not call setHttpResponse() on a failed request." );
        }

        this.httpResponse = httpResponse;
    }

    public String getReason() {

        if ( isFailedRequest() ) {
            return reason;
        }

        return getHttpResponse().getStatusLine().getReasonPhrase();
    }

    public void setReason( String reason ) {

        this.reason = reason;
    }
}
