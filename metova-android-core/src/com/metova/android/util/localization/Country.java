package com.metova.android.util.localization;

public class Country {

    private final String name;
    private final String code;
    private final State[] states;

    public Country(String name, String code) {

        this( name, code, null );
    }

    public Country(String name, String code, State[] states) {

        this.name = name;
        this.code = code;
        this.states = states;
    }

    public String getName() {

        return name;
    }

    public String getCode() {

        return code;
    }

    public String toString() {

        return getName();
    }

    public State[] getStates() {

        return states;
    }
}
