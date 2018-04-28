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

	/** ����GpsLoggerServiceManagerʱ����������service */
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
					// Log.w(TAG, "getLastWaypoint():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getTrackId():Զ������gps��¼����ʧ��", e);
			}
			return trackId;
		}
	}

	/**
	 * ����ɼ�����һ��·��
	 */
	public Location getLastWaypoint() {
		synchronized (mServiceVisitLock) {
			Location lastWaypoint = null;
			try {
				if (mIsBound) {
					lastWaypoint = this.mGpsLoggerRemote.getLastWaypoint();
				} else {
					// Log.w(TAG, "getLastWaypoint():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getLastWaypoint():Զ������gps��¼����ʧ��", e);
			}
			return lastWaypoint;
		}
	}

	/**
	 * GPS�ɼ������״̬
	 */
	public int getLoggingState() {
		synchronized (mServiceVisitLock) {
			int logging = GpsConstants.GPS_LOGGER_UNKNOWN;
			try {
				if (mIsBound) {
					logging = this.mGpsLoggerRemote.loggingState();
				} else {
					Log.w(TAG, "getLoggingState():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "getLoggingState():Զ������gps��¼����ʧ��", e);
			}
			return logging;
		}
	}

	/**
	 * ��Ƭ�������Ƿ�׼����
	 */
	public boolean isMediaPrepared() {
		synchronized (mServiceVisitLock) {
			boolean prepared = false;
			try {
				if (mIsBound) {
					prepared = this.mGpsLoggerRemote.isMediaPrepared();
				} else {
					Log.w(TAG, "isMediaPrepared():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "isMediaPrepared():Զ������gps��¼����ʧ��", e);
			}
			return prepared;
		}
	}

	/**
	 * ��ʼGPS�ɼ�������trackId
	 */
	public long startGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					return this.mGpsLoggerRemote.startLogging();
				} else {
					Log.w(TAG, "startGpsLogging():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "startGpsLogging():Զ������gps��¼����ʧ��", e);
			}
			return -1;
		}
	}

	/**
	 * ��ͣGPS�ɼ�
	 */
	public void pauseGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					this.mGpsLoggerRemote.pauseLogging();
					Log.e(TAG, "this.mGpsLoggerRemote.pauseLogging()");
				} else {
					Log.w(TAG, "pauseGpsLogging():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "pauseGpsLogging():Զ������gps��¼����ʧ��", e);
			}
		}
	}

	/**
	 * ��ͣ�󣬻ָ�GPS�ɼ�������segmentId
	 */
	public long resumeGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					return this.mGpsLoggerRemote.resumeLogging();
				} else {
					Log.w(TAG, "resumeGpsLogging():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "resumeGpsLogging():Զ������gps��¼����ʧ��", e);
			}
			return -1;
		}
	}

	/**
	 * ֹͣGPS�ɼ�
	 */
	public void stopGpsLogging() {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					this.mGpsLoggerRemote.stopLogging();
				} else {
					Log.w(TAG, "stopGpsLogging():gps��¼������δ��");
				}
			} catch (RemoteException e) {
				Log.e(TAG, "stopGpsLogging():Զ������gps��¼����ʧ��", e);
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param datasource
	 */
	public void storeDerivedDataSource(String datasource) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					this.mGpsLoggerRemote.storeDerivedDataSource(datasource);
				} catch (RemoteException e) {
					Log.w(TAG, "storeDerivedDataSource():���������ݷ��͵�gps��¼����", e);
				}
			} else {
				Log.w(TAG, "storeDerivedDataSource():gps��¼������δ��");
			}
		}
	}

	public void storeMediaUri(Uri mediaUri) {
		synchronized (mServiceVisitLock) {
			if (mIsBound) {
				try {
					this.mGpsLoggerRemote.storeMediaUri(mediaUri);
				} catch (RemoteException e) {
					Log.w(TAG, "storeMediaUri():���������ݷ��͵�gps��¼����", e);
				}
			} else {
				Log.w(TAG, "storeMediaUri():gps��¼������δ��");
			}
		}
	}

	/**
	 * �� GPS�ɼ�����
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
						    Log.w(TAG, "�����������onServiceDisconnected():mIsBound = " + mIsBound);
							mIsBound = false;
						}

					}

				};
				context.bindService(new Intent(
						GpsConstants.GPS_LOGGER_SERVICE_NAME), mServiceConnection,
						Context.BIND_AUTO_CREATE);
				Log.v(TAG, "startup():gps��¼����󶨳ɹ�");
			} else {
				Log.w(TAG, "startup():����gps��¼���������ӣ������ظ�����");
			}
		}
	}

	/** ȡ�� �� GPS�ɼ����� */
	public void shutdown(Context context) {
		synchronized (mServiceVisitLock) {
			try {
				if (mIsBound) {
					context.unbindService(this.mServiceConnection);
					mGpsLoggerRemote = null;
					mServiceConnection = null;
					mIsBound = false;
					Log.e(TAG, "�����������GpsLoggerServiceManager.shutdown()" + mIsBound);
				}
			} catch (IllegalArgumentException e) {
				Log.w(TAG, "unbind ����ʧ�ܣ����ܷ����Ѿ�����", e);
			}
		}
	}

}
