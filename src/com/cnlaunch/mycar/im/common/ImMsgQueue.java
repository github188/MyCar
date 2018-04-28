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
			throw(new IllegalArgumentException("注册的观察者,不能为NULL"));
		}
		synchronized (mObservers) {
			//Log.e("IM","注册Id="+observer.getId()+"的observer len="+mObservers.size());
			if (!mSet.contains(observer.getId())) {
				mObservers.add(observer);
				mSet.add(observer.getId());
			} else {
				// 使用新的覆盖旧的
				Log.e("IM", "ID为" + observer.getId()
						+ "的observer已被注册，本次注册将覆盖前一次注册");
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
					//Log.e("IM","注销Id为"+observerId+"的observer");
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
