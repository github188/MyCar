package com.cnlaunch.mycar.jni;


/**
 * 包装一个String，用来传递给JNI函数并接受在JNI函数中的修改
 * 
 * @author jiangjun
 * 
 */

public class X431String {
	public X431String(String strValue){
		mValue = strValue;
	}
	public X431String(){
		
	}
	public String mValue;
}
