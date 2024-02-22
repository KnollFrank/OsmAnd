package org.labyrinth.footpath.converter;

import static net.osmand.binary.BinaryMapRouteReaderAdapter.RouteRegion;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;

import org.junit.Test;

import java.util.Set;

public class RouteSegments2GraphConverterTest {

    @Test
    public void name() {
        // Given
        final Set<RouteSegmentWrapper> routeSegments =
                ImmutableSet
                        .<RouteSegmentWrapper>builder()
                        .add(new RouteSegmentWrapper(new RouteSegment(createRouteDataObject(4711), 5, 6)))
                        .build();
        new RouteSegments2GraphConverter();

        // When

        // Then
    }

    private static RouteDataObject createRouteDataObject(final int id) {
        final RouteDataObject routeDataObject = new RouteDataObject((RouteRegion) null);
        routeDataObject.id = id;
        return routeDataObject;
    }
}
