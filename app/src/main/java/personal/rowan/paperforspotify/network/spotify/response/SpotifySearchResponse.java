package personal.rowan.paperforspotify.network.spotify.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.network.BaseResponse;

public class SpotifySearchResponse
		extends BaseResponse {

	private List<Track> mTracks = new ArrayList<>();

	public SpotifySearchResponse(String responseString, Exception e) {
		super(responseString, e);
		if(mResponseObject != null) {
			JSONObject tracksObject = getJsonObject(mResponseObject, "tracks");
			if(tracksObject != null) {
				JSONArray itemsArray = getJsonArray(tracksObject, "items");
				if(itemsArray != null) {
					for (int i = 0; i < itemsArray.length(); i++) {
						JSONObject trackObject = getJsonObject(itemsArray, i);
						if(trackObject != null) {
							String name = optString(trackObject, "name");
							String uri = optString(trackObject, "uri");
							String url = optString(trackObject, "preview_url");
							String imageUrlLarge = "";
							String imageUrlMedium = "";
							String imageUrlSmall = "";
							StringBuilder artist = new StringBuilder();
							JSONObject albumObject = getJsonObject(trackObject, "album");
							if(albumObject != null) {
								JSONArray imagesArray = getJsonArray(albumObject, "images");
								if(imagesArray != null) {
									JSONObject imageLargeObject = getJsonObject(imagesArray, 0);
									JSONObject imageMediumObject = getJsonObject(imagesArray, 1);
									JSONObject imageSmallObject = getJsonObject(imagesArray, 2);
									if(imageLargeObject != null) {
										imageUrlLarge = optString(imageLargeObject, "url");
									}
									if(imageMediumObject != null) {
										imageUrlMedium = optString(imageMediumObject, "url");
									}
									if(imageSmallObject != null) {
										imageUrlSmall = optString(imageSmallObject, "url");
									}
								}
							}
							JSONArray artistsArray = getJsonArray(trackObject, "artists");
							for(int j = 0; j < artistsArray.length(); j++) {
								JSONObject artistObject = getJsonObject(artistsArray, j);
								String artistName = optString(artistObject, "name");
								if(!TextUtils.isEmpty(artistName)) {
									artist.append(artistName);
									if(j < artistsArray.length() - 1) {
										artist.append(", ");
									}
								}
							}

							mTracks.add(new Track(name, uri, url, imageUrlLarge, imageUrlMedium, imageUrlSmall, artist.toString()));
						}
					}
				}
			}
		}
	}

	public List<Track> getTracks() {
		return mTracks;
	}

}
