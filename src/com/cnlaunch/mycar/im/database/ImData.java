package com.cnlaunch.mycar.im.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ImData {
	public static final String AUTHORITY = "com.cnlaunch.mycar.im";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Friends extends FriendsColumns implements
			android.provider.BaseColumns {
		public static final String TABLE = "friends";
		public static final String CONTENT_ITME_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.im.friend";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.im.friend";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE);

		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "("
				+ " " + BaseColumns._ID + " " + _ID_TYPE + ","
				+ " " + USERUID	+ " " + USERUID_TYPE + "," 
				+ " " + IM_MY_FRIEND_RELATION_ID	+ " " + IM_MY_FRIEND_RELATION_ID_TYPE + "," 
				+ " " + PARENT_FRIEND_UID	+ " " + PARENT_FRIEND_UID_TYPE + "," 
				+ " " + USERSIGN	+ " " + USERSIGN_TYPE + "," 
				+ " " + TASKID	+ " " + TASKID_TYPE + "," 
				+ " " + NAME_REMARK	+ " " + NAME_REMARK_TYPE + "," 
				+ " " + CCNO + " " + CCNO_TYPE + ","
				+ " " + NICKNAME + " " + NICKNAME_TYPE + "," + " " + FACEID
				+ " " + FACEID_TYPE + "," + " " + GROUPUID + " " + GROUPUID_TYPE
				+ "," + " " + GROUPNAME + " " + GROUPNAME_TYPE + ");";

		static final String SQL_CREATE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS index_useruid ON " + TABLE + "(" + USERUID + ")";
	}

	public static final class ChatLog extends ChatLogColumns implements
			android.provider.BaseColumns {
		public static final String TABLE = "chatlog";
		public static final String CONTENT_ITME_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.im.chatlog";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.im.chatlog";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE);

		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "("
				+ " " + BaseColumns._ID + " " + _ID_TYPE + "," + " " + CONTENT
				+ " " + CONTENT_COLUMN_TYPE + "," + " " + RECEIVER + " "
				+ RECEIVER_TYPE + "," + " " + SENDER + " " + SENDER_TYPE + ","
				+ " " + SENDTIME + " " + SENDTIME_TYPE + "," + " " + ISREAD
				+ " " + ISREAD_TYPE + ");";
	}

	public static final class LastChat extends LastChatColumns implements
			android.provider.BaseColumns {
		public static final String TABLE = "lastchat";
		public static final String CONTENT_ITME_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.im.lastchat";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.im.lastchat";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE);

		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "("
				+ " " + BaseColumns._ID + " " + _ID_TYPE + "," + " " + CONTENT
				+ " " + CONTENT_COLUMN_TYPE + "," + " " + USERUID + " "
				+ TARGET_USERUID_TYPE + "," + " " + SENDTIME + " "
				+ SENDTIME_TYPE + "," + " " + UNREAD_COUNT + " "
				+ UNREAD_COUNT_TYPE + ");";
	}

	public static final class SendFileTask extends SendFileTaskColums implements
			android.provider.BaseColumns {
		public static final String TABLE = "sendfiletask";
		public static final String CONTENT_ITME_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.im.sendfiletask";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.im.sendfiletask";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE);

		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "("
				+ " " + BaseColumns._ID + " " + _ID_TYPE + "," + " " + FILE_ID
				+ " " + FILE_ID_TYPE + "," + " " + SEND_USERUID + " "
				+ SEND_USERUID_TYPE + "," + " " + RECEIVE_USERUID + " "
				+ RECEIVE_USERUID_TYPE + "," + " " + FILE_NAME + " "
				+ FILE_NAME_TYPE + "," + " " + FILE_SIZE + " " + FILE_SIZE_TYPE
				+ "," + " " + STATE + " " + STATE_TYPE + "," + " " + SEND_TIME
				+ " " + SEND_TIME_TYPE + "," + " " + FILE_PATH + " "
				+ FILE_PATH_TYPE + ");";

	}

	static class ChatLogColumns {
		public static final String CONTENT = "content";
		public static final String SENDER = "sender";
		public static final String RECEIVER = "receiver";
		public static final String SENDTIME = "sendTime";
		public static final String ISREAD = "isRead";

		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
		static final String CONTENT_COLUMN_TYPE = "TEXT";
		static final String SENDER_TYPE = "TEXT";
		static final String RECEIVER_TYPE = "TEXT";
		static final String SENDTIME_TYPE = "INTEGER NOT NULL";
		static final String ISREAD_TYPE = "BOOLEAN";
	}

	static class LastChatColumns {
		public static final String CONTENT = "content";
		public static final String USERUID = "userUid";
		public static final String SENDTIME = "sendTime";
		public static final String UNREAD_COUNT = "unreadCount";

		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
		static final String CONTENT_COLUMN_TYPE = "TEXT";
		static final String TARGET_USERUID_TYPE = "TEXT";
		static final String SENDTIME_TYPE = "INTEGER NOT NULL";
		static final String UNREAD_COUNT_TYPE = "INTEGER";
	}

	static class FriendsColumns {
		public static final String GROUP_UID_STRANGER = "stranger";

		public static final String GROUP_NAME_STRANGER = "陌生人";

		public static final String IM_MY_FRIEND_RELATION_ID = "IMMyFriendRelationID";		
		public static final String PARENT_FRIEND_UID = "parentFriendUID";
		public static final String USERUID = "userUid";
		public static final String CCNO = "ccno";
		public static final String NICKNAME = "nickName";
		public static final String FACEID = "faceId";
		public static final String GROUPUID = "groupUid";
		public static final String GROUPNAME = "groupName";
		public static final String USERSIGN = "userSign";
		public static final String TASKID = "taskId";
		public static final String NAME_REMARK = "nameRemark";

		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
		static final String IM_MY_FRIEND_RELATION_ID_TYPE = "TEXT";
		static final String PARENT_FRIEND_UID_TYPE = "TEXT";
		static final String USERUID_TYPE = "TEXT";
		static final String CCNO_TYPE = "TEXT";
		static final String NICKNAME_TYPE = "TEXT";
		static final String FACEID_TYPE = "INTEGER NOT NULL";
		static final String GROUPUID_TYPE = "TEXT";
		static final String GROUPNAME_TYPE = "TEXT";
		static final String USERSIGN_TYPE = "TEXT";
		static final String TASKID_TYPE = "TEXT";
		static final String NAME_REMARK_TYPE = "TEXT";
	}

	static class SendFileTaskColums {
		/**
		 * 收到文件传输请求
		 */
		public static final int STATE_RECEIVER_GET_REQUEST = 0;
		/**
		 * 同意接收文件
		 */
		public static final int STATE_RECEIVER_ACCEPTED = 1;
		/**
		 * 正在文件下载
		 */
		public static final int STATE_RECEIVER_DOWNLOADING = 2;
		/**
		 * 文件下载中
		 */
		public static final int STATE_RECEIVER_DOWNLAOD_FAILED = 3;
		/**
		 * 完成文件下载
		 */
		public static final int STATE_RECEIVER_DOWNLOAD_FINISHED = 4;
		/**
		 * 拒绝文件下载
		 */
		public static final int STATE_RECEIVER_REFUSED = 5;

		/**
		 * 接收者状态值的最大编号
		 */
		public static final int RECEIVER_STATE_MAX_NUM = 100;

		/**
		 * 上传文件中
		 */
		public static final int STATE_SENDER_UPLOADING = 101;
		/**
		 * 上传取消
		 */
		public static final int STATE_SENDER_UPLOAD_FAILED = 102;
		/**
		 * 上传完成
		 */
		public static final int STATE_SENDER_UPLOAD_FINISHED = 103;
		/**
		 * 已发送文件传输请求
		 */
		public static final int STATE_SENDER_SEND_REQUEST = 104;
		/**
		 * 对方已成功接收文件
		 */
		public static final int STATE_SENDER_GET_RESPONSE_ACCEPTED = 105;
		/**
		 * 对方拒绝接收文件
		 */
		public static final int STATE_SENDER_GET_RESPONSE_REFUSED = 106;

		public static final int REPLY_ACCEPT = 0;
		public static final int REPLY_REFUSE = 1;

		public static final String FILE_ID = "fileId";
		public static final String SEND_USERUID = "sendUserUID";
		public static final String RECEIVE_USERUID = "ReceiveUserUID";
		public static final String FILE_NAME = "fileName";
		public static final String FILE_SIZE = "fileSize";
		public static final String STATE = "state";
		public static final String SEND_TIME = "sendTime";
		public static final String FILE_PATH = "filePath";

		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
		static final String FILE_ID_TYPE = "TEXT";
		static final String SEND_USERUID_TYPE = "TEXT";
		static final String RECEIVE_USERUID_TYPE = "TEXT";
		static final String FILE_NAME_TYPE = "TEXT";
		static final String FILE_SIZE_TYPE = "INTEGER";
		static final String STATE_TYPE = "INTEGER";
		static final String SEND_TIME_TYPE = "INTEGER";
		static final String FILE_PATH_TYPE = "TEXT";
	}

}
