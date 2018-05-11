package personal.rowan.paperforspotify.manager;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.dagger.ApplicationModule;
import personal.rowan.paperforspotify.data.gcm.PaperLifecycleHandler;

public class PaperApplication
		extends Application {

	private static PaperApplication instance;

	private ApplicationComponent mApplicationComponent;

	public static synchronized PaperApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mApplicationComponent = createComponent();
		registerActivityLifecycleCallbacks(new PaperLifecycleHandler());
	}

	@Singleton
	@Component(modules = ApplicationModule.class)
	public interface PaperComponent
			extends ApplicationComponent {
	}

	protected ApplicationComponent createComponent() {
		return DaggerPaperApplication_PaperComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
	}

	public ApplicationComponent component() {
		return mApplicationComponent;
	}

}
