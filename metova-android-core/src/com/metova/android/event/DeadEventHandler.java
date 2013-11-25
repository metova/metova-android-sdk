package com.metova.android.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.google.inject.Inject;
import com.metova.android.util.event.OttoUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

/**
 * Provides a bucket for unhandled events on the Otto {@link Bus}. It is unlikely that this class will be 
 * used directly, but it supports unique-messaging operations and re-firing of unhandled events.
 * </p>
 * Using this class means you should explicitly add the optional Otto dependency to your project's pom:
 * <pre>
 * {@code
 * <dependency>
 *     <groupId>com.squareup</groupId>
 *     <artifactId>otto</artifactId>
 * </dependency>
 *  }
 * </pre>
 * 
 * @see <a href=http://square.github.io/otto/>Otto</a>
 * @see OttoUtil#postUniqueEvent(Object, android.content.Context, Bus, DeadEventHandler)
 * @see OttoMessageReceivingActivity
 * @see OttoMessageReceivingSherlockActivity
 * @see OttoMessageReceivingFragment
 * @see OttoMessageReceivingSherlockFragment
 */
public class DeadEventHandler {

    private static final String TAG = DeadEventHandler.class.getSimpleName();

    @Inject
    private Bus ottoBus;

    private List<DeadEvent> deadEventList = new LinkedList<DeadEvent>();

    /**
     * Performs post-injection initialization. Use of this method will depend on how the class is instantiated.
     * <p/>
     * <b>In direct instantiation</b>, simply invoke this method after setting the Otto {@link Bus}:
     * <pre>
     * {@code
     * DeadEventHandler deadEventHandler = new DeadEventHandler();
     * deadEventHandler.setOttoBus( bus );
     * deadEventHandler.init();
     * }</pre>
     * <p/>
     * <b>In a RoboGuice context</b>, the {@link Bus} is Injected, so it is best to create this instance
     * via a provider method:
     * <pre>
     * <code>
     * @Provides
     * DeadEventHandler provideDeadEventHandler( Context context ) {
     *
     *     if ( deadEventHandler == null ) {
     *  
     *         deadEventHandler = new DeadEventHandler();
     *         final Injector injector = RoboGuice.getInjector( context );
     *         injector.injectMembers( deadEventHandler );
     *         deadEventHandler.init();  
     *     }
     *     return deadEventHandler;
     * }
     * </code></pre>
     */
    public void init() {

        getOttoBus().register( this );
    }

    /**
     * Handles {@link DeadEvent}s on the Otto {@link Bus}. DeadEvents occur when there are no receivers
     * subscribed to the bus to receive a particular message. They are handed to this method instead,
     * which allows this DeadEventHandler to collect them to re-fired at a later time.
     * @param deadEvent the unhandled message
     * @see DeadEventHandler#refireDeadEvents()
     */
    @Subscribe
    public void handleDeadEvent( DeadEvent deadEvent ) {

        Log.d( TAG, "Handling DeadEvent: " + deadEvent );

        synchronized (getDeadEventList()) {
            getDeadEventList().add( deadEvent );
        }

        Log.d( TAG, "New DeadEvent list size (" + getDeadEventList().size() + ")" );
    }

    /**
     * Purges all messages with the given type from the dead event list.
     * <p/>
     * This might be useful for cases where you want to ensure that a message you're 
     * putting onto the bus is the only one of its kind.
     * 
     * @param messageTypes the type to purge
     * @see OttoUtil#postUniqueEvent(Object, android.content.Context, Bus, DeadEventHandler)
     */
    public void purgeMessages( Class<?>... messageTypes ) {

        synchronized (getDeadEventList()) {

            for (Iterator<DeadEvent> iter = getDeadEventList().iterator(); iter.hasNext();) {

                DeadEvent deadEvent = iter.next();
                for (Class<?> messageType : messageTypes) {

                    if ( deadEvent.event.getClass().isInstance( messageType ) ) {

                        iter.remove();
                    }
                }
            }
        }
    }

    /**
     * Requests that any messages in the dead event list be re-fired so that newly registered clients
     * have the opportunity to respond to them.
     * <p/>
     * This is primarily intended as something that message-receiving Activities or Fragments can
     * do once their onResume is called and they have registered themselves on the bus, and this allows
     * them to receive events that occurred before they were created or while they were paused.
     */
    public void refireDeadEvents() {

        Log.d( TAG, "Re-firing all DeadEvents (" + getDeadEventList().size() + ")" );

        List<DeadEvent> backupList = new LinkedList<DeadEvent>();

        synchronized (getDeadEventList()) {

            backupList.addAll( getDeadEventList() );
            getDeadEventList().clear();
        }

        for (Iterator<DeadEvent> iter = backupList.iterator(); iter.hasNext();) {

            DeadEvent deadEvent = iter.next();

            Log.d( TAG, "Re-firing DeadEvent: " + deadEvent );
            getOttoBus().post( deadEvent.event );
        }

        Log.d( TAG, "New DeadEvent list size (" + getDeadEventList().size() + ")" );
    }

    private List<DeadEvent> getDeadEventList() {

        return deadEventList;
    }

    public Bus getOttoBus() {

        return ottoBus;
    }

    public void setOttoBus( Bus ottoBus ) {

        this.ottoBus = ottoBus;
    }
}
