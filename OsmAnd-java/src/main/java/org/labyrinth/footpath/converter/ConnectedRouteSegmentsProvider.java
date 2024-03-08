package org.labyrinth.footpath.converter;

import com.google.common.collect.Iterators;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.RoutingContext;
import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConnectedRouteSegmentsProvider implements IConnectedRouteSegmentsProvider {

    private final RoutingContext routingContext;

    public ConnectedRouteSegmentsProvider(final RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsStartingAt(
                routeSegment,
                routeSegment.delegate.getSegmentEnd());
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsStartingAt(
                routeSegment,
                routeSegment.delegate.getSegmentStart());
    }

    private Set<RouteSegmentWithEquality> getRouteSegmentsStartingAt(
            final RouteSegmentWithEquality routeSegment,
            final short index) {
        return getConnectedRouteSegments(
                new RouteSegmentWithEquality(
                        loadRouteSegmentStartingAtIndex(
                                routeSegment.delegate.getRoad(),
                                index)));
    }

    private RouteSegment loadRouteSegmentStartingAtIndex(final RouteDataObject road, final short index) {
        return routingContext.loadRouteSegment(
                road.getPoint31XTile(index),
                road.getPoint31YTile(index),
                0,
                false);
    }

    private static Set<RouteSegmentWithEquality> getConnectedRouteSegments(final RouteSegmentWithEquality routeSegment) {
        final Iterable<RouteSegment> iterable = () -> getIterator(routeSegment.delegate);
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .map(RouteSegmentWithEquality::new)
                .collect(Collectors.toSet());
    }

    private static Iterator<RouteSegment> getIterator(final RouteSegment routeSegment) {
        return Iterators.concat(
                Collections.singleton(routeSegment).iterator(),
                createNextElementsIterator(routeSegment));
    }

    private static Iterator<RouteSegment> createNextElementsIterator(final RouteSegment routeSegment) {
        return new Iterator<RouteSegment>() {

            private RouteSegment actual = routeSegment;

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public RouteSegment next() {
                actual = actual.getNext();
                return actual;
            }

            @Override
            public boolean hasNext() {
                return actual.getNext() != null;
            }
        };
    }
}
