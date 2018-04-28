package com.cnlaunch.mycar.im.common;

public class Letter implements ILetter {
	private String Content;
	private String Receiver;
	private String Sender;

	public Letter() {

	}

	public Letter(String receiver, String sender, String content) {
		this.Content = content;
		this.Receiver = receiver;
		this.Sender = sender;
	}

	@Override
	public String getReceiver() {
		return Receiver;
	}

	@Override
	public String getSender() {
		return Sender;
	}

	@Override
	public String getContent() {
		return Content;
	}

	@Override
	public void setReceiver(String receiver) {
		Receiver = receiver;

	}

	@Override
	public void setSender(String sender) {
		Sender = sender;

	}

	@Override
	public void setContent(String content) {
		Content = content;

	}

}
