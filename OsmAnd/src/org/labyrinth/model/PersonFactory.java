package org.labyrinth.model;

import org.labyrinth.converter.Geodetic2RoadConverter;
import org.labyrinth.coordinate.Geodetic;

public class PersonFactory {

    public static Person fromPersonOnGlobe(final Geodetic personOnGlobe, final Geodetic2RoadConverter geodetic2RoadConverter) {
        return new PersonBuilder()
                .withPersonOnGlobe(personOnGlobe)
                .withPersonOnRoad(geodetic2RoadConverter.snapGeodeticToRoad(personOnGlobe))
                .createPerson();
    }

    public static Person fromPersonOnRoad(final Geodetic personOnRoad) {
        return new PersonBuilder()
                .withPersonOnGlobe(personOnRoad)
                .withPersonOnRoad(personOnRoad)
                .createPerson();
    }
}
