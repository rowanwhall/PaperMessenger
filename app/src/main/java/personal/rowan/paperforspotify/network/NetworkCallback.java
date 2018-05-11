package personal.rowan.paperforspotify.network;

import okhttp3.Response;

public interface NetworkCallback<T extends BaseResponse> {
	void onResponse(Response response, T responseObject);
	void onFailure(Exception e);
}
