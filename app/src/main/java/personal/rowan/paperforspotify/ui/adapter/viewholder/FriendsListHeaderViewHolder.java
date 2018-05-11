package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class FriendsListHeaderViewHolder
		extends RecyclerView.ViewHolder {

	public TextView tvHeader;

	public FriendsListHeaderViewHolder(View itemView) {
		super(itemView);

		tvHeader = (TextView) itemView.findViewById(R.id.viewholder_friends_list_header_tv);
	}
}
