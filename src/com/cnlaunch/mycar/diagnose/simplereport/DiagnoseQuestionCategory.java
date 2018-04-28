package com.cnlaunch.mycar.diagnose.simplereport;


public class DiagnoseQuestionCategory {
	
	private String categoryId;   
	
    private String categoryName;      
    
    private String questionNum;
    
    private String questionInfo;

    private String categoryStatus;   
    
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

}
