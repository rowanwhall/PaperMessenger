package personal.rowan.paperforspotify.data.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import personal.rowan.paperforspotify.data.db.CleanCursor;
import personal.rowan.paperforspotify.data.db.Column;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.data.model.ChatSongMessage;

public class ChatMessagesTable
		extends Table {

	public static final String MESSAGE_CHAT_ID = "messageChatId";
	public static final String MESSAGE_USER_ID = "messageUserId";
	public static final String MESSAGE_USER_NAME = "messageUserName";
	public static final String MESSAGE_IMAGE_URL = "messageImageUrl";
	public static final String MESSAGE_MESSAGE = "messageMessage";
	public static final String MESSAGE_TIMESTAMP = "messageTimestamp";

	public static final String MESSAGE_SONG_URI = "messageSongUri";
	public static final String MESSAGE_SONG_URL = "messageSongUrl";
	public static final String MESSAGE_SONG_IMAGE_URL = "messageSongImageUrl";
	public static final String MESSAGE_SONG_NAME = "messageSongName";
	public static final String MESSAGE_ARTIST_NAME = "messageArtistName";

	@Override
	protected void onCreateColumns() {
		addColumn(new Column(MESSAGE_CHAT_ID, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_USER_ID, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_USER_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_IMAGE_URL, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_MESSAGE, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_TIMESTAMP, Column.ColumnType.TEXT));

		addColumn(new Column(MESSAGE_SONG_URI, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_SONG_URL, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_SONG_IMAGE_URL, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_SONG_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(MESSAGE_ARTIST_NAME, Column.ColumnType.TEXT));
	}

	public static ChatMessage toObject(Cursor cursor) {
		CleanCursor cleanCursor = new CleanCursor(cursor);

		String messageId = cleanCursor.getString(ID);
		String chatId = cleanCursor.getString(MESSAGE_CHAT_ID);
		String userId = cleanCursor.getString(MESSAGE_USER_ID);
		String userName = cleanCursor.getString(MESSAGE_USER_NAME);
		String imageUrl = cleanCursor.getString(MESSAGE_IMAGE_URL);
		String message = cleanCursor.getString(MESSAGE_MESSAGE);
		String timestamp = cleanCursor.getString(MESSAGE_TIMESTAMP);

		String songUri = cleanCursor.getString(MESSAGE_SONG_URI);
		String songUrl = cleanCursor.getString(MESSAGE_SONG_URL);
		String songImageUrl = cleanCursor.getString(MESSAGE_SONG_IMAGE_URL);
		String songName = cleanCursor.getString(MESSAGE_SONG_NAME);
		String artistName = cleanCursor.getString(MESSAGE_ARTIST_NAME);

		if(TextUtils.isEmpty(songUri) && TextUtils.isEmpty(songUrl)) {
			return new ChatMessage(messageId, chatId, userId, userName, imageUrl, message, Long.parseLong(timestamp));
		}

		return new ChatSongMessage(messageId, chatId, userId, userName, imageUrl, message, Long.parseLong(timestamp),
				songUri, songUrl, songImageUrl, songName, artistName);
	}

	public static ContentValues toContentValues(ChatMessage object) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(ID, object.getMessageId());
		contentValues.put(MESSAGE_CHAT_ID, object.getChatId().replace("-", ""));
		contentValues.put(MESSAGE_USER_ID, object.getUserId());
		contentValues.put(MESSAGE_USER_NAME, object.getUserName());
		contentValues.put(MESSAGE_IMAGE_URL, object.getImageUrl());
		contentValues.put(MESSAGE_MESSAGE, object.getMessage());
		contentValues.put(MESSAGE_TIMESTAMP, String.valueOf(object.getTimestamp()));

		if(object instanceof ChatSongMessage) {
			ChatSongMessage songObject = (ChatSongMessage) object;
			contentValues.put(MESSAGE_SONG_URI, songObject.getSongUri());
			contentValues.put(MESSAGE_SONG_URL, songObject.getSongUrl());
			contentValues.put(MESSAGE_SONG_IMAGE_URL, songObject.getSongImageUrl());
			contentValues.put(MESSAGE_SONG_NAME, songObject.getSongName());
			contentValues.put(MESSAGE_ARTIST_NAME, songObject.getArtistName());
		} else {
			contentValues.put(MESSAGE_SONG_URI, "");
			contentValues.put(MESSAGE_SONG_URL, "");
			contentValues.put(MESSAGE_SONG_IMAGE_URL, "");
			contentValues.put(MESSAGE_SONG_NAME, "");
			contentValues.put(MESSAGE_ARTIST_NAME, "");
		}

		return contentValues;
	}
}
