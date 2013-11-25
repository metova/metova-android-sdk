package com.metova.android.event;

import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.squareup.otto.Bus;

/**
 * Equivalent to {@link OttoMessageReceivingFragment}, but extends {@link RoboSherlockFragment} for
 * cases where that is the desired hierarchy.
 * 
 * Using this class means you should explicitly add the optional Roboguice-Sherlock dependency to your 
 * project's pom:
 * <pre>
 * {@code
 * <dependency>
 *   <groupId>com.github.rtyley</groupId>
 *   <artifactId>roboguice-sherlock</artifactId>
 * </dependency>
 * }
 * </pre>
 * @see OttoMessageReceivingFragment
 */
public abstract class OttoMessageReceivingSherlockFragment extends RoboSherlockFragment {

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
