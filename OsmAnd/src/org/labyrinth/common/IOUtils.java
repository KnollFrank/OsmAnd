package org.labyrinth.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    public static FileInputStream getFileInputStream(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void saveSource2Sink(final InputStream source, final File sink) {
        try {
            final byte[] buffer = new byte[source.available()];
            source.read(buffer);
            try (final OutputStream outStream = new FileOutputStream(sink)) {
                outStream.write(buffer);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
