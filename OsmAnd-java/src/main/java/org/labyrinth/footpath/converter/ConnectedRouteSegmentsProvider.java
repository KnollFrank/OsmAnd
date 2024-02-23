package org.labyrinth.footpath.converter;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;
import net.osmand.router.RoutingContext;

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
    public Set<RouteSegmentWrapper> getConnectedRouteSegments(final RouteSegmentWrapper routeSegment) {
        return ImmutableSet
                .<RouteSegmentWrapper>builder()
                .add(routeSegment)
                .addAll(_getConnectedRouteSegments(loadConnectedRouteSegment(routeSegment)))
                .build();
    }

    private RouteSegmentWrapper loadConnectedRouteSegment(final RouteSegmentWrapper segment) {
        return new RouteSegmentWrapper(loadConnectedRouteSegment(segment.delegate));
    }

    private RouteSegment loadConnectedRouteSegment(final RouteSegment routeSegment) {
        final RouteDataObject road = routeSegment.getRoad();
        final short segmentEnd = routeSegment.getSegmentEnd();
        return routingContext.loadRouteSegment(
                road.getPoint31XTile(segmentEnd),
                road.getPoint31YTile(segmentEnd),
                0,
                false);
    }

    private static Set<RouteSegmentWrapper> _getConnectedRouteSegments(final RouteSegmentWrapper routeSegment) {
        final Iterable<RouteSegment> iterable = () -> getIterator(routeSegment.delegate);
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .map(RouteSegmentWrapper::new)
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
