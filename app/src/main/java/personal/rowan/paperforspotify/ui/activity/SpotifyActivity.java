package personal.rowan.paperforspotify.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.manager.SpotifyManager;

public class SpotifyActivity
		extends BaseActivity {

	@Inject
	protected PreferenceManager mPreferenceManager;
	@Inject
	protected SpotifyManager mSpotifyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch(requestCode) {
			case SpotifyManager.REQUEST_CODE:
				AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
				switch(response.getType()) {
					case TOKEN:
						mPreferenceManager.storeSpotifyAccessToken(response.getAccessToken());
						mSpotifyManager.initializePlayer(this);
						onAuthenticateSuccess();
						break;
					case ERROR:
					default:
						onAuthenticateFailure();
						break;
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, intent);
		}
	}

	public void onAuthenticateSuccess() {
		//Override for use as callback
	}

	public void onAuthenticateFailure() {
		//Override for use as callback
		dismissProgressDialog();
		showMessage(getString(R.string.error_network_authenticate_spotify));
		mSpotifyManager.authenticate(this);
	}

	public void onInitializePlayerSuccess() {
		//Override for use as callback
	}

	public void onInitializePlayerFailure() {
		//Override for use as callback
		dismissProgressDialog();
		showMessage(getString(R.string.error_spotify_player_initialization));
		mSpotifyManager.authenticate(this);
	}

	@Override
	protected void onDestroy() {
		// VERY IMPORTANT! This must always be called or else you will leak resources
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}
}
