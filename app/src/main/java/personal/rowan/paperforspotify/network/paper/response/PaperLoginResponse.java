package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperLoginResponse
		extends BaseResponse {

	private String mUserId;
	private String mName;
	private String mImageUrl;

	public PaperLoginResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			mUserId = optString(mResponseObject, "id");
			mName = optString(mResponseObject, "name");
			mImageUrl = optString(mResponseObject, "imageUrl");
		}
	}

	public String getUserId() {
		return mUserId;
	}

	public String getName() {
		return mName;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
}
