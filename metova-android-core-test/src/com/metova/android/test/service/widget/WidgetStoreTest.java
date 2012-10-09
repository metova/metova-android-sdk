package com.metova.android.test.service.widget;

import com.metova.android.model.Widget;
import com.metova.android.service.widget.WidgetStore;
import com.metova.android.test.MainActivityTest;

public class WidgetStoreTest extends MainActivityTest {

    public void testInsertPersistsWidgetRecord() {

        Widget widget1 = new Widget();
        widget1.setCode( "WIDGET-ONE" );

        WidgetStore.instance().insert( widget1 );
        assertTrue( widget1.getId() > 0 );

        Widget widget2 = WidgetStore.instance().get( widget1.getId() );
        assertEquals( widget1.getCode(), widget2.getCode() );
        assertEquals( widget1.getId(), widget2.getId() );
    }
}
