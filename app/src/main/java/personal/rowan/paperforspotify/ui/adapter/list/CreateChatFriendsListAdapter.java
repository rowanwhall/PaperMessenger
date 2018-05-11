package personal.rowan.paperforspotify.ui.adapter.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.PicassoCircleTransformation;
import personal.rowan.paperforspotify.ui.adapter.viewholder.CreateChatFriendsListViewHolder;

public class CreateChatFriendsListAdapter
		extends RecyclerView.Adapter<CreateChatFriendsListViewHolder> {

	private List<Friend> mFriends;
	private HashMap<String, Friend> mSelectedFriends;
	private Button mCreateButton;
	private boolean mHasName;

	public CreateChatFriendsListAdapter(List<Friend> friends) {
		mFriends = friends;
		filterFriends();
		mSelectedFriends = new HashMap<>();
		notifyDataSetChanged();
	}

	public void setSelectedFriends(List<Friend> selectedFriends) {
		if(selectedFriends == null) {
			return;
		}

		for(Friend friend : selectedFriends) {
			mSelectedFriends.put(friend.getUserId(), friend);
		}
		if(!selectedFriends.isEmpty()) {
			notifyDataSetChanged();
		}
	}

	public void setCreateButton(Button createButton) {
		mCreateButton = createButton;
	}

	public void setHasName(boolean hasName) {
		mHasName = hasName;
		mCreateButton.setEnabled(mHasName && mSelectedFriends.size() > 0);
	}

	private void filterFriends() {
		List<Friend> toRemove = new ArrayList<>();
		for(Friend friend : mFriends) {
			if(friend.isPending() || friend.isRejected()) {
				toRemove.add(friend);
			}
		}
		mFriends.removeAll(toRemove);
	}

	public ArrayList<Friend> getSelectedFriends() {
		return new ArrayList<>(mSelectedFriends.values());
	}

	@Override
	public CreateChatFriendsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CreateChatFriendsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_create_chat_friends_list, parent, false));
	}

	@Override
	public void onBindViewHolder(CreateChatFriendsListViewHolder holder, int position) {
		final Friend friend = mFriends.get(position);
		holder.tvName.setText(friend.getName());
		Picasso.with(PaperApplication.getInstance())
				.load(friend.getImageUrl())
				.placeholder(R.drawable.ic_person_black_24dp)
				.transform(new PicassoCircleTransformation())
				.into(holder.ivProfile);
		final String userId = friend.getUserId();
		holder.cbAdd.setChecked(mSelectedFriends.containsKey(userId));
		holder.cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mSelectedFriends.put(userId, friend);
				} else if(mSelectedFriends.containsKey(userId)) {
					mSelectedFriends.remove(userId);
				}

				mCreateButton.setEnabled(mHasName && mSelectedFriends.size() > 0);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mFriends.size();
	}

}
