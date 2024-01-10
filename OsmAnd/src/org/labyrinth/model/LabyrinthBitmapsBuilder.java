package org.labyrinth.model;

import android.graphics.Bitmap;

public class LabyrinthBitmapsBuilder {

    private Bitmap entry;
    private Bitmap walking;

    public LabyrinthBitmapsBuilder withEntry(final Bitmap entry) {
        this.entry = entry;
        return this;
    }

    public LabyrinthBitmapsBuilder withWalking(final Bitmap walking) {
        this.walking = walking;
        return this;
    }

    public LabyrinthBitmaps createLabyrinthBitmaps() {
        return new LabyrinthBitmaps(entry, walking);
    }
}