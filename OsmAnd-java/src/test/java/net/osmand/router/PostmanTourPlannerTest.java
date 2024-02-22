package net.osmand.router;

import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.data.LatLon;
import net.osmand.router.RoutingConfiguration.RoutingMemoryLimits;

import org.apache.commons.compress.utils.Sets;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PostmanTourPlannerTest {

    @BeforeClass
    public static void setUp() {
        RouteResultPreparation.PRINT_TO_CONSOLE_ROUTE_INFORMATION_TO_TEST = true;
        BinaryRoutePlanner.TRACE_ROUTING = true;
    }

    @Test
    @Ignore
    public void testRouting() throws Exception {
        // BinaryRoutePlanner.TRACE_ROUTING = true;
        // BinaryRoutePlanner.DEBUG_BREAK_EACH_SEGMENT = true;
        // BinaryRoutePlanner.DEBUG_PRECISE_DIST_MEASUREMENT = true;
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Labyrinth.obf");

        // When
        final RouteResultPreparation.RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                new LatLon(49.4460638, 10.3180879),
                                new LatLon(49.4459823, 10.3178143),
                                Collections.<LatLon>emptyList());

        // Then
        Assert.assertEquals(
                Sets.newHashSet(-1594L, -1593L),
                getReachedSegments(routeCalcResult.getList()));
    }

    @Test
    @Ignore
    public void testRoutingHirschau() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Hirschau.obf");

        // When
        final RouteResultPreparation.RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                // Kapellenweg:
                                new LatLon(48.501619, 8.9929844),
                                // Hofweg:
                                new LatLon(48.5017172, 8.9933938),
                                Collections.<LatLon>emptyList());

        // Then
        Assert.assertEquals(
                Sets.newHashSet(-1594L, -1593L),
                getReachedSegments(routeCalcResult.getList()));
    }

    private static BinaryMapIndexReader createBinaryMapIndexReader(final String fileName) throws IOException {
        return new BinaryMapIndexReader(
                new RandomAccessFile(fileName, "r"),
                new File(fileName));
    }

    private static RoutingContext createRoutingContext(final String obfFileName) throws IOException {
        final RoutingContext ctx =
                new RoutePlannerFrontEnd()
                        .buildRoutingContext(
                                RoutingConfiguration
                                        .getDefault()
                                        .build(
                                                "pedestrian",
                                                new RoutingMemoryLimits(
                                                        RoutingConfiguration.DEFAULT_MEMORY_LIMIT * 3,
                                                        RoutingConfiguration.DEFAULT_NATIVE_MEMORY_LIMIT),
                                                null),
                                null,
                                new BinaryMapIndexReader[]
                                        {
                                                createBinaryMapIndexReader(obfFileName)
                                        },
                                RoutePlannerFrontEnd.RouteCalculationMode.NORMAL);
        ctx.leftSideNavigation = false;
        return ctx;
    }

    private static Set<Long> getReachedSegments(final List<RouteSegmentResult> routeSegments) {
        final Set<Long> reachedSegments = new TreeSet<>();
        int prevSegment = -1;
        for (int i = 0; i <= routeSegments.size(); i++) {
            if (i == routeSegments.size() || routeSegments.get(i).getTurnType() != null) {
                if (prevSegment >= 0) {
                    final RouteSegmentResult routeSegmentResult = routeSegments.get(prevSegment);
                    System.out.println("segmentId: " + getSegmentId(routeSegmentResult) + " description: " + routeSegmentResult.getDescription(false));
                }
                prevSegment = i;
            }
            if (i < routeSegments.size()) {
                reachedSegments.add(getSegmentId(routeSegments.get(i)));
            }
        }
        return reachedSegments;
    }

    private static long getSegmentId(RouteSegmentResult routeSegmentResult) {
        return routeSegmentResult.getObject().getId() >> RouteResultPreparation.SHIFT_ID;
    }
}