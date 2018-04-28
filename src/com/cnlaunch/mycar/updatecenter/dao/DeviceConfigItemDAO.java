package com.cnlaunch.mycar.updatecenter.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.updatecenter.model.DeviceConfigItem;
import com.j256.ormlite.dao.Dao;

/**
 * 数据库处理
 * */
public class DeviceConfigItemDAO
{
	private Dao<DeviceConfigItem,Integer> dao;
	Context context;
	
	public DeviceConfigItemDAO(Context c,UserDbHelper dbHelper)
	{
		if(dbHelper==null)
		{
			throw new NullPointerException("dbHelper 空指针错误!");
		}
		this.context = c;
		this.dao = dbHelper.getDao(DeviceConfigItem.class);
	}
	
	// 查询所有可用的配置项
	public List<DeviceConfigItem> getAllAvailableItems()
	{
		List<DeviceConfigItem> result = null;
		try 
		{
			result =  dao.queryForAll();
		} 
		catch (SQLException e) 
		{
		}
		return result;
	}
	
	public List<DeviceConfigItem> findConfigByVehicleType(String vehicleType)
	{
		List<DeviceConfigItem> result = null;
		try 
		{
			result =  dao.queryForEq("vehiecleType", vehicleType);
		} 
		catch (SQLException e) 
		{
		}
		return result;
	}
	
	public List<DeviceConfigItem> findConfigByVersion(String version)
	{
		List<DeviceConfigItem> result = null;
		try 
		{
			result =  dao.queryForEq("version", version);
		} 
		catch (SQLException e) 
		{
		}
		return result;
	}
	
	public List<DeviceConfigItem> findConfigByLanguage(String lang)
	{
		List<DeviceConfigItem> result = null;
		try 
		{
			result =  dao.queryForEq("language", lang);
		} 
		catch (SQLException e) 
		{
		}
		return result;
	}
	
	public void addConfigItem(DeviceConfigItem item)
	{
		
		
	}
	
	public void removeConfigItem(DeviceConfigItem item)
	{
		
	}
}
