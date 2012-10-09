package com.metova.android.util.http.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * Logs the entire request payload, including request line, heads, and full body.  Note that 
 * this method is horribly inefficient and should not be used in production applications.
 */
public class LoggingRequestInterceptor implements HttpRequestInterceptor {

    private static final String TAG = "HTTP >> ";

    public void process( HttpRequest request, HttpContext context ) throws HttpException, IOException {

        Log.d( TAG, request.getRequestLine().toString() );
        HeaderIterator headerIterator = request.headerIterator();
        while (headerIterator.hasNext()) {

            Header header = (Header) headerIterator.next();
            Log.d( TAG, header.toString() );
        }

        if ( request instanceof EntityEnclosingRequestWrapper ) {

            EntityEnclosingRequestWrapper entityRequest = (EntityEnclosingRequestWrapper) request;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            entityRequest.getEntity().writeTo( outputStream );
            Log.d( TAG, " " );
            Log.d( TAG, outputStream.toString() );
            outputStream.close();

            entityRequest.getEntity().consumeContent();

            ByteArrayEntity entity = new ByteArrayEntity( outputStream.toByteArray() );
            entityRequest.setEntity( entity );
        }
    }
}
