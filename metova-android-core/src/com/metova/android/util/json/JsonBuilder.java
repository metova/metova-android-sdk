package com.metova.android.util.json;

import java.util.Stack;

/**
 * Utility for generating JSON using the builder pattern. This class enables users to write
 * well-formed JSON in a compact way which is less error prone than manually writing the document. <br/>
 * <br/>
 * Example usage:
 * 
 * <pre>
 * new JsonBuilder().open().open( &quot;test&quot; ).value( &quot;enabled&quot;, &quot;true&quot; ).value( &quot;my info&quot; ).close().close();
 * </pre>
 * 
 * Generated JSON:
 * 
 * <pre>
 * {&quot;test&quot;:{&quot;enabled&quot;:&quot;true&quot;,&quot;my info&quot;}}
 * </pre>
 * 
 * Variables are supported and should be declared within your document as follows, where
 * "myVariable" is the name of your variable:
 * 
 * <pre>
 * JsonBuilder jsonBuilder = new JsonBuilder().open().open( &quot;test&quot; ).value( &quot;${myVariable}&quot; ).close().close();
 * 
 * // Now replace its value:
 * jsonBuilder.replace( &quot;myVariable&quot;, &quot;this is my true value&quot; );
 * </pre>
 */
public class JsonBuilder {

    private static final int ACTION_INIT = 0;
    private static final int ACTION_OPEN = 1;
    private static final int ACTION_ARRAY = 2;
    private static final int ACTION_VALUE = 3;
    private static final int ACTION_CLOSE = 4;

    private static final Integer TYPE_OBJECT = Integer.valueOf( 0 );
    private static final Integer TYPE_ARRAY = Integer.valueOf( 1 );

    /**
     * Tracks the order of open elements, allowing us to call close() on the most recently opened
     * element without re-stating whether we're closing an object or an array.
     */
    private final Stack<Integer> typeStack = new Stack<Integer>();

    /**
     * Tracks the last action performed. Used to ensure an improper action is not called given the
     * current state/position of the JSON being built.
     */
    private int lastAction = ACTION_INIT;

    /**
     * Buffer used to build the JSON string as we go.
     */
    private final StringBuffer buffer = new StringBuffer();

    public JsonBuilder open() {

        return open( null );
    }

    public JsonBuilder open( String elementName ) {

        if ( elementName != null && elementName.length() > 0 ) {
            buffer.append( "\"" );
            buffer.append( elementName );
            buffer.append( "\":" );
        }

        buffer.append( "{" );

        typeStack.push( TYPE_OBJECT );
        lastAction = ACTION_OPEN;
        return this;
    }

    public JsonBuilder array() {

        return array( null );
    }

    public JsonBuilder array( String elementName ) {

        if ( elementName != null && elementName.length() > 0 ) {
            buffer.append( "\"" );
            buffer.append( elementName );
            buffer.append( "\":" );
        }

        buffer.append( "[" );

        typeStack.push( TYPE_ARRAY );
        lastAction = ACTION_ARRAY;
        return this;
    }

    public JsonBuilder value( String value ) {

        return value( null, value );
    }

    public JsonBuilder value( String name, String value ) {

        return value( name, value, true );
    }

    public JsonBuilder value( String name, boolean value ) {

        return value( name, Boolean.toString( value ), false );
    }

    public JsonBuilder value( String name, double value ) {

        return value( name, Double.toString( value ), false );
    }

    public JsonBuilder value( String name, long value ) {

        return value( name, Long.toString( value ), false );
    }

    public JsonBuilder value( String name, String value, boolean quoteValue ) {

        if ( ACTION_INIT == lastAction || ( ACTION_CLOSE == lastAction && typeStack.isEmpty() ) ) {
            throw new IllegalStateException( "Must call open() or array() before value()." );
        }

        if ( name != null && name.length() > 0 ) {
            buffer.append( "\"" );
            buffer.append( name );
            buffer.append( "\":" );
        }

        if ( quoteValue ) {
            buffer.append( "\"" );
            buffer.append( value );
            buffer.append( "\"," );
        }
        else {
            buffer.append( value );
            buffer.append( "," );
        }

        lastAction = ACTION_VALUE;
        return this;
    }

    public JsonBuilder replace( String needle, String replacement ) {

        if ( needle == null || needle.length() <= 0 ) {
            throw new IllegalArgumentException( "Needle can not be blank or null." );
        }

        needle = "${" + needle + "}";

        int index = buffer.indexOf( needle );
        while (index >= 0) {

            buffer.replace( index, index + needle.length(), replacement );

            index = buffer.indexOf( needle, index + 1 );
        }

        return this;
    }

    public JsonBuilder close() {

        if ( typeStack.isEmpty() ) {
            throw new IllegalStateException( "Must call open() or array() before close()." );
        }

        buffer.deleteCharAt( buffer.length() - 1 );
        if ( typeStack.pop() == TYPE_ARRAY ) {
            buffer.append( "]," );
        }
        else {
            buffer.append( "}," );
        }

        lastAction = ACTION_CLOSE;
        return this;
    }

    @Override
    public String toString() {

        int index = buffer.length() - 1;
        if ( buffer.charAt( index ) == ',' ) {
            buffer.deleteCharAt( index );
        }

        return buffer.toString();
    }
}
