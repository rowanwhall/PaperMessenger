package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperStoreRegIdResponse;

public class PaperStoreRegIdRequest
		extends BasePaperRequest<PaperStoreRegIdResponse> {

	private final static String PATH = "storeRegId";

	private String mUserId;
	private String mRegId;

	public PaperStoreRegIdRequest(String userId, String regId) {
		mUserId = userId;
		mRegId = regId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId, mRegId);
	}

	@Override
	protected PaperStoreRegIdResponse buildResponse(String responseString, Exception e) {
		return new PaperStoreRegIdResponse(responseString, e);
	}
}
