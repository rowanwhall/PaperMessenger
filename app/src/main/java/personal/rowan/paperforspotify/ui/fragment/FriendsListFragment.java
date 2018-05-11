package personal.rowan.paperforspotify.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.loader.FriendLoader;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.ui.adapter.list.FriendsListAdapter;

public class FriendsListFragment
		extends BaseFragment
		implements LoaderManager.LoaderCallbacks<List<Friend>> {

	@Inject
	LoaderFactory mLoaderFactory;
	@Inject
	PreferenceManager mPreferenceManager;

	private FriendsListAdapter mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_friends_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView rvFriendsList = (RecyclerView) view.findViewById(R.id.frag_friends_list_rv);
		rvFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvFriendsList.setAdapter(mListAdapter = new FriendsListAdapter(getContext()));

		String cachedUserId = mPreferenceManager.getUserId();
		if(!TextUtils.isEmpty(cachedUserId)) {
			Bundle args = new FriendLoader.ArgsBuilder()
					.setUserId(cachedUserId)
					.build();
			startLoader(LoaderId.FRIEND, args, this);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if(!TextUtils.isEmpty(mPreferenceManager.getUserId())) {
			notifyLoaderContentChanged(LoaderId.FRIEND);
		}
	}

	@Override
	public Loader<List<Friend>> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LoaderId.FRIEND:
				return mLoaderFactory.createFriendLoader(getContext(), args);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<Friend>> loader, List<Friend> data) {
		if(data == null) {
			showMessage(getString(R.string.error_network_friends_paper));
			return;
		}

		mListAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<List<Friend>> loader) {
		mListAdapter.setData(new ArrayList<Friend>());
	}
}
