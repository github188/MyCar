package com.cnlaunch.mycar.jni;

/**
 * 包装一个整形数据，用来传递给JNI函数并接受在JNI函数中的修改
 * 
 * @author jiangjun
 * 
 */
public class X431Integer {
	public X431Integer(int iValue) {
		mValue = iValue;
	}
	
	public X431Integer(){
		
	}

	public int mValue;
}
