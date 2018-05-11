package personal.rowan.paperforspotify.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Track;

public class ChatComposer
		extends LinearLayout {

	// Composer elements
	private EditText etChatInput;
	private Button btnSend;
	private ImageView ivAddButton;

	// Selected track elements
	private RelativeLayout rlSongContainer;
	private ImageView ivSongImage;
	private TextView tvSongName;
	private TextView tvArtistName;

	public ChatComposer(Context context) {
		super(context);
		init();
	}

	public ChatComposer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChatComposer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.view_chat_composer, this);
		etChatInput = (EditText) findViewById(R.id.view_chat_composer_input_et);
		btnSend = (Button) findViewById(R.id.view_chat_composer_send_button_btn);
		ivAddButton = (ImageView) findViewById(R.id.view_chat_composer_add_iv);

		rlSongContainer = (RelativeLayout) findViewById(R.id.view_chat_composer_song_detail_rl);
		ivSongImage = (ImageView) findViewById(R.id.view_chat_composer_song_iv);
		tvSongName = (TextView) findViewById(R.id.view_chat_composer_song_name_tv);
		tvArtistName = (TextView) findViewById(R.id.view_chat_composer_artist_name_tv);
	}

	public String getMessage() {
		return etChatInput.getText().toString()
				// % sign breaks Android's url encoding
				.replace("%", "")
				// We want to avoid blank messages consisting only of spaces
				.trim();
	}

	public void setMessage(String message) {
		etChatInput.setText(message);
	}

	public void setSendListener(OnClickListener onClickListener) {
		btnSend.setOnClickListener(onClickListener);
	}

	public void setAddListener(OnClickListener onClickListener) {
		ivAddButton.setOnClickListener(onClickListener);
	}

	public void hideSongContainer() {
		rlSongContainer.setVisibility(View.GONE);
	}

	public void revealSongContainer() {
		rlSongContainer.setVisibility(View.VISIBLE);
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
