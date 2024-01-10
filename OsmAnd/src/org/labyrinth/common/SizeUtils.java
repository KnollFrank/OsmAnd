package org.labyrinth.common;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Size;
import android.util.SizeF;

public class SizeUtils {

    public static Size getSizeOf(final Drawable drawable) {
        return new Size(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    static SizeF asSizeF(final Size size) {
        return new SizeF(size.getWidth(), size.getHeight());
    }

    public static Size getSizeOf(final Bitmap bitmap) {
        return new Size(bitmap.getWidth(), bitmap.getHeight());
    }
}