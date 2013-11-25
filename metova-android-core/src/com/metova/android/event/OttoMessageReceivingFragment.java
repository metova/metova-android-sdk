package com.metova.android.event;

import roboguice.fragment.RoboFragment;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.otto.Bus;

/**
 * Fragment equivalent of {@link OttoMessageReceivingActivity}.
 * 
 * @see OttoMessageReceivingActivity
 */
public abstract class OttoMessageReceivingFragment extends RoboFragment {

    private final String tag = getClass().getSimpleName();

    @Inject
    private Bus ottoBus;
    @Inject
    private DeadEventHandler deadEventHandler;

    @Override
    public void onResume() {

        super.onResume();

        Log.d( tag, "Registering on bus " + getOttoBus() );
        getOttoBus().register( this );

        Log.d( tag, "Requesting re-fire of dead events." );
        getDeadEventHandler().refireDeadEvents();
    }

    @Override
    public void onPause() {

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
