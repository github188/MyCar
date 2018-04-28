package com.cnlaunch.mycar.manager.net;

/**
 * @author xuzhuowei ��ĿBean
 */
public class AccountDTO {

	// �˵�id
	private String id;
	// �˵�����
	private String category;
	// ���
	private String amount;
	// ��ע
	private String remark;
	// ����ʱ��
	private String expenseTime;
	// ������
	private Integer lastOperate;
	// ��ǰ���԰汾
	private String currentLanguage;
	// ���ID
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
