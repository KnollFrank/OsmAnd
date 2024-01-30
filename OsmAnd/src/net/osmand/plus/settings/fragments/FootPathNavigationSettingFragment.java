package net.osmand.plus.settings.fragments;

import static net.osmand.plus.utils.OsmAndFormatter.getFormattedPedestrianHeight;
import static net.osmand.plus.utils.UiUtilities.CompoundButtonType.TOOLBAR;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
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
import net.osmand.plus.settings.enums.SimulationMode;
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
                    updateToolbarSwitch(view);
                    updateAllSettings();
                });
    }

    protected void updateToolbar() {
        final View view = getView();
        if (view == null) {
            return;
        }
        final ImageView profileIcon = view.findViewById(R.id.profile_icon);
        profileIcon.setVisibility(View.GONE);

        final TextView profileTitle = view.findViewById(R.id.toolbar_subtitle);
        profileTitle.setVisibility(View.GONE);

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
        settings.simulateNavigationMode = preference.getKey();
        PreferenceScreen screen = getPreferenceScreen();
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

    private void updateView(PreferenceScreen screen) {
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            Preference preference = screen.getPreference(i);
            if (preference instanceof CheckBoxPreference) {
                String preferenceKey = preference.getKey();
                boolean checked = preferenceKey != null && preferenceKey.equals(settings.simulateNavigationMode);
                ((CheckBoxPreference) preference).setChecked(checked);
            }
        }
    }

    private void setFootpathPref(final PreferenceScreen screen) {
        Preference preference = new Preference(activity);
        preference.setLayoutResource(R.layout.preference_simulation_title);
        preference.setTitle(R.string.speed_mode);
        preference.setSelectable(false);
        screen.addPreference(preference);
        for (final SimulationMode sm : SimulationMode.values()) {
            preference = new CheckBoxPreference(activity);
            preference.setKey(sm.getKey());
            preference.setTitle(sm.getTitle());
            preference.setLayoutResource(sm.getLayout());
            screen.addPreference(preference);
        }
        preference = new Preference(activity);
        preference.setLayoutResource(R.layout.card_bottom_divider);
        preference.setSelectable(false);
        screen.addPreference(preference);
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setItemAnimator(null);
        return recyclerView;
    }

    @Override
    protected void onBindPreferenceViewHolder(Preference preference, PreferenceViewHolder holder) {
        super.onBindPreferenceViewHolder(preference, holder);
        String key = preference.getKey();
        if (key == null) {
            return;
        }
        View itemView = holder.itemView;
        if (preference instanceof CheckBoxPreference) {
            SimulationMode mode = SimulationMode.getMode(key);
            if (mode != null) {
                TextView description = itemView.findViewById(R.id.description);
                boolean checked = ((CheckBoxPreference) preference).isChecked();
                description.setVisibility(checked ? View.VISIBLE : View.GONE);
                String str = getString(mode.getDescription());
                SpannableString spanDescription = new SpannableString(str);
                if (mode == SimulationMode.REALISTIC) {
                    int startLine = 0;
                    int endLine = 0;
                    int dp8 = AndroidUtils.dpToPx(itemView.getContext(), 8f);
                    while (endLine < str.length()) {
                        endLine = str.indexOf("\n", startLine);
                        endLine = endLine > 0 ? endLine : str.length();
                        spanDescription.setSpan(new BulletSpan(dp8),
                                startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        startLine = endLine + 1;
                    }
                    AndroidUtils.setPadding(description, dp8, 0, 0, 0);
                }
                description.setText(spanDescription);
                View slider = itemView.findViewById(R.id.slider_group);
                if (slider != null) {
                    slider.setVisibility(checked ? View.VISIBLE : View.GONE);
                    if (checked) {
                        setupSpeedSlider(itemView, mode.getTitle());
                    }
                }
                View divider = itemView.findViewById(R.id.divider);
                if (mode != SimulationMode.REALISTIC) {
                    divider.setVisibility(View.VISIBLE);
                } else {
                    divider.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setupSpeedSlider(View itemView, int titleRes) {
        Quantity<Length> min = getQuantity(70.0, CENTI(METRE));
        Quantity<Length> max = getQuantity(272.0, CENTI(METRE));
        Quantity<Length> speedValue = settings.pedestrianHeight;
        speedValue = MeasureUtils.min(speedValue, max);
        Slider slider = itemView.findViewById(R.id.slider);
        TextView title = itemView.findViewById(android.R.id.title);
        TextView minSpeed = itemView.findViewById(R.id.min);
        TextView maxSpeed = itemView.findViewById(R.id.max);

        minSpeed.setText(getFormattedPedestrianHeight(min, app));
        maxSpeed.setText(getFormattedPedestrianHeight(max, app));
        title.setText(getString(R.string.ltr_or_rtl_combine_via_colon, getString(titleRes),
                getFormattedPedestrianHeight(speedValue, app)));
        slider.setValueTo(toCentiMetres(max.subtract(min)));
        slider.setValue(toCentiMetres(speedValue.subtract(min)));
        slider.addOnChangeListener((s, val, fromUser) -> {
            final Quantity<Length> value = min.add(fromCentiMetres(val));
            title.setText(getString(R.string.ltr_or_rtl_combine_via_colon,
                    getString(titleRes), getFormattedPedestrianHeight(value, app)));
            settings.pedestrianHeight = value;
        });
        UiUtilities.setupSlider(slider, isNightMode(), getActiveProfileColor());
    }

    private static Quantity<Length> fromCentiMetres(final float length) {
        return getQuantity(length, CENTI(METRE));
    }

    private static float toCentiMetres(final Quantity<Length> length) {
        return length.to(CENTI(METRE)).getValue().floatValue();
    }
}
