package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperAnswerFriendshipResponse;

public class PaperAnswerFriendshipRequest
		extends BasePaperRequest<PaperAnswerFriendshipResponse>{

	private final static String PATH = "answerFriendship";

	private String mInitiatorId;
	private String mReceiverId;
	private boolean mReject;

	public PaperAnswerFriendshipRequest(String initiatorId, String receiverId, boolean reject) {
		mInitiatorId = initiatorId;
		mReceiverId = receiverId;
		mReject = reject;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mInitiatorId, mReceiverId, mReject ? "1" : "0");
	}

	@Override
	protected PaperAnswerFriendshipResponse buildResponse(String responseString, Exception e) {
		return new PaperAnswerFriendshipResponse(responseString, e);
	}
}
