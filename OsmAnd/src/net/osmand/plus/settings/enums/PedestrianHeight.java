package net.osmand.plus.settings.enums;

import android.content.Context;

import androidx.preference.Preference;

import net.osmand.plus.R;

public class PedestrianHeight {

    public static final String key = "pedestrian_height";
    public static final int title = R.string.footpath_pedestrianheight_title;
    public static final int description = R.string.footpath_pedestrianheight_desc;
    public static final int layout = R.layout.footpath_slider;

    public static Preference getPreference(final Context context) {
        final Preference preference = new Preference(context);
        preference.setKey(key);
        preference.setTitle(title);
        preference.setLayoutResource(layout);
        return preference;
    }
}
