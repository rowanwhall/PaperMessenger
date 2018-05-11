package personal.rowan.paperforspotify.network.paper.response;

import personal.rowan.paperforspotify.network.BaseResponse;

public class PaperRequestFriendResponse
		extends BaseResponse {

	private boolean mPending;
	private boolean mRejected;

	public PaperRequestFriendResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			mPending = optBoolean(mResponseObject, "pending");
			mRejected = optBoolean(mResponseObject, "rejected");
		}
	}

	public boolean isPending() {
		return mPending;
	}

	public boolean isRejected() {
		return mRejected;
	}
}
