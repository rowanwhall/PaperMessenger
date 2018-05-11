package personal.rowan.paperforspotify.network.paper.request;

import personal.rowan.paperforspotify.network.paper.response.PaperSearchFriendsResponse;

public class PaperSearchFriendsRequest
		extends BasePaperRequest<PaperSearchFriendsResponse> {

	private final static String PATH = "searchUsers";

	private String mUserId;
	private String mSearchTerm;

	public PaperSearchFriendsRequest(String mUserId, String mSearchTerm) {
		this.mUserId = mUserId;
		this.mSearchTerm = mSearchTerm;
	}

	@Override
	protected String getPath() {
		return buildPath(PATH, mUserId, mSearchTerm);
	}

	@Override
	protected PaperSearchFriendsResponse buildResponse(String responseString, Exception e) {
		return new PaperSearchFriendsResponse(responseString, e);
	}
}
