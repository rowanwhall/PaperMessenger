package personal.rowan.paperforspotify.data.loader.service;

import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

import personal.rowan.paperforspotify.data.loader.BaseLoader;
import personal.rowan.paperforspotify.data.loader.FriendLoader;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.util.NotificationUtil;

public class FriendLoaderService
		extends BaseLoaderService<List<Friend>> {

	public static final String ARGS_USER_ID = "ARGS_USER_ID";
	// notification args
	public static final String ARGS_NOTIFICATION_NAME = "ARGS_NOTIFICATION_NAME";
	public static final String ARGS_NOTIFICATION_ACCEPTANCE = "ARGS_NOTIFICATION_ACCEPTANCE";
	public static final String ARGS_NOTIFICATIONS_ENABLED = "ARGS_NOTIFICATIONS_ENABLED";

	private String mUserId;
	private String mNotificationName;
	private boolean mNotificationAcceptance;
	private boolean mNotificationsEnabled;

	@Override
	protected BaseLoader<List<Friend>> buildLoaderFromIntent(Intent intent) {
		mUserId = intent.getStringExtra(ARGS_USER_ID);
		mNotificationName = intent.getStringExtra(ARGS_NOTIFICATION_NAME);
		mNotificationAcceptance = intent.getBooleanExtra(ARGS_NOTIFICATION_ACCEPTANCE, false);
		mNotificationsEnabled = intent.getBooleanExtra(ARGS_NOTIFICATIONS_ENABLED, true);
		return new FriendLoader(getApplicationContext(), mUserId);
	}

	@Override
	protected int loaderId() {
		return LoaderId.FRIEND;
	}

	public String getUserId() {
		return mUserId;
	}

	@Override
	protected void afterLoadComplete(List<Friend> data) {
		if(!TextUtils.isEmpty(mNotificationName) && data != null && !data.isEmpty()) {
			NotificationUtil.showFriendNotification(mNotificationName, mNotificationAcceptance);
		}
	}

}
