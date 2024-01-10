package org.labyrinth.common;

import android.graphics.Bitmap;

public class BitmapUtils {

    public static Bitmap getScaledBitmap(final Bitmap bitmap, final int width) {
        final int height = bitmap.getHeight() * width / bitmap.getWidth();
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
}
