package com.cnlaunch.mycar.im.model;

import java.util.ArrayList;

public class PaginationRecord {
	private int PageNumber; // ��¼ҳ��
	private ArrayList<IMSearchFriendListModel> ModelList; // ��������
	private int MaxRecordCount;// ����¼��
	private int MaxPageCount; // ���ҳ��
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
