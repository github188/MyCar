package com.cnlaunch.mycar.im.common;

import java.nio.ByteBuffer;

public class TcpMessage {

	public static final int HEAD_LENGTH = 6;
	
	private byte mTypecode;
	private byte mFlag;
	private int mSize;
	private byte[] mContent;

	public byte getTypecode() {
		return mTypecode;
	}

	public void setTypecode(byte mTypecode) {
		this.mTypecode = mTypecode;
	}

	public byte getFlag() {
		return mFlag;
	}

	public void setFlag(byte mFlag) {
		this.mFlag = mFlag;
	}

	public int getSize() {
		return mSize;
	}

	public void setSize(int mSize) {
		this.mSize = mSize;
	}

	public byte[] getContent() {
		return mContent;
	}

	public void setContent(byte[] mContent) {
		this.mContent = mContent;
	}

	public TcpMessage() {

	}

	public TcpMessage(byte typecode, byte flag, byte[] content) {
		mTypecode = typecode;
		mFlag = flag;
		mSize = content.length;
		mContent = content;
	}

	public byte[] ToBytes() {
		int len = HEAD_LENGTH + mContent.length;
		ByteBuffer bb = ByteBuffer.allocate(len);
		bb.put(mTypecode);
		bb.put(mFlag);
		//将int转换成little Ending
		bb.put(BigLittleEnding.intToLittleEnding(mSize));
		if (mSize > 0) {
			bb.put(mContent);
		}
		return bb.array();
	}

	public static TcpMessage FromBytes(byte[] buffer) {
		TcpMessage message = new TcpMessage();
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		message.setTypecode(bb.get());
		message.setFlag(bb.get());
		//将big Ending转换为little Ending
		byte[] intBytes = new byte[4];
		bb.get(intBytes, 0, Integer.SIZE/8);
		message.setSize(BigLittleEnding.intFromLittleEnding(intBytes));
		
		if (message.getSize() > 0) {
			byte[] content = new byte[message.getSize()];
			bb.get(content);
			message.setContent(content);
		}
		return message;
	}
}
