package net.osmand.plus.settings.fragments;

import static net.osmand.plus.utils.OsmAndFormatter.getFormattedPedestrianHeight;
import static net.osmand.plus.utils.UiUtilities.CompoundButtonType.TOOLBAR;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import net.osmand.plus.R;
import net.osmand.plus.activities.OsmandActionBarActivity;
import net.osmand.plus.settings.enums.FootPathMode;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.ColorUtilities;
import net.osmand.plus.utils.UiUtilities;

import org.labyrinth.common.MeasureUtils;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class FootPathNavigationSettingFragment extends BaseSettingsFragment {

    private OsmandActionBarActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getMyActivity();
    }

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
        final boolean checked = settings.footPath;
        final View switchContainer = view.findViewById(R.id.toolbar_switch_container);

        {
            final int color = checked ? getActiveProfileColor() : ContextCompat.getColor(app, R.color.preference_top_switch_off);
            AndroidUtils.setBackground(switchContainer, new ColorDrawable(color));
        }

        {
            final SwitchCompat switchView = switchContainer.findViewById(R.id.switchWidget);
            switchView.setChecked(checked);
            UiUtilities.setupCompoundButton(switchView, isNightMode(), TOOLBAR);
        }

        {
            final TextView title = switchContainer.findViewById(R.id.switchButtonText);
            title.setText(checked ? R.string.shared_string_enabled : R.string.shared_string_disabled);
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
            updateView(screen);
        } else {
            screen.removeAll();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        settings.footPathMode = preference.getKey();
        final PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return false;
        }
        updateView(screen);
        updateAllSettings();
        return false;
    }

    @ColorRes
    protected int getBackgroundColorRes() {
        return ColorUtilities.getActivityBgColorId(isNightMode());
    }

    private void updateView(final PreferenceScreen screen) {
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            final Preference preference = screen.getPreference(i);
            if (preference instanceof CheckBoxPreference) {
                final String preferenceKey = preference.getKey();
                final boolean checked = preferenceKey != null && preferenceKey.equals(settings.footPathMode);
                ((CheckBoxPreference) preference).setChecked(checked);
            }
        }
    }

    private void setFootpathPref(final PreferenceScreen screen) {
        for (final FootPathMode sm : FootPathMode.values()) {
            final Preference preference = new CheckBoxPreference(activity);
            preference.setKey(sm.getKey());
            preference.setTitle(sm.getTitle());
            preference.setLayoutResource(sm.getLayout());
            screen.addPreference(preference);
        }
        {
            final Preference preference = new Preference(activity);
            preference.setLayoutResource(R.layout.card_bottom_divider);
            preference.setSelectable(false);
            screen.addPreference(preference);
        }
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setItemAnimator(null);
        return recyclerView;
    }

    @Override
    protected void onBindPreferenceViewHolder(final Preference preference, final PreferenceViewHolder holder) {
        super.onBindPreferenceViewHolder(preference, holder);
        final String key = preference.getKey();
        if (key == null) {
            return;
        }
        if (preference instanceof CheckBoxPreference) {
            final FootPathMode mode = FootPathMode.getMode(key);
            if (mode != null) {
                final View itemView = holder.itemView;
                final TextView description = itemView.findViewById(R.id.description);
                final boolean checked = ((CheckBoxPreference) preference).isChecked();
                description.setVisibility(checked ? View.VISIBLE : View.GONE);
                description.setText(new SpannableString(getString(mode.getDescription())));
                final View slider = itemView.findViewById(R.id.slider_group);
                if (slider != null) {
                    slider.setVisibility(checked ? View.VISIBLE : View.GONE);
                    if (checked) {
                        setupPedestrianHeightSlider(itemView, mode.getTitle());
                    }
                }
                itemView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            }
        }
    }

    private static final Quantity<Length> MIN_PEDESTRIAN_HEIGHT = getQuantity(30.0, CENTI(METRE));
    private static final Quantity<Length> MAX_PEDESTRIAN_HEIGHT = getQuantity(272.0, CENTI(METRE));

    private void setupPedestrianHeightSlider(View itemView, int titleRes) {
        {
            itemView.<TextView>findViewById(R.id.min).setText(getFormattedPedestrianHeight(MIN_PEDESTRIAN_HEIGHT, app));
            itemView.<TextView>findViewById(R.id.max).setText(getFormattedPedestrianHeight(MAX_PEDESTRIAN_HEIGHT, app));
        }

        final Quantity<Length> pedestrianHeight =
                MeasureUtils.clamp(
                        settings.pedestrianHeight,
                        MIN_PEDESTRIAN_HEIGHT,
                        MAX_PEDESTRIAN_HEIGHT);

        final TextView title = itemView.findViewById(android.R.id.title);
        title.setText(getString(pedestrianHeight, titleRes));

        {
            final Slider slider = itemView.findViewById(R.id.slider);
            slider.setValueFrom(toCentiMetres(MIN_PEDESTRIAN_HEIGHT));
            slider.setValueTo(toCentiMetres(MAX_PEDESTRIAN_HEIGHT));
            slider.setValue(toCentiMetres(pedestrianHeight));
            slider.addOnChangeListener(
                    (s, pedestrianHeightInCentiMetres, fromUser) -> {
                        settings.pedestrianHeight = fromCentiMetres(pedestrianHeightInCentiMetres);
                        title.setText(getString(settings.pedestrianHeight, titleRes));
                        app.getLocationProvider().footPath.setPedestrianHeight(settings.pedestrianHeight);
                    });
            UiUtilities.setupSlider(slider, isNightMode(), getActiveProfileColor());
        }
    }

    private String getString(final Quantity<Length> pedestrianHeight, final int titleRes) {
        return getString(
                R.string.ltr_or_rtl_combine_via_colon,
                getString(titleRes),
                getFormattedPedestrianHeight(pedestrianHeight, app));
    }

    private static Quantity<Length> fromCentiMetres(final float length) {
        return getQuantity(length, CENTI(METRE));
    }

    private static float toCentiMetres(final Quantity<Length> length) {
        return length.to(CENTI(METRE)).getValue().floatValue();
    }
}
