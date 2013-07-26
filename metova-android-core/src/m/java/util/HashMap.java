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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * HashMap is an implementation of Map. All optional operations are supported,
 * adding and removing. Keys and values can be any objects.
 */
public class HashMap extends AbstractMap implements Map {

    private Hashtable delegate;

    // to make this thread safe. . .
    transient int modCount = 0;

    /**
     * Constructs a new empty instance of HashMap.
     *
     */
    public HashMap() {

        delegate = new Hashtable();
    }

    /**
     * Constructs a new instance of HashMap with the specified capacity.
     *
     * @param capacity
     *            the initial capacity of this HashMap
     *
     * @exception IllegalArgumentException
     *                when the capacity is less than zero
     */
    public HashMap(int capacity) {

        if ( capacity >= 0 ) {

            delegate = new Hashtable( capacity );
        }
        else {

            throw new IllegalArgumentException();
        }
    }

    private static class AbstractMapIterator {

        private int expectedModCount;
        Object currentKey;
        final HashMap associatedMap;
        private final Enumeration keyEnumeration;

        private AbstractMapIterator(HashMap hm) {

            associatedMap = hm;
            expectedModCount = hm.modCount;
            keyEnumeration = associatedMap.delegate.keys();
        }

        public boolean hasNext() {

            return keyEnumeration.hasMoreElements();
        }

        private final void checkConcurrentMod() throws ConcurrentModificationException {

            if ( expectedModCount != associatedMap.modCount ) {
                throw new ConcurrentModificationException();
            }
        }

        public final void makeNext() {

            checkConcurrentMod();

            if ( !hasNext() ) {

                throw new NoSuchElementException();
            }

            currentKey = keyEnumeration.nextElement();
        }

        public final void remove() {

            checkConcurrentMod();

            if ( currentKey == null ) {

                throw new IllegalStateException();
            }

            associatedMap.delegate.remove( currentKey );
            expectedModCount++;
            associatedMap.modCount++;
            currentKey = null;
        }
    }

    private static class EntryIterator extends AbstractMapIterator implements Iterator {

        private EntryIterator(HashMap map) {

            super( map );
        }

        public Object next() {

            makeNext();
            return new MapEntry( currentKey, associatedMap.delegate.get( currentKey ) );
        }
    }

    private static class KeyIterator extends AbstractMapIterator implements Iterator {

        private KeyIterator(HashMap map) {

            super( map );
        }

        public Object next() {

            makeNext();
            return currentKey;
        }
    }

    private static class ValueIterator extends AbstractMapIterator implements Iterator {

        private ValueIterator(HashMap map) {

            super( map );
        }

        public Object next() {

            makeNext();
            return associatedMap.delegate.get( currentKey );
        }
    }

    private static class HashMapEntrySet extends AbstractSet {

        private final HashMap associatedMap;

        public HashMapEntrySet(HashMap hm) {

            associatedMap = hm;
        }

        public int size() {

            return associatedMap.size();
        }

        public void clear() {

            associatedMap.clear();
        }

        public boolean remove( Object object ) {

            if ( object instanceof Map.Entry ) {

                Map.Entry oEntry = (Map.Entry) object;
                Entry entry = associatedMap.getEntry( oEntry.getKey() );
                if ( valuesEq( entry, oEntry ) ) {

                    associatedMap.delegate.remove( entry.getKey() );
                    return true;
                }
            }
            return false;
        }

        public boolean contains( Object object ) {

            if ( object instanceof Map.Entry ) {

                Map.Entry oEntry = (Map.Entry) object;
                Entry entry = associatedMap.getEntry( oEntry.getKey() );
                return valuesEq( entry, oEntry );
            }

            return false;
        }

        private static boolean valuesEq( Entry entry, Map.Entry oEntry ) {

            return ( entry != null ) && ( ( entry.getValue() == null ) ? ( oEntry.getValue() == null ) : ( entry.getValue().equals( oEntry.getValue() ) ) );
        }

        public Iterator iterator() {

            return new EntryIterator( associatedMap );
        }
    }

