package com.metova.android.util.http.async;

import com.metova.android.util.http.response.Response;

/**
 * Used to provide a callback cability with {@link QueuedHttpClient}.
 *
 */
public interface AsyncHttpResponseCallback {

    public void onResponseReceived( Response response );
}
