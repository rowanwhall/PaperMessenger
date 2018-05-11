package personal.rowan.paperforspotify.ui.activity;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import okhttp3.Response;
import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.loader.ChatLoader;
import personal.rowan.paperforspotify.data.loader.LoaderFactory;
import personal.rowan.paperforspotify.data.loader.LoaderId;
import personal.rowan.paperforspotify.data.loader.MessageLoader;
import personal.rowan.paperforspotify.data.loader.ReadReceiptLoader;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.data.model.ReadReceipt;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.data.service.MessageSenderService;
import personal.rowan.paperforspotify.manager.DatabaseManager;
import personal.rowan.paperforspotify.network.NetworkCallback;
import personal.rowan.paperforspotify.network.paper.request.PaperAddSongToChatRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperClearUserFromChatRequest;
import personal.rowan.paperforspotify.network.paper.request.PaperStoreChatNameRequest;
import personal.rowan.paperforspotify.network.paper.response.PaperAddSongToChatResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperClearUserFromChatResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperSendTextOnlyMessageResponse;
import personal.rowan.paperforspotify.network.paper.response.PaperStoreChatNameResponse;
import personal.rowan.paperforspotify.ui.adapter.list.ChatMessageListAdapter;
import personal.rowan.paperforspotify.ui.fragment.dialog.BaseDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.ChangeChatNameDialogFragment;
import personal.rowan.paperforspotify.ui.fragment.dialog.SpotifySongSearchDialogFragment;
import personal.rowan.paperforspotify.ui.view.ChatComposer;
import personal.rowan.paperforspotify.ui.view.ChatSongHeader;
import personal.rowan.paperforspotify.util.NotificationUtil;

