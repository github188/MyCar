package com.cnlaunch.mycar.manager.database;

import java.sql.SQLException;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.database.IDatabaseInit;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author xuzhuowei 初始化车辆管家数据表
 */
public class ManagerDatabaseInit implements IDatabaseInit {

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, Context context) {
		try {
			TableUtils.createTable(connectionSource, Account.class);
			TableUtils.createTable(connectionSource, Category.class);
			TableUtils.createTable(connectionSource, ManagerSetting.class);
			TableUtils.createTable(connectionSource, Oil.class);
			TableUtils.createTable(connectionSource, UserCar.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void afterCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, Context context) {
		// 1.添加默认分类

		// type=1 在打开软件首先看到的类别
		// type=2点击“更多”后看到的类别
//		String[] defaultCategorys = context.getResources().getStringArray(
//				R.array.manager_oil_manager_default_category);
//		int len = defaultCategorys.length;
//		String[][] defaultCategory = new String[len][3];
//		for (int i = 0; i < len; i++) {
//			String[] arr = defaultCategorys[i].split("\\|");
//			defaultCategory[i] = arr;
//		}

		// String[][] defaultCategory = new String[][] { { "停车", "11", "1" },
		// { "通行", "10", "1" }, { "加油", "9", "1" }, { "美容", "8", "1" },
		// { "维修", "7", "1" }, { "保险", "6", "1" }, { "罚款", "5", "1" },
		// { "购置", "4", "1" }, { "规费", "3", "1" }, { "事故", "2", "0" },
		// { "保养", "1", "1" }, { "买菜", "9", "0" }, { "三餐", "8", "0" },
		// { "交通", "7", "0" }, { "水果零食", "6", "0" }, { "烟酒茶", "5", "0" },
		// { "日常杂项", "4", "0" }, { "手机费", "3", "0" },
		// { "水电煤气", "2", "0" }, { "房租", "1", "0" },
		// { "送礼请客", "10", "0" }, { "网费", "9", "0" }, { "医药", "8", "0" },
		// { "美容保健", "7", "0" }, { "衣服裤子", "6", "0" },
		// { "鞋帽包包", "5", "0" }, { "化妆饰品", "4", "0" },
		// { "休闲娱乐", "3", "0" }, { "宠物宝贝", "2", "0" },
		// { "旅游度假", "1", "0" }, };

		UserDbHelper helper = OpenHelperManager.getHelper(context,
				UserDbHelper.class);
//		Dao<Category, Integer> daoCategory = helper.getDao(Category.class);
//		try {
//			for (String[] arr : defaultCategory) {
//				daoCategory.create(new Category(arr[0], Integer
//						.parseInt(arr[1]), arr[2]));
//			}
//
//		} catch (SQLException e) {
//			// TODO 数据库初始数据填充失败！！
//			e.printStackTrace();
//		}

		// 2.添加默认设置
		String[][] managerSetting = new String[][] {
				{
						ManagerSettingNames.oilType.toString(),
						"#93,#97,#98,#90,"
								+ context
										.getString(R.string.manager_oil_diesel) },
				{ ManagerSettingNames.budget.toString(), "0" },
				{ ManagerSettingNames.lastBackupDateTime.toString(), "" },
				{ ManagerSettingNames.lastExportDateTime.toString(), "" },
				{ ManagerSettingNames.lastSyncDateTime.toString(), "" },
				{ ManagerSettingNames.categoryGroup.toString(), "0" },
				{ ManagerSettingNames.syncDownAccountPageAchieved.toString(),
						"0" },
				{ ManagerSettingNames.syncDownAccountTotalPage.toString(), "0" },
				{ ManagerSettingNames.syncDownAccountPageFinished.toString(),
						"No" },
				{ ManagerSettingNames.syncDownAccountFinished.toString(), "No" },
				{ ManagerSettingNames.syncDownOilPageAchieved.toString(), "0" },
				{ ManagerSettingNames.syncDownOilTotalPage.toString(), "0" },
				{ ManagerSettingNames.syncDownOilPageFinished.toString(), "No" },
				{ ManagerSettingNames.syncDownOilFinished.toString(), "No" },
				{
						ManagerSettingNames.syncDownManagerSettingFinished
								.toString(), "No" },
				{
						ManagerSettingNames.syncDownCustomCategoryFinished
								.toString(), "No" },
				{ ManagerSettingNames.syncDownUserCarFinished.toString(), "No" },
				{ ManagerSettingNames.syncDownFinished.toString(), "No" },
				{ ManagerSettingNames.oilPrice.toString(), "" }, 
				{ ManagerSettingNames.lastOilType.toString(), "#93" }, 
				};

		Dao<ManagerSetting, Integer> daoManagerSetting = helper
				.getDao(ManagerSetting.class);
		try {
			for (String[] arr : managerSetting) {
				daoManagerSetting.create(new ManagerSetting(arr[0], arr[1]));
			}

		} catch (SQLException e) {
			// TODO 数据库初始数据填充失败！！
			e.printStackTrace();
		}

		// 3.添加默认车辆
		Dao<UserCar, Integer> daoUserCar = helper.getDao(UserCar.class);
		try {
			String defaultCarName = context.getResources().getString(
					R.string.manager_usercar_default_nickname);
			daoUserCar.create(new UserCar(UUID.randomUUID().toString()
					.replace("-", ""), defaultCarName));
			Log.e("Init",defaultCarName);
		} catch (SQLException e) {
			// TODO 数据库初始数据填充失败！！
			e.printStackTrace();
		}

		OpenHelperManager.releaseHelper();
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,
			Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,
			Context context) {
		// TODO Auto-generated method stub

	}

}
