package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperRequestFriendResponse;

public class PaperRequestFriendRequest
		extends BasePaperRequest<PaperRequestFriendResponse> {

	private final static String PATH = "requestFriendship";

	private String mInitiatorId;
	private String mReceiverId;

	public PaperRequestFriendRequest(String initiatorId, String receiverId) {
		mInitiatorId = initiatorId;
		mReceiverId = receiverId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mInitiatorId, mReceiverId);
	}

	@Override
	protected PaperRequestFriendResponse buildResponse(String responseString, Exception e) {
		return new PaperRequestFriendResponse(responseString, e);
	}
}
