package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class FriendSearchListViewHolder
		extends RecyclerView.ViewHolder {

	public ImageView ivProfile;
	public TextView tvName;
	public ImageView ivAdd;

	public FriendSearchListViewHolder(View itemView) {
		super(itemView);
		ivProfile = (ImageView) itemView.findViewById(R.id.viewholder_friend_search_list_profile_iv);
		tvName = (TextView) itemView.findViewById(R.id.viewholder_friend_search_list_name_tv);
		ivAdd = (ImageView) itemView.findViewById(R.id.viewholder_friend_search_list_pending_add_iv);
	}

}
