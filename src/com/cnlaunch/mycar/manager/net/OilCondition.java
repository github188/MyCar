package com.cnlaunch.mycar.manager.net;

import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.Format;

/**
 * @author xuzhuowei 加油记录下载查询条件，网络接口bean
 */
public class OilCondition implements KvmSerializable {
	private String startDate = "";
	private String endDate = "";
	private String currentLanguage;
	
	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}

	public OilCondition() {
	}

	public OilCondition(Date startDate, Date endDate) {
		this.startDate = Format.DateStr.getDate(startDate);
		this.startDate = Format.DateStr.getDate(endDate);
		this.currentLanguage = Env.GetCurrentLanguage().trim();
	}

	public OilCondition(String starDate, String endDate) {
		this.startDate = starDate;
		this.startDate = endDate;
		this.currentLanguage = Env.GetCurrentLanguage().trim();
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public Object getProperty(int arg0) {
		switch (arg0) {
		case 0:
			return startDate;
		case 1:
			return endDate;
		case 2:
			return currentLanguage;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		switch (index) {
		case 0:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "startDate";
			break;
		case 1:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "endDate";
			break;
		case 2:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "currentLanguage";
			break;
		default:
			break;
		}
	}

	@Override
	public void setProperty(int index, Object arg1) {
		switch (index) {
		case 0:
			startDate = arg1.toString();
			break;
		case 1:
			endDate = arg1.toString();
			break;
		case 2:
			currentLanguage = arg1.toString();
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		return startDate + endDate+currentLanguage;
	}

}
