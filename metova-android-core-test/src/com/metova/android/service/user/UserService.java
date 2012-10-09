package com.metova.android.service.user;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.metova.android.util.http.HttpClients;
import com.metova.android.util.http.request.HttpFormPost;
import com.metova.android.util.http.response.Response;

public final class UserService {

    public static Response authenticate( String email, String password ) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add( new BasicNameValuePair( "user[email]", email ) );
        params.add( new BasicNameValuePair( "user[password]", password ) );

        HttpFormPost request = null;
        Response response = null;
        try {
            request = new HttpFormPost( "http://testing.metova.com/users/sign_in.json", params );
        }
        catch (UnsupportedEncodingException e) {
            Log.e( "UserService#authenticate", "Unsupported encoding for form post parameters.", e );
            response = new Response();
            response.setFailedRequest( true );
            response.setReason( "Invalid username or password." );
        }

        if ( response != null ) {
            return response;
        }

        return HttpClients.execute( request );
    }
}
