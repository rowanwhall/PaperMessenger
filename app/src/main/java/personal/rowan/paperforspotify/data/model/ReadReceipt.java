package personal.rowan.paperforspotify.data.model;

public class ReadReceipt {

	private String mChatId;
	private String mMessageId;
	private String mMessage;

	public ReadReceipt(String chatId, String messageId, String message) {
		mChatId = chatId;
		mMessageId = messageId;
		mMessage = message;
	}

	public String getChatId() {
		return mChatId;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public String getMessage() {
		return mMessage;
	}

}
