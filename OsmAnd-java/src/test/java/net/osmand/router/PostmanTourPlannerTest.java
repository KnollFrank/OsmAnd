package net.osmand.router;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

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
        final LatLon start = new LatLon(49.4460708, 10.3188208);

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
                        new RouteSegmentResultWithEquality(-102033, null, 0, 1),
                        new RouteSegmentResultWithEquality(-102033, null, 1, 0),
                        new RouteSegmentResultWithEquality(-101874, null, 2, 1),
                        new RouteSegmentResultWithEquality(-101874, null, 1, 5),
                        new RouteSegmentResultWithEquality(-101884, null, 0, 1),
                        new RouteSegmentResultWithEquality(-101884, null, 1, 0),
                        new RouteSegmentResultWithEquality(-101874, null, 5, 8),
                        new RouteSegmentResultWithEquality(-101886, null, 0, 6)),
                getRouteSegmentResultWithEqualities(routeSegmentResults.subList(0, 8)));
    }

    @Test
    public void testRoutingHirschau() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Hirschau.obf");
        // Kapellenweg:
        final LatLon start = new LatLon(48.5017172, 8.9933938);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                start,
                                // Hofweg:
                                new LatLon(48.5017172, 8.9933938),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        assertThat(getStartOfRoute(routeSegmentResults), is(start));
        assertThat(
                getRouteSegmentResultWithEqualities(routeSegmentResults),
                hasItems(
                        new RouteSegmentResultWithEquality(305275763L, "Hofweg", 6, 7),
                        new RouteSegmentResultWithEquality(66633789123L, "Hofweg", 0, 2),
                        new RouteSegmentResultWithEquality(12544617511L, "Kingersheimer Straße", 6, 7),
                        new RouteSegmentResultWithEquality(67343327585L, "Kirchplatz", 0, 5),
                        new RouteSegmentResultWithEquality(43742393661L, null, 1, 0),
                        new RouteSegmentResultWithEquality(43742393661L, null, 0, 1),
                        new RouteSegmentResultWithEquality(67343327585L, "Kirchplatz", 5, 7),
                        new RouteSegmentResultWithEquality(305273305L, "Kirchplatz", 0, 8),
                        new RouteSegmentResultWithEquality(58331930311L, null, 0, 1),
                        new RouteSegmentResultWithEquality(58331930311L, null, 1, 0),
                        new RouteSegmentResultWithEquality(305273305L, "Kirchplatz", 8, 10),
                        new RouteSegmentResultWithEquality(70755014031L, null, 1, 0)));
    }

    @Test
    public void testRoutingT_junction() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/T_junction.obf");
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

    private static LatLon getStartOfRoute(final List<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults.get(0).getStartPoint();
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

    private static BinaryMapIndexReader createBinaryMapIndexReader(final String fileName) throws IOException {
        return new BinaryMapIndexReader(
                new RandomAccessFile(fileName, "r"),
                new File(fileName));
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