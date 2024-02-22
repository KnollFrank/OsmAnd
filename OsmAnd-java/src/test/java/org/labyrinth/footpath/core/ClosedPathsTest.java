package org.labyrinth.footpath.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClosedPathsTest {

    @Test
    public void test_getClosedPathStartingAtNode1() {
        // Given
        final List<String> closedPath = Arrays.asList("A", "B", "A");

        // When
        final List<String> closedPathStartingAtB = ClosedPaths.getClosedPathStartingAtNode(closedPath, "B");

        // Then
        assertThat(closedPathStartingAtB, is(Arrays.asList("B", "A", "B")));
    }

    @Test
    public void test_getClosedPathStartingAtNode2() {
        // Given
        final List<String> closedPath = Arrays.asList("A", "B", "C", "D", "A");

        // When
        final List<String> closedPathStartingAtC = ClosedPaths.getClosedPathStartingAtNode(closedPath, "C");

        // Then
        assertThat(closedPathStartingAtC, is(Arrays.asList("C", "D", "A", "B", "C")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getClosedPathStartingAtNode_emptyPath() {
        // Given
        final List<String> emptyPath = Collections.emptyList();

        // When
        ClosedPaths.getClosedPathStartingAtNode(emptyPath, "C");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getClosedPathStartingAtNode_pathNotClosed() {
        // Given
        final List<String> nonClosedPath = Arrays.asList("A", "B");

        // When
        ClosedPaths.getClosedPathStartingAtNode(nonClosedPath, "B");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getClosedPathStartingAtNode_newStartNodeDoesNotExist() {
        // Given
        final List<String> closedPath = Arrays.asList("A", "B", "A");

        // When
        ClosedPaths.getClosedPathStartingAtNode(closedPath, "nonExistingStartNode");
    }
}