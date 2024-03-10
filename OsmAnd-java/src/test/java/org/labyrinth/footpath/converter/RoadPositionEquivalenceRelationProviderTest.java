package org.labyrinth.footpath.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.labyrinth.footpath.converter.GraphFactoryTest.createRouteDataObject;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.junit.Test;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Set;

public class RoadPositionEquivalenceRelationProviderTest {

    @Test
    public void test_getRoadPositionEquivalenceRelation_Tjunction() {
        // Given
        // 0--Hofweg--1--> <--1--kingersheimerStrasse--0
        //                ^
        //                0
        //                |
        //          Kreuzlinger Weg
        //                |
        //                v
        //                1
        final RouteSegmentWithEquality kingersheimerStrasse_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 2),
                                0,
                                1));
        final RouteSegmentWithEquality kingersheimerStrasse_1_0 = kingersheimerStrasse_0_1.reverse();
        final RouteSegmentWithEquality hofweg_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(2, "Hofweg", 2),
                                0,
                                1));
        final RouteSegmentWithEquality hofweg_1_0 = hofweg_0_1.reverse();
        final RouteSegmentWithEquality kreuzlingerWeg_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(3, "Kreuzlinger Weg", 2),
                                0,
                                1));
        final RouteSegmentWithEquality kreuzlingerWeg_1_0 = kreuzlingerWeg_0_1.reverse();
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                new IConnectedRouteSegmentsProvider() {

                    private final Set<RouteSegmentWithEquality> routeSegmentsStartingInTheMiddle =
                            ImmutableSet
                                    .<RouteSegmentWithEquality>builder()
                                    .add(hofweg_0_1)
                                    .add(kreuzlingerWeg_0_1)
                                    .add(kingersheimerStrasse_1_0)
                                    .build();

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        } else if (routeSegment.equals(hofweg_0_1)) {
                            return ImmutableSet.of(hofweg_1_0);
                        } else if (routeSegment.equals(hofweg_1_0)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kreuzlingerWeg_0_1)) {
                            return ImmutableSet.of(kreuzlingerWeg_1_0);
                        } else if (routeSegment.equals(kreuzlingerWeg_1_0)) {
                            return routeSegmentsStartingInTheMiddle;
                        }
                        throw new IllegalStateException();
                    }

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        } else if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(hofweg_0_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(hofweg_1_0)) {
                            return ImmutableSet.of(hofweg_1_0);
                        } else if (routeSegment.equals(kreuzlingerWeg_0_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kreuzlingerWeg_1_0)) {
                            return ImmutableSet.of(kreuzlingerWeg_1_0);
                        }
                        throw new IllegalStateException();
                    }
                };

        final RoadPositionEquivalenceRelationProvider roadPositionEquivalenceRelationProvider =
                new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider);

        // When
        final Set<EquivalentRoadPositions> equivalenceRelation =
                roadPositionEquivalenceRelationProvider.getRoadPositionEquivalenceRelation(
                        kingersheimerStrasse_0_1);

        // Then
        assertThat(
                equivalenceRelation,
                is(
                        ImmutableSet.of(
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(
                                                getEndRoadPosition(kingersheimerStrasse_0_1.delegate),
                                                getStartRoadPosition(kreuzlingerWeg_0_1.delegate),
                                                getStartRoadPosition(hofweg_0_1.delegate))),
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(getEndRoadPosition(kreuzlingerWeg_0_1.delegate))),
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(getEndRoadPosition(hofweg_0_1.delegate))),
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(getStartRoadPosition(kingersheimerStrasse_0_1.delegate))))));
    }

    @Test
    public void test_getRoadPositionEquivalenceRelation() {
        // Given
        // 2--kingersheimerStrasse--1--> <--1--kingersheimerStrasse--0
        //                              ^
        //                              1
        //                              |
        //                        Kreuzlinger Weg
        //                              |
        //                              v
        //                              0
        final RouteSegmentWithEquality kingersheimerStrasse_0_1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 2),
                                0,
                                1));
        final RouteSegmentWithEquality kingersheimerStrasse_1_0 = kingersheimerStrasse_0_1.reverse();
        final RouteSegmentWithEquality kingersheimerStrasse_1_2 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 2),
                                1,
                                2));
        final RouteSegmentWithEquality kingersheimerStrasse_2_1 = kingersheimerStrasse_1_2.reverse();
        final RouteSegmentWithEquality kreuzlingerWeg_1_0 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(2, "Kreuzlinger Weg", 2),
                                1,
                                0));
        final RouteSegmentWithEquality kreuzlingerWeg_0_1 = kreuzlingerWeg_1_0.reverse();
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                new IConnectedRouteSegmentsProvider() {

                    private final Set<RouteSegmentWithEquality> routeSegmentsStartingInTheMiddle =
                            ImmutableSet
                                    .<RouteSegmentWithEquality>builder()
                                    .add(kingersheimerStrasse_1_0)
                                    .add(kingersheimerStrasse_1_2)
                                    .add(kreuzlingerWeg_1_0)
                                    .build();

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        } else if (routeSegment.equals(kingersheimerStrasse_1_2)) {
                            return ImmutableSet.of(kingersheimerStrasse_2_1);
                        } else if (routeSegment.equals(kingersheimerStrasse_2_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kreuzlingerWeg_0_1)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kreuzlingerWeg_1_0)) {
                            return ImmutableSet.of(kreuzlingerWeg_0_1);
                        }
                        throw new IllegalStateException();
                    }

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                        if (routeSegment.equals(kingersheimerStrasse_0_1)) {
                            return ImmutableSet.of(kingersheimerStrasse_0_1);
                        } else if (routeSegment.equals(kingersheimerStrasse_1_0)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kingersheimerStrasse_1_2)) {
                            return routeSegmentsStartingInTheMiddle;
                        } else if (routeSegment.equals(kingersheimerStrasse_2_1)) {
                            return ImmutableSet.of(kingersheimerStrasse_2_1);
                        } else if (routeSegment.equals(kreuzlingerWeg_0_1)) {
                            return ImmutableSet.of(kreuzlingerWeg_0_1);
                        } else if (routeSegment.equals(kreuzlingerWeg_1_0)) {
                            return routeSegmentsStartingInTheMiddle;
                        }
                        throw new IllegalStateException();
                    }
                };

        final RoadPositionEquivalenceRelationProvider roadPositionEquivalenceRelationProvider =
                new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider);

        // When
        final Set<EquivalentRoadPositions> equivalenceRelation =
                roadPositionEquivalenceRelationProvider.getRoadPositionEquivalenceRelation(
                        kingersheimerStrasse_0_1);

        // Then
        assertThat(
                equivalenceRelation,
                is(
                        ImmutableSet.of(
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(
                                                getEndRoadPosition(kingersheimerStrasse_0_1.delegate),
                                                getStartRoadPosition(kreuzlingerWeg_1_0.delegate),
                                                getStartRoadPosition(kingersheimerStrasse_1_2.delegate))),
                                new EquivalentRoadPositions(ImmutableSet.of(getStartRoadPosition(kingersheimerStrasse_2_1.delegate))),
                                new EquivalentRoadPositions(ImmutableSet.of(getStartRoadPosition(kingersheimerStrasse_0_1.delegate))),
                                new EquivalentRoadPositions(ImmutableSet.of(getStartRoadPosition(kreuzlingerWeg_0_1.delegate))))));
        ;
    }

    private static RoadPosition getStartRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart());
    }

    private static RoadPosition getEndRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd());
    }
}
