package com.metova.android.util.time;

/**
 * Utility object for tracking start and stop timings of code sections which are being analyzed 
 * for performance improvements.  Start an ExecutionProfile immediately before executing the 
 * code you wish to time, and stop it immediately after executing the code you wish to time.
 */
public final class ExecutionProfile {

    private long startTime;
    private long stopTime;

    /**
     * Starts the timer by recording the current system time.
     */
    public final void start() {

        startTime = System.currentTimeMillis();
    }

    /**
     * Stops the timer by recording the current system time.
     */
    public final void stop() {

        stopTime = System.currentTimeMillis();
    }

    /**
     * Returns the amount of time between {{@link #getStartTime()} and {@link #getStopTime()}.
     * 
     * @return The execution time in milliseconds.
     */
    public final long time() {

        return getStopTime() - getStartTime();
    }

    /**
     * The system time at which {@link #start()} was last invoked.
     * 
     * @return The last start time.
     */
    public final long getStartTime() {

        return startTime;
    }

    /**
     * The system time at which {@link #stop()} was last invoked.
     * 
     * @return The last stop time.
     */
    public final long getStopTime() {

        return stopTime;
    }
}
