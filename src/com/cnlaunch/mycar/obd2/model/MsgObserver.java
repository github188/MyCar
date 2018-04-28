package com.cnlaunch.mycar.obd2.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class MsgObserver
{
    private int mId;
    private Handler mHandler;

    public MsgObserver(final int id, Context context)
    {
        mId = id;
        mHandler = new Handler(context.getMainLooper());
    }

    private final class NotificationRunnable implements Runnable
    {
        Message msg;

        public NotificationRunnable(Message msg)
        {
            this.msg = msg;
        }

        @Override
        public void run()
        {
            dealMessage(this.msg);
        }
    }

    private final void dispatchChange(Message msg)
    {
        mHandler.post(new NotificationRunnable(msg));
    }

    public final void deliverNotification(Message msg)
    {
        if (isNeedNotify(msg.what))
        {
            dispatchChange(msg);
        }
    }

    private boolean isNeedNotify(int msgId)
    {
        if (mId == msgId)
        {
            return true;
        }
        return false;
    }

    public abstract void dealMessage(final Message msg);

    public Integer getId()
    {
        return mId;
    }
}
