package com.cnlaunch.mycar.updatecenter.listener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.dbscar.ApkUpdateInfo;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.webservice.SoapMethod;
import com.cnlaunch.mycar.updatecenter.webservice.SoapRequest;
import com.cnlaunch.mycar.updatecenter.webservice.SoapResponse;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceListener;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;

public class DownloadBinUpdateInfoListener implements WebServiceListener 
{
    ProgressDialog pdlg;
    DownloadBinUpdateInfoListener thiz = this;
    HttpDownloadManager httpDownloader;
    DefaultHttpListener downloadListener;
    WebServiceOperator webservice;
    Activity activity;
    Handler handler;
    Resources resources;
    public DownloadBinUpdateInfoListener(Activity activity, 
        Handler handler,
        HttpDownloadManager httpDownloader,
        DefaultHttpListener downloadListener,
        WebServiceOperator webservice)
    {
        this.activity = activity;
        this.handler = handler;
        this.httpDownloader = httpDownloader;
        this.downloadListener = downloadListener;
        this.webservice = webservice;
        this.resources = activity.getResources();
    }
    final private void download(int detailId)
    {
        httpDownloader.removeAllListeners();
        httpDownloader.addListener(downloadListener);
        httpDownloader.downloadApk(detailId,UpdateCenterConstants.TEMP_DIR);
    }

    @Override
    public void onStartWebServiceRequest(Object context,SoapRequest request)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
//                pdlg = new ProgressDialog(activity);
//                pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                pdlg.setMessage(activity.getResources().getString(R.string.upc_querying));
//                pdlg.setOnKeyListener(new OnKeyListener()
//                {
//                    @Override
//                    public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
//                    {
//                        if(aKeyCode==KeyEvent.KEYCODE_BACK && aEvent.getAction()== KeyEvent.ACTION_UP)
//                        {
//                            webservice.removeListener(thiz);
//                            pdlg.dismiss();
//                        }
//                        return true;
//                    }
//                });
//                pdlg.show();
            }
        });
    }

    @Override
    public void onWebServiceSuccess(Object context,final SoapResponse response)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (pdlg!=null)
                {
                    pdlg.dismiss();
                }
                webservice.removeListener(thiz);
                if (response.getMethod().equals(SoapMethod.QUERY_BIN_FILE_UPDATE_INFO))
                {
                    final ApkUpdateInfo info = (ApkUpdateInfo) response.getResult();
                    if(info != null && !info.isLatestVersion())
                    {
                        download(info.getDetailId());
                    }
//                    final CustomAlertDialog dlg = new CustomAlertDialog(activity);
//                    dlg.setTitle(R.string.upc_tips);
//                    dlg.setMessage(resources.getString(R.string.upc_current_dbscar_version)+":"+
//                                            Constants.MYCAR_VERSION+"\n"  //手机当前的版本 
//                                            +resources.getString(R.string.upc_latest_dbscar_version)+":"
//                                            +info.getVersionNumber()+"\n" //服务器的最新版本
//                                            +info.getUpdateDescription());// 升级描述信息
//                    dlg.setOnKeyListener(new OnKeyListener()
//                    {
//                        @Override
//                        public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
//                        {
//                            if(aKeyCode==KeyEvent.KEYCODE_BACK)
//                            {
//                                dlg.dismiss();
//                            }
//                            return false;
//                        }
//                    });
//         
//                    
//                    if(info.forceUpdate())// 强制升级
//                    {
//                        
//                        
//                        // 立即升级
//                        dlg.setNegativeButton(R.string.upc_update_right_now, new OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View aV)
//                            {
//                                dlg.dismiss();
//                                download(info.getDetailId());
//                            }
//                        });
//                        dlg.show();
//                    }
//                    else if(info.optionalUpdate())// 可选升级,非强制
//                    {
//                        // 立即升级
//                        dlg.setPositiveButton(R.string.upc_update_right_now, new OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View aV)
//                            {
//                                dlg.dismiss();
//                                download(info.getDetailId());
//                            }
//                        });
//                        // 以后再说
//                        dlg.setNegativeButton(R.string.upc_later, new OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View aV)
//                            {
//                                dlg.dismiss();
//                            }
//                        });
//                        dlg.show();
//                    }
//                    else if(info.isLatestVersion())// 已经是最新版本,不必升级
//                    {
//                        // 确定
//                        dlg.setMessage(resources.getString(R.string.upc_current_dbscar_version)+":"
//                                    +Constants.MYCAR_VERSION+","+resources.getString(R.string.upc_already_latest_version));
//                        dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View aV)
//                            {
//                                dlg.dismiss();
//                            }
//                        });
//                        dlg.dismiss();
//                    }
                }
            }
        });
    }
    
    @Override
    public void onWebServiceErrors(Object context,final int code,final SoapRequest request)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (pdlg!=null)
                {
                    pdlg.dismiss();
                }
                webservice.removeListener(thiz);
                if (request!=null)
                {
                    String reason = resources.getString(R.string.upc_network_communication_error);
                    
                    if (code == WebServiceOperator.Error.NETWORK_NOT_AVAILABLE)// 网络不可用
                    {
                         reason = resources.getString(R.string.upc_network_not_active);                   
                    }
                    if (code == WebServiceOperator.Error.TIMEOUT)// 请求超时
                    {
                         reason = resources.getString(R.string.upc_network_timeout);
                    }
                    
                    showErrorInfoDialog(reason,activity);    
                }
            }
        });
    }
    // 错误对话框
    private void showErrorInfoDialog(String message, Activity activity)
    {
        final CustomAlertDialog dlg = new CustomAlertDialog(activity);
        dlg.setTitle(R.string.upc_error);
        dlg.setMessage(message);
        dlg.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
            {
                if(aKeyCode==KeyEvent.KEYCODE_BACK)
                {
                    dlg.dismiss();
                }
                return false;
            }
        });
        dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
        {
            @Override
            public void onClick(View aV)
            {
                dlg.dismiss();
            }
        });
        dlg.show();
    }
}
