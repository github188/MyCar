package com.cnlaunch.mycar.obd2.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;

public class Obd2DiagnoseServiceManager {
	private static final boolean D = false;
	private byte[] mServiceVisitLock = new byte[0];
	private IDiagnoseServiceRemote mDiagnoseServiceRemote;
	private boolean mIsBound = false;

	private Runnable mOnServiceConnected;
	private ServiceConnection mServiceConnection;

	private void d(String str) {
		Log.d("ODB2_SERVICE", str);
	}

	public Obd2DiagnoseServiceManager(Context context) {
		context.startService(new Intent(Constants.OBD2_SERVICE_NAME));
		d("DiagnoseServiceManager -> start");
	}

	public void prepareValue(final int[] dataFlowNames) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					this.mDiagnoseServiceRemote.prepareValue(dataFlowNames);
				} catch (RemoteException e) {
					if (D)
						d("prepareValue() --> 无法连接Service");
				}
			} else {
				if (D)
					d("Service尚未绑定");
			}
		}
	}

	public String getValue(final int dataFlowName) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					return this.mDiagnoseServiceRemote.getValue(dataFlowName);
				} catch (RemoteException e) {
					if (D)
						d("prepareValue() --> 无法连接Service");
				}
			} else {
				if (D)
					d("Service尚未绑定");
			}
		}
		return null;
	}

	public void startup(Context context, Runnable onServiceConnected) {
		synchronized (mServiceVisitLock) {
			if (!mIsBound) {
				mOnServiceConnected = onServiceConnected;
				mServiceConnection = new ServiceConnection() {

					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized (mServiceVisitLock) {
							mDiagnoseServiceRemote = IDiagnoseServiceRemote.Stub
									.asInterface(service);
							mIsBound = true;
							if (mOnServiceConnected != null) {
								mOnServiceConnected.run();
								mOnServiceConnected = null;
							}
						}
					}

					@Override
					public void onServiceDisconnected(ComponentName name) {
						synchronized (mServiceVisitLock) {
							mIsBound = false;
						}

					}
				};
				context.bindService(new Intent(Constants.OBD2_SERVICE_NAME),
						mServiceConnection, Context.BIND_AUTO_CREATE);
				if (D)
					d("服务已绑定");
			} else {
				if (D)
					d("不能重复绑定服务");
			}
		}
	}

	public void shutdown(Context context) {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					context.unbindService(this.mServiceConnection);
					mServiceConnection = null;
					mDiagnoseServiceRemote = null;
					mIsBound = false;
					if (D)
						d("服务已解除绑定");
				}
			} catch (IllegalArgumentException e) {
				if (D)
					d("unbind 服务失败，可能服务已经结束" + e.toString());
			}
		}
	}
}
