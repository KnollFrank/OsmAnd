package org.labyrinth.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compressor {

    public static byte[] compress(final byte[] bytes) {
        final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (final GZIPOutputStream zipStream = new GZIPOutputStream(compressed)) {
            zipStream.write(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return compressed.toByteArray();
    }

    public static byte[] decompress(final byte[] bytes) {
        final ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        try (final GZIPInputStream bytesStream = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            copy(bytesStream, decompressed);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return decompressed.toByteArray();
    }

    private static void copy(final GZIPInputStream src, final ByteArrayOutputStream dst) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = src.read(buffer)) != -1) {
            dst.write(buffer, 0, len);
        }
    }
}
