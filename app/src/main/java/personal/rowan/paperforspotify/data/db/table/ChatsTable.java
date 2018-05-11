package personal.rowan.paperforspotify.data.db.table;

import android.content.ContentValues;
import android.database.Cursor;

import personal.rowan.paperforspotify.data.db.CleanCursor;
import personal.rowan.paperforspotify.data.db.Column;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.Track;

public class ChatsTable
		extends Table {

	public static final String CHAT_NAME = "chatName";
	public static final String CHAT_CREATOR_NAME = "chatCreatorName";
	public static final String CHAT_CREATOR_ID = "chatCreatorId";
	public static final String CHAT_TIMESTAMP = "chatTimestamp";
	public static final String CHAT_LAST_MESSAGE = "chatLastMessage";
	public static final String CHAT_HAS_READ = "chatHasRead";

	public static final String CHAT_SONG_URI = "chatSongUri";
	public static final String CHAT_SONG_URL = "chatSongUrl";
	public static final String CHAT_SONG_IMAGE_URL = "chatSongImageUrl";
	public static final String CHAT_SONG_NAME = "chatSongName";
	public static final String CHAT_SONG_ARTIST_NAME = "chatSongArtistName";

	@Override
	protected void onCreateColumns() {
		addColumn(new Column(CHAT_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_CREATOR_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_CREATOR_ID, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_TIMESTAMP, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_LAST_MESSAGE, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_HAS_READ, Column.ColumnType.BOOLEAN));
		addColumn(new Column(CHAT_SONG_URI, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_SONG_URL, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_SONG_IMAGE_URL, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_SONG_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(CHAT_SONG_ARTIST_NAME, Column.ColumnType.TEXT));
	}

	public static Chat toObject(Cursor cursor) {
		CleanCursor cleanCursor = new CleanCursor(cursor);

		String chatId = cleanCursor.getString(ID);
		String chatName = cleanCursor.getString(CHAT_NAME);
		String chatCreatorName = cleanCursor.getString(CHAT_CREATOR_NAME);
		String chatCreatorId = cleanCursor.getString(CHAT_CREATOR_ID);
		String chatTimestamp = cleanCursor.getString(CHAT_TIMESTAMP);
		String chatLastMessage = cleanCursor.getString(CHAT_LAST_MESSAGE);
		boolean hasRead = cleanCursor.getBoolean(CHAT_HAS_READ);

		String songUri = cleanCursor.getString(CHAT_SONG_URI);
		String songUrl = cleanCursor.getString(CHAT_SONG_URL);
		String songImageUrl = cleanCursor.getString(CHAT_SONG_IMAGE_URL);
		String songName = cleanCursor.getString(CHAT_SONG_NAME);
		String artistName = cleanCursor.getString(CHAT_SONG_ARTIST_NAME);
		Track track = new Track(songName, songUri, songUrl, songImageUrl, songImageUrl, songImageUrl, artistName);

		return new Chat(chatId, chatName, chatCreatorName, chatCreatorId, Long.parseLong(chatTimestamp), chatLastMessage, hasRead, track);
	}

	public static ContentValues toContentValues(Chat object) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(ID, object.getChatId());
		contentValues.put(CHAT_NAME, object.getChatName());
		contentValues.put(CHAT_CREATOR_NAME, object.getCreatorName());
		contentValues.put(CHAT_CREATOR_ID, object.getCreatorId());
		contentValues.put(CHAT_TIMESTAMP, String.valueOf(object.getTimestamp()));
		contentValues.put(CHAT_LAST_MESSAGE, object.getLastMessage());
		contentValues.put(CHAT_HAS_READ, object.hasRead());

		Track track = object.getTrack();
		if(track != null) {
			contentValues.put(CHAT_SONG_URI, track.getUri());
			contentValues.put(CHAT_SONG_URL, track.getUrl());
			contentValues.put(CHAT_SONG_IMAGE_URL, track.getImageUrlMedium());
			contentValues.put(CHAT_SONG_NAME, track.getName());
			contentValues.put(CHAT_SONG_ARTIST_NAME, track.getArtistName());
		}

		return contentValues;
	}

}