    /**
     * Removes all mappings from this HashMap, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    public void clear() {

        if ( delegate.size() > 0 ) {

            delegate.clear();
            modCount++;
        }
    }

    /**
     * Searches this HashMap for the specified key.
     *
     * @param key
     *            the object to search for
     * @return true if <code>key</code> is a key of this HashMap, false
     *         otherwise
     */
    public boolean containsKey( Object key ) {

        return delegate.containsKey( key );
    }

    /**
     * Searches this HashMap for the specified value.
     *
     * @param value
     *            the object to search for
     * @return true if <code>value</code> is a value of this HashMap, false
     *         otherwise
     */
    public boolean containsValue( Object value ) {

        return delegate.contains( value );
    }

    /**
     * Answers a Set of the mappings contained in this HashMap. Each element in
     * the set is a Map.Entry. The set is backed by this HashMap so changes to
     * one are reflected by the other. The set does not support adding.
     *
     * @return a Set of the mappings
     */
    public Set entrySet() {

        return new HashMapEntrySet( this );
    }

    /**
     * Answers the value of the mapping with the specified key.
     *
     * @param key
     *            the key
     * @return the value of the mapping with the specified key
     */
    public Object get( Object key ) {

        return delegate.get( key );
    }

    public final Entry getEntry( Object key ) {

        Entry entry = null;
        if ( key != null ) {

            Object value = delegate.get( key );
            if ( value != null ) {

                return new MapEntry( key, value );
            }
        }

        return entry;
    }

    /**
     * Answers if this HashMap has no elements, a size of zero.
     *
     * @return true if this HashMap has no elements, false otherwise
     *
     * @see #size
     */
    public boolean isEmpty() {

        return delegate.isEmpty();
    }

    /**
     * Answers a Set of the keys contained in this HashMap. The set is backed by
     * this HashMap so changes to one are reflected by the other. The set does
     * not support adding.
     *
     * @return a Set of the keys
     */
    public Set keySet() {

        if ( keySet == null ) {

            keySet = new AbstractSet() {

                public boolean contains( Object object ) {

                    return containsKey( object );
                }

                public int size() {

                    return HashMap.this.size();
                }

                public void clear() {

                    HashMap.this.clear();
                }

                public boolean remove( Object key ) {

                    Object removedEntry = HashMap.this.delegate.remove( key );
                    return removedEntry != null;
                }

                public Iterator iterator() {

                    return new KeyIterator( HashMap.this );
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
     */
    public Object put( Object key, Object value ) {

        return delegate.put( key, value );
    }

    /**
     * Copies all the mappings in the given map to this map. These mappings will
     * replace all mappings that this map had for any of the keys currently in
     * the given map.
     *
     * @param map
     *            the Map to copy mappings from
     * @throws NullPointerException
     *             if the given map is null
     */
    public void putAll( Map map ) {

        if ( !map.isEmpty() ) {

            putAllImpl( map );
        }
    }

    private void putAllImpl( Map map ) {

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
     * Removes a mapping with the specified key from this HashMap.
     *
     * @param key
     *            the key of the mapping to remove
     * @return the value of the removed mapping or null if key is not a key in
     *         this HashMap
     */
    public Object remove( Object key ) {

        modCount++;
        return delegate.remove( key );
    }

    /**
     * Answers the number of mappings in this HashMap.
     *
     * @return the number of mappings in this HashMap
     */
    public int size() {

        return delegate.size();
    }

    /**
     * Answers a Collection of the values contained in this HashMap. The
     * collection is backed by this HashMap so changes to one are reflected by
     * the other. The collection does not support adding.
     *
     * @return a Collection of the values
     */
    public Collection values() {

        if ( valuesCollection == null ) {

            valuesCollection = new AbstractCollection() {

                public boolean contains( Object object ) {

                    return containsValue( object );
                }

                public int size() {

                    return HashMap.this.size();
                }

                public void clear() {

                    HashMap.this.clear();
                }

                public Iterator iterator() {

                    return new ValueIterator( HashMap.this );
                }
            };
        }

        return valuesCollection;
    }
}
