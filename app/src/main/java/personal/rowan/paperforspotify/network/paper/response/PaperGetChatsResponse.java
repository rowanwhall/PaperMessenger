package personal.rowan.paperforspotify.network.paper.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperGetChatsResponse
		extends BaseResponse {

	private List<Chat> mChats = new ArrayList<>();

	public PaperGetChatsResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			JSONArray itemsArray = getJsonArray(mResponseObject, "items");
			if(itemsArray != null && itemsArray.length() > 0) {
				for(int i = 0; i < itemsArray.length(); i++) {
					JSONObject chatObject = getJsonObject(itemsArray, i);
					if(chatObject != null) {
						String chatId = optString(chatObject, "id");
						String name = optString(chatObject, "name");
						String creatorName = optString(chatObject, "creatorName");
						String creatorId = optString(chatObject, "creatorId");
						String updatedTimestamp = optString(chatObject, "updatedTimestamp");
						String lastMessage = optString(chatObject, "lastMessage");
						boolean hasRead = optBoolean(chatObject, "hasRead");

						String songUri = optString(chatObject, "chatSongUri");
						String songUrl = optString(chatObject, "chatSongUrl");
						String songImageUrl = optString(chatObject, "chatSongImageUrl");
						String songName = optString(chatObject, "chatSongName");
						String artistName = optString(chatObject, "chatArtistName");
						Track track = new Track(songName, songUri, songUrl, songImageUrl, songImageUrl, songImageUrl, artistName);

						mChats.add(new Chat(chatId, name, creatorName, creatorId, Long.parseLong(updatedTimestamp), lastMessage, hasRead, track));
					}
				}
			}
		}
	}

	public List<Chat> getChats() {
		return mChats;
	}

}
