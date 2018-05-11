package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperSendTextOnlyMessageResponse;

public class PaperSendTextOnlyMessageRequest
		extends BasePaperRequest<PaperSendTextOnlyMessageResponse> {

	private final static String PATH = "addTextOnlyMessageToChat";

	private String mMessageId;
	private String mChatId;
	private String mUserId;
	private String mChatName;
	private String mName;
	private String mImageUrl;
	private String mMessage;

	public PaperSendTextOnlyMessageRequest(String messageId, String chatId, String userId, String chatName, String name, String imageUrl, String message) {
		mMessageId = messageId;
		mChatId = chatId;
		mUserId = userId;
		mChatName = chatName;
		mName = name;
		mImageUrl = imageUrl;
		mMessage = message;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mMessageId, mChatId, mUserId, mChatName, mName, mImageUrl, mMessage);
	}

	@Override
	protected PaperSendTextOnlyMessageResponse buildResponse(String responseString, Exception e) {
		return new PaperSendTextOnlyMessageResponse(responseString, e);
	}
}
