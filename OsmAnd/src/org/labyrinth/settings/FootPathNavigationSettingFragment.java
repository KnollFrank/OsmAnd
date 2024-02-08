package org.labyrinth.settings;

import static net.osmand.plus.utils.UiUtilities.CompoundButtonType.TOOLBAR;
import static org.labyrinth.common.MeasureUtils.toCentiMetres;
import static org.labyrinth.settings.LengthUnitsConverter.centimetres2FeetAndInches;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import net.osmand.plus.R;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.ColorUtilities;
import net.osmand.plus.utils.UiUtilities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class FootPathNavigationSettingFragment extends BaseSettingsFragment {

    private static final String PEDESTRIAN_HEIGHT_KEY = "pedestrian_height";

    @Override
    protected void createToolbar(@NonNull LayoutInflater inflater, @NonNull View view) {
        super.createToolbar(inflater, view);
        view
                .findViewById(R.id.toolbar_switch_container)
                .setOnClickListener(v -> {
                    settings.footPath = !settings.footPath;
                    app.getLocationProvider().footPath.setEnabled(settings.footPath);
                    updateToolbarSwitch(view);
                    updateAllSettings();
                });
    }

    protected void updateToolbar() {
        final View view = getView();
        if (view == null) {
            return;
        }
        view.<ImageView>findViewById(R.id.profile_icon).setVisibility(View.GONE);
        view.<TextView>findViewById(R.id.toolbar_subtitle).setVisibility(View.GONE);
        updateToolbarSwitch(view);
    }

    private void updateToolbarSwitch(final View view) {
        final View switchContainer = view.findViewById(R.id.toolbar_switch_container);

        AndroidUtils.setBackground(
                switchContainer,
                new ColorDrawable(
                        settings.footPath ?
                                getActiveProfileColor() :
                                ContextCompat.getColor(app, R.color.preference_top_switch_off)));

        {
            final SwitchCompat switchView = switchContainer.findViewById(R.id.switchWidget);
            switchView.setChecked(settings.footPath);
            UiUtilities.setupCompoundButton(switchView, isNightMode(), TOOLBAR);
        }

        {
            final TextView title = switchContainer.findViewById(R.id.switchButtonText);
            title.setText(settings.footPath ? R.string.shared_string_enabled : R.string.shared_string_disabled);
        }
    }

    @Override
    protected void setupPreferences() {
        final PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return;
        }
        if (settings.footPath) {
            setFootpathPref(screen);
        } else {
            screen.removeAll();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        final PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return false;
        }
        updateAllSettings();
        return false;
    }

    @ColorRes
    protected int getBackgroundColorRes() {
        return ColorUtilities.getActivityBgColorId(isNightMode());
    }

    private void setFootpathPref(final PreferenceScreen screen) {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        screen.addPreference(createPedestrianHeightPreference(context));
    }

    private Preference createPedestrianHeightPreference(final Context context) {
        final Preference preference =
                new PreferenceWithSummaryFromOsmandPreference<>(
                        context,
                        settings.PEDESTRIAN_HEIGHT_IN_CENTIMETRES,
                        _preference -> getPedestrianHeightPreferenceSummary());
        preference.setKey(PEDESTRIAN_HEIGHT_KEY);
        preference.setTitle(R.string.footpath_pedestrianheight_title);
        preference.setLayoutResource(R.layout.preference_with_descr);
        return preference;
    }

    private String getPedestrianHeightPreferenceSummary() {
        return this
                .getSettings()
                .getPedestrianHeight()
                .map(FootPathNavigationSettingFragment::getString)
                .orElseGet(() -> getString(R.string.footpath_pedestrianheight_desc));
    }

    private static String getString(final Quantity<Length> length) {
        final double heightInCM = toCentiMetres(length);
        final FeetAndInches feetAndInches = centimetres2FeetAndInches(heightInCM);
        return "" +
                round(heightInCM) + " cm = " +
                feetAndInches.feet + " ft, " + round(feetAndInches.inches) + " in";
    }

    private static int round(final double value) {
        return (int) Math.round(value);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if (preference.getKey().equals(PEDESTRIAN_HEIGHT_KEY)) {
            final Context context = getContext();
            if (context != null) {
                PedestrianHeightDialogHelper.showPedestrianHeightDialog(app, getSelectedAppMode(), context);
            }
            return true;
        }
        return super.onPreferenceClick(preference);
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setItemAnimator(null);
        return recyclerView;
    }
}
