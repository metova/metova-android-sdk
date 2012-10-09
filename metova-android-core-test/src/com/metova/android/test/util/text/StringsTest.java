package com.metova.android.test.util.text;

import android.test.AndroidTestCase;

import com.metova.android.util.text.Strings;

public class StringsTest extends AndroidTestCase {

    private static final String TEN_LETTER_STRING = "10 letters";
    private static final String TWENTY_LETTER_STRING = "twenty letter string";

    public void testLimitTruncatesLongString() {

        String string = Strings.limit( TWENTY_LETTER_STRING, 15 );
        assertEquals( TWENTY_LETTER_STRING.substring( 0, 15 ), string );
    }

    public void testLimitDoesNotTruncateShortString() {

        String string = Strings.limit( TEN_LETTER_STRING, 15 );
        assertEquals( TEN_LETTER_STRING, string );
    }

    public void testSubstringBeforeGetSubstringBeforeNeedle() {

        String string = Strings.substringBefore( TWENTY_LETTER_STRING, "letter" );
        assertEquals( "twenty ", string );
    }

    public void testSubstringBeforeReturnsNullIfNeedleNotFound() {

        String string = Strings.substringBefore( TWENTY_LETTER_STRING, "not found" );
        assertEquals( null, string );
    }
}
