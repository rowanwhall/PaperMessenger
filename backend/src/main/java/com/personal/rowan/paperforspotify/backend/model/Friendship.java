package com.personal.rowan.paperforspotify.backend.model;

public class Friendship {

	public final static String KEY_PARENT = "FriendshipParent";
	public final static String KEY_ENTITY = "Friendship";

	public final static String KEY_INITIATOR_ID = "initiatorId";
	public final static String KEY_RECEIVER_ID = "receiverId";
	public final static String KEY_PENDING = "pending";
	public final static String KEY_REJECTED = "rejected";

	private String initiatorId;
	private String receiverId;
	private String pending = "1";
	private String rejected = "0";

	public String getInitiatorId() {
		return initiatorId;
	}

	public void setInitiatorId(String initiatorId) {
		this.initiatorId = initiatorId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getPending() {
		return pending;
	}

	public void setPending(String pending) {
		this.pending = pending;
	}

	public String getRejected() {
		return rejected;
	}

	public void setRejected(String rejected) {
		this.rejected = rejected;
	}

}
