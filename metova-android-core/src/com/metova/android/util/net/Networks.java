package com.metova.android.util.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Networks {

    public static final String NETWORK_MOBILE = "MOBILE";
    public static final String NETWORK_WIFI = "WIFI";

    private Networks() {

    }

    public static boolean isCDMA( Context context ) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_GSM;
    }

    public static boolean isNetworkAvailable( Context context ) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static boolean isNetworkConnected( Context context ) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getActiveNetwork( Context context ) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if ( activeNetworkInfo != null ) {
            return activeNetworkInfo.getTypeName();
        }

        return null;
    }

    public static String getNetworkName( Context context ) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        return telephonyManager.getNetworkOperatorName();
    }
}
