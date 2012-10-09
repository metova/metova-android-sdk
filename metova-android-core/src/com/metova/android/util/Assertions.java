package com.metova.android.util;

import com.metova.android.util.text.Strings;

public final class Assertions {

    public static void notNull( String name, Object object ) {

        if ( object instanceof String && Strings.isNullOrWhiteSpace( object.toString() ) ) {
            throw new AssertionError( "The value of \"" + name + "\" can not be null!" );
        }

        if ( object == null ) {
            throw new AssertionError( "The value of \"" + name + "\" can not be null!" );
        }
    }
}
