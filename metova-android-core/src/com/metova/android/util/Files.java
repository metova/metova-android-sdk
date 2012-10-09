package com.metova.android.util;

import java.io.FileInputStream;
import java.io.IOException;

public class Files {

	public static final byte[] toByteArray( String filename ) throws IOException {

		byte[] fileContents = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( filename );
			fileContents = Streams.getAsByteArray( fileInputStream );
		}
		finally {

			Streams.close( fileInputStream );
		}

		return fileContents;
	}

	public static final String toString( String filename ) throws IOException {

		byte[] fileContents = toByteArray( filename );
		if ( fileContents != null ) {

			return new String( fileContents );
		}

		return null;
	}
}
