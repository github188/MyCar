package com.cnlaunch.dbs;

public class SearchId {

	/*
	 * PID��������ӿ�
	 * short mitPid			����Pid
	 * byte[] pDataBuffer 	����Ͻ�ͷ���յ������ݻ���������Ԥ����λ������ָ�������PID��������λ��
	 * �����					�ַ�����δ�鵽�򷵻�NULL
	 */
	public native byte[] getResultWithCalc(short mltPid, byte[] pDataBuffer);
	
	/*
	 * ������Ч����ʹ�ú���public native byte[] getTextFromLibReturnByte(int lineId, int iFileName);
	 * int lineId		�����ı�ID
	 * int iFileName	�����ļ���ID
	 * ����� 			��ȡ����һ���ı��ַ�����δ��ѯ�������NULL
	 */
	public native String getTextFromLib(int lineId, int iFileName);
	
	/*
	 * �����ı�ID���ļ�����ѯ�ַ���
	 * int lineId		�����ı�ID
	 * int iFileName	�����ļ���ID
	 * �����õ����ļ��������£�������������ϵ��лл��
	 * #define ID_TEXT_LIB_FILE_NAME                1       �˵����Ի��򣬶������԰�ť��ʹ��
	 * #define ID_DATA_STREAMBOUNDS_LIB_FILE_NAME   2
	 * #define ID_DATA_STREAM_LIB_FILE_NAME         3       ������ѡ����ʾ����������������
	 * #define ID_DATA_STREAM_HELP_LIB_FILE_NAME    4       ����������
	 * #define ID_DATA_STREAM_UNIT_LIB_FILE_NAME    5
	 * #define ID_TROUBLE_CODE_LIB_FILE_NAME        6       ������
	 * #define ID_TROUBLE_CODE_STATUS_LIB_FILE_NAME 7       ������״̬
	 * #define ID_TROUBLE_CODE_HELP_LIB_FILE_NAME   8       ���������
	 * #define ID_INFORMATION_FILE_NAME             9
	 * #define ID_SHOW_PROGRAM_HELP_FILE_NAME       10
	 * #define ID_LICENSE_FILE_NAME                 11
	 * #define ID_PICTURE_FILE_NAME                 12
	 * #define ID_BMP_FILE_PATH                     13
	 * 
	 * �����				��ȡ����һ���ı��ַ�����δ��ѯ�������NULL
	 */
	public native byte[] getTextFromLibReturnByte(int lineId, int iFileName);
	
	
	/*
	 * ��ggp�ļ���������ļ�����׺ȷ������
	 * String filename	�ļ���ȫ·��
	 * ����ֵ��int
	 */
	public native int ggpOpen(String filename);
	
	
	/*
	 * �ر�ggp�ļ�
	 * ����ֵ��	��
	 */
	public native void ggpClose();
	
}
