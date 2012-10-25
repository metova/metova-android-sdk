package com.metova.android.util.http.async;

import android.util.Log;

import com.metova.android.util.http.response.Response;

public class DefaultRetryStrategy implements RetryStrategy {

    private static final String TAG = DefaultRetryStrategy.class.getSimpleName();

    private static final int MAX_RETRIES = 3;

    private int retries = 0;

    public DefaultRetryStrategy() {

        //instantiated by reflection, so explicit null constructor needed
    }

    @Override
    public boolean onRetry( Response response, Throwable throwable ) {

        if ( throwable != null ) {
            //if we got an exception, just stop... something is wrong
            return false;
        }

        if ( retries < MAX_RETRIES ) {

            Log.d( TAG, "Attempt #" + retries + " failed. Retrying." );
            retries++;
            return true;
        }
        else {

            Log.d( TAG, "Max number of retries (" + MAX_RETRIES + " reached." );
            return false;
        }
    }
}
