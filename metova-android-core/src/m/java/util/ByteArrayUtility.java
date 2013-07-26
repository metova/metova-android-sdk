package m.java.util;

/**
 * Utility methods for managing byte arrays.
 */
public class ByteArrayUtility {

    /**
     * Retrieves a section of a byte array.
     * 
     * @param input the original byte array.
     * @param offset  beginning offset of the desired subsection.
     * @param length  number of bytes (starting from the offset) in the desired subsection.
     * @return the section of the byte array.
     */
    public static byte[] getPartialByteArray( byte[] input, int offset, int length ) {

        byte[] output = new byte[length];

        for (int i = 0; i < length; i++) {
            output[i] = input[i + offset];
        }

        return output;
    }

    /**
     * Converts a byte array to a string detailing the contents of the byte array.
     * 
     * @param bytes the byte array.
     * @return the resultant string (e.g., "[41,12,...]").
     */
    public static String toString( byte[] bytes ) {

        StringBuffer sb = new StringBuffer();

        sb.append( '[' );

        if ( bytes.length > 0 ) {
            sb.append( bytes[0] );
        }

        for (int i = 1; i < bytes.length; i++) {
            sb.append( ',' );
            sb.append( bytes[i] );
        }

        sb.append( ']' );

        return sb.toString();
    }
}
