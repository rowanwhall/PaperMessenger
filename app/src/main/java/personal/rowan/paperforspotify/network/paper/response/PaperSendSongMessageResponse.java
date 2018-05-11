package personal.rowan.paperforspotify.network.paper.response;

import java.util.Calendar;

import personal.rowan.paperforspotify.data.model.ChatSongMessage;

public class PaperSendSongMessageResponse
		extends PaperSendTextOnlyMessageResponse {

	private ChatSongMessage mMessage;

	public PaperSendSongMessageResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			String messageId = optString(mResponseObject, "id");
			String chatId = optString(mResponseObject, "chatId");
			String userId = optString(mResponseObject, "userId");
			String userName = optString(mResponseObject, "name");
			String imageUrl = optString(mResponseObject, "imageUrl");
			String message = optString(mResponseObject, "message");
			String timestamp = optString(mResponseObject, "timeStamp");
			long timestampLong = !timestamp.isEmpty() ? Long.parseLong(timestamp) : Calendar.getInstance().getTimeInMillis();
			String uri = optString(mResponseObject, "messageSongUri");
			String url = optString(mResponseObject, "messageSongUrl");
			String songImageUrl = optString(mResponseObject, "messageSongImageUrl");
			String songName = optString(mResponseObject, "messageSongName");
			String artistName = optString(mResponseObject, "messageArtistName");

			mMessage = new ChatSongMessage(messageId, chatId, userId, userName, imageUrl, message, timestampLong, uri, url, songImageUrl, songName, artistName);
		}
	}

	public ChatSongMessage getMessage() {
		return mMessage;
	}

}
