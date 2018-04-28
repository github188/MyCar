package com.cnlaunch.mycar.usercenter.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 *@author zhangweiwei
 *@version 2011-12-9下午4:04:44
 *类说明：请求商家简要信息时需要的一个请求参数为该类的一个对象
 */
public class WSExUserInfo implements KvmSerializable{  
	
	private String address; // 0
    private String city;// 1
    private String companyName;// 2
    private String continent;// 3
    private String country; // 4 
    private String familyPhone;// 5
    private String firstName;// 6
    private String lastName;// 7
    private String latitude;// 8
    private String longitude; // 9
    private String markAddress; // 10
    private String officePhone;// 11
    private String province;// 12
    private Integer userId;// 13
    private String zipCode; // 14

	public WSExUserInfo()
	{
		
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}
	public String getFamilyPhone() {
		return familyPhone;
	}
	public void setFamilyPhone(String familyPhone) {
		this.familyPhone = familyPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getMarkAddress() {
		return markAddress;
	}
	public void setMarkAddress(String markAddress) {
		this.markAddress = markAddress;
	}


	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		Object res = null;
		switch (arg0) {
		case 0:
			res = this.address;
			break;
		case 1:
			res = this.city;
			break;
		case 2:
			res = this.companyName;
			break;
		case 3:
			res = this.continent;
			break;
		case 4:
			res = this.country;
			break;
		case 5:
			res = this.familyPhone;
			break;
		case 6:
			res = this.firstName;
			break;
		case 7:
			res = this.lastName;
			break;
		case 8:
			res = this.latitude;
			break;
		case 9:
			res = this.longitude;
			break;
		case 10:
			res = this.markAddress;
			break;
		case 11:
			res = this.officePhone;
			break;
		case 12:
			res = this.province;
			break;
		case 13:
			res = this.userId;
			break;
		case 14:
			res = this.zipCode;
			break;
		
		default:
			break;
		}
		return res;
	}
	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 15;
	}
	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "address";
			break;
		case 1:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name= "city";
			break;
		case 2:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "companyName";
			break;
		case 3:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "continent";
			break;
		case 4:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "country";
			break;
		case 5:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "familyPhone";
			break;
		case 6:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "firstName";
			break;
		case 7:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "lastName";
			break;
		case 8:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "latitude";
			break;
		case 9:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "longitude";
			break;
		case 10:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "markAddress";
			break;
		case 11:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "officePhone";
			break;
		case 12:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "province";
			break;
		case 13:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "userId";
			break;
		case 14:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "zipCode";
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
			this.address = arg1.toString();
			break;
		case 1:
			this.city= arg1.toString();
			break;
		case 2:
			this.companyName = arg1.toString();
			break;
		case 3:
			this.continent = arg1.toString();
			break;
		case 4:
			this.country = arg1.toString();
			break;
		case 5:
			this.familyPhone = arg1.toString();
			break;
		case 6:
			this.firstName = arg1.toString();
			break;
		case 7:
			this.lastName = arg1.toString();
			break;
		case 8:
			this.latitude = arg1.toString();
			break;
		case 9:
			this.longitude = arg1.toString();
			break;
		case 10:
			this.markAddress = arg1.toString();
			break;
		case 11:
			this.officePhone = arg1.toString();
			break;
		case 12:
			this.province = arg1.toString();
			break;
		case 13:
			this.userId = new Integer(arg1.toString());
			break;
		case 14:
			this.zipCode = arg1.toString();
			break;
		default:
			break;
		}
		
	}
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		
		if (address != null)
		{
			stringBuffer.append(address);
		}

		if (city != null)
		{
			stringBuffer.append(city);
		}
		if (companyName != null)
		{
			stringBuffer.append(companyName);
		}
		if (continent != null)
		{
			stringBuffer.append(continent);
		}
		if (country != null)
		{
			stringBuffer.append(country);
		}


		if (familyPhone != null)
		{
			stringBuffer.append(familyPhone);
		}
		if (firstName != null)
		{
			stringBuffer.append(firstName);
		}
		if (lastName != null)
		{
			stringBuffer.append(lastName);
		}
		if (latitude != null)
		{
			stringBuffer.append(latitude);
		}
		if (longitude != null)
		{
			stringBuffer.append(longitude);
		}
		if (markAddress != null)
		{
			stringBuffer.append(markAddress);
		}
		if (officePhone != null)
		{
			stringBuffer.append(officePhone);
		}
		if (province != null)
		{
			stringBuffer.append(province);
		}
		if (userId != null)
		{
			stringBuffer.append(userId);
		}
		if (zipCode != null)
		{
			stringBuffer.append(zipCode);
		}
		return stringBuffer.toString();
	}
}
