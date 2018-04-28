package com.cnlaunch.mycar.im.model;

public class IMDelFriendComModel {
	private String TaskID;
	private String SourceUserUID;
	private String DestUserUID;
	private int Status; // 1.É¾³ý³É¹¦ 2.É¾³ýÊ§°Ü
	
	public static final int STATUS_RESPONSE_DEL_SUCC = 1;
	public static final int STATUS_RESPONSE_DEL_FAIL = 2;

	
	
	public String getTaskID() {
		return TaskID;
	}
	public void setTaskID(String taskID) {
		TaskID = taskID;
	}
	public String getSourceUserUID() {
		return SourceUserUID;
	}
	public void setSourceUserUID(String sourceUserUID) {
		SourceUserUID = sourceUserUID;
	}
	public String getDestUserUID() {
		return DestUserUID;
	}
	public void setDestUserUID(String destUserUID) {
		DestUserUID = destUserUID;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	
	
}
