package com.metova.android.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.metova.android.service.persistence.record.CursorWrapper;

/**
 * Utility methods for handling streams.
 */
public final class Streams {

    private static final int BUFFER_SIZE = 8192;

    /**
     * Retrieves the contents of the InputStream as a byte array.
     * 
     * @param inputStream the inputStream being read.
     * @return the byte array containing the inputStream's contents.
     * @throws IOException The inputStream could not be read.
     */
    public static final byte[] getAsByteArray( InputStream inputStream ) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {

            int readByte = inputStream.read( buffer, 0, buffer.length );
            if ( readByte == -1 ) {

                break;
            }

            outputStream.write( buffer, 0, readByte );
        }

        outputStream.flush();

        byte[] result = outputStream.toByteArray();
        close( outputStream );

        return result;
    }

    /**
     * Closes the specified cursor.
     * 
     * @param cursor the cursor to be closed.
     */
    public static void close( Cursor cursor ) {

        if ( cursor != null ) {
            cursor.close();
        }
    }

    /**
     * Closes the specified cursor wrapper.
     * 
     * @param cursorWrapper the cursor wrapper to be closed.
     */
    public static void close( CursorWrapper cursorWrapper ) {

        if ( cursorWrapper != null ) {
            cursorWrapper.close();
        }
    }

    /**
     * Closes the specified inputStream.
     * 
     * @param inputStream the stream to be closed.
     */
    public static void close( InputStream inputStream ) {

        if ( inputStream != null ) {

            try {
                inputStream.close();
            }
            catch (IOException e) {
                Log.e( "Streams#close()", "Could not close inputStream " + inputStream, e );
            }
        }
    }

    /**
     * Closes the specified outputStream.
     * 
     * @param outputStream the stream to be closed.
     */
    public static void close( OutputStream outputStream ) {

        if ( outputStream != null ) {

            try {
                outputStream.close();
            }
            catch (IOException e) {
                Log.e( "Streams#close()", "Could not close outputStream " + outputStream, e );
            }
        }
    }

    /**
     * Closes the specified reader.
     * 
     * @param reader the reader to be closed.
     */
    public static void close( Reader reader ) {

        if ( reader != null ) {

            try {
                reader.close();
            }
            catch (IOException e) {
                Log.e( "Streams#close()", "Could not close reader " + reader, e );
            }
        }
    }

    /**
     * Closes the specified database.
     * 
     * @param database the database to be closed.
     */
    public static void close( SQLiteDatabase database ) {

        if ( database != null ) {
            database.close();
        }
    }

    /**
     * Closes the specified database.
     * 
     * @param database the database to be closed.
     */
    public static void close( SQLiteOpenHelper database ) {

        if ( database != null ) {
            database.close();
        }
    }

    /**
     * Closes the specified statement.
     * 
     * @param statement the statement to be closed.
     */
    public static void close( SQLiteStatement statement ) {

        if ( statement != null ) {
            statement.close();
        }
    }

    /**
     * Outputs the contents of the specified inputStream as a string.
     * 
     * @param inputStream the stream whose contents should be returned as a string.
     * @return a string containing the content of the inputStream.
     */
    public static String toString( InputStream inputStream ) {

        if ( inputStream == null ) {
            throw new IllegalArgumentException( "inputStream should not be null." );
        }

        StringBuilder builder = new StringBuilder();
        String line = null;

        BufferedReader reader = null;
        try {

            reader = new BufferedReader( new InputStreamReader( inputStream ) );
            while (( line = reader.readLine() ) != null) {
                builder.append( line ).append( "\n" );
            }
        }
        catch (IOException e) {

            Log.e( "Streams#close()", "Could not read from inputStream " + inputStream, e );
        }
        finally {

            Streams.close( reader );
        }

        return builder.toString();
    }

    /**
     * Outputs the contents of the specified inputStream as a string.
     * 
     * @param inputStream the stream whose contents should be returned as a string.
     * @param encoding the desired encoding for the string (e.g., "UTF-8").
     * @return a string containing the content of the inputStream.
     */
    public static String toStringWithEncoding( InputStream inputStream, String encoding ) throws IOException {

        if ( inputStream == null ) {
            throw new IllegalArgumentException( "inputStream should not be null." );
        }

        char[] buffer = new char[BUFFER_SIZE];
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream, encoding ), BUFFER_SIZE );

        int character = -1;
        while (true) {

            int i = 0;
            for (i = 0; i < buffer.length; i++) {

                character = bufferedReader.read();
                if ( character != -1 ) {
                    buffer[i] = (char) character;
                }
                else {
                    break;
                }
            }

            if ( i != 0 ) {
                //Only append if we read something
                stringBuffer.append( buffer, 0, i );
            }

            if ( character == -1 ) {
                //Stop reading if the end of the input stream has been reached
                break;
            }
        }

        return stringBuffer.toString();
    }

    /**
    * Reads bytes from input and writes them to output using an 8k buffer size.
    * 
    * @param input
    * @param output
    * @throws IOException
    */
    public static void copy( InputStream inputStream, OutputStream outputStream ) throws IOException {

        byte[] buffer = new byte[BUFFER_SIZE];

        int bytesRead = 0;
        while (( bytesRead = inputStream.read( buffer ) ) != -1) {
            outputStream.write( buffer, 0, bytesRead );
        }

        outputStream.flush();
    }
}
