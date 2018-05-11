package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperClearUserFromChatResponse
		extends BaseResponse {

	private boolean mSuccess;

	public PaperClearUserFromChatResponse(String responseString, Exception exception) {
		super(responseString, exception);
		if(mResponseObject != null) {
			mSuccess = true;
		}
	}

	public boolean isSuccessful() {
		return mSuccess;
	}

}
