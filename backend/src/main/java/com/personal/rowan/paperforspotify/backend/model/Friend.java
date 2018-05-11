package com.personal.rowan.paperforspotify.backend.model;

public class Friend {

	private User user;
	private String pending;
	private String rejected;
	private String sentByMe;

	public Friend(User user, String pending, String rejected, String sentByMe) {
		this.user = user;
		this.pending = pending;
		this.rejected = rejected;
		this.sentByMe = sentByMe;
	}

	public User getUser() {
		return user;
	}

	public String getPending() {
		return pending;
	}

	public String getRejected() {
		return rejected;
	}

	public String getSentByMe() {
		return sentByMe;
	}

	public boolean acceptedFriendship() {
		return rejected.equals("0") && pending.equals("0");
	}

	public boolean rejectedByMe() {
		return rejected.equals("1") && sentByMe.equals("0");
	}

	public boolean rejectedByThem() {
		return rejected.equals("1") && sentByMe.equals("1");
	}

	public boolean isPendingMe() {
		return pending.equals("1") && sentByMe.equals("0") && rejected.equals("0");
	}

	public boolean isPendingThem() {
		return pending.equals("1") && sentByMe.equals("1") && rejected.equals("0");
	}

}
