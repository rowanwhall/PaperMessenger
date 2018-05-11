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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.manager.PreferenceManager;
import personal.rowan.paperforspotify.ui.adapter.list.ChatsListAdapter;

public class ChatListFragment
		extends BaseFragment
		implements LoaderManager.LoaderCallbacks<List<Chat>> {

	@Inject
	LoaderFactory mLoaderFactory;
	@Inject
	PreferenceManager mPreferenceManager;

	private ChatsListAdapter mListAdapter;
	private TextView tvEmptyView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_chat_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView rvChatList = (RecyclerView) view.findViewById(R.id.frag_chat_list_rv);
		rvChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvChatList.setAdapter(mListAdapter = new ChatsListAdapter(getActivity()));
		tvEmptyView = (TextView) view.findViewById(R.id.frag_chat_list_empty_tv);

		String cachedUserId = mPreferenceManager.getUserId();
		if(!TextUtils.isEmpty(cachedUserId)) {
			Bundle args = new ChatLoader.ArgsBuilder(ChatLoader.Type.USER_ID)
					.setId(cachedUserId)
					.build();
			startLoader(LoaderId.CHAT, args, this);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if(!TextUtils.isEmpty(mPreferenceManager.getUserId())) {
			notifyLoaderContentChanged(LoaderId.CHAT);
		}
	}

	@Override
	public Loader<List<Chat>> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LoaderId.CHAT:
				return mLoaderFactory.createChatLoader(getContext(), args);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<Chat>> loader, List<Chat> data) {
		if(data == null) {
			showMessage(getString(R.string.error_network_chats_paper));
			return;
		}

		mListAdapter.setData(data);
		tvEmptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<List<Chat>> loader) {
		mListAdapter.setData(new ArrayList<Chat>());
	}
}
