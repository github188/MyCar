package com.cnlaunch.mycar.rescuevehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.utils.FileUtils;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**
 * @description ������Ԯ������
 * @author xiangyuanmao
 */
public class RescueVehiclesActivity extends BaseActivity
{
    // ����log��Ϣtarget
    private static final String TAG = "RescueVehiclesActivity";
    private static final boolean D = true;

    private static final int WEBSERVICE_RESPONSE = 1; // �յ�WebService��Ӧ�Ĵ����ʶ
    private static final int CHANGE_LOCATION_STATE = 2; // ��λ״̬�����ı�ʱ֪ͨUI

    // ----------------------------��λ���--------------------------
    private Location mLocation;
    private LocationManager mLocationManager;
    private CustomProgressDialog pdlg; // ���ȶԻ���
    private ListView storesBriefInfo; // �ܱߵ��̵ļ�Ҫ��Ϣ
    private MerchantListAdapter merchantListAdapter; // �ܱߵ��̼�Ҫ��Ϣ������
    private int currentPage = 1; // ��ǰҳ
    private int pageSize = 10; // ÿҳ��ʾ10��
    private int pageCount = 0; // ��Ϣ��ҳ��
    private static final String LOCATION_TYPE_GPS = "gps"; // ��λ���ͣ�GPS
    private static final String LOCATION_TYPE_NETWORK = "network"; // ��λ���ͣ�NETWORK
    private static final int LOCATION_GPS_STATE = 3; // ��λ״̬��gps
    private static final int LOCATION_NETWORK_STATE = 0; // ��λ״̬��network
    private static final boolean isOpenGps = false; // �Ƿ��gps
    private boolean isOpenProgress = false; // �Ƿ�򿪽��ȿ�
    private boolean isChangeLocatin = false; // λ���Ƿ�ı�
    private static double lat;// = 22.6651594; // ����
    private static double lng;// = 114.0533771110; // γ��
    private HashMap<String, String> map;
    private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(); // list����ʾ��������Դ
    // ���綨λ��ʱ
    public Timer timer;
    private final int TIME_NORMAL = 1;
    private final int TIME_OUT = 2;
    private final int TIME_OUT_GPS = 3;
    private final int CHECK_TIME = 5000;
    private NetWorkThread networkThread;
    private Resources resources;
    CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        resources = getResources();
        // ����ͳһ�ķ�������layout�ļ�
        setContentView(R.layout.rescue_vehicles_main_layout, R.layout.custom_title);
        setCustomeTitleLeft(R.string.rv_rescue_vehicles);
        setCustomeTitleRight(R.string.rv_stores_information);
        // ��ʾ���������̼Ҽ�Ҫ��Ϣ
        storesBriefInfo = (ListView) findViewById(R.id.rv_ls_stores_briefInfo);
        storesBriefInfo.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                showStoreDetails(position);
            }
        });

        data.clear();
        merchantListAdapter = new MerchantListAdapter(this, data);
        storesBriefInfo.setAdapter(merchantListAdapter);
        storesBriefInfo.setOnScrollListener(listScrollListener);
        isOpenProgress = true;
        // ��ȡλ�ö�λ����
        if (mLocationManager == null)
        {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        // �ж��û��Ƿ���GPS
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            startProgressDialog(resources.getString(R.string.rescue_vehicle_use_gps_location));
            Log.d(TAG, "GPS�Ѿ��򿪣����ڿ�ʼ��λ...");
            // ���2�룬λ�øı�Ϊ5�׵�ʱ�����λ����Ϣ
            String locationType = "gpsLocation";
            lat = 0; // ����
            lng = 0; // γ��
            networkThread = new NetWorkThread(locationType);
            networkThread.start();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, new LocationListener());
        }
        else
        {

            // Ĭ��ʹ�����綨λ
            locationByNetWork();
        }
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        isOpenProgress = true;
    }

    @Override
    protected void onRestart()
    {
        // TODO Auto-generated method stub
        super.onRestart();
        isOpenProgress = true;
    }

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        isOpenProgress = false;
    }

    private void showStoreDetails(int position)
    {
        // �������̵���Ϣ
        HashMap<String, String> itemData = data.get(position);
        String serviceId = itemData.get("serviceId");
        // ת����ʾ���̼���ϸ��Ϣ��ҳ��
        Intent intent = new Intent(RescueVehiclesActivity.this, AroundStoresDetailInfoActivity.class);
        intent.putExtra("serviceId", serviceId);
        startActivity(intent);
    }

    // ���ڸ���UI�߳�mHandler
    private final Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case WEBSERVICE_RESPONSE: // �յ�����WebService����Ӧ����
                    stopProgressDialog(); // �رս��ȶԻ���
                    merchantListAdapter.notifyDataSetChanged(); // ������ʾ�б�
                    break;
                case CHANGE_LOCATION_STATE: // �л����ֶ�λ����
                    String text = "GPS" + resources.getString(R.string.rescue_vehicle_location); // ��λ�ؼ��ϵ���ʾ�ı�
                    switch (msg.arg1)
                    {
                        case LOCATION_GPS_STATE: // GPS״̬�����ı�

                            // ���GPS����
                            if (isLocationEnable(LOCATION_TYPE_GPS))
                            {
                                text = resources.getString(R.string.rescue_vehicle_close) + "GPS"; // GPS���ؿؼ��漴��ʾΪ�ر�GPS
                                // �ж��û��Ƿ���GPS
                                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                {
                                    startProgressDialog(resources.getString(R.string.rescue_vehicle_use_gps_location));
                                    Log.d(TAG, "GPS�Ѿ��򿪣����ڿ�ʼ��λ...");
                                    // ���2�룬λ�øı�Ϊ5�׵�ʱ�����λ����Ϣ
                                    String locationType = "gpsLocation";
                                    lat = 0; // ����
                                    lng = 0; // γ��
                                    networkThread = new NetWorkThread(locationType);
                                    networkThread.start();
                                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, new LocationListener());
                                }
                                // �ж�GPS�豸�Ƿ����
                                if (mLocation == null)
                                {
                                    Toast.makeText(RescueVehiclesActivity.this, resources.getString(R.string.rescue_vehicle_gps_can_get_location), Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            // ���GPS�ر�

                            {
                                text = resources.getString(R.string.rescue_vehicle_gps_location);
                            }

                            break;
                        case LOCATION_NETWORK_STATE: // ���綨λ�����ı�
                            if (isLocationEnable(LOCATION_TYPE_NETWORK))
                            {
                                locationByNetWork();
                            }
                            break;

                    }
                    ((TextView) msg.obj).setText(text);
                    break;
            }
        }
    };

    /**
     * �Զ����б�����������ʾ�̼��б�
     * @author xiangyuanmao
     */
    private class MerchantListAdapter extends BaseAdapter
    {
        private Context context; // ������
        private LayoutInflater layoutInflater; // ������
        private List<HashMap<String, String>> data;// ����Դ

        /**
         * ���췽��
         * @param context
         * @param data
         */
        private MerchantListAdapter(Context context, List<HashMap<String, String>> data)
        {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * ���ÿһ��������Ϣ
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            // TODO Auto-generated method stub
            convertView = layoutInflater.inflate(R.layout.rescue_vehicles_storesinfo_list_item, null);
            TextView tvStoreName = (TextView) convertView.findViewById(R.id.tv_store_name);
            TextView tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
            TextView tvTelephone = (TextView) convertView.findViewById(R.id.tv_telephone);
            ImageView ivSendCRecorder = (ImageView) convertView.findViewById(R.id.iv_send_crecorder);
            tvStoreName.setText(data.get(position).get("companyName"));
            tvDistance.setText(data.get(position).get("distance"));
            tvTelephone.setText(data.get(position).get("phone"));

            final int itemId = position;
            ivSendCRecorder.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    showStoreDetails(itemId);
                }
            });

            return convertView;
        }
    }

    class DownloadListner implements FileUtils.FileDownloadListener
    {

        @Override
        public void onFinished()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgress(int percent)
        {
            // TODO Auto-generated method stub

        }
    }

    /**
     * �����¼�����
     */
    private OnScrollListener listScrollListener = new ListView.OnScrollListener()
    {
        @Override
        public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount <= 0)
            {
                return;
            }

            Log.d(TAG, "visibleItemCount..." + visibleItemCount);
            Log.d(TAG, "totalItemCount..." + totalItemCount);
            Log.d(TAG, "firstVisibleItem..." + firstVisibleItem);
            if (firstVisibleItem + visibleItemCount == totalItemCount && pageCount > currentPage)
            {
                Log.d(TAG, "��ʼ������һҳ����...");
                startProgressDialog(resources.getString(R.string.rescue_vehicle_searching_around_store));
                new RequestDataThread(++currentPage, pageSize, new ServiceMerchantCondition("" + lat, "" + lng)).start();
            }
            else if (pageCount == currentPage && isChangeLocatin)
            {
                isChangeLocatin = false;
                // dialog("�����Ѿ����");
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
        }
    };

    /**
     * ����WebService�����ȡ�ܱ���Ϣ
     * @author xiangyuanmao
     */
    private class RequestDataThread extends Thread
    {
        private int pageNo; // ����ҳ��
        private int pageSize; // ÿҳ��ʾ��¼����
        private ServiceMerchantCondition serviceMerchantCondition; // ��ѯ����

        public RequestDataThread(int pageNo, int pageSize, ServiceMerchantCondition serviceMerchantCondition)
        {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.serviceMerchantCondition = serviceMerchantCondition;
        }

        @Override
        public void run()
        {

            // ��װ�������
            TreeMap paraMap = new TreeMap<String, Integer>();
            paraMap.put("pageNo", this.pageNo);
            paraMap.put("pageSize", this.pageSize);
            paraMap.put("condition", serviceMerchantCondition);
            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_RESCUE_VEHICLES, "queryRescueMerchants", null, paraMap, true);
            WebServiceManager webServiceManager = new WebServiceManager(RescueVehiclesActivity.this, requestParameter);
            WSBaseResult wSBaseResult = webServiceManager.execute();

            // ��ӡ����ȡ���Ľ��
            if (wSBaseResult.object != null && wSBaseResult.object instanceof SoapObject)
            {
                SoapObject object = (SoapObject) wSBaseResult.object; // ��õ�һ��SoapObject����
                SoapObject object2 = (SoapObject) object.getProperty(0); // ��õڶ���SoapObject����
                Log.d(TAG, object2.toString());
                if (object2 != null)
                {
                    // ҳ��
                    SoapObject page = object2.hasProperty("page") ? (SoapObject) object2.getProperty("page") : null;
                    // �ܼ�¼��
                    String sizeStr = page.hasProperty("size") ? page.getProperty("size").toString() : "";

                    // ��ȡ����ҳ��
                    pageCount = (Integer.parseInt(sizeStr)) / pageSize + 1;

                    // ��dataList����ȡ���ܱߵ��̵���Ϣ
                    SoapObject dataList = page.hasProperty("dataList") ? (SoapObject) page.getProperty("dataList") : null;
                    if (dataList != null && dataList.getPropertyCount() > 0)
                    {
                        for (int i = 0; i < dataList.getPropertyCount(); i++)
                        {
                            map = new HashMap<String, String>();
                            map.put("companyName", ((SoapObject) dataList.getProperty(i)).getProperty("companyName").toString());
                            map.put("companyAddress", ((SoapObject) dataList.getProperty(i)).getProperty("companyAddress").toString());
                            map.put("distance", ((SoapObject) dataList.getProperty(i)).getProperty("distance").toString());
                            map.put("gprsX", ((SoapObject) dataList.getProperty(i)).getProperty("gprsX").toString());
                            map.put("gprsY", ((SoapObject) dataList.getProperty(i)).getProperty("gprsY").toString());
                            map.put("phone", ((SoapObject) dataList.getProperty(i)).getProperty("phone").toString());
                            map.put("serviceId", ((SoapObject) dataList.getProperty(i)).getProperty("serviceId").toString());
                            data.add(map);
                        }
                    }
                }
            }

            // ֪ͨlist���ݸ�����ɣ�������ʾ��
            mHandler.obtainMessage(WEBSERVICE_RESPONSE, 0, 0, null).sendToTarget();
        }
    };

    /**
     * ��Ӧ�ײ��˵���ť�¼�
     * @param v
     */
    public void MenuButton_ClickHandler(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_gps_location:
                // ʹ��GPS��λ
                TextView tv = (TextView) findViewById(R.id.tv_gps_location);
                toggle(tv, LOCATION_GPS_STATE);
                break;
            case R.id.btn_network_location:
                locationByNetWork();
                break;
            case R.id.btn_search:
                searchStores(1);
                break;
            case R.id.btn_emergency_phone:
                startActivity(new Intent(this, EmergencyPhoneActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * �����������
     * @return
     */
    private boolean checkNetWork()
    {
        // �����������
        ConnectivityManager mConnectivityManager = (ConnectivityManager) RescueVehiclesActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    /**
     * �����Ի���
     * @param message
     * @author xiangyuanmao
     */
    protected void dialog(String message)
    {
        if (customDialog == null)
        {
            customDialog = new CustomDialog(this);
        }
        if (isOpenProgress)
        {
            customDialog.setMessage(message); // ������Ϣ
            customDialog.setTitle(resources.getString(R.string.uc_notice));
            customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    customDialog.dismiss();
                }

            });
            customDialog.setTitle(resources.getString(R.string.uc_notice));
            customDialog.show();

        }
    }

    /**
     * ʹ�����綨λ
     */
    private void locationByNetWork()
    {

        // �����������״̬
        if (!checkNetWork())
        {
            // ������粻ͨ��ʾ�û�������������
            dialog(resources.getString(R.string.usercenter_netword_not_connect));
            Log.d(TAG, "����δ����");
        }
        else
        {
            // ������綨λ�Ƿ����
            if (isLocationEnable(LOCATION_TYPE_NETWORK))
            {
                Log.d(TAG, "����ʹ�����綨λ��ȡλ����Ϣ...");
                startProgressDialog(resources.getString(R.string.rescue_vehicle_network_locationing));
                CellInfoManager cellManager = new CellInfoManager(RescueVehiclesActivity.this);
                WifiInfoManager wifiManager = new WifiInfoManager(RescueVehiclesActivity.this);
                CellLocationManager locationManager = new CellLocationManager(RescueVehiclesActivity.this, cellManager, wifiManager)
                {
                    @Override
                    public void onLocationChanged()
                    {

                        isChangeLocatin = true;
                        lat = this.latitude(); // γ��
                        lng = this.longitude(); // ����
                        stopProgressDialog(); // �����Ի���
                        String message = resources.getString(R.string.rescue_vehicle_current_location_lat) + lat + resources.getString(R.string.rescue_vehicle_current_location_lng) + lng
                            + resources.getString(R.string.rescue_vehicle_is_search_around_store);
                        showLatAndLng(message);
                        // Toast.makeText(RescueVehiclesActivity.this, "����ǰλ�ã�"
                        // + " ���ȣ�" + lat + ", γ�ȣ�"+lng,
                        // Toast.LENGTH_LONG).show();
                        Log.d(TAG, "λ�ã�" + "����" + lat + ",γ��" + lng);
                        // searchStores(1);
                        this.stop();
                    }
                };
                locationManager.start();
                String locationType = "netLocation";
                networkThread = new NetWorkThread(locationManager, locationType);
                networkThread.start();
            }
            else
            {
                dialogSet(resources.getString(R.string.rescue_vehicle_not_open_newwork_loaction));
                Log.d(TAG, "û�п������綨λ���ܣ�");
            }
        }
    }

    protected void dialogSet(String message)
    {
        if (isOpenProgress)
        {
            if (customDialog == null)
            {
                customDialog = new CustomDialog(this);
            }
            customDialog.setMessage(message); // ������Ϣ
            customDialog.setTitle(resources.getString(R.string.uc_notice));
            customDialog.setPositiveButton(resources.getString(R.string.rescue_vehicle_go_setting), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent("/");
                    ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.SecuritySettings");
                    intent.setComponent(cm);
                    intent.setAction("android.intent.action.VIEW");
                    startActivityForResult(intent, 0);
                    customDialog.dismiss();
                }

            });
            customDialog.setNegativeButton(resources.getString(R.string.cancel), new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    customDialog.dismiss();

                }
            });
            customDialog.show();

        }
    }

    private Handler localnetworkHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TIME_OUT:

                    dialog(resources.getString(R.string.rescue_vehicle_fail_network_location));
                    stopProgressDialog();
                    break;
                case TIME_OUT_GPS:
                    dialog(resources.getString(R.string.rescue_vehicle_fail_gps_location));
                    stopProgressDialog();
                    break;

            }

            super.handleMessage(msg);
        }
    };

    /*
     * ���߶�λ�߳�
     */
    class NetWorkThread extends Thread
    {
        CellLocationManager locationManager = null;
        String locationType;

        public NetWorkThread(String locationType)
        {
            this.locationType = locationType;
        }

        public NetWorkThread(CellLocationManager locationManager, String locationType)
        {
            this.locationManager = locationManager;
            this.locationType = locationType;
        }

        private boolean flag = true;

        @Override
        public void run()
        {
            Message msg = new Message();
            msg.what = TIME_NORMAL;
            try
            {
                this.sleep(60 * 1000);

                if ((lat == 0 || lng == 0) && locationType.equals("netLocation"))
                {
                    msg.what = TIME_OUT;
                    if (locationManager != null)
                        locationManager.stop();
                }
                else if ((lat == 0 || lng == 0) && locationType.equals("gpsLocation"))
                {
                    msg.what = TIME_OUT_GPS;
                    TextView tv = (TextView) findViewById(R.id.tv_gps_location);
//                    toggle(tv, LOCATION_GPS_STATE);
                }
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            localnetworkHandler.sendMessage(msg);
        }
    }

    private void searchStores(int currentPage)
    {
        data.clear();
        if (lat == 0 && lng == 0)
        {
            locationByNetWork();
        }
        else
        {

            startProgressDialog(resources.getString(R.string.rescue_vehicle_searching_around_store));
            new RequestDataThread(this.currentPage = currentPage, pageSize, new ServiceMerchantCondition("" + lat, "" + lng)).start();
        }
    }

    /**
     * �رս��ȶԻ���
     */
    private void stopProgressDialog()
    {
        if (D)
            Log.d(TAG, "stopProgressDialog come in ");
        if (pdlg == null)
        {
            if (D)
                Log.d(TAG, "pdlg == null ");
            return;
        }
        else
        {
            if (D)
                Log.d(TAG, pdlg.toString());
            if (pdlg.isShowing())
            {
                if (D)
                    Log.d(TAG, "pdlg is showing");
                pdlg.dismiss();
            }
        }
    }

    /**
     * �������ȶԻ���
     * @param pdlg
     * @param message
     */

    private void startProgressDialog(String message)
    {
        if (isOpenProgress)
        {
            if (pdlg == null)
            {
                // ʵ����һ�����ȿ�
                pdlg = new CustomProgressDialog(this);
                pdlg.setTitle(resources.getString(R.string.notice));
                pdlg.setStyle(true);
            }
            else if (pdlg.isShowing())
            {
                pdlg.dismiss();
            }
            pdlg.setMessage(message);
            pdlg.show();
        }
    }

    private boolean isLocationEnable(String locationType)
    {
        // ��Setting.System����ȡҲ���ԣ�ֻ�����Ǹ��ɵ��÷�
        String str = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (D)
            Log.d(TAG, "GPS " + str);
        if (str != null)
        {
            return str.contains(locationType);
        }
        else
        {
            return false;
        }
    }

    class ChanageLocationStateThread extends Thread
    {
        private int locationCode;
        private View v;

        public ChanageLocationStateThread(int locationCode, View v)
        {
            this.v = v;
            this.locationCode = locationCode;
        }

        public void run()
        {
            try
            {
                sleep(1 * 1000);
                mHandler.obtainMessage(CHANGE_LOCATION_STATE, this.locationCode, -1, v).sendToTarget();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
    };

    private void openLocation(int locationCode)
    {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory("android.intent.category.ALTERNATIVE");
        intent.setData(Uri.parse("custom:" + locationCode));
        try
        {
            PendingIntent.getBroadcast(this, 0, intent, 0).send();
            // ��ȡλ�ö�λ����
            if (mLocationManager == null)
            {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }
        }
        catch (CanceledException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * GPS���أ���gpsδ����������֮�ر�
     * @param v
     */
    private void toggle(View v, int locationCode)
    {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            startProgressDialog(resources.getString(R.string.rescue_vehicle_use_gps_location));
            Log.d(TAG, "GPS�Ѿ��򿪣����ڿ�ʼ��λ...");
            // ���2�룬λ�øı�Ϊ5�׵�ʱ�����λ����Ϣ
            String locationType = "gpsLocation";
            lat = 0; // ����
            lng = 0; // γ��
            networkThread = new NetWorkThread(locationType);
            networkThread.start();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, new LocationListener());
        }
        else
        {
            dialogSet(resources.getString(R.string.rescue_vehicle_not_open_gps_loaction));
        }
    }



    protected void showLatAndLng(String message)
    {

        if (customDialog == null)
        {
            customDialog = new CustomDialog(this);
        }
        if (isOpenProgress)
        {

            customDialog.setMessage(message); // ������Ϣ
            customDialog.setTitle(resources.getString(R.string.uc_notice));
            customDialog.setPositiveButton(R.string.ok, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    searchStores(1);
                    customDialog.dismiss();
                }
            });
            customDialog.setNegativeButton(R.string.cancel, new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    customDialog.dismiss();

                }
            });
            customDialog.show();
        }
    }

    // ����λ�øı䴥��
    class LocationListener implements android.location.LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            stopProgressDialog();
            lng = location.getLongitude(); // ����
            lat = location.getLatitude(); // γ��
            String message = resources.getString(R.string.rescue_vehicle_current_location_lat) + lat + resources.getString(R.string.rescue_vehicle_current_location_lng) + lng
                + resources.getString(R.string.rescue_vehicle_is_search_around_store);

            showLatAndLng(message);
            // Toast.makeText(RescueVehiclesActivity.this, "���ȣ�" + lng + "γ�ȣ�" +
            // lat,
            // Toast.LENGTH_LONG).show();
            Log.e(TAG, "���ȣ�" + lng + "γ�ȣ�" + lat);
            mLocation = location;

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            // TODO Auto-generated method stub
        }
    }

}
