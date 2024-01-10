package org.labyrinth.converter;

import org.labyrinth.coordinate.Geodetic;

public class Geodetic2RoadConverter {

    public final Geodetic2EdgePositionConverter geodetic2EdgePositionConverter;

    public Geodetic2RoadConverter(final Geodetic2EdgePositionConverter geodetic2EdgePositionConverter) {
        this.geodetic2EdgePositionConverter = geodetic2EdgePositionConverter;
    }

    public Geodetic snapGeodeticToRoad(final Geodetic geodetic) {
        return geodetic2EdgePositionConverter
                .geodetic2EdgePosition(geodetic)
                .getGeodetic();
    }
}
