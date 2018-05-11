package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class ChatsListViewHolder
		extends RecyclerView.ViewHolder {

	public RelativeLayout rlContainer;
	public ImageView ivSongImage;
	public TextView tvName;
	public TextView tvDescription;
	public TextView tvTimestamp;

	public ChatsListViewHolder(View itemView) {
		super(itemView);
		rlContainer = (RelativeLayout) itemView.findViewById(R.id.viewholder_chats_list_container_rl);
		ivSongImage = (ImageView) itemView.findViewById(R.id.viewholder_chats_list_iv);
		tvName = (TextView) itemView.findViewById(R.id.viewholder_chats_list_name_tv);
		tvDescription = (TextView) itemView.findViewById(R.id.viewholder_chats_list_description_tv);
		tvTimestamp = (TextView) itemView.findViewById(R.id.viewholder_chats_list_timestamp_tv);
	}

}
