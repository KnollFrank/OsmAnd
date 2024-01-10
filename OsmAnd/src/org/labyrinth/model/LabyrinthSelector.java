package org.labyrinth.model;

import org.labyrinth.coordinate.Geodetic;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.labyrinth.common.MeasureUtils.toMeters;

public class LabyrinthSelector {

    public <T extends ILabyrinth> List<T> getLabyrinthsSortedAscByDistanceToUserLocation(
            final Set<T> labyrinths,
            final Geodetic userLocation) {
        return labyrinths
                .stream()
                .sorted(Comparator.comparingDouble(labyrinth -> getDistance(userLocation, labyrinth)))
                .collect(Collectors.toList());
    }

    public <T extends ILabyrinth> Optional<T> getNearestLabyrinth(final Set<T> labyrinths, final Geodetic userLocation) {
        return labyrinths
                .stream()
                .min(Comparator.comparingDouble(labyrinth -> getDistance(userLocation, labyrinth)));
    }

    private double getDistance(final Geodetic userLocation, final ILabyrinth labyrinth) {
        return toMeters(userLocation.getDistanceTo(labyrinth.getLocation()));
    }
}
