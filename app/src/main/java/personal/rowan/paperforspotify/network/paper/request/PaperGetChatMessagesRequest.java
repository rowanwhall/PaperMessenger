package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperGetChatMessagesResponse;

public class PaperGetChatMessagesRequest
		extends BasePaperRequest<PaperGetChatMessagesResponse> {

	private final static String PATH = "messagesByChatUpdate";

	private String mChatId;
	private long mTimestamp;
	private String mUserId;
	private String mDoNotMark;

	public PaperGetChatMessagesRequest(String chatId, long timestamp, String userId, boolean doNotMark) {
		mChatId = chatId;
		mTimestamp = timestamp;
		mUserId = userId;
		mDoNotMark = doNotMark ? "1" : "0";
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId, String.valueOf(mTimestamp), mUserId, mDoNotMark);
	}

	@Override
	protected PaperGetChatMessagesResponse buildResponse(String responseString, Exception e) {
		return new PaperGetChatMessagesResponse(responseString, e);
	}
}
