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

import java.util.Collections;
import java.util.Set;

public class RoadPositionEquivalenceRelationProviderTest {

    @Test
    public void test_getRoadPositionEquivalenceRelation() {
        // Given
        final RouteSegment kingersheimerStrasse_0_1 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße", 20),
                        0,
                        1);
        final RouteSegment kingersheimerStrasse_1_2 =
                new RouteSegment(
                        createRouteDataObject(1, "Kingersheimer Straße", 20),
                        1,
                        2);
        final RouteSegment kreuzlingerWeg_12_11 =
                new RouteSegment(
                        createRouteDataObject(2, "Kreuzlinger Weg", 20),
                        12,
                        11);
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider =
                new IConnectedRouteSegmentsProvider() {

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                        return routeSegment.equals(new RouteSegmentWithEquality(kingersheimerStrasse_0_1)) ?
                                ImmutableSet
                                        .<RouteSegmentWithEquality>builder()
                                        .add(new RouteSegmentWithEquality(kingersheimerStrasse_1_2))
                                        .add(new RouteSegmentWithEquality(kreuzlingerWeg_12_11))
                                        .build() :
                                Collections.emptySet();
                    }

                    @Override
                    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                        return Collections.singleton(routeSegment);
                    }
                };

        final RoadPositionEquivalenceRelationProvider roadPositionEquivalenceRelationProvider = new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider);

        // When
        final Set<EquivalentRoadPositions> equivalenceRelation = roadPositionEquivalenceRelationProvider.getRoadPositionEquivalenceRelation(new RouteSegmentWithEquality(kingersheimerStrasse_0_1));

        // Then
        assertThat(
                equivalenceRelation,
                is(
                        ImmutableSet.of(
                                new EquivalentRoadPositions(
                                        ImmutableSet.of(
                                                getEndRoadPosition(kingersheimerStrasse_0_1),
                                                getStartRoadPosition(kreuzlingerWeg_12_11))))));
    }

    private static RoadPosition getStartRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart());
    }

    private static RoadPosition getEndRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd());
    }
}
