package personal.rowan.paperforspotify.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.squareup.picasso.Picasso;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Track;

public class PlayerActivity
		extends SpotifyActivity
		implements PlayerNotificationCallback {

	public static final String ARGS_TRACK = "ARGS_TRACK";

	private Track mTrack;
	private boolean mIsPlaying;
	private boolean mIsStarted;

	private ImageView ivPlayPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_player);

		Bundle args = getIntent().getExtras();
		if(args != null) {
			mTrack = args.getParcelable(ARGS_TRACK);
		}

		if(mTrack == null) {
			showMessage(getString(R.string.error_paper_song_not_found));
			finish();
			return;
		}

		mSpotifyManager.addPlayerCallbacks(this, this);

		Toolbar tbPlayer = (Toolbar) findViewById(R.id.act_player_tb);
		setSupportActionBar(tbPlayer);
		setUpButton();
		setTitle(getString(R.string.act_player_tb_title));

		ImageView ivAlbumArt = (ImageView) findViewById(R.id.act_player_album_art_iv);
		TextView tvSongName = (TextView) findViewById(R.id.act_player_song_tv);
		TextView tvArtistName = (TextView) findViewById(R.id.act_player_artist_tv);
		ivPlayPause = (ImageView) findViewById(R.id.act_player_play_pause_iv);
		ivPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mIsPlaying) {
					mSpotifyManager.pause(PlayerActivity.this);
				} else if (mIsStarted) {
					mSpotifyManager.resume(PlayerActivity.this);
				} else {
					mSpotifyManager.play(mTrack.getUri(), PlayerActivity.this);
				}
			}
		});

		tvSongName.setText(mTrack.getName());
		tvArtistName.setText(mTrack.getArtistName());
		Picasso.with(this)
				.load(mTrack.getImageUrlLarge())
				.placeholder(R.drawable.ic_audiotrack_black_24dp)
				.into(ivAlbumArt);
	}

	private void handlePlayPause(boolean isPlaying) {
		ivPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp);
	}

	@Override
	public void onStop() {
		mSpotifyManager.pause(this);
		super.onStop();
	}

	@Override
	public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
		mIsPlaying = playerState.playing;
		switch (eventType) {
			case PLAY:
				handlePlayPause(true);
				mIsStarted = true;
				break;
			case PAUSE:
				handlePlayPause(false);
				break;
			case TRACK_CHANGED:
			case END_OF_CONTEXT:
				mIsStarted = false;
				handlePlayPause(false);
				break;
			default:
				break;
		}
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {
		switch (errorType) {
			case TRACK_UNAVAILABLE:
				showMessage(getString(R.string.error_spotify_track_unavailable));
				handlePlayPause(false);
				break;
			case ERROR_UNKNOWN:
				showMessage(getString(R.string.error_spotify_unknown));
				handlePlayPause(false);
				break;
			default:
				break;
		}
	}

}
