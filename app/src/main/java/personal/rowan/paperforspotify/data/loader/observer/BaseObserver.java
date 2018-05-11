package personal.rowan.paperforspotify.data.loader.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import personal.rowan.paperforspotify.data.loader.BaseLoader;
import personal.rowan.paperforspotify.manager.PaperApplication;

public abstract class BaseObserver<T extends BaseLoader>
		extends BroadcastReceiver {

	private T mLoader;

	public BaseObserver(T loader, String... actions) {
		mLoader = loader;

		IntentFilter intentFilter = new IntentFilter();
		for(String action : actions) {
			intentFilter.addAction(action);
		}

		LocalBroadcastManager
				.getInstance(PaperApplication.getInstance())
				.registerReceiver(this, intentFilter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		mLoader.onContentChanged();
		// if loader exists but is stopped, start it so that data is cached immediately
		if(!mLoader.isStarted()) {
			mLoader.startLoadingWithoutReturningCache();
		}
	}

}
