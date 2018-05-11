package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperSendSongMessageResponse;

public class PaperSendSongMessageRequest
		extends BasePaperRequest<PaperSendSongMessageResponse> {

	private final static String PATH = "addMessageToChat";

	private String mMessageId;
	private String mChatId;
	private String mUserId;
	private String mChatName;
	private String mName;
	private String mImageUrl;
	private String mMessage;
	private String mUri;
	private String mUrl;
	private String mSongImageUrl;
	private String mSongName;
	private String mArtistName;

	public PaperSendSongMessageRequest(String messageId, String chatId, String userId, String chatName, String name, String imageUrl, String message, String uri, String url, String songImageUrl, String songName, String artistName) {
		mMessageId = messageId;
		mChatId = chatId;
		mUserId = userId;
		mChatName = chatName;
		mName = name;
		mImageUrl = imageUrl;
		mMessage = message;
		mUri = uri;
		mUrl = url;
		mSongImageUrl = songImageUrl;
		mSongName = songName;
		mArtistName = artistName;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mMessageId, mChatId, mUserId, mChatName, mName, mImageUrl, mMessage, mUri, mUrl, mSongImageUrl, mSongName, mArtistName);
	}

	@Override
	protected PaperSendSongMessageResponse buildResponse(String responseString, Exception e) {
		return new PaperSendSongMessageResponse(responseString, e);
	}
}
