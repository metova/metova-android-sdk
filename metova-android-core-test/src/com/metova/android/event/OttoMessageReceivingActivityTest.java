package com.metova.android.event;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.mockito.InOrder;

import android.test.ActivityInstrumentationTestCase2;

import com.metova.android.event.OttoMessageReceivingActivityTest.FooActivity;
import com.squareup.otto.Bus;

public class OttoMessageReceivingActivityTest extends ActivityInstrumentationTestCase2<FooActivity> {

    public OttoMessageReceivingActivityTest() {

        super( FooActivity.class );
    }

    public void testActivityRegistersWithBusOnResume() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final FooActivity activity = getActivity();
        activity.setOttoBus( mockOttoBus );
        activity.setDeadEventHandler( mockDeadEventHandler );
        runTestOnUiThread( new Runnable() {

            @Override
            public void run() {

                getInstrumentation().callActivityOnResume( activity );
            }
        } );

        verify( mockOttoBus, timeout( 5000 ) ).register( activity );
    }

    public void testActivityUnRegistersFromBusOnPause() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final FooActivity activity = getActivity();
        activity.setOttoBus( mockOttoBus );
        activity.setDeadEventHandler( mockDeadEventHandler );
        runTestOnUiThread( new Runnable() {

            @Override
            public void run() {

                getInstrumentation().callActivityOnPause( activity );
            }
        } );

        verify( mockOttoBus, timeout( 5000 ) ).unregister( activity );
    }

    public void testRequestsRefireOfDeadEventsAfterRegistration() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final FooActivity activity = getActivity();
        activity.setOttoBus( mockOttoBus );
        activity.setDeadEventHandler( mockDeadEventHandler );
        runTestOnUiThread( new Runnable() {

            @Override
            public void run() {

                getInstrumentation().callActivityOnResume( activity );
            }
        } );

        InOrder inOrder = inOrder( mockOttoBus, mockDeadEventHandler );

        getInstrumentation().waitForIdleSync();

        inOrder.verify( mockOttoBus ).register( activity );
        inOrder.verify( mockDeadEventHandler ).refireDeadEvents();
    }

    public static class FooActivity extends OttoMessageReceivingActivity {

    }
}
