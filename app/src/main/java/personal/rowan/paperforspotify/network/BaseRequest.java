package personal.rowan.paperforspotify.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseRequest <T extends BaseResponse> {

	private Request mRequest;
	private String mData;
	private Handler mHandler;

	public void execute() {
		execute(null);
	}

	public void execute(final NetworkCallback<T> callback) {
		buildCall().enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				getHandler().post(new Runnable() {
					@Override
					public void run() {
						if(callback != null) {
							callback.onFailure(e);
						}
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response)  {
				try {
					final String responseBody = response.body().string();
					getHandler().post(new Runnable() {
						@Override
						public void run() {
							if (callback != null) {
								if(response.isSuccessful()) {
									callback.onResponse(response, buildResponse(responseBody, null));
								} else {
									callback.onFailure(new Exception(String.valueOf(response.code())));
								}
							}
						}
					});
				} catch (IOException e) {
					Log.e(BaseRequest.this.getClass().getName(), "IOException on response");
					if(callback != null) {
						callback.onFailure(new Exception("IOException"));
					}
				}
			}
		});
	}

	public T executeOnThread() {
		Call call = buildCall();
		String responseString = null;
		Exception exception = null;
		try {
			Response response = call.execute();
			responseString = response.body().string();
			if(!response.isSuccessful()) {
				exception = new Exception(String.valueOf(response.code()));
			}
		} catch (IOException e) {
			exception = e;
			Log.e(BaseRequest.this.getClass().getName(), "IOException on response");
		}
		return buildResponse(responseString, exception);
	}

	private Handler getHandler() {
		if(mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}

		return mHandler;
	}

	protected abstract T buildResponse(String responseString, Exception e);

	protected abstract String getUrl();

	private Call buildCall() {
		Request request = getRequest();
		return OkHttpClientSingleton.getInstance().newCall(request);
	}

	private Request getRequest() {
		if (mRequest == null) {
			String url = getUrl();
			Request.Builder builder;

			switch (getMethodType()) {
				default:
				case GET:
					builder = new Request.Builder()
							.get()
							.url(url);
					break;
				case POST:
					builder = new Request.Builder()
							.url(url)
							.post(buildRequestBody());
					break;
				case PUT:
					builder = new Request.Builder()
							.url(url)
							.put(buildRequestBody());
					break;
				case DELETE:
					builder = new Request.Builder()
							.delete()
							.url(url);
					break;
			}

			if (builder != null) {
				addHeaders(builder);
				mRequest = builder.build();
			} else {
				mRequest = null;
			}
		}

		return mRequest;
	}

	protected RequestBody buildRequestBody() {
		String data = getData();
		return RequestBody.create(getMediaType(), data);
	}

	private String getData() {
		if (mData == null &&
				(getMethodType() == MethodType.PUT || getMethodType() == MethodType.POST)) {
			switch (getContentType()) {
				case CONTENT_TYPE_FORM_URLENCODED:
					mData = buildFormData();
					break;
				case CONTENT_TYPE_JSON:
				default:
					mData = buildJsonData();
					break;
			}
		}

		return mData;
	}

	@Nullable
	protected JSONObject buildBody() throws JSONException {
		return null;
	}

	private String buildFormData() {
		try {
			JSONObject requestBody = buildBody();
			if (requestBody == null) {
				return "";
			}
			Iterator<String> keys = requestBody.keys();
			StringBuilder stringBuilder = new StringBuilder();

			while (keys.hasNext()) {
				String key = keys.next();
				stringBuilder.append(key)
						.append("=")
						.append(URLEncoder.encode((String) requestBody.get(key), "UTF-8"))
						.append("&");
			}

			return stringBuilder.toString();
		} catch (JSONException | UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "Error building form data");
		}

		return null;
	}

	private String buildJsonData() {
		try {
			JSONObject body = buildBody();

			if (body == null) {
				return "";
			}

			return body.toString();
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "Error building json data");
		}

		return null;
	}

	protected MediaType getMediaType() {
		switch (getContentType()) {
			case CONTENT_TYPE_FORM_URLENCODED:
				return MediaType.parse(CONTENT_TYPE_FORM_URLENCODED);
			default:
				return null;
		}
	}

	protected void addHeaders(Request.Builder builder) {
		builder.addHeader(CONTENT_TYPE, getContentType());
	}

	@ContentType
	protected String getContentType() {
		return CONTENT_TYPE_JSON;
	}

	/**
	 * Inner classes, interfaces, enums
	 */

	@StringDef({CONTENT_TYPE_JSON, CONTENT_TYPE_FORM_URLENCODED})
	protected @interface ContentType {
	}

	protected static final String CONTENT_TYPE = "Content-Type";
	protected static final String CONTENT_TYPE_JSON = "application/json";
	protected static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

	protected abstract MethodType getMethodType();

	protected enum MethodType {
		GET,
		POST,
		PUT,
		PATCH,
		DELETE
	}

}
