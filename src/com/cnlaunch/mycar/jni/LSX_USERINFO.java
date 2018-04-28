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
 * typedef struct _LSX_USERINFO
 * 
 * {
 * 
 * char *name; // 用户名称
 * 
 * char *phone; // 用户电话
 * 
 * char *license; // 许可证号
 * 
 * } LSX_USERINFO;
 */
public class LSX_USERINFO {	
	public String name = ""; // 用户名称
	public String phone = ""; // 用户电话
	public String license = ""; // 许可证号

}
