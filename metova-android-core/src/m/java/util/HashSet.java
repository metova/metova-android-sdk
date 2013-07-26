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

/**
 * HashSet is an implementation of Set. All optional operations are supported,
 * adding and removing. The elements can be any objects.
 */
public class HashSet extends AbstractSet implements Set {

    private static final long serialVersionUID = -5024744406713321676L;

    transient HashMap delegate;

    /**
     * Constructs a new empty instance of HashSet.
     */
    public HashSet() {

        this( new HashMap() );
    }

    /**
     * Constructs a new instance of HashSet with the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of this HashSet
     */
    public HashSet(int capacity) {

        this( new HashMap( capacity ) );
    }

    /**
     * Constructs a new instance of HashSet with the specified capacity and load
     * factor.
     * 
     * @param capacity
     *            the initial capacity
     * @param loadFactor
     *            the initial load factor
     */
    public HashSet(int capacity, float loadFactor) {

        this( new HashMap( capacity ) );
    }

    /**
     * Constructs a new instance of HashSet containing the unique elements in
     * the specified collection.
     * 
     * @param collection
     *            the collection of elements to add
     */
    public HashSet(Collection collection) {

        this( new HashMap( collection.size() < 6 ? 11 : collection.size() * 2 ) );

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {

            add( iterator.next() );
        }
    }

    private HashSet(HashMap delegate) {

        this.delegate = delegate;
    }

    /**
     * Adds the specified object to this HashSet.
     * 
     * @param object
     *            the object to add
     * @return true when this HashSet did not already contain the object, false
     *         otherwise
     */
    public boolean add( Object object ) {

        return delegate.put( object, this ) == null;
    }

    /**
     * Removes all elements from this HashSet, leaving it empty.
     * 
     * @see #isEmpty
     * @see #size
     */
    public void clear() {

        delegate.clear();
    }

    /**
     * Searches this HashSet for the specified object.
     * 
     * @param object
     *            the object to search for
     * @return true if <code>object</code> is an element of this HashSet,
     *         false otherwise
     */
    public boolean contains( Object object ) {

        return delegate.containsKey( object );
    }

    /**
     * Answers if this HashSet has no elements, a size of zero.
     * 
     * @return true if this HashSet has no elements, false otherwise
     * 
     * @see #size
     */
    public boolean isEmpty() {

        return delegate.isEmpty();
    }

    /**
     * Answers an Iterator on the elements of this HashSet.
     * 
     * @return an Iterator on the elements of this HashSet
     * 
     * @see Iterator
     */
    public Iterator iterator() {

        return delegate.keySet().iterator();
    }

    /**
     * Removes an occurrence of the specified object from this HashSet.
     * 
     * @param object
     *            the object to remove
     * @return true if this HashSet is modified, false otherwise
     */
    public boolean remove( Object object ) {

        return delegate.remove( object ) != null;
    }

    /**
     * Answers the number of elements in this HashSet.
     * 
     * @return the number of elements in this HashSet
     */
    public int size() {

        return delegate.size();
    }
}
