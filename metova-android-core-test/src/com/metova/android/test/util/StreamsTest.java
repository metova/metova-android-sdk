package com.metova.android.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.test.AndroidTestCase;

import com.metova.android.util.Streams;

public class StreamsTest extends AndroidTestCase {

    private static final byte[] TEST_BYTES = { 't', 'e', 's', 't' };

    public void testCloseShouldCloseAnInputStream() {

        CloseableByteArrayInputStream stream = new CloseableByteArrayInputStream( TEST_BYTES );

        // sanity check: ensure the stream can be read (i.e., the stream is open).
        byte[] foundBytes = new byte[TEST_BYTES.length - 1];
        stream.read( foundBytes, 0, TEST_BYTES.length - 1 );
        for (int i = 0; i < TEST_BYTES.length - 1; i++) {
            assertEquals( TEST_BYTES[i], foundBytes[i] );
        }

        assertFalse( stream.isClosed() );
        Streams.close( stream );
        assertTrue( stream.isClosed() );
    }

    public void testCloseShouldCloseAnOutputStream() {

        CloseableStringOutputStream stream = new CloseableStringOutputStream();
        Streams.close( stream );
        assertTrue( stream.isClosed() );
    }

    public void testGetAsByteArrayShouldReturnStreamContents() throws Throwable {

        CloseableByteArrayInputStream stream = new CloseableByteArrayInputStream( TEST_BYTES );

        byte[] foundBytes = Streams.getAsByteArray( stream );
        for (int i = 0; i < TEST_BYTES.length; i++) {
            assertEquals( TEST_BYTES[i], foundBytes[i] );
        }
    }

    public void testCopyShouldWriteContentsToStream() throws IOException {

        InputStream inputStream = new ByteArrayInputStream( TEST_BYTES );
        CloseableStringOutputStream outputStream = new CloseableStringOutputStream();
        Streams.copy( inputStream, outputStream );
        Streams.close( inputStream );
        Streams.close( outputStream );

        assertEquals( new String( TEST_BYTES ), outputStream.getContents() );
    }

    private static class CloseableByteArrayInputStream extends ByteArrayInputStream {

        private boolean closed = false;

        public CloseableByteArrayInputStream(byte[] bytes) {

            super( bytes );
        }

        @Override
        public void close() throws IOException {

            super.close();
            setClosed( true );
        }

        private void setClosed( boolean closed ) {

            this.closed = closed;
        }

        public boolean isClosed() {

            return closed;
        }
    }

    private static class CloseableStringOutputStream extends OutputStream {

        private final StringBuffer buffer = new StringBuffer();
        private boolean closed = false;

        @Override
        public void close() throws IOException {

            super.close();
            setClosed( true );
        }

        private void setClosed( boolean closed ) {

            this.closed = closed;
        }

        public boolean isClosed() {

            return closed;
        }

        @Override
        public void write( int oneByte ) throws IOException {

            buffer.append( new String( new byte[] { (byte) oneByte } ) );
        }

        public String getContents() {

            return buffer.toString();
        }
    }
}
