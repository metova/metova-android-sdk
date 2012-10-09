package com.metova.android.service.persistence.record.column;

public class Column {

    private String name;
    private ColumnType type;

    public Column(String name, ColumnType type) {

        setName( name );
        setType( type );
    }

    public String getName() {

        return name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public ColumnType getType() {

        return type;
    }

    public void setType( ColumnType type ) {

        this.type = type;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer( getName() );
        buffer.append( " " );
        buffer.append( getType().toString() );

        return buffer.toString();
    }
}
