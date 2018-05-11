package personal.rowan.paperforspotify.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import personal.rowan.paperforspotify.data.db.table.ChatMessagesTable;
import personal.rowan.paperforspotify.data.db.table.ChatsTable;
import personal.rowan.paperforspotify.data.db.table.FriendsTable;
import personal.rowan.paperforspotify.data.db.table.ReadReceiptTable;
import personal.rowan.paperforspotify.data.model.Chat;
import personal.rowan.paperforspotify.data.model.ChatMessage;
import personal.rowan.paperforspotify.data.model.Friend;
import personal.rowan.paperforspotify.data.model.ReadReceipt;

@Singleton
public class DatabaseManager extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "PaperDatabase";
	private static final int DATABASE_VERSION = 2;

	private static DatabaseManager instance;

	private DatabaseManager() {
		super(PaperApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
		getWritableDatabase();
	}

	public static DatabaseManager getInstance() {
		if(instance == null) {
			instance = new DatabaseManager();
		}

		return instance;
	}

	public void saveFriends(List<Friend> friends) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for(Friend friend : friends) {
				insertOrReplaceValue(FriendsTable.toContentValues(friend), FriendsTable.class.getSimpleName());
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBError", "insertFriends");
		} finally {
			db.endTransaction();
		}
	}

	public List<Friend> loadFriends() {
		List<Friend> friends = new ArrayList<>();

		try {
			Cursor cursor = getWritableDatabase().query(
					FriendsTable.class.getSimpleName(),
					null, null, null, null, null,
					FriendsTable.FRIEND_NAME + " COLLATE NOCASE");

			if(cursor.moveToFirst()) {
				do {
					friends.add(FriendsTable.toObject(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadChats");
		}

		return friends;
	}

	@SuppressWarnings("unused")
	public HashMap<String, Friend> loadFriendsMap() {
		HashMap<String, Friend> friends = new HashMap<>();

		try {
			Cursor cursor = getWritableDatabase().query(
					FriendsTable.class.getSimpleName(),
					null, null, null, null, null,
					FriendsTable.FRIEND_NAME + " COLLATE NOCASE");

			if(cursor.moveToFirst()) {
				do {
					Friend friend = FriendsTable.toObject(cursor);
					friends.put(friend.getUserId(), friend);
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadChats");
		}

		return friends;
	}

	public void saveChats(List<Chat> chats) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for(Chat chat : chats) {
				insertOrReplaceValue(ChatsTable.toContentValues(chat), ChatsTable.class.getSimpleName());
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBError", "insertChats");
		} finally {
			db.endTransaction();
		}
	}

	public List<Chat> loadChats() {
		List<Chat> chats = new ArrayList<>();

		try {
			Cursor cursor = getWritableDatabase().query(
					ChatsTable.class.getSimpleName(),
					null, null, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					chats.add(ChatsTable.toObject(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadChats");
		}

		return chats;
	}

	public List<Chat> loadChat(String chatId) {
		List<Chat> chats = new ArrayList<>();

		try {
			Cursor cursor = getWritableDatabase().query(
					ChatsTable.class.getSimpleName(),
					null,
					ChatsTable.ID + "=?",
					new String[] {chatId},
					null, null, null);

			if (cursor.moveToFirst()) {
				do {
					chats.add(ChatsTable.toObject(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadChat by id");
		}

		return chats;
	}

	public void deleteChat(String chatId) {
		deleteRow(ChatsTable.class.getSimpleName(), chatId);
	}

	public void saveChatMessages(List<ChatMessage> messages) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			for(ChatMessage message : messages) {
				insertOrReplaceValue(ChatMessagesTable.toContentValues(message), ChatMessagesTable.class.getSimpleName());
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBError", "insertChatMessages");
		} finally {
			db.endTransaction();
		}
	}

	public List<ChatMessage> loadChatMessages(String chatId) {
		List<ChatMessage> messages = new ArrayList<>();

		try {
			Cursor cursor = getWritableDatabase().query(
					ChatMessagesTable.class.getSimpleName(),
					null,
					ChatMessagesTable.MESSAGE_CHAT_ID + "=?",
					new String[] {chatId.replace("-", "")},
					null, null, null);

			if (cursor.moveToFirst()) {
				do {
					messages.add(ChatMessagesTable.toObject(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadChatMessages for " + chatId);
		}

		return messages;
	}

	public void saveReadReceipt(ReadReceipt readReceipt) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			insertOrReplaceValue(ReadReceiptTable.toContentValues(readReceipt), ReadReceiptTable.class.getSimpleName());

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBError", "insertReadReceipt");
		} finally {
			db.endTransaction();
		}
	}

	public ReadReceipt loadReadReceipt(String chatId) {
		ReadReceipt readReceipt = null;

		try {
			Cursor cursor = getWritableDatabase().query(
					ReadReceiptTable.class.getSimpleName(),
					null,
					ReadReceiptTable.ID + "=?",
					new String[] {chatId.replace("-", "")},
					null, null, null);

			if (cursor.moveToFirst()) {
				do {
					readReceipt = ReadReceiptTable.toObject(cursor);
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch(SQLiteException e) {
			Log.e("DBError", "loadReadReceipt for " + chatId);
		}

		return readReceipt;
	}

	public void clearAllRows() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(ChatsTable.class.getSimpleName(), null, null);
		db.delete(ChatMessagesTable.class.getSimpleName(), null, null);
		db.delete(FriendsTable.class.getSimpleName(), null, null);
		db.delete(ReadReceiptTable.class.getSimpleName(), null, null);
	}

	private void insertOrReplaceValue(ContentValues values, String tableName) {
		getWritableDatabase().replace(tableName, null, values);
	}

	private void deleteRow(String tableName, String id) {
		getWritableDatabase().delete(tableName, "_id=?", new String[] {id});
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(new ChatsTable().generateSQL());
		db.execSQL(new ChatMessagesTable().generateSQL());
		db.execSQL(new FriendsTable().generateSQL());
		db.execSQL(new ReadReceiptTable().generateSQL());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
