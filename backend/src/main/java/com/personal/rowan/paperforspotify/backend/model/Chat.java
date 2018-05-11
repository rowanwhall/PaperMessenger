package com.personal.rowan.paperforspotify.backend.model;

import java.util.Calendar;
import java.util.UUID;

public class Chat {

	public final static String KEY_PARENT = "ChatParent";
	public final static String KEY_ENTITY = "Chat";

	public final static String KEY_ID = "chatId";
	public final static String KEY_NAME = "chatName";
	public final static String KEY_CREATOR_NAME = "creatorName";
	public final static String KEY_CREATOR_ID = "creatorId";
	public final static String KEY_UPDATED_TIMESTAMP = "updatedTimestamp";
	public final static String KEY_LAST_MESSAGE = "lastMessage";

	public final static String KEY_CHAT_SONG_URI = "chatSongUri";
	public final static String KEY_CHAT_SONG_URL = "chatSongUrl";
	public final static String KEY_CHAT_SONG_IMAGE_URL = "chatSongImageUrl";
	public final static String KEY_CHAT_SONG_NAME = "chatSongName";
	public final static String KEY_CHAT_SONG_ARTIST_NAME = "chatSongArtistName";

	private String id;
	private String name;
	private String creatorName;
	private String creatorId;
	private Long updatedTimestamp;
	private String lastMessage;

	private String chatSongUri;
	private String chatSongUrl;
	private String chatSongImageUrl;
	private String chatSongName;
	private String chatArtistName;

	//not stored in datastore
	public String hasRead;

	public Chat() {}

	public Chat(String name, String creatorId, String creatorName) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.creatorName = creatorName;
		this.creatorId = creatorId;
		this.updatedTimestamp = Calendar.getInstance().getTimeInMillis();
		this.lastMessage = "";
		this.chatSongImageUrl = "";
		this.chatSongUri = "";
		this.chatSongUrl = "";
		this.chatSongName = "";
		this.chatArtistName = "";
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public void setChatSongUri(String uri) {
		this.chatSongUri = uri;
	}

	public void setChatSongUrl(String url) { this.chatSongUrl = url;}

	public void setChatSongImageUrl(String imageUrl) {
		this.chatSongImageUrl = imageUrl;
	}

	public void setChatSongName(String name) {
		this.chatSongName = name;
	}

	public void setChatArtistName(String name) {
		this.chatArtistName = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public String getChatSongUri() {
		return chatSongUri;
	}

	public String getChatSongUrl() { return chatSongUrl; }

	public String getChatSongImageUrl() {
		return chatSongImageUrl;
	}

	public String getChatSongName() {
		return chatSongName;
	}

	public String getChatArtistName() {
		return chatArtistName;
	}

	public Long getUpdatedTimestamp() {
		return updatedTimestamp;
	}

	public void setUpdatedTimestamp(Long updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
}
