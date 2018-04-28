package com.cnlaunch.mycar.manager.database;

import java.io.Serializable;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author xuzhuowei ÕËÄ¿Àà±ðModel²ã
 */
@DatabaseTable
public class Category implements Serializable {

	private static final long serialVersionUID = -7582623980712135028L;
	static public final int TYPE_SYS = 1;
	static public final int TYPE_SYS_CUSTOM = 0;
	static public final int TYPE_USER_CUSTOM = 2;

	@DatabaseField(generatedId = true)
	private int daoId;

	@DatabaseField
	private String category;

	@DatabaseField
	private int orderId;

	@DatabaseField
	private String type;
	
	@DatabaseField
	private String categoryId;
	
	@DatabaseField
	private String currentLanguage;
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}

	public Category() {
	}

	public Category(String category, int orderId, String type) {
		this.category = category;
		this.orderId = orderId;
		this.type = type;
	}

	public Category(String category, String type,String orderId,String currentLanguage,String categoryId ) {
		this.category = category;
		try {
			this.orderId = Integer.parseInt(orderId);
		} catch (NumberFormatException e) {
			this.orderId = 0;
			Log.e("Category","NumberFormatException");
		}
		this.type = type;
		this.currentLanguage = currentLanguage;
		this.categoryId = categoryId;
	}

	public int getDaoId() {
		return daoId;
	}

	public void setDaoId(int daoId) {
		this.daoId = daoId;
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

}
