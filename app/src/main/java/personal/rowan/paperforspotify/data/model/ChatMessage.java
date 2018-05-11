package personal.rowan.paperforspotify.data.model;

import java.util.Calendar;
import java.util.Locale;

public class ChatMessage {

	private static final String ADMINISTRATOR_ID = "ADMINISTRATOR_ID";
	private static final long SHOW_DATE_THRESHOLD = 1000 * 60 * 15; //15 minutes

	private String messageId;
	private String chatId;
	private String userId;
	private String userName;
	private String imageUrl;
	private String message;
	private long timestamp;

	//only used for viewholder purposes
	private boolean mShouldShowTimestamp;
	protected boolean mFakeMessage;

	public ChatMessage(String messageId, String chatId, String userId, String userName, String imageUrl, String message, long timestamp) {
		this.messageId = messageId;
		this.chatId = chatId;
		this.userId = userId;
		this.userName = userName;
		this.imageUrl = imageUrl;
		this.message = message;
		this.timestamp = timestamp;
	}

	public static ChatMessage createFakeChatMessage(String messageId, String chatId, String userId, String name, String imageUrl, String message) {
		ChatMessage fakeMessage = new ChatMessage(messageId, chatId, userId, name, imageUrl, message, Calendar.getInstance().getTimeInMillis());
		fakeMessage.mFakeMessage = true;
		return fakeMessage;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getChatId() {
		return chatId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return userName.split(" ")[0];
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getFormattedTimestamp() {
		Calendar currentTime = Calendar.getInstance();
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timestamp);

		int today = currentTime.get(Calendar.DAY_OF_YEAR);
		int timestampDay = time.get(Calendar.DAY_OF_YEAR);

		String day = "%1$tb %1$td %1$tY at ";
		if(today == timestampDay) {
			day = "";
		} else if(today == timestampDay + 1) {
			day = "Yesterday at ";
		}
		return String.format(Locale.getDefault(), day + "%1$tI:%1$tM %1$Tp", time);
	}

	public boolean isPastThreshold(ChatMessage previousMessage) {
		return timestamp - previousMessage.getTimestamp() >= SHOW_DATE_THRESHOLD;
	}

	public void setShouldShowTimestamp(boolean shouldShowTimestamp) {
		mShouldShowTimestamp = shouldShowTimestamp;
	}

	public boolean shouldShowTimestamp() {
		return mShouldShowTimestamp;
	}

	public void setIsFake(boolean fakeMessage) {
		mFakeMessage = fakeMessage;
	}

	public boolean isFake() {
		return mFakeMessage;
	}

	public boolean isAdmin() {
		return ADMINISTRATOR_ID.equals(userId);
	}

}
