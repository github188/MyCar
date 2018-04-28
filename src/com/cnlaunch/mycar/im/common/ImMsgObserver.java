package com.cnlaunch.mycar.im.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class ImMsgObserver {
	private int mId;
	private Handler mHandler;

	public ImMsgObserver(final int id, Context context) {
		mId = id;
		mHandler = new Handler(context.getMainLooper());
	}

	private final class NotificationRunnable implements Runnable {
		Message msg;

		public NotificationRunnable(final Message msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			dealMessage(this.msg);
		}
	}

	private final void dispatchChange(final Message msg) {
		mHandler.post(new NotificationRunnable(msg));
	}

	public final boolean deliverNotification(final Message msg) {
		if (isNeedNotify(msg.what)) {
			dispatchChange(msg);
			return true;
		}
		return false;
	}

	private boolean isNeedNotify(int msgId) {
		if (mId == msgId) {
			return true;
		}
		return false;
	}

	public abstract void dealMessage(final Message msg);

	public Integer getId() {
		return mId;
	}
}
