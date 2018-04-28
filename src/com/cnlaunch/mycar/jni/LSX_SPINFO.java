package com.cnlaunch.mycar.jni;

/**
 * 为了与LSX的C/C++库代码结构保持一致，也为了方便使用，名称也与C/C++中结构体名称一致
 * 
 * @author jiangjun
 * 
 */

/*
 * 原C++结构为：
 * 
 * typedef struct _LSX_SPINFO
 * 
 * {
 * 
 * char *name; // 经销商名称
 * 
 * char *phone; // 经销商电话
 * 
 * } LSX_SPINFO;
 */

public class LSX_SPINFO {

	public String name = ""; // 经销商名称
	public String phone = ""; // 经销商电话

}
