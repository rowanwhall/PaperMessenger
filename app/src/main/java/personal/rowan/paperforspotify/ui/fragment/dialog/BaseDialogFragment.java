package personal.rowan.paperforspotify.ui.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.dagger.ApplicationComponent;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.ui.activity.BaseActivity;
import personal.rowan.paperforspotify.ui.fragment.BaseFragment;

public abstract class BaseDialogFragment
		extends DialogFragment {

	private IDialogListener mDialogListener;
	private boolean mShownFromFragment;

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

	protected void showMessage(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
		if (mShownFromFragment) {
			Fragment fragment = getParentFragment();
			if (fragment != null && fragment instanceof IDialogListener) {
				mDialogListener = (IDialogListener) fragment;
			} else {
				throw new IllegalStateException("The parent fragment cannot be null and must implement IDialogFragmentListener");
			}
		} else {
			if (activity != null && activity instanceof IDialogListener) {
				mDialogListener = (IDialogListener) activity;
			} else {
				throw new IllegalStateException("The parent activity cannot be null and must implement IDialogFragmentListener");
			}
		}

		View view = activity.getLayoutInflater().inflate(layoutResource(), null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setView(view)
				.setTitle(title());

		String positiveButtonText = positiveButtonText();

		if (positiveButtonText != null) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (mDialogListener != null) {
								mDialogListener.onDialogPositiveClick(BaseDialogFragment.this);
							}
						}
					});
		}

		String negativeButtonText = negativeButtonText();

		if (negativeButtonText != null) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mDialogListener != null) {
								mDialogListener.onDialogNegativeClick(BaseDialogFragment.this);
							}
						}
					});
		}

		String neutralButtonText = neutralButtonText();

		if (neutralButtonText != null) {
			builder.setNeutralButton(neutralButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mDialogListener != null) {
								mDialogListener.onDialogNeutralClick(BaseDialogFragment.this);
							}
						}
					});
		}

		inflateLayout(view);

		final AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				Button positiveButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
				if (positiveButton != null) {
					positiveButton.setTextColor(ContextCompat.getColor(getContext(), positiveButtonColor()));
				}
				Button negativeButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
				if (negativeButton != null) {
					negativeButton.setTextColor(ContextCompat.getColor(getContext(), negativeButtonColor()));
				}
				Button neutralButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL);
				if (neutralButton != null) {
					neutralButton.setTextColor(ContextCompat.getColor(getContext(), neutralButtonColor()));
				}
				dialog.setCancelable(cancelable());

				afterShown();
			}
		});
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		if (mDialogListener != null) {
			mDialogListener.onDialogDismiss(BaseDialogFragment.this);
		}

		super.onDismiss(dialogInterface);
	}

	public void show(BaseActivity activity) {
		mShownFromFragment = false;
		show(activity.getSupportFragmentManager(), tag());
	}

	@SuppressWarnings("unused")
	public void show(BaseFragment fragment) {
		mShownFromFragment = true;
		show(fragment.getChildFragmentManager(), tag());
	}

	protected abstract int layoutResource();

	protected abstract void inflateLayout(View view);

	protected abstract String title();

	public abstract String tag();

	protected String positiveButtonText() {
		return null;
	}

	protected String negativeButtonText() {
		return null;
	}

	protected String neutralButtonText() {
		return null;
	}

	protected int positiveButtonColor() {
		return R.color.colorButton;
	}

	protected int negativeButtonColor() {
		return R.color.colorButton;
	}

	protected int neutralButtonColor() {
		return R.color.colorButton;
	}

	protected Button getPositiveButton() {
		AlertDialog d = (AlertDialog) getDialog();
		if(d != null) {
			return d.getButton(DialogInterface.BUTTON_POSITIVE);
		}
		return null;
	}

	protected boolean cancelable() {
		return true;
	}

	//Can be overriden to interact with the dialog after it has been shown. Useful for interacting with UI elements that are not yet inflated in inflateLayout(View)
	protected void afterShown() {}

	public interface IDialogListener {
		void onDialogPositiveClick(BaseDialogFragment dialogFragment);

		void onDialogNegativeClick(BaseDialogFragment dialogFragment);

		void onDialogNeutralClick(BaseDialogFragment dialogFragment);

		void onDialogDismiss(BaseDialogFragment dialogFragment);
	}

}

