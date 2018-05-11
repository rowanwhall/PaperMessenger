package personal.rowan.paperforspotify.ui.adapter.list;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.data.model.ChatSongMessage;
import personal.rowan.paperforspotify.data.model.ReadReceipt;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.manager.PaperApplication;
import personal.rowan.paperforspotify.network.PicassoCircleTransformation;
import personal.rowan.paperforspotify.ui.activity.PlayerActivity;
import personal.rowan.paperforspotify.ui.adapter.viewholder.ChatMessageViewHolder;

public class ChatMessageListAdapter
		extends RecyclerView.Adapter<ChatMessageViewHolder> {

	private static final int VIEW_TYPE_SELF = 1;
	private static final int VIEW_TYPE_OTHER = 2;

	private Activity mActivityContext;

	private List<ChatMessage> mMessages;
	private String mUserId;
	private HashMap<String, ChatMessage> mFakeMessageMap;
	private ReadReceipt mReadReceipt;
	private boolean mHasPremiumAccess;

	public ChatMessageListAdapter(String userId, Activity activity, boolean hasPremiumAccess) {
		mMessages = new ArrayList<>();
		mFakeMessageMap = new HashMap<>();
		mUserId = userId;
		mActivityContext = activity;
		mHasPremiumAccess = hasPremiumAccess;
	}

	public void setData(List<ChatMessage> messages) {
		mMessages = messages;
		notifyDataSetChanged();
	}

	public void addData(List<ChatMessage> messages, int start, int sizeAdded) {
		mMessages = messages;
		notifyItemRangeInserted(start, sizeAdded);
		if(start > 0) {
			notifyItemChanged(start - 1);
		}
	}

	private void addMessage(ChatMessage message) {
		mMessages.add(message);
		int size = mMessages.size();
		notifyItemInserted(size - 1);
		if(size >= 2) {
			notifyItemChanged(size - 2);
		}
	}

	public void addFakeMessage(String messageId, String chatId, String userId, String name, String imageUrl, String message) {
		ChatMessage fakeMessage = ChatMessage.createFakeChatMessage(messageId, chatId, userId, name, imageUrl, message);
		mFakeMessageMap.put(fakeMessage.getMessageId(), fakeMessage);
		addMessage(fakeMessage);
	}

	public void addFakeMessage(String messageId, String chatId, String userId, String name, String imageUrl, String message, Track track) {
		ChatSongMessage fakeMessage = ChatSongMessage.createFakeChatMessage(messageId, chatId, userId, name, imageUrl, message, track);
		mFakeMessageMap.put(fakeMessage.getMessageId(), fakeMessage);
		addMessage(fakeMessage);
	}

	private void removeMessage(ChatMessage message) {
		boolean removalSuccessful = mMessages.remove(message);
		if(removalSuccessful) {
			notifyDataSetChanged();
		}
	}

	public void displayFakeMessageAsReal(String messageId) {
		ChatMessage fakeMessage = mFakeMessageMap.get(messageId);
		if(fakeMessage == null) {
			return;
		}
		String fakeMessageId = fakeMessage.getMessageId();
		for(int i = mMessages.size() - 1; i >= 0; i--) {
			ChatMessage message = mMessages.get(i);
			if(message.getMessageId().equals(fakeMessageId)) {
				message.setIsFake(false);
				notifyItemChanged(i);
				return;
			}
		}
	}

	public void removeFakeMessage(String messageId) {
		ChatMessage fakeMessage = mFakeMessageMap.get(messageId);
		if(fakeMessage != null) {
			removeMessage(fakeMessage);
		}
	}

	public void removeFakeMessages(List<ChatMessage> messages) {
		boolean messagesRemoved = false;
		for(ChatMessage message : messages) {
			ChatMessage fakeMessage = mFakeMessageMap.get(message.getMessageId());
			if(fakeMessage != null) {
				messagesRemoved |= mMessages.remove(fakeMessage);
			}
		}
		if(messagesRemoved) {
			notifyDataSetChanged();
		}
	}

	public void removeAllFakeMessages() {
		for(ChatMessage fakeMessage : mFakeMessageMap.values()) {
			removeMessage(fakeMessage);
		}
	}

	public int realMessageCount() {
		return getItemCount() - mFakeMessageMap.size();
	}

	public void setReadReceipt(ReadReceipt readReceipt) {
		mReadReceipt = readReceipt;
		int latestRealMessageIndex = getLatestRealMessageIndex();
		if(latestRealMessageIndex >= 0) {
			notifyItemRangeChanged(getLatestRealMessageIndex(), mMessages.size() - 1);
		} else {
			notifyItemChanged(mMessages.size() - 1);
		}
	}

	private int getLatestRealMessageIndex() {
		for(int i = mMessages.size() - 1;  i >= 0; i--) {
			if(!mMessages.get(i).isFake()) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		boolean isSelf = viewType == VIEW_TYPE_SELF;
		return new ChatMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				isSelf ? R.layout.viewholder_message_list_self : R.layout.viewholder_message_list_other, parent, false), isSelf);
	}

	@Override
	public void onBindViewHolder(final ChatMessageViewHolder holder, int position) {
		int adapterPosition = holder.getAdapterPosition();
		final ChatMessage message = mMessages.get(adapterPosition);
		ChatMessage previousMessage = adapterPosition > 0 ? mMessages.get(adapterPosition - 1) : null;
		ChatMessage nextMessage = adapterPosition < getItemCount() - 1 ? mMessages.get(adapterPosition + 1) : null;
		boolean pastThreshold = previousMessage == null ||
				message.isPastThreshold(previousMessage);
		boolean middleOrLastInBlock = adapterPosition != 0 &&
				previousMessage == null ||
				(!pastThreshold && previousMessage.getUserId().equals(message.getUserId()));
		boolean lastInBlock = nextMessage == null ||
				!nextMessage.getUserId().equals(message.getUserId()) ||
				nextMessage.isPastThreshold(message);
		if (middleOrLastInBlock) {
			if(lastInBlock) {
				holder.llMessageContainer.setBackgroundResource(holder.isSelf() ?
						R.drawable.chat_bubble_self_last : R.drawable.chat_bubble_other_last);
			} else {
				holder.llMessageContainer.setBackgroundResource(holder.isSelf() ?
						R.drawable.chat_bubble_self_middle : R.drawable.chat_bubble_other_middle);
			}

			if(holder.isSelf()) {
				holder.viewPlaceholder.setVisibility(View.GONE);
			} else {
				if(lastInBlock) {
					holder.ivProfile.setVisibility(View.VISIBLE);
					if(message.isAdmin()) {
						holder.ivProfile.setImageResource(R.mipmap.ic_launcher);
					} else {
						Picasso.with(PaperApplication.getInstance())
								.load(message.getImageUrl())
								.placeholder(R.drawable.ic_person_black_24dp)
								.transform(new PicassoCircleTransformation())
								.into(holder.ivProfile);
					}
				} else {
					holder.ivProfile.setVisibility(View.INVISIBLE);
				}
				holder.tvName.setVisibility(View.GONE);
			}
		} else {
			if(lastInBlock) {
				holder.llMessageContainer.setBackgroundResource(holder.isSelf() ?
						R.drawable.chat_bubble_self_solitary : R.drawable.chat_bubble_other_solitary);
			} else {
				holder.llMessageContainer.setBackgroundResource(holder.isSelf() ?
						R.drawable.chat_bubble_self_first : R.drawable.chat_bubble_other_first);
			}

			if(holder.isSelf()) {
				holder.viewPlaceholder.setVisibility(View.VISIBLE);
			} else {
				if(lastInBlock) {
					holder.ivProfile.setVisibility(View.VISIBLE);
					if(message.isAdmin()) {
						holder.ivProfile.setImageResource(R.mipmap.ic_launcher);
					} else {
						Picasso.with(PaperApplication.getInstance())
								.load(message.getImageUrl())
								.placeholder(R.drawable.ic_person_black_24dp)
								.transform(new PicassoCircleTransformation())
								.into(holder.ivProfile);
					}
				} else {
					holder.ivProfile.setVisibility(View.INVISIBLE);
				}
				holder.tvName.setVisibility(View.VISIBLE);
				holder.tvName.setText(message.getFirstName());
			}
		}
		holder.tvMessage.setText(message.getMessage());
		holder.tvTimestamp.setText(message.getFormattedTimestamp());
		holder.tvTimestamp.setVisibility(pastThreshold || message.shouldShowTimestamp() ? View.VISIBLE : View.GONE);
		holder.tvMessage.setOnClickListener(pastThreshold ? null : new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				message.setShouldShowTimestamp(!message.shouldShowTimestamp());
				notifyItemChanged(holder.getAdapterPosition());
			}
		});
		holder.tvMessage.setAlpha(message.isFake() ? .5f : 1f);

		if(!message.isFake() &&
				(adapterPosition == mMessages.size() - 1 || adapterPosition == getLatestRealMessageIndex()) &&
				mReadReceipt != null &&
				!TextUtils.isEmpty(mReadReceipt.getMessage()) &&
				message.getMessageId().equals(mReadReceipt.getMessageId())) {
			holder.tvReadReceipt.setVisibility(View.VISIBLE);
			holder.tvReadReceipt.setText(mReadReceipt.getMessage());
		} else {
			holder.tvReadReceipt.setVisibility(View.GONE);
		}

		if(message instanceof ChatSongMessage) {
			holder.rlSongContainer.setVisibility(View.VISIBLE);
			final ChatSongMessage songMessage = (ChatSongMessage) message;
			holder.tvSongName.setText(songMessage.getSongName());
			holder.tvArtistName.setText(songMessage.getArtistName());
			Picasso.with(PaperApplication.getInstance())
					.load(songMessage.getSongImageUrl())
					.placeholder(R.drawable.ic_audiotrack_black_24dp)
					.into(holder.ivSong);
			holder.rlSongContainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent;
					if(mHasPremiumAccess) {
						String imageUrl = songMessage.getSongImageUrl();
						Track track = new Track(songMessage.getSongName(), songMessage.getSongUri(), songMessage.getSongUrl(), imageUrl, imageUrl, imageUrl, songMessage.getArtistName());
						intent = new Intent(mActivityContext, PlayerActivity.class);
						intent.putExtra(PlayerActivity.ARGS_TRACK, track);
					} else {
						intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(songMessage.getSongUrl()));
					}
					mActivityContext.startActivity(intent);
				}
			});
		} else {
			holder.rlSongContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage message = mMessages.get(position);
		if(message.getUserId().equals(mUserId)) {
			return VIEW_TYPE_SELF;
		} else {
			return VIEW_TYPE_OTHER;
		}
	}

	@Override
	public int getItemCount() {
		return mMessages.size();
	}

}