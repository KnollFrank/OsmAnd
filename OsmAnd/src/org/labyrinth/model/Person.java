package org.labyrinth.model;

import org.labyrinth.coordinate.Geodetic;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Person implements Serializable {

    public final Geodetic personOnGlobe;
    public final Geodetic personOnRoad;

    Person(final Geodetic personOnGlobe, final Geodetic personOnRoad) {
        this.personOnGlobe = personOnGlobe;
        this.personOnRoad = personOnRoad;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Person person = (Person) o;
        return personOnGlobe.equals(person.personOnGlobe) &&
                personOnRoad.equals(person.personOnRoad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personOnGlobe, personOnRoad);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("personOnGlobe=" + personOnGlobe)
                .add("personOnRoad=" + personOnRoad)
                .toString();
    }
}
