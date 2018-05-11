package personal.rowan.paperforspotify.ui.adapter.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import personal.rowan.paperforspotify.R;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Track;
import personal.rowan.paperforspotify.ui.activity.MessagingActivity;
import personal.rowan.paperforspotify.ui.adapter.viewholder.ChatsListViewHolder;

public class ChatsListAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String SUFFIX_SECONDS = "s";
	private static final String SUFFIX_MINUTES = "m";
	private static final String SUFFIX_HOURS = "h";
	private static final String SUFFIX_DAYS = "d";
	private static final String SUFFIX_WEEKS = "w";

	private static final int SECONDS_IN_MINUTE = 60;
	private static final int SECONDS_IN_HOUR = 3600;
	private static final int SECONDS_IN_DAY = 86400;
	private static final int SECONDS_IN_WEEK = 604800;

	private Context mContext;
	private List<Chat> mChats;

	public ChatsListAdapter(Context context) {
		mContext = context;
		setData(new ArrayList<Chat>());
	}

	public void setData(List<Chat> chats) {
		mChats = chats;
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ChatsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_chats_list, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ChatsListViewHolder) {
			ChatsListViewHolder viewHolder = (ChatsListViewHolder) holder;
			final Chat chat = mChats.get(position);
			viewHolder.tvName.setText(chat.getChatName());
			String lastMessage = chat.getLastMessage();
			viewHolder.tvDescription.setText(TextUtils.isEmpty(lastMessage) ? "Created by " + chat.getCreatorName() : chat.getLastMessage());
			viewHolder.tvTimestamp.setText(formatTimestamp(chat.getTimestamp()));
			Track track = chat.getTrack();
			if(track != null && track.isValid()) {
				Picasso.with(mContext)
						.load(track.getImageUrlSmall())
						.placeholder(R.drawable.ic_audiotrack_black_24dp)
						.into(viewHolder.ivSongImage);
			} else {
				viewHolder.ivSongImage.setImageResource(R.drawable.ic_audiotrack_black_24dp);
			}

			Typeface typeface = chat.hasRead() ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD;
			viewHolder.tvName.setTypeface(typeface);
			viewHolder.tvDescription.setTypeface(typeface);
			viewHolder.tvTimestamp.setTypeface(typeface);

			viewHolder.rlContainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, MessagingActivity.class);
					intent.putExtra(MessagingActivity.ARGS_CHAT_ID, chat.getChatId());
					intent.putExtra(MessagingActivity.ARGS_CHAT_NAME, chat.getChatName());
					intent.putExtra(MessagingActivity.ARGS_CHAT_SONG, chat.getTrack());

					mContext.startActivity(intent);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mChats.size();
	}

	private static String formatTimestamp(long timestamp) {
		long now = Calendar.getInstance().getTimeInMillis();

		long differenceSeconds = Math.abs(timestamp - now) / 1000;

		if (differenceSeconds + 1 < SECONDS_IN_MINUTE) {
			long secondsSince = Math.round(differenceSeconds) + 1;
			return secondsSince + SUFFIX_SECONDS;
		} else if (differenceSeconds < SECONDS_IN_HOUR) {
			int minutesSince = Math.round(differenceSeconds / SECONDS_IN_MINUTE);
			return minutesSince + SUFFIX_MINUTES;
		} else if (differenceSeconds < SECONDS_IN_DAY) {
			int hoursSince = (Math.round(differenceSeconds) / SECONDS_IN_HOUR);
			return hoursSince + SUFFIX_HOURS;
		} else if (differenceSeconds < SECONDS_IN_WEEK) {
			int daysSince = Math.round(differenceSeconds / SECONDS_IN_DAY);
			return daysSince + SUFFIX_DAYS;
		} else {
			int weeksSince = Math.round(differenceSeconds / SECONDS_IN_WEEK);
			return weeksSince + SUFFIX_WEEKS;
		}
	}
}
