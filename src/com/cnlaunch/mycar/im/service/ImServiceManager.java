package com.cnlaunch.mycar.im.service;

import com.cnlaunch.mycar.im.common.ImConstant;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ImServiceManager {
	private byte[] mServiceVisitLock = new byte[0];
	private IImServiceRemote mImServiceRemote;
	private boolean mIsBound = false;

	private Runnable mOnServiceConnected;
	private ServiceConnection mServiceConnection;

	private void d(String str) {
		Log.d("ImServiceManager", str);
	}

	public ImServiceManager(final Context context) {

				context.startService(new Intent(ImConstant.IM_SERVICE_NAME));

	}

	public boolean isLogined() {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					return mImServiceRemote.isLogined();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				d("isLogined() -> ImService�� δ��");
			}
		}
		return false;
	}

	public void startup(Context context, Runnable onServiceConnected) {
		synchronized (mServiceVisitLock) {
			if (!mIsBound) {
				mOnServiceConnected = onServiceConnected;
				mServiceConnection = new ServiceConnection() {

					@Override
					public void onServiceDisconnected(ComponentName name) {
						synchronized (mServiceVisitLock) {
							mIsBound = false;
						}
					}

					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized (mServiceVisitLock) {
							mImServiceRemote = IImServiceRemote.Stub
									.asInterface(service);
							mIsBound = true;

							if (mOnServiceConnected != null) {
								mOnServiceConnected.run();
								mOnServiceConnected = null;
							}
						}
					}
				};
				context.bindService(new Intent(ImConstant.IM_SERVICE_NAME),
						mServiceConnection, Context.BIND_AUTO_CREATE);

				d("�����Ѱ�");
			} else {
				d("�����ظ��󶨷���");
			}
		}
	}

	public void shutdown(Context context) {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					context.unbindService(this.mServiceConnection);
					mServiceConnection = null;
					mImServiceRemote = null;
					mIsBound = false;
					d("�����ѽ����");
				}
			} catch (IllegalArgumentException e) {
				d("unBind ����ʧ�ܣ����ܷ����Ѿ�����" + e.toString());
			}
		}
	}

	public void stopImService(Context context) {
		context.stopService(new Intent(ImConstant.IM_SERVICE_NAME));
	}

}
