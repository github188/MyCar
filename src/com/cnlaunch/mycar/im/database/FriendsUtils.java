package com.cnlaunch.mycar.im.database;

import com.cnlaunch.mycar.im.database.ImData.Friends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FriendsUtils {
	public static boolean isMyFriend(Context context, String userUid) {
		Cursor c = null;
		try {
			c = context.getContentResolver().query(Friends.CONTENT_URI,
					new String[] { Friends._ID }, Friends.USERUID + "=? and "+Friends.GROUPUID + "!=?",
					new String[] { userUid,Friends.GROUP_UID_STRANGER }, null);
			if (c != null && c.getCount() > 0) {
				return true;
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}
	
	public static void setToStranger(Context context, String userUid) {
		ContentValues values = new ContentValues();
		values.put(Friends.GROUPUID, Friends.GROUP_UID_STRANGER);
		values.put(Friends.GROUPNAME, Friends.GROUP_NAME_STRANGER);
		context.getContentResolver().update(Friends.CONTENT_URI, values,
				Friends.USERUID + "=?", new String[] { userUid });

	}
	
	public static void setAllToStranger(Context context) {
		ContentValues values = new ContentValues();
		values.put(Friends.GROUPUID, Friends.GROUP_UID_STRANGER);
		values.put(Friends.GROUPNAME, Friends.GROUP_NAME_STRANGER);
		context.getContentResolver().update(Friends.CONTENT_URI, values,
				null, null);

	}
}
