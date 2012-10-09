package com.metova.android.util.xml;

import java.util.Stack;

/**
 * Utility for generating XML using the builder pattern. This class enables users to write
 * well-formed XML in a compact way which is less error prone than manually writing the document. <br/>
 * <br/>
 * Example usage:
 * 
 * <pre>
 * new XmlBuilder().prolog().open( &quot;test&quot; ).attribute( &quot;enabled&quot;, &quot;true&quot; ).value( &quot;my info&quot; ).close();
 * </pre>
 * 
 * Generated XML:
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot;?&gt;&lt;test enabled=&quot;true&quot;&gt;my info&lt;/test&gt;
 * </pre>
 * 
 * Variables are supported and should be declared within your document as follows, where
 * "myVariable" is the name of your variable:
 * 
 * <pre>
 * XmlBuilder xmlBuilder = new XmlBuilder().open( &quot;test&quot; ).value( &quot;${myVariable}&quot; ).close();
 * 
 * // Now replace its value:
 * xmlBuilder.replace( &quot;myVariable&quot;, &quot;this is my true value&quot; );
 * </pre>
 */
public final class XmlBuilder {

    private static final int ACTION_INIT = 0;
    private static final int ACTION_PROLOG = 1;
    private static final int ACTION_OPEN = 2;
    private static final int ACTION_ATTRIBUTE = 3;
    private static final int ACTION_VALUE = 4;
    private static final int ACTION_CLOSE = 5;

    /**
     * Tracks the order of open elements, allowing us to call close() on the most recently opened
     * element without re-declaring the element name.
     */
    private final Stack<String> elementStack = new Stack<String>();

    /**
     * Tracks the last action performed. Used to ensure an improper action is not called given the
     * current state/position of the XML being built.
     */
    private int lastAction = ACTION_INIT;

    /**
     * Buffer used to build the XML string as we go.
     */
    private final StringBuffer buffer = new StringBuffer();

    /**
     * Creates a prolog for the document without an encoding declaration.
     * 
     * @return The XmlBuilder
     */
    public XmlBuilder prolog() {

        return prolog( null );
    }

    /**
     * Creates a prolog for the document with a "UTF-8" encoding declaration.
     * 
     * @return The XmlBuilder.
     */
    public XmlBuilder prologUTF8() {

        return prolog( "UTF-8" );
    }

    /**
     * Creates a prolog for the document with the specified encoding declaration.
     * 
     * @param encoding The encoding type.
     * @return The XmlBuilder
     */
    public XmlBuilder prolog( String encoding ) {

        if ( ACTION_INIT != lastAction ) {
            throw new IllegalStateException( "Only declare prolog() at the start of the document." );
        }

        buffer.append( "<?xml version=\"1.0\"" );
        if ( encoding != null && encoding.length() > 0 ) {
            buffer.append( " encoding=\"" );
            buffer.append( encoding );
            buffer.append( "\"" );
        }
        buffer.append( "?>" );

        lastAction = ACTION_PROLOG;
        return this;
    }

    /**
     * Creates a new element tag.
     * 
     * @param elementName The name of the new element.
     * @return The XmlBuilder
     */
    public XmlBuilder open( String elementName ) {

        if ( elementName == null || elementName.length() <= 0 ) {
            throw new IllegalArgumentException( "Element name can not be blank or null." );
        }

        buffer.append( "<" );
        buffer.append( elementName );
        buffer.append( ">" );

        elementStack.push( elementName );
        lastAction = ACTION_OPEN;
        return this;
    }

    /**
     * Creates a new element tag with the specified namespace prefix.
     * 
     * @param namespacePrefix The namespace prefix for the element.
     * @param elementName The name of the new element.
     * @return The XmlBuilder
     */
    public XmlBuilder open( String namespacePrefix, String elementName ) {

        StringBuffer buffer = new StringBuffer( namespacePrefix );
        buffer.append( ":" );
        buffer.append( elementName );

        return open( buffer.toString() );
    }

    /**
     * Inserts a namespace attribute into the last open element tag.
     * 
     * @param prefix The prefix of the namespace.
     * @param uri The URI of the namespace.
     * @return The XmlBuilder
     */
    public XmlBuilder namespace( String prefix, String uri ) {

        StringBuffer buffer = new StringBuffer( "xmlns" );
        if ( prefix != null && prefix.length() > 0 ) {
            buffer.append( ":" );
            buffer.append( prefix );
        }

        return attribute( buffer.toString(), uri );
    }

