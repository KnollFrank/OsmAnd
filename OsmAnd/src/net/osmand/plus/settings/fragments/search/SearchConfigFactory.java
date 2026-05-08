package net.osmand.plus.settings.fragments.search;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;

import net.osmand.plus.settings.backend.preferences.OsmandPreference;

import de.KnollFrank.lib.settingssearch.client.SearchConfig;
import de.KnollFrank.lib.settingssearch.common.Keyboard;

class SearchConfigFactory {

	public static SearchConfig createSearchConfig(final FragmentActivity fragmentActivity,
												  final @IdRes int fragmentContainerViewId,
												  final OsmandPreference<String> availableAppModes) {
		final EnabledProfilesSearchResultsFilter searchResultsFilter =
				SearchResultsFilterFactory.createSearchResultsFilter(
						PreferencePathDisplayerFactory.getApplicationModeKeys(),
						availableAppModes);
		return SearchConfig
				.builder(
						fragmentContainerViewId,
						fragmentActivity,
						() -> navigateToInitialPreferenceScreen(fragmentActivity))
				.withQueryHint("Search Settings")
				.withSearchResultsFilter(searchResultsFilter)
				.withPreferencePathDisplayer(PreferencePathDisplayerFactory.createPreferencePathDisplayer(fragmentActivity))
				.withSearchPreferenceFragmentUI(new SearchPreferenceFragmentUI(searchResultsFilter))
				.withSearchResultsFragmentUI(new SearchResultsFragmentUI())
				.withShowSettingsFragmentAndHighlightSetting(new ShowSettingsFragmentAndHighlightSetting())
				.build();
	}

	private static void navigateToInitialPreferenceScreen(final FragmentActivity fragmentActivity) {
		fragmentActivity.runOnUiThread(() -> {
			Keyboard.hideKeyboard(fragmentActivity);
			fragmentActivity.getOnBackPressedDispatcher().onBackPressed();
		});
	}
}
