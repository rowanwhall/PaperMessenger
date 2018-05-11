package personal.rowan.paperforspotify.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.gcm.PaperRegistrationIntentService;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperLoginRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperLoginResponse;
import personal.rowan.paperforspotify.network.spotify.request.SpotifyGetUserRequest;
import personal.rowan.paperforspotify.network.spotify.response.SpotifyGetUserResponse;

public class LoginActivity
		extends SpotifyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		if(!TextUtils.isEmpty(mPreferenceManager.getUserId())) {
			navigateToMainActivityAndFinish();
			return;
		}

		Button loginButton = (Button) findViewById(R.id.act_login_btn);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSpotifyManager.authenticate(LoginActivity.this);
			}
		});
	}

	private void navigateToMainActivityAndFinish() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	private void paperLogin(String userId, String displayName, String imageUrl) {
		new PaperLoginRequest(userId, displayName, imageUrl)
				.execute(new NetworkCallback<PaperLoginResponse>() {
					@Override
					public void onResponse(Response response, PaperLoginResponse responseObject) {
						Intent intent = new Intent(LoginActivity.this, PaperRegistrationIntentService.class);
						startService(intent);

						dismissProgressDialog();
						String userId = responseObject.getUserId();

						mPreferenceManager.storeUser(userId, responseObject.getName(), responseObject.getImageUrl());

						navigateToMainActivityAndFinish();
					}

					@Override
					public void onFailure(Exception e) {
						dismissProgressDialog();
						showMessage(getString(R.string.error_network_login_paper));
					}
				});
	}

	@Override
	public void onAuthenticateSuccess() {
		//If this is the same user logging in, show cached data while we load their data in the background
		//Otherwise, block them with a progress dialog until we've verified their identity
		final String cachedUserId = mPreferenceManager.getUserId();
		if(TextUtils.isEmpty(cachedUserId)) {
			showProgressDialog(getString(R.string.progress_spotify_login), getString(R.string.progress_dialog_generic_message));
		}

		new SpotifyGetUserRequest(mPreferenceManager.getSpotifyAccessToken())
				.execute(new NetworkCallback<SpotifyGetUserResponse>() {
					@Override
					public void onResponse(Response response, SpotifyGetUserResponse responseObject) {
						dismissProgressDialog();
						String spotifyId = responseObject.getUserId();
						if (TextUtils.isEmpty(cachedUserId) || !cachedUserId.equals(spotifyId)) {
							showProgressDialog(getString(R.string.progress_paper_login), getString(R.string.progress_dialog_generic_message));
						}
						String displayName = responseObject.getDisplayName();
						if(TextUtils.isEmpty(displayName) || displayName.equals("null")) {
							displayName = spotifyId;
						}
						mPreferenceManager.storeSpotifyPremiumAccess("premium".equals(responseObject.getUserType()));

						paperLogin(spotifyId, displayName, responseObject.getImageUrl());
					}

					@Override
					public void onFailure(Exception e) {
						dismissProgressDialog();
						showMessage(getString(R.string.error_network_login_spotify));
					}
				});
	}

}
