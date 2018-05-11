package personal.rowan.paperforspotify.network.imgur;

import org.json.JSONObject;

import personal.rowan.paperforspotify.network.BaseResponse;

public class ImgurUploadImageResponse
		extends BaseResponse {

	private String mLink;

	public ImgurUploadImageResponse(String responseString, Exception exception) {
		super(responseString, exception);
		if(mResponseObject != null) {
			JSONObject dataObject = getJsonObject(mResponseObject, "data");
			if(dataObject != null) {
				mLink = optString(dataObject, "link");
			}
		}
	}

	public String getLink() {
		return mLink;
	}

}
