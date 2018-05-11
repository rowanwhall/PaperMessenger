package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperClearUserFromChatResponse;

public class PaperClearUserFromChatRequest
		extends BasePaperRequest<PaperClearUserFromChatResponse> {

	private final static String PATH = "clearUserFromChat";

	private String mChatId;
	private String mChatName;
	private String mUserId;
	private String mUserName;

	public PaperClearUserFromChatRequest(String chatId, String chatName, String userId, String userName) {
		mChatId = chatId;
		mChatName = chatName;
		mUserId = userId;
		mUserName = userName;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId, mChatName, mUserId, mUserName);
	}

	@Override
	protected PaperClearUserFromChatResponse buildResponse(String responseString, Exception e) {
		return new PaperClearUserFromChatResponse(responseString, e);
	}
}
