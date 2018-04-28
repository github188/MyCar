package com.cnlaunch.mycar.obd2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.os.Message;

public class MsgQueue
{
    private static ArrayList<MsgObserver> mObservers = new ArrayList<MsgObserver>();
    private static Set<Integer> mSet = new HashSet<Integer>();
    private static MsgQueue mMsgQueue = new MsgQueue();

    private MsgQueue()
    {
    }

    public static MsgQueue getMsgQueue()
    {
        return mMsgQueue;
    }

    public static MsgQueue addMessage(int msgId)
    {
        Message msg = new Message();
        msg.what = msgId;
        return getMsgQueue().addMessage(msg);
    }

    public MsgQueue addMessage(Message msg)
    {
        synchronized (mObservers)
        {
            for (MsgObserver observer : mObservers)
            {
                observer.deliverNotification(msg);
            }
        }
        return this;
    }

    public void registerObserver(MsgObserver observer)
    {
        synchronized (mObservers)
        {
            if (!mSet.contains(observer.getId()))
            {
                mObservers.add(observer);
                mSet.add(observer.getId());
            }
        }
    }

    public void unRegisterObserver(MsgObserver observer)
    {
        synchronized (mObservers)
        {
            int observerId = observer.getId();
            Iterator<MsgObserver> i = mObservers.iterator();
            while (i.hasNext())
            {
                if (i.next().getId() == observerId)
                {
                    i.remove();
                }
            }
        }
    }

    public void unRegisterObserver(int observerId)
    {
        synchronized (mObservers)
        {
            Iterator<MsgObserver> i = mObservers.iterator();
            while (i.hasNext())
            {
                if (i.next().getId() == observerId)
                {
                    i.remove();
                }
            }
        }
    }
}
