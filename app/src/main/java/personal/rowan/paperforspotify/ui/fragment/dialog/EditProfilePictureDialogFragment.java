package personal.rowan.paperforspotify.ui.fragment.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.network.PicassoCircleTransformation;

public class EditProfilePictureDialogFragment
		extends BaseDialogFragment {

	public static final String TAG = "EditProfilePictureDialog";

	@Inject
	PreferenceManager mPreferenceManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);
	}

	@Override
	protected int layoutResource() {
		return R.layout.dialog_frag_edit_profile_picture;
	}

	@Override
	protected void inflateLayout(View view) {
		ImageView ivCurrentProfilePhoto = (ImageView) view.findViewById(R.id.dialog_frag_edit_profile_picture_iv);
		Picasso.with(getContext())
				.load(mPreferenceManager.getUserImageUrl())
				.placeholder(R.drawable.ic_person_black_24dp)
				.transform(new PicassoCircleTransformation())
				.into(ivCurrentProfilePhoto);
	}

	@Override
	protected String title() {
		return getString(R.string.dialog_frag_edit_profile_picture_title);
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	protected String positiveButtonText() {
		return getString(R.string.dialog_frag_edit_profile_picture_positive_btn);
	}

	@Override
	protected String negativeButtonText() {
		return getString(R.string.dialog_frag_edit_profile_picture_negative_btn);
	}

	@Override
	protected String neutralButtonText() {
		return getString(R.string.dialog_frag_edit_profile_picture_neutral_btn);
	}
}
