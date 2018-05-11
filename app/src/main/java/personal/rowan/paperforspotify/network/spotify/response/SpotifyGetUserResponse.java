package personal.rowan.paperforspotify.network.spotify.response;

import org.json.JSONArray;
import org.json.JSONObject;

import personal.rowan.paperforspotify.network.BaseResponse;

public class SpotifyGetUserResponse
		extends BaseResponse {

	private String mUserId;
	private String mDisplayName;
	private String mUserType;
	private String mImageUrl;

	public SpotifyGetUserResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			String userId = optString(mResponseObject, "id");
			String displayName = optString(mResponseObject, "display_name");
			String userType = optString(mResponseObject, "product");
			String imageUrl = "";
			JSONArray imagesArray = getJsonArray(mResponseObject, "images");
			if(imagesArray != null && imagesArray.length() > 0) {
				JSONObject imageObject = getJsonObject(imagesArray, 0);
				if(imageObject != null) {
					imageUrl = optString(imageObject, "url");
				}
			}

			mUserId = userId;
			mDisplayName = displayName;
			mUserType = userType;
			mImageUrl = imageUrl;
		}
	}

	public String getUserId() {
		return mUserId;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public String getUserType() {
		return mUserType;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
}
