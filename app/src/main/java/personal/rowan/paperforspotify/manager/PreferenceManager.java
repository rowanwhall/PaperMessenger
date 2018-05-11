package personal.rowan.paperforspotify.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

@Singleton
public class PreferenceManager {

	private final static String KEY_SHARED_PREFERENCES = "KEY_PAPER_FOR_SPOTIFY_PREFERENCES";

	private final static String KEY_USER_ID = "KEY_USER_ID";
	private final static String KEY_USER_NAME = "KEY_USER_NAME";
	private final static String KEY_USER_IMAGE_URL = "KEY_USER_IMAGE_URL";

	private static final String KEY_SETTING_PUSH_NOTIFICATIONS = "KEY_SETTING_PUSH_NOTIFICATIONS";

	private final static String KEY_SPOTIFY_ACCESS_TOKEN = "KEY_SPOTIFY_ACCESS_TOKEN";
	private final static String KEY_SPOTIFY_PREMIUM_ACCESS = "KEY_SPOTIFY_PREMIUM_ACCESS";

	private static PreferenceManager sInstance;
	private static SharedPreferences sSharedPreferences;

	private PreferenceManager() {
		sSharedPreferences = PaperApplication.getInstance()
				.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}

	private void initializePreferences() {
		if(sSharedPreferences == null) {
			sSharedPreferences = PaperApplication.getInstance()
					.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		}
	}

	public static PreferenceManager getInstance() {
		if(sInstance == null) {
			sInstance = new PreferenceManager();
		}

		return sInstance;
	}

	@SuppressLint("CommitPrefEdits")
	public void clearPreferences() {
		initializePreferences();
		sSharedPreferences.edit().clear().commit();
	}

	public void storeUser(String userId, String userName, String userImageUrl) {
		putString(KEY_USER_ID, userId);
		putString(KEY_USER_NAME, userName);
		putString(KEY_USER_IMAGE_URL, userImageUrl);
	}

	public void storeSettingPushNotifications(boolean enabled) {
		putBoolean(KEY_SETTING_PUSH_NOTIFICATIONS, enabled);
	}

	public boolean getSettingPushNotifications() {
		return getBoolean(KEY_SETTING_PUSH_NOTIFICATIONS, true);
	}

	public void storeSpotifyAccessToken(String accessToken) {
		putString(KEY_SPOTIFY_ACCESS_TOKEN, accessToken);
	}

	public void storeSpotifyPremiumAccess(boolean isPremium) {
		putBoolean(KEY_SPOTIFY_PREMIUM_ACCESS, isPremium);
	}

	public boolean hasSpotifyPremiumAccess() {
		return getBoolean(KEY_SPOTIFY_PREMIUM_ACCESS, false);
	}

	public String getUserId() {
		return getString(KEY_USER_ID);
	}

	public String getUserName() {
		return getString(KEY_USER_NAME);
	}

	public String getUserImageUrl() {
		return getString(KEY_USER_IMAGE_URL);
	}

	public String getSpotifyAccessToken() {
		return getString(KEY_SPOTIFY_ACCESS_TOKEN);
	}

	private void putString(String key, String value) {
		initializePreferences();
		SharedPreferences.Editor editor = sSharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	private String getString(String key) {
		initializePreferences();
		return sSharedPreferences.getString(key, null);
	}

	private void putBoolean(String key, boolean value) {
		initializePreferences();
		SharedPreferences.Editor editor = sSharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	private boolean getBoolean(String key, boolean fallback) {
		initializePreferences();
		return sSharedPreferences.getBoolean(key, fallback);
	}


}
