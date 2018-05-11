package personal.rowan.paperforspotify.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public abstract class BaseResponse {

	protected JSONObject mResponseObject;
	private Exception mException;

	public BaseResponse(String responseString, Exception exception) {
		mResponseObject = toJson(responseString);
		mException = exception;
	}

	public Exception getException() {
		return mException;
	}

	protected JSONObject toJson(String responseString) {
		try {
			return new JSONObject(responseString);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "JSON error");
		} catch (NullPointerException e) {
			Log.e(this.getClass().getName(), "responseString is null");
		}

		return null;
	}

	protected JSONObject getJsonObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getJSONObject(key);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "JSON error: " + key);
		}

		return null;
	}

	protected JSONObject getJsonObject(JSONArray jsonArray, int index) {
		try {
			return jsonArray.getJSONObject(index);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "JSON error: " + index);
		}

		return null;
	}

	protected JSONArray getJsonArray(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getJSONArray(key);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "JSON error: " + key);
		}

		return null;
	}

	protected String optString(JSONObject jsonObject, String key) {
		return optString(jsonObject, key, null);
	}

	protected String optString(JSONObject jsonObject, String key, String fallback) {
		String encodedAttribute = fallback != null ? jsonObject.optString(key, fallback) : jsonObject.optString(key);
		try {
			return URLDecoder.decode(encodedAttribute, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return encodedAttribute;
		}
	}

	protected boolean optBoolean(JSONObject jsonObject, String key) {
		return optBoolean(jsonObject, key, false);
	}

	protected boolean optBoolean(JSONObject jsonObject, String key, boolean fallback) {
		String attribute = jsonObject.optString(key);
		if(attribute == null) {
			return fallback;
		} else {
			return attribute.equals("1");
		}
	}

}
