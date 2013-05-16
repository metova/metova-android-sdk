package com.metova.android.test.util;

import junit.framework.TestCase;
import android.os.Bundle;

import com.metova.android.model.StubParcelable;
import com.metova.android.util.BundleChecker;

public class BundleCheckerTest extends TestCase {

    public void testExtraNoKey() throws Throwable {

        final String key = "test_extra";

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();

        try {

            BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
            fail( "No exception thrown" );
        }
        catch (IllegalArgumentException e) {

            //pass
        }
    }

    public void testExtraNull() throws Throwable {

        final String key = "test_extra";

        Bundle bundle1 = null;
        Bundle bundle2 = new Bundle();

        try {

            BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
            fail( "No exception thrown" );
        }
        catch (IllegalArgumentException e) {

            //pass
        }
    }

    public void testExtraNull2() throws Throwable {

        final String key = "test_extra";

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = null;

        try {

            BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
            fail( "No exception thrown" );
        }
        catch (IllegalArgumentException e) {

            //pass
        }
    }

    public void testExtrainBundle1() throws Throwable {

        final String key = "test_extra";

        Bundle bundle1 = new Bundle();
        bundle1.putInt( key, 1 );
        Bundle bundle2 = new Bundle();

        Integer output = BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
        assertNotNull( output );
    }

    public void testExtrainBundle2() throws Throwable {

        final String key = "test_extra";

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle2.putInt( key, 1 );

        Integer output = BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
        assertNotNull( output );
    }

    public void testGetString() throws Throwable {

        final String key = "test_extra";
        final String value = "test_value";

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle2.putString( key, value );

        String output = BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
        assertEquals( output, value );
    }

    public void testGetInt() throws Throwable {

        final String key = "test_extra";
        final Integer value = 1;

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle2.putInt( key, value );

        Integer output = BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
        assertEquals( output, value );
    }

    public void testGetParcelable() throws Throwable {

        final String key = "social_media_item";
        final StubParcelable parcelable = new StubParcelable( "string", 1 );

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable( key, parcelable );

        StubParcelable output = BundleChecker.getExtraOrThrow( key, new Bundle[] { bundle1, bundle2 } );
        assertEquals( parcelable, output );
    }
}
