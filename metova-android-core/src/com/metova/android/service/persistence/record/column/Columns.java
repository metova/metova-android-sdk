package com.metova.android.service.persistence.record.column;

/**
 * Utility class to assist with creation of new {@link Column}s.
 */
public final class Columns {

    /**
     * Create a new BLOB column with the specified name.
     * 
     * @param name
     * @return
     */
    public static Column blob( String name ) {

        return new Column( name, ColumnType.BLOB );
    }

    /**
     * Create a new INTEGER column with the specified name.
     * @param name
     * @return
     */
    public static Column integer( String name ) {

        return new Column( name, ColumnType.INTEGER );
    }

    /**
     * Create a new INTEGER PRIMARY KEY column with the specified name.
     * 
     * @param name
     * @return
     */
    public static Column integerPrimaryKey( String name ) {

        return new Column( name, ColumnType.INTEGER_PRIMARY_KEY );
    }

    /**
     * Create a new TEXT column with the specified name.
     * 
     * @param name
     * @return
     */
    public static Column text( String name ) {

        return new Column( name, ColumnType.TEXT );
    }

    /**
     * Create a new TIMESTAMP column with the specified name.
     * 
     * @param name
     * @return
     */
    public static Column timestamp( String name ) {

        return new Column( name, ColumnType.TIMESTAMP );
    }
}
