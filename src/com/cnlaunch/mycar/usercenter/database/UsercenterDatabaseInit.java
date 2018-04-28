package com.cnlaunch.mycar.usercenter.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cnlaunch.mycar.common.database.IDatabaseInit;
import com.cnlaunch.mycar.manager.database.Account;
import com.cnlaunch.mycar.manager.database.Category;
import com.cnlaunch.mycar.manager.database.ManagerSetting;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.database.UserCar;
import com.cnlaunch.mycar.rescuevehicles.EmergencyTelephone;
import com.cnlaunch.mycar.updatecenter.Device;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class UsercenterDatabaseInit implements IDatabaseInit {

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, Context context) {
		// TODO Auto-generated method stub
		try {
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, EmergencyTelephone.class);
			TableUtils.createTable(connectionSource, Device.class);
			TableUtils.createTable(connectionSource, ExUser.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,
			Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,
			Context context) {
		// TODO Auto-generated method stub

	}

}
