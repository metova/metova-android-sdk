package com.metova.android.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SendLogActivity extends android.app.Activity {

    public final static String COLLECTOR_TAG = "AndroidLogCollector";//$NON-NLS-1$
    public final static String LINE_SEPARATOR = System.getProperty( "line.separator" );
    public final static String APP_MESSAGE = "This application will attempt to collect the device log. The collected log will be sent using an application of your choice. You also will have an opportunity to see and modify the data being sent.";

    public static final String ACTION_SEND_LOG = "com.metova.android.intent.action.SEND_LOG";//$NON-NLS-1$
    public static final String EXTRA_SEND_INTENT_ACTION = "com.metova.android.intent.extra.SEND_INTENT_ACTION";//$NON-NLS-1$
    public static final String EXTRA_DATA = "com.metova.android.intent.extra.DATA";//$NON-NLS-1$
    public static final String EXTRA_ADDITIONAL_INFO = "com.metova.android.intent.extra.ADDITIONAL_INFO";//$NON-NLS-1$
    public static final String EXTRA_SHOW_UI = "com.metova.android.intent.extra.SHOW_UI";//$NON-NLS-1$
    public static final String EXTRA_FILTER_SPECS = "com.metova.android.intent.extra.FILTER_SPECS";//$NON-NLS-1$
    public static final String EXTRA_FORMAT = "com.metova.android.intent.extra.FORMAT";//$NON-NLS-1$
    public static final String EXTRA_BUFFER = "com.metova.android.intent.extra.BUFFER";//$NON-NLS-1$

    final int MAX_LOG_MESSAGE_LENGTH = 100000;

    private AlertDialog mMainDialog;
    private Intent mSendIntent;
    private CollectLogTask mCollectLogTask;
    private ProgressDialog mProgressDialog;
    private String mAdditonalInfo;
    private boolean mShowUi;
    private String[] mFilterSpecs;
    private String mFormat;
    private String mBuffer;

    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        mSendIntent = null;

        Intent intent = getIntent();
        if ( null != intent ) {
            String action = intent.getAction();
            if ( ACTION_SEND_LOG.equals( action ) ) {
                String extraSendAction = intent.getStringExtra( EXTRA_SEND_INTENT_ACTION );
                if ( extraSendAction == null ) {
                    Log.e( COLLECTOR_TAG, "Quitting, EXTRA_SEND_INTENT_ACTION is not supplied" );//$NON-NLS-1$
                    finish();
                    return;
                }

                mSendIntent = new Intent( extraSendAction );

                Uri data = (Uri) intent.getParcelableExtra( EXTRA_DATA );
                if ( data != null ) {
                    mSendIntent.setData( data );
                }

                String[] emails = intent.getStringArrayExtra( Intent.EXTRA_EMAIL );
                if ( emails != null ) {
                    mSendIntent.putExtra( Intent.EXTRA_EMAIL, emails );
                }

                String[] ccs = intent.getStringArrayExtra( Intent.EXTRA_CC );
                if ( ccs != null ) {
                    mSendIntent.putExtra( Intent.EXTRA_CC, ccs );
                }

                String[] bccs = intent.getStringArrayExtra( Intent.EXTRA_BCC );
                if ( bccs != null ) {
                    mSendIntent.putExtra( Intent.EXTRA_BCC, bccs );
                }

                String subject = intent.getStringExtra( Intent.EXTRA_SUBJECT );
                if ( subject != null ) {
                    mSendIntent.putExtra( Intent.EXTRA_SUBJECT, subject );
                }

                mShowUi = intent.getBooleanExtra( EXTRA_SHOW_UI, false );
                mFilterSpecs = intent.getStringArrayExtra( EXTRA_FILTER_SPECS );
                mFormat = intent.getStringExtra( EXTRA_FORMAT );
                mBuffer = intent.getStringExtra( EXTRA_BUFFER );
            }
        }

        if ( null == mSendIntent ) {
            //standalone application
            mShowUi = true;
            mSendIntent = new Intent( Intent.ACTION_SEND );

            String[] emails = intent.getStringArrayExtra( Intent.EXTRA_EMAIL );
            if ( emails != null ) {
                mSendIntent.putExtra( Intent.EXTRA_EMAIL, emails );
            }

            mSendIntent.putExtra( Intent.EXTRA_SUBJECT, "Android device log" );
            mSendIntent.setType( "text/plain" );//$NON-NLS-1$

            mAdditonalInfo = "Log Collector version: 1.1.0\n Device model: sdk\n Firmware version: 1.5\n Kernel version: 2.6.27-00110-g132305e\nmikechan@cheetara #6\nMon Feb 2 12:47:38 PST 2009\n Build number: sdk-eng 1.5 CUPCAKE 148875 test-keys\n";
            mFormat = "time";
        }

        if ( mShowUi ) {
            mMainDialog = new AlertDialog.Builder( this ).setTitle( "Log Collector" ).setMessage( APP_MESSAGE ).setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick( DialogInterface dialog, int whichButton ) {

                    collectAndSendLog();
                }
            } ).setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {

                public void onClick( DialogInterface dialog, int whichButton ) {

                    finish();
                }
            } ).show();
        }
        else {
            collectAndSendLog();
        }
    }

    @SuppressWarnings( "unchecked" )
    void collectAndSendLog() {

        /*Usage: logcat [options] [filterspecs]
        options include:
          -s              Set default filter to silent.
                          Like specifying filterspec '*:s'
          -f <filename>   Log to file. Default to stdout
          -r [<kbytes>]   Rotate log every kbytes. (16 if unspecified). Requires -f
          -n <count>      Sets max number of rotated logs to <count>, default 4
          -v <format>     Sets the log print format, where <format> is one of:

                          brief process tag thread raw time threadtime long

          -c              clear (flush) the entire log and exit
          -d              dump the log and then exit (don't block)
          -g              get the size of the log's ring buffer and exit
          -b <buffer>     request alternate ring buffer
                          ('main' (default), 'radio', 'events')
          -B              output the log in binary
        filterspecs are a series of
          <tag>[:priority]

        where <tag> is a log component tag (or * for all) and priority is:
          V    Verbose
          D    Debug
          I    Info
          W    Warn
          E    Error
          F    Fatal
          S    Silent (supress all output)

        '*' means '*:d' and <tag> by itself means <tag>:v

        If not specified on the commandline, filterspec is set from ANDROID_LOG_TAGS.
        If no filterspec is found, filter defaults to '*:I'

        If not specified with -v, format is set from ANDROID_PRINTF_LOG
        or defaults to "brief"*/

        ArrayList<String> list = new ArrayList<String>();

        if ( mFormat != null ) {
            list.add( "-v" );
            list.add( mFormat );
        }

        if ( mBuffer != null ) {
            list.add( "-b" );
            list.add( mBuffer );
        }

        if ( mFilterSpecs != null ) {
            for (String filterSpec : mFilterSpecs) {
                list.add( filterSpec );
            }
        }

        mCollectLogTask = (CollectLogTask) new CollectLogTask().execute( list );
    }

    private class CollectLogTask extends AsyncTask<ArrayList<String>, Void, StringBuilder> {

        @Override
        protected void onPreExecute() {

            showProgressDialog( "Acquiring log from the system..." );
        }

        @Override
        protected StringBuilder doInBackground( ArrayList<String>... params ) {

            final StringBuilder log = new StringBuilder();
            try {
                ArrayList<String> commandLine = new ArrayList<String>();
                commandLine.add( "logcat" );//$NON-NLS-1$
                commandLine.add( "-d" );//$NON-NLS-1$
                ArrayList<String> arguments = ( ( params != null ) && ( params.length > 0 ) ) ? params[0] : null;
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
                Log.e( COLLECTOR_TAG, "CollectLogTask.doInBackground failed", e );//$NON-NLS-1$
            }

            return log;
        }

        @Override
        protected void onPostExecute( StringBuilder log ) {

            if ( null != log ) {
                //truncate if necessary
                int keepOffset = Math.max( log.length() - MAX_LOG_MESSAGE_LENGTH, 0 );
                if ( keepOffset > 0 ) {
                    log.delete( 0, keepOffset );
                }

                if ( mAdditonalInfo != null ) {
                    log.insert( 0, LINE_SEPARATOR );
                    log.insert( 0, mAdditonalInfo );
                }

                mSendIntent.putExtra( Intent.EXTRA_TEXT, log.toString() );
                startActivity( Intent.createChooser( mSendIntent, "Select an application to send the log (Email)" ) );
                dismissProgressDialog();
                dismissMainDialog();
                finish();
            }
            else {
                dismissProgressDialog();
                showErrorDialog( "Failed to get the log from the system." );
            }
        }
    }

    void showErrorDialog( String errorMessage ) {

        new AlertDialog.Builder( this ).setTitle( "Log Collector" ).setMessage( errorMessage ).setIcon( android.R.drawable.ic_dialog_alert ).setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick( DialogInterface dialog, int whichButton ) {

                finish();
            }
        } ).show();
    }

    void dismissMainDialog() {

        if ( null != mMainDialog && mMainDialog.isShowing() ) {
            mMainDialog.dismiss();
            mMainDialog = null;
        }
    }

    void showProgressDialog( String message ) {

        mProgressDialog = new ProgressDialog( this );
        mProgressDialog.setIndeterminate( true );
        mProgressDialog.setMessage( message );
        mProgressDialog.setCancelable( true );
        mProgressDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {

            public void onCancel( DialogInterface dialog ) {

                cancellCollectTask();
                finish();
            }
        } );
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {

        if ( null != mProgressDialog && mProgressDialog.isShowing() ) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    void cancellCollectTask() {

        if ( mCollectLogTask != null && mCollectLogTask.getStatus() == AsyncTask.Status.RUNNING ) {
            mCollectLogTask.cancel( true );
            mCollectLogTask = null;
        }
    }

    @Override
    protected void onPause() {

        cancellCollectTask();
        dismissProgressDialog();
        dismissMainDialog();

        super.onPause();
    }
}
