package com.cnlaunch.mycar.diagnose.constant;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

public class DiagnoseSettings {
	// ������������洢һЩ���״̬��
	private static boolean if_has_request_con = false;
	// �����õ���ǰ����������״̬,��ʼΪû������
	private static int BT_state = DiagnoseConstant.BLUETOOTH_STATE_NOT_CONNTED;
	private static BluetoothDevice device = null;

	public static BluetoothDevice getDevice() {
		return device;
	}

	public static void setDevice(BluetoothDevice device) {
		DiagnoseSettings.device = device;
	}

	public static int getBT_state() {
		return BT_state;
	}

	public static void setBT_state(int bT_state) {
		BT_state = bT_state;
	}
    //��ǰ��ʾ��ǰ�����Ͻ���
	private static Activity currentDiagnoseActivity = null;

	public static Activity getCurrentDiagnoseActivity() {
		return currentDiagnoseActivity;
	}

	public static void setCurrentDiagnoseActivity(
			Activity currentDiagnoseActivity) {
		DiagnoseSettings.currentDiagnoseActivity = currentDiagnoseActivity;
	}

	public static boolean is_has_request_con() {
		return if_has_request_con;
	}

	public static void setIf_has_request_con(boolean if_has_request_con) {
		DiagnoseSettings.if_has_request_con = if_has_request_con;
	}

}
