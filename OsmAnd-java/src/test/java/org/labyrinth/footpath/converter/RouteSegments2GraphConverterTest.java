package org.labyrinth.footpath.converter;

import static net.osmand.binary.BinaryMapRouteReaderAdapter.RouteRegion;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.util.MapUtils;

import org.junit.Test;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import java.util.Collections;
import java.util.Set;

public class RouteSegments2GraphConverterTest {

    @Test
    public void test_routeSegments2Graph_empty() {
        // Given
        final Set<RouteSegmentWrapper> noRouteSegments = Collections.emptySet();
        final RouteSegments2GraphConverter routeSegments2GraphConverter = new RouteSegments2GraphConverter();

        // When
        final Graph graph = routeSegments2GraphConverter.routeSegments2Graph(noRouteSegments);

        // Then
        final Graph emptyGraph = new Graph(Collections.emptySet(), Collections.emptySet());
        GraphUtils.assertActualEqualsExpected(graph, emptyGraph);
    }

    @Test
    public void test_routeSegments2Graph() {
        // Given
        final RouteSegment routeSegment = new RouteSegment(createRouteDataObject(4711), 5, 6);
        final Set<RouteSegmentWrapper> routeSegments =
                ImmutableSet
                        .<RouteSegmentWrapper>builder()
                        .add(new RouteSegmentWrapper(routeSegment))
                        .build();
        final RouteSegments2GraphConverter routeSegments2GraphConverter = new RouteSegments2GraphConverter();

        // When
        final Graph graph = routeSegments2GraphConverter.routeSegments2Graph(routeSegments);

        // Then
        final Node nodeStart =
                new Node(
                        1,
                        getGeodetic(routeSegment, routeSegment.getSegmentStart()),
                        routeSegment.getRoad().getName() + "-start");
        final Node nodeEnd =
                new Node(
                        2,
                        getGeodetic(routeSegment, routeSegment.getSegmentEnd()),
                        routeSegment.getRoad().getName() + "-end");
        final Graph graphExpected =
                new Graph(
                        ImmutableSet.of(nodeStart, nodeEnd),
                        ImmutableSet.of(new Edge(nodeStart, nodeEnd)));
        GraphUtils.assertActualEqualsExpected(graph, graphExpected);
    }

    private static Geodetic getGeodetic(final RouteSegment routeSegment, final short i) {
        return getGeodetic(routeSegment.getRoad(), i);
    }

    private static Geodetic getGeodetic(final RouteDataObject road, final short i) {
        return new Geodetic(getLatitude(road, i), getLongitude(road, i));
    }

    private static Angle getLatitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LatitudeY(road.getPoint31YTile(i)), Angle.Unit.DEGREES);
    }

    private static Angle getLongitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LongitudeX(road.getPoint31XTile(i)), Angle.Unit.DEGREES);
    }

    private static RouteDataObject createRouteDataObject(final int id) {
        final RouteDataObject routeDataObject = new RouteDataObject((RouteRegion) null);
        routeDataObject.id = id;
        routeDataObject.pointsX = new int[10];
        routeDataObject.pointsY = new int[10];
        return routeDataObject;
    }
}
