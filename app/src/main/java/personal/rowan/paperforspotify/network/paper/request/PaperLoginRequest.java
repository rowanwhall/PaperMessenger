package personal.rowan.paperforspotify.network.paper.request;

import android.text.TextUtils;

import personal.rowan.paperforspotify.network.paper.response.PaperLoginResponse;

public class PaperLoginRequest
		extends BasePaperRequest<PaperLoginResponse> {

	private final static String PATH = "login";

	private String mUserId;
	private String mName;
	private String mImageUrl;

	public PaperLoginRequest(String userId, String name, String imageUrl) {
		mUserId = userId;
		mName = name;
		mImageUrl = TextUtils.isEmpty(imageUrl) ? "null" : imageUrl;
	}

	@Override
	protected PaperLoginResponse buildResponse(String responseString, Exception e) {
		return new PaperLoginResponse(responseString, e);
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId, mName, mImageUrl);
	}

}
