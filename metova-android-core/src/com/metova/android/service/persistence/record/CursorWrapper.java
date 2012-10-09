package com.metova.android.service.persistence.record;

import android.database.Cursor;

import com.metova.android.util.Streams;

public final class CursorWrapper {

    private Cursor cursor;
    private int offset;

    public CursorWrapper(Cursor cursor) {

        this.setCursor( cursor );
    }

    public byte[] nextBlob() {

        return getCursor().getBlob( offset++ );
    }

    public boolean nextBoolean() {

        return getCursor().getLong( offset++ ) == 1L;
    }

    public double nextDouble() {

        return getCursor().getDouble( offset++ );
    }

    public float nextFloat() {

        return getCursor().getFloat( offset++ );
    }

    public int nextInt() {

        return getCursor().getInt( offset++ );
    }

    public long nextLong() {

        return getCursor().getLong( offset++ );
    }

    public short nextShort() {

        return getCursor().getShort( offset++ );
    }

    public String nextString() {

        return getCursor().getString( offset++ );
    }

    /**
     * @see Cursor#close()
     */
    public void close() {

        Streams.close( getCursor() );
        setCursor( null );
    }

    /**
     * @see Cursor#move(int)
     */
    public boolean move( int offset ) {

        offset = 0;
        return getCursor().move( offset );
    }

    /**
     * @see Cursor#moveToFirst()
     */
    public boolean moveToFirst() {

        offset = 0;
        return getCursor().moveToFirst();
    }

    /**
     * @see Cursor#moveToLast()
     */
    public boolean moveToLast() {

        offset = 0;
        return getCursor().moveToLast();
    }

    /**
     * @see Cursor#moveToNext()
     */
    public boolean moveToNext() {

        offset = 0;
        return getCursor().moveToNext();
    }

    /**
     * @see Cursor#moveToPosition(int)
     */
    public boolean moveToPosition( int position ) {

        offset = 0;
        return getCursor().moveToPosition( position );
    }

    /**
     * @see Cursor#moveToPrevious()
     */
    public boolean moveToPrevious() {

        offset = 0;
        return getCursor().moveToPrevious();
    }

    private Cursor getCursor() {

        return cursor;
    }

    public void setCursor( Cursor cursor ) {

        this.cursor = cursor;
    }
}
