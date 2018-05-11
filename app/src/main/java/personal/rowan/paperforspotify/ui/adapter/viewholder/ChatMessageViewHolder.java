package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class ChatMessageViewHolder
		extends RecyclerView.ViewHolder {

	public LinearLayout llMessageContainer;
	public TextView tvName;
	public TextView tvMessage;
	public TextView tvTimestamp;
	public ImageView ivProfile;
	public TextView tvReadReceipt;
	public View viewPlaceholder;
	public RelativeLayout rlSongContainer;
	public ImageView ivSong;
	public TextView tvSongName;
	public TextView tvArtistName;

	private boolean mSelf;

	public ChatMessageViewHolder(View itemView, boolean self) {
		super(itemView);
		llMessageContainer = (LinearLayout) itemView.findViewById(R.id.viewholder_message_list_container_ll);
		tvName = (TextView) itemView.findViewById(R.id.viewholder_message_list_name_tv);
		tvMessage = (TextView) itemView.findViewById(R.id.viewholder_message_list_message_tv);
		tvTimestamp = (TextView) itemView.findViewById(R.id.viewholder_message_list_timestamp_tv);
		ivProfile = (ImageView) itemView.findViewById(R.id.viewholder_message_list_iv);
		viewPlaceholder = itemView.findViewById(R.id.viewholder_message_list_placeholder_view);
		tvReadReceipt = (TextView) itemView.findViewById(R.id.viewholder_message_list_read_receipt_tv);
		rlSongContainer = (RelativeLayout) itemView.findViewById(R.id.viewholder_message_list_song_container_rl);
		ivSong = (ImageView) itemView.findViewById(R.id.viewholder_message_list_song_iv);
		tvSongName = (TextView) itemView.findViewById(R.id.viewholder_message_list_song_name_tv);
		tvArtistName = (TextView) itemView.findViewById(R.id.viewholder_message_list_artist_name_tv);

		mSelf = self;
	}

	public boolean isSelf() {
		return mSelf;
	}
}
