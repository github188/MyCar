package com.cnlaunch.mycar.updatecenter.webservice;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.tools.NetworkChecker;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;

/**
 * web service SOAP 服务
 */
public class WebServiceOperator
{
    private boolean D = true;
    private final static String TAG = "WebServiceWrapper";
    private final static int TIME_OUT = 10;
    private ArrayList<WebServiceListener> listeners = new ArrayList<WebServiceListener>();
    private Context context;

    public static class Error
    {
        public final static int TIMEOUT = 1;
        public final static int NULL_POINTER = 2;
        public final static int SOAP_FAULT = 3;
        public final static int NOT_SOAP_INSTANCE = 4;
        public final static int NO_SUCH_PROPERTY = 5;
        public final static int NETWORK_NOT_AVAILABLE = 6;
        public final static int NETWORK_IO_ERROR = 7;
        public final static int SOAP_XML_PARSE_ERROR = 8;
    };

    public WebServiceOperator(Context c)
    {
        this.context = c;
    }

    private boolean assureNetworkIsActive()
    {
        return NetworkChecker.isConnected(context);
    }

    synchronized public void addListener(WebServiceListener listener)
    {
        if (listener != null)
        {
            listeners.add(listener);
        }
    }

    synchronized public void removeListener(WebServiceListener listener)
    {
        if (listener != null)
        {
            listeners.remove(listener);
        }
    }

    synchronized public void removeAllListeners()
    {
        listeners.clear();
    }

