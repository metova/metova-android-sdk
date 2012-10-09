package com.metova.android.util.localization;

import com.metova.android.util.text.Strings;

public class PostalCodes {

    /**
     * Determines if given postal code is a valid Austratilian postal code
     * @param postalCode - postal code to check
     * @return - boolean true if code is valid an Austratilian postal code, false otherwise
     */
    public static boolean isValidAustratilianPostalCode( String postalCode ) {

        if ( postalCode.length() == 4 ) {
            if ( Strings.isNumeric( postalCode ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if given postal code is a valid United States postal code
     * @param postalCode - postal code to check
     * @return - boolean true if code is valid an United States postal code, false otherwise
     */
    public static boolean isValidUSPostalCode( String postalCode ) {

        int dashIndex = postalCode.indexOf( '-' );

        if ( dashIndex == -1 ) {
            if ( postalCode.length() == 5 ) {
                if ( Strings.isNumeric( postalCode ) ) {
                    /**
                     * This is valid 5 digit zipcode
                     */
                    return true;
                }
            }
        }
        else if ( dashIndex == 5 ) {
            if ( postalCode.length() == 10 ) {
                String fiveDigitPortion = postalCode.substring( 0, dashIndex );
                String fourDigitPortion = postalCode.substring( dashIndex + 1 );

                if ( Strings.isNumeric( fiveDigitPortion ) && Strings.isNumeric( fourDigitPortion ) ) {
                    /**
                     * This is valid 9 digit zipcode
                     */
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if given postal code is a valid Canadian postal code
     * @param postalCode - postal code to check
     * @return - boolean true if code is valid an Canadian postal code, false otherwise
     */
    public static boolean isValidCanadianPostalCode( String postalCode ) {

        int length = postalCode.length();

        boolean expectingNumber = false;
        boolean foundSeparator = false;

        for (int i = 0; i < length; i++) {
            char c = postalCode.charAt( i );

            if ( c == '-' || c == ' ' ) {
                if ( !isCanadianSeparatorCharacterValid( length, i ) ) {
                    return false;
                }

                foundSeparator = true;
            }
            else {

                if ( !isCanadianCharacterValid( expectingNumber, c ) ) {
                    return false;
                }

                /**
                 * Canadian postal codes alternate between letters and numbers, so swap this flag.
                 */
                expectingNumber = !expectingNumber;
            }
        }

        int expectedLength;
        if ( foundSeparator ) {
            expectedLength = 7;
        }
        else {
            expectedLength = 6;
        }

        if ( postalCode.length() != expectedLength ) {
            /**
             * The postal code is not a valid length
             */
            return false;
        }

        return true;
    }

    private static boolean isCanadianCharacterValid( boolean expectingNumber, char c ) {

        if ( expectingNumber && Strings.isNumeric( new String( new char[] { c } ) ) ) {
            return true;
        }
        else if ( Strings.isAlphabetic( new String( new char[] { c } ) ) ) {
            return true;
        }

        return false;
    }

    private static boolean isCanadianSeparatorCharacterValid( int length, int index ) {

        if ( length != 7 ) {
            /**
             * The postal code is not the correct length to include a separator
             */
            return false;
        }
        else if ( index != 3 ) {
            /**
             * This separator is not in a valid location.
             */
            return false;
        }

        return true;
    }

    /**
     * Determines if given postal code is a valid Kiwi postal code
     * @param postalCode - postal code to check
     * @return - boolean true if code is valid an Kiwi postal code, false otherwise
     */
    public static boolean isValidKiwiPostalCode( String postalCode ) {

        if ( postalCode.length() == 4 ) {
            if ( Strings.isNumeric( postalCode ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if given postal code is a valid United Kingdom postal code
     * @param postalCode - postal code to check
     * @return - boolean true if code is valid an United Kingdom postal code, false otherwise
     */
    public static boolean isValidUKPostalCode( String postalCode ) {

        int spaceIndex = postalCode.indexOf( ' ' );

        if ( spaceIndex != -1 ) {
            String firstPart = postalCode.substring( 0, spaceIndex );
            if ( isUKFirstPartValid( firstPart ) ) {
                String secondPart = postalCode.substring( spaceIndex + 1 );
                if ( isUKSecondPartValid( secondPart ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * The second portion consists of one or two letters followed by a number, then following by either
     * another number, another letter, or nothing. 
     */
    private static boolean isUKFirstPartValid( String firstPart ) {

        int firstNumberIndex = Strings.indexOfNumber( firstPart );

        if ( firstNumberIndex != -1 ) {
            String beginning = firstPart.substring( 0, firstNumberIndex );
            if ( beginning.length() == 1 || beginning.length() == 2 ) {
                if ( Strings.isAlphabetic( beginning ) ) {
                    String remainder = firstPart.substring( firstNumberIndex + 1 );

                    if ( remainder.length() == 0 ) {
                        return true;
                    }
                    else if ( remainder.length() == 1 ) {
                        if ( Strings.isAlphanumeric( remainder ) ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * The second portion must be in the form "#LL", where # is a number and L is a letter.
     */
    private static boolean isUKSecondPartValid( String secondPart ) {

        if ( secondPart.length() == 3 ) {

            if ( Strings.isNumeric( new String( new char[] { secondPart.charAt( 0 ) } ) ) ) {
                if ( Strings.isAlphabetic( new String( new char[] { secondPart.charAt( 1 ) } ) ) ) {
                    if ( Strings.isAlphabetic( new String( new char[] { secondPart.charAt( 2 ) } ) ) ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
