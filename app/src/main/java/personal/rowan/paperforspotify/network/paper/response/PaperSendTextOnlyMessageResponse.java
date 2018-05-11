package personal.rowan.paperforspotify.network.paper.response;

import java.util.Calendar;

import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperSendTextOnlyMessageResponse
		extends BaseResponse {

	private ChatMessage mMessage;

	public PaperSendTextOnlyMessageResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			String messageId = optString(mResponseObject, "id");
			String chatId = optString(mResponseObject, "chatId");
			String userId = optString(mResponseObject, "userId");
			String userName = optString(mResponseObject, "name");
			String imageUrl = optString(mResponseObject, "imageUrl");
			String message = optString(mResponseObject, "message");
			String timestamp = optString(mResponseObject, "timeStamp");
			long timestampLong = !timestamp.isEmpty() ? Long.parseLong(timestamp) : Calendar.getInstance().getTimeInMillis();

			mMessage = new ChatMessage(messageId, chatId, userId, userName, imageUrl, message, timestampLong);
		}
	}

	public ChatMessage getMessage() {
		return mMessage;
	}

}
