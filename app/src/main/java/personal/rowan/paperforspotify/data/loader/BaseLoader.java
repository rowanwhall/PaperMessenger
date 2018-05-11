package personal.rowan.paperforspotify.data.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.content.AsyncTaskLoader;

import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PaperApplication;

public abstract class BaseLoader<T>
		extends AsyncTaskLoader<T> {

	public BaseLoader(Context context) {
		super(context);
	}

	private T mCache;
	private BroadcastReceiver mReceiver;
	protected boolean mSkipCache;

	protected ApplicationComponent component() {
		return PaperApplication.getInstance().component();
	}

	// subclasses should implement this if they have their own cache, i.e. database or preferences
	protected T getCache() {
		return mCache;
	}

	// subclasses should implement this if they have their own cache, i.e. database or preferences
	protected void saveToCache(T data) {
		mCache = data;
	}

	// subclasses should implement this if they have their own cache, i.e. database or preferences
	protected boolean cacheInvalid() {
		return mCache == null;
	}

	// subclasses should implement this if want a receiver to listen to loader changes
	protected BroadcastReceiver generateBroadcastReceiver() {
		return null;
	}

	// subclasses should implement this if their receiver uses a different context than the loader
	protected void unregisterReceiver(BroadcastReceiver receiver) {
		getContext().unregisterReceiver(receiver);
	}

	@Override
	protected void onStartLoading() {
		T cache = getCache();
		if(!mSkipCache && !cacheInvalid()) {
			deliverResult(cache);
		}

		if(mReceiver == null) {
			mReceiver = generateBroadcastReceiver();
		}

		if(takeContentChanged() || cacheInvalid()) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();

		T cache = getCache();
		if(cache != null) {
			releaseResources(cache);
		}

		if(mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	public void deliverResult(T data) {
		if(isReset()) {
			releaseResources(data);
			return;
		}

		T oldData = getCache();
		saveToCache(data);

		if(isStarted()) {
			super.deliverResult(getCache());
		}

		if(oldData != null && oldData != data) {
			releaseResources(oldData);
		}
	}

	@Override
	public void onCanceled(T data) {
		super.onCanceled(data);
		releaseResources(data);
	}

	public void startLoadingWithoutReturningCache() {
		mSkipCache = true;
		startLoading();
	}

	// subclasses should implement this if anything needs to be done to release resources for its data
	// for example, a Cursor would be closed in this method
	@CallSuper
	protected void releaseResources(@SuppressWarnings("UnusedParameters") T data) {
		mCache = null;
	}

	public static abstract class BaseArgsBuilder {

		private Bundle mBundle;

		protected BaseArgsBuilder() {
			// always call through to super
			mBundle = new Bundle();
		}

		protected void writeString(String key, String value) {
			mBundle.putString(key, value);
		}

		protected void writeInt(String key, int value) {
			mBundle.putInt(key, value);
		}

		protected void writeBoolean(String key, boolean value) {
			mBundle.putBoolean(key, value);
		}

		public Bundle build() {
			return mBundle;
		}
	}

}
