package com.cnlaunch.mycar.jni;

/**
 * Ϊ����LSX��C/C++�����ṹ����һ�£�ҲΪ�˷���ʹ�ã�����Ҳ��C/C++�нṹ������һ��
 * 
 * @author jiangjun
 * 
 *         *
 */

/*
 * 
 * 
 * ԭC++�ṹΪ��
 * 
 * typedef struct _LSX_BASEINFO
 * 
 * {
 * 
 * char serialno[20]; // ��Ʒ���к�
 * 
 * unsigned short productid; // ��Ʒ����
 * 
 * unsigned short codepage; // �������Դ���ҳ
 * 
 * char langname[30]; // �������������ַ���
 * 
 * char langcode[4]; // �������Դ��봮
 * 
 * char langcode_en[4]; // Ӣ�����Դ��봮
 * 
 * char *diagversion; // �������汾
 * 
 * char *creationtime; // �ļ�����ʱ��
 * 
 * } LSX_BASEINFO;
 */
public class LSX_BASEINFO {
	public String serialno = ""; // ��Ʒ���к�
	public short productid = 0; // ��Ʒ����
	public int codepage = 0; // �������Դ���ҳ
	public String langname = ""; // �������������ַ���
	public String langcode = ""; // �������Դ��봮
	public String langcode_en = ""; // Ӣ�����Դ��봮
	public String diagversion = ""; // �������汾
	public String creationtime = ""; // �ļ�����ʱ��

}
