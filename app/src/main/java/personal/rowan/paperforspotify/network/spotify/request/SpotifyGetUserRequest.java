package personal.rowan.paperforspotify.network.spotify.request;

import okhttp3.Request;
import personal.rowan.paperforspotify.network.BaseRequest;
import personal.rowan.paperforspotify.network.spotify.response.SpotifyGetUserResponse;

public class SpotifyGetUserRequest
		extends BaseRequest<SpotifyGetUserResponse> {

	private String mAuthorization;

	public SpotifyGetUserRequest(String authorization) {
		mAuthorization = authorization;
	}

	@Override
	protected SpotifyGetUserResponse buildResponse(String responseString, Exception e) {
		return new SpotifyGetUserResponse(responseString, e);
	}

	@Override
	protected String getUrl() {
		return "https://api.spotify.com/v1/me";
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
