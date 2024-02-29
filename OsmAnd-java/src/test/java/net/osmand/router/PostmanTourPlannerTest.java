package net.osmand.router;

import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.data.LatLon;
import net.osmand.router.RouteResultPreparation.RouteCalcResult;
import net.osmand.router.RoutingConfiguration.RoutingMemoryLimits;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostmanTourPlannerTest {

    @BeforeClass
    public static void setUp() {
        RouteResultPreparation.PRINT_TO_CONSOLE_ROUTE_INFORMATION_TO_TEST = true;
        BinaryRoutePlanner.TRACE_ROUTING = true;
    }

    @Test
    public void testRoutingLabyrinth() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Labyrinth.obf");
        final LatLon start = new LatLon(49.4460638, 10.3180879);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                start,
                                new LatLon(49.4459823, 10.3178143),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        Assert.assertEquals(getStartOfRoute(routeSegmentResults), start);
        Assert.assertEquals(
                Arrays.asList(
                        new RouteSegmentResultWithEquality(-101963, null, 2, 4),
                        new RouteSegmentResultWithEquality(-101967, null, 1, 2),
                        new RouteSegmentResultWithEquality(-101967, null, 2, 1),
                        new RouteSegmentResultWithEquality(-101963, null, 4, 2)),
                getRouteSegmentResultWithEqualities(routeSegmentResults));
    }

    @Test
    public void testRoutingHirschau() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Hirschau.obf");
        final LatLon start = new LatLon(48.501619, 8.9929844);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                // Kapellenweg:
                                start,
                                // Hofweg:
                                new LatLon(48.5017172, 8.9933938),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        Assert.assertEquals(getStartOfRoute(routeSegmentResults), start);
        Assert.assertEquals(
                Arrays.asList(
                        new RouteSegmentResultWithEquality(22432831547L, "Kapellenweg", 10, 11),
                        new RouteSegmentResultWithEquality(73829969825L, null, 1, 0),
                        new RouteSegmentResultWithEquality(73829969825L, null, 0, 1),
                        new RouteSegmentResultWithEquality(22432831547L, "Kapellenweg", 11, 15),
                        new RouteSegmentResultWithEquality(22432831547L, "Kapellenweg", 15, 10)),
                getRouteSegmentResultWithEqualities(routeSegmentResults));
    }

    // FK-TODO: TwoPoints.osm umbenennen in T_junction.osm und in JOSM zu einem T-Buchstaben ähnlicher malen.
    @Test
    public void testRoutingTwoPoints() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/TwoPoints.obf");
        final LatLon start = new LatLon(43.0257384, 9.4062576);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                start,
                                new LatLon(43.0258502, 9.4061449),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        Assert.assertEquals(getStartOfRoute(routeSegmentResults), start);
        Assert.assertEquals(
                Arrays.asList(
                        new RouteSegmentResultWithEquality(-1347, "langer Weg", 0, 2),
                        new RouteSegmentResultWithEquality(-1347, "langer Weg", 2, 1),
                        new RouteSegmentResultWithEquality(-1348, "kurzer Weg", 0, 1),
                        new RouteSegmentResultWithEquality(-1348, "kurzer Weg", 1, 0),
                        new RouteSegmentResultWithEquality(-1347, "langer Weg", 1, 0)),
                getRouteSegmentResultWithEqualities(routeSegmentResults));
    }

    private static LatLon getStartOfRoute(
            final List<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults.get(0).getStartPoint();
    }

    private static BinaryMapIndexReader createBinaryMapIndexReader(
            final String fileName) throws
            IOException {
        return new BinaryMapIndexReader(
                new RandomAccessFile(fileName, "r"),
                new File(fileName));
    }

    private static RoutingContext createRoutingContext(final String obfFileName) throws
            IOException {
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

    private static void print(final List<RouteSegmentResult> routeSegmentResults) {
        for (int i = 0; i < routeSegmentResults.size(); i++) {
            final RouteSegmentResult routeSegmentResult = routeSegmentResults.get(i);
            System.out.println(i + ": " + routeSegmentResult);
            System.out.println("   description: " + routeSegmentResult.getDescription(true));
        }
    }

    private static List<RouteSegmentResultWithEquality> getRouteSegmentResultWithEqualities(final List<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults
                .stream()
                .map(routeSegmentResult ->
                        new RouteSegmentResultWithEquality(
                                routeSegmentResult.getObject().id,
                                routeSegmentResult.getObject().getName(),
                                routeSegmentResult.getStartPointIndex(),
                                routeSegmentResult.getEndPointIndex()))
                .collect(Collectors.toList());
    }
}