package com.personal.rowan.paperforspotify.backend.model;

public class UserSearchResult {

	private User user;
	private String canSendRequest;

	public UserSearchResult(User user, String canSendRequest) {
		this.user = user;
		this.canSendRequest = canSendRequest;
	}

	public User getUser() {
		return user;
	}

	public String getCanSendRequest() {
		return canSendRequest;
	}
}
