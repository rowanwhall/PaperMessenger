package personal.rowan.paperforspotify.data.loader.service;

import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

import personal.rowan.paperforspotify.data.loader.BaseLoader;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.loader.MessageLoader;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.util.NotificationUtil;

public class MessageLoaderService
		extends BaseLoaderService<List<ChatMessage>> {

	private static final String ADMINISTRATOR_ID = "ADMINISTRATOR_ID";

	public static final String ARGS_CHAT_ID = "ARGS_NOTIFICATION_CHAT_ID";
	public static final String ARGS_USER_ID = "ARGS_USER_ID";
	// notification args
	public static final String ARGS_NOTIFICATION_CHAT_NAME = "ARGS_NOTIFICATION_CHAT_NAME";
	public static final String ARGS_NOTIFICATIONS_ENABLED = "ARGS_NOTIFICATIONS_ENABLED";

	private String mChatId;
	private String mUserId;
	private String mNotificationChatName;
	private boolean mNotificationsEnabled;

	@Override
	protected BaseLoader<List<ChatMessage>> buildLoaderFromIntent(Intent intent) {
		mChatId = intent.getStringExtra(ARGS_CHAT_ID);
		mUserId = intent.getStringExtra(ARGS_USER_ID);
		mNotificationChatName = intent.getStringExtra(ARGS_NOTIFICATION_CHAT_NAME);
		mNotificationsEnabled = intent.getBooleanExtra(ARGS_NOTIFICATIONS_ENABLED, true);
		// doNotMark set to true because the messages are being loaded in the background
		return new MessageLoader(getApplicationContext(), mChatId, mUserId, true);
	}

	@Override
	protected int loaderId() {
		return LoaderId.MESSAGE;
	}

	public String getChatId() {
		return mChatId;
	}

	public String getUserId() {
		return mUserId;
	}

	@Override
	protected void afterLoadComplete(List<ChatMessage> data) {
		if(mNotificationsEnabled &&
				!TextUtils.isEmpty(mChatId) && data != null && !data.isEmpty()) {
			ChatMessage message = data.get(data.size() - 1);
			if(!ADMINISTRATOR_ID.equals(message.getUserId())) {
				NotificationUtil.showMessageNotification(message.getFirstName() + ": " + message.getMessage(), mNotificationChatName, mChatId);
			}
		}
	}

}
