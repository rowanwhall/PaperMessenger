package personal.rowan.paperforspotify.network.paper.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.UserSearchResult;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperUserByChatIdResponse
		extends BaseResponse {

	private List<UserSearchResult> mUserSearchResults = new ArrayList<>();

	public PaperUserByChatIdResponse(String responseString, Exception exception) {
		super(responseString, exception);
		if(mResponseObject != null) {
			JSONArray itemsArray = getJsonArray(mResponseObject, "items");
			if(itemsArray != null && itemsArray.length() > 0) {
				for(int i = 0; i < itemsArray.length(); i++) {
					JSONObject searchResultObject = getJsonObject(itemsArray, i);
					if(searchResultObject != null) {
						String userId = optString(searchResultObject, "id");
						String name = optString(searchResultObject, "name");
						String imageUrl = optString(searchResultObject, "imageUrl");

						mUserSearchResults.add(new UserSearchResult(userId, name, imageUrl, false));
					}
				}
			}
		}
	}

	public List<UserSearchResult> getUserSearchResults() {
		return mUserSearchResults;
	}
}
