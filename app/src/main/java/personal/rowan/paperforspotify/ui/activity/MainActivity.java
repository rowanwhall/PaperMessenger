package personal.rowan.paperforspotify.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.gcm.PaperRegistrationIntentService;
import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.FriendLoader;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperCreateChatRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperLoginRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperCreateChatResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperLoginResponse;
import personal.rowan.paperforspotify.network.spotify.request.SpotifyGetUserRequest;
import personal.rowan.paperforspotify.network.spotify.response.SpotifyGetUserResponse;
import personal.rowan.paperforspotify.ui.adapter.pager.MainActivityViewPagerAdapter;
import personal.rowan.paperforspotify.ui.fragment.ChatListFragment;
import personal.rowan.paperforspotify.ui.fragment.FriendsListFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.BaseDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.CreateChatDialogFragment;

public class MainActivity
		extends SpotifyActivity {

	private MainActivityViewPagerAdapter mViewPagerAdapter;
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			if(position == MainActivityViewPagerAdapter.POSITION_CHAT_LIST) {
				fabFriends.hide();
				fabChat.show();
			} else if(position == MainActivityViewPagerAdapter.POSITION_FRIENDS_LIST) {
				fabChat.hide();
				fabFriends.show();
			}

			setTabAlpha(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	@Inject
	DatabaseManager mDatabaseManager;

	private TabLayout tlMain;
	private FloatingActionButton fabChat;
	private FloatingActionButton fabFriends;

	private View.OnClickListener mFabChatClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new CreateChatDialogFragment().show(MainActivity.this);
		}
	};
	private View.OnClickListener mFabFriendsClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(MainActivity.this, FriendSearchActivity.class));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		component().inject(this);

		Toolbar tbMain = (Toolbar) findViewById(R.id.act_main_tb);
		tlMain = (TabLayout) findViewById(R.id.act_main_tl);
		ViewPager vpMain = (ViewPager) findViewById(R.id.act_main_vp);

		setSupportActionBar(tbMain);
		setTitle(getString(R.string.act_main_tb_title));
		vpMain.setAdapter(mViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager()));
		vpMain.addOnPageChangeListener(mPageChangeListener);
		tlMain.setupWithViewPager(vpMain);
		for(int i = 0; i < mViewPagerAdapter.getCount(); i++) {
			TabLayout.Tab tab = tlMain.getTabAt(i);
			if(tab != null) {
				tab.setIcon(mViewPagerAdapter.getDrawableResourceId(i));
			}
		}
		setTabAlpha(0);

		fabChat = (FloatingActionButton) findViewById(R.id.act_main_fab_chat);
		fabFriends = (FloatingActionButton) findViewById(R.id.act_main_fab_friends);
		fabChat.setOnClickListener(mFabChatClickListener);
		fabFriends.setOnClickListener(mFabFriendsClickListener);
		fabFriends.hide();

		if(savedInstanceState == null) {
			String cachedUserId = mPreferenceManager.getUserId();
			if(TextUtils.isEmpty(cachedUserId)) {
				if (TextUtils.isEmpty(mPreferenceManager.getSpotifyAccessToken())) {
					mSpotifyManager.authenticate(this);
				}
			} else {
				paperLogin(cachedUserId, mPreferenceManager.getUserName(), mPreferenceManager.getUserImageUrl());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.act_main_menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.act_main_menu_logout:
				mSpotifyManager.logout(this);
				mPreferenceManager.clearPreferences();
				mDatabaseManager.clearAllRows();
				startActivity(new Intent(this, LoginActivity.class));
				finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAuthenticateSuccess() {
		//If this is the same user logging in, show cached data while we load their data in the background
		//Otherwise, block them with a progress dialog until we've verified their identity
		final String cachedUserId = mPreferenceManager.getUserId();
		if(TextUtils.isEmpty(cachedUserId)) {
			showProgressDialog(getString(R.string.progress_spotify_login), getString(R.string.progress_dialog_generic_message));
		}

		new SpotifyGetUserRequest(mPreferenceManager.getSpotifyAccessToken())
				.execute(new NetworkCallback<SpotifyGetUserResponse>() {
					@Override
					public void onResponse(Response response, SpotifyGetUserResponse responseObject) {
						dismissProgressDialog();
						String spotifyId = responseObject.getUserId();
						if (TextUtils.isEmpty(cachedUserId) || !cachedUserId.equals(spotifyId)) {
							showProgressDialog(getString(R.string.progress_paper_login), getString(R.string.progress_dialog_generic_message));
						}
						String displayName = responseObject.getDisplayName();
						if(TextUtils.isEmpty(displayName) || displayName.equals("null")) {
							displayName = spotifyId;
						}

						paperLogin(spotifyId, displayName, responseObject.getImageUrl());
					}

					@Override
					public void onFailure(Exception e) {
						dismissProgressDialog();
						showMessage(getString(R.string.error_network_login_spotify));
						mSpotifyManager.authenticate(MainActivity.this);
					}
				});
	}

	private void paperLogin(String userId, String displayName, String imageUrl) {
		new PaperLoginRequest(userId, displayName, imageUrl)
				.execute(new NetworkCallback<PaperLoginResponse>() {
					@Override
					public void onResponse(Response response, PaperLoginResponse responseObject) {
						Intent intent = new Intent(MainActivity.this, PaperRegistrationIntentService.class);
						startService(intent);

						dismissProgressDialog();
						String userId = responseObject.getUserId();

						mPreferenceManager.storeUser(userId, responseObject.getName(), responseObject.getImageUrl());

						if(mViewPagerAdapter == null) {
							return;
						}

						ChatListFragment chatListFragment = mViewPagerAdapter.getChatListFragment();
						if(chatListFragment != null) {
							Bundle chatArgs = new ChatLoader.ArgsBuilder(ChatLoader.Type.USER_ID)
									.setId(userId)
									.build();
							startLoader(LoaderId.CHAT, chatArgs, chatListFragment);
						}

						FriendsListFragment friendsListFragment = mViewPagerAdapter.getFriendsListFragment();
						if(friendsListFragment != null) {
							Bundle friendArgs = new FriendLoader.ArgsBuilder()
									.setUserId(userId)
									.build();
							startLoader(LoaderId.FRIEND, friendArgs, friendsListFragment);
						}
					}

					@Override
					public void onFailure(Exception e) {
						dismissProgressDialog();
						showMessage(getString(R.string.error_network_login_paper));
					}
				});
	}

	private void setTabAlpha(int position) {
		for(int i = 0; i < mViewPagerAdapter.getCount(); i++) {
			TabLayout.Tab tab = tlMain.getTabAt(i);
			if(tab != null) {
				Drawable icon = tab.getIcon();
				if(icon != null) {
					icon.setAlpha(i == position ? 255 : 255 / 2);
				}
			}
		}
	}

	@Override
	public void onDialogPositiveClick(BaseDialogFragment dialogFragment) {
		switch(dialogFragment.tag()) {
			case CreateChatDialogFragment.TAG:
				CreateChatDialogFragment createChatDialogFragment = (CreateChatDialogFragment) dialogFragment;
				String chatName = createChatDialogFragment.getChatName();
				List<Friend> selectedFriends = createChatDialogFragment.getSelectedFriends();

				if(!TextUtils.isEmpty(chatName) && selectedFriends != null && !selectedFriends.isEmpty()) {
					showProgressDialog(getString(R.string.progress_paper_create_chat), getString(R.string.progress_dialog_generic_message));
					new PaperCreateChatRequest(chatName, mPreferenceManager.getUserId(), mPreferenceManager.getUserName(), selectedFriends)
							.execute(new NetworkCallback<PaperCreateChatResponse>() {
								@Override
								public void onResponse(Response response, PaperCreateChatResponse responseObject) {
									dismissProgressDialog();
									Chat chat = responseObject.getChat();
									if (chat != null) {
										Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
										intent.putExtra(MessagingActivity.ARGS_CHAT_ID, chat.getChatId());
										intent.putExtra(MessagingActivity.ARGS_CHAT_NAME, chat.getChatName());
										startActivity(intent);
									} else {
										showMessage(getString(R.string.error_network_create_chat_paper));
									}
								}

								@Override
								public void onFailure(Exception e) {
									dismissProgressDialog();
									showMessage(getString(R.string.error_network_create_chat_paper));
								}
							});
				}
				break;
		}
	}

}
