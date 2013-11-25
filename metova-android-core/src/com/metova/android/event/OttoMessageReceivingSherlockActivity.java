package com.metova.android.event;

import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;

/**
 * Equivalent to {@link OttoMessageReceivingActivity}, but extends {@link RoboSherlockFragmentActivity} for
 * cases where that is the desired hierarchy.
 * 
 *<pre>
 * {@code
 * <dependency>
 *   <groupId>com.github.rtyley</groupId>
 *   <artifactId>roboguice-sherlock</artifactId>
 * </dependency>
 * }
 * </pre>
 * 
 * @see OttoMessageReceivingActivity
 */
public abstract class OttoMessageReceivingSherlockActivity extends RoboSherlockFragmentActivity {

    private final String tag = getClass().getSimpleName();

    @Inject
    private Bus ottoBus;
    @Inject
    private DeadEventHandler deadEventHandler;

    @Override
    protected void onResume() {

        super.onResume();

        Log.d( tag, "Registering on bus " + getOttoBus() );
        getOttoBus().register( this );

        Log.d( tag, "Requesting re-fire of dead events." );
        getDeadEventHandler().refireDeadEvents();
    }

    @Override
    protected void onPause() {

        Log.d( tag, "Unregistering on bus" + getOttoBus() );
        getOttoBus().unregister( this );

        super.onPause();
    }

    protected Bus getOttoBus() {

        return ottoBus;
    }

    protected void setOttoBus( Bus ottoBus ) {

        this.ottoBus = ottoBus;
    }

    protected DeadEventHandler getDeadEventHandler() {

        return deadEventHandler;
    }

    protected void setDeadEventHandler( DeadEventHandler deadEventHandler ) {

        this.deadEventHandler = deadEventHandler;
    }
}
