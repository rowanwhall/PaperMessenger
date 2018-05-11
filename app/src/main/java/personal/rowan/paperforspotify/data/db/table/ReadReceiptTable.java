package personal.rowan.paperforspotify.data.db.table;

import android.content.ContentValues;
import android.database.Cursor;

import personal.rowan.paperforspotify.data.db.CleanCursor;
import personal.rowan.paperforspotify.data.db.Column;
import personal.rowan.paperforspotify.data.model.ReadReceipt;

public class ReadReceiptTable
		extends Table {

	public static final String READ_RECEIPT_MESSAGE_ID = "readReceiptMessageId";
	public static final String READ_RECEIPT_MESSAGE = "readReceiptMessage";

	@Override
	protected void onCreateColumns() {
		addColumn(new Column(READ_RECEIPT_MESSAGE_ID, Column.ColumnType.TEXT));
		addColumn(new Column(READ_RECEIPT_MESSAGE, Column.ColumnType.TEXT));
	}

	public static ReadReceipt toObject(Cursor cursor) {
		CleanCursor cleanCursor = new CleanCursor(cursor);

		String chatId = cleanCursor.getString(ID);
		String messageId = cleanCursor.getString(READ_RECEIPT_MESSAGE_ID);
		String readReceiptMessage = cleanCursor.getString(READ_RECEIPT_MESSAGE);

		return new ReadReceipt(chatId, messageId, readReceiptMessage);
	}

	public static ContentValues toContentValues(ReadReceipt object) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(ID, object.getChatId().replace("-", ""));
		contentValues.put(READ_RECEIPT_MESSAGE_ID, object.getMessageId());
		contentValues.put(READ_RECEIPT_MESSAGE, object.getMessage());

		return contentValues;
	}

}
