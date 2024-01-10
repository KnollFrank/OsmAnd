package org.labyrinth.footpath.core;

import org.labyrinth.coordinate.Angle;

public interface IStepListener {

    void onStepDetected(Angle stepDirection);
}
