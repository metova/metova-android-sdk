package com.metova.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public abstract class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {

        super( MainActivity.class );
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        Log.i( "MainActivityTest#setUp", "Getting activity so the application actually starts. This is needed for database service interactions." );
        getActivity();
    }
}
