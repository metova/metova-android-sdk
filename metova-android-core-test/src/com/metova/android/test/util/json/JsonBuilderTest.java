package com.metova.android.test.util.json;

import android.test.AndroidTestCase;

import com.metova.android.util.json.JsonBuilder;

public class JsonBuilderTest extends AndroidTestCase {

    public void testBuilderShouldBeDocumented() {

        JsonBuilder json = new JsonBuilder().open().open( "test" ).value( "${myVariable}" ).close().close();
        json.replace( "myVariable", "this is my true value" );

        String expected = "{\"test\":{\"this is my true value\"}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldCreateObject() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "awesomePerson", "Dave Lane" ).close();
        json.close();

        String expected = "{\"test\":{\"awesomePerson\":\"Dave Lane\"}}";
        assertEquals( expected, json.toString() );
    }

    public void testToStringShouldBeRepeatable() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "awesomePerson", "Dave Lane" ).close();
        json.close();

        String expected = json.toString();
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldCreateArray() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.array( "test" ).value( "awesome" ).value( "radical" ).close();
        json.close();

        String expected = "{\"test\":[\"awesome\",\"radical\"]}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowBooleanValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "enabled", true ).close();
        json.close();

        String expected = "{\"test\":{\"enabled\":true}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowDoubleValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "amount", 2.99 ).close();
        json.close();

        String expected = "{\"test\":{\"amount\":2.99}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowIntValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "amount", 2 ).close();
        json.close();

        String expected = "{\"test\":{\"amount\":2}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowLongValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "test" ).value( "amount", Long.MAX_VALUE ).close();
        json.close();

        String expected = "{\"test\":{\"amount\":9223372036854775807}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowNestedObjectsWithArrayOfObjectsWithMultipleValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "menu" ).value( "id", "file" ).value( "value", "File" );

        json.open( "popup" );
        json.array( "menuitem" );
        json.open().value( "value", "New" ).value( "onclick", "CreateNewDoc()" ).close();
        json.open().value( "value", "Open" ).value( "onclick", "OpenDoc()" ).close();
        json.open().value( "value", "Close" ).value( "onclick", "CloseDoc()" ).close();
        json.close();
        json.close();

        json.close();
        json.close();

        String expected = "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldAllowNestedObjectsWithMultipleArraysOfObjectsWithMultipleValues() {

        JsonBuilder json = new JsonBuilder();
        json.open();
        json.open( "menu" ).value( "id", "file" ).value( "value", "File" );

        json.open( "popup" );

        json.array( "menuitem" );
        json.open().value( "value", "New" ).value( "onclick", "CreateNewDoc()" ).close();
        json.open().value( "value", "Open" ).value( "onclick", "OpenDoc()" ).close();
        json.open().value( "value", "Close" ).value( "onclick", "CloseDoc()" ).close();
        json.close();

        json.array( "descriptions" );
        json.open().value( "value", "New" ).value( "pageTitle", "New Description" ).close();
        json.open().value( "value", "Open" ).value( "pageTitle", "Open Description" ).close();
        json.open().value( "value", "Close" ).value( "pageTitle", "Close Description" ).close();
        json.close();

        json.close();

        json.close();
        json.close();

        String expected = "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}],\"descriptions\":[{\"value\":\"New\",\"pageTitle\":\"New Description\"},{\"value\":\"Open\",\"pageTitle\":\"Open Description\"},{\"value\":\"Close\",\"pageTitle\":\"Close Description\"}]}}}";
        assertEquals( expected, json.toString() );
    }

    public void testBuilderShouldNotAllowValueBeforeOpen() {

        boolean caught = false;

        try {
            JsonBuilder json = new JsonBuilder();
            json.value( "awesome", "Dave Lane" );
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowCloseBeforeOpen() {

        boolean caught = false;

        try {
            JsonBuilder json = new JsonBuilder();
            json.close();
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }
}
