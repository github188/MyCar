package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.manager.database.ManagerSetting;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.j256.ormlite.dao.Dao;

/**
 * @author xuzhuowei 用户配置BLL层
 */
public class ManagerSettingBll {
	private Dao<ManagerSetting, Integer> dao;

	public ManagerSettingBll(Context context, UserDbHelper dbHelper) {
		this.dao = dbHelper.getDao(ManagerSetting.class);
	}

	public void save(ManagerSetting managerSetting) {
		try {
			dao.createIfNotExists(managerSetting);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void save(String key, String value) {

		ManagerSetting managerSetting = new ManagerSetting();
		managerSetting.setKey(key);
		managerSetting.setValue(value);
		save(managerSetting);
	}

	public void update(ManagerSetting managerSetting) {
		try {
			dao.update(managerSetting);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String[] getOilType() {
		String oilType = find(ManagerSettingNames.oilType.toString());
		Log.e("Oil", oilType);
		if (oilType == null) {
			return null;
		} else {
			return oilType.split(",");
		}

	}

	public String find(String key) {
		List<ManagerSetting> list;

		try {
			list = dao.queryForAll();
		} catch (SQLException e) {
			return "";
		}

		if (list != null) {
			for (ManagerSetting ms : list) {
				if (ms.getKey().equals(key)) {
					return ms.getValue();
				}
			}
		}
		return "";
	}

	public void update(String key, String value) {
		List<ManagerSetting> list;

		try {
			list = dao.queryForAll();
		} catch (SQLException e) {
			return;
		}

		if (list != null) {
			for (ManagerSetting ms : list) {
				if (ms.getKey().equals(key)) {
					try {
						ms.setValue(value);
						dao.update(ms);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public List<ManagerSetting> getAllManagerSettingForSync() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			return null;
		}
	}
	//获得油品类型
	public String[] getAllLocalOilType(Context context) {
		
		Map<String,String> categroyMap = new  HashMap<String,String>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_oiltype);
		int len = defaultCategorys.length;
		String[] oilTypes = new String[len];
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
			String oilType = arr[0].trim();
			oilTypes[i] = oilType;
		}
		return oilTypes;

	}
	//获得油品类型
	public Map<String,String> getAllLocalOilTypeByID(Context context) {
		
		Map<String,String> oilTypeMap = new  HashMap<String,String>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_oiltype);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
				String oilType = arr[0].trim();
				String oilTypeId = arr[1].trim();
				oilTypeMap.put(oilTypeId, oilType);
		}
		return oilTypeMap;

	}
	//获得油品类型	
	public Map<String,String> getAllLocalOilTypeByName(Context context) {
		
		Map<String,String> oilTypeMap = new  HashMap<String,String>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_oiltype);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
				String oilType = arr[0].trim();
				String oilTypeId = arr[1].trim();
				oilTypeMap.put(oilType,oilTypeId);
		}
		return oilTypeMap;

	}
}
