package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperGetFriendsResponse;

public class PaperGetFriendsRequest
		extends BasePaperRequest<PaperGetFriendsResponse> {

	private final static String PATH = "friendsForUser";

	private String mUserId;

	public PaperGetFriendsRequest(String userId) {
		mUserId = userId;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId);
	}

	@Override
	protected PaperGetFriendsResponse buildResponse(String responseString, Exception e) {
		return new PaperGetFriendsResponse(responseString, e);
	}
}
