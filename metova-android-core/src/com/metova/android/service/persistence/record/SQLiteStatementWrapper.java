package com.metova.android.service.persistence.record;

import java.lang.ref.WeakReference;

import android.database.sqlite.SQLiteStatement;

public final class SQLiteStatementWrapper {

    private final WeakReference<SQLiteStatement> sqliteStatementWrapper;
    private int index = 1;

    public SQLiteStatementWrapper(SQLiteStatement sqliteStatement) {

        this.sqliteStatementWrapper = new WeakReference<SQLiteStatement>( sqliteStatement );
    }

    private boolean bindNull( Object object ) {

        if ( object == null ) {

            getSqliteStatement().bindNull( index++ );
            return true;
        }

        return false;
    }

    public void bindBlob( byte[] byteArray ) {

        if ( !bindNull( byteArray ) ) {
            getSqliteStatement().bindBlob( index++, byteArray );
        }
    }

    public void bindBoolean( boolean value ) {

        if ( !bindNull( value ) ) {
            getSqliteStatement().bindLong( index++, value ? 1L : 0L );
        }
    }

    public void bindDouble( Double number ) {

        if ( !bindNull( number ) ) {
            getSqliteStatement().bindDouble( index++, number );
        }
    }

    public void bindLong( Long number ) {

        if ( !bindNull( number ) ) {
            getSqliteStatement().bindLong( index++, number );
        }
    }

    public void bindString( String string ) {

        if ( !bindNull( string ) ) {
            getSqliteStatement().bindString( index++, string );
        }
    }

    private SQLiteStatement getSqliteStatement() {

        if ( sqliteStatementWrapper != null ) {
            return sqliteStatementWrapper.get();
        }

        return null;
    }
}
