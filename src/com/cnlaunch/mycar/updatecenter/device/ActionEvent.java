package com.cnlaunch.mycar.updatecenter.device;
/**
 * �豸�����¼�
 * @author luxingsong
 */
public class ActionEvent
{
	/**
	 * ������
	 */
	public final static int ACTION_CODE_START_UPDATE = 0; // ��ʼ����
	public final static int ACTION_CODE_CONNECT_DEVICE = 1; // �����豸
	public final static int ACTION_CODE_CHECK_FILE_INTEGRETY = 2; // �ļ�������У��
	public final static int ACTION_CODE_CALC_FILE_MD5 = 3;   // md5 У��
	public final static int ACTION_CODE_DATA_TRANSFERING = 4;    // ���ݴ���
	public final static int ACTION_CODE_COMPARE_DATA_INTEGRETY = 5;// ���������ԶԱ�
	public final static int ACTION_CODE_PREPARE_FILE_INFO = 6;// ׼�������ļ���Ϣ
	public final static int ACTION_CODE_FINISH_UPDATE = 7;// �������
	public final static int ACTION_CODE_DECOMPRESS_FILES = 8;// ��ѹ�ļ�
	public final static int ACTION_CODE_SCANNING_FILES = 9;// ɨ���ļ�
	public final static int ACTION_CODE_SPILT_INI_FILE = 10;// ��ȡ INI �ļ�
	
	/**
	 *  ������
	 */
	public final static int ERROR_CONNECT_DEVICE = -10;// �����豸����
	public final static int ERROR_FILE_NOT_COMPLETE = -11; // �ļ�������
	public final static int ERROR_DEVICE_NO_REPLY = -12;  //�豸û�л�Ӧ
	public final static int ERROR_DATA_TRANSFER = -13;  // ���ݴ������
	public final static int ERROR_DATA_INTEGRETY = -14; // ����������У�����
	public final static int ERROR_DEVICE_EXCEPTION = -15; // �豸��Ӧ�쳣 23
	public final static int ERROR_REMOTE_OPERATION = -16;// Զ�̲�������
	public final static int ERROR_FIRMWARE_BIN_FILE = -17;// �̼��ļ�������
	public final static int ERROR_BOOT_VERSION_NUMBER = -18;// �̼��汾����
	public final static int ERROR_SWITCH_TO_BOOT_MODE = -19;// �л�Bootģʽ����
	public final static int ERROR_FILE_INFO_FOR_DEVICE = -20;// �ļ���Ϣ����
	public final static int ERROR_FILE_POSITION_OPERATION = -21;// �ļ�����λ�ô���
	public final static int ERROR_UPDATE_COMPLETE_INDICATION = -22;// �������ָʾ��Ӧ����
	public final static int ERROR_UPDATE_SERIALS_NOT_SEEM = -23;//���кŲ�һ��
	
}
