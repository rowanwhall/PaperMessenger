package personal.rowan.paperforspotify.data.loader;

import android.content.Context;
import android.support.annotation.IntDef;

import java.util.List;

import personal.rowan.paperforspotify.data.model.UserSearchResult;
import personal.rowan.paperforspotify.network.paper.request.PaperSearchFriendsRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperUserByChatIdRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperSearchFriendsResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperUserByChatIdResponse;

public class UserLoader
		extends BaseLoader<List<UserSearchResult>> {

	@IntDef({
			Type.SEARCH,
			Type.CHAT
	})
	public @interface Type {
		int SEARCH = 0;
		int CHAT = 1;
	}

	public static final String ARGS_TYPE = "ARGS_TYPE";
	public static final String ARGS_USER_ID = "ARGS_USER_ID";
	public static final String ARGS_SEARCH_TERM = "ARGS_SEARCH_TERM";
	public static final String ARGS_CHAT_ID = "ARGS_CHAT_ID";

	private int mType;

	// Search term parameters
	private String mUserId;
	private String mSearchTerm;

	// Chat parameters
	private String mChatId;

	public UserLoader(Context context, int type, String userId, String searchTerm) {
		super(context);
		mType = type;
		mUserId = userId;
		mSearchTerm = searchTerm;
	}

	public UserLoader(Context context, int type, String chatId) {
		super(context);
		mType = type;
		mChatId = chatId;
	}

	@Override
	protected boolean cacheInvalid() {
		switch(mType) {
			case Type.SEARCH:
				return super.cacheInvalid();
			case Type.CHAT:
				List<UserSearchResult> cache = getCache();
				return cache == null || cache.isEmpty();
		}
		return super.cacheInvalid();
	}

	@Override
	public List<UserSearchResult> loadInBackground() {
		switch(mType) {
			case Type.SEARCH:
				PaperSearchFriendsResponse searchResponse =
						new PaperSearchFriendsRequest(mUserId, mSearchTerm)
								.executeOnThread();

				return searchResponse == null ?
						null :
						searchResponse.getSearchResults();
			case Type.CHAT:
				PaperUserByChatIdResponse chatResponse =
						new PaperUserByChatIdRequest(mChatId)
								.executeOnThread();

				return chatResponse == null ?
						null :
						chatResponse.getUserSearchResults();
			default:
				return null;
		}
	}

	public static class ArgsBuilder
			extends BaseArgsBuilder {

		public ArgsBuilder(int type) {
			super();
			writeInt(ARGS_TYPE, type);
		}

		public ArgsBuilder setUserId(String userId) {
			writeString(ARGS_USER_ID, userId);
			return this;
		}

		public ArgsBuilder setSearchTerms(String searchTerms) {
			writeString(ARGS_SEARCH_TERM, searchTerms);
			return this;
		}

		public ArgsBuilder setChatId(String chatId) {
			writeString(ARGS_CHAT_ID, chatId);
			return this;
		}

	}

}
