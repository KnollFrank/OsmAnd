package org.labyrinth.footpath.graph;

import org.labyrinth.common.ListUtils;
import org.labyrinth.common.MeasureUtils;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.labyrinth.common.MeasureUtils.abs;
import static org.labyrinth.common.MeasureUtils.divide;

public class PathPositionFactory {

    public static PathPosition edgePosition2NearestPathPosition(
            final EdgePosition edgePosition,
            final PathPosition pathPosition) {
        return PathPositionFactory
                .getAlignedEdgePositionsWithEdges(edgePosition, pathPosition.path.getEdges())
                .map(alignedEdgePosition ->
                        new PathPosition(
                                pathPosition.path,
                                divide(
                                        getCoveredPathLength(pathPosition, alignedEdgePosition),
                                        pathPosition.path.getLength())))
                .min((pathPosition1, pathPosition2) ->
                        compareLen1AndLen2Relative2LenRef(
                                pathPosition1.getCoveredPathLength(),
                                pathPosition2.getCoveredPathLength(),
                                pathPosition.getCoveredPathLength()))
                .orElseThrow(IllegalArgumentException::new);
    }

    private static Stream<EdgePosition> getAlignedEdgePositionsWithEdges(final EdgePosition edgePosition, final List<Edge> edges) {
        return Stream
                .of(edgePosition, edgePosition.reverseRepresentation())
                .filter(isAlignedWith(edges));
    }

    private static Predicate<EdgePosition> isAlignedWith(final List<Edge> edges) {
        return edgePosition -> edges.contains(edgePosition.edge);
    }

    private static Quantity<Length> getCoveredPathLength(final PathPosition pathPosition, final EdgePosition edgePosition) {
        return PathPositionFactory
                .getCoveredPathLengths(pathPosition.path, edgePosition)
                .min((len1, len2) -> compareLen1AndLen2Relative2LenRef(len1, len2, pathPosition.getCoveredPathLength()))
                .get();
    }

    private static Stream<Quantity<Length>> getCoveredPathLengths(final Path path, final EdgePosition edgePosition) {
        return PathPositionFactory
                .getLensOfEdgesUpToEdge(path.getEdges(), edgePosition.edge)
                .map(lenOfEdgesUpToEdge -> lenOfEdgesUpToEdge.add(edgePosition.getCoveredLength()));
    }

    private static Stream<Quantity<Length>> getLensOfEdgesUpToEdge(final List<Edge> edges, final Edge edge) {
        return IntStream
                .of(ListUtils.indexesOf(edges, edge))
                .mapToObj(edgeIndex -> edges.subList(0, edgeIndex))
                .map(Edges::getLength);
    }

    private static int compareLen1AndLen2Relative2LenRef(final Quantity<Length> len1, final Quantity<Length> len2, final Quantity<Length> lenRef) {
        return MeasureUtils.COMPARATOR.compare(diff(len1, lenRef), diff(len2, lenRef));
    }

    private static Quantity<Length> diff(final Quantity<Length> len1, final Quantity<Length> len2) {
        return abs(len1.subtract(len2));
    }
}
