package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;
import net.osmand.util.MapUtils;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class EdgesVisitor implements IConnectedRouteSegmentsVisitor<Set<Edge>> {

    private final Set<EquivalentRoadPositions> equivalenceRelation;

    public EdgesVisitor(final Set<EquivalentRoadPositions> equivalenceRelation) {
        this.equivalenceRelation = equivalenceRelation;
    }

    @Override
    public Set<Edge> processConnectedRouteSegments(final RouteSegmentWithEquality source, final Set<RouteSegmentWithEquality> destinations) {
        return asEdges(destinations);
    }

    @Override
    public Set<Edge> combine(final List<Set<Edge>> sets) {
        return union(sets);
    }

    private Set<Edge> asEdges(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .map(routeSegmentWrapper -> routeSegmentWrapper.delegate)
                .map(this::asEdge)
                .collect(Collectors.toSet());
    }

    private Edge asEdge(final RouteSegment routeSegment) {
        return new Edge(
                getSourceNode(routeSegment),
                getTargetNode(routeSegment),
                Arrays.asList(routeSegment));
    }

    private Node getSourceNode(final RouteSegment routeSegment) {
        return getNode(routeSegment, routeSegment.getSegmentStart());
    }

    private Node getTargetNode(final RouteSegment routeSegment) {
        return getNode(routeSegment, routeSegment.getSegmentEnd());
    }

    private Node getNode(final RouteSegment routeSegment, final short position) {
        return new Node(
                getEquivalentRoadPositions(new RoadPosition(routeSegment.getRoad().id, position), equivalenceRelation),
                getGeodetic(routeSegment, position));
    }

    private static Geodetic getGeodetic(final RouteSegment routeSegment, final short i) {
        return getGeodetic(routeSegment.getRoad(), i);
    }

    private static Geodetic getGeodetic(final RouteDataObject road, final short i) {
        return new Geodetic(getLatitude(road, i), getLongitude(road, i));
    }

    private static Angle getLatitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LatitudeY(road.getPoint31YTile(i)), Angle.Unit.DEGREES);
    }

    private static Angle getLongitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LongitudeX(road.getPoint31XTile(i)), Angle.Unit.DEGREES);
    }

    static EquivalentRoadPositions getEquivalentRoadPositions(final RoadPosition roadPosition, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return equivalenceRelation
                .stream()
                .filter(equivalentRoadPositions -> equivalentRoadPositions.roadPositions.contains(roadPosition))
                .findFirst()
                .orElseGet(() -> new EquivalentRoadPositions(ImmutableSet.of(roadPosition)));
    }
}
