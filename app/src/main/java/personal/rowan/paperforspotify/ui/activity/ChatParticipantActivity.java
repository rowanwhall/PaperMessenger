package personal.rowan.paperforspotify.ui.activity;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

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

public class ChatParticipantActivity
		extends BaseActivity
		implements LoaderManager.LoaderCallbacks<List<UserSearchResult>> {

	public static final String ARGS_CHAT_ID = "ARGS_CHAT_ID";
	public static final String ARGS_CHAT_NAME = "ARGS_CHAT_NAME";

	@Inject
	LoaderFactory mLoaderFactory;
	@Inject
	PreferenceManager mPreferenceManager;

	private FriendSearchListAdapter mAdapter;
	private String mChatId;

	private SwipeRefreshLayout srlUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_chat_participant);
		component().inject(this);

		Toolbar tbFriendSearch = (Toolbar) findViewById(R.id.act_chat_participant_tb);
		setSupportActionBar(tbFriendSearch);
		setUpButton();

		String toolbarTitle = getString(R.string.act_chat_participant_tb_title_default);
		Bundle args = getIntent().getExtras();
		if(args != null) {
			mChatId = args.getString(ARGS_CHAT_ID);
			String chatName = args.getString(ARGS_CHAT_NAME);
			if(!TextUtils.isEmpty(chatName)) {
				toolbarTitle = chatName;
			}
		}
		setTitle(toolbarTitle);

		if(!TextUtils.isEmpty(mChatId)) {
			srlUsers = (SwipeRefreshLayout) findViewById(R.id.act_chat_participant_srl);
			srlUsers.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSwipeRefresh));
			RecyclerView rvUsers = (RecyclerView) findViewById(R.id.act_chat_participant_rv);
			rvUsers.setLayoutManager(new LinearLayoutManager(this));
			rvUsers.setAdapter(mAdapter = new FriendSearchListAdapter(mPreferenceManager.getUserId()));

			srlUsers.setRefreshing(true);
			Bundle userArgs = new UserLoader.ArgsBuilder(UserLoader.Type.CHAT)
					.setChatId(mChatId)
					.build();
			startLoader(LoaderId.USER, userArgs, this);

			srlUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					notifyLoaderContentChanged(LoaderId.USER);
				}
			});
		} else {
			showMessage(getString(R.string.error_paper_chat_not_found));
			finish();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		notifyLoaderContentChanged(LoaderId.USER);
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
		srlUsers.setRefreshing(false);
		if(data == null) {
			showMessage(getString(R.string.error_network_chat_participants_paper));
			return;
		}

		mAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<List<UserSearchResult>> loader) {
		mAdapter.setData(new ArrayList<UserSearchResult>());
	}

}
