package personal.rowan.paperforspotify.ui.fragment.dialog;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.loader.SpotifySongLoader;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.manager.SpotifyManager;
import personal.rowan.paperforspotify.ui.activity.SpotifyActivity;
import personal.rowan.paperforspotify.ui.adapter.list.SpotifySongSearchListAdapter;

public class SpotifySongSearchDialogFragment
		extends BaseDialogFragment
		implements LoaderManager.LoaderCallbacks<List<Track>> {

	public static final String TAG = "SpotifySongSearchDialog";
	public static final String ARGS_GROUP_SONG = "ARGS_GROUP_SONG";

	private static final String ARGS_SEARCH_RESULTS = "ARGS_SEARCH_RESULTS";
	private static final String ARGS_SELECTED_TRACK = "ARGS_SELECTED_TRACK";

	private TextInputEditText tietSearchTerms;
	private ProgressBar pbSongSearch;

	private SpotifySongSearchListAdapter mAdapter;
	private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String searchTerms = tietSearchTerms.getText().toString().trim();
			if(!TextUtils.isEmpty(searchTerms)) {
				mAdapter.setData(new ArrayList<Track>());
				pbSongSearch.setVisibility(View.VISIBLE);

				Bundle args = new SpotifySongLoader.ArgsBuilder(mPreferenceManager.getSpotifyAccessToken())
						.setSearchTerms(searchTerms)
						.build();
				startLoader(LoaderId.SPOTIFY_SONG, args, SpotifySongSearchDialogFragment.this);
			}
		}
	};

	@Inject
	PreferenceManager mPreferenceManager;
	@Inject
	SpotifyManager mSpotifyManager;
	@Inject
	LoaderFactory mLoaderFactory;

	private SpotifyActivity mSpotifyContext;
	private boolean mGroupSong;

	@Override
	protected int layoutResource() {
		return R.layout.dialog_frag_spotify_song_search;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);

		Bundle args = getArguments();
		if(args != null) {
			mGroupSong = args.getBoolean(ARGS_GROUP_SONG);
		}
		mSpotifyContext = (SpotifyActivity) getActivity();
		mAdapter = new SpotifySongSearchListAdapter(new SpotifySongSearchListAdapter.ISongSearchAdapter() {
			@Override
			public void onSongSelected() {
				getPositiveButton().callOnClick();
			}
		});
		if(savedInstanceState != null) {
			mGroupSong = savedInstanceState.getBoolean(ARGS_GROUP_SONG);
			List<Track> searchResults = savedInstanceState.getParcelableArrayList(ARGS_SEARCH_RESULTS);
			int selectedTrack = savedInstanceState.getInt(ARGS_SELECTED_TRACK, -1);
			mAdapter.setData(searchResults, selectedTrack);
		}
	}

	@Override
	protected void inflateLayout(View view) {
		tietSearchTerms = (TextInputEditText) view.findViewById(R.id.dialog_frag_spotify_song_search_tiet);
		pbSongSearch = (ProgressBar) view.findViewById(R.id.dialog_frag_spotify_song_search_pb);
		Button btnSearch = (Button) view.findViewById(R.id.dialog_frag_spotify_song_search_btn);
		btnSearch.setOnClickListener(mSearchClickListener);
		RecyclerView rvSongSearch = (RecyclerView) view.findViewById(R.id.dialog_frag_spotify_song_search_rv);
		rvSongSearch.setLayoutManager(new LinearLayoutManager(mSpotifyContext));
		rvSongSearch.setAdapter(mAdapter);
	}

	@Override
	protected String title() {
		return getString(R.string.dialog_frag_spotify_song_search_title);
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	protected String positiveButtonText() {
		return getString(R.string.dialog_frag_spotify_song_search_positive_btn);
	}

	@Override
	protected String negativeButtonText() {
		return getString(R.string.dialog_frag_spotify_song_search_negative_btn);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ARGS_GROUP_SONG, mGroupSong);
		outState.putParcelableArrayList(ARGS_SEARCH_RESULTS, mAdapter.getSearchResultsAsArrayList());
		outState.putInt(ARGS_SELECTED_TRACK, mAdapter.getSelectedTrackIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void afterShown() {
		getPositiveButton().setVisibility(View.GONE);
	}

	public Track getSelectedTrack() {
		return mAdapter.getSelectedTrack();
	}

	public boolean isGroupSong() {
		return mGroupSong;
	}

	@Override
	public Loader<List<Track>> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LoaderId.SPOTIFY_SONG:
				return mLoaderFactory.createSpotifySongLoader(getContext(), args);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<Track>> loader, List<Track> data) {
		pbSongSearch.setVisibility(View.GONE);

		if(data == null) {
			if(loader instanceof SpotifySongLoader && ((SpotifySongLoader) loader).isAuthorizationError()) {
				showMessage(getString(R.string.error_spotify_deauthenticated));
				mSpotifyManager.authenticate(mSpotifyContext);
			} else {
				showMessage(getString(R.string.error_network_song_search_spotify));
			}
			return;
		}

		mAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<List<Track>> loader) {
		mAdapter.setData(new ArrayList<Track>());
	}
}
