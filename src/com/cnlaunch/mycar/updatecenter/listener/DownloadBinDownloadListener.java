package com.cnlaunch.mycar.updatecenter.listener;

import java.io.File;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.utils.ZipHelper;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;

public class DownloadBinDownloadListener extends DefaultHttpListener
{
    private boolean D = true;
    private final static String TAG = "DownloadBinDownloadListener";
    CustomProgressDialog dlg;
    DefaultHttpListener listener = this;
    HttpDownloadManager httpDownloader;
    Activity activity;
    Handler handler;
    public DownloadBinDownloadListener(Activity activity, Handler handler,HttpDownloadManager httpDownloader)
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
//                dlg = new CustomProgressDialog(activity);
//                dlg.setTitle(activity.getResources().getString(R.string.upc_download_and_waiting));
//                dlg.setStyle(false);
//                dlg.setOnKeyListener(new OnKeyListener()
//                {
//                    @Override
//                    public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
//                    {
//                        if(aKeyCode == KeyEvent.KEYCODE_BACK)
//                        {
//                            dlg.dismiss();
//                        }
//                        return false;
//                    }
//                });
//                dlg.show();
            }
        });
    }

    @Override
    public void onHttpDownloadProgress(final int aPercent, int aSpeed,
            int aRestHours, int aRestMinutes, int aRestSeconds)
    {
//        handler.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                if(dlg!=null)
//                {
//                    dlg.setProgress(aPercent);
//                }
//            }
//        });
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
                if(aTarget instanceof File)
                {
                    unzipFile((File)aTarget);// ��ѹ�ļ�
                }
                // �����Ƿ���Ҫ����download.binΪ����
                ((MyCarApplication) activity.getApplication()).setIsNeedUpdateDownloadBin(true);
            }
        });
    }
    
    /**
     * ��ѹ�ļ�
     * */ 
    private void unzipFile(final File zipFile) 
    {
        new Thread()
        {
            public void run()
            { 
                ZipHelper unzipTool = new ZipHelper();
                unzipTool.setUnzipListener(new UnzipFileListener());
                unzipTool.unzipFileTo(zipFile, UpdateCenterConstants.CNLAUNCH_DIR);
            }
        }.start();
    }
    
    // �ļ���ѹ���� 
    class UnzipFileListener implements ZipHelper.UnzipListener
    {
        @Override
        public void onUnzipping(final String file,final String destDir,final int percent)
        {
        }
        
        /**
         * zip�ļ���ѹ���
         * */
        @Override
        public void onUnzipFinished(final File file,final String destFile)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {

                    String fileName = file.getName();
                    String subString = "";
                    if(fileName.endsWith(".zip"))
                    {
                        subString = fileName.substring(0,file.getName().indexOf(".zip"));                       
                    }else if (fileName.endsWith(".ZIP"))
                    {
                        subString = fileName.substring(0,file.getName().indexOf(".ZIP"));
                    }else
                    {
                        if(D)Log.e(TAG,"���ش���,����ZIP��!");
                        return;
                    }
                    String[] sa = subString.split("_");
                    
                    if(sa.length != 5)
                    {
                        if(D)Log.e(TAG,"�汾��Ϣ��������");
                        return;
                    }

                    String version  = sa[2]+"."+sa[3];// �汾
                    String language = sa[4];// ����
                    ((MyCarApplication)activity.getApplication()).saveDownloadBinVersion(version, language);
                    if(D)Log.d(TAG, "���صõ���Download.bin"+"�汾 :" + version +  "����: "+ language);
                }
            });
        }

        @Override
        public void onUnzipError(final File zipFile,final Object reason)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                }
            });
        }
    }
}
