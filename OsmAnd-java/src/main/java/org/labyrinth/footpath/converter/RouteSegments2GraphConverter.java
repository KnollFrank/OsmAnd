package org.labyrinth.footpath.converter;

import static net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;

import org.labyrinth.footpath.graph.Graph;

import java.util.Collections;
import java.util.Set;

public class RouteSegments2GraphConverter {

    public Graph routeSegments2Graph(final Set<RouteSegmentWrapper> routeSegments) {
        // final Set<Edge> footpathEdges = getFootpathEdges(osm, getFootpathNodes(osm));
        // return new Graph(getNodes(footpathEdges), footpathEdges);
        return new Graph(Collections.emptySet(), Collections.emptySet());
    }

//    private Set<Node> getNodes(final Set<Edge> edges) {
//        return edges
//                .stream()
//                .flatMap(edge -> Stream.of(edge.getSource(), edge.getTarget()))
//                .collect(Collectors.toSet());
//    }
//
//    private Map<Long, Node> getFootpathNodes(final InMemoryMapDataSet osm) {
//        return osm
//                .getNodes()
//                .valueCollection()
//                .stream()
//                .map(this::osmNode2FootpathNode)
//                .collect(Collectors.toMap(Node::getId, Function.identity()));
//    }
//
//    private Node osmNode2FootpathNode(final OsmNode osmNode) {
//        return new NodeBuilder()
//                .withId(osmNode.getId())
//                .withLat(new Angle(osmNode.getLatitude(), Unit.DEGREES))
//                .withLon(new Angle(osmNode.getLongitude(), Unit.DEGREES))
//                .withName(OsmModelUtil.getTagsAsMap(osmNode).get("name"))
//                .createNode();
//    }
//
//    private Set<Edge> getFootpathEdges(final InMemoryMapDataSet osm, final Map<Long, Node> footpathNodesById) {
//        return getHighways(osm)
//                .stream()
//                .flatMap(osmWay -> getWayEdges(osmWay, footpathNodesById))
//                .collect(Collectors.toSet());
//    }
//
//    static Set<OsmWay> getHighways(final InMemoryMapDataSet data) {
//        return data
//                .getWays()
//                .valueCollection()
//                .stream()
//                .filter(way -> OsmModelUtil.getTagsAsMap(way).containsKey(HIGHWAY))
//                .collect(Collectors.toSet());
//    }
//
//    private Stream<Edge> getWayEdges(final OsmWay osmWay, final Map<Long, Node> footpathNodesById) {
//        return Utils
//                .getConsecutivePairs(0, osmWay.getNumberOfNodes() - 1)
//                .map(sourceWayPointIdAndTargetWayPointId ->
//                        getFootpathEdge(
//                                footpathNodesById,
//                                osmWay,
//                                sourceWayPointIdAndTargetWayPointId.first,
//                                sourceWayPointIdAndTargetWayPointId.second));
//    }
//
//    private Edge getFootpathEdge(final Map<Long, Node> footpathNodesById,
//                                 final OsmWay osmWay,
//                                 final int sourceWayPointId,
//                                 final int targetWayPointId) {
//        return new Edge(
//                footpathNodesById.get(osmWay.getNodeId(sourceWayPointId)),
//                footpathNodesById.get(osmWay.getNodeId(targetWayPointId)));
//    }
}
