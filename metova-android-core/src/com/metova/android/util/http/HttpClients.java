package com.metova.android.util.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.metova.android.util.http.response.Response;

/**
 * Utility methods for handling HTTP requests.
 */
public final class HttpClients {

    /**
     * Executes the specified request. 
     * 
     * @param request
     * @return 
     */
    public static Response execute( HttpUriRequest request ) {

        return execute( new DefaultHttpClient(), request );
    }

    /**
     * Executes the specified request with the specified client. 
     * 
     * @param request
     * @return 
     */
    public static Response execute( HttpClient client, HttpUriRequest request ) {

        Response response = new Response();
        try {
            HttpResponse httpResponse = client.execute( request );
            response.setHttpResponse( httpResponse );
        }
        catch (ClientProtocolException e) {
            Log.e( "HttpClients#execute", "Problem encountered with URI " + request.getURI(), e );
            response.setFailedRequest( true );
            response.setReason( "Failure to resolve URI." );
        }
        catch (IOException e) {
            Log.e( "HttpClients#execute", "Failure to process request to " + request.getURI(), e );
            response.setFailedRequest( true );
            response.setReason( "Failure to process request." );
        }

        return response;
    }
}
