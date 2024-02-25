package org.labyrinth.footpath.converter;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.BinaryMapRouteReaderAdapter.RouteRegion;
import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;
import net.osmand.util.MapUtils;

import org.junit.Test;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Arrays;
import java.util.Collections;

public class GraphFactoryTest {

    @Test
    public void test_createGraph_depth1() {
        // Given
        // routeSegment: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [0-1] 2.9 m
        //  1: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [0-1] 2.9 m
        //  2: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [1-2] 10.8 m
        //  3: Road (2), name ('Kreuzlinger Weg') [12-11] 4.8 m
        final RouteSegment kingersheimerStrasse_0_1 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße"),
                        0,
                        1);
        final RouteSegment kingersheimerStrasse_1_2 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße"),
                        1,
                        2);
        final RouteSegment kreuzlingerWeg_12_11 =
                new RouteSegment(
                        createRouteDataObject(2, "Kreuzlinger Weg"),
                        12,
                        11);
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                routeSegment ->
                        routeSegment.equals(new RouteSegmentWithEquality(kingersheimerStrasse_0_1)) ?
                                ImmutableSet
                                        .<RouteSegmentWithEquality>builder()
                                        .add(new RouteSegmentWithEquality(kingersheimerStrasse_0_1))
                                        .add(new RouteSegmentWithEquality(kingersheimerStrasse_1_2))
                                        .add(new RouteSegmentWithEquality(kreuzlingerWeg_12_11))
                                        .build() :
                                Collections.singleton(routeSegment);
        final GraphFactory graphFactory = new GraphFactory(connectedRouteSegmentsProvider);

        // When
        final Graph graph = graphFactory.createGraph(new RouteSegmentWithEquality(kingersheimerStrasse_0_1));

        // Then
        final Node kingersheimerStrasse_0 = getStartNode(kingersheimerStrasse_0_1);
        final Node kingersheimerStrasse_1 = getEndNode(kingersheimerStrasse_0_1);
        final Node kingersheimerStrasse_2 = getEndNode(kingersheimerStrasse_1_2);
        final Node kreuzlingerWeg_12 = getStartNode(kreuzlingerWeg_12_11);
        final Node kreuzlingerWeg_11 = getEndNode(kreuzlingerWeg_12_11);
        final Graph graphExpected =
                new Graph(
                        ImmutableSet
                                .<Node>builder()
                                .add(kingersheimerStrasse_0)
                                .add(kingersheimerStrasse_1)
                                .add(kingersheimerStrasse_2)
                                .add(kreuzlingerWeg_12)
                                .add(kreuzlingerWeg_11)
                                .build(),
                        ImmutableSet.of(
                                new Edge(kingersheimerStrasse_0, kingersheimerStrasse_1, Arrays.asList(kingersheimerStrasse_0_1)),
                                new Edge(kingersheimerStrasse_1, kingersheimerStrasse_2, Arrays.asList(kingersheimerStrasse_1_2)),
                                new Edge(kingersheimerStrasse_1, kreuzlingerWeg_12, Arrays.asList(kingersheimerStrasse_0_1, kreuzlingerWeg_12_11)),
                                new Edge(kreuzlingerWeg_12, kreuzlingerWeg_11, Arrays.asList(kreuzlingerWeg_12_11))));
        GraphUtils.assertActualEqualsExpected(graph, graphExpected);
    }

    @Test
    public void test_createGraph_depth2() {
        // Given
        final RouteSegment kingersheimerStrasse_0_1 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße"),
                        0,
                        1);
        final RouteSegment kingersheimerStrasse_1_2 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße"),
                        1,
                        2);
        final RouteSegment kingersheimerStrasse_2_3 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße"),
                        2,
                        3);
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                routeSegment -> {
                    if (routeSegment.equals(new RouteSegmentWithEquality(kingersheimerStrasse_0_1))) {
                        return ImmutableSet.of(routeSegment, new RouteSegmentWithEquality(kingersheimerStrasse_1_2));
                    }
                    if (routeSegment.equals(new RouteSegmentWithEquality(kingersheimerStrasse_1_2))) {
                        return ImmutableSet.of(routeSegment, new RouteSegmentWithEquality(kingersheimerStrasse_2_3));
                    }
                    return Collections.singleton(routeSegment);
                };
        final GraphFactory graphFactory = new GraphFactory(connectedRouteSegmentsProvider);

        // When
        final Graph graph = graphFactory.createGraph(new RouteSegmentWithEquality(kingersheimerStrasse_0_1));

        // Then
        final Node kingersheimerStrasse_0 = getStartNode(kingersheimerStrasse_0_1);
        final Node kingersheimerStrasse_1 = getEndNode(kingersheimerStrasse_0_1);
        final Node kingersheimerStrasse_2 = getEndNode(kingersheimerStrasse_1_2);
        final Node kingersheimerStrasse_3 = getEndNode(kingersheimerStrasse_2_3);
        final Graph graphExpected =
                new Graph(
                        ImmutableSet
                                .<Node>builder()
                                .add(kingersheimerStrasse_0)
                                .add(kingersheimerStrasse_1)
                                .add(kingersheimerStrasse_2)
                                .add(kingersheimerStrasse_3)
                                .build(),
                        ImmutableSet.of(
                                new Edge(kingersheimerStrasse_0, kingersheimerStrasse_1, Arrays.asList(kingersheimerStrasse_0_1)),
                                new Edge(kingersheimerStrasse_1, kingersheimerStrasse_2, Arrays.asList(kingersheimerStrasse_1_2)),
                                new Edge(kingersheimerStrasse_2, kingersheimerStrasse_3, Arrays.asList(kingersheimerStrasse_2_3))));
        GraphUtils.assertActualEqualsExpected(graph, graphExpected);
    }

    private static Node getStartNode(final RouteSegment routeSegment) {
        return new Node(
                new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart()),
                getGeodetic(routeSegment, routeSegment.getSegmentStart()));
    }

    private static Node getEndNode(final RouteSegment routeSegment) {
        return new Node(
                new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd()),
                getGeodetic(routeSegment, routeSegment.getSegmentEnd()));
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

    static RouteDataObject createRouteDataObject(final int id, final String name) {
        final RouteDataObject routeDataObject = new RouteDataObject((RouteRegion) null);
        routeDataObject.id = id;
        routeDataObject.pointsX = new int[20];
        routeDataObject.pointsY = new int[20];
        return new RouteDataObject(routeDataObject) {

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
