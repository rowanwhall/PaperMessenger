package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperStoreChatNameResponse
		extends BaseResponse {

	private Chat mChat;

	public PaperStoreChatNameResponse(String responseString, Exception exception) {
		super(responseString, exception);
		if(mResponseObject != null) {
			String chatId = optString(mResponseObject, "id");
			String name = optString(mResponseObject, "name");
			String creatorName = optString(mResponseObject, "creatorName");
			String creatorId = optString(mResponseObject, "creatorId");
			String updatedTimestamp = optString(mResponseObject, "updatedTimestamp");
			String lastMessage = optString(mResponseObject, "lastMessage");
			boolean hasRead = optBoolean(mResponseObject, "hasRead");

			String songUri = optString(mResponseObject, "chatSongUri");
			String songUrl = optString(mResponseObject, "chatSongUrl");
			String songImageUrl = optString(mResponseObject, "chatSongImageUrl");
			String songName = optString(mResponseObject, "chatSongName");
			String artistName = optString(mResponseObject, "chatArtistName");
			Track track = new Track(songName, songUri, songUrl, songImageUrl, songImageUrl, songImageUrl, artistName);

			mChat = new Chat(chatId, name, creatorName, creatorId, Long.parseLong(updatedTimestamp), lastMessage, hasRead, track);
		}
	}

	public Chat getChat() {
		return mChat;
	}

}
