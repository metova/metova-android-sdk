package com.metova.android.util.net;

import com.metova.android.util.text.Strings;

/**
 * Provides constant values and utility methods for content types
 */
public final class ContentTypes {

    //
    // http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4
    //
    //  The content type "application/x-www-form-urlencoded" is inefficient for sending large quantities of binary data 
    //      or text containing non-ASCII characters. The content type "multipart/form-data" should be used for submitting 
    //      forms that contain files, non-ASCII data, and binary data
    //
    public static final String FORM_MULTIPART = "multipart/form-data";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    public static final String XML = "application/xml";

    public static final String TEXT_HTML_UTF8 = "text/html; charset=utf-8";

    public static final String JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";

    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_XPDF = "application/x-pdf";
    public static final String APPLICATION_PPT = "application/vnd.ms-powerpoint";
    public static final String APPLICATION_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String APPLICATION_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String APPLICATION_XLS = "application/vnd.ms-excel";
    public static final String APPLICATION_DOC = "application/msword";
    public static final String TEXT_HTML = "text/html";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_BMP = "image/bmp";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String VIDEO_MPEG = "video/mpeg";
    public static final String VIDEO_QUICKTIME = "video/quicktime";
    public static final String VIDEO_X_LA_ASF = "video/x-la-asf";
    public static final String VIDEO_X_MS_ASF = "video/x-ms-asf";
    public static final String VIDEO_X_MSVIDEO = "video/x-msvideo";
    public static final String VIDEO_X_SGI_MOVIE = "video/x-sgi-movie";
    public static final String VIDEO_3GPP = "video/3gpp";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String AUDIO_BASIC = "audio/basic";
    public static final String AUDIO_MID = "audio/mid";
    public static final String AUDIO_MPEG = "audio/mpeg";
    public static final String AUDIO_OGG = "audio/ogg";
    public static final String AUDIO_FLAC = "audio/flac";
    public static final String AUDIO_X_AIFF = "audio/x-aiff";
    public static final String AUDIO_X_MPEGURL = "audio/x-mpegurl";
    public static final String AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio";
    public static final String AUDIO_X_WAV = "audio/x-wav";
    public static final String TEXT_X_CSRC = "text/x-csrc";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String TEXT_X_C_SRC = "text/x-c++src";
    public static final String TEXT_X_JAVA = "text/x-java";
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";
    public static final String TEXT_X_PYTHON = "text/x-python";
    public static final String TEXT_X_RUBY = "text/x-ruby";
    public static final String APPLICATION_RTF = "application/rtf";
    public static final String TEXT_X_SCALA = "text/x-scala";
    public static final String TEXT_X_TEX = "text/x-tex";
    public static final String TEXT_X_MAKEFILE = "text/x-makefile";
    public static final String APPLICATION_POSTSCRIPT = "application/postscript";
    public static final String APPLICATION_RSS_XML = "application/rss+xml";
    public static final String APPLICATION_X_CSH = "application/x-csh";
    public static final String APPLICATION_X_GENSHI = "application/x-genshi";
    public static final String APPLICATION_X_GENSHI_TEXT = "application/x-genshi-text";
    public static final String APPLICATION_X_SH = "application/x-sh";
    public static final String APPLICATION_X_TROFF = "application/x-troff";
    public static final String APPLICATION_X_YAML = "application/x-yaml";
    public static final String APPLICATION_XSL_XML = "application/xsl+xml";
    public static final String APPLICATION_XSLT_XML = "application/xslt+xml";
    public static final String APPLICATION_OGG = "application/ogg";
    public static final String APPLICATION_FLAC = "application/x-flac";
    public static final String AUDIO_3GPP = "audio/3gpp";

