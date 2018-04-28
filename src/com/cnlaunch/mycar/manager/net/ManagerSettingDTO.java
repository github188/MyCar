package com.cnlaunch.mycar.manager.net;

/**
 * @author xuzhuowei
 *车辆管家用户配置,网络接口bean
 */
public class ManagerSettingDTO {
	private String key;
	private String value;
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
	@Override
	public String toString() {
		return key + value ;
	}

	
	
}