    /**
     * Inserts an attribute into the last open element tag.
     * 
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @return The XmlBuilder
     */
    public XmlBuilder attribute( String name, String value ) {

        if ( ACTION_OPEN != lastAction && ACTION_ATTRIBUTE != lastAction ) {
            throw new IllegalStateException( "Only add attribute() after open() or attribute()." );
        }

        if ( name != null && ( name.contains( "<" ) || name.contains( ">" ) ) ) {
            throw new IllegalArgumentException( "Tag brackets are not allowed in attribute() name." );
        }

        if ( value != null && ( value.contains( "<" ) || value.contains( ">" ) ) ) {
            throw new IllegalArgumentException( "Tag brackets are not allowed in attribute() value." );
        }

        buffer.deleteCharAt( buffer.length() - 1 );
        buffer.append( " " );
        buffer.append( name );
        buffer.append( "=\"" );
        buffer.append( value );
        buffer.append( "\">" );

        lastAction = ACTION_ATTRIBUTE;
        return this;
    }

    /**
     * Inserts an attribute into the last open element tag.
     * 
     * @param namespacePrefix The namespace prefix for the attribute.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @return The XmlBuilder
     */
    public XmlBuilder attribute( String namespacePrefix, String name, String value ) {

        StringBuffer buffer = new StringBuffer( namespacePrefix );
        buffer.append( ":" );
        buffer.append( name );

        return attribute( buffer.toString(), value );
    }

    /**
     * Inserts a text value for the last open element.
     * 
     * @param text The text value.
     * @return The XmlBuilder
     */
    public XmlBuilder value( String text ) {

        return validatedValue( text, false );
    }

    /**
     * Inserts a raw value for the last open element. This allows insertion of 
     * XML tags within the current element's body.
     * 
     * @param text The text value.
     * @return The XmlBuilder
     */
    public XmlBuilder rawValue( String text ) {

        return validatedValue( text, true );
    }

    /**
     * Inserts a text value for the last open element, validating its value to ensure 
     * the value does not contain XML if skipValidation equals false.  Otherwise validation 
     * is skipped and the text value is inserted into the current element's body.
     * 
     * @param text The text value.
     * @param skipValidation True to skip validation, false to validate the text value.
     * @return The XmlBuilder
     */
    private XmlBuilder validatedValue( String text, boolean skipValidation ) {

        if ( ACTION_OPEN != lastAction && ACTION_ATTRIBUTE != lastAction ) {
            throw new IllegalStateException( "Only add value() after open() or attribute()." );
        }

        if ( !skipValidation ) {
            if ( text != null && ( text.contains( "<" ) || text.contains( ">" ) ) ) {
                throw new IllegalArgumentException( "Tag brackets are not allowed in value()." );
            }
        }

        buffer.append( text );

        lastAction = ACTION_VALUE;
        return this;
    }

    /**
     * Inserts a CDATA value for the last open element.
     * 
     * @param text The text value.
     * @return The XmlBuilder.
     */
    public XmlBuilder cdata( String text ) {

        if ( text != null ) {
            if ( text.contains( "<![CDATA[" ) ) {
                throw new IllegalArgumentException( "Opening CDATA brackets <![CDATA[ are not allowed in cdata()." );
            }
            if ( text.contains( "]]>" ) ) {
                throw new IllegalArgumentException( "Closing CDATA brackets ]]> are not allowed in cdata()." );
            }
        }

        StringBuffer buffer = new StringBuffer( "<![CDATA[" );
        buffer.append( text );
        buffer.append( "]]>" );

        return rawValue( buffer.toString() );
    }

    /**
     * Replaces all occurrences of the specified needle variable from the current XML document. This
     * is useful for templating a document before all of its values are known (e.g., to allow an
     * XmlBuilder to be shared between platforms). <br />
     * <br />
     * Example usage:
     * 
     * <pre>
     * new XmlBuilder().open( &quot;test&quot; ).value( &quot;${myVariable}&quot; ).replace( &quot;myVariable&quot;, &quot;my info&quot; ).close();
     * </pre>
     * 
     * Generated XML:
     * 
     * <pre>
     * &lt;test&gt;my info&lt;/test&gt;
     * </pre>
     * 
     * @param needle the variable to be replaced
     * @param replacement the text which will replace needle.
     * @return The XmlBuilder
     */
    public XmlBuilder replace( String needle, String replacement ) {

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

    /**
     * Closes the last open element tag.
     * 
     * @return The XmlBuilder
     */
    public XmlBuilder close() {

        if ( elementStack.isEmpty() ) {
            throw new IllegalStateException( "Must call open() before close()." );
        }

        buffer.append( "</" );
        buffer.append( elementStack.pop() );
        buffer.append( ">" );

        lastAction = ACTION_CLOSE;
        return this;
    }

    /**
     * Returns the string built by this XmlBuilder.
     * 
     * @return A String of XML representing the built document.
     */
    @Override
    public String toString() {

        if ( !elementStack.isEmpty() ) {
            throw new IllegalStateException( "Must call close() the same number of times as open()." );
        }

        return buffer.toString();
    }
}
