package com.cnlaunch.mycar.manager.net;

/**
 * @author xuzhuowei �û�����������ӿ�bean
 */
public class UserCarDTO {
	private String userCarId;
	private String userCarName;

	public String getUserCarId() {
		return userCarId;
	}

	public void setUserCarId(String userCarId) {
		this.userCarId = userCarId;
	}

	public String getUserCarName() {
		return userCarName;
	}

	public void setUserCarName(String userCarName) {
		this.userCarName = userCarName;
	}

	@Override
	public String toString() {
		return userCarId + userCarName;
	}

}
