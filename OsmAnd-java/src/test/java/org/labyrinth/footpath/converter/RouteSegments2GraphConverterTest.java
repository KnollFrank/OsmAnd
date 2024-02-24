package org.labyrinth.footpath.converter;

import static net.osmand.binary.BinaryMapRouteReaderAdapter.RouteRegion;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.util.MapUtils;

import org.junit.Test;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Collections;
import java.util.Set;

public class RouteSegments2GraphConverterTest {

    @Test
    public void test_routeSegments2Graph_empty() {
        // Given
        final Set<RouteSegmentWithEquality> noRouteSegments = Collections.emptySet();
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
        final RouteSegment routeSegment1 = new RouteSegment(createRouteDataObject(4711), 0, 1);
        final RouteSegment routeSegment2 = new RouteSegment(createRouteDataObject(4711), 1, 2);
        final RouteSegment routeSegment3 = new RouteSegment(createRouteDataObject(4711), 1, 3);
        final Set<RouteSegmentWithEquality> routeSegments =
                ImmutableSet
                        .<RouteSegmentWithEquality>builder()
                        .add(new RouteSegmentWithEquality(routeSegment1))
                        .add(new RouteSegmentWithEquality(routeSegment2))
                        .add(new RouteSegmentWithEquality(routeSegment3))
                        .build();
        final RouteSegments2GraphConverter routeSegments2GraphConverter = new RouteSegments2GraphConverter();

        // When
        final Graph graph = routeSegments2GraphConverter.routeSegments2Graph(routeSegments);

        // Then
        final Node nodeStart =
                new Node(
                        new RoadPosition(routeSegment1.getRoad().id, routeSegment1.getSegmentStart()),
                        getGeodetic(routeSegment1, routeSegment1.getSegmentStart()));
        final Node nodeEnd =
                new Node(
                        new RoadPosition(routeSegment1.getRoad().id, routeSegment1.getSegmentEnd()),
                        getGeodetic(routeSegment1, routeSegment1.getSegmentEnd()));
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
