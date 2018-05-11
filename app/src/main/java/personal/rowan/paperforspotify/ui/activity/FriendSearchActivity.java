package personal.rowan.paperforspotify.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.loader.UserLoader;
import personal.rowan.paperforspotify.data.model.UserSearchResult;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.ui.adapter.list.FriendSearchListAdapter;

public class FriendSearchActivity
		extends BaseActivity
		implements LoaderManager.LoaderCallbacks<List<UserSearchResult>> {

	private static final String ARGS_SEARCH_RESULTS = "ARGS_SEARCH_RESULTS";

	private TextInputEditText tietSearchTerms;
	private ProgressBar pbFriendSearch;
	private Button btnSearch;

	@Inject
	LoaderFactory mLoaderFactory;
	@Inject
	PreferenceManager mPreferenceManager;

	private FriendSearchListAdapter mAdapter;
	private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager) FriendSearchActivity.this.getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(tietSearchTerms.getWindowToken(), 0);
			mAdapter.setData(new ArrayList<UserSearchResult>());
			pbFriendSearch.setVisibility(View.VISIBLE);
			btnSearch.setEnabled(false);

			Bundle args = new UserLoader.ArgsBuilder(UserLoader.Type.SEARCH)
					.setUserId(mPreferenceManager.getUserId())
					.setSearchTerms(tietSearchTerms.getText().toString())
					.build();
			startLoader(LoaderId.USER, args, FriendSearchActivity.this);
		}
	};
	private TextView.OnEditorActionListener mSearchEditorActionListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (((event != null &&
					(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
					(actionId == EditorInfo.IME_ACTION_SEARCH))) {
				btnSearch.callOnClick();
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_friend_search);
		component().inject(this);

		Toolbar tbFriendSearch = (Toolbar) findViewById(R.id.act_friend_search_tb);
		setSupportActionBar(tbFriendSearch);
		setUpButton();
		setTitle(getString(R.string.act_friend_search_tb_title));

		pbFriendSearch = (ProgressBar) findViewById(R.id.act_friend_search_pb);
		tietSearchTerms = (TextInputEditText) findViewById(R.id.act_friend_search_tiet);
		tietSearchTerms.setOnEditorActionListener(mSearchEditorActionListener);
		btnSearch = (Button) findViewById(R.id.act_friend_search_enter_btn);
		btnSearch.setOnClickListener(mSearchClickListener);
		RecyclerView rvSearchResults = (RecyclerView) findViewById(R.id.act_friend_search_rv);
		rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
		rvSearchResults.setAdapter(mAdapter =  new FriendSearchListAdapter(mPreferenceManager.getUserId()));

		if(savedInstanceState != null) {
			List<UserSearchResult> searchResults = savedInstanceState.getParcelableArrayList(ARGS_SEARCH_RESULTS);
			mAdapter.setData(searchResults);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(ARGS_SEARCH_RESULTS, mAdapter.getSearchResultsAsArrayList());
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<List<UserSearchResult>> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LoaderId.USER:
				return mLoaderFactory.createUserLoader(this, args);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<UserSearchResult>> loader, List<UserSearchResult> data) {
		if(data == null) {
			showMessage(getString(R.string.error_network_friend_search_paper));
			pbFriendSearch.setVisibility(View.GONE);
			btnSearch.setEnabled(true);
			return;
		}

		mAdapter.setData(data);
		pbFriendSearch.setVisibility(View.GONE);
		btnSearch.setEnabled(true);
	}

	@Override
	public void onLoaderReset(Loader<List<UserSearchResult>> loader) {
		mAdapter.setData(new ArrayList<UserSearchResult>());
	}
}
