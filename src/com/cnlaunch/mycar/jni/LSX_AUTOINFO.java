package com.cnlaunch.mycar.jni;

/**
 * Ϊ����LSX��C/C++�����ṹ����һ�£�ҲΪ�˷���ʹ�ã�����Ҳ��C/C++�нṹ������һ��
 * 
 * @author jiangjun
 * 
 */

/*
 * ԭC++�ṹΪ��
 * 
 * typedef struct _LSX_AUTOINFO
 * 
 * {
 * 
 * char vin[20]; // VIN �룬��������ʹ��
 * 
 * char *make; // ��ϵ��
 * 
 * char *model; // ������
 * 
 * char *year; // �������
 * 
 * char *madein; // ������
 * 
 * char *chassis; // ����
 * 
 * char *enginemodel; // �������ͺ�
 * 
 * char *displacement; // ����
 * 
 * } LSX_AUTOINFO;
 */

public class LSX_AUTOINFO {
	public String vin = ""; // VIN �룬��������ʹ��
	public String make = ""; // ��ϵ��
	public String model = ""; // ������
	public String year = ""; // �������
	public String madein = ""; // ������
	public String chassis = ""; // ����
	public String enginemodel = ""; // �������ͺ�
	public String displacement = ""; // ����
}
