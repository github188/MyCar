package com.cnlaunch.mycar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.ZipHelper;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

public class AutoUpdateService extends Service
{

    // 调试log信息target
    private static final String TAG = "AutoUpdateService";
    private static final boolean D = true;

    WebServiceOperator webservice;
    HttpDownloadManager httpDownloader;
    DefaultHttpListener downloadBinDownloadListener;
    DefaultHttpListener apkDownloadListener;
    Handler mHandler;
    MyCarApplication application;
    String language = "";

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        mHandler = new Handler();
        webservice = new WebServiceOperator(this);
        httpDownloader = new HttpDownloadManager(this);
        application = (MyCarApplication) getApplication();
        language = Locale.getDefault().getLanguage();
        if (language.equals("zh"))
        {
            language = "CN";
        }
        // downloadBinDownloadListener = new
        // DownloadBinDownloadListener(AutoUpdateService.this, mHandler,
        // httpDownloader);
        // apkDownloadListener = new ApkDownloadListener(AutoUpdateService.this,
        // mHandler, httpDownloader);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        // -----------------第一步：同步服务器上该用户已经注册的序列号------------------
        try
        {
            querySerialNumerOfRegistered();

            // -----------------第二步：检查download.bin是否有更新---------------------
            List<String> serials = application.getValues(MyCarActivity.cc, Constants.SP_DEVICE_INFO_SERIAL_NUMBER);// 得到序列号

            if (serials != null && serials.size() > 0)
            {
                if (getLocalDownloadBinVersion() != null)
                {
                    queryBinFileUpdateInfo(MyCarActivity.cc, serials.get(0), getLocalDownloadBinVersion(), language.equals("CN") ? language : "EN");
                }
                else
                {
                    queryBinFileUpdateInfo(MyCarActivity.cc, serials.get(0), Constants.DOWNLOAD_BIN_BASE_VERSION, language.equals("CN") ? language : "EN");
                }
            }
            // ------------------第三步：检查诊断软件是否有更新-------------------------
            queryDiagnoseSWIsNeedUpdate();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询设备Download.bin的升级信息
     * @param serialNum 设备序列号
     */
    public void queryBinFileUpdateInfo(String cc, String productSerialNo, String versionNo, String displayLan)
    {
        TreeMap paraMap = new TreeMap();
        paraMap.put("cc", cc);
        paraMap.put("productSerialNo", productSerialNo);
        paraMap.put("versionNo", versionNo);
        paraMap.put("displayLan", displayLan);
        RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_PUBLIC_SOFT, "getBinFileMaxVersion", null, paraMap, true);
        WebServiceManager wsm = new WebServiceManager(requestParameter);

        // 获得注册结果对象
        Object object = wsm.execute().object;
        SoapObject result = null;
        // getBinFileMaxVersionResponse{return=anyType{code=0; message=success;
        // SoftMaxVersion=anyType{forceUpgrade=1; versionDetailId=1146;
        // versionNo=10.48; }; }; }

        if (object != null && object instanceof SoapObject)
        {
            result = (SoapObject) object;

            // 取到soap对象中的第一个元素
            SoapObject so = (SoapObject) result.getProperty(0);

            // 取得服务端返回的响应code
            int code = so.hasProperty("code") ? new Integer(so.getProperty("code").toString()).intValue() : -1;
            if (code == UsercenterConstants.RESULT_SUCCESS)
            {
                SoapObject softMaxVersion = (SoapObject) so.getProperty("SoftMaxVersion");
                int forceUpgrade = new Integer(softMaxVersion.getPropertyAsString("forceUpgrade"));
                int versionDetailId = new Integer(softMaxVersion.getPropertyAsString("versionDetailId"));
                String newVersionNo = softMaxVersion.getPropertyAsString("versionNo");
                if (forceUpgrade == 1) // 需要升级
                {
                    if (D)
                        Log.d(TAG, "downloadApk() 从http URL 下载 诊断 文件 ..." + "明细 id: " + versionDetailId);
                    TreeMap<String, Object> paramList = new TreeMap<String, Object>();
                    paramList.put("versionDetailId", versionDetailId);// 明细ID
                    requestParameter = new RequestParameter(UpdateCenterConstants.UPDATE_PUBLIC_SOFTWARE_DOWNLOAD_URL, null, paramList);
                    requestParameter.downloadDir = UpdateCenterConstants.DBSCAR_DIR + "/temp";// 软件下载保存目录
                    download(paramList,requestParameter);

                }
            }
        }
    }

