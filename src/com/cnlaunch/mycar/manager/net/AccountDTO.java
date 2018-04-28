package com.cnlaunch.mycar.manager.net;

/**
 * @author xuzhuowei 账目Bean
 */
public class AccountDTO {

	// 账单id
	private String id;
	// 账单类型
	private String category;
	// 金额
	private String amount;
	// 备注
	private String remark;
	// 记账时间
	private String expenseTime;
	// 最后操作
	private Integer lastOperate;
	// 当前语言版本
	private String currentLanguage;
	// 类别ID
	private String categoryId; 
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

	public String getAmount() {
		return amount;
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
		return id + category + amount + remark + expenseTime + lastOperate;
	}

}
