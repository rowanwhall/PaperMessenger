package personal.rowan.paperforspotify.data.loader.service;

import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

import personal.rowan.paperforspotify.data.loader.BaseLoader;
import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.util.NotificationUtil;

public class ChatLoaderService
		extends BaseLoaderService<List<Chat>> {

	public static final String ARGS_TYPE = "ARGS_TYPE";
	public static final String ARGS_ID = "ARGS_ID";
	// notification args
	public static final String ARGS_NOTIFICATION_CREATOR_NAME = "ARGS_NOTIFICATION_CREATOR_NAME";
	public static final String ARGS_NOTIFICATION_CHAT_ID = "ARGS_NOTIFICATION_CHAT_ID";
	public static final String ARGS_NOTIFICATIONS_ENABLED = "ARGS_NOTIFICATIONS_ENABLED";

	private int mType;
	private String mNotificationInitiatorName;
	private String mNotificationChatId;
	private boolean mNotificationsEnabled;

	@Override
	protected BaseLoader<List<Chat>> buildLoaderFromIntent(Intent intent) {
		mType = intent.getIntExtra(ARGS_TYPE, ChatLoader.Type.USER_ID);
		String id = intent.getStringExtra(ARGS_ID);
		mNotificationInitiatorName = intent.getStringExtra(ARGS_NOTIFICATION_CREATOR_NAME);
		mNotificationChatId = intent.getStringExtra(ARGS_NOTIFICATION_CHAT_ID);
		mNotificationsEnabled = intent.getBooleanExtra(ARGS_NOTIFICATIONS_ENABLED, true);
		return new ChatLoader(getApplicationContext(), mType, id);
	}

	@Override
	protected int loaderId() {
		return LoaderId.CHAT;
	}

	@Override
	protected void afterLoadComplete(List<Chat> data) {
		if(mNotificationsEnabled && data != null && !data.isEmpty()) {
			switch(mType) {
				case ChatLoader.Type.USER_ID:
					for (Chat chat : data) {
						if (chat.getChatId().equals(mNotificationChatId)) {
							NotificationUtil.showChatNotification(TextUtils.isEmpty(mNotificationInitiatorName) ? chat.getCreatorName() : mNotificationInitiatorName,
									chat.getChatName(),
									mNotificationChatId,
									chat.getTrack());
							return;
						}
					}
					break;
			}
		}
	}

}
