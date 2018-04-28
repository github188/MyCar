package com.cnlaunch.mycar.im.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.util.Log;

public class Envelope implements IEnvelope {

	// / ����Ҫ���ݵ���Ϣͨ��Envelope�����ṩ�ķ���ת��Ϊbyte[]���ͣ�ʵ�ַ�����͵Ĺ���
	// / ����Ҫ���ܵ���Ϣͨ��Envelope�����ṩ�ķ�������������ʵ�ֲ���Ĺ���
	// / letter�����ż��࣬��װ��ҵ����Ҫ���ݵľ�����Ϣ
	// / ��Ϣ���Ϊ32λ��4�ֽ�
	private final int MESSAGE_CATEGORY_LENGTH = 4;
	private ILetter mLetter;// letter�����ż��࣬��װ��ҵ����Ҫ���ݵľ�����Ϣ
	private IEnvelope mNext;
	private int mCategory = -1;
	private int mSource = -1;

	public Envelope() {
	}

	public Envelope(int source, int category, ILetter letter) {
		mSource = source;// MessageSource ��Ϣ��Դ
		mCategory = category; // MessageCategory ��Ϣ����
		mLetter = letter;
	}

	public Envelope(byte[] binaryData) // ����������Ϣ����
	{
		mSource = getSource(binaryData);
		mCategory = getCategory(binaryData);
		String letterJsonStr = getData(binaryData);
		mLetter = JsonConvert.fromJson(letterJsonStr,Letter.class);
		Log.i("IM","Envelope���죬�յ����ŷ� -->category:"+ mCategory +" -> "+letterJsonStr);
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
		if (binaryData.length < MESSAGE_CATEGORY_LENGTH)// ��ȡ��Ϣ������Ϣ
		{
			throw new IllegalArgumentException("��Ϣ����̫�̣��޷�ʶ����Ϣ����");
		}
		return ((((binaryData[0])) | (binaryData[1] << 0x8)) | (binaryData[2] << 0x10));
	}

	private int getSource(byte[] binaryData)// ��ȡ��Ϣ��Դ��Ϣ
	{
		if (binaryData.length < MESSAGE_CATEGORY_LENGTH) {
			throw new IllegalArgumentException("��Ϣ����̫�̣��޷�ʶ����Ϣ��Դ");
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
