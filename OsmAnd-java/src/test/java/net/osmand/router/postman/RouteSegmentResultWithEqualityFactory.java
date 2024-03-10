package net.osmand.router.postman;

import net.osmand.router.RouteSegmentResult;

import java.util.List;
import java.util.stream.Collectors;

class RouteSegmentResultWithEqualityFactory {

    public static RouteSegmentResultWithEquality createRouteSegmentResultWithEquality(
            final RouteSegmentResult routeSegmentResult) {
        return new RouteSegmentResultWithEquality(
                routeSegmentResult.getObject().id,
                routeSegmentResult.getObject().getName(),
                routeSegmentResult.getStartPointIndex(),
                routeSegmentResult.getEndPointIndex());
    }

    public static List<RouteSegmentResultWithEquality> getRouteSegmentResultWithEqualities(
            final List<RouteSegmentResult> routeSegmentResults) {
        return routeSegmentResults
                .stream()
                .map(RouteSegmentResultWithEqualityFactory::createRouteSegmentResultWithEquality)
                .collect(Collectors.toList());
    }
}
