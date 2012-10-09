package com.metova.android.service.persistence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.IBinder;

import com.metova.android.util.Streams;

public final class DatabaseService extends Service {

    private static DatabaseConfiguration databaseConfiguration;

    private final Binder binder = new DatabaseServiceBinder();
    private Database database;
    private SQLiteDatabase writableDatabase;

    @Override
    public void onCreate() {

        super.onCreate();

        Database database = new Database( getApplicationContext() );
        setDatabase( database );
        setWritableDatabase( database.getWritableDatabase() );
    }

    @Override
    public void onDestroy() {

        Streams.close( getWritableDatabase() );
        Streams.close( getDatabase() );

        super.onDestroy();
    }

    @Override
    public IBinder onBind( Intent intent ) {

        return binder;
    }

    /**
     * A {@link SQLiteOpenHelper} implementation which makes assumptions about
     */
    public final class Database extends SQLiteOpenHelper {

        public Database(Context context) {

            super( context, getDatabaseConfiguration().getDatabaseName(), null, getDatabaseConfiguration().getDatabaseVersion() );
        }

        @Override
        public void onCreate( SQLiteDatabase db ) {

            getDatabaseConfiguration().createTables( db );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

            getDatabaseConfiguration().upgradeTables( db, oldVersion, newVersion );
        }
    }

    /**
     * A {@link Binder} which provides access to the {@link DatabaseService} instance.
     */
    public final class DatabaseServiceBinder extends Binder {

        public DatabaseService getDatabaseService() {

            return DatabaseService.this;
        }
    }

    private Database getDatabase() {

        return database;
    }

    private void setDatabase( Database database ) {

        this.database = database;
    }

    public final SQLiteDatabase getWritableDatabase() {

        return writableDatabase;
    }

    private void setWritableDatabase( SQLiteDatabase writableDatabase ) {

        this.writableDatabase = writableDatabase;
    }

    public static DatabaseConfiguration getDatabaseConfiguration() {

        return databaseConfiguration;
    }

    public static void setDatabaseConfiguration( DatabaseConfiguration databaseConfiguration ) {

        DatabaseService.databaseConfiguration = databaseConfiguration;
    }
}
