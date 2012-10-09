package com.metova.android.test.util;

import android.test.AndroidTestCase;

import com.metova.android.util.ComponentManager;

public class ComponentManagerTest extends AndroidTestCase {

    public void testBeanShouldReturnAnInstanceForARegisteredType() {

        ComponentManager.register( Computer.class, MacBook.class );
        Computer computer = ComponentManager.bean( Computer.class );
        assertTrue( computer instanceof MacBook );
    }

    public void testBeanShouldReturnRegisteredInstance() {

        MacBook macBook = new MacBook();
        ComponentManager.register( Computer.class, macBook );
        assertEquals( macBook, ComponentManager.bean( Computer.class ) );
    }

    public void testUnregisterShouldDiscardRegisteredType() {

        ComponentManager.register( Computer.class, MacBook.class );
        ComponentManager.unregister( Computer.class );
        assertNull( ComponentManager.bean( Computer.class ) );

        ComponentManager.register( Computer.class, TabletPC.class );
        Computer computer = ComponentManager.bean( Computer.class );
        assertTrue( computer instanceof TabletPC );
    }

    public static interface Computer {

    }

    public static final class MacBook implements Computer {

    }

    public static final class TabletPC implements Computer {

    }
}
