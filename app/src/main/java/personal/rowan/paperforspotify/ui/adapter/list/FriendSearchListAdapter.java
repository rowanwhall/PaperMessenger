package personal.rowan.paperforspotify.ui.adapter.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.data.model.UserSearchResult;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.PicassoCircleTransformation;
import personal.rowan.paperforspotify.network.paper.request.PaperRequestFriendRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperRequestFriendResponse;
import personal.rowan.paperforspotify.ui.adapter.viewholder.FriendSearchListViewHolder;

public class FriendSearchListAdapter
		extends RecyclerView.Adapter<FriendSearchListViewHolder> {

	private List<UserSearchResult> mSearchResults;
	private String mUserId;

	public FriendSearchListAdapter(String userId) {
		setData(new ArrayList<UserSearchResult>());
		mUserId = userId;
	}

	public void setData(List<UserSearchResult> searchResults) {
		mSearchResults = searchResults;
		notifyDataSetChanged();
	}

	@Override
	public FriendSearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FriendSearchListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friend_search_list, parent, false));
	}

	@Override
	public void onBindViewHolder(final FriendSearchListViewHolder holder, final int position) {
		final int adapterPosition = holder.getAdapterPosition();
		final UserSearchResult searchResult = mSearchResults.get(adapterPosition);
		holder.tvName.setText(searchResult.getName());
		Picasso.with(PaperApplication.getInstance())
				.load(searchResult.getImageUrl())
				.placeholder(R.drawable.ic_person_black_24dp)
				.transform(new PicassoCircleTransformation())
				.into(holder.ivProfile);
		if(searchResult.canSendRequest()) {
			holder.ivAdd.setVisibility(View.VISIBLE);
			holder.ivAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new PaperRequestFriendRequest
							(mUserId, searchResult.getUserId())
							.execute(new NetworkCallback<PaperRequestFriendResponse>() {
								@Override
								public void onResponse(Response response, PaperRequestFriendResponse responseObject) {
								}

								@Override
								public void onFailure(Exception e) {
								}
							});
					mSearchResults.get(adapterPosition).setCanSendRequest(false);
					FriendSearchListAdapter.this.notifyItemChanged(adapterPosition);
				}
			});
		} else {
			holder.ivAdd.setVisibility(View.INVISIBLE);
			holder.ivAdd.setOnClickListener(null);
		}
	}

	@Override
	public int getItemCount() {
		return mSearchResults.size();
	}

	public ArrayList<UserSearchResult> getSearchResultsAsArrayList() {
		return new ArrayList<>(mSearchResults);
	}

}
