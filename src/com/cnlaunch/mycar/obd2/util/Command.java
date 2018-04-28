package com.cnlaunch.mycar.obd2.util;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
/**
 * <功能简述>记录OBD2所有要发送的命令 <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class Command {
	private BluetoothDataService bluetoothDataService = null;

	public Command(BluetoothDataService bluetoothDataService) {
		this.bluetoothDataService = bluetoothDataService;
	}

	/**
	 * 发送设置(得到)模式命令
	 * 
	 * @param modeCommand
	 *            GetMode=0 SMARTBOX=1 MYCAR=2 CREADER=3 CRECORDER=4 OBD=5
	 * @since DBS V100
	 */
	public void setOrGetMode(byte[] modeCommand) {
		byte[] v_cmd = new byte[]{0x21, 0x09};
		byte[] v_sendbuf = modeCommand;
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 1500);
	}

	// 发送扫描进入系统命令
	public void scanSystem() {
		byte[] v_cmd = new byte[]{0x29, 0x00};
		byte[] v_sendbuf = new byte[]{};
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 1500);
	}

	// 发送得到VIN码的命令
	public void getVin() {
		byte[] v_cmd = new byte[]{0x29, 0x09};
		byte[] v_sendbuf = new byte[]{0x02};
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 3000);
	}

	// 发送得到故障码的命令
	public void getFaultCode() {
		byte[] v_cmd = new byte[]{0x29, 0x03};
		byte[] v_sendbuf = new byte[]{};
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 3000);
	}

	// 发送得到数据流支持pid（all）的命令
	public void getPIDQuantity() {
		byte[] v_cmd = new byte[]{0x29, 0x01};
		byte[] v_sendbuf = new byte[]{};
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 3000);
	}

	// 发送得到计算数据流值和名称的命令
	public void getDataStreamData(byte[] pidNumAndPidvalue) {
		byte[] v_cmd = new byte[]{0x29, 0x11};
		byte[] v_sendbuf = pidNumAndPidvalue;
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 3000);
	}

	// 发送清除清除故障码的命令
	public void clearFaultCode() {
		byte[] v_cmd = new byte[]{0x29, 0x04};
		byte[] v_sendbuf = new byte[]{};
		if (bluetoothDataService != null)
			bluetoothDataService.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 3000);
	}
}
