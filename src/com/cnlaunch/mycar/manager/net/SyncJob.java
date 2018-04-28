package com.cnlaunch.mycar.manager.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @author xuzhuowei ͬ���������
 */
public abstract class SyncJob {
	public static final int SYNC_JOB_RESULT_CODE_SUCC = 0;
	public static final int SYNC_JOB_RESULT_CODE_ERR = -1;
	public static final int SYNC_JOB_RESULT_CODE_NO_DATA_TO_SYNC = -2;


	public static final int SYNC_TIMEOUT = 404;
	public static final int SYNC_MSG_USER_SETTING_SUCC = 10;
	public static final int SYNC_MSG_USER_SETTING_ERR = 11;
	public static final int SYNC_MSG_CUSTOM_CATEGORY_SUCC = 20;
	public static final int SYNC_MSG_CUSTOM_CATEGORY_ERR = 21;
	public static final int SYNC_MSG_CUSTOM_NO_DATA_TO_SYNC = 22;
	public static final int SYNC_MSG_ACCOUNT_SUCC = 30;
	public static final int SYNC_MSG_ACCOUNT_ERR = 31;
	public static final int SYNC_MSG_ACCOUNT_NO_DATA_TO_SYNC = 32;
	public static final int SYNC_MSG_USER_CAR_SUCC = 40;
	public static final int SYNC_MSG_USER_CAR_ERR = 41;
	public static final int SYNC_MSG_OIL_SUCC = 50;
	public static final int SYNC_MSG_OIL_ERR = 51;
	public static final int SYNC_MSG_OIL_NO_DATA_TO_SYNC = 52;
	public static final int SYNC_MSG_NOT_LOGIN = 61;
	
	protected NetRequest netRequest;
	protected List<Map<String, Object>> jobQueue;
	protected Handler mainHandler;
	protected Context context;
	protected int syncJobResultCode = SYNC_JOB_RESULT_CODE_NO_DATA_TO_SYNC;


	public SyncJob(Context context, Handler mainHandler) {
		this.mainHandler = mainHandler;
		this.context = context;
	}

	/**
	 * ����ͬ���������У�����ͬ������
	 */
	@SuppressWarnings("unchecked")
	public void doSync() {
		setRequestPara();
		if (jobQueue != null) {

			while (jobQueue.size() != 0) {
				Map<String, Object> map = jobQueue.remove(0);
				if (!map.containsKey("list")) {
					netRequest = new NetRequest(context,
							(TreeMap<String, Object>) map.get("paraMap"),
							(String) map.get("methodName"));
				} else {
					netRequest = new NetRequest(context,
							(TreeMap<String, Object>) map.get("paraMap"),
							(List) map.get("list"),
							(String) map.get("methodName"));
				}
				if (netRequest.doRequest()) {
					dealResult(map, netRequest.getResult());
				} else {
					dealResult(map, null);
				}
			}
		}
		sendMsgToUI();
	}

	/**
	 * ����Ϣ֪ͨ����UI��ͬ������
	 * 
	 * @param what
	 */
	protected void sendMsgToUi(int what) {
		if (mainHandler != null) {
			Message msg = new Message();
			msg.what = what;
			mainHandler.sendMessage(msg);
		}
	}

	/**
	 * ������ø÷����������������������
	 * 
	 * @param paraMap
	 *            �������
	 * @param methodName
	 *            �����������webservice������
	 */
	protected void addJob(TreeMap<String, Object> paraMap, String methodName) {
		if (jobQueue == null) {
			jobQueue = new ArrayList<Map<String, Object>>();
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("paraMap", paraMap);
		map.put("methodName", methodName);
		jobQueue.add(map);
	}

	protected void addJob(TreeMap<String, Object> paraMap, List list,
			String methodName) {
		if (jobQueue == null) {
			jobQueue = new ArrayList<Map<String, Object>>();
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("paraMap", paraMap);
		map.put("methodName", methodName);
		map.put("list", list);
		jobQueue.add(map);
	}

	protected void addJob(HashMap<String, Object> map) {
		if (jobQueue == null) {
			jobQueue = new ArrayList<Map<String, Object>>();
		}
		if (map != null) {
			jobQueue.add(map);
		}
	}

	/**
	 * ����������� ����ͨ��ʵ�ָ÷�������ͬ��������������ͬ������
	 */
	protected abstract void setRequestPara();

	/**
	 * ����ͨ��ʵ�ָ÷������Դ�����ֵ
	 * 
	 * @param map
	 *            �������
	 * @param soapObject
	 *            ��������ķ���ֵ
	 */
	protected abstract void dealResult(Map<String, Object> map,
			SoapObject result);

	/**
	 * ���ʹ�������UI
	 */
	protected abstract void sendMsgToUI();
}