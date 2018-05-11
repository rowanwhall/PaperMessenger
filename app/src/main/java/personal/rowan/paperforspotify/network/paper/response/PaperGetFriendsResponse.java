package personal.rowan.paperforspotify.network.paper.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperGetFriendsResponse
		extends BaseResponse {

	private List<Friend> mFriends = new ArrayList<>();

	public PaperGetFriendsResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			JSONArray itemsArray = getJsonArray(mResponseObject, "items");
			if(itemsArray != null && itemsArray.length() > 0) {
				for(int i = 0; i < itemsArray.length(); i++) {
					JSONObject friendObject = getJsonObject(itemsArray, i);
					if(friendObject != null) {
						boolean pending = optBoolean(friendObject, "pending");
						boolean rejected = optBoolean(friendObject, "rejected");
						boolean sentByMe = optBoolean(friendObject, "sentByMe");

						JSONObject userObject = getJsonObject(friendObject, "user");
						if(userObject != null) {
							String userId = optString(userObject, "id");
							String name = optString(userObject, "name");
							String imageUrl = optString(userObject, "imageUrl");

							mFriends.add(new Friend(userId, name, imageUrl, pending, rejected, sentByMe));
						}
					}
				}
			}
		}
	}

	public List<Friend> getFriends() {
		return mFriends;
	}

}
