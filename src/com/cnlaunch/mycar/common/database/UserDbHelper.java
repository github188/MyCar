package com.cnlaunch.mycar.common.database;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

public class UserDbHelper extends MyCarDbHelper {

	public UserDbHelper(Context context) {
		super(context, getCurrentUserDbName(context), null,
				MyCarConfig.dataBaseVersion,
				MyCarConfig.classesForUserDatabaseInit);
	}

	/**
	 * @return ��ǰ��¼�û������ݿ�����,�û���¼����ΪANONYMOUS_DATABASE_NAME
	 */
	public static String getCurrentUserDbName(Context context) {
		String dbName = MyCarConfig.currentCCToDbName == null ? UsercenterConstants.ANONYMOUS_DATABASE_NAME
				: MyCarConfig.currentCCToDbName;
		Log.i("MyCarDbHelper", "��ǰ���ݿ�����֣�" + dbName);
		return dbName;
	}
}
