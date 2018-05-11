package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperGetChatsResponse;

public class PaperGetChatsRequest
		extends BasePaperRequest<PaperGetChatsResponse> {

	private final static String PATH = "chatByUserId";

	private String mUserId;

	public PaperGetChatsRequest(String userId) {
		mUserId = userId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId);
	}

	@Override
	protected PaperGetChatsResponse buildResponse(String responseString, Exception e) {
		return new PaperGetChatsResponse(responseString, e);
	}
}
