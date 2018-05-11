package personal.rowan.paperforspotify.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.ui.fragment.dialog.BaseDialogFragment;

public class BaseActivity
		extends AppCompatActivity
		implements BaseDialogFragment.IDialogListener {

	private ProgressDialog mProgressDialog;

	protected ApplicationComponent component() {
		return PaperApplication.getInstance().component();
	}

	protected void startLoader(int loaderId, Bundle args, LoaderManager.LoaderCallbacks callbacks) {
		LoaderManager loaderManager = getSupportLoaderManager();
		Loader loader = loaderManager.getLoader(loaderId);
		if(loader == null) {
			loaderManager.initLoader(loaderId, args, callbacks);
		} else {
			loaderManager.restartLoader(loaderId, args, callbacks);
		}
	}

	protected void notifyLoaderContentChanged(int loaderId) {
		Loader loader = getSupportLoaderManager().getLoader(loaderId);
		if(loader != null) {
			loader.onContentChanged();
		}
	}

	protected void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showProgressDialog(String title, String message) {
		mProgressDialog = ProgressDialog.show(this, title, message, true);
	}

	public void dismissProgressDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}

	protected void setUpButton() {
		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	// Override these methods if the activity in question uses a BaseDialogFragment
	@Override
	public void onDialogPositiveClick(BaseDialogFragment dialogFragment) {

	}

	@Override
	public void onDialogNegativeClick(BaseDialogFragment dialogFragment) {

	}

	@Override
	public void onDialogNeutralClick(BaseDialogFragment dialogFragment) {

	}

	@Override
	public void onDialogDismiss(BaseDialogFragment dialogFragment) {

	}


}
