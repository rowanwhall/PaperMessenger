package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.data.model.ReadReceipt;
import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperReadReceiptResponse
		extends BaseResponse {

	private ReadReceipt mReadReceipt;

	public PaperReadReceiptResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			String chatId = optString(mResponseObject, "chatId");
			String chatMessageId = optString(mResponseObject, "chatMessageId");
			String message = optString(mResponseObject, "message");
			mReadReceipt = new ReadReceipt(chatId, chatMessageId, message);
		}
	}

	public ReadReceipt getReadReceipt() {
		return mReadReceipt;
	}

}
