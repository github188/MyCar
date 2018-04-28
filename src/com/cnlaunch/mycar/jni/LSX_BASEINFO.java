package com.cnlaunch.mycar.jni;

/**
 * 为了与LSX的C/C++库代码结构保持一致，也为了方便使用，名称也与C/C++中结构体名称一致
 * 
 * @author jiangjun
 * 
 *         *
 */

/*
 * 
 * 
 * 原C++结构为：
 * 
 * typedef struct _LSX_BASEINFO
 * 
 * {
 * 
 * char serialno[20]; // 产品序列号
 * 
 * unsigned short productid; // 产品代码
 * 
 * unsigned short codepage; // 本地语言代码页
 * 
 * char langname[30]; // 本地语言名称字符串
 * 
 * char langcode[4]; // 本地语言代码串
 * 
 * char langcode_en[4]; // 英文语言代码串
 * 
 * char *diagversion; // 诊断软件版本
 * 
 * char *creationtime; // 文件创建时间
 * 
 * } LSX_BASEINFO;
 */
public class LSX_BASEINFO {
	public String serialno = ""; // 产品序列号
	public short productid = 0; // 产品代码
	public int codepage = 0; // 本地语言代码页
	public String langname = ""; // 本地语言名称字符串
	public String langcode = ""; // 本地语言代码串
	public String langcode_en = ""; // 英文语言代码串
	public String diagversion = ""; // 诊断软件版本
	public String creationtime = ""; // 文件创建时间

}
