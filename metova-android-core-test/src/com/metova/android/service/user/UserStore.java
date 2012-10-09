package com.metova.android.service.user;

import com.metova.android.model.User;
import com.metova.android.service.persistence.AbstractRecordStore;
import com.metova.android.service.persistence.record.CursorWrapper;
import com.metova.android.service.persistence.record.SQLiteStatementWrapper;
import com.metova.android.service.persistence.record.column.Column;
import com.metova.android.service.persistence.record.column.Columns;

public class UserStore extends AbstractRecordStore<User> {

    private static final Column[] EXTRA_COLUMNS = new Column[] { Columns.text( "authenticationToken" ), Columns.text( "email" ), Columns.text( "password" ), Columns.integer( "rememberMe" ) };
    private static final UserStore INSTANCE = new UserStore();

    protected UserStore() {

        super( User.class );
    }

    public static UserStore instance() {

        return INSTANCE;
    }

    @Override
    protected Column[] getExtraColumns() {

        return EXTRA_COLUMNS;
    }

    @Override
    protected void bindRecord( SQLiteStatementWrapper statement, User object ) {

        statement.bindString( object.getAuthenticationToken() );
        statement.bindString( object.getEmail() );
        statement.bindString( object.getPassword() );
        statement.bindBoolean( object.isRememberMe() );
    }

    @Override
    protected void populateRecord( CursorWrapper cursor, User object ) {

        object.setAuthenticationToken( cursor.nextString() );
        object.setEmail( cursor.nextString() );
        object.setPassword( cursor.nextString() );
        object.setRememberMe( cursor.nextBoolean() );
    }
}
