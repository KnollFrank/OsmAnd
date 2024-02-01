package org.labyrinth.footpath.core;

import static org.labyrinth.common.MeasureUtils.divide;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.footpath.core.positioner.PositionerBestFit;
import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathPosition;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Navigator {

    public final Path path;
    private final PositionerBestFit positionerBestFit;

    public Navigator(final Path path, final Quantity<Length> stepLength) {
        this.path = path;
        this.positionerBestFit = new PositionerBestFit(path.getEdges(), stepLength);
    }

    public void stepInDirection(final Angle stepDirection) {
        positionerBestFit.stepInDirection(stepDirection);
    }

    public PathPosition getCurrentPathPosition() {
        return new PathPosition(
                path,
                divide(positionerBestFit.getCurrentCoveredLength(), path.getLength()));
    }
}
