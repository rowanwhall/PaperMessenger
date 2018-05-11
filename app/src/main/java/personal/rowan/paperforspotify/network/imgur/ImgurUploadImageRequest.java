package personal.rowan.paperforspotify.network.imgur;

import android.net.Uri;

import java.io.File;

import okhttp3.Request;
import okhttp3.RequestBody;
import personal.rowan.paperforspotify.network.BaseRequest;

public class ImgurUploadImageRequest
		extends BaseRequest<ImgurUploadImageResponse> {

	private static final String CLIENT_ID = "47f89063108db8c";
	/*private static final String REDIRECT_URL = "https://papermessenger";
	private static final String CLIENT_SECRET = "7b18c8e9291cc85f2aa7e0a1f548f68e2e610473";*/
	private static final String SCHEME = "https";
	private static final String BASE_URL = "api.imgur.com";
	private static final String VERSION_NUMBER = "3";
	private static final String PATH = "image";
	private static final String AUTH = "Client-ID " + CLIENT_ID;

	private String mTitle;
	private String mDescription;
	private File mFile;

	public ImgurUploadImageRequest(String title, String description,  File file) {
		mTitle = title;
		mDescription = description;
		mFile = file;
	}

	@Override
	protected ImgurUploadImageResponse buildResponse(String responseString, Exception e) {
		return new ImgurUploadImageResponse(responseString, e);
	}

	@Override
	protected RequestBody buildRequestBody() {
		return RequestBody.create(null, mFile);
	}

	@Override
	protected String getUrl() {
		return new Uri.Builder()
				.scheme(SCHEME)
				.authority(BASE_URL)
				.appendPath(VERSION_NUMBER)
				.appendPath(PATH)
				.appendQueryParameter("title", mTitle)
				.appendQueryParameter("description", mDescription)
				.build().toString();
	}

	@Override
	protected MethodType getMethodType() {
		return MethodType.POST;
	}

	@Override
	protected void addHeaders(Request.Builder builder) {
		builder.addHeader("Authorization", AUTH);
	}

}
