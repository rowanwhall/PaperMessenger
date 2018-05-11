package personal.rowan.paperforspotify;

import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PaperApplication;

public class TestApplication
		extends PaperApplication {

	@Override
	protected ApplicationComponent createComponent() {
		return DaggerMainActivityTest_TestComponent.builder()
				.testApplicationModule(new TestApplicationModule(this))
				.build();
	}

}
