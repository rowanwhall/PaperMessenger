package com.personal.rowan.paperforspotify.backend.model;

public class ChatMembership {

	public final static String KEY_PARENT = "ChatMembershipParent";
	public final static String KEY_ENTITY = "ChatMembership";

	public final static String KEY_CSV_USER_IDS = "csvUserIds";
	public final static String KEY_READ_RECEIPT = "readReceipt";

	private String chatId;
	private String userId;
	private String hasRead;

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHasRead() {
		return hasRead;
	}

	public void setHasRead(String hasRead) {
		this.hasRead = hasRead;
	}

}
