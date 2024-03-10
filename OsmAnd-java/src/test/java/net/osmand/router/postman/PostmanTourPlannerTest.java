package net.osmand.router.postman;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.data.LatLon;
import net.osmand.router.RoutePlannerFrontEnd;
import net.osmand.router.RouteResultPreparation;
import net.osmand.router.RouteResultPreparation.RouteCalcResult;
import net.osmand.router.RouteSegmentResult;
import net.osmand.router.RoutingConfiguration;
import net.osmand.router.RoutingConfiguration.RoutingMemoryLimits;
import net.osmand.router.RoutingContext;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PostmanTourPlannerTest {

    @BeforeClass
    public static void setUp() {
        RouteResultPreparation.PRINT_TO_CONSOLE_ROUTE_INFORMATION_TO_TEST = true;
        // BinaryRoutePlanner.TRACE_ROUTING = true;
    }

    @Test
    public void testRoutingLabyrinth() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Labyrinth.obf");
        final LatLon entrance = new LatLon(49.4460780, 10.3188074);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                entrance,
                                new LatLon(49.4459823, 10.3178143),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        Assert.assertEquals(getStartOfRoute(routeSegmentResults), entrance);

        {
            final Set<LatLon> latLons = getLatLons(routeSegmentResults);
            assertThat(latLons, hasItem(entrance));
            final LatLon exit = new LatLon(49.4459827, 10.3178140);
            assertThat(latLons, hasItem(exit));
        }
    }

    @Test
    public void testRoutingHirschau() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/Hirschau.obf");
        final LatLon hofweg = new LatLon(48.5017172, 8.9933938);
        final LatLon kapellenweg = new LatLon(48.501619, 8.9929844);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                hofweg,
                                kapellenweg,
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        assertThat(getStartOfRoute(routeSegmentResults), is(hofweg));
        {
            final Set<LatLon> latLons = getLatLons(routeSegmentResults);
            assertThat(latLons, hasItem(hofweg));
            assertThat(latLons, hasItem(kapellenweg));
        }
    }

    @Test
    public void testRoutingHirschauKapellenweg() throws Exception {
        // Given
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/HirschauKapellenweg.obf");
        final LatLon south = new LatLon(48.50146813118, 8.99308606848);

        // When
        final RouteCalcResult routeCalcResult =
                new RoutePlannerFrontEnd()
                        .searchRoute(
                                routingContext,
                                south,
                                new LatLon(48.501619, 8.9929844),
                                Collections.emptyList());

        // Then
        final List<RouteSegmentResult> routeSegmentResults = routeCalcResult.getList();
        assertThat(getStartOfRoute(routeSegmentResults), is(south));
        assertThat(
                RouteSegmentResultWithEqualityFactory.getRouteSegmentResultWithEqualities(routeSegmentResults),
                hasItems(
                        new RouteSegmentResultWithEquality(-688, null, 2, 0),
                        new RouteSegmentResultWithEquality(-688, null, 0, 2)));
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
                RouteSegmentResultWithEqualityFactory.getRouteSegmentResultWithEqualities(routeSegmentResults));
    }

    private static LatLon getStartOfRoute(final List<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults.get(0).getStartPoint();
    }

    static RoutingContext createRoutingContext(final String obfFileName) throws IOException {
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

    private static Set<LatLon> getLatLons(final Collection<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults
                .stream()
                .flatMap(
                        routeSegmentResult ->
                                IntStream
                                        .range(0, routeSegmentResult.getObject().getPointsLength())
                                        .mapToObj(routeSegmentResult::getPoint))
                .collect(Collectors.toSet());
    }
}