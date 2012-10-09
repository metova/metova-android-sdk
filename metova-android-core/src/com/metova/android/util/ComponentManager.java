package com.metova.android.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Allows management of beans for IoC behavior. This can be used to provide common interfaces for functionality across
 * platforms. The IoC contract allows a bean to be retrieved at any time by any caller, so bean registration should be
 * the first setup action performed within your application.<br />
 * <br />
 * Example usage:
 * <br /><br />
 * Register:
 * <br />
 * <code>ComponentManager.register( Computer.class, MacBook.class );</code>
 * <br /><br />
 * Retrieval:
 * <br />
 * <code>Computer computer = ComponentManager.bean( Computer.class );</code>
 */
public final class ComponentManager {

    private static Map<String, Object> beans = new HashMap<String, Object>();

    private ComponentManager() {

    }

    /**
     * @param id The interface used to identify the bean.
     * @return the bean which implements the specified interface.
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T bean( Class<T> id ) {

        if ( id != null ) {

            return (T) beans.get( id.getName() );
        }

        return null;
    }

    /**
     * Register a new bean with the application context.
     * 
     * @param id The interface used to identify the bean.
     * @param implementor The class which implements the interface.
     */
    public static void register( Class<?> id, Class<?> implementor ) {

        if ( !id.isAssignableFrom( implementor ) ) {
            throw new IllegalArgumentException( "Type " + implementor + " does not implement " + id + "." );
        }

        try {
            register( id, implementor.newInstance() );
        }
        catch (Exception e) {
            Log.e( "ComponentManager#register()", "Could not create a new instance of " + implementor, e );
            throw new RuntimeException( e );
        }
    }

    public static void register( Class<?> id, Object bean ) {

        if ( !id.isAssignableFrom( bean.getClass() ) ) {
            throw new IllegalArgumentException( "Type " + bean.getClass() + " does not implement " + id + "." );
        }

        beans.put( id.getName(), bean );
    }

    /**
     * Unregisters a bean from the application context.
     * 
     * @param id The interface used to identify the bean.
     * @return true if <code>id</code> was removed, false if it was never registered
     */
    public static boolean unregister( Class<?> id ) {

        return beans.remove( id.getName() ) != null;
    }
}
