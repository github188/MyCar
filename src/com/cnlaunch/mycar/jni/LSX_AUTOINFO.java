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
 * typedef struct _LSX_AUTOINFO
 * 
 * {
 * 
 * char vin[20]; // VIN 码，仅保留不使用
 * 
 * char *make; // 车系名
 * 
 * char *model; // 车型名
 * 
 * char *year; // 出厂年份
 * 
 * char *madein; // 出厂地
 * 
 * char *chassis; // 底盘
 * 
 * char *enginemodel; // 发动机型号
 * 
 * char *displacement; // 排量
 * 
 * } LSX_AUTOINFO;
 */

public class LSX_AUTOINFO {
	public String vin = ""; // VIN 码，仅保留不使用
	public String make = ""; // 车系名
	public String model = ""; // 车型名
	public String year = ""; // 出厂年份
	public String madein = ""; // 出厂地
	public String chassis = ""; // 底盘
	public String enginemodel = ""; // 发动机型号
	public String displacement = ""; // 排量
}
