package org.labyrinth.common;

import android.graphics.Bitmap;
import android.util.Size;
import android.util.SizeF;

import math.geom2d.Box2D;
import math.geom2d.Point2D;

import static org.labyrinth.common.SizeUtils.asSizeF;
import static org.labyrinth.common.SizeUtils.getSizeOf;

public class CenterUtils {

    public static Point2D getCenterOf(final SizeF size) {
        return Point2D.fromSizeF(size).scale(0.5);
    }

    public static Point2D getCenterOf(final Size size) {
        return getCenterOf(asSizeF(size));
    }

    public static Point2D getCenterOf(final Box2D boundingBox) {
        return getCenterOf(boundingBox.getSize());
    }

    public static Point2D getCenterOf(final Bitmap bitmap) {
        return getCenterOf(getSizeOf(bitmap));
    }
}
