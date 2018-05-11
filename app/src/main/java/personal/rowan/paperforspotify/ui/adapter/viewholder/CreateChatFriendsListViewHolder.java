package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class CreateChatFriendsListViewHolder
		extends RecyclerView.ViewHolder {

	public ImageView ivProfile;
	public TextView tvName;
	public CheckBox cbAdd;

	public CreateChatFriendsListViewHolder(View itemView) {
		super(itemView);
		ivProfile = (ImageView) itemView.findViewById(R.id.viewholder_create_chat_friends_list_profile_iv);
		tvName = (TextView) itemView.findViewById(R.id.viewholder_create_chat_friends_list_name_tv);
		cbAdd = (CheckBox) itemView.findViewById(R.id.viewholder_create_chat_friends_list_cb);
	}

}
