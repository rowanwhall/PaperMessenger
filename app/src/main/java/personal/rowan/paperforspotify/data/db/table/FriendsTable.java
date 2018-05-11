package personal.rowan.paperforspotify.data.db.table;

import android.content.ContentValues;
import android.database.Cursor;

import personal.rowan.paperforspotify.data.db.CleanCursor;
import personal.rowan.paperforspotify.data.db.Column;
import personal.rowan.paperforspotify.data.model.Friend;

public class FriendsTable
		extends Table {

	public static final String FRIEND_NAME = "friendName";
	public static final String FRIEND_IMAGE_URL = "friendImageUrl";
	public static final String FRIEND_PENDING = "friendPending";
	public static final String FRIEND_REJECTED = "friendRejected";
	public static final String FRIEND_SENT_BY_ME = "friendSentByMe";

	@Override
	protected void onCreateColumns() {
		addColumn(new Column(FRIEND_NAME, Column.ColumnType.TEXT));
		addColumn(new Column(FRIEND_IMAGE_URL, Column.ColumnType.TEXT));
		addColumn(new Column(FRIEND_PENDING, Column.ColumnType.BOOLEAN));
		addColumn(new Column(FRIEND_REJECTED, Column.ColumnType.BOOLEAN));
		addColumn(new Column(FRIEND_SENT_BY_ME, Column.ColumnType.BOOLEAN));
	}

	public static Friend toObject(Cursor cursor) {
		CleanCursor cleanCursor = new CleanCursor(cursor);

		String friendId = cleanCursor.getString(ID);
		String friendName = cleanCursor.getString(FRIEND_NAME);
		String friendImageUrl = cleanCursor.getString(FRIEND_IMAGE_URL);
		boolean pending = cleanCursor.getBoolean(FRIEND_PENDING);
		boolean rejected = cleanCursor.getBoolean(FRIEND_REJECTED);
		boolean sentByMe = cleanCursor.getBoolean(FRIEND_SENT_BY_ME);

		return new Friend(friendId, friendName, friendImageUrl, pending, rejected, sentByMe);
	}

	public static ContentValues toContentValues(Friend object) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(ID, object.getUserId());
		contentValues.put(FRIEND_NAME, object.getName());
		contentValues.put(FRIEND_IMAGE_URL, object.getImageUrl());
		contentValues.put(FRIEND_PENDING, object.isPending());
		contentValues.put(FRIEND_REJECTED, object.isRejected());
		contentValues.put(FRIEND_SENT_BY_ME, object.isSentByMe());

		return contentValues;
	}
}
