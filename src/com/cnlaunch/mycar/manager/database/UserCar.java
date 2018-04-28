package com.cnlaunch.mycar.manager.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author xuzhuowei
 *ÓÃ»§³µÁ¾Model²ã
 */
@DatabaseTable
public class UserCar  implements Serializable {

	private static final long serialVersionUID = -7582623980712135028L;
	@DatabaseField(generatedId = true)
	private int daoId;

	@DatabaseField
	private String userCarId;

	@DatabaseField
	private String userCarName;
	
	public UserCar(){}

	public UserCar(String userCarId,String userCarName){
		this.userCarId = userCarId;
		this.userCarName = userCarName;
	}

	public int getDaoId() {
		return daoId;
	}

	public void setDaoId(int daoId) {
		this.daoId = daoId;
	}

	public String getUserCarId() {
		return userCarId;
	}

	public void setUserCarId(String userCarId) {
		this.userCarId = userCarId.replace("-", "");
	}

	public String getUserCarName() {
		return userCarName;
	}

	public void setUserCarName(String userCarName) {
		this.userCarName = userCarName;
	}


	
}
