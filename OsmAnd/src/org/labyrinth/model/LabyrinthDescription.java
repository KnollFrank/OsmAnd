package org.labyrinth.model;

public class LabyrinthDescription {

    public static final String LABYRINTH_DEFINITION_OSM = "Labyrinth.pbf";
    public static final String LABYRINTH_DEFINITION_MAP = "Labyrinth.map";

    private final String directory;

    public LabyrinthDescription(final String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public String getOsmFileName() {
        return LABYRINTH_DEFINITION_OSM;
    }
}