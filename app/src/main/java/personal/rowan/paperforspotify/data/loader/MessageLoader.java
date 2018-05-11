package personal.rowan.paperforspotify.data.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.data.loader.observer.MessageObserver;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.paper.request.PaperGetChatMessagesRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperGetChatMessagesResponse;

public class MessageLoader
		extends BaseLoader<List<ChatMessage>> {

	public static final String ARGS_CHAT_ID = "ARGS_NOTIFICATION_CHAT_ID";
	public static final String ARGS_USER_ID = "ARGS_USER_ID";
	public static final String ARGS_DO_NOT_MARK = "ARGS_DO_NOT_MARK";

	@Inject
	DatabaseManager mDatabaseManager;

	private String mChatId;
	private String mUserId;
	private boolean mDoNotMark;

	public MessageLoader(Context context, String chatId, String userId, boolean doNotMark) {
		super(context);
		component().inject(this);

		mChatId = chatId;
		mUserId = userId;
		mDoNotMark = doNotMark;
	}

	@Override
	protected List<ChatMessage> getCache() {
		return mDatabaseManager.loadChatMessages(mChatId);
	}

	@Override
	protected void saveToCache(List<ChatMessage> data) {
		mDatabaseManager.saveChatMessages(data);
	}

	@Override
	protected boolean cacheInvalid() {
		List<ChatMessage> cache = getCache();
		return cache == null ||
				cache.isEmpty();
	}

	@Override
	protected BroadcastReceiver generateBroadcastReceiver() {
		return new MessageObserver(this, mChatId);
	}

	@Override
	protected void unregisterReceiver(BroadcastReceiver receiver) {
		LocalBroadcastManager
				.getInstance(PaperApplication.getInstance())
				.unregisterReceiver(receiver);
	}

	@Override
	public List<ChatMessage> loadInBackground() {
		List<ChatMessage> cache = getCache();
		long timestamp = cache.isEmpty() ? 0 :
				cache.get(cache.size() - 1).getTimestamp();
		PaperGetChatMessagesResponse response =
				new PaperGetChatMessagesRequest(mChatId, timestamp, mUserId, mDoNotMark)
				.executeOnThread();

		if(response == null) {
			return null;
		}

		List<ChatMessage> messages = getCache();
		return messages.addAll(response.getMessages()) ?
				messages :
				null;

	}

	public static class ArgsBuilder
			extends BaseArgsBuilder {

		public ArgsBuilder(String chatId) {
			super();
			writeString(ARGS_CHAT_ID, chatId);
		}

		public ArgsBuilder setUserId(String userId) {
			writeString(ARGS_USER_ID, userId);
			return this;
		}

		public ArgsBuilder setDoNotMark(boolean doNotMark) {
			writeBoolean(ARGS_DO_NOT_MARK, doNotMark);
			return this;
		}

	}
}
