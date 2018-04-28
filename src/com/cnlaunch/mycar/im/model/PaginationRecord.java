package com.cnlaunch.mycar.im.model;

import java.util.ArrayList;

public class PaginationRecord {
	private int PageNumber; // 记录页码
	private ArrayList<IMSearchFriendListModel> ModelList; // 数据类型
	private int MaxRecordCount;// 最大记录数
	private int MaxPageCount; // 最大页码
	public int getPageNumber() {
		return PageNumber;
	}
	public void setPageNumber(int pageNumber) {
		PageNumber = pageNumber;
	}
	public ArrayList<IMSearchFriendListModel> getModelList() {
		return ModelList;
	}
	public void setModelList(ArrayList<IMSearchFriendListModel> modelList) {
		ModelList = modelList;
	}
	public int getMaxRecordCount() {
		return MaxRecordCount;
	}
	public void setMaxRecordCount(int maxRecordCount) {
		MaxRecordCount = maxRecordCount;
	}
	public int getMaxPageCount() {
		return MaxPageCount;
	}
	public void setMaxPageCount(int maxPageCount) {
		MaxPageCount = maxPageCount;
	}
	
	
}
