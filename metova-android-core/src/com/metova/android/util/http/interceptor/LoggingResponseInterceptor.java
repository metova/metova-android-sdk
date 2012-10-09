package com.metova.android.util.http.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * Logs the entire response payload, including status line, headers, and full body.  Note that 
 * this method is horribly inefficient and should not be used in production applications.
 */
public class LoggingResponseInterceptor implements HttpResponseInterceptor {

    private static final String TAG = "HTTP << ";

    public void process( HttpResponse response, HttpContext context ) throws HttpException, IOException {

        Log.d( TAG, response.getStatusLine().toString() );
        HeaderIterator headerIterator = response.headerIterator();
        while (headerIterator.hasNext()) {

            Header header = (Header) headerIterator.next();
            Log.d( TAG, header.toString() );
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo( outputStream );
        Log.d( TAG, " " );
        Log.d( TAG, outputStream.toString() );
        outputStream.close();

        response.getEntity().consumeContent();

        ByteArrayEntity entity = new ByteArrayEntity( outputStream.toByteArray() );
        response.setEntity( entity );
    }
}
