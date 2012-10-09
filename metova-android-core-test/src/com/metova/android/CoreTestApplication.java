package com.metova.android;

import android.database.sqlite.SQLiteDatabase;

import com.metova.android.service.persistence.DatabaseConfiguration;
import com.metova.android.service.widget.WidgetStore;

public class CoreTestApplication extends DatabaseConnectedApplication {

    private final DatabaseConfiguration databaseConfiguration = new CoreTestDatabaseConfiguration();

    @Override
    protected DatabaseConfiguration getDatabaseConfiguration() {

        return databaseConfiguration;
    }

    private static final class CoreTestDatabaseConfiguration extends DatabaseConfiguration {

        @Override
        public void createTables( SQLiteDatabase db ) {

            WidgetStore.instance().createTable( db );
        }

        @Override
        public void upgradeTables( SQLiteDatabase db, int oldVersion, int newVersion ) {

            WidgetStore.instance().upgradeTable( db, oldVersion, newVersion );
        }
    }
}
