package personal.rowan.paperforspotify.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.manager.SpotifyManager;

@Module
public class ApplicationModule {

	private PaperApplication mApplication;

	public ApplicationModule(PaperApplication application) {
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
		return PreferenceManager.getInstance();
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
		return SpotifyManager.getInstance();
	}

	@Provides
	@Singleton
	@NonNull
	LoaderFactory providesLoaderFactory() {
		return LoaderFactory.getInstance();
	}

}
