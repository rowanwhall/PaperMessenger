package personal.rowan.paperforspotify.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend
		implements Parcelable {

	private String userId;
	private String name;
	private String imageUrl;

	private boolean pending;
	private boolean rejected;
	private boolean sentByMe;

	public Friend(String userId, String name, String imageUrl, boolean pending, boolean rejected, boolean sentByMe) {
		this.userId = userId;
		this.name = name;
		this.imageUrl = imageUrl;
		this.pending = pending;
		this.rejected = rejected;
		this.sentByMe = sentByMe;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isPending() {
		return pending;
	}

	public boolean isRejected() {
		return rejected;
	}

	public boolean isSentByMe() {
		return sentByMe;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isUserPending() {
		return pending && !sentByMe && !rejected;
	}

	public boolean isRecipientPending() {
		return pending && sentByMe;
	}

	public boolean isAccepted() {
		return !pending && !rejected;
	}

	protected Friend(Parcel in) {
		userId = in.readString();
		name = in.readString();
		imageUrl = in.readString();
		pending = in.readByte() != 0x00;
		rejected = in.readByte() != 0x00;
		sentByMe = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(name);
		dest.writeString(imageUrl);
		dest.writeByte((byte) (pending ? 0x01 : 0x00));
		dest.writeByte((byte) (rejected ? 0x01 : 0x00));
		dest.writeByte((byte) (sentByMe ? 0x01 : 0x00));
	}

	public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
		@Override
		public Friend createFromParcel(Parcel in) {
			return new Friend(in);
		}

		@Override
		public Friend[] newArray(int size) {
			return new Friend[size];
		}
	};
}
