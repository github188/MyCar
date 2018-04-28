package com.cnlaunch.mycar.manager.net;

/**
 * @author xuzhuowei 自定义类别,网络接口bean
 */
public class CategoryDTO {
	private String category;
	private int orderId;
	private String type;
	private String currentLanguage;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return category + orderId + type;
	}

}
