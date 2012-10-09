package com.metova.android.util.http.request;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

/**
 * An {@link HttpPost} request which accepts form-URL-encoded parameters.
 */
public class HttpFormPost extends HttpPost {

    public HttpFormPost(String url, List<NameValuePair> formParams) throws UnsupportedEncodingException {

        super( url );
        setEntity( new UrlEncodedFormEntity( formParams ) );
    }
}
