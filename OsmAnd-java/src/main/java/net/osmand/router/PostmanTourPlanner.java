package net.osmand.router;

import static net.osmand.router.BinaryRoutePlanner.FinalRouteSegment;
import static net.osmand.router.BinaryRoutePlanner.MultiFinalRouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;
import static net.osmand.router.RoutePlannerFrontEnd.RouteCalculationMode;

import net.osmand.PlatformUtil;
import net.osmand.binary.RouteDataObject;
import net.osmand.osm.MapRenderingTypes;
import net.osmand.util.MapUtils;

import org.apache.commons.logging.Log;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class PostmanTourPlanner {


    private static final int REVERSE_WAY_RESTRICTION_ONLY = 1024;
    /*private*/ static final int STANDARD_ROAD_IN_QUEUE_OVERHEAD = 220;
    /*private*/ static final int STANDARD_ROAD_VISITED_OVERHEAD = 150;

    protected static final Log log = PlatformUtil.getLog(PostmanTourPlanner.class);

    private static final int ROUTE_POINTS = 11;
    static boolean ASSERT_CHECKS = true;
    static boolean TRACE_ROUTING = false;
    static int TEST_ID = 194349150;
    static boolean TEST_SPECIFIC = false;

    public static boolean DEBUG_PRECISE_DIST_MEASUREMENT = false;
    public static boolean DEBUG_BREAK_EACH_SEGMENT = false;


    private static double squareRootDist(int x1, int y1, int x2, int y2) {
        if (DEBUG_PRECISE_DIST_MEASUREMENT) {
            return MapUtils.measuredDist31(x1, y1, x2, y2);
        }
        return MapUtils.squareRootDist31(x1, y1, x2, y2);
    }


    private static class SegmentsComparator implements Comparator<RouteSegmentCost> {
        @Override
        public int compare(RouteSegmentCost o1, RouteSegmentCost o2) {
            return Double.compare(o1.cost, o2.cost);
        }
    }

    private static float cost(float distanceFromStart, float distanceToEnd, RoutingContext ctx) {
        return ctx.config.heuristicCoefficient * distanceToEnd + distanceFromStart;
    }

    private static class RouteSegmentCost {
        float cost;
        RouteSegment segment;

        public RouteSegmentCost(RouteSegment segment, RoutingContext ctx) {
            this.cost = cost(segment.distanceFromStart, segment.distanceToEnd, ctx);
            this.segment = segment;
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", cost, segment);
        }
    }

    /**
     * Calculate route between start.segmentEnd and end.segmentStart (using A* algorithm)
     * return list of segments
     */
    public FinalRouteSegment searchRouteInternal(
            final RoutingContext ctx,
            RouteSegmentPoint start,
            RouteSegmentPoint end,
            TLongObjectMap<RouteSegment> boundaries) throws InterruptedException {
        // measure time
        ctx.memoryOverhead = 1000;
        // Initializing priority queue to visit way segments
        PriorityQueue<RouteSegmentCost> graphDirectSegments = new PriorityQueue<>(50, new SegmentsComparator());
        PriorityQueue<RouteSegmentCost> graphReverseSegments = new PriorityQueue<>(50, new SegmentsComparator());
        // Set to not visit one segment twice (stores road.id << X + segmentStart)
        TLongObjectHashMap<RouteSegment> visitedDirectSegments = new TLongObjectHashMap<>();
        TLongObjectHashMap<RouteSegment> visitedOppositeSegments = new TLongObjectHashMap<>();
        initQueuesWithStartEnd(ctx, start, end, graphDirectSegments, graphReverseSegments);

        boolean onlyBackward = ctx.getPlanRoadDirection() < 0;
        boolean onlyForward = ctx.getPlanRoadDirection() > 0;
        // Extract & analyze segment with min(f(x)) from queue while final segment is not found
        boolean forwardSearch = !onlyForward;

        FinalRouteSegment finalSegment = null;
        ctx.dijkstraMode = end == null ? 1 : (start == null ? -1 : 0);
        if (ctx.dijkstraMode == 1) {
            start.others = null;
            forwardSearch = true;
        } else if (ctx.dijkstraMode == -1) {
            end.others = null;
            forwardSearch = false;
        }
        PriorityQueue<RouteSegmentCost> graphSegments = forwardSearch ? graphDirectSegments : graphReverseSegments;
        float[] minCost = new float[]{Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
        while (!graphSegments.isEmpty()) {
            RouteSegmentCost cst = graphSegments.poll();
            RouteSegment segment = cst.segment;
            int visitedCnt = (start != null ? visitedDirectSegments.size() : 0) + (end != null ? visitedOppositeSegments.size() : 0);
            // use accumulative approach
            ctx.memoryOverhead = visitedCnt * STANDARD_ROAD_VISITED_OVERHEAD +
                    (graphDirectSegments.size() + graphReverseSegments.size()) * STANDARD_ROAD_IN_QUEUE_OVERHEAD;
            if (TRACE_ROUTING) {
                printRoad(">", segment, !forwardSearch);
            }
            if (ctx.config.MAX_VISITED > 0 && visitedCnt > ctx.config.MAX_VISITED) {
                if (finalSegment != null) {
                    // we can mark incomplete
                }
                break;
            }
            boolean skipSegment = false;
            if (segment instanceof FinalRouteSegment) {
                if (RoutingContext.SHOW_GC_SIZE) {
                    log.warn("Estimated overhead " + (ctx.memoryOverhead / (1 << 20)) + " mb");
                    printMemoryConsumption("Memory occupied after calculation : ");
                }
                if (TRACE_ROUTING) {
                    println(" >>FINAL segment: " + segment);
                }

                if (ctx.dijkstraMode != 0) {
                    if (finalSegment == null) {
                        finalSegment = new MultiFinalRouteSegment((FinalRouteSegment) segment);
                    }
                    ((MultiFinalRouteSegment) finalSegment).all.add((FinalRouteSegment) segment);
                    skipSegment = true;
                } else {
                    finalSegment = (FinalRouteSegment) segment;
                    break;
                }
            }

            if (ctx.memoryOverhead > ctx.config.memoryLimitation * 0.95 && RoutingContext.SHOW_GC_SIZE) {
                printMemoryConsumption("Memory occupied before exception : ");
            }
            if (ctx.memoryOverhead > ctx.config.memoryLimitation * 0.95) {
                throw new IllegalStateException("There is not enough memory " + ctx.config.memoryLimitation / (1 << 20) + " Mb");
            }
            if (ctx.calculationProgress != null) {
                ctx.calculationProgress.visitedSegments++;
            }
            boolean visited = (forwardSearch ? visitedDirectSegments : visitedOppositeSegments)
                    .containsKey(calculateRoutePointId(segment));
            if (visited) {
                if (TRACE_ROUTING) {
                    println("  " + segment.segEnd + ">> Already visited by minimum");
                }
                skipSegment = true;
            } else if (cst.cost + 0.1 < minCost[forwardSearch ? 1 : 0] && ASSERT_CHECKS && ctx.calculationMode != RouteCalculationMode.COMPLEX) {
                if (ctx.config.heuristicCoefficient <= 1) {
                    throw new IllegalStateException(cst.cost + " < ???  " + minCost[forwardSearch ? 1 : 0]);
                }
            } else {
                minCost[forwardSearch ? 1 : 0] = cst.cost;
            }
            if (!skipSegment) {
                if (forwardSearch) {
                    boolean doNotAddIntersections = onlyBackward;
                    processRouteSegment(ctx, false, graphDirectSegments, visitedDirectSegments, segment,
                            visitedOppositeSegments, boundaries, doNotAddIntersections);
                } else {
                    boolean doNotAddIntersections = onlyForward;
                    processRouteSegment(ctx, true, graphReverseSegments, visitedOppositeSegments, segment,
                            visitedDirectSegments, boundaries, doNotAddIntersections);
                }
            }
            updateCalculationProgress(ctx, graphDirectSegments, graphReverseSegments);

            boolean reiterate = false;
            reiterate |= checkIfGraphIsEmpty(ctx, ctx.getPlanRoadDirection() <= 0, true, graphReverseSegments, end,
                    visitedOppositeSegments, "Route is not found to selected target point.");
            reiterate |= checkIfGraphIsEmpty(ctx, ctx.getPlanRoadDirection() >= 0, false, graphDirectSegments, start,
                    visitedDirectSegments, "Route is not found from selected start point.");
            if (reiterate) {
                minCost = new float[]{Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
            }
            if (ctx.planRouteIn2Directions()) {
                // initial iteration make in 2 directions
                if (visitedDirectSegments.isEmpty() && !graphDirectSegments.isEmpty()) {
                    forwardSearch = true;
                } else if (visitedOppositeSegments.isEmpty() && !graphReverseSegments.isEmpty()) {
                    forwardSearch = false;
                } else if (graphDirectSegments.isEmpty() || graphReverseSegments.isEmpty()) {
                    // can't proceed any more - check if final already exist
                    graphSegments = graphDirectSegments.isEmpty() ? graphReverseSegments : graphDirectSegments;
                    if (finalSegment == null) {
                        while (!graphSegments.isEmpty()) {
                            RouteSegmentCost pc = graphSegments.poll();
                            if (pc.segment instanceof FinalRouteSegment) {
                                finalSegment = (FinalRouteSegment) pc.segment;
                                break;
                            }
                        }
                    }
                    return finalSegment;
                } else {
                    RouteSegment fw = graphDirectSegments.peek().segment;
                    RouteSegment bw = graphReverseSegments.peek().segment;
                    forwardSearch = Double.compare(cost(fw.distanceFromStart, fw.distanceToEnd, ctx),
                            cost(bw.distanceFromStart, bw.distanceToEnd, ctx)) <= 0;
                }
            } else {
                // different strategy : use one directional graph
                forwardSearch = onlyForward;
                if (onlyBackward && !graphDirectSegments.isEmpty()) {
                    forwardSearch = true;
                }
                if (onlyForward && !graphReverseSegments.isEmpty()) {
                    forwardSearch = false;
                }
            }

            if (forwardSearch) {
                graphSegments = graphDirectSegments;
            } else {
                graphSegments = graphReverseSegments;
            }
            // check if interrupted
            if (ctx.calculationProgress != null && ctx.calculationProgress.isCancelled) {
                throw new InterruptedException("Route calculation interrupted");
            }
        }
        if (ctx.calculationProgress != null) {
            ctx.calculationProgress.visitedDirectSegments += visitedDirectSegments.size();
            ctx.calculationProgress.visitedOppositeSegments += visitedOppositeSegments.size();
            ctx.calculationProgress.directQueueSize += graphDirectSegments.size(); // Math.max(ctx.directQueueSize, graphDirectSegments.size());
            ctx.calculationProgress.oppositeQueueSize += graphReverseSegments.size();
        }
        return finalSegment;
    }

    private boolean checkIfGraphIsEmpty(final RoutingContext ctx, boolean allowDirection,
                                        boolean reverseWaySearch, PriorityQueue<RouteSegmentCost> graphSegments, RouteSegmentPoint pnt, TLongObjectMap<RouteSegment> visited,
                                        String msg) {
        if (allowDirection && graphSegments.isEmpty()) {
            if (pnt.others != null) {
                Iterator<RouteSegmentPoint> pntIterator = pnt.others.iterator();
                while (pntIterator.hasNext()) {
                    RouteSegmentPoint next = pntIterator.next();
                    pntIterator.remove();
                    float estimatedDistance = (float) estimatedDistance(next, reverseWaySearch, ctx);
                    RouteSegment pos = next.initRouteSegment(true);
                    if (pos != null && !visited.containsKey(calculateRoutePointId(pos)) &&
                            checkMovementAllowed(ctx, reverseWaySearch, pos)) {
                        pos.setParentRoute(null);
                        pos.distanceFromStart = 0;
                        pos.distanceToEnd = estimatedDistance;
                        graphSegments.add(new RouteSegmentCost(pos, ctx));
                    }
                    RouteSegment neg = next.initRouteSegment(false);
                    if (neg != null && !visited.containsKey(calculateRoutePointId(neg)) &&
                            checkMovementAllowed(ctx, reverseWaySearch, neg)) {
                        neg.setParentRoute(null);
                        neg.distanceFromStart = 0;
                        neg.distanceToEnd = estimatedDistance;
                        graphSegments.add(new RouteSegmentCost(neg, ctx));
                    }
                    if (!graphSegments.isEmpty()) {
                        println("Reiterate point with new " + (!reverseWaySearch ? "start " : "destination ")
                                + next.getRoad());
                        return true;
                    }
                }
                if (graphSegments.isEmpty()) {
                    throw new IllegalArgumentException(msg);
                }
            }
        }
        return false;
    }

    private RouteSegment initEdgeSegment(final RoutingContext ctx, RouteSegmentPoint pnt, boolean originalDir, PriorityQueue<RouteSegmentCost> graphSegments, boolean reverseSearchWay) {
        if (pnt == null) {
            return null;
        }
        // originalDir = true: start points & end points equal to pnt
        RouteSegment seg = ctx.loadRouteSegment(originalDir ? pnt.getStartPointX() : pnt.getEndPointX(),
                originalDir ? pnt.getStartPointY() : pnt.getEndPointY(), 0, reverseSearchWay);
        while (seg != null) {
            if (seg.getRoad().getId() == pnt.getRoad().getId() &&
                    (seg.getSegmentStart() == (originalDir ? pnt.getSegmentStart() : pnt.getSegmentEnd()))) {
                break;
            }
            seg = seg.getNext();
        }
        if (seg.getSegmentStart() != (originalDir ? pnt.getSegmentStart() : pnt.getSegmentEnd())
                || seg.getSegmentEnd() != (originalDir ? pnt.getSegmentEnd() : pnt.getSegmentStart())) {
            seg = seg.initRouteSegment(!seg.isPositive());
        }
        if (originalDir && (seg.getSegmentStart() != pnt.getSegmentStart() || seg.getSegmentEnd() != pnt.getSegmentEnd())) {
            throw new IllegalStateException();
        }
        if (!originalDir && (seg.getSegmentStart() != pnt.getSegmentEnd() || seg.getSegmentEnd() != pnt.getSegmentStart())) {
            throw new IllegalStateException();
        }
        if (!originalDir && ctx.config.initialDirection == null && ctx.config.penaltyForReverseDirection < 0) {
            // special case for single side spread point-dijkstra
            return null;
        }
        seg.setParentRoute(RouteSegment.NULL);
        float dist = -calculatePreciseStartTime(ctx, pnt.preciseX, pnt.preciseY, seg);
        // full segment length will be added on first visit
        seg.distanceFromStart = dist;

        if ((!reverseSearchWay && ctx.config.initialDirection != null) || (reverseSearchWay && ctx.config.targetDirection != null)) {
            // for start : f(start) = g(start) + h(start) = 0 + h(start) = h(start)
            // mark here as positive for further check
            double plusDir = seg.getRoad().directionRoute(seg.getSegmentStart(), seg.isPositive());
            double diff = plusDir - (reverseSearchWay ? ctx.config.targetDirection : ctx.config.initialDirection);
            if (Math.abs(MapUtils.alignAngleDifference(diff - Math.PI)) <= Math.PI / 3) {
                seg.distanceFromStart += ctx.config.penaltyForReverseDirection;
            }
        }
        if (checkMovementAllowed(ctx, reverseSearchWay, seg)) {
            seg.distanceToEnd = estimatedDistance(seg, reverseSearchWay, ctx);
            graphSegments.add(new RouteSegmentCost(seg, ctx));
            return seg;
        }
        return null;
    }

    private float calculatePreciseStartTime(final RoutingContext ctx, int projX, int projY, RouteSegment seg) {
        // compensate first segment difference to mid point (length) https://github.com/osmandapp/OsmAnd/issues/14148
        double fullTime = calcRoutingSegmentTimeOnlyDist(ctx.getRouter(), seg);
        double full = squareRootDist(seg.getStartPointX(), seg.getStartPointY(), seg.getEndPointX(), seg.getEndPointY()) + 0.01; // avoid div 0
        double fromStart = squareRootDist(projX, projY, seg.getStartPointX(), seg.getStartPointY());
        float dist = (float) (fromStart / full * fullTime);
        return dist;
    }


    private void initQueuesWithStartEnd(final RoutingContext ctx, RouteSegmentPoint start, RouteSegmentPoint end,
                                        PriorityQueue<RouteSegmentCost> graphDirectSegments, PriorityQueue<RouteSegmentCost> graphReverseSegments) {
        if (start != null) {
            ctx.startX = start.preciseX;
            ctx.startY = start.preciseY;
        }
        if (end != null) {
            ctx.targetX = end.preciseX;
            ctx.targetY = end.preciseY;
        }
        RouteSegment startPos = initEdgeSegment(ctx, start, true, graphDirectSegments, false);
        RouteSegment startNeg = initEdgeSegment(ctx, start, false, graphDirectSegments, false);
        RouteSegment endPos = initEdgeSegment(ctx, end, true, graphReverseSegments, true);
        RouteSegment endNeg = initEdgeSegment(ctx, end, false, graphReverseSegments, true);
        if (TRACE_ROUTING) {
            printRoad("Initial segment start positive: ", startPos, false);
            printRoad("Initial segment start negative: ", startNeg, false);
            printRoad("Initial segment end positive: ", endPos, false);
            printRoad("Initial segment end negative: ", endNeg, false);
        }
    }


    private void printMemoryConsumption(String string) {
        long h1 = RoutingContext.runGCUsedMemory();
        float mb = (1 << 20);
        log.warn(string + h1 / mb);
    }


    private void updateCalculationProgress(final RoutingContext ctx, PriorityQueue<RouteSegmentCost> graphDirectSegments,
                                           PriorityQueue<RouteSegmentCost> graphReverseSegments) {
        if (ctx.calculationProgress != null) {
            ctx.calculationProgress.reverseSegmentQueueSize = graphReverseSegments.size();
            ctx.calculationProgress.directSegmentQueueSize = graphDirectSegments.size();
            if (!graphDirectSegments.isEmpty() && ctx.getPlanRoadDirection() >= 0) {
                RouteSegment peek = graphDirectSegments.peek().segment;
                ctx.calculationProgress.distanceFromBegin = Math.max(peek.distanceFromStart,
                        ctx.calculationProgress.distanceFromBegin);
                ctx.calculationProgress.directDistance = peek.distanceFromStart + peek.distanceToEnd;
            }
            if (!graphReverseSegments.isEmpty() && ctx.getPlanRoadDirection() <= 0) {
                RouteSegment peek = graphReverseSegments.peek().segment;
                ctx.calculationProgress.distanceFromEnd = Math.max(peek.distanceFromStart + peek.distanceToEnd,
                        ctx.calculationProgress.distanceFromEnd);
                ctx.calculationProgress.reverseDistance = peek.distanceFromStart + peek.distanceToEnd;
            }
        }
    }

    private void printRoad(String prefix, RouteSegment segment, Boolean reverseWaySearch) {
        String p = "";
        if (reverseWaySearch != null) {
            p = (reverseWaySearch ? "B" : "F");
        }
        if (segment == null) {
            println(p + prefix + " Segment=null");
        } else {
            String pr;
            if (segment.parentRoute != null) {
                pr = " pend=" + segment.parentRoute.segEnd + " parent=" + segment.parentRoute.road;
            } else {
                pr = "";
            }
            println(p + prefix + "" + segment.road + " ind=" + segment.getSegmentStart() + "->" + segment.getSegmentEnd() +
                    " ds=" + ((float) segment.distanceFromStart) + " es=" + ((float) segment.distanceToEnd) + pr);
        }
    }

    private float estimatedDistance(RouteSegment seg, boolean rev, final RoutingContext ctx) {
        int x = seg.getStartPointX() / 2 + seg.getEndPointX() / 2;
        int y = seg.getStartPointY() / 2 + seg.getEndPointY() / 2;
        double distance = squareRootDist(x, y, rev ? ctx.startX : ctx.targetX, rev ? ctx.startY : ctx.targetY);
        return (float) (distance / ctx.getRouter().getMaxSpeed());
    }

    private static float h(RoutingContext ctx, int begX, int begY, int endX, int endY) {
        if (ctx.dijkstraMode != 0) {
            return 0;
        }
        double distToFinalPoint = squareRootDist(begX, begY, endX, endY);
        double result = distToFinalPoint / ctx.getRouter().getMaxSpeed();
        if (ctx.precalculatedRouteDirection != null) {
            float te = ctx.precalculatedRouteDirection.timeEstimate(begX, begY, endX, endY);
            if (te > 0) {
                return te;
            }
        }
        return (float) result;
    }


    private static void println(String logMsg) {
//		log.info(logMsg);
        System.out.println(logMsg);
    }

    private double calculateRouteSegmentTime(RoutingContext ctx, boolean reverseWaySearch, RouteSegment segment) {
        final RouteDataObject road = segment.road;
        // store <segment> in order to not have unique <segment, direction> in visitedSegments
        short segmentInd = reverseWaySearch ? segment.getSegmentStart() : segment.getSegmentEnd();
        short prevSegmentInd = !reverseWaySearch ? segment.getSegmentStart() : segment.getSegmentEnd();

        double distTimeOnRoadToPass = calcRoutingSegmentTimeOnlyDist(ctx.getRouter(), segment);
        // calculate possible obstacle plus time
        double obstacle = ctx.getRouter().defineRoutingObstacle(road, segmentInd, prevSegmentInd > segmentInd);
        if (obstacle < 0) {
            return -1;
        }
        double heightObstacle = ctx.getRouter().defineHeightObstacle(road, segmentInd, prevSegmentInd);
        if (heightObstacle < 0) {
            return -1;
        }
        return obstacle + heightObstacle + distTimeOnRoadToPass;

    }


    private float calcRoutingSegmentTimeOnlyDist(VehicleRouter router, RouteSegment segment) {
        int prevX = segment.road.getPoint31XTile(segment.getSegmentStart());
        int prevY = segment.road.getPoint31YTile(segment.getSegmentStart());
        int x = segment.road.getPoint31XTile(segment.getSegmentEnd());
        int y = segment.road.getPoint31YTile(segment.getSegmentEnd());
        float priority = router.defineSpeedPriority(segment.road, segment.isPositive());
        float speed = (router.defineRoutingSpeed(segment.road, segment.isPositive()) * priority);
        if (speed == 0) {
            speed = router.getDefaultSpeed() * priority;
        }
        // speed can not exceed max default speed according to A*
        if (speed > router.getMaxSpeed()) {
            speed = router.getMaxSpeed();
        }
        float distOnRoadToPass = (float) squareRootDist(prevX, prevY, x, y);
        return distOnRoadToPass / speed;
    }

    private void processRouteSegment(final RoutingContext ctx, boolean reverseWaySearch,
                                     PriorityQueue<RouteSegmentCost> graphSegments, TLongObjectMap<RouteSegment> visitedSegments,
                                     RouteSegment startSegment, TLongObjectMap<RouteSegment> oppositeSegments,
                                     TLongObjectMap<RouteSegment> boundaries, boolean doNotAddIntersections) {
        if (ASSERT_CHECKS && !checkMovementAllowed(ctx, reverseWaySearch, startSegment)) {
            throw new IllegalStateException();
        }
        final RouteDataObject road = startSegment.getRoad();
        if (TEST_SPECIFIC && road.getId() >> 6 == TEST_ID) {
            printRoad(" ! " + startSegment.distanceFromStart + " ", startSegment, reverseWaySearch);
        }
        // Go through all point of the way and find ways to continue
        // ! Actually there is small bug when there is restriction to move forward on the way (it doesn't take into account)
        // +/- diff from middle point
        RouteSegment nextCurrentSegment = startSegment;
        RouteSegment currentSegment = null;
        while (nextCurrentSegment != null) {
            currentSegment = nextCurrentSegment;
            nextCurrentSegment = null;

            // 1. calculate obstacle for passing this segment
            float segmentAndObstaclesTime = (float) calculateRouteSegmentTime(ctx, reverseWaySearch, currentSegment);
            if (segmentAndObstaclesTime < 0) {
                break;
            }
            // calculate new start segment time as we're going to assign to put to visited segments
            float distFromStartPlusSegmentTime = currentSegment.distanceFromStart + segmentAndObstaclesTime;

            // 2. check if segment was already visited in opposite direction
            // We check before we calculate segmentTime (to not calculate it twice with opposite and calculate turns
            // onto each segment).
            boolean bothDirVisited = checkIfOppositeSegmentWasVisited(ctx, reverseWaySearch, graphSegments,
                    currentSegment, oppositeSegments, boundaries);

            // 3. upload segment itself to visited segments
            long nextPntId = calculateRoutePointId(currentSegment);
            RouteSegment existingSegment = visitedSegments.put(nextPntId, currentSegment);
            if (existingSegment != null) {
                if (distFromStartPlusSegmentTime > existingSegment.distanceFromStart) {
                    // insert back original segment (test case with large area way)
                    visitedSegments.put(nextPntId, existingSegment);
                    if (TRACE_ROUTING) {
                        println("  " + currentSegment.segEnd + ">> Already visited");
                    }
                    break;
                } else {
                    if (ctx.config.heuristicCoefficient <= 1) {
                        if (RoutingContext.PRINT_ROUTING_ALERTS) {
                            System.err.println("! ALERT slower segment was visited earlier " + distFromStartPlusSegmentTime + " > "
                                    + existingSegment.distanceFromStart + ": " + currentSegment + " - " + existingSegment);
                        } else {
                            ctx.alertSlowerSegmentedWasVisitedEarlier++;
                        }
                    }
                }
            }

            // reassign @distanceFromStart to make it correct for visited segment
            currentSegment.distanceFromStart = distFromStartPlusSegmentTime;

            if (bothDirVisited) {
                // We stop here for shortcut creation (we can't improve the neighbors if they're already visited cause the opposite is min - prove by contradiction)
                if (TRACE_ROUTING) {
                    println("  " + currentSegment.segEnd + ">> 2 dir visited");
                }
                break;
            }

            // 4. load road connections at the end of segment
            nextCurrentSegment = processIntersections(ctx, graphSegments, visitedSegments, currentSegment, reverseWaySearch, doNotAddIntersections);

            // Theoretically we should process each step separately but we don't have any issues with it.
            // a) final segment is always in queue & double checked b) using osm segment almost always is shorter routing than other connected
            if (DEBUG_BREAK_EACH_SEGMENT && nextCurrentSegment != null) {
                if (!doNotAddIntersections) {
                    graphSegments.add(new RouteSegmentCost(nextCurrentSegment, ctx));
                }
                break;
            }
            if (doNotAddIntersections) {
                break;
            }
        }

        if (ctx.visitor != null) {
            ctx.visitor.visitSegment(startSegment, currentSegment.getSegmentEnd(), true);
        }
    }

    private boolean checkMovementAllowed(final RoutingContext ctx, boolean reverseWaySearch, RouteSegment segment) {
        boolean directionAllowed;
        int oneway = ctx.getRouter().isOneWay(segment.getRoad());
        // use positive direction as agreed
        if (!reverseWaySearch) {
            if (segment.isPositive()) {
                directionAllowed = oneway >= 0;
            } else {
                directionAllowed = oneway <= 0;
            }
        } else {
            if (segment.isPositive()) {
                directionAllowed = oneway <= 0;
            } else {
                directionAllowed = oneway >= 0;
            }
        }
        return directionAllowed;
    }


    private boolean checkViaRestrictions(RouteSegment from, RouteSegment to) {
        if (from != null && to != null) {
            long fid = to.getRoad().getId();
            for (int i = 0; i < from.getRoad().getRestrictionLength(); i++) {
                long id = from.getRoad().getRestrictionId(i);
                int tp = from.getRoad().getRestrictionType(i);
                if (fid == id) {
                    if (tp == MapRenderingTypes.RESTRICTION_NO_LEFT_TURN
                            || tp == MapRenderingTypes.RESTRICTION_NO_RIGHT_TURN
                            || tp == MapRenderingTypes.RESTRICTION_NO_STRAIGHT_ON
                            || tp == MapRenderingTypes.RESTRICTION_NO_U_TURN) {
                        return false;
                    }
                    break;
                }
                if (tp == MapRenderingTypes.RESTRICTION_ONLY_STRAIGHT_ON) {
                    return false;
                }
            }
        }
        return true;
    }

    private RouteSegment getParentDiffId(RouteSegment s) {
        if (s == null) {
            return null;
        }
        while (s.getParentRoute() != null && s.getParentRoute().getRoad().getId() == s.getRoad().getId()) {
            s = s.getParentRoute();
        }
        return s.getParentRoute();
    }

    private boolean checkIfOppositeSegmentWasVisited(RoutingContext ctx, boolean reverseWaySearch,
                                                     PriorityQueue<RouteSegmentCost> graphSegments, RouteSegment currentSegment,
                                                     TLongObjectMap<RouteSegment> oppositeSegments, TLongObjectMap<RouteSegment> boundaries) {
        // check inverse direction for opposite
        long currPoint = calculateRoutePointInternalId(currentSegment.getRoad(),
                currentSegment.getSegmentEnd(), currentSegment.getSegmentStart());
        if (boundaries != null && ctx.dijkstraMode != 0) {
            // limit by boundaries for dijkstra mode
            oppositeSegments = boundaries;
        }
        if (oppositeSegments.containsKey(currPoint)) {
            RouteSegment opposite = oppositeSegments.get(currPoint);
            RouteSegment curParent = getParentDiffId(currentSegment);
            RouteSegment oppParent = getParentDiffId(opposite);
            RouteSegment to = reverseWaySearch ? curParent : oppParent;
            RouteSegment from = !reverseWaySearch ? curParent : oppParent;
            if (checkViaRestrictions(from, to)) {
                FinalRouteSegment frs = new FinalRouteSegment(currentSegment.getRoad(),
                        currentSegment.getSegmentStart(), currentSegment.getSegmentEnd());
                frs.setParentRoute(currentSegment.getParentRoute());
                frs.reverseWaySearch = reverseWaySearch;
                float oppTime = opposite == null ? 0 : opposite.distanceFromStart;
                frs.distanceFromStart = oppTime + currentSegment.distanceFromStart;
                frs.distanceToEnd = 0;
                frs.opposite = opposite;
                if (frs.distanceFromStart < 0) {
                    // impossible route (when start/point on same segment but different dir) don't add to queue
                    return true;
                }
                graphSegments.add(new RouteSegmentCost(frs, ctx));
                if (TRACE_ROUTING) {
                    printRoad("  " + currentSegment.segEnd + ">> Final segment : ", frs, reverseWaySearch);
                }
                if (ctx.calculationProgress != null) {
                    ctx.calculationProgress.finalSegmentsFound++;
                }
                return true;
            }
        }
        if (boundaries != null && ctx.dijkstraMode == 0 && boundaries.containsKey(currPoint)) {
            // limit search by boundaries
            return true;
        }
        return false;
    }

    private long calculateRoutePointInternalId(final RouteDataObject road, int pntId, int nextPntId) {
        int positive = nextPntId - pntId;
        int pntLen = road.getPointsLength();
        if (pntId < 0 || nextPntId < 0 || pntId >= pntLen || nextPntId >= pntLen || (positive != -1 && positive != 1)) {
            // should be assert
            throw new IllegalStateException("Assert failed");
        }
        return (road.getId() << ROUTE_POINTS) + (pntId << 1) + (positive > 0 ? 1 : 0);
    }

    private long calculateRoutePointId(RouteSegment segm) {
        return calculateRoutePointInternalId(segm.getRoad(), segm.getSegmentStart(),
                segm.isPositive() ? segm.getSegmentStart() + 1 : segm.getSegmentStart() - 1);
        // return calculateRoutePointInternalId(segm.getRoad(), segm.getSegmentStart(), segm.getSegmentEnd());
    }


    private boolean proccessRestrictions(RoutingContext ctx, RouteSegment segment, RouteSegment inputNext, boolean reverseWay) {
        if (!ctx.getRouter().restrictionsAware()) {
            return false;
        }
        RouteDataObject road = segment.getRoad();
        RouteSegment parent = getParentDiffId(segment);
        if (!reverseWay && road.getRestrictionLength() == 0 &&
                (parent == null || parent.getRoad().getRestrictionLength() == 0)) {
            return false;
        }
        ctx.segmentsToVisitPrescripted.clear();
        ctx.segmentsToVisitNotForbidden.clear();
        processRestriction(ctx, inputNext, reverseWay, 0, road);
        if (parent != null) {
            processRestriction(ctx, inputNext, reverseWay, road.id, parent.getRoad());
        }
        return true;
    }


    private void processRestriction(RoutingContext ctx, RouteSegment inputNext, boolean reverseWay, long viaId,
                                    RouteDataObject road) {
        boolean via = viaId != 0;
        RouteSegment next = inputNext;
        boolean exclusiveRestriction = false;
        while (next != null) {
            int type = -1;
            if (!reverseWay) {
                for (int i = 0; i < road.getRestrictionLength(); i++) {
                    int rt = road.getRestrictionType(i);
                    long rv = road.getRestrictionVia(i);
                    if (road.getRestrictionId(i) == next.road.id) {
                        if (!via || rv == viaId) {
                            type = rt;
                            break;
                        }
                    }
                    if (rv == viaId && via && rt == MapRenderingTypes.RESTRICTION_ONLY_STRAIGHT_ON) {
                        type = MapRenderingTypes.RESTRICTION_NO_STRAIGHT_ON;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < next.road.getRestrictionLength(); i++) {
                    int rt = next.road.getRestrictionType(i);
                    long rv = next.road.getRestrictionVia(i);
                    long restrictedTo = next.road.getRestrictionId(i);
                    if (restrictedTo == road.id) {
                        if (!via || rv == viaId) {
                            type = rt;
                            break;
                        }
                    }

                    if (rv == viaId && via && rt == MapRenderingTypes.RESTRICTION_ONLY_STRAIGHT_ON) {
                        type = MapRenderingTypes.RESTRICTION_NO_STRAIGHT_ON;
                        break;
                    }

                    // Check if there is restriction only to the other than current road
                    if (rt == MapRenderingTypes.RESTRICTION_ONLY_RIGHT_TURN || rt == MapRenderingTypes.RESTRICTION_ONLY_LEFT_TURN
                            || rt == MapRenderingTypes.RESTRICTION_ONLY_STRAIGHT_ON) {
                        // check if that restriction applies to considered junk
                        RouteSegment foundNext = inputNext;
                        while (foundNext != null) {
                            if (foundNext.getRoad().id == restrictedTo) {
                                break;
                            }
                            foundNext = foundNext.next;
                        }
                        if (foundNext != null) {
                            type = REVERSE_WAY_RESTRICTION_ONLY; // special constant
                        }
                    }
                }
            }
            if (type == REVERSE_WAY_RESTRICTION_ONLY) {
                // next = next.next; continue;
            } else if (type == -1 && exclusiveRestriction) {
                // next = next.next; continue;
            } else if (type == MapRenderingTypes.RESTRICTION_NO_LEFT_TURN || type == MapRenderingTypes.RESTRICTION_NO_RIGHT_TURN
                    || type == MapRenderingTypes.RESTRICTION_NO_STRAIGHT_ON || type == MapRenderingTypes.RESTRICTION_NO_U_TURN) {
                // next = next.next; continue;
                if (via) {
                    ctx.segmentsToVisitPrescripted.remove(next);
                }
            } else if (type == -1) {
                // case no restriction
                ctx.segmentsToVisitNotForbidden.add(next);
            } else {
                if (!via) {
                    // case exclusive restriction (only_right, only_straight, ...)
                    // 1. in case we are going backward we should not consider only_restriction
                    // as exclusive because we have many "in" roads and one "out"
                    // 2. in case we are going forward we have one "in" and many "out"
                    if (!reverseWay) {
                        exclusiveRestriction = true;
                        ctx.segmentsToVisitNotForbidden.clear();
                        ctx.segmentsToVisitPrescripted.add(next);
                    } else {
                        ctx.segmentsToVisitNotForbidden.add(next);
                    }
                }
            }
            next = next.next;
        }
        if (!via) {
            ctx.segmentsToVisitPrescripted.addAll(ctx.segmentsToVisitNotForbidden);
        }
    }

    private RouteSegment processIntersections(RoutingContext ctx, PriorityQueue<RouteSegmentCost> graphSegments,
                                              TLongObjectMap<RouteSegment> visitedSegments, RouteSegment currentSegment,
                                              boolean reverseWaySearch, boolean doNotAddIntersections) {
        RouteSegment nextCurrentSegment = null;
        int targetEndX = reverseWaySearch ? ctx.startX : ctx.targetX;
        int targetEndY = reverseWaySearch ? ctx.startY : ctx.targetY;
        final int x = currentSegment.getRoad().getPoint31XTile(currentSegment.getSegmentEnd());
        final int y = currentSegment.getRoad().getPoint31YTile(currentSegment.getSegmentEnd());
        float distanceToEnd = h(ctx, x, y, targetEndX, targetEndY);
        // reassign @distanceToEnd to make it correct for visited segment
        currentSegment.distanceToEnd = distanceToEnd;

        final RouteSegment connectedNextSegment = ctx.loadRouteSegment(x, y, ctx.config.memoryLimitation - ctx.memoryOverhead, reverseWaySearch);
        RouteSegment roadIter = connectedNextSegment;
        boolean directionAllowed = true;
        boolean singleRoad = true;
        while (roadIter != null) {
            if (currentSegment.getSegmentEnd() == roadIter.getSegmentStart() && roadIter.road.getId() == currentSegment.getRoad().getId()) {
                nextCurrentSegment = roadIter.initRouteSegment(currentSegment.isPositive());
                if (nextCurrentSegment == null) {
                    // end of route (-1 or length + 1)
                    directionAllowed = false;
                } else {
                    if (nextCurrentSegment.isSegmentAttachedToStart()) {
                        directionAllowed = processOneRoadIntersection(ctx, reverseWaySearch, null,
                                visitedSegments, currentSegment, nextCurrentSegment);
                        if (!directionAllowed) {
                            nextCurrentSegment = null;
                        }
                    } else {
                        nextCurrentSegment.setParentRoute(currentSegment);
                        nextCurrentSegment.distanceFromStart = currentSegment.distanceFromStart;
                        nextCurrentSegment.distanceToEnd = distanceToEnd;
                        final int nx = nextCurrentSegment.getRoad().getPoint31XTile(nextCurrentSegment.getSegmentEnd());
                        final int ny = nextCurrentSegment.getRoad().getPoint31YTile(nextCurrentSegment.getSegmentEnd());
                        if (nx == x && ny == y) {
                            // don't process other intersections (let process further segment)
                            return nextCurrentSegment;
                        }
                    }
                }
            } else {
                singleRoad = false;
            }
            roadIter = roadIter.getNext();
        }

        if (singleRoad) {
            return nextCurrentSegment;
        }

        // find restrictions and iterator
        Iterator<RouteSegment> nextIterator = null;
        boolean thereAreRestrictions = proccessRestrictions(ctx, currentSegment, connectedNextSegment, reverseWaySearch);
        if (thereAreRestrictions) {
            nextIterator = ctx.segmentsToVisitPrescripted.iterator();
            if (TRACE_ROUTING) {
                println("  " + currentSegment.segEnd + ">> There are restrictions ");
            }
        }

        // Calculate possible turns to put into priority queue
        RouteSegment next = connectedNextSegment;
        boolean hasNext = nextIterator != null ? nextIterator.hasNext() : next != null;
        while (hasNext) {
            if (nextIterator != null) {
                next = nextIterator.next();
            }
            if (next.getSegmentStart() == currentSegment.getSegmentEnd() &&
                    next.getRoad().getId() == currentSegment.getRoad().getId()) {
                // skip itself
            } else if (!doNotAddIntersections) {
                RouteSegment nextPos = next.initRouteSegment(true);
                processOneRoadIntersection(ctx, reverseWaySearch, graphSegments, visitedSegments, currentSegment, nextPos);
                RouteSegment nextNeg = next.initRouteSegment(false);
                processOneRoadIntersection(ctx, reverseWaySearch, graphSegments, visitedSegments, currentSegment, nextNeg);
            }
            // iterate to next road
            if (nextIterator == null) {
                next = next.next;
                hasNext = next != null;
            } else {
                hasNext = nextIterator.hasNext();
            }
        }

        if (nextCurrentSegment == null && directionAllowed) {
            if (ctx.calculationMode != RouteCalculationMode.BASE) {
                // exception as it should not occur, if happens during approximation - should be investigated
                if (ASSERT_CHECKS) {
                    throw new IllegalStateException();
                }
            } else {
                //  Issue #13284: we know that bug in data (how we simplify base data and connect between regions), so we workaround it
                int newEnd = currentSegment.getSegmentEnd() + (currentSegment.isPositive() ? +1 : -1);
                if (newEnd >= 0 && newEnd < currentSegment.getRoad().getPointsLength() - 1) {
                    nextCurrentSegment = new RouteSegment(currentSegment.getRoad(), currentSegment.getSegmentEnd(),
                            newEnd);
                    nextCurrentSegment.setParentRoute(currentSegment);
                    nextCurrentSegment.distanceFromStart = currentSegment.distanceFromStart;
                    nextCurrentSegment.distanceToEnd = distanceToEnd;
                }
            }
        }
        return nextCurrentSegment;
    }

    private boolean processOneRoadIntersection(RoutingContext ctx, boolean reverseWaySearch, PriorityQueue<RouteSegmentCost> graphSegments,
                                               TLongObjectMap<RouteSegment> visitedSegments, RouteSegment segment, RouteSegment next) {
        if (next != null) {
            if (!checkMovementAllowed(ctx, reverseWaySearch, next)) {
                return false;
            }
            float obstaclesTime = 0;
            if (next.road.getId() != segment.road.getId()) {
                obstaclesTime = (float) ctx.getRouter().calculateTurnTime(next, segment);
            }
            if (obstaclesTime < 0) {
                return false;
            }
            float distFromStart = obstaclesTime + segment.distanceFromStart;
            if (TEST_SPECIFIC && next.road.getId() >> 6 == TEST_ID) {
                printRoad(" !? distFromStart=" + distFromStart + " from " + segment.getRoad().getId() +
                        " distToEnd=" + segment.distanceFromStart +
                        " segmentPoint=" + segment.getSegmentEnd() + " -- ", next, null);
            }
            RouteSegment visIt = visitedSegments.get(calculateRoutePointId(next));
            if (visIt != null) {
                if (TRACE_ROUTING) {
                    printRoad("  " + segment.segEnd + ">?", visitedSegments.get(calculateRoutePointId(next)), null);
                }
                // The segment was already visited! We can try to follow new route if it's shorter.
                // That is very exceptional situation and almost exception, it can happen
                // 1. We underestimate distanceToEnd - wrong h() of A* (heuristic > 1)
                // 2. We don't process small segments 1 by 1 as we should by Dijkstra
                if (distFromStart < visIt.distanceFromStart) {
                    double routeSegmentTime = calculateRouteSegmentTime(ctx, reverseWaySearch, visIt);
                    // we need to properly compare @distanceFromStart VISITED and NON-VISITED segment
                    if (visIt.distanceFromStart - (distFromStart + routeSegmentTime) > 0.01) { // cause we do double -> float we can get into infinite loop here
                        // Here it's not very legitimate action cause in theory we need to go up to the final segment in the queue & decrease final time
                        // But it's compensated by chain reaction cause next.distanceFromStart < finalSegment.distanceFromStart and revisiting all segments
                        // We don't check ```next.getParentRoute() == null``` cause segment could be unloaded
                        // so we need to add segment back to the queue & reassign the parent (same as for next.getParentRoute() == null)
                        if (ctx.config.heuristicCoefficient <= 1) {
                            if (DEBUG_BREAK_EACH_SEGMENT && ASSERT_CHECKS) {
                                throw new IllegalStateException();
                            }
                            if (RoutingContext.PRINT_ROUTING_ALERTS) {
                                System.err.println("! ALERT new faster path to a visited segment: "
                                        + (distFromStart + routeSegmentTime) + " < " + visIt.distanceFromStart + ": " + next + " - " + visIt);
                            } else {
                                ctx.alertFasterRoadToVisitedSegments++;
                            }
                        }
                        visitedSegments.remove(calculateRoutePointId(next));
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (!next.isSegmentAttachedToStart() || cost(next.distanceFromStart, next.distanceToEnd,
                    ctx) > cost(distFromStart, segment.distanceToEnd, ctx)) {
                next.distanceFromStart = distFromStart;
                next.distanceToEnd = segment.distanceToEnd;
                if (TRACE_ROUTING) {
                    printRoad(" " + (next.isSegmentAttachedToStart() ? "*" : "") + segment.getSegmentEnd() + ">>", next, null);
                }
                // put additional information to recover whole route after
                next.setParentRoute(segment);
                if (graphSegments != null) {
                    graphSegments.add(new RouteSegmentCost(next, ctx));
                }
                return true;
            }
        }
        return false;
    }
}
