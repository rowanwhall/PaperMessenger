package personal.rowan.paperforspotify.ui.adapter.list;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.gcm.PaperGcmListenerService;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.PicassoCircleTransformation;
import personal.rowan.paperforspotify.network.paper.request.PaperAnswerFriendshipRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperAnswerFriendshipResponse;
import personal.rowan.paperforspotify.ui.adapter.viewholder.FriendsListHeaderViewHolder;
import personal.rowan.paperforspotify.ui.adapter.viewholder.FriendsListPendingViewHolder;
import personal.rowan.paperforspotify.ui.adapter.viewholder.FriendsListViewHolder;

public class FriendsListAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String USER_ID_HEADER_PENDING = "USER_ID_HEADER_PENDING";
	private static final String USER_ID_HEADER_FRIENDS = "USER_ID_HEADER_FRIENDS";

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_PENDING = 1;
	private static final int VIEW_TYPE_ACCEPTED = 2;

	private static final Comparator<Friend> mFriendComparator = new Comparator<Friend>() {
		@Override
		public int compare(Friend lhs, Friend rhs) {

			// rejected friendships always at bottom, will be removed
			if(rhs.isRejected()) {
				return -1;
			}
			if(lhs.isRejected()) {
				return 1;
			}

			// pending sent friendships just above rejected ones, will be removed
			if(rhs.isRecipientPending()) {
				return -1;
			}
			if(lhs.isRecipientPending()) {
				return 1;
			}

			// pending header always at top
			if(lhs.getUserId().equals(USER_ID_HEADER_PENDING)) {
				return -1;
			}
			if(rhs.getUserId().equals(USER_ID_HEADER_PENDING)) {
				return 1;
			}

			// pending friends always above friends header
			if(lhs.isUserPending() && rhs.getUserId().equals(USER_ID_HEADER_FRIENDS)) {
				return -1;
			}
			if(rhs.isUserPending() && lhs.getUserId().equals(USER_ID_HEADER_FRIENDS)) {
				return 1;
			}

			// friends header always above accepted friends
			if(lhs.getUserId().equals(USER_ID_HEADER_FRIENDS) && rhs.isAccepted()) {
				return -1;
			}
			if(rhs.getUserId().equals(USER_ID_HEADER_FRIENDS) && rhs.isAccepted()) {
				return 1;
			}

			// pending friends always above accepted friends
			if(lhs.isUserPending() && rhs.isAccepted()) {
				return -1;
			}
			if(rhs.isUserPending() && lhs.isAccepted()) {
				return 1;
			}

			//for friends in the same category, sort alphabetically
			return lhs.getName().toUpperCase().compareTo(rhs.getName().toUpperCase());
		}
	};

	@Inject
	PreferenceManager mPreferenceManager;

	private List<Friend> mFriends;
	private LocalBroadcastManager mLocalBroadcastManager;

	public FriendsListAdapter(Context context) {
		PaperApplication.getInstance().component().inject(this);

		setData(new ArrayList<Friend>());
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
	}

	public void setData(List<Friend> friends) {
		mFriends = friends;
		if(mFriends != null && !mFriends.isEmpty()) {
			mFriends.add(new Friend(USER_ID_HEADER_PENDING, "", "", false, false, false));
			mFriends.add(new Friend(USER_ID_HEADER_FRIENDS, "", "", false, false, false));
			Collections.sort(mFriends, mFriendComparator);
			filterFriends();
		}
		notifyDataSetChanged();
	}

	private void filterFriends() {
		boolean pendingHeaderFound = false;
		boolean friendsHeaderFound = false;
		List<Friend> toRemove = new ArrayList<>();
		for(int i = mFriends.size() - 1; i >= 0; i--) {
			Friend friend = mFriends.get(i);
			if(friend.getUserId().equals(USER_ID_HEADER_PENDING)) {
				if(pendingHeaderFound) {
					toRemove.add(friend);
				} else {
					pendingHeaderFound = true;
				}
			}
			if(friend.getUserId().equals(USER_ID_HEADER_FRIENDS)) {
				if(friendsHeaderFound) {
					toRemove.add(friend);
				} else {
					friendsHeaderFound = true;
				}
			}
			if(friend.isRejected() || friend.isRecipientPending()) {
				toRemove.add(friend);
			} else {
				break;
			}
		}

		if(mFriends.size() >= 2) {
			Friend pendingHeader = mFriends.get(0);
			Friend nextItem = mFriends.get(1);
			if(pendingHeader.getUserId().equals(USER_ID_HEADER_PENDING) && nextItem.getUserId().equals(USER_ID_HEADER_FRIENDS)) {
				toRemove.add(pendingHeader);
			}
		}

		mFriends.removeAll(toRemove);
	}

	private void acceptFriend(int position) {
		Friend friend = mFriends.get(position);
		friend.setPending(false);
		mFriends.set(position, friend);
		Collections.sort(mFriends, mFriendComparator);
		notifyItemChanged(0);
		notifyItemMoved(position, getIndex(friend.getUserId()));
		notifyItemRangeChanged(position, getIndex(friend.getUserId()));
	}

	private void rejectFriend(int position) {
		Friend friend = mFriends.get(position);
		friend.setPending(false);
		friend.setRejected(true);
		mFriends.set(position, friend);
		Collections.sort(mFriends, mFriendComparator);
		filterFriends();
		notifyItemChanged(0);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, getItemCount());
	}

	private int getIndex(String friendId) {
		for(int i = 0; i < mFriends.size(); i++) {
			if(mFriends.get(i).getUserId().equals(friendId)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch(viewType) {
			case VIEW_TYPE_HEADER:
				return new FriendsListHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friends_list_header, parent, false));
			case VIEW_TYPE_PENDING:
				return new FriendsListPendingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friends_list_pending, parent, false));
			case VIEW_TYPE_ACCEPTED:
			default:
				return new FriendsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friends_list, parent, false));
		}
	}

	@Override
	public int getItemViewType(int position) {
		Friend friend = mFriends.get(position);
		String userId = friend.getUserId();
		if(userId.equals(USER_ID_HEADER_PENDING) || userId.equals(USER_ID_HEADER_FRIENDS)) {
			return VIEW_TYPE_HEADER;
		} else if(friend.isUserPending()) {
			return VIEW_TYPE_PENDING;
		} else if(friend.isAccepted()) {
			return VIEW_TYPE_ACCEPTED;
		}

		//error
		return -1;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof FriendsListViewHolder) {
			final Friend friend = mFriends.get(position);
			FriendsListViewHolder friendsListViewHolder = (FriendsListViewHolder) holder;

			Picasso.with(PaperApplication.getInstance())
					.load(friend.getImageUrl())
					.placeholder(R.drawable.ic_person_black_24dp)
					.transform(new PicassoCircleTransformation())
					.into(friendsListViewHolder.ivProfile);

			friendsListViewHolder.tvName.setText(friend.getName());

			if(friendsListViewHolder instanceof FriendsListPendingViewHolder) {
				FriendsListPendingViewHolder friendsListPendingViewHolder = (FriendsListPendingViewHolder) friendsListViewHolder;

				final String userId = mPreferenceManager.getUserId();
				friendsListPendingViewHolder.ivAccept.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new PaperAnswerFriendshipRequest
								(friend.getUserId(), userId, false)
								.execute(new NetworkCallback<PaperAnswerFriendshipResponse>() {
									@Override
									public void onResponse(Response response, PaperAnswerFriendshipResponse responseObject) {
										triggerRefresh();
									}

									@Override
									public void onFailure(Exception e) {
										triggerRefresh();
									}
								});
						acceptFriend(holder.getAdapterPosition());
					}
				});
				friendsListPendingViewHolder.ivReject.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new PaperAnswerFriendshipRequest
								(friend.getUserId(), userId, true)
								.execute(new NetworkCallback<PaperAnswerFriendshipResponse>() {
									@Override
									public void onResponse(Response response, PaperAnswerFriendshipResponse responseObject) {
										triggerRefresh();
									}

									@Override
									public void onFailure(Exception e) {
										triggerRefresh();
									}
								});
						rejectFriend(holder.getAdapterPosition());
					}
				});
			}
		} else if(holder instanceof FriendsListHeaderViewHolder) {
			String userId = mFriends.get(position).getUserId();
			FriendsListHeaderViewHolder friendsListHeaderViewHolder = (FriendsListHeaderViewHolder) holder;

			Context context = PaperApplication.getInstance();
			if(userId.equals(USER_ID_HEADER_PENDING)) {
				friendsListHeaderViewHolder.tvHeader.setText(context.getString(R.string.frag_friends_header_pending));

				/*if(position + 1 < getItemCount()) {
					friendsListHeaderViewHolder.tvHeader.setVisibility(mFriends.get(position + 1).getUserId().equals(USER_ID_HEADER_FRIENDS) ? View.GONE : View.VISIBLE);
				}*/
			} else if(userId.equals(USER_ID_HEADER_FRIENDS)) {
				friendsListHeaderViewHolder.tvHeader.setText(context.getString(R.string.frag_friends_header_accepted));
				//friendsListHeaderViewHolder.tvHeader.setVisibility(View.VISIBLE);
			}
		}
	}

	private void triggerRefresh() {
		mLocalBroadcastManager.sendBroadcast(new Intent(PaperGcmListenerService.ACTION_UPDATE_FRIEND));
	}

	@Override
	public int getItemCount() {
		return mFriends.size();
	}
}
