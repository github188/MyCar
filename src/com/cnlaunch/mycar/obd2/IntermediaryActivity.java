package com.cnlaunch.mycar.obd2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueCallback;
import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueStateEvent;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.obd2.util.Command;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

/**
 * <功能简述>该Activity只是负责接收蓝牙消息，搜索系统模式，设置系统模式扫描系统，跳转到仪表界面，中介！ <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
@Deprecated
public class IntermediaryActivity extends Activity implements
		BluetoothInterface, BlueCallback {
	private static final String TAG = "IntermediaryActivity";
	private static final boolean D = false;
	private Context context;
	private ProgressDialog progressDialog;
	ConditionVariable next = new ConditionVariable(false);
	Command command;
	BluetoothDataService m_blue_data_service;

	boolean isOpenedBluetoothConnecteDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diagnose_formal_main);
			
		// 初始化蓝牙服务
		m_blue_data_service = BluetoothDataService.getInstance();
		command = new Command(m_blue_data_service);
		context = IntermediaryActivity.this;
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(this.getString(R.string.intermediary_activity_init));
		progressDialog.show();
		
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
				
			}
		});

	}
	@Override
	protected void onDestroy() {
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_blue_data_service.AddObserver(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		m_blue_data_service.DelObserver(this);
	}

	@Override
	protected void onResume() {
		if (isOpenedBluetoothConnecteDialog) {
			if (m_blue_data_service.IsConnected()) {
				command.setOrGetMode(new byte[] { 0x00 });
			} else {
				progressDialog.dismiss();
				this.finish();
			}

		} else {
			if (m_blue_data_service.IsConnected()) {
				command.setOrGetMode(new byte[] { 0x00 });
			} else {
				isOpenedBluetoothConnecteDialog = true;
				m_blue_data_service.ShowBluetoothConnectActivity(this);// 弹出蓝牙连接对话框
			}
		}
		super.onResume();
	}

	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		byte[] dpuPackage = OrderUtils.filterReturnDataPackage(databuf);
		byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
		// 带长度字节
		byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);
		// 不带长度字节
		byte[] paramNOLength = OrderUtils.filterOBD2CmdParameters(dpuPackage);
		String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);

		if (D) {
			String dpuPackageSTR = "dpuPackage收到";
			String cmd_subcmdSTR = "cmd_subcmd收到";
			String paramSTR = "param收到";
			for (int i = 0; i < dpuPackage.length; i++) {
				dpuPackageSTR += (i == 0 ? ":" : " ")
						+ Integer.toHexString(dpuPackage[i]);
			}
			Log.i("get", dpuPackageSTR);

			for (int i = 0; i < cmd_subcmd.length; i++) {
				cmd_subcmdSTR += (i == 0 ? ":" : " ")
						+ Integer.toHexString(cmd_subcmd[i]);
			}
			Log.i("get", cmd_subcmdSTR);

			for (int i = 0; i < param.length; i++) {
				paramSTR += (i == 0 ? ":" : " ")
						+ Integer.toHexString(param[i]);
			}
			Log.i("get", paramSTR);
		}

		// if (!cmd_subcmdString.equals("6109") &&
		// !cmd_subcmdString.equals("6900"))
		// {
		// command.setOrGetMode(new byte[] { 0x00 });
		// next.set(true);
		// }

		if (cmd_subcmdString.equals("6109")) {
			if (param.length == 2 && param[0] != 0x05 && param[1] == 0x00) {
				command.setOrGetMode(new byte[] { 0x05 });
				next.set(true);
			} else {
				if (param.length == 2 && param[0] == 0x05 && param[1] == 0x00) {
					command.scanSystem();// 扫描进入系统
					next.set(true);
				}
			}
		} else if (cmd_subcmdString.equals("6900")) {
			if (param.length == 1 && param[0] == 0x00) {
				command.scanSystem();// 扫描进入系统
				next.set(true);
			} else {
				command.getPIDQuantity();// 读取支持PID
				next.set(true);
				// finish();
				// progressDialog.dismiss();
				// startActivity(new Intent(IntermediaryActivity.this,
				// DataFlowMain.class));

			}
		} else if (cmd_subcmdString.equals("6901")) {
			com.cnlaunch.mycar.obd2.util.DataExchange
					.setPidsMeter(paramNOLength);// 保存支持PID
			progressDialog.dismiss();
			startActivity(new Intent(IntermediaryActivity.this,
					DataFlowMain.class));
			// startActivity(new Intent(IntermediaryActivity.this,
			// TestActivity.class));
			finish();
		} else if (cmd_subcmdString.equals("69EE")) {
			new AlertDialog.Builder(context)
					.setTitle(this.getString(R.string.intermediary_activity_tip))
					.setMessage(this.getString(R.string.intermediary_activity_tipmessage))
					.setPositiveButton(R.string.intermediary_activity_tip_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									progressDialog.dismiss();
									startActivity(new Intent(
											IntermediaryActivity.this,
											MyCarActivity.class));
								}
							}).show();
		}
	}

	@Override
	public void GetDataTimeout() {
	}

	@Override
	public void BlueConnectLost(String name, String mac) {
	}

	@Override
	public void BlueConnected(String name, String mac) {
		// 发送命令得到模式
		command.setOrGetMode(new byte[] { 0x00 });
	}

	@Override
	public void GetDataFromBlueSocket(byte[] buf, int len) {// 
	}

	@Override
	public void GetBluetoothState(BlueStateEvent state) {
		// 
	}
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// finish();
	// return true;
	// }
	// return false;
	// }
}
