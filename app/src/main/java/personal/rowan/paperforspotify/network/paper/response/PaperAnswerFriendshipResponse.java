package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperAnswerFriendshipResponse
		extends BaseResponse {

	boolean mPending;
	boolean mRejected;

	public PaperAnswerFriendshipResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			mPending = optBoolean(mResponseObject, "pending");
			mRejected = optBoolean(mResponseObject, "rejected");
		}
	}

}