    synchronized void notifyAboutStartWebServiceRequest(SoapRequest rq)
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            WebServiceListener l = listeners.get(i);
            l.onStartWebServiceRequest(this, rq);
        }
    }

    synchronized void notifyAboutWebServiceSuccess(SoapResponse response)
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            WebServiceListener l = listeners.get(i);
            l.onWebServiceSuccess(this, response);
        }
    }

    synchronized void notifyAboutWebServiceError(int code, SoapRequest rq)
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            WebServiceListener l = listeners.get(i);
            l.onWebServiceErrors(this, code, rq);
        }
    }

    /**
     * 注册产品,注意参数的顺序
     * @param serialNo 序列号
     * @param chipId 芯片ID
     * @param bluetoothMac 蓝牙地址
     * @param longitude 经度
     * @param latitude 纬度
     */
    public void registerProduct(final String serialNo, final String chipId, final String bluetoothMac, final String longitude, final String latitude)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getProductRegistrationURL(), SoapMethod.REGISTER_PRODUCT, true, TIME_OUT);

        request.setParam("serialNo", serialNo).setParam("chipID", chipId).setParam("bluetoothMAC", bluetoothMac).setParam("longitude", longitude).setParam("latitude", latitude);
        if (D)
            Log.i(TAG, "serialNo:" + serialNo + "chipId:" + chipId + "bluetoothMac:" + bluetoothMac + "longitude:" + longitude + "latitude:" + latitude);
        postRequest(request);
    }

    /**
     * 序列号合法验证
     * @param serialNum 序列号
     * @param chipId 序列号对应的密码
     */
    public void chekcSerialNumber(final String serialNum, final String chipId)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getProductRegistrationURL(), SoapMethod.CHECK_SERIAL_NUMBER, true, TIME_OUT);

        request.setParam("serialNo", serialNum).setParam("chipID", chipId);
        postRequest(request);
    }

    /**
     * 获取剩余的可配置次数
     * @param serialNum 序列号
     */
    public void getRemainingConfigCount(final String serialNum)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_REMAINING_CONFIGURABLE_COUNT, true, TIME_OUT);

        request.setParam("serialNo", serialNum);
        postRequest(request);
    }

    /**
     * 查询客户端的升级信息
     * @param currentVersion 当前版本
     */
    public void queryApkUpdateInfo(String currentVersion)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getPublicSoftURL(), SoapMethod.QUERY_APK_UPDATE_INFO, false, TIME_OUT);

        request.setParam("mobilePlatform", UpdateCenterConstants.PLATFORM_ANDROID).setParam("versionNo", currentVersion);
        postRequest(request);
    }

    /**
     * 查询设备Download.bin的升级信息
     * @param serialNum 设备序列号
     */
    public void queryBinFileUpdateInfo(String cc, String productSerialNo, String versionNo, String displayLan)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getPublicSoftURL(), SoapMethod.QUERY_BIN_FILE_UPDATE_INFO, false, TIME_OUT);

        request.setParam("cc", cc);
        request.setParam("productSerialNo", productSerialNo);
        request.setParam("versionNo", versionNo);
        request.setParam("displayLan", displayLan);
        postRequest(request);
    }

    /**
     * 查询设备的历史配置信息
     * @param serialNum 序列号
     */
    public void queryDeviceHistoricalConfig(String serialNum)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_HISTORICAL_CONFIG_INFO, true, TIME_OUT);

        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG).setParam("serialNo", serialNum);
        postRequest(request);
    }

    public void queryLatestDiagSofts(String serialNo, int lanId, int defaultLanId)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_LATEST_DIAG_SOFTS, true, TIME_OUT);
        request.setParam("serialNo", serialNo);
        request.setParam("lanId", lanId);
        request.setParam("defaultLanId", defaultLanId);
        postRequest(request);
    }
    /**
     * 根据VIN SN 查询车系列表
     * @param vin VIN 码
     * @param serialNum 序列号
     */
    public void queryCarBrandListByVIN(String vin, String serialNum)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_CAR_BRAND_LIST_BY_VIN, true, TIME_OUT);
        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG)
        .setParam("VIN", vin)
        .setParam("serialNo", serialNum)
        .setParam("displayLan", Env.GetCurrentLanguage());
        postRequest(request);
    }

    /**
     * 开始选择配置
     * @param carBrandId
     * @param vin
     * @param serialNum
     */
    public void beginConfigurate(String carBrandId, String vin, String serialNum)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.BEGIN_ONE_KEY_CALC, true, TIME_OUT);

        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG).setParam("carBrandId", carBrandId).setParam("VIN", vin).setParam("serialNo", serialNum);
        postRequest(request);
    }

    /**
     * 查询软件支持的语言列表
     * @param carBrandId
     * @param lang
     */
    public void queryLanguageList(String carBrandId, String lang)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_DIAG_SOFT_LANGUAGE_LIST, true, TIME_OUT);

        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG).setParam("carBrandId", carBrandId).setParam("displayLan", lang);
        postRequest(request);
    }

    /**
     * 没有收到停止标志位时,继续进行配置
     * @param conditionId 条件id
     * @param selectedValue 选项的值
     */
    public void resumeConfigurate(String conditionId, String selectedValue)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.ONE_KEY_DIAG_CALC, true, TIME_OUT);

        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG).setParam("conditionId", conditionId).setParam("selectedValue", selectedValue);
        postRequest(request);
    }

    // 发送请求
    private void postRequest(SoapRequest request)
    {
        if (!assureNetworkIsActive())// 网络连接检查
        {
            notifyAboutWebServiceError(Error.NETWORK_NOT_AVAILABLE, request);
            return;
        }
        Executor executor = new Executor(context, request);
        executor.start();
    }

    /**
     * 执行请求操作
     * @author luxingsong
     */
    class Executor extends Thread
    {
        SoapRequest request;
        Context context;
        boolean timeoutHappened = false;
        boolean executeErrorHappened = false;

        public Executor(Context ctx, SoapRequest rq)
        {
            this.request = rq;
            this.context = ctx;
        }

        public void run()
        {
            // steps:
            RequestParameter wsRequest = new RequestParameter(request.getType(), request.getMethod(), request.getAction(), /*
                                                                                                                            * action
                                                                                                                            * usually
                                                                                                                            * null
                                                                                                                            */
            (TreeMap<String, Object>) request.getParamList(), request.getSignFlag());

            Log.d(TAG, "soap 请求: " + request.toString());

            wsRequest.wsUrl = request.getUrl();

            WebServiceManager wsm = new WebServiceManager(context, wsRequest);

            class TimeoutListener implements TimeoutCounter.Callback
            {
                @Override
                public void onTimeout()
                {
                    timeoutHappened = true;
                    Log.e(TAG, ":)))))))----->>>>请求" + request.toString() + "超时!");
                    notifyAboutWebServiceError(Error.TIMEOUT, request);
                }

            }
            final TimeoutCounter timeout = new TimeoutCounter(request.getTimeout(), new TimeoutListener());
            // start post request
            notifyAboutStartWebServiceRequest(request);

            wsm.setExecuteListener(new WebServiceManager.OnWebSeriveExecuteListener()
            {

                @Override
                public void onStart()
                {
                }

                @Override
                public void onResult(WSBaseResult result)
                {
                }

                @Override
                public void onExceptions(int error_code, String detail)
                {
                    executeErrorHappened = true;
                    if (error_code == WebServiceManager.OnWebSeriveExecuteListener.ERROR_IO)
                    {
                        notifyAboutWebServiceError(WebServiceOperator.Error.NETWORK_IO_ERROR, request);
                    }
                    if (error_code == WebServiceManager.OnWebSeriveExecuteListener.ERROR_XML_PARSING)
                    {
                        notifyAboutWebServiceError(WebServiceOperator.Error.SOAP_XML_PARSE_ERROR, request);
                    }
                }
            });

            WSBaseResult result = wsm.execute();// 这里会阻塞
            if (result == null)
            {
                return;
            }

            Object wsRet = result.object;

            if (executeErrorHappened)
            {
                return;
            }

            if (timeoutHappened)
            {
                return;
            }

            if (timeout != null)
            {
                timeout.cancel();
            }

            SoapResponseHandler soapRespHandler = new SoapResponseHandler(context, wsRet, request.getMethod(), new OnSoapObjectConvertListener()
            {
                @Override
                public void onStart()// 开始转换
                {
                }

                @Override
                public void onError(int code, Object err)// 转换出错
                {
                    notifyAboutWebServiceError(code, request);
                }

                @Override
                public void onConvertResult(SoapResponse resp)// 转换完成
                {
                    notifyAboutWebServiceSuccess(resp);
                }
            });
            soapRespHandler.convert(wsRet);// 将SoapObject转换为响应的业务对象
        }
    }
}
