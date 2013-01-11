package com.metova.android.graphics.drawable;

import android.graphics.drawable.AnimationDrawable;

import com.metova.android.util.concurrent.GlobalThreadPool;

/**
 * <p>A class that extends {@link android.graphics.drawable.AnimationDrawable} to add several utility callbacks.
 * To use these callbacks, you need to implement {@link CustomAnimationDrawableCallback} 
 * and call {@link CustomAnimationDrawable#setCustomCallback(CustomAnimationDrawableCallback)}</p>
 */
public class CustomAnimationDrawable extends AnimationDrawable {

    private CustomAnimationDrawableCallback customCallback;
    private int frameForCallback = -1;
    private boolean invokeRestartCallback = false;

    /**
     * Constructs a new {@link CustomAnimationDrawable} from
     * the given {@link android.graphics.drawable.AnimationDrawable}.
     * 
     * @param animationDrawable
     */
    public CustomAnimationDrawable(AnimationDrawable animationDrawable) {

        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {

            addFrame( animationDrawable.getFrame( i ), animationDrawable.getDuration( i ) );
        }
    }

    @Override
    public void stop() {

        super.stop();
        setInvokeRestartCallback( false );
    }

    @Override
    public boolean selectDrawable( final int index ) {

        boolean returnValue = super.selectDrawable( index );
        if ( null != getCustomCallback() ) {

            handleRestartCallback( index );

            handleFrameReachedCallback( index );

            handleFinishedCallback( index );
        }

        return returnValue;
    }

    private void handleRestartCallback( final int index ) {

        if ( ( index == 0 && !isOneShot() ) ) {

            if ( isInvokeRestartCallback() ) {

                getCustomCallback().animationRestarted();
            }
            else {

                setInvokeRestartCallback( true );
            }
        }
    }

    private void handleFrameReachedCallback( final int index ) {

        if ( ( getCallbackFrameIndex() >= 0 && index == getCallbackFrameIndex() ) ) {

            getCustomCallback().frameReached();
        }
    }

    private void handleFinishedCallback( int index ) {

        if ( index == getNumberOfFrames() - 1 && isOneShot() ) {

            GlobalThreadPool.invoke( new Runnable() {

                @Override
                public void run() {

                    int duration = getDuration( getNumberOfFrames() - 1 );
                    try {

                        Thread.sleep( duration, 0 );
                    }
                    catch (InterruptedException e) {
                        //Non-critical: catch and move on.
                    }
                    GlobalThreadPool.invokeOnUiThread( new Runnable() {

                        @Override
                        public void run() {

                            getCustomCallback().animationFinished();
                        }
                    } );
                }
            } );
        }
    }

    /**
     * <p>Several utility callbacks for knowing when {@link android.graphics.drawable.AnimationDrawable}
     * animations are restarted, a certain frame is reached, or when a one shot animation is finished.</p>
     */
    public interface CustomAnimationDrawableCallback {

        /**
         * Callback method to be invoked when a one-shot animation reaches its last frame.
         */
        public void animationFinished();

        /**
         * Called method to be invoked when a repeating animation restarts.
         */
        public void animationRestarted();

        /**
         * Called when a certain frame is reached.  The frame that invokes this callback
         * is set using {@link CustomAnimationDrawable#setFrameReachedCallbackNumber(int)}.
         * 
         * If no frame is set, this callback will not be invoked.
         */
        public void frameReached();
    }

    /**
     * Sets the callback for {@link CustomAnimationDrawableCallback#animationFinished()},{@link CustomAnimationDrawableCallback#animationRestarted()}
     * and {@link CustomAnimationDrawableCallback#frameReached(int)}.
     * @param customCallback
     */
    public void setCustomCallback( CustomAnimationDrawableCallback customCallback ) {

        this.customCallback = customCallback;
    }

    private CustomAnimationDrawableCallback getCustomCallback() {

        return customCallback;
    }

    /**
     * Sets the frame that will invoke {@link CustomAnimationDrawableCallback#frameReached(int)}.
     * 
     * If this is never set, {@link CustomAnimationDrawableCallback#frameReached(int)} will not be invoked
     * @param frameNumber
     */
    public void setFrameForCallback( int frameNumber ) {

        frameForCallback = frameNumber;
    }

    /**
     * Returns the frame that is set to invoke {@link CustomAnimationDrawableCallback#frameReached()}
     * 
     * @return frame number or -1 if not set
     */
    public int getCallbackFrameIndex() {

        return frameForCallback;
    }

    private boolean isInvokeRestartCallback() {

        return invokeRestartCallback;
    }

    private void setInvokeRestartCallback( boolean invokeRestartCallback ) {

        this.invokeRestartCallback = invokeRestartCallback;
    }
}
