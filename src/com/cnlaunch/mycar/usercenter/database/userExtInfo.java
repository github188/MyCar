package com.cnlaunch.mycar.usercenter.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 *@author zhangweiwei
 *@version 2011-12-9下午4:04:44
 *类说明：请求商家简要信息时需要的一个请求参数为该类的一个对象
 */
public class userExtInfo implements KvmSerializable{  
	
	private String address; // 0
    /**
     * 姓
     */
    private String birthdays; // 1
    /**
     * 名
     */
    private String city;// 2
    /**
     * 公司名称
     */
    private String companyName;// 3
    /**
     * 用户类型
     */
    private String continent;// 4
    /**
     * 性别 0:男1:女
     */
    private String country; // 5 
    /**
     * 昵称
     */
    private String email;// 6
    /**
     * 日期格式为yyyy-mm-dd
     */
    private String familyPhone;// 7
    /**
     * 州代号
     */
    private String firstName;// 8
    /**
     * 国家代号
     */
    private String lastName;// 9
    /**
     * 省份代号
     */
    private String latitude;// 10
    /**
     * 地市代号
     */
    private String longitude; // 11
    /**
     * 办公电话
     */
    private String markAddress; // 12
    /**
     * 家庭电话
     */
    private String mobile;// 13
    /**
     * 详细地址
     */
    private String nickName;// 14
    /**
     * 邮编
     */
    private String officePhone;// 15
    /**
     * 经度
     */
    private String province;// 16
    /**
     * 纬度
     */
    private Integer sex;// 17
    /**
     * 定位地址
     */
    private Integer userId;// 18
    /**
     * 手机
     */
    private Integer userTypeId; // 19
    /**
     * 邮箱
     */
    private String zipCode; // 20

	public userExtInfo()
	{
		
	}
	public userExtInfo(Integer userId, String lastName, String firstName,String mobile, String email ,String address)
	{
		super();
		this.userId = userId;
		this.lastName = lastName;
		this.firstName = firstName;

		this.mobile = mobile;
		this.email = email;
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
	public Integer getUserTypeId() {
		return userTypeId;
	}
	public void setUserTypeId(Integer userTypeId) {
		this.userTypeId = userTypeId;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getBirthdays() {
		return birthdays;
	}
	public void setBirthdays(String birthdays) {
		this.birthdays = birthdays;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
			res = this.birthdays;
			break;
		case 2:
			res = this.city;
			break;
		case 3:
			res = this.companyName;
			break;
		case 4:
			res = this.continent;
			break;
		case 5:
			res = this.country;
			break;
		case 6:
			res = this.email;
			break;
		case 7:
			res = this.familyPhone;
			break;
		case 8:
			res = this.firstName;
			break;
		case 9:
			res = this.lastName;
			break;
		case 10:
			res = this.latitude;
			break;
		case 11:
			res = this.longitude;
			break;
		case 12:
			res = this.markAddress;
			break;
		case 13:
			res = this.mobile;
			break;
		case 14:
			res = this.nickName;
			break;
		case 15:
			res = this.officePhone;
			break;
		case 16:
			res = this.province;
			break;
		case 17:
			res = this.sex;
			break;
		case 18:
			res = this.userId;
			break;
		case 19:
			res = this.userTypeId;
			break;
		case 20:
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
		return 21;
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
			arg2.name = "birthdays";
			break;
		case 2:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name= "city";
			break;
		case 3:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "companyName";
			break;
		case 4:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "continent";
			break;
		case 5:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "country";
			break;
		case 6:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "email";
			break;
		case 7:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "familyPhone";
			break;
		case 8:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "firstName";
			break;
		case 9:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "lastName";
			break;
		case 10:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "latitude";
			break;
		case 11:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "longitude";
			break;
		case 12:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "markAddress";
			break;
		case 13:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "mobile";
			break;
		case 14:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "nickName";
			break;
		case 15:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "officePhone";
			break;
		case 16:
			arg2.type = PropertyInfo.STRING_CLASS;
			arg2.name = "province";
			break;
		case 17:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "sex";
			break;
		case 18:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "userId";
			break;
		case 19:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "userTypeId";
			break;
		case 20:
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
			this.birthdays = null;
			break;
		case 2:
			this.city= arg1.toString();
			break;
		case 3:
			this.companyName = arg1.toString();
			break;
		case 4:
			this.continent = arg1.toString();
			break;
		case 5:
			this.country = arg1.toString();
			break;
		case 6:
			this.email = arg1.toString();
			break;
		case 7:
			this.familyPhone = arg1.toString();
			break;
		case 8:
			this.firstName = arg1.toString();
			break;
		case 9:
			this.lastName = arg1.toString();
			break;
		case 10:
			this.latitude = arg1.toString();
			break;
		case 11:
			this.longitude = arg1.toString();
			break;
		case 12:
			this.markAddress = arg1.toString();
			break;
		case 13:
			this.mobile = arg1.toString();
			break;
		case 14:
			this.nickName = arg1.toString();
			break;
		case 15:
			this.officePhone = arg1.toString();
			break;
		case 16:
			this.province = arg1.toString();
			break;
		case 17:
			this.sex = new Integer(arg1.toString());
			break;
		case 18:
			this.userId = new Integer(arg1.toString());
			break;
		case 19:
			this.userTypeId = new Integer(arg1.toString());
			break;
		case 20:
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
		if (birthdays != null)
		{
			stringBuffer.append(birthdays);
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

		if (email != null)
		{
			stringBuffer.append(email);
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
		if (mobile != null)
		{
			stringBuffer.append(mobile);
		}
		
		
		if (nickName != null)
		{
			stringBuffer.append(nickName);
		}
		if (officePhone != null)
		{
			stringBuffer.append(officePhone);
		}
		if (province != null)
		{
			stringBuffer.append(province);
		}
		if (sex != null)
		{
			stringBuffer.append(sex);
		}
		if (userId != null)
		{
			stringBuffer.append(userId);
		}
		if (userTypeId != null)
		{
			stringBuffer.append(userTypeId);
		}
		if (zipCode != null)
		{
			stringBuffer.append(zipCode);
		}
		return stringBuffer.toString();
	}
}
