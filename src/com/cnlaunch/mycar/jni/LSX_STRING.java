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
 * typedef struct _LSX_STRING
 * 
 * {
 * 
 * char *str; // 本地语言文本
 * 
 * char *str_en; // 英文文本
 * 
 * } LSX_STRING;
 */

public class LSX_STRING {

	public String str = ""; // 本地语言文本

	public String str_en = ""; // 英文文本
}
