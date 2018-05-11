package personal.rowan.paperforspotify.data.service;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;
import java.util.Calendar;

import javax.inject.Inject;

import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.network.imgur.ImgurUploadImageRequest;
import personal.rowan.paperforspotify.network.imgur.ImgurUploadImageResponse;
import personal.rowan.paperforspotify.network.paper.request.PaperStoreImageUrlRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperStoreImageUrlResponse;
import personal.rowan.paperforspotify.util.NotificationUtil;

public class ImageUploadService
		extends IntentService {

	@Inject
	PreferenceManager mPreferenceManager;

	public static final String ARGS_FILE_PATH = "ARGS_FILE_PATH";

	public ImageUploadService() {
		super("ImageUploadService");
		PaperApplication.getInstance().component().inject(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		File photoFile = (File) intent.getSerializableExtra(ARGS_FILE_PATH);

		if(photoFile == null) {
			return;
		}

		NotificationUtil.showImageUploadNotification();

		ImgurUploadImageResponse imgurResponse =
		new ImgurUploadImageRequest(mPreferenceManager.getUserId() + "_photo_" + Calendar.getInstance().getTimeInMillis(),
				"profile_photo", photoFile).executeOnThread();

		if(imgurResponse != null) {
			PaperStoreImageUrlResponse paperResponse =
					new PaperStoreImageUrlRequest(mPreferenceManager.getUserId(), imgurResponse.getLink())
					.executeOnThread();

			if(paperResponse != null) {
				mPreferenceManager.storeUser(paperResponse.getUserId(), paperResponse.getName(), paperResponse.getImageUrl());
				displaySuccess();
			} else {
				displayError();
			}
		} else {
			displayError();
		}
	}

	private void displayError() {
		NotificationUtil.showImageUploadResult(false);
	}

	private void displaySuccess() {
		NotificationUtil.showImageUploadResult(true);
	}

}
