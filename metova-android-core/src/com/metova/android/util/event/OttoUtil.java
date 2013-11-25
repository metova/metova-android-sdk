package com.metova.android.util.event;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.metova.android.event.DeadEventHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Convenience methods for dealing with a {@link Bus} and {@link DeadEventHandler}.
 */
public class OttoUtil {

    private static final String TAG = OttoUtil.class.getSimpleName();

    private OttoUtil() {

    }

    /**
     * Posts a message to the {@link Bus} as the only one of its kind. This is accomplished by purging
     * any other messages of the same type from the {@link DeadEventHandler} before the message is put on the Bus. 
     * @param message the message to post
     * @param context a Context used to post the message on the bus on the main thread
     * @param ottoBus the message bus
     * @param deadEventHandler the dead event handler to purge
     * 
     * @see #postEvent(Object, Context, Bus)
     * @see DeadEventHandler#purgeMessages(Class...)
     */
    public static void postUniqueEvent( final Object message, final Context context, final Bus ottoBus, final DeadEventHandler deadEventHandler ) {

        deadEventHandler.purgeMessages( message.getClass() );
        postEvent( message, context, ottoBus );
    }

    /**
     * Convenience method to post a message to the {@link Bus} after ensuring that the post occurs on the 
     * main thread.
     * <p/>
     * This makes the assumption that the Bus was instantiated with {@code ThreadEnforcer.MAIN} enforcement
     * policy (the default).
     * @param message the message to post
     * @param context a Context used to post the message on the bus on the main thread 
     * @param ottoBus the message bus
     * @see ThreadEnforcer
     */
    public static void postEvent( final Object message, final Context context, final Bus ottoBus ) {

        /*
         * The Otto bus is configured to enforce that messages are passed on the main thread,
         * so we need to get on the main thread to post this event
         */
        new Handler( context.getMainLooper() ).post( new Runnable() {

            @Override
            public void run() {

                Log.d( TAG, "Posting " + message.getClass().getName() + " to bus " + ottoBus );
                ottoBus.post( message );
            }
        } );
    }

}
