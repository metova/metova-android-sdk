package com.metova.android.util.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.metova.android.util.math.Flags;
import com.metova.android.util.math.Functions;

/**
 * 
 * This class has various static final variables that are useful for calculating time in milliseconds.
 * The getTimeDuration methods help with displaying readable time strings.
 * 
 */
public class Dates {

    /**
     * Number of milliseconds in one second.
     */
    public static final long SECOND = 1000;

    /**
     * Number of milliseconds in one minute.
     */
    public static final long MINUTE = 60000;

    /**
     * Number of milliseconds in one hour.
     */
    public static final long HOUR = 3600000;

    /**
     * Number of milliseconds in one day.
     */
    public static final long DAY = 86400000;

    /**
     * Number of milliseconds in one week.
     */
    public static final long WEEK = 6048000000L;

    public static final int FLAG_SECOND = 1;
    public static final int FLAG_MINUTE = 2;
    public static final int FLAG_HOUR = 4;
    public static final int FLAG_DAY = 8;
    public static final int FLAG_WEEK = 16;

    public static final long MICROSECONDS_IN_A_MILLISECOND = 1000L;
    public static final long MICROSECONDS_IN_A_SECOND = 1000000L;
    public static final long MILLISECONDS_IN_A_SECOND = 1000L;
    public static final int SECONDS_IN_A_MINUTE = 60;
    public static final int MINUTES_IN_AN_HOUR = 60;
    public static final long HOURS_IN_A_DAY = 24;
    public static final int MONTHS_IN_A_YEAR = 12;

    private static final String TEXT_SECOND = "sec";
    private static final String TEXT_MINUTE = "min";
    private static final String TEXT_HOUR = "hour";
    private static final String TEXT_DAY = "day";
    private static final String TEXT_WEEK = "week";

    private static final int INDEX_WEEK = 0;
    private static final int INDEX_DAY = 1;
    private static final int INDEX_HOUR = 2;
    private static final int INDEX_MINUTE = 3;
    private static final int INDEX_SECOND = 4;

    private Dates() {

    }

