package org.labyrinth.footpath.core;

import com.google.common.collect.ImmutableList;
import org.labyrinth.common.ListUtils;

import java.util.List;

public class ClosedPaths {

    public static <T> List<T> getClosedPathStartingAtNode(final List<T> closedPath, final T newStartNode) {
        final int newStartNodeIndex = closedPath.indexOf(newStartNode);
        if (closedPath.isEmpty() || !isClosed(closedPath) || newStartNodeIndex == -1) {
            throw new IllegalArgumentException();
        }
        final List<T> openPath = closedPath.subList(0, closedPath.size() - 1);
        return ImmutableList
                .<T>builder()
                .addAll(openPath.subList(newStartNodeIndex, openPath.size()))
                .addAll(openPath.subList(0, newStartNodeIndex + 1))
                .build();
    }

    private static <T> boolean isClosed(final List<T> path) {
        final T start = ListUtils.getStart(path).get();
        final T end = ListUtils.getEnd(path).get();
        return start.equals(end);
    }
}
