package net.osmand.plus.card.color.cstyle;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.osmand.plus.R;
import net.osmand.plus.routepreparationmenu.cards.BaseCard;

public class ColoringStyleDetailsCard extends BaseCard {

	private final IColoringStyleDetailsController controller;

	public ColoringStyleDetailsCard(@NonNull FragmentActivity activity,
	                                @NonNull IColoringStyleDetailsController controller,
	                                boolean usedOnMap) {
		super(activity, usedOnMap);
		this.controller = controller;
		controller.bindCard(this);
	}

	@Override
	public int getCardLayoutId() {
		return R.layout.card_coloring_style_details;
	}

	@Override
	protected void updateContent() {
		if (controller.shouldHideCard()) {
			updateVisibility(false);
			return;
		}
		updateVisibility(true);

		updateVisibility(R.id.upper_space, controller.shouldShowUpperSpace());
		updateVisibility(R.id.bottom_space, controller.shouldShowBottomSpace());

		updateVisibility(R.id.slope_legend, controller.shouldShowSlopeLegend());
		updateVisibility(R.id.speed_altitude_legend, controller.shouldShowSpeedAltitudeLegend());

		updateDescription();
		updateLegend();
	}

	private void updateDescription() {
		TextView tvDescription = view.findViewById(R.id.description);
		String description = controller.getTypeDescription();
		if (description != null) {
			tvDescription.setText(description);
		}
		updateVisibility(tvDescription, description != null);
	}

	private void updateLegend() {
		CharSequence[] legendHeadlines = controller.getLegendHeadlines();
		if (legendHeadlines != null) {
			TextView minValue = view.findViewById(R.id.min_value);
			TextView maxValue = view.findViewById(R.id.max_value);
			minValue.setText(legendHeadlines[0]);
			maxValue.setText(legendHeadlines[1]);
		}
	}
}
