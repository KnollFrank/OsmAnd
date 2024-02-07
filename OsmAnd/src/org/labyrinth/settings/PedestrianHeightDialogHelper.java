package org.labyrinth.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.utils.UiUtilities;

import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class PedestrianHeightDialogHelper {

    public static void showPedestrianHeightDialog(final OsmandApplication app,
                                                  final ApplicationMode mode,
                                                  final Context context) {
        PedestrianHeightDialogHelper
                .createPedestrianHeightDialog(
                        Height.fromQuantity(mode.getPedestrianHeight()),
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
                .setPositiveButton(
                        R.string.shared_string_ok,
                        (dialog, which) -> onOkButtonClicked.accept(pedestrianHeightDialog.getHeight().toQuantity()))
                .setNegativeButton(R.string.shared_string_cancel, null)
                .create();
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
