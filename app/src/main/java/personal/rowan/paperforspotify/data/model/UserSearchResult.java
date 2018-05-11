package personal.rowan.paperforspotify.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSearchResult implements Parcelable {

	private String userId;
	private String name;
	private String imageUrl;

	private boolean canSendRequest;

	public UserSearchResult(String userId, String name, String imageUrl, boolean canSendRequest) {
		this.userId = userId;
		this.name = name;
		this.imageUrl = imageUrl;
		this.canSendRequest = canSendRequest;
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

	public boolean canSendRequest() {
		return canSendRequest;
	}

	public void setCanSendRequest(boolean canSendRequest) {
		this.canSendRequest = canSendRequest;
	}


	protected UserSearchResult(Parcel in) {
		userId = in.readString();
		name = in.readString();
		imageUrl = in.readString();
		canSendRequest = in.readByte() != 0x00;
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
		dest.writeByte((byte) (canSendRequest ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<UserSearchResult> CREATOR = new Parcelable.Creator<UserSearchResult>() {
		@Override
		public UserSearchResult createFromParcel(Parcel in) {
			return new UserSearchResult(in);
		}

		@Override
		public UserSearchResult[] newArray(int size) {
			return new UserSearchResult[size];
		}
	};
}
