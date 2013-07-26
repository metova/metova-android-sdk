package com.metova.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Test class for methods that use a {@link android.os.Parcelable} object.
 */
public class StubParcelable implements Parcelable {

    private String string;
    private Integer integer;

    public StubParcelable() {

    }

    public StubParcelable(String string, Integer integer) {

        this.string = string;
        this.integer = integer;
    }

    private StubParcelable(Parcel source) {

        setString( (String) source.readValue( String.class.getClassLoader() ) );
        setInteger( (Integer) source.readValue( Integer.class.getClassLoader() ) );
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel( Parcel dest, int flags ) {

        dest.writeValue( getString() );
        dest.writeValue( getInteger() );
    }

    public static final Parcelable.Creator<StubParcelable> CREATOR = new Parcelable.Creator<StubParcelable>() {

        @Override
        public StubParcelable createFromParcel( Parcel source ) {

            return new StubParcelable( source );
        }

        @Override
        public StubParcelable[] newArray( int size ) {

            return new StubParcelable[size];
        }
    };

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( integer == null ) ? 0 : integer.hashCode() );
        result = prime * result + ( ( string == null ) ? 0 : string.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {

        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        StubParcelable other = (StubParcelable) obj;
        if ( integer == null ) {
            if ( other.integer != null )
                return false;
        }
        else if ( !integer.equals( other.integer ) )
            return false;
        if ( string == null ) {
            if ( other.string != null )
                return false;
        }
        else if ( !string.equals( other.string ) )
            return false;
        return true;
    }

    public String getString() {

        return string;
    }

    public void setString( String string ) {

        this.string = string;
    }

    public Integer getInteger() {

        return integer;
    }

    public void setInteger( Integer integer ) {

        this.integer = integer;
    }
}
