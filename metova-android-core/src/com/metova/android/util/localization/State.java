package com.metova.android.util.localization;

public class State {

    private String name;
    private String abbreviation;

    public State(String name, String abbreviation) {

        this.setName( name );
        this.setAbbreviation( abbreviation );
    }

    public String getName() {

        return name;
    }

    public String getAbbreviation() {

        return abbreviation;
    }

    public String toString() {

        return getName();
    }

    protected void setName( String name ) {

        this.name = name;
    }

    protected void setAbbreviation( String abbreviation ) {

        this.abbreviation = abbreviation;
    }
}
