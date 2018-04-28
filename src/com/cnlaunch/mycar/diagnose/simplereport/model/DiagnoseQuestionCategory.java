package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;


public class DiagnoseQuestionCategory implements Serializable{
	
	/**
	 * ���кŰ汾��ʶ
	 */
	private static final long serialVersionUID = 1805023402567642328L;
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private String categoryId;   //���
	@DatabaseField
    private String categoryName; //�������
	@DatabaseField
    private String questionNum; //���ϱ��
	@DatabaseField
    private String questionInfo;//������Ϣ
	@DatabaseField
    private String categoryStatus;   //����״̬
	@DatabaseField
    private String categoryParentId; //�����ID
	@DatabaseField
	private String categoryParentStr;//�ı���Ϣ
	@DatabaseField
	private String categoryParentTextID;//�ı���Ϣ
    public String getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(String categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

	public String getCategoryParentTextID() {
		return categoryParentTextID;
	}

	public void setCategoryParentTextID(String categoryParentTextID) {
		this.categoryParentTextID = categoryParentTextID;
	}

	public String getCategoryParentStr() {
		return categoryParentStr;
	}

	public void setCategoryParentStr(String categoryParentStr) {
		this.categoryParentStr = categoryParentStr;
	}


    
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryStatus() {
		return categoryStatus;
	}

	public void setCategoryStatus(String categoryStatus) {
		this.categoryStatus = categoryStatus;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(String questionNum) {
		this.questionNum = questionNum;
	}

	public String getQuestionInfo() {
		return questionInfo;
	}

	public void setQuestionInfo(String questionInfo) {
		this.questionInfo = questionInfo;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


}
