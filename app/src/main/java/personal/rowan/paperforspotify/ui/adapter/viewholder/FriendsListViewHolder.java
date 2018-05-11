package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class FriendsListViewHolder
		extends RecyclerView.ViewHolder {

	public ImageView ivProfile;
	public TextView tvName;

	public FriendsListViewHolder(View itemView) {
		super(itemView);
		ivProfile = (ImageView) itemView.findViewById(R.id.viewholder_friends_list_profile_iv);
		tvName = (TextView) itemView.findViewById(R.id.viewholder_friends_list_name_tv);
	}

}

