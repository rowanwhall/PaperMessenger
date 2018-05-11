package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import personal.rowan.paperforspotify.R;

public class FriendsListPendingViewHolder
		extends FriendsListViewHolder{

	public ImageView ivAccept;
	public ImageView ivReject;

	public FriendsListPendingViewHolder(View itemView) {
		super(itemView);
		ivAccept = (ImageView) itemView.findViewById(R.id.viewholder_friends_list_pending_accept_iv);
		ivReject = (ImageView) itemView.findViewById(R.id.viewholder_friends_list_pending_reject_iv);
	}

}
