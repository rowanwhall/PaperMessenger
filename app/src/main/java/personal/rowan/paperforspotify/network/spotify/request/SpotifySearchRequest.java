package personal.rowan.paperforspotify.network.spotify.request;

import okhttp3.Request;
import personal.rowan.paperforspotify.network.BaseRequest;
import personal.rowan.paperforspotify.network.spotify.response.SpotifySearchResponse;
import personal.rowan.paperforspotify.util.StringUtil;

public class SpotifySearchRequest
		extends BaseRequest<SpotifySearchResponse> {

	private String mAuthorization;
	private String mSearchTerm;

	public SpotifySearchRequest(String authorization, String searchTerm) {
		mAuthorization = authorization;
		mSearchTerm = searchTerm;
	}

	@Override
	protected SpotifySearchResponse buildResponse(String responseString, Exception e) {
		return new SpotifySearchResponse(responseString, e);
	}

	@Override
	protected String getUrl() {
		return "https://api.spotify.com/v1/search?q=" + StringUtil.urlEncode(mSearchTerm) + "&type=track";
	}

	@Override
	protected MethodType getMethodType() {
		return MethodType.GET;
	}

	@Override
	protected void addHeaders(Request.Builder builder) {
		super.addHeaders(builder);
		builder.addHeader("Authorization", "Bearer " + mAuthorization);
	}

}
