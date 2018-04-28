package com.cnlaunch.mycar.usercenter.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @description 
 * @author 向远茂
 * @date：2012-5-1
 */
@DatabaseTable
public class ExUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6476976046069435613L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String firstName; // 名字
	@DatabaseField
	private String lastName; // 姓氏
	@DatabaseField
	private String latitude; // 纬度
	@DatabaseField
	private String longitude; // 经度
	@DatabaseField
	private String markAddress; // 定位地址
	@DatabaseField
	private String companyName; // 公司名称
	@DatabaseField
	private String familyPhone; // 家庭电话
	@DatabaseField
	private String officePhone; // 办公电话
	@DatabaseField
	private String address; // 地址
	@DatabaseField
	private String city; // 城市
	@DatabaseField
	private String continent; // 洲
	@DatabaseField
	private String country; // 国家
	@DatabaseField
	private String province; // 省
	@DatabaseField
	private String userId; // CC
	@DatabaseField
	private String zipCode; // 邮编
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getMarkAddress() {
		return markAddress;
	}
	public void setMarkAddress(String markAddress) {
		this.markAddress = markAddress;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFamilyPhone() {
		return familyPhone;
	}
	public void setFamilyPhone(String familyPhone) {
		this.familyPhone = familyPhone;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public ExUser() {
		super();
	}
	
	public String getPropertyByIndex(int index)
	{
		switch(index)
		{
		case 0:
			return getFirstName();
		case 1:
			return getLastName();
		case 2:
			return getLatitude();
		case 3:
			return getLongitude();
		case 4:
			return getMarkAddress();
		case 5:
			return getCompanyName();
		case 6:
			return getFamilyPhone();
		case 7:
			return getOfficePhone();
		case 8:
			return getAddress();
		case 9:
			return getCity();
		case 10:
			return getContinent();
		case 11:
			return getCountry();
		case 12:
			return getProvince();
		case 13:
			return getUserId();
		case 14:
			return getZipCode();
		}
		return null;
	}
	public void setPropertyByIndex(int index, String value)
	{
		switch(index)
		{
		case 0:
			setFirstName(value);
			break;
		case 1:
			setLastName(value);
			break;
		case 2:
			setLatitude(value);
			break;
		case 3:
			setLongitude(value);
			break;
		case 4:
			setMarkAddress(value);
			break;
		case 5:
			setCompanyName(value);
			break;
		case 6:
			setFamilyPhone(value);
			break;
		case 7:
			setOfficePhone(value);
			break;
		case 8:
			setAddress(value);
			break;
		case 9:
			setCity(value);
			break;
		case 10:
			setContinent(value);
			break;
		case 11:
			setCountry(value);
			break;
		case 12:
			setProvince(value);
			break;
		case 13:
			setUserId(value);
			break;
		case 14:
			setZipCode(value);
			break;
		}
	}
	public int getCount()
	{
		return 15;
	}
	
	public WSExUserInfo getUserExtInfo()
	{
		WSExUserInfo eui = new WSExUserInfo();
		eui.setAddress(this.getAddress());
		eui.setCity(this.getCity()) ;           
		eui.setCompanyName(this.getCompanyName());
		eui.setContinent(this.getContinent())    ;
		eui.setCountry(this.getCountry()) ;
		eui.setFamilyPhone(this.getFamilyPhone());
		eui.setFirstName(this.getFirstName())    ;
		eui.setLastName(this.getLastName())      ;
		eui.setLatitude(this.getLatitude())    ;  
		eui.setLongitude(this.getLongitude())    ;
		eui.setMarkAddress(this.getMarkAddress());
		eui.setOfficePhone(this.getOfficePhone());
		eui.setProvince(this.getProvince())     ;
		eui.setUserId(new Integer(this.getUserId()))   ;     
		eui.setZipCode(this.getZipCode())  ;    
		return eui;
	}
}



