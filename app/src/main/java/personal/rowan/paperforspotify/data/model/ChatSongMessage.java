package personal.rowan.paperforspotify.data.model;

import java.util.Calendar;

public class ChatSongMessage
		extends ChatMessage {

	private String songUri;
	private String songUrl;
	private String songImageUrl;
	private String songName;
	private String artistName;

	public ChatSongMessage(String messageId, String chatId, String userId, String userName, String imageUrl, String message, long timestamp,
	                       String songUri, String songUrl, String songImageUrl, String songName, String artistName) {
		super(messageId, chatId, userId, userName, imageUrl, message, timestamp);

		this.songUri = songUri;
		this.songUrl = songUrl;
		this.songImageUrl = songImageUrl;
		this.songName = songName;
		this.artistName = artistName;
	}

	public static ChatSongMessage createFakeChatMessage(String messageId, String chatId, String userId, String name, String imageUrl, String message, Track track) {
		ChatSongMessage fakeMessage = new ChatSongMessage(messageId, chatId, userId, name, imageUrl, message, Calendar.getInstance().getTimeInMillis(),
				track.getUri(), track.getUrl(), track.getImageUrlMedium(), track.getName(), track.getArtistName());
		fakeMessage.mFakeMessage = true;
		return fakeMessage;
	}

	public String getSongUri() {
		return songUri;
	}

	public String getSongUrl() {
		return songUrl;
	}

	public String getSongImageUrl() {
		return songImageUrl;
	}

	public String getSongName() {
		return songName;
	}

	public String getArtistName() {
		return artistName;
	}
}
