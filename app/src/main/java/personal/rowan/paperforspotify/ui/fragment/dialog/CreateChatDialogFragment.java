package personal.rowan.paperforspotify.ui.fragment.dialog;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.ui.adapter.list.CreateChatFriendsListAdapter;

public class CreateChatDialogFragment
		extends BaseDialogFragment {

	public static final String TAG = "CreateChatDialog";
	private static final String ARGS_SELECTED_FRIENDS = "ARGS_SELECTED_FRIENDS";

	@Inject
	DatabaseManager mDatabaseManager;

	private TextInputEditText tietChatName;

	private CreateChatFriendsListAdapter mAdapter;
	private TextWatcher mChatNameTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			int length = s.length();
			mAdapter.setHasName(length > 0 && length <= Chat.MAX_CHAT_NAME_LENGTH);
		}
	};

	@Override
	protected int layoutResource() {
		return R.layout.dialog_frag_create_chat;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		component().inject(this);

		mAdapter = new CreateChatFriendsListAdapter(mDatabaseManager.loadFriends());
		if(savedInstanceState != null) {
			List<Friend> selectedFriends = savedInstanceState.getParcelableArrayList(ARGS_SELECTED_FRIENDS);
			mAdapter.setSelectedFriends(selectedFriends);
		}
	}

	@Override
	protected void inflateLayout(View view) {
		tietChatName = (TextInputEditText) view.findViewById(R.id.dialog_frag_create_chat_name_tiet);
		RecyclerView rvFriends = (RecyclerView) view.findViewById(R.id.dialog_frag_create_chat_rv);
		rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
		rvFriends.setAdapter(mAdapter);
	}

	public String getChatName() {
		return tietChatName.getText().toString();
	}

	public ArrayList<Friend> getSelectedFriends() {
		return mAdapter.getSelectedFriends();
	}

	@Override
	protected String title() {
		return getString(R.string.dialog_frag_create_chat_title);
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	protected String positiveButtonText() {
		return getString(R.string.dialog_frag_create_chat_positive_btn);
	}

	@Override
	protected String negativeButtonText() {
		return getString(R.string.dialog_frag_create_chat_negative_btn);
	}

	@Override
	protected void afterShown() {
		Button positiveButton = getPositiveButton();
		mAdapter.setCreateButton(positiveButton);
		mAdapter.setHasName(!tietChatName.getText().toString().isEmpty());
		tietChatName.addTextChangedListener(mChatNameTextWatcher);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(ARGS_SELECTED_FRIENDS, getSelectedFriends());
		super.onSaveInstanceState(outState);
	}

}
