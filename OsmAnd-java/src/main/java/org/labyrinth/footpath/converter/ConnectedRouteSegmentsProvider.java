package org.labyrinth.footpath.converter;

import com.google.common.collect.ImmutableSet;
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
    public Set<RouteSegmentWithEquality> getConnectedRouteSegments(final RouteSegmentWithEquality routeSegment) {
        return ImmutableSet
                .<RouteSegmentWithEquality>builder()
                .add(routeSegment)
                .addAll(_getConnectedRouteSegments(loadConnectedRouteSegment(routeSegment)))
                .build();
    }

    private RouteSegmentWithEquality loadConnectedRouteSegment(final RouteSegmentWithEquality segment) {
        return new RouteSegmentWithEquality(loadConnectedRouteSegment(segment.delegate));
    }

    private RouteSegment loadConnectedRouteSegment(final RouteSegment routeSegment) {
        final RouteDataObject road = routeSegment.getRoad();
        final short segmentEnd = routeSegment.getSegmentEnd();
        final RouteSegment routeSegmentEnd =
                routingContext.loadRouteSegment(
                        road.getPoint31XTile(segmentEnd),
                        road.getPoint31YTile(segmentEnd),
                        0,
                        false);
        final short segmentStart = routeSegment.getSegmentStart();
        final RouteSegment routeSegmentStart =
                routingContext.loadRouteSegment(
                        road.getPoint31XTile(segmentStart),
                        road.getPoint31YTile(segmentStart),
                        0,
                        false);
        return routeSegmentEnd;
    }

    private static Set<RouteSegmentWithEquality> _getConnectedRouteSegments(final RouteSegmentWithEquality routeSegment) {
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
