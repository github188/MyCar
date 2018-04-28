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
 * @author xuzhuowei ��ʼ�������ܼ����ݱ�
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
		// 1.���Ĭ�Ϸ���

		// type=1 �ڴ�������ȿ��������
		// type=2��������ࡱ�󿴵������
//		String[] defaultCategorys = context.getResources().getStringArray(
//				R.array.manager_oil_manager_default_category);
//		int len = defaultCategorys.length;
//		String[][] defaultCategory = new String[len][3];
//		for (int i = 0; i < len; i++) {
//			String[] arr = defaultCategorys[i].split("\\|");
//			defaultCategory[i] = arr;
//		}

		// String[][] defaultCategory = new String[][] { { "ͣ��", "11", "1" },
		// { "ͨ��", "10", "1" }, { "����", "9", "1" }, { "����", "8", "1" },
		// { "ά��", "7", "1" }, { "����", "6", "1" }, { "����", "5", "1" },
		// { "����", "4", "1" }, { "���", "3", "1" }, { "�¹�", "2", "0" },
		// { "����", "1", "1" }, { "���", "9", "0" }, { "����", "8", "0" },
		// { "��ͨ", "7", "0" }, { "ˮ����ʳ", "6", "0" }, { "�̾Ʋ�", "5", "0" },
		// { "�ճ�����", "4", "0" }, { "�ֻ���", "3", "0" },
		// { "ˮ��ú��", "2", "0" }, { "����", "1", "0" },
		// { "�������", "10", "0" }, { "����", "9", "0" }, { "ҽҩ", "8", "0" },
		// { "���ݱ���", "7", "0" }, { "�·�����", "6", "0" },
		// { "Ьñ����", "5", "0" }, { "��ױ��Ʒ", "4", "0" },
		// { "��������", "3", "0" }, { "���ﱦ��", "2", "0" },
		// { "���ζȼ�", "1", "0" }, };

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
//			// TODO ���ݿ��ʼ�������ʧ�ܣ���
//			e.printStackTrace();
//		}

		// 2.���Ĭ������
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
			// TODO ���ݿ��ʼ�������ʧ�ܣ���
			e.printStackTrace();
		}

		// 3.���Ĭ�ϳ���
		Dao<UserCar, Integer> daoUserCar = helper.getDao(UserCar.class);
		try {
			String defaultCarName = context.getResources().getString(
					R.string.manager_usercar_default_nickname);
			daoUserCar.create(new UserCar(UUID.randomUUID().toString()
					.replace("-", ""), defaultCarName));
			Log.e("Init",defaultCarName);
		} catch (SQLException e) {
			// TODO ���ݿ��ʼ�������ʧ�ܣ���
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
