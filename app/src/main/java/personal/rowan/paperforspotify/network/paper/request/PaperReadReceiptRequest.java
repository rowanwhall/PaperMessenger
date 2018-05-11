package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperReadReceiptResponse;

public class PaperReadReceiptRequest
		extends BasePaperRequest<PaperReadReceiptResponse> {

	private final static String PATH = "readReceiptMessageForChat";

	private String mChatId;
	private String mUserId;

	public PaperReadReceiptRequest(String chatId, String userId) {
		mChatId = chatId;
		mUserId = userId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId, mUserId);
	}

	@Override
	protected PaperReadReceiptResponse buildResponse(String responseString, Exception e) {
		return new PaperReadReceiptResponse(responseString, e);
	}
}
