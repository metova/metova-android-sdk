package com.metova.android.util.http.request;

import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;

public class HttpMultipartPost extends HttpPost {

    public HttpMultipartPost(String url, Map<String, AbstractContentBody> parts) {

        super( url );

        MultipartEntity entity = new MultipartEntity();
        for (String key : parts.keySet()) {
            entity.addPart( key, parts.get( key ) );
        }

        setEntity( entity );
    }
}
