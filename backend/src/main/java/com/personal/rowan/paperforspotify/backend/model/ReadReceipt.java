package com.personal.rowan.paperforspotify.backend.model;

public class ReadReceipt {

	public String chatId;
	public String chatMessageId;
	public String message;

	public ReadReceipt(String chatId, String chatMessageId, String message) {
		this.chatId = chatId;
		this.chatMessageId = chatMessageId;
		this.message = message;
	}

}
