package personal.rowan.paperforspotify.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat
		implements Parcelable {

	public static final int MAX_CHAT_NAME_LENGTH = 20;

	private String chatId;
	private String chatName;
	private String creatorName;
	private String creatorId;
	private long timestamp;
	private String lastMessage;
	private boolean hasRead;
	private Track track;

	public Chat(String chatId, String chatName, String creatorName, String creatorId, long timestamp, String lastMessage, boolean hasRead, Track track) {
		this.chatId = chatId;
		this.chatName = chatName;
		this.creatorName = creatorName;
		this.creatorId = creatorId;
		this.timestamp = timestamp;
		this.lastMessage = lastMessage;
		this.hasRead = hasRead;
		if(track.isValid()) {
			this.track = track;
		}
	}

	public String getChatId() {
		return chatId;
	}

	public String getChatName() {
		return chatName;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public boolean hasRead() {
		return hasRead;
	}

	public Track getTrack() {
		return track;
	}

	protected Chat(Parcel in) {
		chatId = in.readString();
		chatName = in.readString();
		creatorName = in.readString();
		creatorId = in.readString();
		timestamp = in.readLong();
		lastMessage = in.readString();
		hasRead = in.readByte() != 0x00;
		track = (Track) in.readValue(Track.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(chatId);
		dest.writeString(chatName);
		dest.writeString(creatorName);
		dest.writeString(creatorId);
		dest.writeLong(timestamp);
		dest.writeString(lastMessage);
		dest.writeByte((byte) (hasRead ? 0x01 : 0x00));
		dest.writeValue(track);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
		@Override
		public Chat createFromParcel(Parcel in) {
			return new Chat(in);
		}

		@Override
		public Chat[] newArray(int size) {
			return new Chat[size];
		}
	};

}
