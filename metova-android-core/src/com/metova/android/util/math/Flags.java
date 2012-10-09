package com.metova.android.util.math;

/**
 * Utility methods for handling whether flags are set or not
 */
public class Flags {

    /**
     *    11110001 (value)
     *  & 00010000 (flag)
     *  = 00010000 if flag is set for value
     * @param flag
     * @param value
     * @return
     */
    public static final boolean isSet( long flag, long value ) {

        return ( value & flag ) == flag;
    }

    /**
     *    11110001 (value)
     *  & 00010000 (flag)
     *  = 00010000 if flag is set for value
     * @param flag
     * @param value
     * @return
     */
    public static final boolean isSet( int flag, int value ) {

        return ( value & flag ) == flag;
    }

    /**
     *      11110001 (value)
     *  & ~(00010000) (flag)
     *  =   11100001 (return)
     * @param flag
     * @param value
     * @return
     */
    public static final long unset( long flag, long value ) {

        return value & ~flag;
    }

    /**
     *      11110001 (value)
     *  & ~(00010000) (flag)
     *  =   11100001 (return)
     * @param flag
     * @param value
     * @return
     */
    public static final int unset( int flag, int value ) {

        return value & ~flag;
    }

    /**
     *      11110000 (value)
     *  & ~(00000001) (flag)
     *  =   11110001 (return)
     * @param flag
     * @param value
     * @return
     */
    public static int set( int flag, int value ) {

        return value | flag;
    }
}