    public String getLocalDownloadBinVersion()
    {
        File tempFile = new File(UpdateCenterConstants.DBSCAR_DIR, "/temp");
        String[] files = tempFile.list();
        if (files != null && files.length > 0)
        {
            for (String string : files)
            {
                if (string.startsWith("Download_DBScar_"))
                {
                    return (string.substring("Download_DBScar_".length(), string.length() - 7)).replace("_", ".");
                }
            }
        }

        return null;
    }

    /**
     * 查询用户已经注册的序列号 <功能简述> <功能详细描述>
     * @author xiangyuanmao
     * @version 1.0 2012-11-9
     * @since DBS V100
     */
    public void querySerialNumerOfRegistered() 
    {

        TreeMap paraMap = new TreeMap();
        paraMap.put("productType", "DBScar");
        RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_PRODUCT, "getRegisteredProductsForPad", null, paraMap, true);
        WebServiceManager wsm = new WebServiceManager(requestParameter);

        // 获得注册结果对象
        Object object = wsm.execute().object;
        SoapObject result = null;
        List<String> serials = null;
        if (object != null && object instanceof SoapObject)
        {
            result = (SoapObject) object;

            // 取到soap对象中的第一个元素
            SoapObject so = (SoapObject) result.getProperty(0);

            // 取得服务端返回的响应code
            int code = so.hasProperty("code") ? new Integer(so.getProperty("code").toString()).intValue() : -1;
            if (code == UsercenterConstants.RESULT_SUCCESS)
            {
                serials = new ArrayList<String>();
                // 取得产品信息列表
                for (int i = 1; i < so.getPropertyCount(); i++)
                {
                    SoapObject product = (SoapObject) so.getProperty(i);
                    serials.add(product.getProperty("serialNo").toString());
                }
            }
            if (serials != null && serials.size() > 0)
            {
                application.clearRecord(Constants.SP_DEVICE_INFO_SERIAL_NUMBER);
                application.removeBluetoothMac();
                application.setSerialNumber(serials);
            }
            else
            {
                application.clearRecord(Constants.SP_DEVICE_INFO_SERIAL_NUMBER);
            }
        }
    };

    private void download(TreeMap<String, Object> paramList, RequestParameter requestParameter)
    {

        WebServiceManager wsm = new WebServiceManager(this, requestParameter);
        WSBaseResult result = wsm.executeHttpPost(new HttpDownloadListener());
        if (result != null && result.object != null && result.object instanceof File)
        {

            unzipFile((File) result.object);
        }
    }

    /**
     * 查询用户已经注册的序列号 <功能简述> <功能详细描述>
     * @author xiangyuanmao
     * @version 1.0 2012-11-9
     * @since DBS V100
     */
    public void queryDiagnoseSWIsNeedUpdate()
    {
        TreeMap paraMap = new TreeMap();
        paraMap.put("productType", "DBScar");
        RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_PRODUCT, "getRegisteredProductsForPad", null, paraMap, true);
        WebServiceManager wsm = new WebServiceManager(requestParameter);

        // 获得注册结果对象
        Object object = wsm.execute().object;
        SoapObject result = null;
        List<String> serials = null;
        if (object != null && object instanceof SoapObject)
        {
            result = (SoapObject) object;

            // 取到soap对象中的第一个元素
            SoapObject so = (SoapObject) result.getProperty(0);

            // 取得服务端返回的响应code
            int code = so.hasProperty("code") ? new Integer(so.getProperty("code").toString()).intValue() : -1;
            if (code == UsercenterConstants.RESULT_SUCCESS)
            {
                serials = new ArrayList<String>();
                // 取得产品信息列表
                for (int i = 1; i < so.getPropertyCount(); i++)
                {
                    SoapObject product = (SoapObject) so.getProperty(i);
                    serials.add(product.getProperty("serialNo").toString());
                }
            }
            application.setSerialNumber(serials);

            // 查询更新信息
            if (serials != null && serials.size() > 0)
            {
                for (String string : serials)
                {
                    paraMap.clear();
                    paraMap.put("serialNo", string);
                    paraMap.put("displayLan", language.equals("CN") ? language : "EN");
                    requestParameter = null;
                    requestParameter = new RequestParameter(Constants.SERVICE_CLASS_ONE_KEY_DIAG, "getDiagSoftMaxVersionBySerialNo", null, paraMap, true);
                    wsm = new WebServiceManager(requestParameter);
                    object = wsm.execute().object;
                    if (object != null && object instanceof SoapObject)
                    {
                        result = (SoapObject) object;

                        // 取到soap对象中的第一个元素
                        so = (SoapObject) result.getProperty(0);

                        // 取得服务端返回的响应code
                        code = so.hasProperty("code") ? new Integer(so.getProperty("code").toString()).intValue() : -1;
                        if (code == UsercenterConstants.RESULT_SUCCESS)
                        {
                            SoapObject softMaxVersion = (SoapObject) so.getProperty("SoftMaxVersion");
                            int forceUpgrade = new Integer(softMaxVersion.getPropertyAsString("forceUpgrade"));
                            int versionDetailId = new Integer(softMaxVersion.getPropertyAsString("versionDetailId"));
                            String versionNo = softMaxVersion.getPropertyAsString("versionNo");
                            if (forceUpgrade == 1)
                            {
                                if (versionDetailId < 0)
                                {
                                    throw new IllegalArgumentException("detailId 不应该是负数!");
                                }

                                if (TextUtils.isEmpty(UpdateCenterConstants.DBSCAR_DIR + "/temp"))
                                {
                                    throw new IllegalArgumentException("目标路径   targetPath 不能为空!");
                                }

                                if (D)
                                    Log.d(TAG, "从http URL 下载 诊断 文件 ..." + "明细 id: " + versionDetailId);
                                TreeMap<String,Object> map = new TreeMap<String,Object>();
                                map.put("cc", MyCarActivity.cc);// CC 号
                                map.put("versionDetailId", versionDetailId);// 明细ID
                                map.put("productSerialNo", string);// 产品序列号  
                                Log.d(TAG,"download CC:"+MyCarActivity.cc);
                                Log.d(TAG,"download versionDetailId:"+versionDetailId);
                                Log.d(TAG,"download productSerialNo:"+string);
                                String httpUrl = UpdateCenterConstants.UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL;
                                requestParameter = new RequestParameter(httpUrl, null, map);
                                requestParameter.downloadDir = UpdateCenterConstants.TEMP_DIR;//  软件下载的保存目录
                                download(map,requestParameter);
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 监听WebServiceManager的下载进度
     */
    private class HttpDownloadListener implements WebServiceManager.HttpDownloadListener
    {
        @Override
        public void onHttpDownloadStart(String url, Object params)
        {
            // notifyAboutHttpDownloadStart(url);
        }

        @Override
        public void onHttpDownloadProgress(int percent, int restHours, int restMinutes, int restSeconds)
        {
            // notifyAboutHttpDownloadProgress(percent, 0, restHours,
            // restMinutes, restSeconds);
        }

        @Override
        public void onHttpDownloadFinished(File target, Object extra)
        {
            // notifyAboutHttpDownloadFinish(target,extra);
            unzipFile(target);
        }

        @Override
        public void onHttpDownloadException(Object detail)
        {
            // notifyAboutHttpDownloadException(detail);
        }
    }

    /**
     * 解压文件
     */
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

    // 文件解压监听
    class UnzipFileListener implements ZipHelper.UnzipListener
    {
        @Override
        public void onUnzipping(final String file, final String destDir, final int percent)
        {
        }

        /**
         * zip文件解压完成
         */
        @Override
        public void onUnzipFinished(final File file, final String destFile)
        {
            String fileName = file.getName();
            String subString = "";
            if (fileName.endsWith(".zip"))
            {
                subString = fileName.substring(0, file.getName().indexOf(".zip"));
            }
            else if (fileName.endsWith(".ZIP"))
            {
                subString = fileName.substring(0, file.getName().indexOf(".ZIP"));
            }
            else
            {
                if (D)
                    Log.e(TAG, "下载错误,不是ZIP包!");
                return;
            }
            String[] sa = subString.split("_");

            if (sa.length != 5)
            {
                if (D)
                    Log.e(TAG, "版本信息解析错误");
                return;
            }

            String version = sa[2] + "." + sa[3];// 版本
            String language = sa[4];// 语言
            application.saveDownloadBinVersion(version, language);
            if (D)
                Log.d(TAG, "下载得到的Download.bin" + "版本 :" + version + "语言: " + language);
        }

        @Override
        public void onUnzipError(final File zipFile, final Object reason)
        {
        }
    }
}
