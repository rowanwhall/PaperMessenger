package personal.rowan.paperforspotify.data.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Response;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperStoreRegIdRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperStoreRegIdResponse;

public class PaperRegistrationIntentService
		extends IntentService {

	private static final String TAG = "PaperRegistrationIntentService";
	private static final String SENDER_ID = "343757833104";

	@Inject
	PreferenceManager mPreferenceManager;

	public PaperRegistrationIntentService() {
		super(TAG);
		PaperApplication.getInstance().component().inject(this);
	}

	@Override
	public void onHandleIntent(Intent intent) {
		String token;
		try {
			token = InstanceID.getInstance(getApplicationContext()).getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
		} catch (IOException ex) {
			//Toast.makeText(getApplicationContext(), "Failed to obtain regId", Toast.LENGTH_SHORT).show();
			token = null;
		}
		sendTokenToPaper(token);
	}

	private void sendTokenToPaper(String token) {
		if(!TextUtils.isEmpty(token)) {
			//Toast.makeText(getApplicationContext(), "Successfully obtained regId", Toast.LENGTH_SHORT).show();
			new PaperStoreRegIdRequest(mPreferenceManager.getUserId(), token).execute(new NetworkCallback<PaperStoreRegIdResponse>() {
				@Override
				public void onResponse(Response response, PaperStoreRegIdResponse responseObject) {
					//Toast.makeText(getApplicationContext(), "Successfully stored regId", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(Exception e) {
					//Toast.makeText(getApplicationContext(), "Failed to store regId", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

}
