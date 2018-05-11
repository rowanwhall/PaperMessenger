package personal.rowan.paperforspotify.data.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.data.loader.observer.ChatObserver;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.paper.request.PaperChatByIdRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperGetChatsRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperChatByIdResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperGetChatsResponse;

public class ChatLoader
		extends BaseLoader<List<Chat>> {

	@IntDef({
			Type.USER_ID,
			Type.CHAT_ID
	})
	public @interface Type {
		int USER_ID = 0;
		int CHAT_ID = 1;
	}

	public static final String ARGS_TYPE = "ARGS_TYPE";
	public static final String ARGS_ID = "ARGS_ID";

	@Inject
	DatabaseManager mDatabaseManager;

	private String mUserId;
	private String mChatId;
	@Type
	private int mType;

	public ChatLoader(Context context, int type, String id) {
		super(context);
		component().inject(this);

		mType = type;
		switch(mType) {
			case Type.USER_ID:
				mUserId = id;
				break;
			case Type.CHAT_ID:
				mChatId = id;
				break;
		}
	}

	@Override
	protected List<Chat> getCache() {
		switch(mType) {
			case Type.USER_ID:
				List<Chat> chats = mDatabaseManager.loadChats();
				Collections.sort(chats, new Comparator<Chat>() {
					@Override
					public int compare(Chat o1, Chat o2) {
						if(o1.getTimestamp() > o2.getTimestamp()) {
							return -1;
						} else if(o1.getTimestamp() < o2.getTimestamp()) {
							return 1;
						} else {
							return 0;
						}
					}
				});
				return chats;
			case Type.CHAT_ID:
				return mDatabaseManager.loadChat(mChatId);
			default:
				return null;
		}
	}

	@Override
	protected void saveToCache(List<Chat> data) {
		mDatabaseManager.saveChats(data);
	}

	@Override
	protected boolean cacheInvalid() {
		List<Chat> cache = getCache();
		return cache == null ||
				cache.isEmpty();
	}

	@Override
	protected BroadcastReceiver generateBroadcastReceiver() {
		switch(mType) {
			case Type.USER_ID:
				return new ChatObserver(this);
			case Type.CHAT_ID:
				return new ChatObserver(this, mChatId);
			default:
				return null;
		}
	}

	@Override
	protected void unregisterReceiver(BroadcastReceiver receiver) {
		LocalBroadcastManager
				.getInstance(PaperApplication.getInstance())
				.unregisterReceiver(receiver);
	}

	@Override
	public List<Chat> loadInBackground() {
		switch(mType) {
			case Type.USER_ID:
				PaperGetChatsResponse chatsResponse = new PaperGetChatsRequest(mUserId)
						.executeOnThread();

				return chatsResponse == null ?
						null :
						chatsResponse.getChats();
			case Type.CHAT_ID:
				PaperChatByIdResponse chatByIdResponse = new PaperChatByIdRequest(mChatId)
						.executeOnThread();

				List<Chat> chats = new ArrayList<>();
				chats.add(chatByIdResponse.getChat());
				return chats;
			default:
				return null;

		}
	}

	public static class ArgsBuilder
			extends BaseArgsBuilder {

		public ArgsBuilder(@Type int type) {
			super();
			writeInt(ARGS_TYPE, type);
		}

		public ArgsBuilder setId(String id) {
			writeString(ARGS_ID, id);
			return this;
		}

	}
}
