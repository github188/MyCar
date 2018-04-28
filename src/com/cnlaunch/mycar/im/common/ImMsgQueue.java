package com.cnlaunch.mycar.im.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.os.Message;
import android.util.Log;

public class ImMsgQueue {

	private ArrayList<ImMsgObserver> mObservers = new ArrayList<ImMsgObserver>();
	private Set<Integer> mSet = new HashSet<Integer>();
	private static ImMsgQueue mInstance = new ImMsgQueue();

	private ImMsgQueue() {
	}

	public static ImMsgQueue getInstance() {
		return mInstance;
	}

	public static ImMsgQueue addMessage(int msgId) {
		Message msg = new Message();
		msg.what = msgId;
		return getInstance().addMessage(msg);
	}

	public ImMsgQueue addMessage(Message msg) {
		synchronized (mObservers) {
			for (ImMsgObserver observer : mObservers) {
				if(observer.deliverNotification(msg)){
					//Log.e("IM","ImMsgQueue -> MsgId="+msg.what + "->" + observer.getId());
					break;
				}
			}
			
		}
		return this;
	}

	public void registerObserver(ImMsgObserver observer) {
		if(observer == null){
			throw(new IllegalArgumentException("ע��Ĺ۲���,����ΪNULL"));
		}
		synchronized (mObservers) {
			//Log.e("IM","ע��Id="+observer.getId()+"��observer len="+mObservers.size());
			if (!mSet.contains(observer.getId())) {
				mObservers.add(observer);
				mSet.add(observer.getId());
			} else {
				// ʹ���µĸ��Ǿɵ�
				Log.e("IM", "IDΪ" + observer.getId()
						+ "��observer�ѱ�ע�ᣬ����ע�Ὣ����ǰһ��ע��");
				mObservers.remove(observer);
				mSet.remove(observer.getId());
				mObservers.add(observer);
				mSet.add(observer.getId());
			}
			//Log.e("IM",System.currentTimeMillis() + " len="+mObservers.size());
		}
	}

	public void unRegisterObserver(ImMsgObserver observer) {
		if (observer == null) {
			return;
		}
		synchronized (mObservers) {
			//Log.e("IM",System.currentTimeMillis() + " len="+mObservers.size());
			int observerId = observer.getId();
			Iterator<ImMsgObserver> i = mObservers.iterator();
			while (i.hasNext()) {
				if (i.next().getId() == observerId) {
					i.remove();
					mSet.remove(observerId);
					//Log.e("IM","ע��IdΪ"+observerId+"��observer");
				}
			}
			//Log.e("IM",System.currentTimeMillis() + " len="+mObservers.size());
		}
	}

	public void unRegisterObserver(int observerId) {
		synchronized (mObservers) {
			Iterator<ImMsgObserver> i = mObservers.iterator();
			while (i.hasNext()) {
				if (i.next().getId() == observerId) {
					i.remove();
				}
			}
		}
	}
}
