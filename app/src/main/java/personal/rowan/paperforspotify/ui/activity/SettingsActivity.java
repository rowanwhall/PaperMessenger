package personal.rowan.paperforspotify.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.service.ImageUploadService;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperStoreNameRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperStoreNameResponse;
import personal.rowan.paperforspotify.ui.fragment.BaseSupportPreferenceFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.BaseDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.EditProfilePictureDialogFragment;

public class SettingsActivity
		extends BaseActivity {

	private static final int PERMISSION_REQUEST_CAMERA = 1;
	private static final int ACTIVITY_REQUEST_CODE_CAMERA = 1;
	private static final int ACTIVITY_REQUEST_CODE_GALLERY = 2;
	private static final String TEMP_FILE_NAME = "PAPER_TEMP_PHOTO_FILE";
	private File mPhotoFile;

	@Inject
	PreferenceManager mPreferenceManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_settings);
		component().inject(this);

		Toolbar tbMain = (Toolbar) findViewById(R.id.act_settings_tb);
		setSupportActionBar(tbMain);
		setUpButton();
		setTitle(getString(R.string.act_settings_tb_title));

		if(savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.act_settings_container_fl, new SettingsFragment())
					.commit();
		}
	}

	public static class SettingsFragment
			extends BaseSupportPreferenceFragment
			implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

		private static final String KEY_NAME = "settings_preference_change_name";
		private static final String KEY_IMAGE = "settings_preference_change_image";
		private static final String KEY_PUSH_NOTIFICATIONS = "settings_preference_push_notifications";

		@Inject
		PreferenceManager mPreferenceManager;

		private EditTextPreference mNamePreference;
		private SwitchPreference mNotificationsPreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			component().inject(this);

			addPreferencesFromResource(R.xml.settings_preference);
			assignPreferenceChangeListeners(getPreferenceScreen(), this);
			assignClickListeners(getPreferenceScreen(), this);

			mNamePreference = (EditTextPreference) findPreference(KEY_NAME);
			mNotificationsPreference = (SwitchPreference) findPreference(KEY_PUSH_NOTIFICATIONS);
			initPreferences();
		}

		private void initPreferences() {
			mNamePreference.setSummary(mPreferenceManager.getUserName());
			mNotificationsPreference.setChecked(mPreferenceManager.getSettingPushNotifications());
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			switch(preference.getKey()) {
				case KEY_PUSH_NOTIFICATIONS:
					mPreferenceManager.storeSettingPushNotifications((boolean) newValue);
					break;
				case KEY_NAME:
					String name = ((String) newValue);
					if(!TextUtils.isEmpty(name)) {
						showProgressDialog(getString(R.string.progress_paper_store_name), getString(R.string.progress_dialog_generic_message));
						new PaperStoreNameRequest(mPreferenceManager.getUserId(), name)
								.execute(new NetworkCallback<PaperStoreNameResponse>() {
									@Override
									public void onResponse(Response response, PaperStoreNameResponse responseObject) {
										SettingsFragment.this.dismissProgressDialog();
										String newName = responseObject.getName();
										mPreferenceManager.storeUser(responseObject.getUserId(), newName, responseObject.getImageUrl());
										mNamePreference.setSummary(newName);
									}

									@Override
									public void onFailure(Exception e) {
										SettingsFragment.this.dismissProgressDialog();
										showMessage(getString(R.string.error_network_store_name_paper));
									}
								});
					}
					break;
			}

			return true;
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {

			switch(preference.getKey()) {
				case KEY_IMAGE:
					new EditProfilePictureDialogFragment()
							.show((SettingsActivity) getActivity());
					break;
			}

			return true;
		}
	}

	@Override
	public void onDialogPositiveClick(BaseDialogFragment dialogFragment) {
		switch (dialogFragment.tag()) {
			case EditProfilePictureDialogFragment.TAG:
				openCamera();
				break;
		}
	}

	@Override
	public void onDialogNegativeClick(BaseDialogFragment dialogFragment) {
		switch (dialogFragment.tag()) {
			case EditProfilePictureDialogFragment.TAG:
				openGallery();
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch(requestCode) {
			case PERMISSION_REQUEST_CAMERA:
				for(int grant : grantResults) {
					if(PermissionChecker.PERMISSION_DENIED == grant) {
						new AlertDialog.Builder(this)
								.setTitle(getString(R.string.act_settings_camera_permissions_denied_dialog_title))
								.setMessage(getString(R.string.act_settings_camera_permissions_denied_dialog_message))
								.show();
						return;
					}
				}

				openCamera();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ACTIVITY_REQUEST_CODE_CAMERA:
				if(resultCode == Activity.RESULT_OK) {
					uploadPhotoFile();
				}
				break;
			case ACTIVITY_REQUEST_CODE_GALLERY:
				if(resultCode == Activity.RESULT_OK) {
					Uri imageUri = data.getData();
					InputStream inputStream;
					try {
						inputStream = getContentResolver().openInputStream(imageUri);
					} catch (FileNotFoundException ingored) {
						showMessage(getString(R.string.error_paper_file_not_found));
						return;
					}
					mPhotoFile = new File(getCacheDir(), TEMP_FILE_NAME + ".png");
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, new BitmapFactory.Options());
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
					byte[] bitmapData = byteArrayOutputStream.toByteArray();

					FileOutputStream fileOutputStream;

					try {
						fileOutputStream = new FileOutputStream(mPhotoFile);
						fileOutputStream.write(bitmapData);
						fileOutputStream.flush();
						fileOutputStream.close();
					} catch (IOException ignored) {
						showMessage(getString(R.string.error_paper_file_not_found));
						return;
					}
					uploadPhotoFile();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void openCamera() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
				(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
			requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
			return;
		}

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(cameraIntent.resolveActivity(getPackageManager()) != null) {
			try {
				mPhotoFile = File.createTempFile(TEMP_FILE_NAME, ".png", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
				startActivityForResult(cameraIntent, ACTIVITY_REQUEST_CODE_CAMERA);
			} catch(IOException ignored) {
				showMessage(getString(R.string.error_paper_file_creation));
			}
		} else {
			showMessage(getString(R.string.error_paper_camera_not_found));
		}
	}

	private void openGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_GALLERY);
	}

	private void uploadPhotoFile() {
		if(mPhotoFile == null) {
			showMessage(getString(R.string.error_paper_file_not_found));
			return;
		}

		Intent intent = new Intent(this, ImageUploadService.class);
		intent.putExtra(ImageUploadService.ARGS_FILE_PATH, mPhotoFile);
		startService(intent);
	}

}
