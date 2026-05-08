package net.osmand.plus.settings.fragments.search;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.osmand.plus.activities.MapActivity;
import net.osmand.test.common.AndroidTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InteractiveDevelopmentTest extends AndroidTest {

	@Rule
	public ActivityScenarioRule<MapActivity> activityRule = new ActivityScenarioRule<>(MapActivity.class);

	@Test
	public void runAppForDevelopment() throws InterruptedException {
		keepAppOpen();
	}

	private static void keepAppOpen() throws InterruptedException {
		while (true) {
			Thread.sleep(1000);
		}
	}
}