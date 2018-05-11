package personal.rowan.paperforspotify.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.ui.activity.BaseActivity;

public class BaseFragment
		extends Fragment {

	protected ApplicationComponent component() {
		return PaperApplication.getInstance().component();
	}

	protected void startLoader(int loaderId, Bundle args, LoaderManager.LoaderCallbacks callbacks) {
		LoaderManager loaderManager = getLoaderManager();
		Loader loader = loaderManager.getLoader(loaderId);
		if(loader == null) {
			loaderManager.initLoader(loaderId, args, callbacks);
		} else {
			loaderManager.restartLoader(loaderId, args, callbacks);
		}
	}

	protected void notifyLoaderContentChanged(int loaderId) {
		Loader loader = getLoaderManager().getLoader(loaderId);
		if(loader != null) {
			loader.onContentChanged();
		}
	}

	protected void showMessage(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}

	protected void showProgressDialog(String title, String message) {
		Activity activity = getActivity();
		if(activity != null && activity instanceof BaseActivity) {
			((BaseActivity) activity).showProgressDialog(title, message);
		}
	}

	protected void dismissProgressDialog() {
		Activity activity = getActivity();
		if(activity != null && activity instanceof BaseActivity) {
			((BaseActivity) activity).dismissProgressDialog();
		}
	}

}
