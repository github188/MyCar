package com.cnlaunch.mycar.manager.database;

import java.io.Serializable;
import java.util.Date;

import com.cnlaunch.mycar.common.utils.Format;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author xuzhuowei ÕËÄ¿Model²ã
 */
@DatabaseTable
public class Account implements Serializable {

	private static final long serialVersionUID = -7582623980712135028L;
	@DatabaseField(generatedId = true)
	private int daoId;

	@DatabaseField
	private String id;

	@DatabaseField
	private String category;

	@DatabaseField
	private String amount;

	@DatabaseField
	private String remark;

	@DatabaseField
	private Date expenseTime;

	@DatabaseField
	private String year;

	@DatabaseField
	private String month;

	@DatabaseField
	private String day;

	@DatabaseField
	private String time;

	@DatabaseField
	private int syncFlag;
	
	@DatabaseField
	private String categoryId;
	
	@DatabaseField
	private String currentLanguage;

	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@DatabaseField
	private int lastOperate;
	
	public Account() {
	}

	public Account(String id) {
		this.id = id;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getExpenseTime() {
		return expenseTime;
	}

	public void setExpenseTime(Date expenseTime) {
		this.expenseTime = expenseTime;
		if (expenseTime != null) {
			this.year = Format.DateStr.getYear(expenseTime);
			this.month = Format.DateStr.getMonth(expenseTime);
			this.day = Format.DateStr.getDay(expenseTime);
			this.time = Format.DateStr.getTime(expenseTime);
		}
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getYearMonthDay() {
		return year + "-" + month + "-" + day;
	}

	public void setTime(String time) {
		this.time = time;
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

	public String getTime() {
		return time;
	}

	public String getTimeStr() {
		if (time != null && time.length() == 6) {
			return time.substring(0, 2) + ":" + time.substring(2, 4) + ":"
					+ time.substring(4, 6);
		} else {
			return time;
		}
	}

	public String getAmountStr() {
		return amount;
	}

}
