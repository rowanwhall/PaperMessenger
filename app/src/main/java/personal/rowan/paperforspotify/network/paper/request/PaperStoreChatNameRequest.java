package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperStoreChatNameResponse;

public class PaperStoreChatNameRequest
		extends BasePaperRequest<PaperStoreChatNameResponse> {

	private final static String PATH = "storeChatName";

	private String mChatId;
	private String mChatName;
	private String mUserId;
	private String mUserName;

	public PaperStoreChatNameRequest(String chatId, String chatName, String userId, String userName) {
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
	protected PaperStoreChatNameResponse buildResponse(String responseString, Exception e) {
		return new PaperStoreChatNameResponse(responseString, e);
	}

}
