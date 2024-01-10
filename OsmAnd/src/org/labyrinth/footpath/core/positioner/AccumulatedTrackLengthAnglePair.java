package org.labyrinth.footpath.core.positioner;

import org.labyrinth.coordinate.Angle;

class AccumulatedTrackLengthAnglePair {

    public final double accumulatedTrackLength;
    public final Angle angle;

    public AccumulatedTrackLengthAnglePair(final double accumulatedTrackLength, final Angle angle) {
        this.accumulatedTrackLength = accumulatedTrackLength;
        this.angle = angle;
    }
}
