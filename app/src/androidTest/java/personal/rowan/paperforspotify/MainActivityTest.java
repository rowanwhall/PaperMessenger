package personal.rowan.paperforspotify;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.manager.SpotifyManager;
import personal.rowan.paperforspotify.ui.activity.FriendSearchActivity;
import personal.rowan.paperforspotify.ui.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest  {

	@Inject
	PreferenceManager mPreferenceManager;
	@Inject
	SpotifyManager mSpotifyManager;

	@Singleton
	@Component(modules = TestApplicationModule.class)
	public interface TestComponent
			extends ApplicationComponent {
		void inject(MainActivityTest mainActivityTest);
	}

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, true, false);

	@Before
	public void setup() {
		Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
		TestApplication app = (TestApplication) instrumentation.getTargetContext().getApplicationContext();
		((TestComponent) app.component()).inject(this);
	}

	@Test
	public void testStartFriendSearchActivity() {
		mActivityRule.launchActivity(null);
		onView(withId(R.id.act_main_vp)).perform(ViewActions.swipeLeft());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		init();
		onView(withId(R.id.act_main_fab_friends)).perform(ViewActions.click());
		intended(hasComponent(FriendSearchActivity.class.getName()));
		release();
	}

}