package org.labyrinth.settings;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.utils.UiUtilities;

import java.util.Optional;
import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class PedestrianHeightDialogHelper {

    public static void showPedestrianHeightDialog(final OsmandApplication app,
                                                  final ApplicationMode mode,
                                                  final Context context) {
        PedestrianHeightDialogHelper
                .createPedestrianHeightDialog(
                        Height.fromQuantity(getInitialPedestrianHeight(mode.getPedestrianHeight())),
                        pedestrianHeight -> {
                            mode.setPedestrianHeight(pedestrianHeight);
                            app.getLocationProvider().footPath.setPedestrianHeight(pedestrianHeight);
                        },
                        UiUtilities.getThemedContext(context, isNightMode(app, mode)))
                .show();
    }

    private static Dialog createPedestrianHeightDialog(
            final Height initialHeight,
            final Consumer<Quantity<Length>> onOkButtonClicked,
            final Context context) {
        final PedestrianHeightDialog pedestrianHeightDialog =
                new PedestrianHeightDialog(initialHeight, createView(context));
        return new AlertDialog
                .Builder(context)
                .setView(pedestrianHeightDialog.view)
                .setTitle(R.string.preferences_height_title)
                .setPositiveButton(
                        R.string.shared_string_ok,
                        (dialog, which) -> onOkButtonClicked.accept(pedestrianHeightDialog.getHeight().toQuantity()))
                .setCancelable(false)
                .create();
    }

    private static Quantity<Length> getInitialPedestrianHeight(final Optional<Quantity<Length>> pedestrianHeight) {
        return pedestrianHeight.orElseGet(() -> getQuantity(170.0, CENTI(METRE)));
    }

    private static boolean isNightMode(final OsmandApplication app, final ApplicationMode mode) {
        return !app.getSettings().isLightContentForMode(mode);
    }

    private static View createView(final Context context) {
        return LayoutInflater
                .from(context)
                .inflate(R.layout.pref_dialog_height, null, false);
    }
}
