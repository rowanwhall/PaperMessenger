package personal.rowan.paperforspotify.ui.adapter.list;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.ui.adapter.viewholder.SpotifySongSearchListViewHolder;

public class SpotifySongSearchListAdapter
		extends RecyclerView.Adapter<SpotifySongSearchListViewHolder> {

	private List<Track> mSearchResults;
	private int mSelectedTrack;
	private ISongSearchAdapter mSongSearchCallback;

	public SpotifySongSearchListAdapter(ISongSearchAdapter callback) {
		mSongSearchCallback = callback;
		setData(new ArrayList<Track>());
	}

	public void setData(List<Track> searchResults) {
		setData(searchResults, -1);
	}

	public void setData(List<Track> searchResults, int selectedTrack) {
		mSearchResults = searchResults;
		mSelectedTrack = selectedTrack;
		notifyDataSetChanged();
	}

	@Override
	public SpotifySongSearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new SpotifySongSearchListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_spotify_song_search_list, parent, false));
	}

	@Override
	public void onBindViewHolder(final SpotifySongSearchListViewHolder holder, int position) {
		Track track = mSearchResults.get(position);
		holder.tvSongName.setText(track.getName());
		holder.tvArtistName.setText(track.getArtistName());
		Picasso.with(PaperApplication.getInstance())
				.load(track.getImageUrlSmall())
				.placeholder(R.drawable.ic_person_black_24dp)
				.into(holder.ivAlbumArt);
		holder.rlContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectedTrack = holder.getAdapterPosition();
				if(mSongSearchCallback != null) {
					mSongSearchCallback.onSongSelected();
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mSearchResults.size();
	}

	public ArrayList<Track> getSearchResultsAsArrayList() {
		return new ArrayList<>(mSearchResults);
	}

	public int getSelectedTrackIndex() {
		return mSelectedTrack;
	}

	public Track getSelectedTrack() {
		return mSearchResults.get(mSelectedTrack);
	}

	public interface ISongSearchAdapter {
		void onSongSelected();
	}

}
