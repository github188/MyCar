package com.cnlaunch.mycar.obd2.model;

import android.content.Context;
import android.os.Handler;

public abstract class DataFlowObserver
{
    private int[] mWatchDataFlows;
    private Handler mHandler;

    public DataFlowObserver(final int[] watchDataFlows, Context context)
    {
        mWatchDataFlows = watchDataFlows;
        mHandler = new Handler(context.getMainLooper());
    }

    private final class NotificationRunnable implements Runnable
    {

        @Override
        public void run()
        {
            onChange(mWatchDataFlows);
        }
    }

    public final boolean deliverNotification(int[] changedDataFlows)
    {
        if (isNeedNotify(changedDataFlows))
        {
            //Log.i("time2", "mHandler.post s->" + System.currentTimeMillis() + " s");
            new Thread()
            {
                @Override
                public void run()
                {
                    //Log.i("time2", "mHandler.post ss->" + System.currentTimeMillis() + " s");
                    mHandler.post(new NotificationRunnable());
                    //Log.i("time2", "mHandler.post ee->" + System.currentTimeMillis() + " s");
                }
            }.start();
            return true;
        }

        //Log.i("time2", "mHandler.post e->" + System.currentTimeMillis() + " s");

        return false;
    }

    private boolean isNeedNotify(int[] changedDataFlows)
    {
        for (Integer watchDataFlow : mWatchDataFlows)
        {
            for (Integer changedDataFlow : changedDataFlows)
            {
                if (watchDataFlow.intValue() == changedDataFlow.intValue())
                {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void onChange(final int[] watchDataFlows);

}
