package com.metova.android.model.persistence;

import com.metova.android.service.persistence.AbstractRecordStore;

/**
 * Used in conjunction with an {@link AbstractRecordStore} to simplify database persistence.
 */
public abstract class AbstractRecord {

    private long id;

    /**
     * The primary key ID for the record.
     * 
     * @return the primary key ID.
     */
    public long getId() {

        return id;
    }

    public void setId( long id ) {

        this.id = id;
    }
}
