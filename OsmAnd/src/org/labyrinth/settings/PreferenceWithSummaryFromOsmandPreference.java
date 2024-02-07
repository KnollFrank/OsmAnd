package org.labyrinth.settings;

import android.content.Context;

import androidx.preference.Preference;

import net.osmand.StateChangedListener;
import net.osmand.plus.settings.backend.preferences.OsmandPreference;

class PreferenceWithSummaryFromOsmandPreference<T> extends Preference {

    private final OsmandPreference<T> osmandPreference;

    private final StateChangedListener<T> setSummary;

    public PreferenceWithSummaryFromOsmandPreference(
            final Context context,
            final OsmandPreference<T> osmandPreference,
            final SummaryProvider<PreferenceWithSummaryFromOsmandPreference<T>> summaryProvider) {
        super(context);
        this.osmandPreference = osmandPreference;
        this.setSummary = change -> setSummary(summaryProvider.provideSummary(this));
        this.setSummary.stateChanged(null);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        osmandPreference.addListener(this.setSummary);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        osmandPreference.removeListener(this.setSummary);
    }
}
