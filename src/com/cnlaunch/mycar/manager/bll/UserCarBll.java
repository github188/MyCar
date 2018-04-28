package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.manager.database.UserCar;
import com.j256.ormlite.dao.Dao;

/**
 * @author xuzhuowei
 *ÓÃ»§³µÁ¾BLL²ã
 */
public class UserCarBll {

	private Dao<UserCar, Integer> dao;

	public UserCarBll(Context context, UserDbHelper dbHelper) {
		if (dbHelper == null) {
			throw (new IllegalArgumentException("DbHelper Should not be null!"));
		}
		this.dao = dbHelper.getDao(UserCar.class);
	}

	public void save(UserCar userCar) {
		try {
			dao.createIfNotExists(userCar);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update(UserCar userCar) {
		try {
			dao.update(userCar);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public UserCar find(int daoId) {
		try {
			return dao.queryForId(daoId);
		} catch (SQLException e) {
			return null;
		}
	}
	
	public List<UserCar> find(String	userCarName) {
		try {
			return dao.queryForEq("userCarName", userCarName);
		} catch (SQLException e) {
			return null;
		}
	}
	
	public void delete(int daoId) {
		try {
			dao.deleteById(daoId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<UserCar> getAllUserCar() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			return null;
		}
	}
	
	public List<UserCar> getAllUserCarForSync() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			return null;
		}
	}
	

}
