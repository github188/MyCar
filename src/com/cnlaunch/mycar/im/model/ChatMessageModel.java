package com.cnlaunch.mycar.im.model;

import java.util.Date;

public class ChatMessageModel {
	private String messageContent; // 消息内容(包含表情转义)
	private long SendTime; // 发送时间
	private String SenderUID; // 消息发送者
	private String ReceiverUID; // 消息接受者


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
