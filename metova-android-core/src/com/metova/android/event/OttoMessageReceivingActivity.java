package com.metova.android.event;

import roboguice.activity.RoboFragmentActivity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.otto.Bus;

/**
 * Message-receiving activity that uses an Otto {@link Bus} to receive messages.
 * <p/>
 * See the <a href="http://square.github.io/otto/">Otto documentation</a> for more, but sub-classes 
 * simply annotate methods with {@code @Subscribe} in order to receive messages of the specified type:
 * <pre>
 * <code>
 * {@code @Subscribe}
 * public void handleFooMessage( FooMessage foo) {
 *     //...
 * }</code></pre>
 * This class also utilizes a {@link DeadEventHandler} to re-fire events that occurred before the activity
 * was created or while the activity was paused, which allows sub-class activities to be agnostic to the 
 * timing of the events.
 * <p/>
 * One potential application of message-passing of this nature is to decouple {@link AsyncTask}s from the 
 * activities/fragments that spawn them. By allowing the AsyncTasks to fire messages when completed rather
 * than directly undertake UI operations themselves, it is easier to decouple and re-use the AsyncTasks.
 * Additionally, this alleviates the burden from the AsyncTask to cancel itself if its activity is paused 
 * or to verify that its activity is still alive when it reaches its {@code onPostExecute} method.
 * 
 * <pre><code>public class FooActivity extends OttoMessageReceivingActivity {
 * 
 * {@code @Override}
 *   protected void onCreate( Bundle savedInstanceState ) {
 *     super.onCreate( savedInstanceState);
 *     new FooAsyncTask().execute();
 *   }
 * 
 * {@code @Subscribe}
 *   public void handleFooCompleteMessage( FooCompleteMessage message ) {
 *    //...
 *   }
 * 
 * {@code @Subscribe}
 *   public void handleFooFailureMessage( FooFailureMessage message ) {
 *    //...
 *   }
 * }
 * 
 * public class FooAsyncTask extends AsyncTask<Void, Void, Foo> {
 *
 *   public void onPostExecute( Foo result ) {
 * 
 *    if ( getException() != null ) {
 *  
 *      OttoUtil.postUniqueEvent( new FooFailureMessage(), getContext(), getOttoBus(), getDeadEventHandler() ) {
 *    } else {
 *  
 *      OttoUtil.postUniqueEvent( new FooCompleteMessage(), getContext(), getOttoBus(), getDeadEventHandler() ) {
 *    }
 * 
 *   public static class FooCompleteMessage {
 *     //...
 *   }
 * 
 *   public static class FooFailureMessage {
 *     //...
 *   }
 * }<code></pre>
 * 
 * <p/>
 * This class is a {@link RoboFagmentActivity}, so in an application that uses Roboguice, it will
 * automatically be {@code @Inject}ed with the needed Bus and DeadEventHandler. In applications not using
 * Roboguice, these two dependencies will have to be provided manually.
 * <p/>
 * Using this class means you should explicitly add the optional Otto dependency to your project's pom:
 * <pre>
 * {@code
 * <dependency>
 *     <groupId>com.squareup</groupId>
 *     <artifactId>otto</artifactId>
 * </dependency>
 * }
 * </pre>
 * 
 * @see OttoMessageReceivingSherlockActivity
 * @see OttoMessageReceivingFragment
 */
public abstract class OttoMessageReceivingActivity extends RoboFragmentActivity {

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
