package org.labyrinth.model;

import org.labyrinth.coordinate.Geodetic;

import java.util.Objects;

public class PathSrcDst {

    private final Geodetic src;
    private final Geodetic dst;

    public PathSrcDst(final Geodetic src, final Geodetic dst) {
        this.src = src;
        this.dst = dst;
    }

    public Geodetic getSrc() {
        return src;
    }

    public Geodetic getDst() {
        return dst;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PathSrcDst that = (PathSrcDst) o;
        return src.equals(that.src) &&
                dst.equals(that.dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst);
    }

    @Override
    public String toString() {
        return "PathSrcDst{" +
                "src=" + src +
                ", dst=" + dst +
                '}';
    }
}
