package personal.rowan.paperforspotify.data.loader;

import android.content.Context;
import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;

import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;

@Singleton
public class LoaderFactory {

	private static LoaderFactory sInstance;

	@Inject
	PreferenceManager mPreferenceManager;

	public static LoaderFactory getInstance() {
		if(sInstance == null) {
			sInstance = new LoaderFactory();
		}

		return sInstance;
	}

	private LoaderFactory() {
		PaperApplication.getInstance().component().inject(this);
	}

	public MessageLoader createMessageLoader(Context context, Bundle args) {
		String chatId = args.getString(MessageLoader.ARGS_CHAT_ID);
		String userId = args.getString(MessageLoader.ARGS_USER_ID, mPreferenceManager.getUserId());
		boolean doNotMark = args.getBoolean(MessageLoader.ARGS_DO_NOT_MARK);
		return new MessageLoader(context, chatId, userId, doNotMark);
	}

	public ChatLoader createChatLoader(Context context, Bundle args) {
		@ChatLoader.Type int type = args.getInt(ChatLoader.ARGS_TYPE);
		String id = args.getString(ChatLoader.ARGS_ID);
		return new ChatLoader(context, type, id);
	}

	public FriendLoader createFriendLoader(Context context, Bundle args) {
		String userId = args.getString(MessageLoader.ARGS_USER_ID, mPreferenceManager.getUserId());
		return new FriendLoader(context, userId);
	}

	public UserLoader createUserLoader(Context context, Bundle args) {
		@UserLoader.Type int type = args.getInt(UserLoader.ARGS_TYPE);
		switch(type) {
			case UserLoader.Type.SEARCH:
				String userId = args.getString(UserLoader.ARGS_USER_ID);
				String searchTerm = args.getString(UserLoader.ARGS_SEARCH_TERM);
				return new UserLoader(context, type, userId, searchTerm);
			case UserLoader.Type.CHAT:
				String chatId = args.getString(UserLoader.ARGS_CHAT_ID);
				return new UserLoader(context, type, chatId);
			default:
				return null;
		}
	}

	public ReadReceiptLoader createReadReceiptLoader(Context context, Bundle args) {
		String chatId = args.getString(ReadReceiptLoader.ARGS_CHAT_ID);
		String userId = args.getString(ReadReceiptLoader.ARGS_USER_ID);
		return new ReadReceiptLoader(context, chatId, userId);
	}

	public SpotifySongLoader createSpotifySongLoader(Context context, Bundle args) {
		String accessToken = args.getString(SpotifySongLoader.ARGS_SPOTIFY_TOKEN);
		String searchTerms = args.getString(SpotifySongLoader.ARGS_SEARCH_TERMS);
		return new SpotifySongLoader(context, accessToken, searchTerms);
	}

}
