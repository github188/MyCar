package com.cnlaunch.mycar.manager.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author xuzhuowei
 *”√ªß≈‰÷√Model≤„
 */
@DatabaseTable
public class ManagerSetting  implements Serializable {

	private static final long serialVersionUID = -7582623980712135028L;
	@DatabaseField(generatedId = true)
	private int daoId;

	@DatabaseField
	private String key;

	@DatabaseField
	private String value;

	public ManagerSetting(){}
	
	public ManagerSetting(String key,String value){
		this.key = key;
		this.value = value;
	}
	
	public int getDaoId() {
		return daoId;
	}

	public void setDaoId(int daoId) {
		this.daoId = daoId;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
