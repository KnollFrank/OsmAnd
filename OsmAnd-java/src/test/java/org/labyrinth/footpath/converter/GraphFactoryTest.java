package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;
import static org.labyrinth.footpath.converter.GraphFactory.getEquivalentRoadPositions;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.BinaryMapRouteReaderAdapter.RouteRegion;
import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.junit.Test;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Edges;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class GraphFactoryTest {

    @Test
    public void test_createGraph_depth1() {
        // Given
        // routeSegment: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [0-1] 2.9 m
        //  1: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [0-1] 2.9 m
        //  2: Road (1), ref ('L 371'), name ('Kingersheimer Straße') [1-2] 10.8 m
        //  3: Road (2), name ('Kreuzlinger Weg') [12-11] 4.8 m
        final RouteSegmentWithEquality kingersheimerStrasse_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 3),
                                0,
                                1));
        final RouteSegmentWithEquality kingersheimerStrasse_1_2 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 3),
                                1,
                                2));
        final RouteSegmentWithEquality kreuzlingerWeg_1_0 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(2, "Kreuzlinger Weg", 2),
                                1,
                                0));
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                new IConnectedRouteSegmentsProvider() {

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                        return routeSegment.equals(kingersheimerStrasse_0_1) ?
                                ImmutableSet
                                        .<RouteSegmentWithEquality>builder()
                                        .add(kingersheimerStrasse_1_2)
                                        .add(kreuzlingerWeg_1_0)
                                        .build() :
                                Collections.emptySet();
                    }

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                        return Collections.singleton(routeSegment);
                    }
                };
        final GraphFactory graphFactory = new GraphFactory(connectedRouteSegmentsProvider);

        // When
        final Graph graph = graphFactory.createGraph(kingersheimerStrasse_0_1);

        // Then
        final Set<EquivalentRoadPositions> equivalenceRelation = new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider).getRoadPositionEquivalenceRelation(kingersheimerStrasse_0_1);
        final Node kingersheimerStrasse_0 = getStartNode(kingersheimerStrasse_0_1, equivalenceRelation);
        final Node kingersheimerStrasse_1 = getEndNode(kingersheimerStrasse_0_1, equivalenceRelation);
        final Node kingersheimerStrasse_2 = getEndNode(kingersheimerStrasse_1_2, equivalenceRelation);
        final Node kreuzlingerWeg_1 = getStartNode(kreuzlingerWeg_1_0, equivalenceRelation);
        final Node kreuzlingerWeg_0 = getEndNode(kreuzlingerWeg_1_0, equivalenceRelation);
        final Set<Edge> edges =
                ImmutableSet.of(
                        new Edge(kingersheimerStrasse_0, kingersheimerStrasse_1, Arrays.asList(kingersheimerStrasse_0_1.delegate)),
                        new Edge(kingersheimerStrasse_1, kingersheimerStrasse_2, Arrays.asList(kingersheimerStrasse_1_2.delegate)),
                        new Edge(kreuzlingerWeg_1, kreuzlingerWeg_0, Arrays.asList(kreuzlingerWeg_1_0.delegate)));
        final Graph graphExpected =
                org.labyrinth.footpath.graph.GraphFactory.createGraph(
                        union(edges, Edges.reverse(edges)));
        GraphUtils.assertActualEqualsExpected(graph, graphExpected);
    }

    @Test
    public void test_createGraph_depth2() {
        // Given
        final int kingersheimerStrasse_id = 1;
        final RouteSegmentWithEquality kingersheimerStrasse_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(kingersheimerStrasse_id, "Kingersheimer Straße", 4),
                                0,
                                1));
        final RouteSegmentWithEquality kingersheimerStrasse_1_0 = kingersheimerStrasse_0_1.reverse();
        final RouteSegmentWithEquality kingersheimerStrasse_1_2 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(kingersheimerStrasse_id, "Kingersheimer Straße", 4),
                                1,
                                2));
        final RouteSegmentWithEquality kingersheimerStrasse_2_1 = kingersheimerStrasse_1_2.reverse();
        final RouteSegmentWithEquality kingersheimerStrasse_2_3 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(kingersheimerStrasse_id, "Kingersheimer Straße", 4),
                                2,
                                3));
        final RouteSegmentWithEquality kingersheimerStrasse_3_2 = kingersheimerStrasse_2_3.reverse();
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =

                new IConnectedRouteSegmentsProvider() {

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_1_0,
                                    kingersheimerStrasse_1_2);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_1_2)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_2_3,
                                    kingersheimerStrasse_2_1);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_2_1)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_1_0,
                                    kingersheimerStrasse_1_2);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_2_3)) {
                            return ImmutableSet.of(kingersheimerStrasse_3_2);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_3_2)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_2_1,
                                    kingersheimerStrasse_2_3);
                        }
                        throw new IllegalArgumentException();
                    }

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_1_0,
                                    kingersheimerStrasse_1_2);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_1_2)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_1_2,
                                    kingersheimerStrasse_1_0);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_2_1)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_2_1,
                                    kingersheimerStrasse_2_3);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_2_3)) {
                            return ImmutableSet.of(
                                    kingersheimerStrasse_2_3,
                                    kingersheimerStrasse_2_1);
                        }
                        if (routeSegment.equals(kingersheimerStrasse_3_2)) {
                            return ImmutableSet.of(kingersheimerStrasse_3_2);
                        }
                        throw new IllegalArgumentException();
                    }
                };
        final GraphFactory graphFactory = new GraphFactory(connectedRouteSegmentsProvider);
        final RouteSegmentWithEquality start = kingersheimerStrasse_0_1;

        // When
        final Graph graph = graphFactory.createGraph(start);

        // Then
        final Set<EquivalentRoadPositions> equivalenceRelation =
                new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider)
                        .getRoadPositionEquivalenceRelation(start);
        final Node kingersheimerStrasse_0 = getStartNode(kingersheimerStrasse_0_1, equivalenceRelation);
        final Node kingersheimerStrasse_1 = getEndNode(kingersheimerStrasse_0_1, equivalenceRelation);
        final Node kingersheimerStrasse_2 = getEndNode(kingersheimerStrasse_1_2, equivalenceRelation);
        final Node kingersheimerStrasse_3 = getEndNode(kingersheimerStrasse_2_3, equivalenceRelation);
        final Set<Edge> edges =
                ImmutableSet.of(
                        new Edge(kingersheimerStrasse_0, kingersheimerStrasse_1, Arrays.asList(kingersheimerStrasse_0_1.delegate)),
                        new Edge(kingersheimerStrasse_1, kingersheimerStrasse_2, Arrays.asList(kingersheimerStrasse_1_2.delegate)),
                        new Edge(kingersheimerStrasse_2, kingersheimerStrasse_3, Arrays.asList(kingersheimerStrasse_2_3.delegate)));
        final Graph graphExpected =
                org.labyrinth.footpath.graph.GraphFactory.createGraph(
                        union(edges, Edges.reverse(edges)));
        GraphUtils.assertActualEqualsExpected(graph, graphExpected);
    }

    private static Node getStartNode(final RouteSegmentWithEquality routeSegment,
                                     final Set<EquivalentRoadPositions> equivalenceRelation) {
        return new Node(
                getEquivalentRoadPositions(new RoadPosition(routeSegment.delegate.getRoad().id, routeSegment.delegate.getSegmentStart()), equivalenceRelation),
                RouteSegment2Geodetic.getGeodetic(routeSegment.delegate, routeSegment.delegate.getSegmentStart()));
    }

    private static Node getEndNode(final RouteSegmentWithEquality routeSegment,
                                   final Set<EquivalentRoadPositions> equivalenceRelation) {
        return new Node(
                getEquivalentRoadPositions(new RoadPosition(routeSegment.delegate.getRoad().id, routeSegment.delegate.getSegmentEnd()), equivalenceRelation),
                RouteSegment2Geodetic.getGeodetic(routeSegment.delegate, routeSegment.delegate.getSegmentEnd()));
    }

    static RouteDataObject createRouteDataObject(final int id, final String name, final int numPoints) {
        final RouteDataObject routeDataObject = new RouteDataObject((RouteRegion) null);
        routeDataObject.id = id;
        routeDataObject.pointsX = new int[numPoints];
        routeDataObject.pointsY = new int[numPoints];
        return new RouteDataObject(routeDataObject) {

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
