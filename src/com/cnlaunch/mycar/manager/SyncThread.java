package com.cnlaunch.mycar.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.manager.net.SyncAccountJob;
import com.cnlaunch.mycar.manager.net.SyncCustomCategoryJob;
import com.cnlaunch.mycar.manager.net.SyncJob;
import com.cnlaunch.mycar.manager.net.SyncManagerSettingJob;
import com.cnlaunch.mycar.manager.net.SyncOilJob;
import com.cnlaunch.mycar.manager.net.SyncUserCarJob;

public class SyncThread extends Thread {

	Context mContext;
	Handler mHandler;

	public SyncThread(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		try
        {
            SyncJob[] syncJobs = new SyncJob[] {
            		new SyncManagerSettingJob(mContext, mHandler),
            		new SyncCustomCategoryJob(mContext, mHandler),
            		new SyncAccountJob(mContext, mHandler),
            		new SyncUserCarJob(mContext, mHandler),
            		new SyncOilJob(mContext, mHandler), };
            final int SYNC_JOB_COUNT = syncJobs.length;

            int i = 0;
            while (MyCarActivity.isLogin && i < syncJobs.length) {
            	syncJobs[i++].doSync();
            }

            // 如果同步过程中，用户登录状态有变化，导致同步未全部完成
            if (i < SYNC_JOB_COUNT) {
            	if (mHandler != null) {
            		Message msg = new Message();
            		msg.what = SyncJob.SYNC_MSG_NOT_LOGIN;
            		mHandler.sendMessage(msg);
            	}
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

	}
}
