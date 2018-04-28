package com.cnlaunch.mycar.im.database;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.database.ImData.LastChat;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;

public class ImDataProvider extends ContentProvider {
	private static final int FRIENDS = 1;
	private static final int FRIEND_ID = 2;
	private static final int FRIEND_CHATLOG = 3;
	private static final int CHATLOG = 5;
	private static final int CHATLOG_ID = 6;
	private static final int SEND_FILE_TASK = 8;
	private static final int SEND_FILE_TASK_ID = 9;
	private static final int LAST_CHAT = 10;
	private static final int LAST_CHAT_ID = 11;
	private static final int LAST_CHAT_NEWEST = 12;

	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(ImData.AUTHORITY, "friends", FRIENDS);
		sUriMatcher.addURI(ImData.AUTHORITY, "friends/#", FRIEND_ID);
		sUriMatcher.addURI(ImData.AUTHORITY, "friends/#/chatlog",
				FRIEND_CHATLOG);
		sUriMatcher.addURI(ImData.AUTHORITY, "chatlog", CHATLOG);
		sUriMatcher.addURI(ImData.AUTHORITY, "chatlog/#", CHATLOG_ID);
		sUriMatcher.addURI(ImData.AUTHORITY, "sendfiletask", SEND_FILE_TASK);
		sUriMatcher.addURI(ImData.AUTHORITY, "sendfiletask/#",
				SEND_FILE_TASK_ID);
		sUriMatcher.addURI(ImData.AUTHORITY, "lastchat", LAST_CHAT);
		sUriMatcher.addURI(ImData.AUTHORITY, "lastchat/#", LAST_CHAT_ID);
		sUriMatcher.addURI(ImData.AUTHORITY, "lastchat/newest",
				LAST_CHAT_NEWEST);

	}

	private static ImDatabaseHelper mDbHelper;

	private static final void d(String log) {
		Log.e("ImDataProvider", log);
	}

	@Override
	public boolean onCreate() {
		if (mDbHelper == null) {
			mDbHelper = new ImDatabaseHelper(getContext());
		}

		return true;

	}

	/**
	 * 用户登录后，切换用户数据库
	 * 
	 * @param context
	 */
	public static void changeUserDatabase(Context context) {
		if (mDbHelper != null) {
			mDbHelper.closeDb();
		}
		mDbHelper = new ImDatabaseHelper(context);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sUriMatcher.match(uri);

		String tableName = null;
		String whereclause = null;
		String sortorder = sortOrder;
		List<String> pathSegments = uri.getPathSegments();
		switch (match) {
		case FRIENDS:
			tableName = Friends.TABLE;
			break;
		case FRIEND_ID:
			tableName = Friends.TABLE;
			whereclause = Friends._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case CHATLOG:
			tableName = ChatLog.TABLE;
			break;
		case CHATLOG_ID:
			tableName = ChatLog.TABLE;
			whereclause = ChatLog._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case SEND_FILE_TASK:
			tableName = SendFileTask.TABLE;
			break;
		case SEND_FILE_TASK_ID:
			tableName = SendFileTask.TABLE;
			whereclause = SendFileTask._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case LAST_CHAT_NEWEST:
			return mDbHelper.getLastNewestChat();
		case LAST_CHAT:
			tableName = LastChat.TABLE;
			break;
		case LAST_CHAT_ID:
			tableName = LastChat.TABLE;
			whereclause = LastChat._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;

		default:
			d("无法识别URI: " + uri.toString());
			break;
		}
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setTables(tableName);
		if (whereclause != null) {
			qBuilder.appendWhere(whereclause);
		}

		SQLiteDatabase sqlDb = mDbHelper.getWritableDatabase();
		Cursor c = qBuilder.query(sqlDb, projection, selection, selectionArgs,
				null, null, sortorder);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		String mime = null;
		switch (match) {
		case FRIENDS:
			mime = Friends.CONTENT_TYPE;
			break;
		case FRIEND_ID:
			mime = Friends.CONTENT_ITME_TYPE;
			break;
		case CHATLOG:
			mime = ChatLog.CONTENT_TYPE;
			break;
		case CHATLOG_ID:
			mime = ChatLog.CONTENT_ITME_TYPE;
			break;
		case SEND_FILE_TASK:
			mime = SendFileTask.CONTENT_TYPE;
			break;
		case SEND_FILE_TASK_ID:
			mime = SendFileTask.CONTENT_ITME_TYPE;
			break;
		case LAST_CHAT:
			mime = LastChat.CONTENT_TYPE;
			break;
		case LAST_CHAT_ID:
			mime = LastChat.CONTENT_ITME_TYPE;
			break;
		default:
			break;
		}
		return mime;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri insertedUri = null;
		int match = sUriMatcher.match(uri);
		long id = 0;
		switch (match) {
		case FRIENDS:
			id = mDbHelper.insertFriend(values);
			insertedUri = ContentUris.withAppendedId(uri, id);
			break;
		case CHATLOG:
			id = mDbHelper.insertChatLog(values);
			insertedUri = ContentUris.withAppendedId(uri, id);
			break;
		case SEND_FILE_TASK:
			id = mDbHelper.insertSendFileTask(values);
			insertedUri = ContentUris.withAppendedId(uri, id);
			break;
		case LAST_CHAT:
			id = mDbHelper.insertLastChat(values);
			insertedUri = ContentUris.withAppendedId(uri, id);
			break;
		default:
			break;
		}
		return insertedUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sUriMatcher.match(uri);
		int affected = 0;
		switch (match) {
		case FRIENDS:
			affected = mDbHelper.deleteFriend(selection,selectionArgs);
			break;
		case FRIEND_ID:
			affected = mDbHelper.deleteFriend(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		case CHATLOG_ID:
			affected = mDbHelper.deleteChatLog(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		case SEND_FILE_TASK_ID:
			affected = mDbHelper.deleteSendFileTask(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		case LAST_CHAT_ID:
			affected = mDbHelper.deleteLastChat(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		default:
			break;
		}
		return affected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = sUriMatcher.match(uri);
		int updates = -1;
		long id = 0;
		switch (match) {
		case FRIENDS:
			updates = mDbHelper.updateFriend(values, selection,selectionArgs);
			break;
		case FRIEND_ID:
			id = new Long(uri.getLastPathSegment()).longValue();
			updates = mDbHelper.updateFriend(id, values);
			break;
		case CHATLOG_ID:
			id = new Long(uri.getLastPathSegment()).longValue();
			updates = mDbHelper.updateChatLog(id, values);
			break;
		case SEND_FILE_TASK_ID:
			id = new Long(uri.getLastPathSegment()).longValue();
			updates = mDbHelper.updateSendFileTaskLog(id, values);
			break;
		case LAST_CHAT_ID:
			id = new Long(uri.getLastPathSegment()).longValue();
			updates = mDbHelper.updateLastChat(id, values);
			break;
		default:
			break;
		}
		return updates;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int match = sUriMatcher.match(uri);
		int update = -1;
		switch (match) {
		case FRIENDS:
			update = mDbHelper.bulkInsertFriends(values);
			break;
		default:
			break;
		}
		return update;
	}

}
