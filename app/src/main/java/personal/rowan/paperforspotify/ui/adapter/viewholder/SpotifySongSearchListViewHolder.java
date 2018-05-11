package personal.rowan.paperforspotify.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import personal.rowan.paperforspotify.R;

public class SpotifySongSearchListViewHolder
		extends RecyclerView.ViewHolder {

	public RelativeLayout rlContainer;
	public ImageView ivAlbumArt;
	public TextView tvSongName;
	public TextView tvArtistName;

	public SpotifySongSearchListViewHolder(View itemView) {
		super(itemView);
		rlContainer = (RelativeLayout) itemView.findViewById(R.id.viewholder_spotify_song_search_container_rl);
		ivAlbumArt = (ImageView) itemView.findViewById(R.id.viewholder_spotify_song_search_list_iv);
		tvSongName = (TextView) itemView.findViewById(R.id.viewholder_spotify_song_search_song_name_tv);
		tvArtistName = (TextView) itemView.findViewById(R.id.viewholder_spotify_song_search_artist_name_tv);
	}

}
