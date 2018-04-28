package com.cnlaunch.mycar.diagnose.loveCarHealth;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * �������Service��
 * 
 * @author huangweiyong
 * LoveCarService
 */
public class LoveCarService extends Service {
	// debuging
	private static final boolean D = true;
	// ������ַ
	public static String address = null;
	// ������������
	private BluetoothAdapter bluetoothAdapter;
	// Զ�������豸
	private BluetoothDevice device;
	private Handler handler = new Handler();
	//private BluetoothOperate operate;
	@SuppressWarnings("rawtypes")
	private static List items;
	
	/////////////////////////////////////////////////////////////////////////////////////////
	private BluetoothAdapter mBluetoothAdapter = null;
	//private BluetoothDiagnoseService diagnoseService = null;
	
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		if (D) {
			Log.d("MyService", "onCreate------------------------------------");
		}
		super.onCreate();
//		operate = BluetoothOperate.getInstance();
//		operate.DiscoverBluetooth();
//		operate.OpenBluetoothService();
		///////////////////////////////////////////////////////////////////
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		setupChat();
		//////////////////////////////////////////////////////////////////
	}
	
	private void setupChat() {

		// Initialize the BluetoothChatService to perform bluetooth connections
		//diagnoseService = BluetoothDiagnoseService.getInstance();
	}

	@Override
	public void onDestroy() {
		if (D) {
			Log.d("MyService", "onDestroy-----------------------------------");
		}
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (D) {
			Log.d("MyService", "onStart------------------------------------->");
		}
		super.onStart(intent, startId);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoveCarService.this);
		String bluetoothName = sharedPreferences.getString("edittext_key", "LAUNCH");
		try{
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter != null) {
			System.out.println("�����������豸");
			Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
			if (set.size() > 0) {
				for (Iterator<BluetoothDevice> iterator = set.iterator(); iterator
						.hasNext();) {
					device = iterator.next();
					// LAUNCH-AB83
					if (bluetoothName.equals(device.getName())) {
						address = device.getAddress();
					}
				}
			}
		} else {
			Toast.makeText(LoveCarService.this, "��ʹ�ð������֮ǰ��������ֻ������豸��", Toast.LENGTH_SHORT);
			System.out.println("����û�������豸");
		}
		//System.out.println(address);

		device = mBluetoothAdapter.getRemoteDevice(address);
		//diagnoseService.connect(device);
		//02-14 10:07:32.489: E/AndroidRuntime(1075): Caused by: java.lang.IllegalArgumentException: null is not a valid Bluetooth address

}catch (IllegalArgumentException e) {
	Toast.makeText(LoveCarService.this, "���������Ƿ��!", Toast.LENGTH_SHORT);
}
		
//		operate.ConnBluetoothService(address);
//		new GetCodeNum().start();
	}

//	public List<Map<String, Object>> get_listview_data() {
//		return new InitDataService(getApplicationContext()).getCodes();
//	}

	/**
	 * ��ȡ�������߳�
	 * @author huangweiyong
	 *
	 */
	/*
	class GetCodeNum extends Thread {
		@Override
		public void run() {
			super.run();
			for (int i = 0; i < 10; i++) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(BluetoothPairing.BluetoothStatus);
				if (BluetoothPairing.BluetoothStatus == 3) {
					if (D) {
						handler.post(new Runnable() {
							public void run() {
								ShowToast(getResources().getText(
										R.string.LINK_OK).toString());
							}
						});
					}
					items = new InitDataService(getApplicationContext())
							.getCodes();
					if (items.size() > 0) {
						Log.i("getCodes", "�����й����룬��С�ļ�ʻ");
						showNotification("����������", "��ܰ��ʾ", "�����й����룬��С�ļ�ʻ",
								R.drawable.smile, R.drawable.smile);
						MediaPlayer mediaPlayer = MediaPlayer.create(
								getApplicationContext(), R.raw.launch);
						try {
							mediaPlayer.prepare();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						mediaPlayer.start();
						System.out.println("����start");
					} else {
						Log.i("getCodes", "�����޹����룬����ļ�ʻ");
					}
					BluetoothPairing.setEXTRA_DEVICE_ADDRESS(null);
					break;
				}
			}
		}
	}
*/
	/*
	private void ShowToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	//����֪ͨ����ʾ�û�
	private void showNotification(String tickerText, String contentTitle,
			String contentText, int id, int resId) {
		//��ȡNotificationManager����
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(resId, tickerText,
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getService(MyService.this,
				0, new Intent(MyService.this, MyService.class), 0);
		notification.setLatestEventInfo(MyService.this, contentTitle,
				contentText, contentIntent);
		notificationManager.notify(id, notification);
	}
	*/
}