    public static final long getStartOfDay() {

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );
        return calendar.getTime().getTime();
    }

    public static final long getMillisSinceMidnight() {

        return getMillisSinceMidnight( System.currentTimeMillis() );
    }

    public static final long getMillisSinceMidnight( long date ) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date( date ) );
        long millisSinceMidnight = Dates.HOUR * calendar.get( Calendar.HOUR_OF_DAY );
        millisSinceMidnight += Dates.MINUTE * calendar.get( Calendar.MINUTE );
        millisSinceMidnight += Dates.SECOND * calendar.get( Calendar.SECOND );
        millisSinceMidnight += calendar.get( Calendar.MILLISECOND );
        return millisSinceMidnight;
    }

    /**
     * Returns a human-readable String describing the amount of 
     * time represented by the given number of seconds.
     * 
     * @param seconds  number of seconds.
     * @return  human-readable String describing the amount of time represented by the given parameter (e.g., "2 hours 3mins 10secs".
     */
    public static String getTimeDuration( int seconds ) {

        long milliseconds = 1000 * seconds;
        return getTimeDuration( milliseconds );
    }

    /**
     * Returns a human-readable String describing the amount of 
     * time represented by the given number of milliseconds.
     * 
     * @param milliseconds  number of milliseconds.
     * @return human-readable String describing the amount of time represented by the given parameter (e.g., "2hours 3mins 10secs".
     */
    public static String getTimeDuration( long milliseconds ) {

        return getTimeDuration( milliseconds, FLAG_WEEK | FLAG_DAY | FLAG_HOUR | FLAG_MINUTE | FLAG_SECOND );
    }

    /**
     * Returns a human-readable String describing the amount of time
     * represented by the given number of milliseconds.
     * 
     * @param milliseconds Number of milliseconds
     * @param mask Mask 
     * @return human-readable String describing the amount of time
     */
    public static String getTimeDuration( long milliseconds, int mask ) {

        // display week, day, hour, minute and second
        long[] unitOrder = { WEEK, DAY, HOUR, MINUTE, SECOND };
        return getTimeDuration( milliseconds, mask, unitOrder );
    }

    /**
     * Returns a human-readable String describing the amount of 
     * time represented by the given number of milliseconds.
     * 
     * @param milliseconds  number of milliseconds
     * @param mask
     * @param unitOrder order units should be printed eg. xhrs xmins xsecs would be { HOUR, MINUTE, SECOND }
     * @return human-readable String describing the amount of time represented by the given parameter (e.g., "2hours 3mins 10secs".
     */
    public static String getTimeDuration( long milliseconds, int mask, long[] unitOrder ) {

        StringBuffer stringBuffer = new StringBuffer();

        boolean lessThan = true;
        lessThan = getLessThanText( stringBuffer, milliseconds, SECOND, mask, lessThan );
        lessThan = getLessThanText( stringBuffer, milliseconds, MINUTE, mask, lessThan );
        lessThan = getLessThanText( stringBuffer, milliseconds, HOUR, mask, lessThan );
        lessThan = getLessThanText( stringBuffer, milliseconds, DAY, mask, lessThan );
        lessThan = getLessThanText( stringBuffer, milliseconds, WEEK, mask, lessThan );

        if ( stringBuffer.length() > 0 ) {

            stringBuffer.insert( 0, "less than 1 " );
        }
        else {

            Vector<String> timeDurationStrings = new Vector<String>();

            milliseconds = getTimeDuration( timeDurationStrings, milliseconds, WEEK, mask );
            milliseconds = getTimeDuration( timeDurationStrings, milliseconds, DAY, mask );
            milliseconds = getTimeDuration( timeDurationStrings, milliseconds, HOUR, mask );
            milliseconds = getTimeDuration( timeDurationStrings, milliseconds, MINUTE, mask );
            milliseconds = getTimeDuration( timeDurationStrings, milliseconds, SECOND, mask );

            for (int i = 0; i < unitOrder.length; i++) {

                int index = getIndexForUnit( unitOrder[i] );

                if ( index != -1 ) {

                    String timeDuration = timeDurationStrings.elementAt( index );

                    if ( timeDuration != null ) {

                        if ( stringBuffer.length() > 0 ) {

                            stringBuffer.append( " " );
                        }

                        stringBuffer.append( timeDuration );
                    }
                }
            }
        }

        return stringBuffer.toString();
    }

    private static boolean getLessThanText( StringBuffer stringBuffer, long milliseconds, long unit, int mask, boolean lessThan ) {

        int flag = getFlagForUnit( unit );

        if ( lessThan && Flags.isSet( flag, mask ) ) {

            if ( milliseconds < unit ) {

                if ( stringBuffer.length() == 0 && Flags.isSet( flag, mask ) ) {

                    stringBuffer.append( getTextForUnit( unit ) );
                }
            }
            else {

                lessThan = false;
            }
        }

        return lessThan;
    }

    private static long getTimeDuration( Vector<String> timeDurationStrings, long milliseconds, long unit, int mask ) {

        int flag = getFlagForUnit( unit );
        if ( milliseconds >= unit && Flags.isSet( flag, mask ) ) {

            StringBuffer stringBuffer = new StringBuffer();

            int amount = (int) Math.floor( Functions.doubleDivision( milliseconds, unit ) );
            milliseconds -= ( unit * amount );

            stringBuffer.append( amount );
            stringBuffer.append( " " );
            stringBuffer.append( getTextForUnit( unit ) );
            stringBuffer.append( ( amount > 1 ) ? "s" : "" );

            timeDurationStrings.addElement( stringBuffer.toString() );
        }
        else {

            timeDurationStrings.addElement( null );
        }

        return milliseconds;
    }

    private static final int getFlagForUnit( long unit ) {

        if ( unit == SECOND ) {

            return FLAG_SECOND;
        }
        else if ( unit == MINUTE ) {

            return FLAG_MINUTE;
        }
        else if ( unit == HOUR ) {

            return FLAG_HOUR;
        }
        else if ( unit == DAY ) {

            return FLAG_DAY;
        }
        else if ( unit == WEEK ) {

            return FLAG_WEEK;
        }
        else {

            return 0;
        }
    }

    private static final String getTextForUnit( long unit ) {

        if ( unit == SECOND ) {

            return TEXT_SECOND;
        }
        else if ( unit == MINUTE ) {

            return TEXT_MINUTE;
        }
        else if ( unit == HOUR ) {

            return TEXT_HOUR;
        }
        else if ( unit == DAY ) {

            return TEXT_DAY;
        }
        else if ( unit == WEEK ) {

            return TEXT_WEEK;
        }
        else {

            return "";
        }
    }

    private static final int getIndexForUnit( long unit ) {

        if ( unit == SECOND ) {

            return INDEX_SECOND;
        }
        else if ( unit == MINUTE ) {

            return INDEX_MINUTE;
        }
        else if ( unit == HOUR ) {

            return INDEX_HOUR;
        }
        else if ( unit == DAY ) {

            return INDEX_DAY;
        }
        else if ( unit == WEEK ) {

            return INDEX_WEEK;
        }
        else {

            return -1;
        }
    }
}
