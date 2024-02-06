package org.labyrinth.settings;

import static org.labyrinth.settings.LengthUnitsConverter.centimetres2FeetAndInches;
import static org.labyrinth.settings.LengthUnitsConverter.feetAndInches2Centimetres;
import static org.labyrinth.settings.Spinner2LengthUnitConverter.itemPos2LengthUnit;
import static org.labyrinth.settings.Spinner2LengthUnitConverter.lengthUnit2ItemPos;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.core.math.MathUtils;

import com.google.common.collect.Range;

import net.osmand.plus.R;

class PedestrianHeightDialog {

    private static final Range<Integer> CM_RANGE = Range.closed(30, 272);
    private static final Range<Integer> FEET_RANGE = Range.closed(1, 8);
    private static final Range<Integer> INCHES_RANGE = Range.closed(0, 11);

    public final View view;
    private final Spinner unitsSpinner;
    private final NumberPicker centimetrePicker;
    private final NumberPicker feetPicker;
    private final NumberPicker inchesPicker;

    public PedestrianHeightDialog(final Height initialHeight, final View view) {
        this.view = view;
        this.unitsSpinner = view.findViewById(R.id.units_spinner);
        this.centimetrePicker = view.findViewById(R.id.centimetrePicker);
        this.feetPicker = view.findViewById(R.id.feetPicker);
        this.inchesPicker = view.findViewById(R.id.inchesPicker);
        configureUnitsSpinner(view.getContext(), initialHeight.lengthUnit);
        configureCentimetrePicker(initialHeight.heightInCM);
        configureFeetAndInchesPicker(centimetres2FeetAndInches(initialHeight.heightInCM));
    }

    public Height getHeight() {
        final LengthUnit lengthUnit = itemPos2LengthUnit(unitsSpinner.getSelectedItemPosition());
        final int heightInCM = centimetrePicker.getValue();
        return new Height(lengthUnit, heightInCM);
    }

    private void configureUnitsSpinner(final Context context, final LengthUnit lengthUnit) {
        final ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        context,
                        R.array.units_array,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(adapter);
        unitsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                        showSectionFor(itemPos2LengthUnit(position));
                    }

                    private void showSectionFor(final LengthUnit lengthUnit) {
                        switch (lengthUnit) {
                            case CENTIMETRE:
                                centimetrePicker.setVisibility(View.VISIBLE);
                                feetPicker.setVisibility(View.GONE);
                                inchesPicker.setVisibility(View.GONE);
                                break;
                            case FEET_AND_INCHES:
                                centimetrePicker.setVisibility(View.GONE);
                                feetPicker.setVisibility(View.VISIBLE);
                                inchesPicker.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(final AdapterView<?> parent) {
                    }
                });
        unitsSpinner.setSelection(lengthUnit2ItemPos(lengthUnit));
    }

    private void configureFeetAndInchesPicker(final FeetAndInches feetAndInches) {
        configureFeetPicker(feetAndInches.feet);
        configureInchesPicker(feetAndInches.inches);
    }

    private void configureFeetPicker(final int feet) {
        setMinMaxValue(feetPicker, FEET_RANGE);
        setRoundedAndClampedValueInNumberPicker(feetPicker, feet, FEET_RANGE);
        feetPicker.setDisplayedValues(getDisplayedValues(FEET_RANGE, "ft"));
        feetPicker.setWrapSelectorWheel(true);
        feetPicker.setOnValueChangedListener(
                (picker, oldFeet, newFeet) -> updateCentimetrePickerFromFeetAndInchesPicker());
    }

    private void configureInchesPicker(final double inches) {
        setMinMaxValue(inchesPicker, INCHES_RANGE);
        setRoundedAndClampedValueInNumberPicker(inchesPicker, inches, INCHES_RANGE);
        inchesPicker.setDisplayedValues(getDisplayedValues(INCHES_RANGE, "in"));
        inchesPicker.setWrapSelectorWheel(true);
        inchesPicker.setOnValueChangedListener((picker, oldInches, newInches) ->
                updateCentimetrePickerFromFeetAndInchesPicker());
    }

    private void updateCentimetrePickerFromFeetAndInchesPicker() {
        setRoundedAndClampedValueInNumberPicker(
                centimetrePicker,
                feetAndInches2Centimetres(feetPicker.getValue(), inchesPicker.getValue()),
                CM_RANGE);
    }

    private void setRoundedAndClampedValueInNumberPicker(final NumberPicker numberPicker,
                                                         final double value,
                                                         final Range<Integer> clampRange) {
        numberPicker.setValue(clamp(round(value), clampRange));
    }

    private static int round(final double value) {
        return (int) Math.round(value);
    }

    private static int clamp(final int value, final Range<Integer> clampRange) {
        return MathUtils.clamp(value, clampRange.lowerEndpoint(), clampRange.upperEndpoint());
    }

    private void configureCentimetrePicker(final double heightInCM) {
        setMinMaxValue(centimetrePicker, CM_RANGE);
        setRoundedAndClampedValueInNumberPicker(centimetrePicker, heightInCM, CM_RANGE);
        centimetrePicker.setDisplayedValues(getDisplayedValues(CM_RANGE, "cm"));
        centimetrePicker.setWrapSelectorWheel(true);
        centimetrePicker.setOnValueChangedListener((picker, oldCM, newCM) -> {
            final FeetAndInches feetAndInches = centimetres2FeetAndInches(newCM);
            setRoundedAndClampedValueInNumberPicker(feetPicker, feetAndInches.feet, FEET_RANGE);
            setRoundedAndClampedValueInNumberPicker(inchesPicker, feetAndInches.inches, INCHES_RANGE);
        });
    }

    private void setMinMaxValue(final NumberPicker numberPicker, final Range<Integer> minMaxRange) {
        numberPicker.setMinValue(minMaxRange.lowerEndpoint());
        numberPicker.setMaxValue(minMaxRange.upperEndpoint());
    }

    private String[] getDisplayedValues(final Range<Integer> range, final String unit) {
        final int minValue = range.lowerEndpoint();
        final int maxValue = range.upperEndpoint();
        final String[] displayedValues = new String[maxValue - minValue + 1];
        for (int value = minValue; value <= maxValue; value++) {
            displayedValues[value - minValue] = "" + value + " " + unit;
        }
        return displayedValues;
    }
}
