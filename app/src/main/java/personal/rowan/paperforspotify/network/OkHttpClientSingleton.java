package personal.rowan.paperforspotify.network;

import okhttp3.OkHttpClient;

public class OkHttpClientSingleton
		extends OkHttpClient {

	private static OkHttpClientSingleton instance;

	private OkHttpClientSingleton() {

	}

	public static OkHttpClientSingleton getInstance() {
		if(instance == null) {
			instance = new OkHttpClientSingleton();
		}

		return instance;
	}
}
