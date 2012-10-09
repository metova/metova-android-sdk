package com.metova.android.model;

import com.metova.android.model.persistence.AbstractRecord;

public class User extends AbstractRecord {

    private String authenticationToken;
    private String email;
    private String password;
    private boolean rememberMe;

    public String getAuthenticationToken() {

        return authenticationToken;
    }

    public void setAuthenticationToken( String authenticationToken ) {

        this.authenticationToken = authenticationToken;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail( String email ) {

        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword( String password ) {

        this.password = password;
    }

    public boolean isRememberMe() {

        return rememberMe;
    }

    public void setRememberMe( boolean rememberMe ) {

        this.rememberMe = rememberMe;
    }
}
