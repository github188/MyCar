package com.cnlaunch.mycar.im.model;

import java.util.Date;

public class ChatMessageModel {
	private String messageContent; // ��Ϣ����(��������ת��)
	private long SendTime; // ����ʱ��
	private String SenderUID; // ��Ϣ������
	private String ReceiverUID; // ��Ϣ������


	public ChatMessageModel() {
	}

	public ChatMessageModel(String senderUID, String receiverUID,
			String messageContent, long sendTime) {
		this.messageContent = messageContent;
		this.SenderUID = senderUID;
		this.ReceiverUID = receiverUID;
		this.SendTime = sendTime;
	}

	public ChatMessageModel(String senderUID, String receiverUID, String messageContent) {
		this(senderUID, receiverUID, messageContent, new Date().getTime());
	}

	public String getContent() {
		return messageContent;
	}

	public void setContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public long getSendTime() {
		return SendTime;
	}

	public void setSendTime(long sendTime) {
		SendTime = sendTime;
	}

	public String getSenderUID() {
		return SenderUID;
	}

	public void setSenderUID(String senderUID) {
		SenderUID = senderUID;
	}

	public String getReceiverUID() {
		return ReceiverUID;
	}

	public void setReceiverUID(String receiverUID) {
		ReceiverUID = receiverUID;
	}
}
