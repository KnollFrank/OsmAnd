package org.labyrinth.model;

import org.labyrinth.footpath.graph.EdgePosition;

import java.util.Objects;
import java.util.StringJoiner;

public class PathEdgePositionSrcDst {

    private final EdgePosition src;
    private final EdgePosition dst;

    public PathEdgePositionSrcDst(final EdgePosition src, final EdgePosition dst) {
        this.src = src;
        this.dst = dst;
    }

    public EdgePosition getSrc() {
        return src;
    }

    public EdgePosition getDst() {
        return dst;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PathEdgePositionSrcDst that = (PathEdgePositionSrcDst) o;
        return src.equals(that.src) &&
                dst.equals(that.dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dst);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PathEdgePositionSrcDst.class.getSimpleName() + "[", "]")
                .add("src=" + src)
                .add("dst=" + dst)
                .toString();
    }
}
