package personal.rowan.paperforspotify.data.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.data.loader.observer.FriendObserver;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.paper.request.PaperGetFriendsRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperGetFriendsResponse;

public class FriendLoader
		extends BaseLoader<List<Friend>> {

	private static final String ARGS_USER_ID = "ARGS_USER_ID";

	@Inject
	DatabaseManager mDatabaseManager;

	private String mUserId;

	public FriendLoader(Context context, String userId) {
		super(context);
		component().inject(this);

		mUserId = userId;
	}

	@Override
	protected List<Friend> getCache() {
		return mDatabaseManager.loadFriends();
	}

	@Override
	protected void saveToCache(List<Friend> data) {
		mDatabaseManager.saveFriends(data);
	}

	@Override
	protected boolean cacheInvalid() {
		List<Friend> cache = getCache();
		return cache == null ||
				cache.isEmpty();
	}

	@Override
	protected BroadcastReceiver generateBroadcastReceiver() {
		return new FriendObserver(this);
	}

	@Override
	protected void unregisterReceiver(BroadcastReceiver receiver) {
		LocalBroadcastManager
				.getInstance(PaperApplication.getInstance())
				.unregisterReceiver(receiver);
	}

	@Override
	public List<Friend> loadInBackground() {
		PaperGetFriendsResponse response =
				new PaperGetFriendsRequest(mUserId)
						.executeOnThread();

		return response == null ?
				null :
				response.getFriends();
	}

	public static class ArgsBuilder
			extends BaseArgsBuilder {

		public ArgsBuilder() {
			super();
		}

		public ArgsBuilder setUserId(String userId) {
			writeString(ARGS_USER_ID, userId);
			return this;
		}

	}
}
