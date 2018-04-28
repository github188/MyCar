package com.cnlaunch.mycar.im.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.util.Log;

public class Envelope implements IEnvelope {

	// / 将需要传递的信息通过Envelope类中提供的方法转换为byte[]类型，实现封包发送的功能
	// / 将需要接受的信息通过Envelope类中提供的方法解析出来，实现拆包的功能
	// / letter代表信件类，封装了业务需要传递的具体信息
	// / 消息类别为32位，4字节
	private final int MESSAGE_CATEGORY_LENGTH = 4;
	private ILetter mLetter;// letter代表信件类，封装了业务需要传递的具体信息
	private IEnvelope mNext;
	private int mCategory = -1;
	private int mSource = -1;

	public Envelope() {
	}

	public Envelope(int source, int category, ILetter letter) {
		mSource = source;// MessageSource 消息来源
		mCategory = category; // MessageCategory 消息种类
		mLetter = letter;
	}

	public Envelope(byte[] binaryData) // 将二进制信息解析
	{
		mSource = getSource(binaryData);
		mCategory = getCategory(binaryData);
		String letterJsonStr = getData(binaryData);
		mLetter = JsonConvert.fromJson(letterJsonStr,Letter.class);
		Log.i("IM","Envelope构造，收到新信封 -->category:"+ mCategory +" -> "+letterJsonStr);
	}

	private String getData(byte[] binaryData) {
		try {
			return new String(binaryData, MESSAGE_CATEGORY_LENGTH,
					binaryData.length - MESSAGE_CATEGORY_LENGTH, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private int getCategory(byte[] binaryData) {
		if (binaryData.length < MESSAGE_CATEGORY_LENGTH)// 读取消息类型信息
		{
			throw new IllegalArgumentException("消息数据太短，无法识别消息类型");
		}
		return ((((binaryData[0])) | (binaryData[1] << 0x8)) | (binaryData[2] << 0x10));
	}

	private int getSource(byte[] binaryData)// 读取消息来源信息
	{
		if (binaryData.length < MESSAGE_CATEGORY_LENGTH) {
			throw new IllegalArgumentException("消息数据太短，无法识别消息来源");
		}
		return binaryData[3];
	}

	@Override
	public ILetter getLetter() {
		return mLetter;
	}

	@Override
	public IEnvelope getNext() {
		return mNext;
	}

	@Override
	public int getCategory() {
		return mCategory;
	}

	@Override
	public int getSource() {
		return mSource;
	}

	@Override
	public void setLetter(ILetter letter) {
		mLetter = letter;

	}

	@Override
	public void setNext(IEnvelope envelope) {
		mNext = envelope;

	}

	@Override
	public void setCategory(int category) {
		mCategory = category;

	}

	@Override
	public void setSource(int source) {
		mSource = source;

	}

	@Override
	public byte[] toBinary() {
		byte[] data;
		try {
			data = JsonConvert.toJson(mLetter).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		ByteBuffer bb = null;
		if (data == null) {
			bb = ByteBuffer.allocate(MESSAGE_CATEGORY_LENGTH);
			bb.put(BigLittleEnding.intToLittleEnding((mSource << 0x18) + mCategory));
		} else {
			bb = ByteBuffer.allocate(data.length
					+ MESSAGE_CATEGORY_LENGTH);
			bb.put(BigLittleEnding.intToLittleEnding((mSource << 0x18) + mCategory));
			bb.put(data);
		}
		return bb.array();
	}

}
