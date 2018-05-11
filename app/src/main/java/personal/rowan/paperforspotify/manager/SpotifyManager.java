package personal.rowan.paperforspotify.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import javax.inject.Inject;
import javax.inject.Singleton;

import personal.rowan.paperforspotify.ui.activity.SpotifyActivity;

@Singleton
public class SpotifyManager
		implements PlayerNotificationCallback, ConnectionStateCallback {

	private static final String CLIENT_ID = "7a8ab55d37764c4589779c63734258e6";
	private static final String REDIRECT_URI = "paperforspotify://callback";

	public static final String ERROR_CODE_UNAUTHORIZED = "401";

	@Inject
	PreferenceManager mPreferenceManager;

	private static SpotifyManager instance;
	private Player mPlayer;
	private String mUriToPlay;
	private PlayerNotificationCallback mPlayerCallbacksToSet;

	public static final int REQUEST_CODE = 1337;

	private SpotifyManager() {
		PaperApplication.getInstance().component().inject(this);
	}

	public static SpotifyManager getInstance() {
		if(instance == null) {
			instance = new SpotifyManager();
		}

		return instance;
	}

	public void authenticate(SpotifyActivity spotifyContext) {
		AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
		builder.setScopes(new String[]{"user-read-private", "streaming"});
		AuthenticationRequest request = builder.build();

		AuthenticationClient.openLoginActivity(spotifyContext, REQUEST_CODE, request);
	}

	public void logout(Context context) {
		AuthenticationClient.clearCookies(context);
	}

	public void initializePlayer(final SpotifyActivity spotifyContext) {
		Config playerConfig = new Config(spotifyContext, mPreferenceManager.getSpotifyAccessToken(), CLIENT_ID);
		mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
			@Override
			public void onInitialized(Player player) {
				if(!TextUtils.isEmpty(mUriToPlay)) {
					mPlayer.play(mUriToPlay);
					mUriToPlay = null;
				}
				if(mPlayerCallbacksToSet != null) {
					mPlayer.addPlayerNotificationCallback(mPlayerCallbacksToSet);
					mPlayerCallbacksToSet = null;
				}
				mPlayer.addConnectionStateCallback(SpotifyManager.this);
				mPlayer.addPlayerNotificationCallback(SpotifyManager.this);
				spotifyContext.onInitializePlayerSuccess();
			}

			@Override
			public void onError(Throwable throwable) {
				mUriToPlay = null;
				spotifyContext.onInitializePlayerFailure();
				Log.e(this.getClass().getName(), "Could not initialize player: " + throwable.getMessage());
			}
		});
	}

	public void play(String songUri, SpotifyActivity spotifyContext) {
		if(mPlayer != null) {
			mPlayer.play(songUri);
		} else {
			mUriToPlay = songUri;
			initializePlayer(spotifyContext);
		}
	}

	public void resume(SpotifyActivity spotifyContext) {
		if(mPlayer != null) {
			mPlayer.resume();
		} else {
			initializePlayer(spotifyContext);
		}
	}

	public void pause(SpotifyActivity spotifyContext) {
		if(mPlayer != null) {
			mPlayer.pause();
		} else {
			initializePlayer(spotifyContext);
		}
	}

	public void addPlayerCallbacks(SpotifyActivity spotifyContext, PlayerNotificationCallback callback) {
		if(mPlayer != null && mPlayer.isInitialized()) {
			mPlayer.addPlayerNotificationCallback(callback);
		} else {
			mPlayerCallbacksToSet = callback;
			initializePlayer(spotifyContext);
		}
	}

	@Override
	public void onLoggedIn() {
		Log.d(this.getClass().getName(), "User logged in");
	}

	@Override
	public void onLoggedOut() {
		Log.d(this.getClass().getName(), "User logged out");
	}

	@Override
	public void onLoginFailed(Throwable error) {
		Log.d(this.getClass().getName(), "Login failed");
	}

	@Override
	public void onTemporaryError() {
		Log.d(this.getClass().getName(), "Temporary error occurred");
	}

	@Override
	public void onConnectionMessage(String message) {
		Log.d(this.getClass().getName(), "Received connection message: " + message);
	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Log.d(this.getClass().getName(), "Playback event received: " + eventType.name());
		switch (eventType) {
			// Handle event type as necessary
			default:
				break;
		}
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String errorDetails) {
		Log.d(this.getClass().getName(), "Playback error received: " + errorType.name());
		switch (errorType) {
			// Handle error type as necessary
			default:
				break;
		}
	}
}
