package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperUserByChatIdResponse;

public class PaperUserByChatIdRequest
		extends BasePaperRequest<PaperUserByChatIdResponse> {

	private final static String PATH = "usersByChatId";

	private String mChatId;

	public PaperUserByChatIdRequest(String chatId) {
		mChatId = chatId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId);
	}

	@Override
	protected PaperUserByChatIdResponse buildResponse(String responseString, Exception e) {
		return new PaperUserByChatIdResponse(responseString, e);
	}

}