    public static final String IMAGE_SVG_XML = "image/svg+xml";
    public static final String IMAGE_X_ICON = "image/x-icon";
    public static final String MODEL_VRML = "model/vrml";
    public static final String TEXT_CSS = "text/css";
    public static final String TEXT_X_ADA = "text/x-ada";
    public static final String TEXT_X_ASM = "text/x-asm";
    public static final String TEXT_X_ASP = "text/x-asp";
    public static final String TEXT_X_AWK = "text/x-awk";
    public static final String TEXT_X_C_HDR = "text/x-c++hdr";
    public static final String TEXT_X_CHDR = "text/x-chdr";
    public static final String TEXT_X_CSHARP = "text/x-csharp";
    public static final String TEXT_X_DIFF = "text/x-diff";
    public static final String TEXT_X_EIFFEL = "text/x-eiffel";
    public static final String TEXT_X_ELISP = "text/x-elisp";
    public static final String TEXT_X_FORTRAN = "text/x-fortran";
    public static final String TEXT_X_HASKELL = "text/x-haskell";
    public static final String TEXT_X_IDL = "text/x-idl";
    public static final String TEXT_X_INF = "text/x-inf";
    public static final String TEXT_X_INI = "text/x-ini";
    public static final String TEXT_X_KSH = "text/x-ksh";
    public static final String TEXT_X_LUA = "text/x-lua";
    public static final String TEXT_X_M4 = "text/x-m4";
    public static final String TEXT_X_MAIL = "text/x-mail";
    public static final String TEXT_X_OBJC = "text/x-objc";
    public static final String TEXT_X_OCAML = "text/x-ocaml";
    public static final String TEXT_X_PASCAL = "text/x-pascal";
    public static final String TEXT_X_PERL = "text/x-perl";
    public static final String TEXT_X_PHP = "text/x-php";
    public static final String TEXT_PHP = "text/php";
    public static final String TEXT_X_PSP = "text/x-psp";
    public static final String TEXT_X_PYREX = "text/x-pyrex";
    public static final String TEXT_X_PYTHON_DOCTEST = "text/x-python-doctest";
    public static final String TEXT_X_RFC = "text/x-rfc";
    public static final String TEXT_X_RST = "text/x-rst";
    public static final String TEXT_X_SCHEME = "text/x-scheme";
    public static final String TEXT_X_SQL = "text/x-sql";
    public static final String TEXT_X_TCL = "text/x-tcl";
    public static final String TEXT_X_TEXTILE = "text/x-textile";
    public static final String TEXT_X_VBA = "text/x-vba";
    public static final String TEXT_X_VERILOG = "text/x-verilog";
    public static final String TEXT_X_VHDL = "text/x-vhdl";
    public static final String TEXT_X_ZSH = "text/x-zsh";
    public static final String TEXT_XML = "text/xml";
    public static final String AUDIO_X_M4A = "audio/x-m4a";
    public static final String AUDIO_WMA = "audio/x-ms-wma";
    public static final String APPLICATION_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private ContentTypes() {

    }

    /**
     * Determines whether the content types are equal.
     * @param targetContentType
     * @param contentType
     * @return
     */
    public static boolean isContentType( String targetContentType, String contentType ) {

        return targetContentType.equals( Strings.toLowerCase( contentType ) );
    }

    /**
     * Tries to guess the correct content type based on the file name's extension.
     * @param fileName
     * @return The content type; "application/unknown" if content type is unknown or extension is unavailable.
     */
    public static String getContentType( String fileName ) {

        fileName = fileName.toLowerCase();
        if ( fileName.endsWith( ".jar" ) ) {

            return "application/java-archive";
        }
        else if ( fileName.endsWith( ".txt" ) ) {
            return TEXT_PLAIN;
        }
        else if ( fileName.endsWith( ".xhtml" ) ) {

            return "application/vnd.pwg-xhtml-print+xml:0.95";
        }
        else if ( fileName.endsWith( ".jpg" ) ) {

            return IMAGE_JPEG;
        }
        else if ( fileName.endsWith( ".jpeg" ) ) {

            return IMAGE_JPEG;
        }
        else if ( fileName.endsWith( ".gif" ) ) {

            return IMAGE_GIF;
        }
        else if ( fileName.endsWith( ".html" ) ) {
            return "text/html";
        }
        else if ( fileName.endsWith( ".vcf" ) ) {

            return "text/x-vcard";
        }
        else if ( fileName.endsWith( ".pdf" ) ) {
            return "application/pdf";
        }
        else {

            return "application/unknown";
        }
    }
}
