package net.osmand.plus.quickaction.actions;

import static net.osmand.plus.quickaction.QuickActionIds.SHOW_HIDE_POI_ACTION_ID;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import net.osmand.plus.widgets.ctxmenu.ContextMenuAdapter;
import net.osmand.plus.widgets.ctxmenu.ViewCreator;
import net.osmand.plus.widgets.ctxmenu.data.ContextMenuItem;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.utils.UiUtilities;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.poi.PoiFiltersHelper;
import net.osmand.plus.poi.PoiUIFilter;
import net.osmand.plus.quickaction.QuickAction;
import net.osmand.plus.quickaction.QuickActionType;
import net.osmand.plus.render.RenderingIcons;
import net.osmand.util.Algorithms;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ShowHidePoiAction extends QuickAction {
	private static final int defaultActionNameId = R.string.poi;

	public static final QuickActionType TYPE = new QuickActionType(SHOW_HIDE_POI_ACTION_ID,
			"poi.showhide", ShowHidePoiAction.class)
			.nameActionRes(R.string.quick_action_show_hide_title)
			.nameRes(defaultActionNameId)
			.iconRes(R.drawable.ic_action_info_dark)
			.category(QuickActionType.CONFIGURE_MAP);

	public static final String KEY_FILTERS = "filters";

	private transient EditText title;

	public ShowHidePoiAction() {
		super(TYPE);
	}

	public ShowHidePoiAction(QuickAction quickAction) {
		super(quickAction);
	}

	@Override
	public String getActionText(@NonNull OsmandApplication app) {
		String actionName = isActionWithSlash(app) ? app.getString(R.string.shared_string_hide) : app.getString(R.string.shared_string_show);
		return app.getString(R.string.ltr_or_rtl_combine_via_dash, actionName, getName(app));
	}

	@Override
	public boolean isActionWithSlash(@NonNull OsmandApplication app) {

		return isCurrentFilters(app);
	}

	@Override
	public void setAutoGeneratedTitle(EditText title) {
		this.title = title;
	}

	@Override
	public int getIconRes(Context context) {
		String filtersIdsJson = getParams().get(KEY_FILTERS);
		List<String> filtersIds = tryParseFiltersIds(filtersIdsJson);
		if (Algorithms.isEmpty(filtersIds)) {
			return getIconRes();
		}

		OsmandApplication app = (OsmandApplication) context.getApplicationContext();
		PoiUIFilter filter = app.getPoiFilters().getFilterById(filtersIds.get(0));
		if (filter == null) {
			return getIconRes();
		}

		Object iconRes = filter.getIconResource();
		return (iconRes instanceof String && RenderingIcons.containsBigIcon(iconRes.toString()))
				? RenderingIcons.getBigIconResourceId(iconRes.toString())
				: getIconRes();
	}

	@Override
	public void execute(@NonNull MapActivity mapActivity) {
		mapActivity.getFragmentsHelper().closeQuickSearch();

		PoiFiltersHelper pf = mapActivity.getMyApplication().getPoiFilters();
		List<PoiUIFilter> poiFilters = loadPoiFilters(mapActivity.getMyApplication().getPoiFilters());
		if (!isCurrentFilters(pf.getSelectedPoiFilters(), poiFilters)) {
			pf.clearSelectedPoiFilters();
			for (PoiUIFilter filter : poiFilters) {
				if (filter.isStandardFilter()) {
					filter.removeUnsavedFilterByName();
				}
				pf.addSelectedPoiFilter(filter);
			}
		} else {
			pf.clearSelectedPoiFilters();
		}

		mapActivity.getMapLayers().updateLayers(mapActivity);
	}

	private boolean isCurrentFilters(OsmandApplication application) {
		PoiFiltersHelper pf = application.getPoiFilters();
		List<PoiUIFilter> poiFilters = loadPoiFilters(application.getPoiFilters());
		return isCurrentFilters(pf.getSelectedPoiFilters(), poiFilters);
	}

	private boolean isCurrentFilters(Set<PoiUIFilter> currentPoiFilters, List<PoiUIFilter> poiFilters) {
		return currentPoiFilters.size() == poiFilters.size() && currentPoiFilters.containsAll(poiFilters);
	}

	@Override
	public void drawUI(@NonNull ViewGroup parent, @NonNull MapActivity mapActivity) {
		boolean nightMode = mapActivity.getMyApplication().getDaynightHelper().isNightModeForMapControls();
		View view = UiUtilities.getInflater(mapActivity, nightMode).inflate(R.layout.quick_action_show_hide_poi, parent, false);

		RecyclerView list = view.findViewById(R.id.list);
		List<PoiUIFilter> poiFilters = loadPoiFilters(mapActivity.getMyApplication().getPoiFilters());
		Adapter adapter = new Adapter(poiFilters);
		list.setAdapter(adapter);

		Button addFilter = view.findViewById(R.id.btnAddCategory);
		addFilter.setOnClickListener(v -> showSingleChoicePoiFilterDialog(mapActivity, adapter));

		parent.addView(view);
	}

	public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

		private final List<PoiUIFilter> filters;

		public Adapter(List<PoiUIFilter> filters) {
			this.filters = filters;
		}

		private void addItem(PoiUIFilter filter) {

			if (!filters.contains(filter)) {

				filters.add(filter);
				savePoiFilters(filters);

				notifyDataSetChanged();
			}
		}

		@Override
		public Adapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {

			return new Adapter.Holder(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.quick_action_deletable_list_item, parent, false));
		}

		@Override
		public void onBindViewHolder(Adapter.Holder holder, int position) {

			PoiUIFilter filter = filters.get(position);

			Object res = filter.getIconResource();
			if (res instanceof String && RenderingIcons.containsBigIcon(res.toString())) {
				holder.icon.setImageResource(RenderingIcons.getBigIconResourceId(res.toString()));
			} else {
				holder.icon.setImageResource(R.drawable.mx_special_custom_category);
			}

			holder.title.setText(filter.getName());
			holder.delete.setOnClickListener(view -> {
				String oldTitle = getTitle(filters);

				filters.remove(position);
				savePoiFilters(filters);

				notifyDataSetChanged();

				String titleContent = title.getText().toString();
				if (oldTitle.equals(titleContent) || titleContent.equals(view.getContext().getString(defaultActionNameId))) {
					String newTitle = getTitle(filters);
					title.setText(newTitle);
				}
			});
		}

		@Override
		public int getItemCount() {
			return filters.size();
		}

		class Holder extends RecyclerView.ViewHolder {

			private final TextView title;
			private final ImageView icon;
			private final ImageView delete;

			public Holder(View itemView) {
				super(itemView);

				title = itemView.findViewById(R.id.title);
				icon = itemView.findViewById(R.id.icon);
				delete = itemView.findViewById(R.id.delete);
			}
		}
	}

	public void savePoiFilters(List<PoiUIFilter> poiFilters) {
		List<String> filters = new ArrayList<>();
		for (PoiUIFilter f : poiFilters) {
			filters.add(f.getFilterId());
		}

		JSONArray jsonArray = new JSONArray(filters);
		getParams().put(KEY_FILTERS, jsonArray.toString());
	}

	private List<PoiUIFilter> loadPoiFilters(PoiFiltersHelper helper) {
		String filtersIds = getParams().get(KEY_FILTERS);
		List<String> filters = tryParseFiltersIds(filtersIds);

		List<PoiUIFilter> poiFilters = new ArrayList<>();
		for (String f : filters) {
			PoiUIFilter filter = helper.getFilterById(f);
			if (filter != null) {
				poiFilters.add(filter);
			}
		}

		return poiFilters;
	}

	private List<String> tryParseFiltersIds(String filtersIdsString) {
		if (Algorithms.isBlank(filtersIdsString)) {
			return Collections.emptyList();
		}
		try {
			List<String> filtersIds = new ArrayList<>();
			JSONArray jsonArray = new JSONArray(filtersIdsString);
			for (int i = 0; i < jsonArray.length(); i++) {
				String filterId = jsonArray.getString(i);
				if (!Algorithms.isBlank(filterId)) {
					filtersIds.add(filterId);
				}
			}
			return filtersIds;
		} catch (JSONException e) {
			return Arrays.asList(filtersIdsString.split(","));
		}
	}

	private void showSingleChoicePoiFilterDialog(MapActivity mapActivity, Adapter filtersAdapter) {
		OsmandApplication app = mapActivity.getMyApplication();
		PoiFiltersHelper poiFilters = app.getPoiFilters();
		ContextMenuAdapter adapter = new ContextMenuAdapter(app);

		List<PoiUIFilter> list = new ArrayList<>();

		for (PoiUIFilter f : poiFilters.getSortedPoiFilters(true)) {
			if (!f.isCustomPoiFilter()) {
				addFilterToList(adapter, list, f);
			}
		}

		boolean nightMode = app.getDaynightHelper().isNightModeForMapControls();
		ViewCreator viewCreator = new ViewCreator(mapActivity, nightMode);
		ArrayAdapter<ContextMenuItem> listAdapter = adapter.toListAdapter(mapActivity, viewCreator);
		AlertDialog.Builder builder = new AlertDialog.Builder(UiUtilities.getThemedContext(mapActivity, nightMode));
		builder.setAdapter(listAdapter, (dialog, which) -> {
			String oldTitle = getTitle(filtersAdapter.filters);

			filtersAdapter.addItem(list.get(which));

			String titleContent = title.getText().toString();
			if (oldTitle.equals(titleContent) || titleContent.equals(mapActivity.getString(defaultActionNameId))) {
				String newTitle = getTitle(filtersAdapter.filters);
				title.setText(newTitle);
			}
		});
		builder.setTitle(R.string.show_poi_over_map);
		builder.setNegativeButton(R.string.shared_string_dismiss, null);

		AlertDialog alertDialog = builder.create();

		alertDialog.setOnShowListener(dialog -> {
			Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
			Drawable drawable = app.getUIUtilities().getThemedIcon(R.drawable.ic_action_multiselect);
			neutralButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		});

		alertDialog.show();
	}

	private String getTitle(List<PoiUIFilter> filters) {

		if (filters.isEmpty()) return "";

		return filters.size() > 1
				? filters.get(0).getName() + " +" + (filters.size() - 1)
				: filters.get(0).getName();
	}

	private void addFilterToList(ContextMenuAdapter adapter,
	                             List<PoiUIFilter> list,
	                             PoiUIFilter f) {
		list.add(f);
		ContextMenuItem item = new ContextMenuItem(null);

		item.setTitle(f.getName());

		if (RenderingIcons.containsBigIcon(f.getIconId())) {
			item.setIcon(RenderingIcons.getBigIconResourceId(f.getIconId()));
		} else {
			item.setIcon(R.drawable.mx_special_custom_category);
		}

		item.setUseNaturalIconColor(true);
		adapter.addItem(item);
	}

	@Override
	public boolean fillParams(@NonNull View root, @NonNull MapActivity mapActivity) {
		return !getParams().isEmpty() && (getParams().get(KEY_FILTERS) != null || !getParams().get(KEY_FILTERS).isEmpty());
	}
}
