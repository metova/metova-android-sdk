package com.metova.android.test.util.time;

import android.test.AndroidTestCase;

import com.metova.android.util.time.ExecutionProfile;

public class ExecutionProfileTest extends AndroidTestCase {

    private static final int EXECUTION_TIME = 2000;

    public void testTimeMatchesStartAndStopInterval() throws Throwable {

        ExecutionProfile profile = new ExecutionProfile();
        profile.start();
        Thread.sleep( 2000 );
        profile.stop();

        assertTrue( Math.abs( EXECUTION_TIME - profile.time() ) <= 1000 );
        assertEquals( profile.time(), ( profile.getStopTime() - profile.getStartTime() ) );
    }
}
