package com.personal.rowan.paperforspotify.backend.model;

import java.util.Calendar;
import java.util.UUID;

public class ChatMessage {

	public final static String KEY_PARENT = "ChatMessageParent";
	public final static String KEY_ENTITY = "ChatMessage";

	public final static String KEY_ID = "messageId";
	public final static String KEY_MESSAGE = "message";
	public final static String KEY_TIMESTAMP = "timestamp";
	public final static String KEY_SONG_URI = "messageSongUri";
	public final static String KEY_SONG_URL = "messageSongUrl";
	public final static String KEY_SONG_IMAGE_URL = "messageSongImageUrl";
	public final static String KEY_SONG_NAME = "messageSongName";
	public final static String KEY_ARTIST_NAME = "messageArtistName";

	private String id;
	private String chatId;
	private String userId;
	private String chatName;
	private String name;
	private String imageUrl;
	private String message;
	private Long timeStamp;

	private String messageSongUri;
	private String messageSongUrl;
	private String messageSongImageUrl;
	private String messageSongName;
	private String messageArtistName;

	public ChatMessage() {

	}

	public ChatMessage(String messageId, String chatId, String userId, String chatName, String name, String imageUrl, String message) {
		this.id = messageId;
		this.chatId = chatId;
		this.userId = userId;
		this.chatName = chatName;
		this.name = name;
		this.imageUrl = imageUrl;
		this.message = message;
		this.timeStamp = Calendar.getInstance().getTimeInMillis();
	}

	public void addSong(String songUri, String songUrl, String songImageUrl, String songName, String artistName) {
		this.messageSongUri = songUri;
		this.messageSongUrl = songUrl;
		this.messageSongImageUrl = songImageUrl;
		this.messageSongName = songName;
		this.messageArtistName = artistName;
	}

	public String getId() {
		return id;
	}

	public String getChatId() {
		return chatId;
	}

	public String getUserId() {
		return userId;
	}

	public String getChatName() {
		return chatName;
	}

	public String getName() {
		return name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getMessage() {
		return message;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public String getMessageSongUri() {
		return messageSongUri;
	}

	public String getMessageSongUrl() {
		return messageSongUrl;
	}

	public String getMessageSongImageUrl() {
		return messageSongImageUrl;
	}

	public String getMessageSongName() {
		return messageSongName;
	}

	public String getMessageArtistName() {
		return messageArtistName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setMessageSongUri(String messageSongUri) {
		this.messageSongUri = messageSongUri;
	}

	public void setMessageSongUrl(String messageSongUrl) {
		this.messageSongUrl = messageSongUrl;
	}

	public void setMessageSongImageUrl(String messageSongImageUrl) {
		this.messageSongImageUrl = messageSongImageUrl;
	}

	public void setMessageSongName(String messageSongName) {
		this.messageSongName = messageSongName;
	}

	public void setMessageArtistName(String messageArtistName) {
		this.messageArtistName = messageArtistName;
	}
}
