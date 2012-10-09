package com.metova.android.service.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Used to describe the current database version and create/upgrade actions which should be taken.
 */
public abstract class DatabaseConfiguration {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public String getDatabaseName() {

        return DATABASE_NAME;
    }

    public int getDatabaseVersion() {

        return DATABASE_VERSION;
    }

    public abstract void createTables( SQLiteDatabase db );

    public abstract void upgradeTables( SQLiteDatabase db, int oldVersion, int newVersion );
}
