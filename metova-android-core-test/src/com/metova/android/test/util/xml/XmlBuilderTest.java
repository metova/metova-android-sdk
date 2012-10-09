package com.metova.android.test.util.xml;

import android.test.AndroidTestCase;

import com.metova.android.util.xml.XmlBuilder;

public class XmlBuilderTest extends AndroidTestCase {

    public void testBuilderShouldCreateADocument() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "test" ).attribute( "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expectedDocument = "<?xml version=\"1.0\"?><test enabled=\"true\">Dave Lane is amazing.</test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldSupportNamespacesForElements() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "abc", "test" ).namespace( "abc", "http://abc.com/abc.xsd" ).attribute( "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expectedDocument = "<?xml version=\"1.0\"?><abc:test xmlns:abc=\"http://abc.com/abc.xsd\" enabled=\"true\">Dave Lane is amazing.</abc:test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldSupportNamespacesForAttributes() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "abc", "test" ).namespace( "abc", "http://abc.com/abc.xsd" ).attribute( "abc", "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expectedDocument = "<?xml version=\"1.0\"?><abc:test xmlns:abc=\"http://abc.com/abc.xsd\" abc:enabled=\"true\">Dave Lane is amazing.</abc:test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testToStringShouldBeRepeatable() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "test" ).attribute( "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expected = xml.toString();
        assertEquals( expected, xml.toString() );
    }

    public void testBuilderShouldSupportCdataValues() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "test" ).cdata( "<anything goes here>" ).close();

        String expected = "<?xml version=\"1.0\"?><test><![CDATA[<anything goes here>]]></test>";
        assertEquals( expected, xml.toString() );
    }

    public void testBuilderShouldAllowAnUTF8Prolog() {

        XmlBuilder xml = new XmlBuilder();
        xml.prologUTF8().open( "test" ).close();

        String expectedDocument = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test></test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowPrologWithCustomEncoding() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog( "UTF-16" ).open( "test" ).close();

        String expectedDocument = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><test></test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowNestedElements() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "test" ).open( "awesome" ).value( "Dave Lane is amazing." ).close().close();

        String expectedDocument = "<?xml version=\"1.0\"?><test><awesome>Dave Lane is amazing.</awesome></test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowMultipleAttributes() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "test" ).attribute( "id", "42" ).attribute( "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expectedDocument = "<?xml version=\"1.0\"?><test id=\"42\" enabled=\"true\">Dave Lane is amazing.</test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowXmlWithoutProlog() {

        XmlBuilder xml = new XmlBuilder();
        xml.open( "test" ).attribute( "enabled", "true" ).value( "Dave Lane is amazing." ).close();

        String expectedDocument = "<test enabled=\"true\">Dave Lane is amazing.</test>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowVariableReplacement() {

        XmlBuilder xml = new XmlBuilder();
        xml.prolog().open( "outerTagBecauseImOCD" );
        xml.open( "test" ).value( "${name} is amazing." ).close();
        xml.open( "radical" ).value( "This variable is ${radicalAdjective}!" ).close();
        xml.open( "awesome" ).value( "${name} is my name." ).close();
        xml.close();

        xml.replace( "name", "Dave Lane" );
        xml.replace( "radicalAdjective", "cool" );

        String expectedDocument = "<?xml version=\"1.0\"?><outerTagBecauseImOCD><test>Dave Lane is amazing.</test><radical>This variable is cool!</radical><awesome>Dave Lane is my name.</awesome></outerTagBecauseImOCD>";
        assertEquals( expectedDocument, xml.toString() );
    }

    public void testBuilderShouldAllowSecondaryBuilderOuputAsNestedRawValue() {

        // build device details.
        XmlBuilder hardwareProfile = new XmlBuilder();
        hardwareProfile.open( "hardwareProfile" );
        hardwareProfile.open( "carrier" ).value( "Verizon Wireless" ).close();
        hardwareProfile.open( "manufacturer" ).value( "Research In Motion" ).close();
        hardwareProfile.open( "model" ).value( "9530" ).close();
        hardwareProfile.open( "platformType" ).value( "BLACKBERRY" ).close();
        hardwareProfile.close();

        // build device statistics.
        XmlBuilder deviceStatistics = new XmlBuilder();
        deviceStatistics.open( "deviceStatistics" );
        deviceStatistics.open( "externalSpaceFree" ).value( "2048000000" ).close();
        deviceStatistics.open( "externalSpaceTotal" ).value( "16000000000" ).close();
        deviceStatistics.open( "internalSpaceFree" ).value( "1024000000" ).close();
        deviceStatistics.open( "internalSpaceTotal" ).value( "8000000000" ).close();
        deviceStatistics.close();

        // build outer XML document ("<device>${hardwareProfile}${deviceStatistics}</device>").
        XmlBuilder xml = new XmlBuilder();
        xml.prolog();
        xml.open( "device" );
        xml.attribute( "externalId", "3087ace1" );
        xml.attribute( "phoneNumber", "15558675309" );
        xml.rawValue( hardwareProfile.toString() + deviceStatistics.toString() );
        xml.close();

        // ensure the complete document is what we expect.
        String expected = "<?xml version=\"1.0\"?><device externalId=\"3087ace1\" phoneNumber=\"15558675309\"><hardwareProfile><carrier>Verizon Wireless</carrier><manufacturer>Research In Motion</manufacturer><model>9530</model><platformType>BLACKBERRY</platformType></hardwareProfile><deviceStatistics><externalSpaceFree>2048000000</externalSpaceFree><externalSpaceTotal>16000000000</externalSpaceTotal><internalSpaceFree>1024000000</internalSpaceFree><internalSpaceTotal>8000000000</internalSpaceTotal></deviceStatistics></device>";
        assertEquals( expected, xml.toString() );
    }

    public void testBuilderShouldNotAllowNullVariableReplacement() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" ).value( "${} is amazing." ).close();

            xml.replace( null, "Dave Lane" );
        }
        catch (IllegalArgumentException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowBlankVariableReplacement() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" ).value( "${} is amazing." ).close();

            xml.replace( "", "Dave Lane" );
        }
        catch (IllegalArgumentException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowNullElementName() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( null );
        }
        catch (IllegalArgumentException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowBlankElementName() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "" );
        }
        catch (IllegalArgumentException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowCallToStringWithOpenTags() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" );
            xml.toString();
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldAllowCloseOnlyAfterOpen() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.close();
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldAllowOnlyOneProlog() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.prolog().prolog();
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowPrologAfterAnyOtherAction() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" ).prolog();
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowValueBeforeOpen() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.value( "Dave Lane is amazing." );
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowAttributeBeforeOpen() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.attribute( "enabled", "true" );
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShouldNotAllowAttributeAfterValue() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" ).value( "Dave Lane is amazing." ).attribute( "enabled", "true" );
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }

    public void testBuilderShowNotAllowAttributeAfterClose() {

        boolean caught = false;

        try {
            XmlBuilder xml = new XmlBuilder();
            xml.open( "test" ).close().attribute( "enabled", "true" );
        }
        catch (IllegalStateException e) {
            caught = true;
        }

        assertTrue( caught );
    }
}
