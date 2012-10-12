package com.metova.android.util.http.async;

import com.metova.android.util.http.response.Response;

/**
 * Used to provide a callback cability with {@link QueuedHttpClient}.
 * @author runger
 *
 */
//TODO YMKA-1178 make this Serializable?
public interface AsyncHttpResponseCallback {

    public void onResponseReceived( Response response );
}
