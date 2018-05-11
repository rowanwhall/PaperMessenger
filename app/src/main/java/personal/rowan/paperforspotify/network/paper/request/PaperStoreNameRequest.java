package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperStoreNameResponse;

public class PaperStoreNameRequest
		extends BasePaperRequest<PaperStoreNameResponse> {

	private final static String PATH = "storeName";

	private String mUserId;
	private String mName;

	public PaperStoreNameRequest(String userId, String name) {
		mUserId = userId;
		mName = name;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId, mName);
	}

	@Override
	protected PaperStoreNameResponse buildResponse(String responseString, Exception e) {
		return new PaperStoreNameResponse(responseString, e);
	}
}
