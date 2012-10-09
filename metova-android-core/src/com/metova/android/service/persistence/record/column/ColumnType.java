package com.metova.android.service.persistence.record.column;

public enum ColumnType {

    BLOB,
    INTEGER,
    INTEGER_PRIMARY_KEY,
    TEXT,
    TIMESTAMP;

    @Override
    public String toString() {

        return super.toString().replace( "_", " " );
    }
}
