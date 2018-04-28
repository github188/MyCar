package com.cnlaunch.mycar.manager.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author xuzhuowei º””Õº«’ÀModel≤„
 */
@DatabaseTable
public class Oil implements Serializable {

	private static final long serialVersionUID = -7582623980712135028L;
	@DatabaseField(generatedId = true)
	private int daoId;

	@DatabaseField
	private String id;

	@DatabaseField
	private String userCarId;

	@DatabaseField
	private String oilType;

	@DatabaseField
	private String amount;

	@DatabaseField
	private float quantity;

	@DatabaseField
	private int mileage;

	@DatabaseField
	private String remark;

	@DatabaseField
	private java.util.Date expenseTime;

	@DatabaseField
	private int syncFlag;

	@DatabaseField
	private int lastOperate;
	
	@DatabaseField
	private String oilTypeId;
	
	@DatabaseField
	private String currentLanguage;
	
	public Oil() {
	}

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

	public int getDaoId() {
		return daoId;
	}

	public void setDaoId(int daoId) {
		this.daoId = daoId;
	}

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

	public double getAmount() {
		if(amount == null){
			return 0;
		}
		try {
			return Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getAmountStr() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public java.util.Date getExpenseTime() {
		return expenseTime;
	}

	public void setExpenseTime(java.util.Date expenseTime) {
		this.expenseTime = expenseTime;
	}

	public int getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}

	public int getLastOperate() {
		return lastOperate;
	}

	public void setLastOperate(int lastOperate) {
		this.lastOperate = lastOperate;
	}

}
