package com.cnlaunch.mycar.gps.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cnlaunch.mycar.gps.GpsConstants;

public class GpsLoggerServiceManager {
	private String TAG = "GpsLoggerServiceManager";
	private IGpsLoggerServiceRemote mGpsLoggerRemote;
	private boolean mIsBound = false;
	private byte[] mServiceVisitLock = new byte[0];

	private Runnable mOnServiceConnected;
	private ServiceConnection mServiceConnection;

	/** 创建GpsLoggerServiceManager时，立即启动service */
	public GpsLoggerServiceManager(Context context) {
		context.startService(new Intent(GpsConstants.GPS_LOGGER_SERVICE_NAME));
		Log.e(TAG, "context.startService():"
				+ GpsConstants.GPS_LOGGER_SERVICE_NAME);
	}

	public long getTrackId() {
		synchronized (mServiceVisitLock) {
			long trackId = -1;
			try {
				if (mIsBound) {
					trackId = this.mGpsLoggerRemote.getTrackId();
				} else {
					// Log.w(TAG, "getLastWaypoint():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getTrackId():远程连接gps记录服务失败", e);
			}
			return trackId;
		}
	}

	/**
	 * 最近采集到的一个路点
	 */
	public Location getLastWaypoint() {
		synchronized (mServiceVisitLock) {
			Location lastWaypoint = null;
			try {
				if (mIsBound) {
					lastWaypoint = this.mGpsLoggerRemote.getLastWaypoint();
				} else {
					// Log.w(TAG, "getLastWaypoint():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getLastWaypoint():远程连接gps记录服务失败", e);
			}
			return lastWaypoint;
		}
	}

	/**
	 * GPS采集服务的状态
	 */
	public int getLoggingState() {
		synchronized (mServiceVisitLock) {
			int logging = GpsConstants.GPS_LOGGER_UNKNOWN;
			try {
				if (mIsBound) {
					logging = this.mGpsLoggerRemote.loggingState();
				} else {
					Log.w(TAG, "getLoggingState():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getLoggingState():远程连接gps记录服务失败", e);
			}
			return logging;
		}
	}

	/**
	 * 照片或文字是否准备好
	 */
	public boolean isMediaPrepared() {
		synchronized (mServiceVisitLock) {
			boolean prepared = false;
			try {
				if (mIsBound) {
					prepared = this.mGpsLoggerRemote.isMediaPrepared();
				} else {
					Log.w(TAG, "isMediaPrepared():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "isMediaPrepared():远程连接gps记录服务失败", e);
			}
			return prepared;
		}
	}

	/**
	 * 开始GPS采集，返回trackId
	 */
	public long startGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					return this.mGpsLoggerRemote.startLogging();
				} else {
					Log.w(TAG, "startGpsLogging():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "startGpsLogging():远程连接gps记录服务失败", e);
			}
			return -1;
		}
	}

	/**
	 * 暂停GPS采集
	 */
	public void pauseGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					this.mGpsLoggerRemote.pauseLogging();
					Log.e(TAG, "this.mGpsLoggerRemote.pauseLogging()");
				} else {
					Log.w(TAG, "pauseGpsLogging():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "pauseGpsLogging():远程连接gps记录服务失败", e);
			}
		}
	}

	/**
	 * 暂停后，恢复GPS采集，返回segmentId
	 */
	public long resumeGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					return this.mGpsLoggerRemote.resumeLogging();
				} else {
					Log.w(TAG, "resumeGpsLogging():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "resumeGpsLogging():远程连接gps记录服务失败", e);
			}
			return -1;
		}
	}

	/**
	 * 停止GPS采集
	 */
	public void stopGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					this.mGpsLoggerRemote.stopLogging();
				} else {
					Log.w(TAG, "stopGpsLogging():gps记录服务尚未绑定");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "stopGpsLogging():远程连接gps记录服务失败", e);
			}
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param datasource
	 */
	public void storeDerivedDataSource(String datasource) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					this.mGpsLoggerRemote.storeDerivedDataSource(datasource);
				} catch (RemoteException e) {
					Log.w(TAG, "storeDerivedDataSource():无数将数据发送到gps记录服务", e);
				}
			} else {
				Log.w(TAG, "storeDerivedDataSource():gps记录服务尚未绑定");
			}
		}
	}

	public void storeMediaUri(Uri mediaUri) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					this.mGpsLoggerRemote.storeMediaUri(mediaUri);
				} catch (RemoteException e) {
					Log.w(TAG, "storeMediaUri():无数将数据发送到gps记录服务", e);
				}
			} else {
				Log.w(TAG, "storeMediaUri():gps记录服务尚未绑定");
			}
		}
	}

	/**
	 * 绑定 GPS采集服务
	 * 
	 * @param context
	 * @param onServiceConnected
	 */
	public void startup(Context context, final Runnable onServiceConnected) {
		synchronized (mServiceVisitLock) {
			if (!mIsBound) {
				mOnServiceConnected = onServiceConnected;
				mServiceConnection = new ServiceConnection() {

					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized (mServiceVisitLock) {
							mGpsLoggerRemote = IGpsLoggerServiceRemote.Stub
									.asInterface(service);
							mIsBound = true;
							Log.e(TAG, "GpsLoggerServiceManager.startup()");

							if (mOnServiceConnected != null) {
								mOnServiceConnected.run();
								mOnServiceConnected = null;
							}
						}
					}

					@Override
					public void onServiceDisconnected(ComponentName name) {
						synchronized (mServiceVisitLock) {
						    Log.w(TAG, "☆☆☆☆☆☆☆☆☆☆onServiceDisconnected():mIsBound = " + mIsBound);
							mIsBound = false;
						}

					}

				};
				context.bindService(new Intent(
						GpsConstants.GPS_LOGGER_SERVICE_NAME), mServiceConnection,
						Context.BIND_AUTO_CREATE);
				Log.v(TAG, "startup():gps记录服务绑定成功");
			} else {
				Log.w(TAG, "startup():已与gps记录服务建立连接，不能重复连接");
			}
		}
	}

	/** 取消 绑定 GPS采集服务 */
	public void shutdown(Context context) {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					context.unbindService(this.mServiceConnection);
					mGpsLoggerRemote = null;
					mServiceConnection = null;
					mIsBound = false;
					Log.e(TAG, "☆☆☆☆☆☆☆☆☆☆GpsLoggerServiceManager.shutdown()" + mIsBound);
				}
			} catch (IllegalArgumentException e) {
				Log.w(TAG, "unbind 服务失败，可能服务已经结束", e);
			}
		}
	}

}
