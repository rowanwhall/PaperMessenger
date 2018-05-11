package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperChatByIdResponse;

public class PaperChatByIdRequest
		extends BasePaperRequest<PaperChatByIdResponse> {

	private final static String PATH = "chatById";

	private String mChatId;

	public PaperChatByIdRequest(String chatId) {
		mChatId = chatId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mChatId);
	}

	@Override
	protected PaperChatByIdResponse buildResponse(String responseString, Exception e) {
		return new PaperChatByIdResponse(responseString, e);
	}

}
