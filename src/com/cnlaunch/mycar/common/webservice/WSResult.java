package com.cnlaunch.mycar.common.webservice;

/**
 * Webservice�������Ӧ����ĳ���
 * ˵��: 
 * 1> code ������
 * 2> message ��Ϣ
 * 3> 0����ɹ�
 * 4> 400��ʾ�ͻ����������������
 * 5> 500 ��ʾ�������쳣(�����ݿ��쳣)
 * 6> �����������ɸ���ģ����չ(����ú����֪ͨ�����Ա)
 * 7> �����з���ֵ�Ľӿڣ����̳���WSResult
 * 8> ������WsResult���Ͷ�������http://www.x431.com namespace�ռ���
 * 9> ���е�complexType�������� http://www.x431.com namespace�ռ���
 * 10> Ĭ�����ڸ�ʽ��ʾ�ַ�����:2011-11-21T21:10:06+08:00 �����ṩʱ���ʽ: 
 * @author xiangyuanmao
 *
 */
public class WSResult {
 public int code; // ������
 public String message; // ������Ϣ
}
