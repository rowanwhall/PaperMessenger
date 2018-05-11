package personal.rowan.paperforspotify.network.paper.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.UserSearchResult;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperSearchFriendsResponse
		extends BaseResponse {

	private List<UserSearchResult> mSearchResults = new ArrayList<>();

	public PaperSearchFriendsResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			JSONArray itemsArray = getJsonArray(mResponseObject, "items");
			if(itemsArray != null && itemsArray.length() > 0) {
				for(int i = 0; i < itemsArray.length(); i++) {
					JSONObject searchResultObject = getJsonObject(itemsArray, i);
					if(searchResultObject != null) {
						boolean canSendRequest = optBoolean(searchResultObject, "canSendRequest");

						JSONObject userObject = getJsonObject(searchResultObject, "user");
						if(userObject != null) {
							String userId = optString(userObject, "id");
							String name = optString(userObject, "name");
							String imageUrl = optString(userObject, "imageUrl");

							mSearchResults.add(new UserSearchResult(userId, name, imageUrl, canSendRequest));
						}
					}
				}
			}
		}
	}

	public List<UserSearchResult> getSearchResults() {
		return mSearchResults;
	}

}
