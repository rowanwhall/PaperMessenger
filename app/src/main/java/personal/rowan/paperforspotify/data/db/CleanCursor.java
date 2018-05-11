package personal.rowan.paperforspotify.data.db;

import android.database.Cursor;

public class CleanCursor {

	private Cursor mCursor;

	public CleanCursor(Cursor cursor) {
		mCursor = cursor;
	}

	public Boolean getBoolean(String columnName) {
		return getBoolean(columnName, null);
	}

	public Boolean getBoolean(String columnName, Boolean defaultValue) {
		int columnIndex = mCursor.getColumnIndex(columnName);

		if (columnIndex != -1) {
			return mCursor.getInt(columnIndex) == 1;
		} else {
			return defaultValue;
		}
	}

	public double getDouble(String columnName) {
		int columnIndex = mCursor.getColumnIndex(columnName);

		if (columnIndex != -1) {
			return mCursor.getDouble(columnIndex);
		} else {
			return 0;
		}
	}

	public int getInt(String columnName) {
		int columnIndex = mCursor.getColumnIndex(columnName);

		if (columnIndex != -1) {
			return mCursor.getInt(columnIndex);
		} else {
			return 0;
		}
	}

	public String getString(String columnName) {
		int columnIndex = mCursor.getColumnIndex(columnName);

		if (columnIndex != -1) {
			return mCursor.getString(columnIndex);
		} else {
			return null;
		}
	}

}
