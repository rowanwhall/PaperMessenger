package personal.rowan.paperforspotify.data.loader;

import android.support.annotation.IntDef;

@IntDef({
		LoaderId.CHAT,
		LoaderId.MESSAGE,
		LoaderId.FRIEND,
		LoaderId.USER,
		LoaderId.READ_RECEIPT,
		LoaderId.SPOTIFY_SONG
})
public @interface LoaderId {
	int CHAT = 0;
	int MESSAGE = 1;
	int FRIEND = 2;
	int USER = 3;
	int READ_RECEIPT = 4;
	int SPOTIFY_SONG = 5;
}
