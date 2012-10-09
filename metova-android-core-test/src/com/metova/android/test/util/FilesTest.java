package com.metova.android.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.test.AndroidTestCase;

import com.metova.android.util.Files;

public class FilesTest extends AndroidTestCase {

    private static final String FILE_CONTENTS = "Just a test.";
    private static final String FILES_TEST_TXT = "FilesTest.txt";

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        FileOutputStream stream = getContext().openFileOutput( FILES_TEST_TXT, Context.MODE_APPEND );
        stream.write( FILE_CONTENTS.getBytes() );
        stream.flush();
        stream.close();
    }

    @Override
    protected void tearDown() throws Exception {

        File file = getContext().getFileStreamPath( FILES_TEST_TXT );
        file.delete();

        super.tearDown();
    }

    public void testToByteArrayShouldReturnFileContents() throws IOException {

        String filename = getContext().getFilesDir().getPath() + "/" + FILES_TEST_TXT;
        byte[] bytes = Files.toByteArray( filename );
        assertEquals( FILE_CONTENTS, new String( bytes ) );
    }

    public void testToStringShouldReturnFileContents() throws IOException {

        String filename = getContext().getFilesDir().getPath() + "/" + FILES_TEST_TXT;
        String contents = Files.toString( filename );
        assertEquals( FILE_CONTENTS, contents );
    }
}
