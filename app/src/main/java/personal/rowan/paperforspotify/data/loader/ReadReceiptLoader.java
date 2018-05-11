package personal.rowan.paperforspotify.data.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import javax.inject.Inject;

import personal.rowan.paperforspotify.data.loader.observer.ReadReceiptObserver;
import personal.rowan.paperforspotify.data.model.ReadReceipt;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.paper.request.PaperReadReceiptRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperReadReceiptResponse;

public class ReadReceiptLoader
		extends BaseLoader<ReadReceipt> {

	public static final String ARGS_CHAT_ID = "ARGS_CHAT_ID";
	public static final String ARGS_USER_ID = "ARGS_USER_ID";

	@Inject
	DatabaseManager mDatabaseManager;

	private String mChatId;
	private String mUserId;

	public ReadReceiptLoader(Context context, String chatId, String userId) {
		super(context);
		component().inject(this);

		mChatId = chatId;
		mUserId = userId;
	}

	@Override
	protected ReadReceipt getCache() {
		return mDatabaseManager.loadReadReceipt(mChatId);
	}

	@Override
	protected void saveToCache(ReadReceipt data) {
		mDatabaseManager.saveReadReceipt(data);
	}

	@Override
	protected boolean cacheInvalid() {
		return getCache() == null;
	}

	@Override
	protected BroadcastReceiver generateBroadcastReceiver() {
		return new ReadReceiptObserver(this);
	}

	@Override
	protected void unregisterReceiver(BroadcastReceiver receiver) {
		LocalBroadcastManager
				.getInstance(PaperApplication.getInstance())
				.unregisterReceiver(receiver);
	}

	@Override
	public ReadReceipt loadInBackground() {
		PaperReadReceiptResponse response =
				new PaperReadReceiptRequest(mChatId, mUserId)
				.executeOnThread();

		return response == null ?
				null :
				response.getReadReceipt();
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

	}
}
