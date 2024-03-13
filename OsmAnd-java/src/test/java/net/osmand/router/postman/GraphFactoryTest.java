package net.osmand.router.postman;

import static net.osmand.router.postman.PostmanTourPlannerTest.createRoutingContext;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.KILO;
import static tec.units.ri.unit.Units.METRE;

import com.google.common.collect.ImmutableSet;

import net.osmand.data.LatLon;
import net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;
import net.osmand.router.RoutePlannerFrontEnd;
import net.osmand.router.RouteResultPreparation;
import net.osmand.router.RoutingContext;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.BeforeClass;
import org.junit.Test;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.footpath.converter.Graph2JGraphConverter;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class GraphFactoryTest {

    @BeforeClass
    public static void setUp() {
        RouteResultPreparation.PRINT_TO_CONSOLE_ROUTE_INFORMATION_TO_TEST = true;
    }

    @Test
    public void test_getGraphAndStartNode_T_junction2() throws Exception {
        // Given
        // node2--B--node1--A--node0
        //             |
        //             C
        //             |
        //           node3
        final LatLon start = new LatLon(49.44617713901, 10.31896367602);
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/T_junction2.obf");
        final RouteSegmentPoint startSegment =
                new RoutePlannerFrontEnd().findRouteSegment(
                        start.getLatitude(),
                        start.getLongitude(),
                        routingContext,
                        null,
                        false);

        // When
        final Pair<Graph, Node> graphAndStartNode =
                GraphFactory.getGraphAndStartNode(
                        routingContext,
                        startSegment,
                        getQuantity(0.25, KILO(METRE)));

        // Then
        final Graph graph = graphAndStartNode.getFirst();
        final Node startNode = graphAndStartNode.getSecond();
        assertThat(getTallestConnectedSet(graph).contains(startNode), is(true));
        final Node node0 =
                new Node(
                        new EquivalentRoadPositions(
                                ImmutableSet.of(
                                        new RoadPosition(-1360, 0))),
                        GeodeticFactory.createGeodetic(start));
        final Node node1 =
                new Node(
                        new EquivalentRoadPositions(
                                ImmutableSet.of(
                                        new RoadPosition(-1360, 1),
                                        new RoadPosition(-1362, 0),
                                        new RoadPosition(-1386, 0))),
                        GeodeticFactory.createGeodetic(new LatLon(49.44617844691, 10.31871356003)));
        final Node node2 =
                new Node(
                        new EquivalentRoadPositions(
                                ImmutableSet.of(
                                        new RoadPosition(-1386, 1))),
                        GeodeticFactory.createGeodetic(new LatLon(49.44617888288, 10.31844735079)));
        final Node node3 =
                new Node(
                        new EquivalentRoadPositions(
                                ImmutableSet.of(
                                        new RoadPosition(-1362, 1))),
                        GeodeticFactory.createGeodetic(new LatLon(49.44592689294, 10.31871087782)));
        assertThat(startNode, is(node0));
        final Graph graphExpected =
                org.labyrinth.footpath.graph.GraphFactory.createGraph(
                        ImmutableSet.of(
                                // A:
                                new Edge(node0, node1, null),
                                new Edge(node1, node0, null),
                                // B:
                                new Edge(node1, node2, null),
                                new Edge(node2, node1, null),
                                // C:
                                new Edge(node1, node3, null),
                                new Edge(node3, node1, null)));
        assertThat(graph, is(graphExpected));
    }

    @Test
    public void test_getGraphAndStartNode_T_junction() throws Exception {
        // Given
        // node3---node1---node2
        //           |
        //           |
        //         node0
        final LatLon start = new LatLon(43.0257384, 9.4062576);
        final RoutingContext routingContext = createRoutingContext("src/test/resources/routing/T_junction.obf");
        final RouteSegmentPoint startSegment =
                new RoutePlannerFrontEnd().findRouteSegment(
                        start.getLatitude(),
                        start.getLongitude(),
                        routingContext,
                        null,
                        false);

        // When
        final Pair<Graph, Node> graphAndStartNode =
                GraphFactory.getGraphAndStartNode(
                        routingContext,
                        startSegment,
                        getQuantity(0.25, KILO(METRE)));

        // Then
        final Graph graph = graphAndStartNode.getFirst();
        final Node startNode = graphAndStartNode.getSecond();
        assertThat(getTallestConnectedSet(graph).contains(startNode), is(true));
        assertThat(startNode, is(node2()));
        final Graph graphExpected =
                org.labyrinth.footpath.graph.GraphFactory.createGraph(
                        ImmutableSet.of(
                                new Edge(node0(), node1(), null),
                                new Edge(node2(), node1(), null),
                                new Edge(node3(), node1(), null),
                                new Edge(node1(), node0(), null),
                                new Edge(node1(), node2(), null),
                                new Edge(node1(), node3(), null)));
        assertThat(graph, is(graphExpected));
    }

    private static Node node0() {
        return new Node(
                new EquivalentRoadPositions(
                        ImmutableSet.of(
                                new RoadPosition(-1348, 1))),
                GeodeticFactory.createGeodetic(new LatLon(43.025719777334494, 9.405868649482727)));
    }

    private static Node node1() {
        return new Node(
                new EquivalentRoadPositions(
                        ImmutableSet.of(
                                new RoadPosition(-1347, 1),
                                new RoadPosition(-1348, 0))),
                GeodeticFactory.createGeodetic(new LatLon(43.02591193760422, 9.406225383281708)));
    }

    private static Node node2() {
        return new Node(
                new EquivalentRoadPositions(
                        ImmutableSet.of(
                                new RoadPosition(-1347, 0))),
                GeodeticFactory.createGeodetic(new LatLon(43.02573938555285, 9.406254887580872)));
    }

    private static Node node3() {
        return new Node(
                new EquivalentRoadPositions(
                        ImmutableSet.of(
                                new RoadPosition(-1347, 2))),
                GeodeticFactory.createGeodetic(new LatLon(43.02598056612619, 9.406008124351501)));
    }

    private static Set<Node> getTallestConnectedSet(final Graph graph) {
        final ConnectivityInspector<Node, DefaultWeightedEdge> connectivityInspector =
                new ConnectivityInspector<>(Graph2JGraphConverter.convert(graph));
        return getTallestSet(connectivityInspector.connectedSets());
    }

    private static Set<Node> getTallestSet(final List<Set<Node>> sets) {
        return sets
                .stream()
                .max(Comparator.comparingInt(Set::size))
                .get();
    }
}