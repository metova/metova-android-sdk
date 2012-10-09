package com.metova.android.test.util;

import android.test.AndroidTestCase;

import com.metova.android.util.Assertions;

public class AssertionsTest extends AndroidTestCase {

    public void testNotNullShouldThrowAssertionErrorForNullObject() {

        boolean caught = false;

        try {
            Assertions.notNull( "object", null );
        }
        catch (AssertionError e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testNotNullShouldThrowAssertionErrorForEmptyString() {

        boolean caught = false;

        try {
            Assertions.notNull( "string", "" );
        }
        catch (AssertionError e) {
            caught = true;
        }

        assertTrue( caught );
    }
}
