package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperAddSongToChatResponse;

public class PaperAddSongToChatRequest
		extends BasePaperRequest<PaperAddSongToChatResponse> {

	private final static String PATH = "addSongToChat";

	private String mChatId;
	private String mUri;
	private String mUrl;
	private String mSongImageUrl;
	private String mSongName;
	private String mArtistName;
	private String mUserId;
	private String mUserName;

	public PaperAddSongToChatRequest(String chatId, String uri, String url, String imageUrl, String name, String artistName, String userId, String userName) {
		mChatId = chatId;
		mUri = uri;
		mUrl = url;
		mSongImageUrl = imageUrl;
		mSongName = name;
		mArtistName = artistName;
		mUserId = userId;
		mUserName = userName;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId, mUri, mUrl, mSongImageUrl, mSongName, mArtistName, mUserId, mUserName);
	}

	@Override
	protected PaperAddSongToChatResponse buildResponse(String responseString, Exception e) {
		return new PaperAddSongToChatResponse(responseString, e);
	}

}
