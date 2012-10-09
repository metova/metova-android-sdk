package com.metova.android.model;

import com.metova.android.model.persistence.AbstractRecord;

public class Widget extends AbstractRecord {

    private String code;

    public String getCode() {

        return code;
    }

    public void setCode( String code ) {

        this.code = code;
    }
}
