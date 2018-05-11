package personal.rowan.paperforspotify.network.paper.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import personal.rowan.paperforspotify.network.BaseRequest;
import personal.rowan.paperforspotify.network.BaseResponse;

public abstract class BasePaperRequest<T extends BaseResponse>
		extends BaseRequest<T> {

	private static final String PAPER_BASE_URL = "https://paper-for-spotify.appspot.com/_ah/api/messaging/v1/";

	@Override
	protected String getUrl() {
		return PAPER_BASE_URL + getPath();
	}

	@Override
	protected MethodType getMethodType() {
		return MethodType.POST;
	}

	protected abstract String getPath();

	protected String buildPath(String... variables) {
		StringBuilder b = new StringBuilder();

		for(String v : variables) {
			try {
				b.append(URLEncoder.encode(v, "UTF-8"));
				b.append("/");
			} catch(UnsupportedEncodingException e) {
				Log.e(BasePaperRequest.this.getClass().getName(), "URL encoding error");
			}
		}

		return b.toString();
	}
}
