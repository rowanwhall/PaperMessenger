package personal.rowan.paperforspotify.data.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.service.ChatLoaderService;
import personal.rowan.paperforspotify.data.loader.service.FriendLoaderService;
import personal.rowan.paperforspotify.data.loader.service.MessageLoaderService;
import personal.rowan.paperforspotify.data.loader.service.ReadReceiptLoaderService;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.util.StringUtil;

public class PaperGcmListenerService
		extends GcmListenerService {

	@Inject
	PreferenceManager mPreferenceManager;

	private static final String PUSH_KEY_MESSAGE = "PUSH_KEY_MESSAGE";
	private static final String PUSH_KEY_CHAT = "PUSH_KEY_CHAT";
	private static final String PUSH_KEY_CHAT_DETAIL = "PUSH_KEY_CHAT_DETAIL";
	private static final String PUSH_KEY_FRIEND_REQUEST = "PUSH_KEY_FRIEND_REQUEST";
	private static final String PUSH_KEY_FRIEND_ACCEPT = "PUSH_KEY_FRIEND_ACCEPT";
	private static final String PUSH_KEY_READ_RECEIPT = "PUSH_KEY_READ_RECEIPT";

	public static final String ACTION_UPDATE_MESSAGE = "ACTION_UPDATE_MESSAGE_";
	public static final String ACTION_UPDATE_CHAT = "ACTION_UPDATE_CHAT";
	public static final String ACTION_UPDATE_CHAT_DETAIL = "ACTION_UPDATE_CHAT_DETAIL_";
	public static final String ACTION_UPDATE_FRIEND = "ACTION_UPDATE_FRIEND";
	public static final String ACTION_UPDATE_READ_RECEIPT = "ACTION_UPDATE_READ_RECEIPT";

	@Override
	public void onCreate() {
		super.onCreate();
		PaperApplication.getInstance().component().inject(this);
	}

	@Override
	public void onMessageReceived(String from, Bundle data) {
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
				.getInstance(PaperApplication.getInstance());
		String userId = mPreferenceManager.getUserId();

		String pushKey = StringUtil.urlDecode(data.getString("key"));
		switch(pushKey) {
			case PUSH_KEY_MESSAGE:
				String chatName = StringUtil.urlDecode(data.getString("title"));
				String chatIdMessage = StringUtil.urlDecode(data.getString("senderId"));

				if (!TextUtils.isEmpty(chatIdMessage)) {
					// broadcast sent to specific chat
					if (appNotInForeground() ||
							!localBroadcastManager
									.sendBroadcast(new Intent(ACTION_UPDATE_MESSAGE + chatIdMessage))) {
						if(!TextUtils.isEmpty(userId)) {
							startMessageLoaderService(chatIdMessage, userId, chatName, mPreferenceManager.getSettingPushNotifications());
						}
					}

					// broadcast sent to update master list of chats
					if (!localBroadcastManager
							.sendBroadcast(new Intent(ACTION_UPDATE_CHAT)) ||
							appNotInForeground()) {
						startChatLoaderServiceForMasterUpdate(userId);
					}
				}
				break;
			case PUSH_KEY_CHAT:
				String initiatorName = StringUtil.urlDecode(data.getString("message"));
				String chatId = StringUtil.urlDecode(data.getString("senderId"));

				if(!TextUtils.isEmpty(chatId)) {
					if (appNotInForeground() ||
							!localBroadcastManager
							.sendBroadcast(new Intent(ACTION_UPDATE_CHAT))) {
						if(!TextUtils.isEmpty(userId)) {
							startChatLoaderServiceForChatNotification(userId, initiatorName, chatId, mPreferenceManager.getSettingPushNotifications());
						}
					}
				}
				break;
			case PUSH_KEY_CHAT_DETAIL:
				initiatorName = StringUtil.urlDecode(data.getString("message"));
				chatId = StringUtil.urlDecode(data.getString("senderId"));

				if(!TextUtils.isEmpty(chatId)) {
					if(appNotInForeground() ||
							!localBroadcastManager
							.sendBroadcast(new Intent(ACTION_UPDATE_CHAT_DETAIL + chatId))) {
						startChatLoaderServiceForChatDetail(chatId, initiatorName, mPreferenceManager.getSettingPushNotifications());
					}
				}
				break;
			case PUSH_KEY_FRIEND_REQUEST:
			case PUSH_KEY_FRIEND_ACCEPT:
				String name = StringUtil.urlDecode(data.getString("message"));
				boolean accept = pushKey.equals(PUSH_KEY_FRIEND_ACCEPT);
				if(!localBroadcastManager
						.sendBroadcast(new Intent(ACTION_UPDATE_FRIEND)) ||
						appNotInForeground()) {
					if (!TextUtils.isEmpty(userId)) {
						startFriendLoaderService(userId, name, accept, mPreferenceManager.getSettingPushNotifications());
					}
				}
				break;
			case PUSH_KEY_READ_RECEIPT:
				String chatIdToRefresh = StringUtil.urlDecode(data.getString("message"));

				if(!TextUtils.isEmpty(chatIdToRefresh)) {
					if(!localBroadcastManager
							.sendBroadcast(new Intent(ACTION_UPDATE_READ_RECEIPT)) ||
							appNotInForeground()) {
						if(!TextUtils.isEmpty(userId)) {
							startReadReceiptLoaderService(chatIdToRefresh, userId);
						}
					}
				}
				break;
		}
	}

	private void startMessageLoaderService(String chatId, String userId, String chatName, boolean notificationsEnabled) {
		Intent messageLoadingIntent = new Intent(getApplicationContext(), MessageLoaderService.class);
		messageLoadingIntent.putExtra(MessageLoaderService.ARGS_CHAT_ID, chatId);
		messageLoadingIntent.putExtra(MessageLoaderService.ARGS_USER_ID, userId);
		messageLoadingIntent.putExtra(MessageLoaderService.ARGS_NOTIFICATION_CHAT_NAME, chatName);
		messageLoadingIntent.putExtra(MessageLoaderService.ARGS_NOTIFICATIONS_ENABLED, notificationsEnabled);
		startService(messageLoadingIntent);
	}

	private void startChatLoaderServiceForChatNotification(String userId, String initiatorId, String chatId, boolean notificationsEnabled) {
		Intent chatLoadingIntent = new Intent(getApplicationContext(), ChatLoaderService.class);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_TYPE, ChatLoader.Type.USER_ID);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_ID, userId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATION_CREATOR_NAME, initiatorId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATION_CHAT_ID, chatId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATIONS_ENABLED, notificationsEnabled);
		startService(chatLoadingIntent);
	}

	private void startChatLoaderServiceForMasterUpdate(String userId) {
		Intent chatLoadingIntent = new Intent(getApplicationContext(), ChatLoaderService.class);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_TYPE, ChatLoader.Type.USER_ID);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_ID, userId);
		startService(chatLoadingIntent);
	}

	private void startChatLoaderServiceForChatDetail(String chatId, String initiatorId, boolean notificationsEnabled) {
		Intent chatLoadingIntent = new Intent(getApplicationContext(), ChatLoaderService.class);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_TYPE, ChatLoader.Type.CHAT_ID);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_ID, chatId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATION_CREATOR_NAME, initiatorId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATION_CHAT_ID, chatId);
		chatLoadingIntent.putExtra(ChatLoaderService.ARGS_NOTIFICATIONS_ENABLED, notificationsEnabled);
		startService(chatLoadingIntent);
	}

	private void startFriendLoaderService(String userId, String name, boolean accept, boolean notificationsEnabled) {
		Intent friendLoadingIntent = new Intent(getApplicationContext(), FriendLoaderService.class);
		friendLoadingIntent.putExtra(FriendLoaderService.ARGS_USER_ID, userId);
		friendLoadingIntent.putExtra(FriendLoaderService.ARGS_NOTIFICATION_NAME, name);
		friendLoadingIntent.putExtra(FriendLoaderService.ARGS_NOTIFICATION_ACCEPTANCE, accept);
		friendLoadingIntent.putExtra(FriendLoaderService.ARGS_NOTIFICATIONS_ENABLED, notificationsEnabled);
		startService(friendLoadingIntent);
	}

	private void startReadReceiptLoaderService(String chatId, String userId) {
		Intent readReceiptLoadingIntent = new Intent(getApplicationContext(), ReadReceiptLoaderService.class);
		readReceiptLoadingIntent.putExtra(ReadReceiptLoaderService.ARGS_CHAT_ID, chatId);
		readReceiptLoadingIntent.putExtra(ReadReceiptLoaderService.ARGS_USER_ID, userId);
		startService(readReceiptLoadingIntent);
	}

	private boolean appNotInForeground() {
		return !PaperLifecycleHandler.isApplicationInForeground() &&
				!PaperLifecycleHandler.isApplicationVisible();
	}

}
