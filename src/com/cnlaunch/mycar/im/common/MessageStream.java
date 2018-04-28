package com.cnlaunch.mycar.im.common;

import java.util.Arrays;

import android.util.Log;

public class MessageStream {
	private byte[] mBuffer;
	private int mPosition;
	private int mLength;
	private int mCapacity;

	public MessageStream() {
		mBuffer = new byte[0];
		mPosition = 0;
		mLength = 0;
		mCapacity = 0;
	}

	private byte readByte() {
		if (this.mPosition >= this.mLength) {
			throw new IndexOutOfBoundsException("readByte()读取位置超出缓冲区长度");
		}
		return this.mBuffer[this.mPosition++];
	}

	private int readInt() {
		if (this.mPosition + Integer.SIZE / 8 >= this.mLength) {
			throw new IndexOutOfBoundsException("readInt()读取位置超出缓冲区长度");
		}
		//服务器传过来的是little Ending,需要转little Ending
		int i = BigLittleEnding.intFromLittleEnding(readBytes(Integer.SIZE / 8));
		Log.v("IM","readInt() -> " + i);
		return i;
	}

	private byte[] readBytes(int count) {
		int num = this.mLength - this.mPosition;
		if (num > count) {
			num = count;
		}
		if (num <= 0) {
			return null;
		}
		byte[] buffer = new byte[num];
		System.arraycopy(mBuffer, this.mPosition, buffer, 0, num);

		this.mPosition += num;
		return buffer;
	}

	public boolean read(TcpMessage message) {
		message.setContent(null);
		message.setFlag((byte)0);
		message.setSize(0);
		message.setTypecode((byte)0);
		
		mPosition = 0;
		if (mLength > TcpMessage.HEAD_LENGTH) {
			message.setTypecode(readByte());
			message.setFlag(readByte());
			message.setSize(readInt());
			if (message.getSize() <= 0
					|| message.getSize() <= mLength - mPosition) {
				if (message.getSize() > 0) {
					message.setContent(readBytes(message.getSize()));
				}
				remove(message.getSize() + TcpMessage.HEAD_LENGTH);
				return true;
			} else {
				message = null;
				return false;
			}
		} else {
			return false;
		}
	}

	private void ensureCapacity(int value) {

		if (value <= this.mCapacity - this.mLength)
			return;

		int capycityNew = value;
		if (capycityNew < 0x100)
			capycityNew = 0x100;
		if (capycityNew < (this.mCapacity * 2))
			capycityNew = this.mCapacity * 2;
		byte[] bufferNew = new byte[capycityNew];
		if (this.mLength > 0) {
			System.arraycopy(mBuffer, 0, bufferNew, 0, this.mLength);
		}
		this.mBuffer = bufferNew;
		this.mCapacity = capycityNew;
	}

	public void write(byte[] buffer, int offset, int count) {
		if (buffer.length - offset < count) {
			count = buffer.length - offset;
		}
		if(count <= 0){
			return;
		}
		
		ensureCapacity(buffer.length + count);

		Arrays.fill(mBuffer, mLength, mCapacity, (byte) 0);
		System.arraycopy(buffer, offset, mBuffer, mLength, count);
		mLength += count;
	}

	private void remove(int count) {
		if (mLength >= count) {
			System.arraycopy(mBuffer, count, mBuffer, 0, mLength - count);
			mLength -= count;
			Arrays.fill(mBuffer, mLength, mCapacity,(byte)0);
		} else {
			mLength = 0;
			Arrays.fill(mBuffer, 0, mCapacity,(byte)0);
		}
	}
}
