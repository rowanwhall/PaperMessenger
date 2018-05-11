package personal.rowan.paperforspotify.data.loader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.loader.BaseLoader;

public abstract class BaseLoaderService<T>
		extends Service
		implements Loader.OnLoadCompleteListener<T> {

	private final IBinder mBinder = new LoaderServiceBinder();

	private BaseLoader<T> mLoader;
	private List<ILoaderService<T>> mCallbacks = new ArrayList<>();

	public class LoaderServiceBinder extends Binder {
		public BaseLoaderService getService() {
			// if the loader is null, the service has been created via bindService without starting, so return a null reference to indicate this
			return mLoader == null ? null :
					BaseLoaderService.this;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLoader = buildLoaderFromIntent(intent);
		mLoader.registerListener(loaderId(), this);
		mLoader.onContentChanged();
		mLoader.startLoadingWithoutReturningCache();

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		if(mLoader != null) {
			mLoader.unregisterListener(this);
			mLoader.stopLoading();
			mLoader = null;
		}
		mCallbacks.clear();
		super.onDestroy();
	}

	protected abstract BaseLoader<T> buildLoaderFromIntent(Intent intent);

	protected abstract int loaderId();

	protected void afterLoadComplete(T data) {

	}

	@Override
	public void onLoadComplete(Loader<T> loader, T data) {
		afterLoadComplete(data);
		for(ILoaderService<T> callback : mCallbacks) {
			callback.onLoadComplete(loader, data);
		}
		stopSelf();
	}

	public void registerListener(ILoaderService<T> callback) {
		mCallbacks.add(callback);
	}

	public void unregisterListener(ILoaderService<T> callback) {
		mCallbacks.remove(callback);
	}

	public interface ILoaderService<T> {
		void onLoadComplete(Loader<T> loader, T data);
	}
}
