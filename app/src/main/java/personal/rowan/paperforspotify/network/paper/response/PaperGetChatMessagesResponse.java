package personal.rowan.paperforspotify.network.paper.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.data.model.ChatSongMessage;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperGetChatMessagesResponse
		extends BaseResponse{

	private List<ChatMessage> mMessages = new ArrayList<>();

	public PaperGetChatMessagesResponse(String responseString, Exception exception) {
		super(responseString, exception);
		if(mResponseObject != null) {
			JSONArray itemsArray = getJsonArray(mResponseObject, "items");
			if(itemsArray != null && itemsArray.length() > 0) {
				for(int i = 0; i < itemsArray.length(); i++) {
					JSONObject messageObject = getJsonObject(itemsArray, i);
					if(messageObject != null) {
						String messageId = optString(messageObject, "id");
						String chatId = optString(messageObject, "chatId");
						String userId = optString(messageObject, "userId");
						String userName = optString(messageObject, "name");
						String imageUrl = optString(messageObject, "imageUrl");
						String message = optString(messageObject, "message");
						String timestamp = optString(messageObject, "timeStamp");

						String songUri = optString(messageObject, "messageSongUri");
						if(TextUtils.isEmpty(songUri)) {
							mMessages.add(new ChatMessage(messageId, chatId, userId, userName, imageUrl, message, Long.parseLong(timestamp)));
						} else {
							String songUrl = optString(messageObject, "messageSongUrl");
							String songImageUrl = optString(messageObject, "messageSongImageUrl");
							String songName = optString(messageObject, "messageSongName");
							String artistName = optString(messageObject, "messageArtistName");
							mMessages.add(new ChatSongMessage(messageId, chatId, userId, userName, imageUrl, message, Long.parseLong(timestamp),
									songUri, songUrl, songImageUrl, songName, artistName));
						}
					}
				}
			}
		}
	}

	public List<ChatMessage> getMessages() {
		return mMessages;
	}

}
