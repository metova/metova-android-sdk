package com.metova.android.service.widget;

import com.metova.android.model.Widget;
import com.metova.android.service.persistence.AbstractRecordStore;
import com.metova.android.service.persistence.record.CursorWrapper;
import com.metova.android.service.persistence.record.SQLiteStatementWrapper;
import com.metova.android.service.persistence.record.column.Column;
import com.metova.android.service.persistence.record.column.Columns;

public final class WidgetStore extends AbstractRecordStore<Widget> {

    private static final Column[] EXTRA_COLUMNS = new Column[] { Columns.text( "code" ) };
    private static final WidgetStore INSTANCE = new WidgetStore();

    protected WidgetStore() {

        super( Widget.class );
    }

    public static WidgetStore instance() {

        return INSTANCE;
    }

    @Override
    protected Column[] getExtraColumns() {

        return EXTRA_COLUMNS;
    }

    @Override
    protected void bindRecord( SQLiteStatementWrapper statement, Widget object ) {

        statement.bindString( object.getCode() );
    }

    @Override
    protected void populateRecord( CursorWrapper cursor, Widget object ) {

        object.setCode( cursor.nextString() );
    }
}
