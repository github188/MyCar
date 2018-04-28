package com.cnlaunch.mycar.updatecenter.listener;

import java.io.File;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.tools.PackageInstaller;

public class ApkDownloadListener extends DefaultHttpListener
{
    CustomProgressDialog dlg;
    DefaultHttpListener listener = this;
    HttpDownloadManager httpDownloader;
    Activity activity;
    Handler handler;
    public ApkDownloadListener(Activity activity, Handler handler,HttpDownloadManager httpDownloader)
    {
        this.activity = activity;
        this.handler = handler;
        this.httpDownloader = httpDownloader;
    }
    @Override
    public void onHttpStart(String aUrl)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                dlg = new CustomProgressDialog(activity);
                dlg.setTitle(activity.getResources().getString(R.string.upc_download_and_waiting));
                dlg.setStyle(false);
                dlg.setOnKeyListener(new OnKeyListener()
                {
                    @Override
                    public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
                    {
                        if(aKeyCode == KeyEvent.KEYCODE_BACK)
                        {
                            dlg.dismiss();
                        }
                        return false;
                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    public void onHttpDownloadProgress(final int aPercent, int aSpeed,
            int aRestHours, int aRestMinutes, int aRestSeconds)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(dlg!=null)
                {
                    dlg.setProgress(aPercent);
                }
            }
        });
    }

    @Override
    public void onHttpException(final Object aReason)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                httpDownloader.removeListener(listener);
                if (dlg!=null) dlg.dismiss();
                if(aReason!=null)
                {
                    if (aReason!=null)
                    {
                        showErrorInfoDialog(aReason.toString(),activity);
                    }
                }
            }
        });
    }

    @Override
    public void onHttpFinish(final Object aTarget,Object extra)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (dlg!=null) dlg.dismiss();
                httpDownloader.removeListener(listener);
                if(aTarget!=null && aTarget instanceof File)
                {
                    File apkFile = (File)aTarget;
                    PackageInstaller installer = new PackageInstaller(activity);
                    installer.installApk(apkFile);
                }
            }
        });
    }
}
