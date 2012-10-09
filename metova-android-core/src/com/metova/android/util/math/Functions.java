package com.metova.android.util.math;

/**
 * Utility methods for performing mathematical computations.
 */
public class Functions {

    /**
     * Divide two numbers with double precision
     * 
     * @param dividend
     * @param divisor
     * @return
     */
    public static double doubleDivision( double dividend, double divisor ) {

        return dividend / divisor;
    }

    /**
     * @param low inclusive
     * @param value
     * @param high inclusive
     * @return
     */
    public static double clamp( double low, double value, double high ) {

        if ( high < low ) {
            throw new IllegalStateException( "High bound cannot be lower than the low bound" );
        }
        if ( value < low ) {
            return low;
        }
        if ( value > high ) {
            return high;
        }
        return value;
    }

    public static int clamp( int low, int value, int high ) {

        return (int) clamp( (double) low, (double) value, (double) high );
    }

    /**
     * Find the distance between two vertices
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance( double x1, double y1, double x2, double y2 ) {

        double a = ( x2 - x1 ) * ( x2 - x1 );
        double b = ( y2 - y1 ) * ( y2 - y1 );
        return Math.sqrt( a + b );
    }

    /**
     * Check to see if a given number is even or odd
     * 
     * @param number
     * @return
     */
    public static boolean isEven( double number ) {

        return number % 2 == 0;
    }

    /**
     * Round to the nearest even number
     * 
     * @param number
     * @return
     */
    public static int roundEvenly( double number ) {

        int result = (int) Math.floor( number );
        if ( isEven( result ) ) {

            result = (int) Math.ceil( number );
        }

        return result;
    }

    /**
     * Round to the nearest odd number
     * 
     * @param number
     * @return
     */
    public static int roundOddly( double number ) {

        int result = (int) Math.floor( number );
        if ( !isEven( result ) ) {

            result = (int) Math.ceil( number );
        }

        return result;
    }

    /**
     * Find the nearest factor of a given number that is less than the given number
     * 
     * @param number
     * @param factor
     * @return
     */
    public static int floorFactor( double number, int factor ) {

        int result = (int) Math.floor( number );
        while (result > 0 && result % factor != 0) {

            result--;
        }

        return result;
    }

    /**
     * Find the nearest factor of a given number that is greater than the given number
     * 
     * @param number
     * @param factor
     * @return
     */
    public static int ceilFactor( double number, int factor ) {

        int result = (int) Math.ceil( number );
        while (result > 0 && result % factor != 0) {

            result++;
        }

        return result;
    }

    /**
     * Ensure that a positive modulus is returned for both positive and negative numbers
     * 
     * @param number
     * @param factor
     * @return
     */
    public static double posMod( double number, double factor ) {

        double mod = number % factor;
        return ( mod < 0 && number < 0 ) ? mod + factor : mod;
    }

    /**
     * Raises the base to the exponent'th power
     * 
     * @param base
     * @param exponent
     * @return base raised to the exponent'th power
     */
    public static double pow( double base, double exponent ) {

        double result = 1;

        while (exponent > 0) {

            result *= base;
            exponent--;
        }

        return result;
    }

    public static long round( double d ) {

        long l = (long) d;

        double remainder = d - l;

        if ( remainder < .5 ) {
            return l;
        }
        else {
            return l + 1;
        }
    }
}
