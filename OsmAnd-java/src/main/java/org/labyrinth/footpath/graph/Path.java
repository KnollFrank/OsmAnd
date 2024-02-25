package org.labyrinth.footpath.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.labyrinth.common.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Path {

    public final EdgePosition src;
    public final List<Node> intermediateNodes;
    public final EdgePosition dst;

    private List<Edge> edges = null;
    private Quantity<Length> length = null;

    Path(final EdgePosition src, final List<Node> intermediateNodes, final EdgePosition dst) {
        this.src = src;
        this.intermediateNodes = intermediateNodes;
        this.dst = dst;
    }

    public List<Edge> getEdges() {
        if (edges == null) {
            edges = Utils
                    .getConsecutivePairs(getNodes())
                    .map(sourceTargetPair ->
                            new Edge(
                                    sourceTargetPair.getFirst(),
                                    sourceTargetPair.getSecond(),
                                    Collections.emptyList()))
                    .collect(Collectors.toList());
        }
        return edges;
    }

    public Quantity<Length> getLength() {
        if (length == null) {
            length = Edges.getLength(getEdges());
        }
        return length;
    }

    public List<Node> getNodes() {
        return ImmutableList
                .<Node>builder()
                .add(createNode(src, new EquivalentRoadPositions(ImmutableSet.of(new RoadPosition(-1, 0)))))
                .addAll(intermediateNodes)
                .add(createNode(dst, new EquivalentRoadPositions(ImmutableSet.of(new RoadPosition(-1, 1)))))
                .build();
    }

    private Node createNode(final EdgePosition edgePosition, final EquivalentRoadPositions newid) {
        return edgePosition
                .getNode()
                .orElseGet(() -> new Node(newid, edgePosition.getGeodetic()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Path path = (Path) o;
        return src.equals(path.src) &&
                intermediateNodes.equals(path.intermediateNodes) &&
                dst.equals(path.dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, intermediateNodes, dst);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Path.class.getSimpleName() + "[", "]")
                .add("src=" + src)
                .add("intermediateNodes=" + intermediateNodes)
                .add("dst=" + dst)
                .toString();
    }
}
