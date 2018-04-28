package com.cnlaunch.mycar.obd2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class DataFlowModel
{private static final boolean D = false;
    private static DataFlowModel mDataFlowModel = new DataFlowModel();
    private static ArrayList<DataFlowObserver> mObservers = new ArrayList<DataFlowObserver>();

    private Map<Integer, String> mMap = Collections.synchronizedMap(new HashMap<Integer, String>());

    private DataFlowModel()
    {
    }

    public static DataFlowModel getModel()
    {
        return mDataFlowModel;
    }

    public DataFlowModel setValue(int key, String value)
    {
        mMap.put(key, value);
        return this;
    }

    public String getValue(int key)
    {
        return mMap.get(key);
    }

    public void registerObserver(DataFlowObserver observer)
    {
        synchronized (mObservers)
        {
            if (!mObservers.contains(observer))
            {
                mObservers.add(observer);
                //Log.e("registerObserver", "-->" + mObservers.size());
            }
        }
    }

    public void unRegisterObserver(DataFlowObserver observer)
    {
        synchronized (mObservers)
        {
            if (mObservers.contains(observer))
            {
                mObservers.remove(observer);
            }
            if(D) Log.e("unRegisterObserver", "-->" + mObservers.size());
        }
    }

//    public void notifyChange(int[] changedDataFlows)
//    {
//        synchronized (mObservers)
//        {
//            Log.i("time2", "for s->" + System.currentTimeMillis() + " s");
//            for (DataFlowObserver observer : mObservers)
//            {
//                Log.i("time2", "for ss->" + System.currentTimeMillis() + " s");
//                if(observer.deliverNotification(changedDataFlows)){
//                    break;
//                }
//                Log.i("time2", "for ee->" + System.currentTimeMillis() + " s");
//            }
//            Log.i("time2", "for e->" + System.currentTimeMillis() + " s");
//        }
//    }
    public void notifyChange(int[] changedDataFlows) {
        synchronized (mObservers) {
            for (DataFlowObserver observer : mObservers) {
                observer.deliverNotification(changedDataFlows);
            }
        }
    }

}
