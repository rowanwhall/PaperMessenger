package personal.rowan.paperforspotify.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Track
		implements Parcelable {

	private String name;
	private String uri;
	private String url;
	private String imageUrlLarge;
	private String imageUrlMedium;
	private String imageUrlSmall;
	private String artistName;

	public Track(String name, String uri, String url, String imageUrlLarge, String imageUrlMedium, String imageUrlSmall, String artistName) {
		this.name = name;
		this.uri = uri;
		this.url = url;
		this.imageUrlLarge = imageUrlLarge;
		this.imageUrlMedium = imageUrlMedium;
		this.imageUrlSmall = imageUrlSmall;
		this.artistName = artistName;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public String getUrl() {
		return url;
	}

	public String getImageUrlLarge() {
		return imageUrlLarge;
	}

	public String getImageUrlSmall() {
		return imageUrlSmall;
	}

	public String getImageUrlMedium() {
		return imageUrlMedium;
	}

	public String getArtistName() { return artistName; }

	public boolean isValid() {
		return !TextUtils.isEmpty(uri) || !TextUtils.isEmpty(url);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.uri);
		dest.writeString(this.url);
		dest.writeString(this.imageUrlLarge);
		dest.writeString(this.imageUrlMedium);
		dest.writeString(this.imageUrlSmall);
		dest.writeString(this.artistName);
	}

	private Track(Parcel in) {
		this.name = in.readString();
		this.uri = in.readString();
		this.url = in.readString();
		this.imageUrlLarge = in.readString();
		this.imageUrlMedium = in.readString();
		this.imageUrlSmall = in.readString();
		this.artistName = in.readString();
	}

	public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
		public Track createFromParcel(Parcel source) {
			return new Track(source);
		}

		public Track[] newArray(int size) {
			return new Track[size];
		}
	};
}
