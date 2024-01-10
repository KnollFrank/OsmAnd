package org.labyrinth.model;

import org.labyrinth.coordinate.Geodetic;

public class PersonBuilder {

    private Geodetic personOnGlobe;
    private Geodetic personOnRoad;

    public PersonBuilder withPersonOnGlobe(final Geodetic personOnGlobe) {
        this.personOnGlobe = personOnGlobe;
        return this;
    }

    public PersonBuilder withPersonOnRoad(final Geodetic personOnRoad) {
        this.personOnRoad = personOnRoad;
        return this;
    }

    public Person createPerson() {
        return new Person(personOnGlobe, personOnRoad);
    }
}