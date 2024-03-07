package net.osmand.router.postman;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

import java.util.Objects;

public class RouteSegmentWithEquality {

    public final RouteSegment delegate;

    public RouteSegmentWithEquality(final RouteSegment delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RouteSegmentWithEquality that = (RouteSegmentWithEquality) o;
        return this.delegate.getRoad().id == that.delegate.getRoad().id &&
                this.delegate.getSegmentStart() == that.delegate.getSegmentStart() &&
                this.delegate.getSegmentEnd() == that.delegate.getSegmentEnd();
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate.getRoad().id, delegate.getSegmentStart(), delegate.getSegmentEnd());
    }

    @Override
    public String toString() {
        return "RouteSegmentWrapper{" +
                "delegate=" + delegate +
                '}';
    }
}
