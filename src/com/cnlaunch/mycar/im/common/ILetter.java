package com.cnlaunch.mycar.im.common;

public interface ILetter {
	String getReceiver();
	String getSender();
	String getContent();

	void setSender(String sender);
	void setContent(String content);
	void setReceiver(String receiver);

}
