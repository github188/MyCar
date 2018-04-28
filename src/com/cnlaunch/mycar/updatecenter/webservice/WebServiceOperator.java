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
 * web service SOAP ����
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
     * ע���Ʒ,ע�������˳��
     * @param serialNo ���к�
     * @param chipId оƬID
     * @param bluetoothMac ������ַ
     * @param longitude ����
     * @param latitude γ��
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
     * ���кźϷ���֤
     * @param serialNum ���к�
     * @param chipId ���кŶ�Ӧ������
     */
    public void chekcSerialNumber(final String serialNum, final String chipId)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getProductRegistrationURL(), SoapMethod.CHECK_SERIAL_NUMBER, true, TIME_OUT);

        request.setParam("serialNo", serialNum).setParam("chipID", chipId);
        postRequest(request);
    }

    /**
     * ��ȡʣ��Ŀ����ô���
     * @param serialNum ���к�
     */
    public void getRemainingConfigCount(final String serialNum)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.QUERY_REMAINING_CONFIGURABLE_COUNT, true, TIME_OUT);

        request.setParam("serialNo", serialNum);
        postRequest(request);
    }

    /**
     * ��ѯ�ͻ��˵�������Ϣ
     * @param currentVersion ��ǰ�汾
     */
    public void queryApkUpdateInfo(String currentVersion)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getPublicSoftURL(), SoapMethod.QUERY_APK_UPDATE_INFO, false, TIME_OUT);

        request.setParam("mobilePlatform", UpdateCenterConstants.PLATFORM_ANDROID).setParam("versionNo", currentVersion);
        postRequest(request);
    }

    /**
     * ��ѯ�豸Download.bin��������Ϣ
     * @param serialNum �豸���к�
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
     * ��ѯ�豸����ʷ������Ϣ
     * @param serialNum ���к�
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
     * ����VIN SN ��ѯ��ϵ�б�
     * @param vin VIN ��
     * @param serialNum ���к�
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
     * ��ʼѡ������
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
     * ��ѯ���֧�ֵ������б�
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
     * û���յ�ֹͣ��־λʱ,������������
     * @param conditionId ����id
     * @param selectedValue ѡ���ֵ
     */
    public void resumeConfigurate(String conditionId, String selectedValue)
    {
        SoapRequest request = new SoapRequest(DBSURLManager.getDiagConfigURL(), SoapMethod.ONE_KEY_DIAG_CALC, true, TIME_OUT);

        request.setType(com.cnlaunch.mycar.common.config.Constants.SERVICE_CLASS_ONE_KEY_DIAG).setParam("conditionId", conditionId).setParam("selectedValue", selectedValue);
        postRequest(request);
    }

    // ��������
    private void postRequest(SoapRequest request)
    {
        if (!assureNetworkIsActive())// �������Ӽ��
        {
            notifyAboutWebServiceError(Error.NETWORK_NOT_AVAILABLE, request);
            return;
        }
        Executor executor = new Executor(context, request);
        executor.start();
    }

    /**
     * ִ���������
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

            Log.d(TAG, "soap ����: " + request.toString());

            wsRequest.wsUrl = request.getUrl();

            WebServiceManager wsm = new WebServiceManager(context, wsRequest);

            class TimeoutListener implements TimeoutCounter.Callback
            {
                @Override
                public void onTimeout()
                {
                    timeoutHappened = true;
                    Log.e(TAG, ":)))))))----->>>>����" + request.toString() + "��ʱ!");
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

            WSBaseResult result = wsm.execute();// ���������
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
                public void onStart()// ��ʼת��
                {
                }

                @Override
                public void onError(int code, Object err)// ת������
                {
                    notifyAboutWebServiceError(code, request);
                }

                @Override
                public void onConvertResult(SoapResponse resp)// ת�����
                {
                    notifyAboutWebServiceSuccess(resp);
                }
            });
            soapRespHandler.convert(wsRet);// ��SoapObjectת��Ϊ��Ӧ��ҵ�����
        }
    }
}
