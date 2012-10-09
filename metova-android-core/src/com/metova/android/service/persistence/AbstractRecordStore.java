package com.metova.android.service.persistence;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.metova.android.DatabaseConnectedApplication;
import com.metova.android.model.persistence.AbstractRecord;
import com.metova.android.service.persistence.record.CursorWrapper;
import com.metova.android.service.persistence.record.SQLiteStatementWrapper;
import com.metova.android.service.persistence.record.column.Column;
import com.metova.android.service.persistence.record.column.ColumnType;
import com.metova.android.util.Streams;

/**
 * Store helper which takes care of many basic database operations.
 */
public abstract class AbstractRecordStore<T extends AbstractRecord> {

    private static final String TAG = AbstractRecordStore.class.getSimpleName();

    private static final Column[] ABSTRACT_COLUMNS = { new Column( "id", ColumnType.INTEGER_PRIMARY_KEY ) };
    private static final String ID_ASCENDING = "id ASC";

    private final Class<T> type;

    protected AbstractRecordStore(Class<T> type) {

        this.type = type;
    }

    /**
     * Returns columns specific to the database table. This should <b>NOT</b> include 
     * fields described in {@link AbstractRecord}.
     * 
     * @return an array of column objects.
     */
    protected abstract Column[] getExtraColumns();

    /**
     * Binds a statement with any needed values from the specified {@link AbstractRecord}.
     * 
     * @param statement the statement to be bound with record values.
     * @param object the record containing the desired values.
     */
    protected abstract void bindRecord( final SQLiteStatementWrapper statement, final T object );

    /**
     * Populates an {@link AbstractRecord} object with any needed values from the current cursor index.
     * 
     * @param cursor
     * @param object
     */
    protected abstract void populateRecord( final CursorWrapper cursor, final T object );

    /**
     * Returns the name of the table.  By default, this is the simple class name of the 
     * type of {@link AbstractRecord} for which the table configuration was created.
     * 
     * @return The table name.
     */
    public String getTableName() {

        return getType().getSimpleName();
    }

    /**
     * Creates database table for the current store.
     * 
     * @param db
     */
    public final void createTable( final SQLiteDatabase db ) {

        try {

            db.beginTransaction();

            StringBuffer query = new StringBuffer( "CREATE TABLE " );
            query.append( getTableName() );
            query.append( " ( " );

            Column[] columns = getColumns();
            for (int i = 0; i < columns.length; i++) {

                query.append( columns[i].toString() );
                if ( i < columns.length - 1 ) {
                    query.append( ", " );
                }
            }

            query.append( " );" );
            db.execSQL( query.toString() );

            db.setTransactionSuccessful();
        }
        finally {

            db.endTransaction();
        }
    }

    /**
     * Upgrades the table for the current store.
     * 
     * @param db
     * @param oldVersion
     * @param newVersion
     * 
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     */
    public void upgradeTable( final SQLiteDatabase db, int oldVersion, int newVersion ) {

    }

    /**
     * Deletes the record which represents the specified object.  The object's ID field value 
     * is set to 0 to indicate that the object is no longer represented by a record in the database.
     * 
     * @param object the record to delete.
     */
    public final void delete( final AbstractRecord object ) {

        if ( object == null ) {
            throw new IllegalArgumentException( "Record to delete can not be null." );
        }

        if ( object.getId() <= 0 ) {
            throw new IllegalStateException( "Can not delete " + object + " because it has not been inserted." );
        }

        DatabaseConnectedApplication.getWritableDatabase().delete( getTableName(), "id=?", new String[] { Long.toString( object.getId() ) } );
        object.setId( 0 );
    }

    /**
     * Deletes all records from the table for the current store.
     */
    public final void deleteAll() {

        DatabaseConnectedApplication.getWritableDatabase().delete( getTableName(), null, null );
    }

