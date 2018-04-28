package com.cnlaunch.mycar.im.model;

public class IMAddFriendComModel {
	private String TaskID;
	private IMMyFriendComModel SourceUserComModel;
	private IMMyFriendComModel DestUserComModel;
	private int AddFriendConfigType;// 0.δ֪ 1.�Զ����,2.���������� 3.�Զ��ܾ�
	private int ConfirmType; // 1.ͬ�����, 2.�ܾ����(����_addfriendconfigtype=2ʱ��ʹ��)
	private String ConfirmContent; // ����ı���Ϣ(����_addfriendconfigtype=2ʱ��ʹ��)
	private int Status; // 1.����������� 2.�ȴ�����������, 3.��ӳɹ�,4.���ʧ��
	
	public static final int STATUS_REQUEST = 1;
	public static final int STATUS_WAIT_FOR_RESPONSE = 2;
	public static final int STATUS_RESPONSE_SUCC = 3;
	public static final int STATUS_RESPONSE_FAIL = 4;
	
	
	public String getTaskID() {
		return TaskID;
	}
	public void setTaskID(String taskID) {
		TaskID = taskID;
	}
	public IMMyFriendComModel getSourceUserComModel() {
		return SourceUserComModel;
	}
	public void setSourceUserComModel(IMMyFriendComModel sourceUserComModel) {
		SourceUserComModel = sourceUserComModel;
	}
	public IMMyFriendComModel getDestUserComModel() {
		return DestUserComModel;
	}
	public void setDestUserComModel(IMMyFriendComModel destUserComModel) {
		DestUserComModel = destUserComModel;
	}
	public int getAddFriendConfigType() {
		return AddFriendConfigType;
	}
	public void setAddFriendConfigType(int addFriendConfigType) {
		AddFriendConfigType = addFriendConfigType;
	}
	public int getConfirmType() {
		return ConfirmType;
	}
	public void setConfirmType(int confirmType) {
		ConfirmType = confirmType;
	}
	public String getConfirmContent() {
		return ConfirmContent;
	}
	public void setConfirmContent(String confirmContent) {
		ConfirmContent = confirmContent;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}

	
	
}
