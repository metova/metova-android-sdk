/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package m.java.util;

import m.java.lang.UnsupportedOperationException;

/**
 * AbstractMap is an abstract implementation of the Map interface. This
 * implementation does not support adding. A subclass must implement the
 * abstract method entrySet().
 * 
 * @since 1.2
 */
public abstract class AbstractMap implements Map {

    // Lazily initialized key set.
    protected Set keySet;

    protected Collection valuesCollection;

    /**
     * Constructs a new instance of this AbstractMap.
     */
    protected AbstractMap() {

        super();
    }

    /**
     * Removes all elements from this Map, leaving it empty.
     * 
     * @exception UnsupportedOperationException
     *                when removing from this Map is not supported
     * 
     * @see #isEmpty
     * @see #size
     */
    public void clear() {

        entrySet().clear();
    }

    /**
     * Searches this Map for the specified key.
     * 
     * @param key
     *            the object to search for
     * @return true if <code>key</code> is a key of this Map, false otherwise
     */
    public boolean containsKey( Object key ) {

        Iterator it = entrySet().iterator();
        if ( key != null ) {
            while (it.hasNext()) {
                if ( key.equals( ( (Map.Entry) it.next() ).getKey() ) ) {
                    return true;
                }
            }
        }
        else {
            while (it.hasNext()) {
                if ( ( (Map.Entry) it.next() ).getKey() == null ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Searches this Map for the specified value.
     * 
     * @param value
     *            the object to search for
     * @return true if <code>value</code> is a value of this Map, false
     *         otherwise
     */
    public boolean containsValue( Object value ) {

        Iterator it = entrySet().iterator();
        if ( value != null ) {
            while (it.hasNext()) {
                if ( value.equals( ( (Map.Entry) it.next() ).getValue() ) ) {
                    return true;
                }
            }
        }
        else {
            while (it.hasNext()) {
                if ( ( (Map.Entry) it.next() ).getValue() == null ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a Set of <code>Map.Entry</code>s that represent the entries in
     * this Map. Making changes to this Set will change the original Map and
     * vice-versa. Entries can be removed from the Set, or their values can be
     * changed, but new entries cannot be added to the Set.
     * 
     * @return a Set of <code>Map.Entry</code>s representing the entries in
     *         this Map
     */
    public abstract Set entrySet();

    /**
     * Compares the specified object to this Map and answer if they are equal.
     * The object must be an instance of Map and contain the same key/value
     * pairs.
     * 
     * @param object
     *            the object to compare with this object
     * @return true if the specified object is equal to this Map, false
     *         otherwise
     * 
     * @see #hashCode
     */
    ////@  Override
    public boolean equals( Object object ) {

        if ( this == object ) {
            return true;
        }
        if ( object instanceof Map ) {
            Map map = (Map) object;
            if ( size() != map.size() ) {
                return false;
            }

            Iterator it = entrySet().iterator();

            try {
                while (it.hasNext()) {
                    Entry entry = (Entry) it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    Object obj = map.get( key );
                    if ( null != obj && ( !obj.equals( value ) ) || null == obj && obj != value ) {
                        return false;
                    }
                }
            }
            catch (ClassCastException cce) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Answers the value of the mapping with the specified key.
     * 
     * @param key
     *            the key
     * @return the value of the mapping with the specified key
     */
    public Object get( Object key ) {

        Iterator it = entrySet().iterator();
        if ( key != null ) {
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if ( key.equals( entry.getKey() ) ) {
                    return entry.getValue();
                }
            }
        }
        else {
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if ( entry.getKey() == null ) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Answers an integer hash code for the receiver. Objects which are equal
     * answer the same value for this method.
     * 
     * @return the receiver's hash
     * 
     * @see #equals
     */
    ////@  Override
    public int hashCode() {

        int result = 0;
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            result += it.next().hashCode();
        }
        return result;
    }

    /**
     * Answers if this Map has no elements, a size of zero.
     * 
     * @return true if this Map has no elements, false otherwise
     * 
     * @see #size
     */
    public boolean isEmpty() {

        return size() == 0;
    }

    /**
     * Answers a Set of the keys contained in this Map. The set is backed by
     * this Map so changes to one are reflected by the other. The set does not
     * support adding.
     * 
     * @return a Set of the keys
     */
    public Set keySet() {

        if ( keySet == null ) {
            keySet = new AbstractSet() {

                ////@  Override
                public boolean contains( Object object ) {

                    return containsKey( object );
                }

                ////@  Override
                public int size() {

                    return AbstractMap.this.size();
                }

                ////@  Override
                public Iterator iterator() {

                    return new Iterator() {

                        Iterator setIterator = entrySet().iterator();

                        public boolean hasNext() {

                            return setIterator.hasNext();
                        }

                        public Object next() {

                            return ( (Map.Entry) setIterator.next() ).getKey();
                        }

                        public void remove() {

                            setIterator.remove();
                        }
                    };
                }
            };
        }
        return keySet;
    }

    /**
     * Maps the specified key to the specified value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the value of any previous mapping with the specified key or null
     *         if there was no mapping
     * 
     * @exception UnsupportedOperationException
     *                when adding to this Map is not supported
     * @exception ClassCastException
     *                when the class of the key or value is inappropriate for
     *                this Map
     * @exception IllegalArgumentException
     *                when the key or value cannot be added to this Map
     * @exception NullPointerException
     *                when the key or value is null and this Map does not
     *                support null keys or values
     */
    public Object put( Object key, Object value ) {

        throw new UnsupportedOperationException();
    }

    /**
     * Copies every mapping in the specified Map to this Map.
     * 
     * @param map
     *            the Map to copy mappings from
     * 
     * @exception UnsupportedOperationException
     *                when adding to this Map is not supported
     * @exception ClassCastException
     *                when the class of a key or value is inappropriate for this
     *                Map
     * @exception IllegalArgumentException
     *                when a key or value cannot be added to this Map
     * @exception NullPointerException
     *                when a key or value is null and this Map does not support
     *                null keys or values
     */
    public void putAll( Map map ) {

        Set entrySet = map.entrySet();
        if ( entrySet != null ) {

            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()) {

                Map.Entry entry = (Map.Entry) iterator.next();
                put( entry.getKey(), entry.getValue() );
            }
        }
    }

    /**
     * Removes a mapping with the specified key from this Map.
     * 
     * @param key
     *            the key of the mapping to remove
     * @return the value of the removed mapping or null if key is not a key in
     *         this Map
     * 
     * @exception UnsupportedOperationException
     *                when removing from this Map is not supported
     */
    public Object remove( Object key ) {

        Iterator it = entrySet().iterator();
        if ( key != null ) {
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if ( key.equals( entry.getKey() ) ) {
                    it.remove();
                    return entry.getValue();
                }
            }
        }
        else {
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if ( entry.getKey() == null ) {
                    it.remove();
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Answers the number of elements in this Map.
     * 
     * @return the number of elements in this Map
     */
    public int size() {

        return entrySet().size();
    }

    /**
     * Answers the string representation of this Map.
     * 
     * @return the string representation of this Map
     */
    ////@  Override
    public String toString() {

        if ( isEmpty() ) {
            return "{}"; //$NON-NLS-1$
        }

        StringBuffer buffer = new StringBuffer( size() * 28 );
        buffer.append( '{' );
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if ( key != this ) {
                buffer.append( key );
            }
            else {
                buffer.append( "(this Map)" ); //$NON-NLS-1$
            }
            buffer.append( '=' );
            Object value = entry.getValue();
            if ( value != this ) {
                buffer.append( value );
            }
            else {
                buffer.append( "(this Map)" ); //$NON-NLS-1$
            }
            if ( it.hasNext() ) {
                buffer.append( ", " ); //$NON-NLS-1$
            }
        }
        buffer.append( '}' );
        return buffer.toString();
    }

    /**
     * Answers a collection of the values contained in this map. The collection
     * is backed by this map so changes to one are reflected by the other. The
     * collection supports remove, removeAll, retainAll and clear operations,
     * and it does not support add or addAll operations.
     * 
     * This method answers a collection which is the subclass of
     * AbstractCollection. The iterator method of this subclass answers a
     * "wrapper object" over the iterator of map's entrySet(). The size method
     * wraps the map's size method and the contains method wraps the map's
     * containsValue method.
     * 
     * The collection is created when this method is called at first time and
     * returned in response to all subsequent calls. This method may return
     * different Collection when multiple calls to this method, since it has no
     * synchronization performed.
     * 
     * @return a collection of the values contained in this map
     * 
     */
    public Collection values() {

        if ( valuesCollection == null ) {
            valuesCollection = new AbstractCollection() {

                ////@  Override
                public int size() {

                    return AbstractMap.this.size();
                }

                ////@  Override
                public boolean contains( Object object ) {

                    return containsValue( object );
                }

                ////@  Override
                public Iterator iterator() {

                    return new Iterator() {

                        Iterator setIterator = entrySet().iterator();

                        public boolean hasNext() {

                            return setIterator.hasNext();
                        }

                        public Object next() {

                            return ( (Map.Entry) setIterator.next() ).getValue();
                        }

                        public void remove() {

                            setIterator.remove();
                        }
                    };
                }
            };
        }
        return valuesCollection;
    }

    //TODO:ORIG-CLONE
    //    /**
    //     * Answers a new instance of the same class as the receiver, whose slots
    //     * have been filled in with the values in the slots of the receiver.
    //     * 
    //     * @return Object a shallow copy of this object.
    //     * @exception CloneNotSupportedException
    //     *                if the receiver's class does not implement the interface
    //     *                Cloneable.
    //     */
    //    ////@  Override
    //    ////@  SuppressWarnings("unchecked")
    //    protected Object clone() throws CloneNotSupportedException {
    //        AbstractMap result = (AbstractMap) super.clone();
    //        result.keySet = null;
    //        result.valuesCollection = null;
    //        return result;
    //    }
}
