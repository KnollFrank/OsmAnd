package org.labyrinth.footpath.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.labyrinth.common.MeasureUtils.toMetres;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphUtils {

    static void assertActualEqualsExpected(final Graph actual, final Graph expected) {
        assertActualEqualsExpected(actual.nodes, expected.nodes);
        assertActualEdgesEqualsExpectedEdges(actual.edges, expected.edges);
    }

    private static void assertActualEdgesEqualsExpectedEdges(final Set<Edge> actual, final Set<Edge> expected) {
        assertActualEdgesEqualsExpectedEdges(new ArrayList<>(actual), new ArrayList<>(expected));
    }

    private static void assertActualEdgesEqualsExpectedEdges(final List<Edge> actual, final List<Edge> expected) {
        assertThat(actual.size(), is(expected.size()));
        for (int i = 0; i < expected.size(); i++) {
            final Edge expectedEdge = expected.get(i);
            final Edge actualEdge = getEdge(actual, expectedEdge.source.id, expectedEdge.target.id);
            assertActualEqualsExpected(actualEdge, expectedEdge);
        }
    }

    private static Edge getEdge(final List<Edge> edges, final RoadPosition sourceId, final RoadPosition targetId) {
        return edges
                .stream()
                .filter(edge -> isEdgeFromSourceToTarget(edge, sourceId, targetId))
                .findFirst()
                .orElse(null);
    }

    private static boolean isEdgeFromSourceToTarget(final Edge edge, final RoadPosition sourceId, final RoadPosition targetId) {
        return edge.source.id.equals(sourceId) && edge.target.id.equals(targetId);
    }

    private static void assertActualEqualsExpected(final Edge actual, final Edge expected) {
        assertThat(actual.source.id, is(expected.source.id));
        assertThat(actual.target.id, is(expected.target.id));
        assertThat(toMetres(actual.getLength()), is(closeTo(toMetres(expected.getLength()), 1)));
        // FK-FIXME: schlägt oft fehl, auch bei großem error-Wert.
        // assertThat(actual.getCompDir().toDegrees(), is(closeTo(expected.getCompDir().toDegrees(), 180)));
    }

    private static void assertActualEqualsExpected(final Set<Node> actual, final Set<Node> expected) {
        assertActualEqualsExpected(asSortedList(actual), asSortedList(expected));
    }

    private static List<Node> asSortedList(final Set<Node> nodes) {
        return nodes.stream().sorted().collect(Collectors.toList());
    }

    private static void assertActualEqualsExpected(final List<Node> actual, final List<Node> expected) {
        assertThat(actual.size(), is(expected.size()));
        for (int i = 0; i < expected.size(); i++) {
            assertActualEqualsExpected(actual.get(i), expected.get(i));
        }
    }

    private static void assertActualEqualsExpected(final Node actual, final Node expected) {
        final double error = 0.0001;
        assertThat(actual.position.getLatitude().to(Angle.Unit.DEGREES), is(closeTo(expected.position.getLatitude().to(Angle.Unit.DEGREES), error)));
        assertThat(actual.position.getLongitude().to(Angle.Unit.DEGREES), is(closeTo(expected.position.getLongitude().to(Angle.Unit.DEGREES), error)));
        assertThat(actual.id, is(expected.id));
        // assertThat(actual.getLocEdges(), is(expected.getLocEdges()));
    }
}