public class MessagingActivity
		extends SpotifyActivity
		implements LoaderManager.LoaderCallbacks {

	public static final String ARGS_CHAT_ID = "ARGS_NOTIFICATION_CHAT_ID";
	public static final String ARGS_CHAT_NAME = "ARGS_NOTIFICATION_CHAT_NAME";
	public static final String ARGS_CHAT_SONG = "ARGS_CHAT_SONG";

	@Inject
	LoaderFactory mLoaderFactory;
	@Inject
	DatabaseManager mDatabaseManager;

	private ChatMessageListAdapter mAdapter;
	private String mChatId;
	private String mChatName;
	private Track mChatTrack;
	private Track mSelectedTrack;

	private RecyclerView rvMessages;
	private ChatComposer viewChatComposer;
	private ChatSongHeader viewChatSongHeader;

	private boolean mSenderServiceBound = false;
	private MessageSenderService mSenderService;
	private ServiceConnection mSenderServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MessageSenderService.MessageSenderServiceBinder binder = (MessageSenderService.MessageSenderServiceBinder) service;
			mSenderService = binder.getService();
			mSenderServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSenderService.unregisterAllListeners();
			mSenderServiceBound = false;
		}
	};

	private View.OnClickListener mSendClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String message = viewChatComposer.getMessage();
			if(!TextUtils.isEmpty(message) &&
					mSenderService != null &&
					mSenderServiceBound) {
				viewChatComposer.setMessage(null);
				final String messageId = UUID.randomUUID().toString();
				String userId = mPreferenceManager.getUserId();
				String userName = mPreferenceManager.getUserName();
				String userImageUrl = mPreferenceManager.getUserImageUrl();

				if(mSelectedTrack != null) {
					mAdapter.addFakeMessage(messageId, mChatId, userId, userName, userImageUrl, message, mSelectedTrack);
				} else {
					mAdapter.addFakeMessage(messageId, mChatId, userId, userName, userImageUrl, message);
				}
				rvMessages.smoothScrollToPosition(mAdapter.getItemCount());

				mSenderService.registerListener(messageId, new MessageSenderService.IMessageSenderService() {
					@Override
					public void onResponse(PaperSendTextOnlyMessageResponse responseObject) {
						String messageId = responseObject.getMessage().getMessageId();
						mAdapter.displayFakeMessageAsReal(messageId);
						mAdapter.setReadReceipt(new ReadReceipt(mChatId, messageId, ""));
						notifyLoaderContentChanged(LoaderId.MESSAGE);
					}

					@Override
					public void onFailure(String messageId) {
						mAdapter.removeFakeMessage(messageId);
						showMessage(getString(R.string.error_network_send_message_paper));
					}
				});

				if(mSelectedTrack != null) {
					mSenderService.sendSongMessage(messageId, mChatId, userId, mChatName, userName, userImageUrl, message, mSelectedTrack);
					mSelectedTrack = null;
					viewChatComposer.hideSongContainer();
				} else {
					mSenderService.sendMessage(messageId, mChatId, userId, mChatName, userName, userImageUrl, message);
				}
			}
		}
	};

	private View.OnClickListener mSongSearchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showSongSearchDialog(false);
		}
	};

	private View.OnClickListener mChatSongClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent intent;
			if(mPreferenceManager.hasSpotifyPremiumAccess()) {
				intent = new Intent(MessagingActivity.this, PlayerActivity.class);
				intent.putExtra(PlayerActivity.ARGS_TRACK, mChatTrack);
			} else {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mChatTrack.getUrl()));
			}
			startActivity(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_messaging);
		component().inject(this);

		Toolbar tbChat = (Toolbar) findViewById(R.id.act_messaging_tb);
		setSupportActionBar(tbChat);
		setUpButton();

		String toolbarTitle = getString(R.string.act_messaging_tb_title_default);
		Bundle args = getIntent().getExtras();
		if(args != null) {
			mChatId = args.getString(ARGS_CHAT_ID);
			mChatName = args.getString(ARGS_CHAT_NAME);
			mChatTrack = args.getParcelable(ARGS_CHAT_SONG);
			if(!TextUtils.isEmpty(mChatName)) {
				toolbarTitle = mChatName;
			}
		}
		setTitle(toolbarTitle);

		if(!TextUtils.isEmpty(mChatId)) {
			viewChatComposer = (ChatComposer) findViewById(R.id.act_messaging_composer_view);
			viewChatComposer.setSendListener(mSendClickListener);
			viewChatComposer.setAddListener(mSongSearchClickListener);

			viewChatSongHeader = (ChatSongHeader) findViewById(R.id.act_messaging_song_header_view);
			viewChatSongHeader.setPlayListener(mChatSongClickListener);
			if(mChatTrack != null) {
				viewChatSongHeader.setVisibility(View.VISIBLE);
				viewChatSongHeader.setSong(mChatTrack);
			} else {
				viewChatSongHeader.setVisibility(View.GONE);
			}

			rvMessages = (RecyclerView) findViewById(R.id.act_messaging_rv);
			LinearLayoutManager layoutManager = new LinearLayoutManager(this);
			layoutManager.setStackFromEnd(true);
			rvMessages.setLayoutManager(layoutManager);
			rvMessages.setAdapter(mAdapter = new ChatMessageListAdapter(mPreferenceManager.getUserId(), this, mPreferenceManager.hasSpotifyPremiumAccess()));

			String userId = mPreferenceManager.getUserId();
			Bundle messageArgs = new MessageLoader.ArgsBuilder(mChatId)
					.setUserId(userId)
					.setDoNotMark(false)
					.build();
			startLoader(LoaderId.MESSAGE, messageArgs, this);

			Bundle readReceiptArgs = new ReadReceiptLoader.ArgsBuilder(mChatId)
					.setUserId(userId)
					.build();
			startLoader(LoaderId.READ_RECEIPT, readReceiptArgs, this);

			Bundle chatArgs = new ChatLoader.ArgsBuilder(ChatLoader.Type.CHAT_ID)
					.setId(mChatId)
					.build();
			startLoader(LoaderId.CHAT, chatArgs, this);
		} else {
			showMessage(getString(R.string.error_paper_chat_not_found));
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_messaging, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.act_messaging_menu_set_song:
				showSongSearchDialog(true);
				return true;
			case R.id.act_messaging_menu_set_chat_name:
				showChatNameDialog();
				return true;
			case R.id.act_messaging_menu_view_people:
				Intent chatParticipantIntent = new Intent(this, ChatParticipantActivity.class);
				chatParticipantIntent.putExtra(ChatParticipantActivity.ARGS_CHAT_ID, mChatId);
				chatParticipantIntent.putExtra(ChatParticipantActivity.ARGS_CHAT_NAME, mChatName);
				startActivity(chatParticipantIntent);
				return true;
			case R.id.act_messaging_menu_leave_chat:
				showLeaveChatDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent senderServiceIntent = new Intent(this, MessageSenderService.class);
		bindService(senderServiceIntent, mSenderServiceConnection, Context.BIND_AUTO_CREATE);

		notifyLoaderContentChanged(LoaderId.MESSAGE);
		notifyLoaderContentChanged(LoaderId.READ_RECEIPT);
		notifyLoaderContentChanged(LoaderId.CHAT);
	}

	@Override
	public void onStop() {
		if(mSenderServiceBound && mSenderServiceConnection != null) {
			unbindService(mSenderServiceConnection);
			mSenderServiceBound = false;
		}

		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSenderService != null) {
			mSenderService.stopSelfWhenFinished();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NotificationUtil.NOTIFICATION_ID_MESSAGE);
	}

	public String getChatId() {
		return mChatId;
	}

	private void onChatMessagesLoaded(List<ChatMessage> messages) {
		int realMessageCount = mAdapter.realMessageCount();
		int newMessageCount = messages.size();
		// check if any new messages were loaded
		if(newMessageCount > realMessageCount) {
			// check all new messages to see if there is a corresponding fake message to remove
			mAdapter.removeFakeMessages(messages.subList(realMessageCount, newMessageCount));
			mAdapter.addData(messages, mAdapter.getItemCount(), newMessageCount);
			rvMessages.smoothScrollToPosition(mAdapter.getItemCount());
		}
	}

	private void onChatDetailsLoaded(List<Chat> chats) {
		if(!chats.isEmpty()) {
			Chat chat = chats.get(0);
			if(chat != null && mChatId.equals(chat.getChatId())) {
				// Update chat name
				mChatName = chat.getChatName();
				ActionBar actionBar = getSupportActionBar();
				if(actionBar != null) {
					actionBar.setTitle(mChatName);
				}

				// Update chat song
				Track track = chat.getTrack();
				if(track != null && track.isValid()) {
					mChatTrack = track;
					viewChatSongHeader.setSong(mChatTrack);
					viewChatSongHeader.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void showSongSearchDialog(boolean groupSong) {
		SpotifySongSearchDialogFragment spotifySongSearchDialogFragment = new SpotifySongSearchDialogFragment();
		Bundle args = new Bundle();
		args.putBoolean(SpotifySongSearchDialogFragment.ARGS_GROUP_SONG, groupSong);
		spotifySongSearchDialogFragment.setArguments(args);
		spotifySongSearchDialogFragment.show(this);
	}

	private void showChatNameDialog() {
		ChangeChatNameDialogFragment changeChatNameDialogFragment = new ChangeChatNameDialogFragment();
		Bundle args = new Bundle();
		args.putString(ChangeChatNameDialogFragment.ARGS_ORIGINAL_CHAT_NAME, mChatName);
		changeChatNameDialogFragment.setArguments(args);
		changeChatNameDialogFragment.show(this);
	}

	private void showLeaveChatDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.act_messaging_dialog_leave_chat_title))
				.setMessage(getString(R.string.act_messaging_dialog_leave_chat_message))
				.setPositiveButton(getString(R.string.act_messaging_dialog_leave_chat_btn_positive), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						showProgressDialog(getString(R.string.progress_paper_leaving_chat), getString(R.string.progress_dialog_generic_message));
						new PaperClearUserFromChatRequest(mChatId, mChatName, mPreferenceManager.getUserId(), mPreferenceManager.getUserName())
								.execute(new NetworkCallback<PaperClearUserFromChatResponse>() {
									@Override
									public void onResponse(Response response, PaperClearUserFromChatResponse responseObject) {
										mDatabaseManager.deleteChat(mChatId);
										dismissProgressDialog();
										finish();
									}

									@Override
									public void onFailure(Exception e) {
										dismissProgressDialog();
										showMessage(getString(R.string.error_network_leave_chat_paper));
									}
								});
					}
				})
				.setNegativeButton(getString(R.string.act_messaging_dialog_leave_chat_btn_negative), null)
				.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MessagingActivity.this, R.color.colorButton));
				dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MessagingActivity.this, R.color.colorButton));
			}
		});

		dialog.show();
	}

	@Override
	public void onDialogPositiveClick(BaseDialogFragment dialogFragment) {
		switch (dialogFragment.tag()) {
			case SpotifySongSearchDialogFragment.TAG:
				SpotifySongSearchDialogFragment spotifySongSearchDialogFragment = (SpotifySongSearchDialogFragment) dialogFragment;
				if(spotifySongSearchDialogFragment.isGroupSong()) {
					Track track = spotifySongSearchDialogFragment.getSelectedTrack();
					showProgressDialog(getString(R.string.progress_paper_add_song), getString(R.string.progress_dialog_generic_message));
					new PaperAddSongToChatRequest(mChatId, track.getUri(), track.getUrl(), track.getImageUrlMedium(), track.getName(), track.getArtistName(), mPreferenceManager.getUserId(), mPreferenceManager.getUserName())
							.execute(new NetworkCallback<PaperAddSongToChatResponse>() {
								@Override
								public void onResponse(Response response, PaperAddSongToChatResponse responseObject) {
									dismissProgressDialog();
									Chat chat = responseObject.getChat();
									if(chat != null) {
										Track track = chat.getTrack();
										if(track != null) {
											viewChatSongHeader.setVisibility(View.VISIBLE);
											viewChatSongHeader.setSong(mChatTrack = track);
											return;
										}
									}
									showMessage(getString(R.string.error_network_add_song_paper));
								}

								@Override
								public void onFailure(Exception e) {
									dismissProgressDialog();
									showMessage(getString(R.string.error_network_add_song_paper));
								}
							});
				} else {
					mSelectedTrack = spotifySongSearchDialogFragment.getSelectedTrack();
					viewChatComposer.revealSongContainer();
					viewChatComposer.setSong(mSelectedTrack);
				}
				break;
			case ChangeChatNameDialogFragment.TAG:
				ChangeChatNameDialogFragment changeChatNameDialogFragment = (ChangeChatNameDialogFragment) dialogFragment;
				showProgressDialog(getString(R.string.progress_paper_store_chat_name), getString(R.string.progress_dialog_generic_message));
				new PaperStoreChatNameRequest(mChatId, changeChatNameDialogFragment.getName(), mPreferenceManager.getUserId(), mPreferenceManager.getUserName())
						.execute(new NetworkCallback<PaperStoreChatNameResponse>() {
							@Override
							public void onResponse(Response response, PaperStoreChatNameResponse responseObject) {
								dismissProgressDialog();
								mChatName = responseObject.getChat().getChatName();
								ActionBar actionBar = getSupportActionBar();
								if(actionBar != null) {
									actionBar.setTitle(mChatName);
								}
							}

							@Override
							public void onFailure(Exception e) {
								dismissProgressDialog();
								showMessage(getString(R.string.error_network_store_chat_name_paper));
							}
						});
				break;
		}
	}

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LoaderId.MESSAGE:
				return mLoaderFactory.createMessageLoader(this, args);
			case LoaderId.READ_RECEIPT:
				return mLoaderFactory.createReadReceiptLoader(this, args);
			case LoaderId.CHAT:
				return mLoaderFactory.createChatLoader(this, args);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader loader, Object data) {
		switch(loader.getId()) {
			case LoaderId.MESSAGE:
				if (data == null) {
					mAdapter.removeAllFakeMessages();
					return;
				}

				onChatMessagesLoaded((List<ChatMessage>) data);
				break;
			case LoaderId.READ_RECEIPT:
				if(data == null) {
					return;
				}

				mAdapter.setReadReceipt((ReadReceipt) data);
				break;
			case LoaderId.CHAT:
				if(data == null) {
					return;
				}

				onChatDetailsLoaded((List<Chat>) data);
		}
	}

	@Override
	public void onLoaderReset(Loader loader) {
		mAdapter.removeAllFakeMessages();
	}

	@Override
	public void onInitializePlayerFailure() {
		showMessage(getString(R.string.error_spotify_player_initialization));
	}

}
