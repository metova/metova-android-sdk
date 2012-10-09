package com.metova.android;

import java.lang.ref.WeakReference;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.metova.android.service.persistence.AbstractRecordStore;
import com.metova.android.service.persistence.DatabaseConfiguration;
import com.metova.android.service.persistence.DatabaseService;
import com.metova.android.service.persistence.DatabaseService.DatabaseServiceBinder;

/**
 * Child classes are automatically bound to a database.  Any {@link AbstractRecordStore} 
 * implementation can gain read/write access through the use of this 
 * Application's {{@link #getWritableDatabase()} method.
 */
public abstract class DatabaseConnectedApplication extends Application {

    private static WeakReference<SQLiteDatabase> writableDatabaseReference;

    private DatabaseService databaseService;
    private final ServiceConnection serviceConnection = new DatabaseServiceConnection();

    protected abstract DatabaseConfiguration getDatabaseConfiguration();

    @Override
    public void onCreate() {

        super.onCreate();

        DatabaseService.setDatabaseConfiguration( getDatabaseConfiguration() );
        bindService( new Intent( this, DatabaseService.class ), getServiceConnection(), Context.BIND_AUTO_CREATE );
    }

    public static SQLiteDatabase getWritableDatabase() {

        if ( getWritableDatabaseReference() == null ) {
            throw new IllegalStateException( "Can not get writable database. Database service is not bound. Did you add " + DatabaseService.class.getName() + " as a <service> in your AndroidManifest.xml?" );
        }

        return getWritableDatabaseReference().get();
    }

    private final class DatabaseServiceConnection implements ServiceConnection {

        public void onServiceConnected( ComponentName name, IBinder service ) {

            setDatabaseService( ( (DatabaseServiceBinder) service ).getDatabaseService() );
            setWritableDatabaseReference( new WeakReference<SQLiteDatabase>( getDatabaseService().getWritableDatabase() ) );
        }

        public void onServiceDisconnected( ComponentName name ) {

            setDatabaseService( null );
        }
    }

    private DatabaseService getDatabaseService() {

        return databaseService;
    }

    private void setDatabaseService( DatabaseService databaseService ) {

        this.databaseService = databaseService;
    }

    private final ServiceConnection getServiceConnection() {

        return serviceConnection;
    }

    private static WeakReference<SQLiteDatabase> getWritableDatabaseReference() {

        return writableDatabaseReference;
    }

    private static void setWritableDatabaseReference( WeakReference<SQLiteDatabase> writableDatabaseReference ) {

        DatabaseConnectedApplication.writableDatabaseReference = writableDatabaseReference;
    }
}