    /**
     * Creates a new record of the data represented in the specified object. The object's 
     * ID field value is set to be the primary key ID for the newly created record.
     * 
     * Do not use this insert method for inserting a list of objects. This uses one database transaction per insert.
     * 
     * @param object the object containing the data for the new record.
     * @return the primary key ID for the newly created record.
     */
    public final long insert( final T object ) {

        if ( object == null ) {
            throw new IllegalArgumentException( "Record to insert can not be null." );
        }

        if ( object.getId() > 0 ) {
            throw new IllegalStateException( "Can not insert " + object + " because it has already been inserted." );
        }

        StringBuffer query = createInsertQuery();
        long id = -1;

        SQLiteStatement statement = null;
        try {

            statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query.toString() );

            SQLiteStatementWrapper statementWrapper = new SQLiteStatementWrapper( statement );
            bindRecord( statementWrapper, object );

            id = statement.executeInsert();
            object.setId( id );
        }
        finally {

            Streams.close( statement );
        }

        return id;
    }

    /**
     * Creates new records of the data represented in the specified list of objects. This uses a single transaction which
     * greatly increases performance on bulk inserts. 
     * 
     * @param objects - The list of objects containing data for the new records.
     */
    public final void insert( final List<T> objects ) {

        if ( objects == null || objects.size() == 0 ) {
            throw new IllegalArgumentException( "Records to insert can not be null or empty." );
        }

        long id = -1;
        SQLiteStatement statement = null;
        try {

            StringBuffer query = createInsertQuery();

            DatabaseConnectedApplication.getWritableDatabase().beginTransaction();
            for (T object : objects) {

                statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query.toString() );
                SQLiteStatementWrapper statementWrapper = new SQLiteStatementWrapper( statement );
                bindRecord( statementWrapper, object );

                id = statement.executeInsert();
                object.setId( id );

                if ( statement != null ) {
                    Streams.close( statement );
                }
            }

            DatabaseConnectedApplication.getWritableDatabase().setTransactionSuccessful();
        }
        finally {

            DatabaseConnectedApplication.getWritableDatabase().endTransaction();
        }
    }

    /**
     * Updates the record represented by the specified object to contain the values currently 
     * held in the object.
     * 
     * Do not use this to bulk update objects. This uses a database transaction per update.
     * 
     * @param object the object containing new values, which shall replace the backing record's values.
     */
    public final void update( final T object ) {

        if ( object == null ) {
            throw new IllegalArgumentException( "Record to update can not be null." );
        }

        if ( object.getId() <= 0 ) {
            throw new IllegalStateException( "Can not update " + object + " because it has not been inserted." );
        }

        StringBuffer query = createUpdateQuery();

        SQLiteStatement statement = null;
        try {

            statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query.toString() );

            SQLiteStatementWrapper statementWrapper = new SQLiteStatementWrapper( statement );
            bindRecord( statementWrapper, object );

            statementWrapper.bindLong( object.getId() );
            statement.execute();
        }
        finally {

            Streams.close( statement );
        }
    }

    /**
     * Updates a list of records represented by the specified list of objects to contain the values currently 
     * stored for these objects. This uses a single transaction which greatly increases performance on bulk updates. 
     * 
     * @param objects - The list of objects containing new values, which shall replace the backing records' values.
     */
    public final void update( final List<T> objects ) {

        if ( objects == null || objects.size() == 0 ) {
            throw new IllegalArgumentException( "Records to update can not be null or empty." );
        }

        StringBuffer query = createUpdateQuery();
        SQLiteStatement statement = null;

        try {

            DatabaseConnectedApplication.getWritableDatabase().beginTransaction();
            statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query.toString() );
            for (T object : objects) {

                SQLiteStatementWrapper statementWrapper = new SQLiteStatementWrapper( statement );
                bindRecord( statementWrapper, object );

                statementWrapper.bindLong( object.getId() );
                statement.execute();

                if ( statement != null ) {

                    Streams.close( statement );

                }
            }

            DatabaseConnectedApplication.getWritableDatabase().setTransactionSuccessful();
        }
        finally {

            DatabaseConnectedApplication.getWritableDatabase().endTransaction();
        }
    }

    /**
     * Retrieves a count of the number of records in the given table.
     * 
     * @return the total number of records in the table, or -1 if an error was encountered.
     */
    public final long count() {

        String query = "SELECT COUNT(1) FROM " + getTableName();
        long count = -1;

        SQLiteStatement statement = null;
        try {

            statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query );
            count = statement.simpleQueryForLong();
        }
        finally {

            Streams.close( statement );
        }

        return count;
    }

    /**
     * Finds a count of items in the current store's table which have an ID smaller than that 
     * of the specified object.
     * 
     * @param object the object whose ID should be greater than any item included in the returned count.
     * @return the number of records which have an ID smaller than the specified object's ID.
     */
    public final long countBefore( final T object ) {

        String query = "SELECT COUNT(1) FROM " + getTableName() + " WHERE id < " + object.getId();
        long count = -1;

        SQLiteStatement statement = null;
        try {

            statement = DatabaseConnectedApplication.getWritableDatabase().compileStatement( query );
            count = statement.simpleQueryForLong();
        }
        finally {

            Streams.close( statement );
        }

        return count;
    }

    /**
     * Select all available records for the given table.
     * 
     * @return a list of all records in the table.
     */
    public final List<T> selectAll() {

        final List<T> list = new ArrayList<T>();

        CursorWrapper cursorWrapper = null;
        try {

            cursorWrapper = new CursorWrapper( DatabaseConnectedApplication.getWritableDatabase().query( getTableName(), getColumnNames(), null, null, null, null, ID_ASCENDING ) );
            if ( cursorWrapper.moveToFirst() ) {

                do {
                    T object = null;
                    try {

                        object = getType().newInstance();
                        object.setId( cursorWrapper.nextLong() );
                        populateRecord( cursorWrapper, object );
                        list.add( object );
                    }
                    catch (IllegalAccessException e) {
                        Log.e( TAG + "#selectAll", "Could not access constructor of " + type + " from the current context.", e );
                    }
                    catch (InstantiationException e) {
                        Log.e( TAG + "#selectAll", "Could not instantiate new instance of " + type, e );
                    }
                }
                while (cursorWrapper.moveToNext());
            }
        }
        finally {

            Streams.close( cursorWrapper );
        }

        return list;
    }

    private final String[] getColumnNames() {

        final Column[] columns = getColumns();
        final String[] columnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            columnNames[i] = columns[i].getName();
        }

        return columnNames;
    }

    /**
     * Select records with the specified LIMIT for the given table.
     * 
     * @param offset offset of the first record.
     * @param count number of records to return.
     * @return a list of records in the table within the specified limits.
     */
    public final List<T> select( long offset, long count ) {

        final List<T> list = new ArrayList<T>();
        final String limit = offset + ", " + count;

        CursorWrapper cursorWrapper = null;
        try {

            cursorWrapper = new CursorWrapper( DatabaseConnectedApplication.getWritableDatabase().query( getTableName(), getColumnNames(), null, null, null, null, ID_ASCENDING, limit ) );
            if ( cursorWrapper.moveToFirst() ) {

                do {

                    T object = null;
                    try {

                        object = type.newInstance();
                        object.setId( cursorWrapper.nextLong() );
                        populateRecord( cursorWrapper, object );
                        list.add( object );
                    }
                    catch (IllegalAccessException e) {
                        Log.e( TAG + "#select", "Could not access constructor of " + type + " from the current context.", e );
                    }
                    catch (InstantiationException e) {
                        Log.e( TAG + "#select", "Could not instantiate new instance of " + type, e );
                    }
                }
                while (cursorWrapper.moveToNext());
            }
        }
        finally {

            Streams.close( cursorWrapper );
        }

        return list;
    }

    /**
     * Retrieve the record identified by the specified ID.
     * 
     * @param id the ID of the desired record.
     * @return the record identified by the ID.
     */
    public final T get( long id ) {

        return queryLimitOne( "SELECT id," + getCommaDelimitedExtraColumnNames() + " FROM " + getTableName() + " WHERE id=? LIMIT 1", new String[] { Long.toString( id ) } );
    }

    protected String getCommaDelimitedExtraColumnNames() {

        StringBuffer buffer = new StringBuffer();

        Column[] extraColumns = getExtraColumns();
        for (int i = 0; i < extraColumns.length; i++) {

            buffer.append( extraColumns[i] );
            if ( i < extraColumns.length - 1 ) {
                buffer.append( "," );
            }
        }

        return buffer.toString();
    }

    /**
     * Retrieves a single record matching the specified query and parameters.
     * 
     * @param query the parameterized SQL query to execute.
     * @param parameters the parameter values to use to populate the query.
     * @return the record found by the query.
     */
    public final T queryLimitOne( String query, String[] parameters ) {

        if ( !query.toLowerCase().contains( " limit 1" ) ) {
            query += " LIMIT 1";
        }

        List<T> list = query( query, parameters );
        if ( list != null && list.size() > 0 ) {
            return list.get( 0 );
        }

        return null;
    }

    /**
     * Retrieves records matching the specified query and parameters.
     * 
     * @param query the parameterized SQL query to execute.
     * @param parameters the parameter values to use to populate the query.
     * @return the records found by the query.
     */
    public final List<T> query( String query, String[] parameters ) {

        List<T> list = new ArrayList<T>();

        CursorWrapper cursorWrapper = null;
        try {

            cursorWrapper = new CursorWrapper( DatabaseConnectedApplication.getWritableDatabase().rawQuery( query, parameters ) );
            if ( cursorWrapper.moveToFirst() ) {

                T object = null;
                try {

                    object = getType().newInstance();
                    object.setId( cursorWrapper.nextInt() );
                    populateRecord( cursorWrapper, object );
                }
                catch (IllegalAccessException e) {

                    Log.e( TAG + "#get", "Could not access constructor of " + getType() + " from the current context.", e );
                    object = null;
                }
                catch (InstantiationException e) {

                    Log.e( TAG + "#get", "Could not instantiate new instance of " + getType(), e );
                    object = null;
                }

                list.add( object );
            }
        }
        finally {

            Streams.close( cursorWrapper );
        }

        return list;
    }

    /**
     * Convenience method to check whether a record exists.
     * 
     * @param object
     * @return true if record exists, false otherwise.
     */
    public final boolean exists( T object ) {

        Cursor cursor = null;
        try {

            cursor = DatabaseConnectedApplication.getWritableDatabase().query( getTableName(), getColumnNames(), "id = ?", new String[] { String.valueOf( object.getId() ) }, null, null, null );
            if ( cursor.moveToFirst() ) {
                return true;
            }
            else {
                return false;
            }
        }
        finally {

            if ( cursor != null ) {

                cursor.close();
            }
        }
    }

    /**
     * Returns the columns available for the database table.
     * 
     * @return an array of column objects.
     */
    private final Column[] getColumns() {

        final Column[] extraColumns = getExtraColumns();
        int extraColumnsLength = extraColumns.length;
        int abstractColumnsLength = ABSTRACT_COLUMNS.length;

        Column[] columns = new Column[abstractColumnsLength + extraColumnsLength];
        for (int i = 0; i < abstractColumnsLength; i++) {
            columns[i] = ABSTRACT_COLUMNS[i];
        }

        for (int i = 0; i < extraColumnsLength; i++) {
            columns[abstractColumnsLength + i] = extraColumns[i];
        }

        return columns;
    }

    private StringBuffer createInsertQuery() {

        StringBuffer query = new StringBuffer( "INSERT INTO " );
        query.append( getTableName() );
        query.append( " ( " );

        Column[] columns = getExtraColumns();
        int columnsLength = columns.length;
        for (int i = 0; i < columnsLength; i++) {

            query.append( columns[i].getName() );
            if ( i < columnsLength - 1 ) {
                query.append( " , " );
            }
        }

        query.append( " ) VALUES ( " );
        for (int i = 0; i < columnsLength; i++) {

            query.append( "?" );
            if ( i < columnsLength - 1 ) {
                query.append( " , " );
            }
        }

        query.append( " )" );
        return query;
    }

    private StringBuffer createUpdateQuery() {

        StringBuffer query = new StringBuffer( "UPDATE " );
        query.append( getTableName() );
        query.append( " SET " );

        Column[] columns = getColumns();
        int columnsLength = columns.length;
        for (int i = 1; i < columnsLength; i++) {

            query.append( columns[i].getName() );
            query.append( "=?" );
            if ( i < columnsLength - 1 ) {
                query.append( " , " );
            }
        }

        query.append( " WHERE id=?" );
        return query;
    }

    public Class<T> getType() {

        return type;
    }
}
