package org.labyrinth.footpath.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.labyrinth.common.SetUtils.union;
import static org.labyrinth.footpath.converter.GraphFactoryTest.createRouteDataObject;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class ConnectedRouteSegmentsWithinAreaProviderTest {

    @Test
    public void test_getConnectedRouteSegmentsWithinArea() {
        // Given
        final RouteSegmentWithEquality withinArea1 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 20),
                                0,
                                1));
        final RouteSegmentWithEquality withinArea2 =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(1, "Kingersheimer Straße", 20),
                                1,
                                2));
        final RouteSegmentWithEquality outsideArea =
                new RouteSegmentWithEquality(
                        new RouteSegment(
                                createRouteDataObject(2, "Kreuzlinger Weg", 20),
                                12,
                                11));
        final IConnectedRouteSegmentsProvider connectedRouteSegmentsWithinAreaProvider =
                new ConnectedRouteSegmentsWithinAreaProvider(
                        new IConnectedRouteSegmentsProvider() {

                            @Override
                            public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
                                return routeSegment.equals(withinArea1) ?
                                        ImmutableSet.of(withinArea2, outsideArea) :
                                        Collections.emptySet();
                            }

                            @Override
                            public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
                                return Collections.singleton(routeSegment);
                            }
                        },
                        routeSegment ->
                                ImmutableSet
                                        .of(withinArea1, withinArea2)
                                        .contains(new RouteSegmentWithEquality(routeSegment)));

        // When
        final Set<RouteSegmentWithEquality> connectedRouteSegments =
                union(
                        connectedRouteSegmentsWithinAreaProvider.getRouteSegmentsStartingAtEndOf(withinArea1),
                        connectedRouteSegmentsWithinAreaProvider.getRouteSegmentsStartingAtStartOf(withinArea1));

        // Then
        assertThat(connectedRouteSegments, is(ImmutableSet.of(withinArea1, withinArea2)));
    }
}
