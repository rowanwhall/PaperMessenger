package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperStoreImageUrlResponse;

public class PaperStoreImageUrlRequest
		extends BasePaperRequest<PaperStoreImageUrlResponse> {

	private final static String PATH = "storeImageUrl";

	private String mUserId;
	private String mImageUrl;

	public PaperStoreImageUrlRequest(String userId, String imageUrl) {
		mUserId = userId;
		mImageUrl = imageUrl;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId, mImageUrl);
	}

	@Override
	protected PaperStoreImageUrlResponse buildResponse(String responseString, Exception e) {
		return new PaperStoreImageUrlResponse(responseString, e);
	}

}
