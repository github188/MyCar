package com.cnlaunch.mycar.rescuevehicles;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 *@author zhangweiwei
 *@version 2011-12-9下午4:04:44
 *类说明：请求商家简要信息时需要的一个请求参数为该类的一个对象
 */
public class ServiceMerchantCondition implements KvmSerializable{
	private String lat;//经度------------------0
	private String lng;//纬度------------------1
	private String serviceType;//服务类型------2
	private Integer raidus;//半径--------------3
	private String minLat;//最小经度-----------4
	private String minLng;//最小纬度-----------5
	private String maxLat;//最大经度-----------6
	private String maxLng;//最大纬度-----------7
	public ServiceMerchantCondition(String lat,String lng){
		this.lat = lat;
		this.lng = lng;
	}
	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		Object res = null;
		switch (arg0) {
		case 0:
			res = this.lat;
			break;
		case 1:
			res = this.lng;
			break;
		case 2:
			res = this.serviceType;
			break;
		case 3:
			res = this.raidus;
			break;
		case 4:
			res = this.minLat;
			break;
		case 5:
			res = this.minLng;
			break;
		case 6:
			res = this.maxLat;
			break;
		case 7:
			res = this.maxLng;
			break;
       
		default:
			break;
		}
		return res;
	}
	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 8;
	}
	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "lat";
			break;
		case 1:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "lng";
			break;
		case 2:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name= "serviceType";
			break;
		case 3:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "raidus";
			break;
		case 4:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "minLat";
			break;
		case 5:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "minLng";
			break;
		case 6:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "maxLat";
			break;
		case 7:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "maxLng";
			break;

		default:
			break;
		}
		
	}
	@Override
	public void setProperty(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg1==null)return;
		switch (arg0) {
		case 0:
			this.lat = arg1.toString();
			break;
		case 1:
			this.lng = arg1.toString();
			break;
		case 2:
			this.serviceType= arg1.toString();
			break;
		case 3:
			this.raidus = Integer.valueOf(arg1.toString());
			break;
		case 4:
			this.minLat = arg1.toString();
			break;
		case 5:
			this.minLng = arg1.toString();
			break;
		case 6:
			this.maxLat = arg1.toString();
			break;
		case 7:
			this.maxLng = arg1.toString();
			break;

		default:
			break;
		}
		
	}
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		if (lat != null)
		{
			stringBuffer.append(lat);			
		}
		if (lng != null)
		{
			stringBuffer.append(lng);	
		}
		if (serviceType != null)
		{
			stringBuffer.append(serviceType);	
		}
		if (raidus != null)
		{
			stringBuffer.append(raidus);
		}
		if (minLat != null)
		{
			stringBuffer.append(minLat);
		}
		if (raidus != null)
		{
			stringBuffer.append(raidus);
		}
		if (minLng != null)
		{
			stringBuffer.append(minLng);
		}
		if (maxLat != null)
		{
			stringBuffer.append(maxLat);
		}
		if (maxLng != null)
		{
			stringBuffer.append(maxLng);
		}
		return stringBuffer.toString();
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Integer getRaidus() {
		return raidus;
	}
	public void setRaidus(Integer raidus) {
		this.raidus = raidus;
	}
	public String getMinLat() {
		return minLat;
	}
	public void setMinLat(String minLat) {
		this.minLat = minLat;
	}
	public String getMinLng() {
		return minLng;
	}
	public void setMinLng(String minLng) {
		this.minLng = minLng;
	}
	public String getMaxLat() {
		return maxLat;
	}
	public void setMaxLat(String maxLat) {
		this.maxLat = maxLat;
	}
	public String getMaxLng() {
		return maxLng;
	}
	public void setMaxLng(String maxLng) {
		this.maxLng = maxLng;
	}

}
