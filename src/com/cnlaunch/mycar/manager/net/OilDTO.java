package com.cnlaunch.mycar.manager.net;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author xuzhuowei 加油记录，网络接口bean
 */
public class OilDTO {
	private String id;
	private String userCarId;
	private String oilType;
	private String amount;
	private String quantity;
	private Integer mileage;
	private String remark;
	private String expenseTime;
	private Integer lastOperate;
	private String oilTypeId;
	public String getOilTypeId() {
		return oilTypeId;
	}

	public void setOilTypeId(String oilTypeId) {
		this.oilTypeId = oilTypeId;
	}

	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}

	private String currentLanguage;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserCarId() {
		return userCarId;
	}

	public void setUserCarId(String userCarId) {
		this.userCarId = userCarId;
	}

	public String getOilType() {
		return oilType;
	}

	public void setOilType(String oilType) {
		this.oilType = oilType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Integer getMileage() {
		return mileage;
	}

	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExpenseTime() {
		return expenseTime;
	}

	public void setExpenseTime(String expenseTime) {
		this.expenseTime = expenseTime;
	}

	public Integer getLastOperate() {
		return lastOperate;
	}

	public void setLastOperate(Integer lastOperate) {
		this.lastOperate = lastOperate;
	}

	@Override
	public String toString() {
		return id + userCarId + oilType + amount + quantity + mileage + remark
				+ expenseTime + lastOperate;
	}

}
