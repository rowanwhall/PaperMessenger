package personal.rowan.paperforspotify.data.loader;

import android.content.Context;

import java.util.List;

import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.manager.SpotifyManager;
import personal.rowan.paperforspotify.network.spotify.request.SpotifySearchRequest;
import personal.rowan.paperforspotify.network.spotify.response.SpotifySearchResponse;

public class SpotifySongLoader
		extends BaseLoader<List<Track>> {

	public static final String ARGS_SPOTIFY_TOKEN = "ARGS_SPOTIFY_TOKEN";
	public static final String ARGS_SEARCH_TERMS = "ARGS_SEARCH_TERMS";

	private String mSpotifyAccessToken;
	private String mSearchTerms;

	private boolean mAuthorizationError;

	public SpotifySongLoader(Context context, String spotifyAccessToken, String searchTerms) {
		super(context);
		mSpotifyAccessToken = spotifyAccessToken;
		mSearchTerms = searchTerms;
	}

	@Override
	public List<Track> loadInBackground() {
		SpotifySearchResponse response =
				new SpotifySearchRequest(mSpotifyAccessToken, mSearchTerms)
				.executeOnThread();
		Exception e = response.getException();
		if(e != null) {
			if (e.getMessage().equals(SpotifyManager.ERROR_CODE_UNAUTHORIZED)) {
				mAuthorizationError = true;
			}
			return null;
		}
		return response.getTracks();
	}

	public boolean isAuthorizationError() {
		return mAuthorizationError;
	}

	public static class ArgsBuilder
			extends BaseArgsBuilder {

		public ArgsBuilder(String spotifyToken) {
			super();
			writeString(ARGS_SPOTIFY_TOKEN, spotifyToken);
		}

		public ArgsBuilder setSearchTerms(String searchTerms) {
			writeString(ARGS_SEARCH_TERMS, searchTerms);
			return this;
		}

	}

}
