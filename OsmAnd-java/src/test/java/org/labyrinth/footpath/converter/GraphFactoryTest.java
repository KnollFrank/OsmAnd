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
import org.labyrinth.footpath.graph.RoadPosition;

public class GraphFactoryTest {

    @Test
    public void test_createGraph() {
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
                        ImmutableSet
                                .<RouteSegmentWrapper>builder()
                                .add(new RouteSegmentWrapper(kingersheimerStrasse_0_1))
                                .add(new RouteSegmentWrapper(kingersheimerStrasse_1_2))
                                .add(new RouteSegmentWrapper(kreuzlingerWeg_12_11))
                                .build();
        final GraphFactory graphFactory = new GraphFactory(connectedRouteSegmentsProvider);

        // When
        final Graph graph = graphFactory.createGraph(new RouteSegmentWrapper(kingersheimerStrasse_0_1));

        // Then
        final Node kingersheimerStrasse_0 =
                new Node(
                        new RoadPosition(kingersheimerStrasse_0_1.getRoad().id, kingersheimerStrasse_0_1.getSegmentStart()),
                        getGeodetic(kingersheimerStrasse_0_1, kingersheimerStrasse_0_1.getSegmentStart()));
        final Node kingersheimerStrasse_1 =
                new Node(
                        new RoadPosition(kingersheimerStrasse_0_1.getRoad().id, kingersheimerStrasse_0_1.getSegmentEnd()),
                        getGeodetic(kingersheimerStrasse_0_1, kingersheimerStrasse_0_1.getSegmentEnd()));
        final Node kingersheimerStrasse_2 =
                new Node(
                        new RoadPosition(kingersheimerStrasse_1_2.getRoad().id, kingersheimerStrasse_1_2.getSegmentEnd()),
                        getGeodetic(kingersheimerStrasse_1_2, kingersheimerStrasse_1_2.getSegmentEnd()));
        final Node kreuzlingerWeg_12 =
                new Node(
                        new RoadPosition(kreuzlingerWeg_12_11.getRoad().id, kreuzlingerWeg_12_11.getSegmentStart()),
                        getGeodetic(kreuzlingerWeg_12_11, kreuzlingerWeg_12_11.getSegmentStart()));
        final Node kreuzlingerWeg_11 =
                new Node(
                        new RoadPosition(kreuzlingerWeg_12_11.getRoad().id, kreuzlingerWeg_12_11.getSegmentEnd()),
                        getGeodetic(kreuzlingerWeg_12_11, kreuzlingerWeg_12_11.getSegmentEnd()));
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
                                new Edge(kingersheimerStrasse_0, kingersheimerStrasse_1),
                                new Edge(kingersheimerStrasse_1, kingersheimerStrasse_2),
                                new Edge(kingersheimerStrasse_1, kreuzlingerWeg_12),
                                new Edge(kreuzlingerWeg_12, kreuzlingerWeg_11)));
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

    private static RouteDataObject createRouteDataObject(final int id, final String name) {
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
