package org.labyrinth.footpath.graph;

import java.util.LinkedList;
import java.util.List;

public class Way {
    // all nodes on this path ( ref0 -> ref1 -> ref2  -> ...)
    private final List<Long> refs;
    private long id;

    private boolean isHighway = false;

    public Way() {
        this.refs = new LinkedList<>();
        this.id = 0;
    }

    public Way(final List<Long> refs, final long id) {
        this.refs = refs;
        this.id = id;
    }

    public List<Long> getRefs() {
        return refs;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public boolean isHighway() {
        return isHighway;
    }

    public void setHighway(final boolean highway) {
        isHighway = highway;
    }

    public void addRef(final long ref) {
        this.refs.add(ref);
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder("\nWay(" + this.id + "): ");
        ret.append("\nRefs:");
        for (final long ref : refs) {
            ret.append("\n    ").append(ref);
        }
        return ret.toString();
    }
}
