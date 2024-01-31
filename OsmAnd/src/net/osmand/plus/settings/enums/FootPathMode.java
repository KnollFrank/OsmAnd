package net.osmand.plus.settings.enums;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.osmand.plus.R;

// FK-TODO: refactor, this is not an enum
public enum FootPathMode {
    PEDESTRIAN_HEIGHT("pedestrian_height", R.string.footpath_pedestrianheight_title, R.string.footpath_pedestrianheight_desc, R.layout.preference_simulation_mode_slider);

    public final String key;
    public final int title;
    public final int description;
    public final int layout;

    FootPathMode(String key, @StringRes int title, @StringRes int description, @LayoutRes int layout) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.layout = layout;
    }

    @Nullable
    public static FootPathMode getMode(String key) {
        for (FootPathMode mode : values()) {
            if (mode.key.equals(key)) {
                return mode;
            }
        }
        return null;
    }
}
