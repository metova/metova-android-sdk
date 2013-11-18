package com.metova.android.util.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

public class CollectLogTask extends AsyncTask<String, Void, StringBuilder> {

    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private static final int DEFAULT_MAX_LOG_MESSAGE_LENGTH = 100000;
    private static final String TAG = CollectLogTask.class.getSimpleName();

    private int maxLogMessageLength;

    public CollectLogTask() {

        this( DEFAULT_MAX_LOG_MESSAGE_LENGTH );
    }

    public CollectLogTask(int maxLogMessageLength) {

        setMaxLogMessageLength( maxLogMessageLength );
    }

    @Override
    protected StringBuilder doInBackground( String... params ) {

        final StringBuilder log = new StringBuilder();
        try {
            List<String> commandLine = new ArrayList<String>();
            commandLine.add( "logcat" );
            commandLine.add( "-d" );
            List<String> arguments = ( params != null ) ? Arrays.asList( params ) : null;
            if ( null != arguments ) {
                commandLine.addAll( arguments );
            }

            Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[0] ) );
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

            String line;
            while (( line = bufferedReader.readLine() ) != null) {
                log.append( line );
                log.append( LINE_SEPARATOR );
            }
        }
        catch (IOException e) {
            Log.e( TAG, "CollectLogTask.doInBackground failed", e );
        }

        return log;
    }

    @Override
    protected void onPostExecute( StringBuilder log ) {

        if ( null != log ) {

            //truncate if necessary
            int keepOffset = Math.max( log.length() - getMaxLogMessageLength(), 0 );
            if ( keepOffset > 0 ) {
                log.delete( 0, keepOffset );
            }
        }
    }

    private int getMaxLogMessageLength() {

        return maxLogMessageLength;
    }

    private void setMaxLogMessageLength( int maxLogMessageLength ) {

        this.maxLogMessageLength = maxLogMessageLength;
    }
}
