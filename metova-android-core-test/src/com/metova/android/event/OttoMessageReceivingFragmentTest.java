package com.metova.android.event;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.mockito.InOrder;

import com.squareup.otto.Bus;

public class OttoMessageReceivingFragmentTest extends TestCase {

    public void testFragmentRegistersWithBusOnResume() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final OttoMessageReceivingFragment fragment = new OttoMessageReceivingFragment() {
        };
        fragment.setOttoBus( mockOttoBus );
        fragment.setDeadEventHandler( mockDeadEventHandler );
        fragment.onResume();

        verify( mockOttoBus, timeout( 5000 ) ).register( fragment );
    }

    public void testActivityUnRegistersFromBusOnPause() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final OttoMessageReceivingFragment fragment = new OttoMessageReceivingFragment() {
        };
        fragment.setOttoBus( mockOttoBus );
        fragment.setDeadEventHandler( mockDeadEventHandler );
        fragment.onPause();

        verify( mockOttoBus, timeout( 5000 ) ).unregister( fragment );
    }

    public void testRequestsRefireOfDeadEventsAfterRegistration() throws Throwable {

        final Bus mockOttoBus = mock( Bus.class );
        final DeadEventHandler mockDeadEventHandler = mock( DeadEventHandler.class );

        final OttoMessageReceivingFragment fragment = new OttoMessageReceivingFragment() {
        };
        fragment.setOttoBus( mockOttoBus );
        fragment.setDeadEventHandler( mockDeadEventHandler );
        fragment.onResume();

        InOrder inOrder = inOrder( mockOttoBus, mockDeadEventHandler );

        inOrder.verify( mockOttoBus ).register( fragment );
        inOrder.verify( mockDeadEventHandler ).refireDeadEvents();
    }

}
