package com.cnlaunch.mycar.im.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.database.ImData.LastChat;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;

public class ImDatabaseHelper extends SQLiteOpenHelper {
	private Context mContext;
	private static int mVersion = 1;
	private static byte[] LOCK_FRIEND_TABLE = new byte[0];
	private static byte[] LOCK_CHAT_MESSAGE_TABLE = new byte[0];
	private static byte[] LOCK_LAST_CHAT_TABLE = new byte[0];

	public ImDatabaseHelper(Context context) {
		super(context, getCurrentUserImDbName(context), null, mVersion);
		mContext = context;
	}

	public void closeDb() {
		getWritableDatabase().close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Friends.SQL_CREATE_TABLE);
		db.execSQL(ChatLog.SQL_CREATE_TABLE);
		db.execSQL(SendFileTask.SQL_CREATE_TABLE);
		db.execSQL(LastChat.SQL_CREATE_TABLE);
		db.execSQL(Friends.SQL_CREATE_INDEX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return 当前登录用户的数据库名称,用户登录，则为ANONYMOUS_DATABASE_NAME
	 */
	public static String getCurrentUserImDbName(Context context) {
		String dbName = (MyCarActivity.cc == null || MyCarActivity.cc.length() == 0) ? ImConstant.CONFIG_DB_NAME_ANONYMOUS
				: ImConstant.CONFIG_DB_NAME_PREFIX + MyCarActivity.cc;
		return dbName;
	}

	public int deleteFriend(long id) {
		synchronized (LOCK_FRIEND_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.delete(Friends.TABLE, Friends._ID + "= ?",
					new String[] { String.valueOf(id) });
		}
	}

	public int deleteFriend(String selection, String[] selectionArgs) {
		synchronized (LOCK_FRIEND_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.delete(Friends.TABLE, selection, selectionArgs);
		}
	}

	public int deleteChatLog(long id) {
		synchronized (LOCK_CHAT_MESSAGE_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			sqldb.delete(ChatLog.TABLE, ChatLog._ID + "= ?",
					new String[] { String.valueOf(id) });
			return 0;
		}
	}

	public int deleteSendFileTask(long id) {
		SQLiteDatabase sqldb = getWritableDatabase();
		int update = sqldb.delete(SendFileTask.TABLE, SendFileTask._ID + "= ?",
				new String[] { String.valueOf(id) });

		if (update > 0) {
			ContentResolver resolver = this.mContext.getContentResolver();
			resolver.notifyChange(SendFileTask.CONTENT_URI, null);
		}
		return update;
	}

	public int deleteLastChat(long id) {
		synchronized (LOCK_LAST_CHAT_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			int update = sqldb.delete(LastChat.TABLE, LastChat._ID + "= ?",
					new String[] { String.valueOf(id) });

			if (update > 0) {
				ContentResolver resolver = this.mContext.getContentResolver();
				resolver.notifyChange(LastChat.CONTENT_URI, null);
			}
			return update;
		}
	}

	public long insertChatLog(ContentValues values) {
		synchronized (LOCK_CHAT_MESSAGE_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			long chatLogId = sqldb.insert(ChatLog.TABLE, null, values);

			ContentResolver resolver = this.mContext.getContentResolver();
			resolver.notifyChange(ChatLog.CONTENT_URI, null);

			return chatLogId;
		}

	}

	/**
	 * 按useruid，如果记录有重复，就更新，否则插入
	 * 
	 * @param values
	 * @return
	 */
	public long insertFriend(ContentValues values) {
		synchronized (LOCK_FRIEND_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			long friendId = sqldb.replace(Friends.TABLE, null, values);

			ContentResolver resolver = this.mContext.getContentResolver();
			resolver.notifyChange(Friends.CONTENT_URI, null);

			return friendId;
		}
	}

	public long insertSendFileTask(ContentValues values) {
		SQLiteDatabase sqldb = getWritableDatabase();
		long taskId = sqldb.insert(SendFileTask.TABLE, null, values);

		ContentResolver resolver = this.mContext.getContentResolver();
		resolver.notifyChange(SendFileTask.CONTENT_URI, null);

		return taskId;
	}

	public long insertLastChat(ContentValues values) {
		synchronized (LOCK_LAST_CHAT_TABLE) {

			SQLiteDatabase sqldb = getWritableDatabase();
			long id = sqldb.insert(LastChat.TABLE, null, values);

			ContentResolver resolver = this.mContext.getContentResolver();
			resolver.notifyChange(LastChat.CONTENT_URI, null);

			return id;
		}
	}

	public int updateChatLog(long id, ContentValues values) {
		synchronized (LOCK_CHAT_MESSAGE_TABLE) {

			String whereclause = ChatLog._ID + " = " + id;
			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.update(ChatLog.TABLE, values, whereclause, null);
		}
	}

	public int updateFriend(long id, ContentValues values) {
		synchronized (LOCK_FRIEND_TABLE) {

			String whereclause = Friends._ID + " = " + id;
			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.update(Friends.TABLE, values, whereclause, null);
		}
	}

	public int updateFriend(ContentValues values, String whereclause,
			String[] selectionArgs) {
		synchronized (LOCK_FRIEND_TABLE) {
			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.update(Friends.TABLE, values, whereclause,
					selectionArgs);
		}
	}

	public int updateLastChat(long id, ContentValues values) {
		synchronized (LOCK_LAST_CHAT_TABLE) {

			String whereclause = LastChat._ID + " = " + id;
			SQLiteDatabase sqldb = getWritableDatabase();
			return sqldb.update(LastChat.TABLE, values, whereclause, null);
		}
	}

	public int updateSendFileTaskLog(long id, ContentValues values) {
		String whereclause = SendFileTask._ID + " = " + id;
		SQLiteDatabase sqldb = getWritableDatabase();
		int update = sqldb
				.update(SendFileTask.TABLE, values, whereclause, null);
		if (update > 0) {
			ContentResolver resolver = this.mContext.getContentResolver();
			resolver.notifyChange(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(id)), null);
		}
		return update;
	}

	public int bulkInsertFriends(ContentValues[] valuesArray) {
		synchronized (LOCK_FRIEND_TABLE) {

			int update = 0;
			for (ContentValues values : valuesArray) {
				insertFriend(values);
				update++;
			}
			return update;
		}
	}

	public Cursor getLastNewestChat() {
		synchronized (LOCK_LAST_CHAT_TABLE) {

			SQLiteDatabase sqldb = getReadableDatabase();

			String rawSql = "select " + LastChat.TABLE + "." + LastChat.USERUID
					+ ", " + LastChat.CONTENT + ", " + LastChat.SENDTIME + ", "
					+ Friends.FACEID + ", " + Friends.NICKNAME + ", "
					+ Friends.CCNO + " " + ", " + LastChat.UNREAD_COUNT + ","
					+ LastChat.TABLE + "." + LastChat._ID + " " + "from "
					+ LastChat.TABLE + " " + "left join " + Friends.TABLE + " "
					+ "on " + LastChat.TABLE + "." + LastChat.USERUID + " "
					+ " = " + Friends.TABLE + "." + Friends.USERUID
					+ " order by " + LastChat.SENDTIME + " " + "desc "
					+ " limit 0,30";
			return sqldb.rawQuery(rawSql, null);
		}
	}

}
