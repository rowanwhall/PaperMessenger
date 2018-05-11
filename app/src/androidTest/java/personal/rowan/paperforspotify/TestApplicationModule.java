package personal.rowan.paperforspotify;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.manager.SpotifyManager;

import static org.mockito.Mockito.mock;

@Module
public class TestApplicationModule {

	private TestApplication mApplication;

	public TestApplicationModule(TestApplication application) {
		mApplication = application;
	}

	@Provides
	@Singleton
	@NonNull
	PaperApplication providesPaperApplication() {
		return mApplication;
	}

	@Provides
	@Singleton
	@NonNull
	PreferenceManager providesPreferenceManager() {
		return mock(PreferenceManager.class);
	}

	@Provides
	@Singleton
	@NonNull
	DatabaseManager providesDatabaseManager() {
		return DatabaseManager.getInstance();
	}

	@Provides
	@Singleton
	@NonNull
	SpotifyManager providesSpotifyManager() {
		return mock(SpotifyManager.class);
	}

	@Provides
	@Singleton
	@NonNull
	LoaderFactory providesLoaderFactory() {
		return LoaderFactory.getInstance();
	}

}

