package personal.rowan.paperforspotify.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Track;

public class ChatSongHeader extends LinearLayout {

	private ImageView ivSongImage;
	private TextView tvSongName;
	private TextView tvArtistName;
	private Button btnPlay;

	public ChatSongHeader(Context context) {
		super(context);
		init();
	}

	public ChatSongHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChatSongHeader(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.view_chat_song_header, this);

		ivSongImage = (ImageView) findViewById(R.id.view_chat_song_header_song_iv);
		tvSongName = (TextView) findViewById(R.id.view_chat_song_header_song_name_tv);
		tvArtistName = (TextView) findViewById(R.id.view_chat_song_header_artist_name_tv);
		btnPlay = (Button) findViewById(R.id.view_chat_song_header_play_btn);
	}

	public void setPlayListener(OnClickListener onClickListener) {
		btnPlay.setOnClickListener(onClickListener);
	}

	public void setSong(Track track) {
		tvSongName.setText(track.getName());
		tvArtistName.setText(track.getArtistName());
		Picasso.with(getContext())
				.load(track.getImageUrlSmall())
				.placeholder(R.drawable.ic_person_black_24dp)
				.into(ivSongImage);
	}

}
