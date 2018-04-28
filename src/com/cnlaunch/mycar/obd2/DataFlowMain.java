package com.cnlaunch.mycar.obd2;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

import launch.obd2.OBD2SearchIdUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueCallback;
import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueStateEvent;
import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.ui.EnginePointer;
import com.cnlaunch.mycar.common.ui.FuelAnalysisBar;
import com.cnlaunch.mycar.common.ui.PowerPointer;
import com.cnlaunch.mycar.common.ui.ScalePlate;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.obd2.model.DataFlowModel;
import com.cnlaunch.mycar.obd2.model.DataFlowObserver;
import com.cnlaunch.mycar.obd2.model.MsgIds;
import com.cnlaunch.mycar.obd2.model.MsgQueue;
import com.cnlaunch.mycar.obd2.util.Command;
import com.cnlaunch.mycar.obd2.util.DataExchange;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

public class DataFlowMain extends TabActivity implements BluetoothInterface, BlueCallback
{

    // 调试用，设置false为了关闭蓝牙弹窗直接进入界面，发布必须设置true
    private static final boolean S = true;
    private final static String TAG = "DataFlowMain";
    /***/
    // 调试用，设置false为了关闭log信息，发布最好设置false
    private static final boolean D = false;
    private ProgressDialog progressDialog;
    // private ProgressDialog progressDialog1;
    Command command;
    final Handler handler = new Handler();
    private BluetoothDataService m_blue_data_service = null;
    ConditionVariable next = new ConditionVariable(false);
    private Context context = DataFlowMain.this;
    boolean isOpenedBluetoothConnecteDialog = false;
    /**/

    private WakeLock mWakeLock;
    private ScalePlate scalePlate_data1;
    private ScalePlate scalePlate_data2;
    private ScalePlate scalePlate_data3;
    private ScalePlate scalePlate_data_horizontal_1;

    private EnginePointer engine_pointer_1;
    private ScalePlate scalePlate_data_horizontal_engine_1;
    private ScalePlate scalePlate_data_horizontal_engine_2;
    private ScalePlate scalePlate_data_horizontal_engine_3;
    private ScalePlate scalePlate_data_horizontal_engine_4;

    private EnginePointer engine_pointer_2;
    private ScalePlate scalePlate_data_horizontal_engine_5;
    private ScalePlate scalePlate_data_horizontal_engine_6;
    private ScalePlate scalePlate_data_horizontal_engine_7;
    private ScalePlate scalePlate_data_horizontal_engine_8;

    private ScalePlate scalePlate_data_oil;
    private FuelAnalysisBar fuel_analysis_bar;

    private TextView textview_data1_value;
    private TextView textview_data2_value;
    private TextView textview_data3_value;
    private TextView textview_data_value_carspeed;
    private TextView textview_data_value_engine_speed;

    private TextView textview_oil_data1_value;
    private TextView textview_oil_data2_value;
    private TextView textview_oil_data3_value;
    private TextView textview_oil_data4_value;
    private TextView textview_oil_data5_value;

    private TextView textview_engine_data1_value;
    private TextView textview_engine_data2_value;
    private TextView textview_engine_data3_value;
    private TextView textview_engine_data4_value;

    private TextView textview_engine_data5_value;
    private TextView textview_engine_data6_value;
    private TextView textview_engine_data7_value;
    private TextView textview_engine_data8_value;

    private TextView textview_engine_data9_value;
    private TextView textview_engine_data10_value;
    private TextView textview_engine_data11_value;

    private PowerPointer powerPointer_carspeed;

    // 手势
    GestureDetector mGestureDetector;
    private MyOnGestureListener myOnGestureListener;

    private DataFlowModel dataFlowModel;
    private DataFlowObserver mDataFlowObserverForTabInstrument;
    private DataFlowObserver mDataFlowObserverForTabOil;
    private DataFlowObserver mDataFlowObserverForTabEngine1;
    private DataFlowObserver mDataFlowObserverForTabEngine2;

    // private Obd2DiagnoseServiceManager mDiagnoseServerManager;
    private TabHost mTabHost;
    private CustomTabWidget[] mCustomTabWidgets;

    OBD2SearchIdUtils searchIdUtils;
    ProgressDialog progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dataflow_main);

        acquireWakeLock();
        searchIdUtils = new OBD2SearchIdUtils(context);
        // 初始化蓝牙服务
        m_blue_data_service = BluetoothDataService.getInstance();
        dataFlowModel = DataFlowModel.getModel();
        // mDiagnoseServerManager = new Obd2DiagnoseServiceManager(context);
        initTabHost();
        findView();
        registerOnGestureListener();
        /***/

        command = new Command(m_blue_data_service);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(this.getString(R.string.intermediary_activity_init));
        if (S)
        {
            new StopProgressDialog().start();
            if (D)
                Log.i("progressDialog", "progressDialog.show();");
            if (m_blue_data_service.IsConnected())
            {
                if (!progressDialog.isShowing())
                {
                    progressDialog.show();
                }
            }
        }
    }

    @Override
    protected void onStart()
    {
        /****/
        m_blue_data_service.AddObserver(this);
        /*****/
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        /****/
        m_blue_data_service.DelObserver(this);
        /****/
        super.onStop();
        try
        {
            analysisFuel.setEnd();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            popErrorInfo(e.toString(),e);
        }
        releaseWakeLock();
    }

    /**
     * 请求锁：防屏幕关闭
     */
    private void acquireWakeLock()
    {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (mWakeLock != null)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "com.cnlaunch.mycar.obd2.DataFlowMain");
        mWakeLock.acquire();
    }

    private void releaseWakeLock()
    {
        if (mWakeLock != null)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    private Runnable mOnServiceConnected = new Runnable()
    {
        @Override
        public void run()
        {
            // mDiagnoseServerManager.prepareValue(mDataFlows_tab_current);
        }
    };

    /**
     * 注册手势
     */
    private void registerOnGestureListener()
    {
        myOnGestureListener = new MyOnGestureListener(this)
        {
            private Animation leftIn = new TranslateAnimation(-Env.getScreenWidth(context), 0, 0, 0);
            private Animation rightOut = new TranslateAnimation(0, Env.getScreenWidth(context), 0, 0);
            private Animation leftOut = new TranslateAnimation(0, -Env.getScreenWidth(context), 0, 0);
            private Animation rightIn = new TranslateAnimation(Env.getScreenWidth(context), 0, 0, 0);

            @Override
            public void flingLeft()
            {

                rightIn.setDuration(500);
                leftOut.setDuration(500);

                int currentTabId = mTabHost.getCurrentTab();
                int len = mCustomTabWidgets.length;
                // 滑出动画
                mTabHost.getCurrentView().startAnimation(leftOut);
                if (currentTabId < len - 1)
                {
                    mTabHost.setCurrentTab(++currentTabId);
                }
                else
                {
                    mTabHost.setCurrentTab(0);
                }
                // 滑入动画
                mTabHost.getCurrentView().startAnimation(rightIn);
            }

            @Override
            public void flingRight()
            {
                rightOut.setDuration(500);
                leftIn.setDuration(500);

                int currentTabId = mTabHost.getCurrentTab();
                int len = mCustomTabWidgets.length;
                // 滑出动画
                mTabHost.getCurrentView().startAnimation(rightOut);
                if (currentTabId > 0)
                {
                    mTabHost.setCurrentTab(--currentTabId);
                }
                else
                {
                    mTabHost.setCurrentTab(len - 1);
                }
                // 滑入动画
                mTabHost.getCurrentView().startAnimation(leftIn);
            }
        };
        mGestureDetector = new GestureDetector(myOnGestureListener);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        mGestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void initTabHost()
    {
        mCustomTabWidgets = new CustomTabWidget[] { new CustomTabWidget(this, R.string.instrument, R.drawable.tab_instrument, true),
            new CustomTabWidget(this, R.string.oil, R.drawable.tab_oil, false), new CustomTabWidget(this, R.string.drive_record, R.drawable.tab_engine1, false),
            new CustomTabWidget(this, R.string.engine, R.drawable.tab_engine2, false) };

        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(mCustomTabWidgets[0].getView()).setContent(R.id.tab0));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(mCustomTabWidgets[1].getView()).setContent(R.id.tab1));
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(mCustomTabWidgets[2].getView()).setContent(R.id.tab2));
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(mCustomTabWidgets[3].getView()).setContent(R.id.tab3));

        mTabHost.setOnTabChangedListener(new OnTabChangeListener()
        {

            @Override
            public void onTabChanged(String tabId)
            {
                int currentTabId = mTabHost.getCurrentTab();
                for (int i = 0; i < mCustomTabWidgets.length; i++)
                {
                    if (i == currentTabId)
                    {
                        mCustomTabWidgets[i].checked(true);
                        // 切换需要监听的数据流
                        setDataFlowsForTabCurrent(i);
                    }
                    else
                    {
                        mCustomTabWidgets[i].checked(false);
                    }
                }
            }
        });

    }

    private void setDataFlowsForTabCurrent(int tabId)
    {
        // mDiagnoseServerManager.prepareValue(mDataFlows_tab_current);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        try
        {
            analysisFuel.setStart();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            popErrorInfo(e.toString(),e);
        }
        if (S)
        {
            if (isOpenedBluetoothConnecteDialog)
            {
                if (m_blue_data_service.IsConnected())
                {
                    command.setOrGetMode(new byte[] { 0x00 });
                }
                else
                {
                    progressDialog.dismiss();
                    this.finish();
                }

            }
            else
            {
                if (m_blue_data_service.IsConnected())
                {
                    command.setOrGetMode(new byte[] { 0x00 });
                }
                else
                {
                    isOpenedBluetoothConnecteDialog = true;
                    m_blue_data_service.ShowBluetoothConnectActivity(this);// 弹出蓝牙连接对话框
                }
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void findView()
    {
        scalePlate_data1 = (ScalePlate) findViewById(R.id.scalePlate_data1);
        scalePlate_data2 = (ScalePlate) findViewById(R.id.scalePlate_data2);
        scalePlate_data3 = (ScalePlate) findViewById(R.id.scalePlate_data3);
        scalePlate_data_horizontal_1 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_1);
        powerPointer_carspeed = (PowerPointer) findViewById(R.id.powerPointer_carspeed);
        textview_data1_value = (TextView) findViewById(R.id.textview_data1_value);
        textview_data2_value = (TextView) findViewById(R.id.textview_data2_value);
        textview_data3_value = (TextView) findViewById(R.id.textview_data3_value);
        textview_data_value_carspeed = (TextView) findViewById(R.id.textview_data_value_carspeed);
        textview_data_value_engine_speed = (TextView) findViewById(R.id.textview_data_value_engine_speed);
        textview_oil_data1_value = (TextView) findViewById(R.id.textview_oil_data1_value);
        textview_oil_data2_value = (TextView) findViewById(R.id.textview_oil_data2_value);
        textview_oil_data3_value = (TextView) findViewById(R.id.textview_oil_data3_value);
        textview_oil_data4_value = (TextView) findViewById(R.id.textview_oil_data4_value);
        textview_oil_data5_value = (TextView) findViewById(R.id.textview_oil_data5_value);
        scalePlate_data_horizontal_engine_1 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_1);
        scalePlate_data_horizontal_engine_2 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_2);
        scalePlate_data_horizontal_engine_3 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_3);
        scalePlate_data_horizontal_engine_4 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_4);
        scalePlate_data_horizontal_engine_5 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_5);
        scalePlate_data_horizontal_engine_6 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_6);
        scalePlate_data_horizontal_engine_7 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_7);
        scalePlate_data_horizontal_engine_8 = (ScalePlate) findViewById(R.id.scalePlate_data_horizontal_engine_8);
        engine_pointer_1 = (EnginePointer) findViewById(R.id.engine_pointer_1);
        engine_pointer_2 = (EnginePointer) findViewById(R.id.engine_pointer_2);
        textview_engine_data1_value = (TextView) findViewById(R.id.textview_engine_data1_value);
        textview_engine_data2_value = (TextView) findViewById(R.id.textview_engine_data2_value);
        textview_engine_data3_value = (TextView) findViewById(R.id.textview_engine_data3_value);
        textview_engine_data4_value = (TextView) findViewById(R.id.textview_engine_data4_value);
        textview_engine_data5_value = (TextView) findViewById(R.id.textview_engine_data5_value);
        textview_engine_data6_value = (TextView) findViewById(R.id.textview_engine_data6_value);
        textview_engine_data7_value = (TextView) findViewById(R.id.textview_engine_data7_value);
        textview_engine_data8_value = (TextView) findViewById(R.id.textview_engine_data8_value);
        textview_engine_data9_value = (TextView) findViewById(R.id.textview_engine_data9_value);
        textview_engine_data10_value = (TextView) findViewById(R.id.textview_engine_data10_value);
        textview_engine_data11_value = (TextView) findViewById(R.id.textview_engine_data11_value);
        scalePlate_data_oil = (ScalePlate) findViewById(R.id.scalePlate_data_oil);
        fuel_analysis_bar = (FuelAnalysisBar) findViewById(R.id.fuel_analysis_bar);
    }

    @Override
    protected void onDestroy()
    {
        // 发送消息，让service 关闭自己
        MsgQueue.addMessage(MsgIds.ORDER_STOP_SERVICE);

        /***/
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        /***/
        super.onDestroy();
    }

    // 按下返回按钮事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {  
            CustomProgressDialog dialog = new CustomProgressDialog(DataFlowMain.this);
            dialog.setTitle(R.string.notice);
            dialog.setMessage(context.getString(R.string.exiting));
            dialog.show();
            new Command(m_blue_data_service).setOrGetMode(new byte[] { 0x05 });// 用作复位
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            dialog.dismiss();
            DataFlowMain.this.finish();
            return true;
        }
        return false;
    }

    @Override
    public void BlueConnectLost(String name, String mac)
    {
        if (D)
            Log.i("BlueConnectLost", "蓝牙丢失！！！！");
    }

    @Override
    public void BlueConnected(String name, String mac)
    {
        handler.post(new Runnable()
        {

            @Override
            public void run()
            {
                if (!progressDialog.isShowing())
                {
                    progressDialog.show();
                }
            }
        });
        // 发送命令得到模式
        command.setOrGetMode(new byte[] { 0x00 });
    }

    @Override
    public void GetDataFromService(byte[] databuf, int datalen)
    {
        byte[] dpuPackage = OrderUtils.filterReturnDataPackage(databuf);
        byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
        // 带长度字节
        byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);
        // 不带长度字节
        byte[] paramNOLength = OrderUtils.filterOBD2CmdParameters(dpuPackage);
        String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);

        if (cmd_subcmdString.equals("6912"))
        {

                parseDataFlow(databuf);

        }
        else if (cmd_subcmdString.equals("6109"))
        {
            try
            {
                if (param.length == 2 && param[0] != 0x05 && param[1] == 0x00)
                {
                    command.setOrGetMode(new byte[] { 0x05 });
                    next.set(true);
                }
                else
                {
                    if (param.length == 2 && param[0] == 0x05 && param[1] == 0x00)
                    {
                        command.scanSystem();// 扫描进入系统
                        next.set(true);
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                popErrorInfo(e.toString(),e);
            }
        }
        else if (cmd_subcmdString.equals("6900"))
        {
            try
            {
                if (param.length == 1 && param[0] == 0x00)
                {
                    command.scanSystem();// 扫描进入系统
                    next.set(true);
                }
                else
                {
                    command.getPIDQuantity();// 读取支持PID
                    next.set(true);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                popErrorInfo(e.toString(),e);
            }
        }
        else if (cmd_subcmdString.equals("6901"))
        {
            try
            {
                if (paramNOLength != null)
                {
                    com.cnlaunch.mycar.obd2.util.DataExchange.setPidsMeter(paramNOLength);// 保存支持PID
                }
                progressDialog.dismiss();
                readDataFlow();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                popErrorInfo(e.toString(),e);
            }

        }
        else if (cmd_subcmdString.equals("69EE"))
        {
            handler.post(new Runnable()// 必须在主线程里面做处理
                {
                    @Override
                    public void run()
                    {
                        new AlertDialog.Builder(context).setTitle(DataFlowMain.this.getString(R.string.intermediary_activity_tip))
                            .setMessage(DataFlowMain.this.getString(R.string.intermediary_activity_tipmessage))
                            .setPositiveButton(R.string.intermediary_activity_tip_ok, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    new Command(m_blue_data_service).setOrGetMode(new byte[] { 0x05 });// 用作复位
                                    finish();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(DataFlowMain.this, MyCarActivity.class));
                                }
                            }).show();
                    }
                });
        }

    }

    private class StopProgressDialog extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(23 * 1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (progressDialog.isShowing())
            {
                handler.post(new Runnable()// 必须在主线程里面做处理
                    {
                        @Override
                        public void run()
                        {
                            final CustomAlertDialog dlg = new CustomAlertDialog(DataFlowMain.this);
                            dlg.setTitle(R.string.upc_error);
                            dlg.setMessage(R.string.upc_device_response_timeout);
                            dlg.setOnKeyListener(new OnKeyListener()
                            {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                                {
                                    if (keyCode == KeyEvent.KEYCODE_BACK)
                                    {
                                        new Command(m_blue_data_service).setOrGetMode(new byte[] { 0x05 });// 用作复位
                                        dlg.dismiss();
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                    return false;
                                }
                            });
                            dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    dlg.dismiss();
                                    finish();
                                }
                            });
                            dlg.show();
                        }
                    });
            }
            super.run();
        }
    }

    @Override
    public void GetDataTimeout()
    {
        if (progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        handler.post(new Runnable()// 必须在主线程里面做处理
            {
                @Override
                public void run()
                {
                    final CustomAlertDialog dlg = new CustomAlertDialog(DataFlowMain.this);
                    dlg.setTitle(R.string.upc_error);
                    dlg.setMessage(R.string.upc_device_response_timeout);
                    dlg.setOnKeyListener(new OnKeyListener()
                    {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                        {
                            if (keyCode == KeyEvent.KEYCODE_BACK)
                            {
                                new Command(m_blue_data_service).setOrGetMode(new byte[] { 0x05 });// 用作复位
                                dlg.dismiss();
                                finish();
                            }
                            return false;
                        }
                    });
                    dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dlg.dismiss();
                            finish();
                        }
                    });
                    dlg.show();
                }
            });
    }

    @Override
    public void BlueConnectClose()
    {
        Log.i("BlueConnectClose", "蓝牙丢失");
    }

    @Override
    public void GetDataFromBlueSocket(byte[] buf, int len)
    {
    }

    @Override
    public void GetBluetoothState(BlueStateEvent state)
    {
    }

    final static byte[] MAIN_COMMAND_READ_DATA_FLOW = new byte[] { 0x29, 0x12 };// 读数据流主命令
    public final static byte ROTATE_SPEED = 0x0C;// 发动机转速
    public final static byte CAR_SPEED = 0x0D; // 车速
    public final static byte COOLANT_TEMPERATURE = 0x05;// 冷却液温度
    public static final byte INTAKE_AIR_TEMPERATURE = 0x0f; // 进气温度
    public final static byte ATMOSPHERIC_PRESSURE = 0x33;// 大气压力
    public final static byte ENGINE_DUTY = 0x04;// 发动机负载
    public final static byte AIR_FLOW = 0x10;// 空气流量
    public final static byte INTAKE_PRESURE = 0x0b;// 进气压力
    public final static byte CATALYST_TEMPERATURE = 0x3c;// 催化剂温度
    public final static byte FUEL_LEVEL = 0x2f;// 剩余油量 /*燃油液位输入*/
    public final static byte FUEL_TRIM_LONG_TREM = 0X07;// 燃油修正（长期）
    public final static byte FUEL_TRIM_SHORT_TREM = 0X06;// 燃油修正（短期）
    public final static byte IGNITION_ADVANCE_ANGLE = 0x0e;// 点火提前角
    public final static byte FUEL_RAIL_PRESSURE = 0x0a;// 油轨压力(表压力
    public final static byte BATTERY_VOLTAGE = 0x42;// 电瓶电压
    public final static byte AMBIENT_TEMPERATURE = 0x46;// 环境温度

    public final static byte[] ALL_PID_BYTE_ARRAY = new byte[] { // 所有需要的pid字节数组
    ROTATE_SPEED,// 发动机转速
        CAR_SPEED, // 车速
        COOLANT_TEMPERATURE,// 冷却液温度
        INTAKE_AIR_TEMPERATURE,// 进气温度
        ATMOSPHERIC_PRESSURE,// 大气压力
        ENGINE_DUTY,// 发动机负载
        AIR_FLOW,// 空气流量
        INTAKE_PRESURE,// 进气压力
        CATALYST_TEMPERATURE,// 催化剂温度
        FUEL_LEVEL,// 剩余油量 /*燃油液位输入*/
        FUEL_TRIM_LONG_TREM,// 燃油修正（长期）
        FUEL_TRIM_SHORT_TREM,// 燃油修正（短期）
        IGNITION_ADVANCE_ANGLE,// 点火提前角
        FUEL_RAIL_PRESSURE,// 油轨压力(表压力
        BATTERY_VOLTAGE,// 电瓶电压
        AMBIENT_TEMPERATURE // 环境温度
    };
    public static byte[] supportPids = null; // 发动机支持的pid字节数组
    HashMap dataflowMap = new HashMap(); // 数据流的值

    /**
     * 读数据流
     * @param currenTabByte
     * @since DBS V100
     */
    private void readDataFlow()
    {
        supportPids = DataExchange.getSupportPids(ALL_PID_BYTE_ARRAY);
        byte[] subCommand = new byte[supportPids.length + 1];
        subCommand[0] = (byte) (supportPids.length);
        System.arraycopy(supportPids, 0, subCommand, 1, supportPids.length);
        if (m_blue_data_service != null)
            m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode, MAIN_COMMAND_READ_DATA_FLOW, subCommand, subCommand.length, 2500);
    }

    private void freashNotSupportPid()
    {
        byte[] notSupportPid = DataExchange.getNotSupportPids(ALL_PID_BYTE_ARRAY);
        if (notSupportPid != null && notSupportPid.length > 0)
        {
            for (Byte pid : notSupportPid)
            {
                switch (pid)
                {
                    case ROTATE_SPEED: // 发动机转速
                        textview_data_value_engine_speed.setText("N/A");
                        break;
                    case CAR_SPEED: // 车速
                        textview_data_value_carspeed.setText("N/A");
                        powerPointer_carspeed.setValue(0);
                        break;
                    case BATTERY_VOLTAGE: // 电瓶电压
                        scalePlate_data3.setValue(0);
                        textview_data3_value.setText("N/A");
                        break;
                    case AMBIENT_TEMPERATURE: // 环境温度
                        scalePlate_data2.setValue(0);
                        textview_data2_value.setText("N/A");
                        break;
                    case COOLANT_TEMPERATURE: // 冷却液温度
                        scalePlate_data_horizontal_engine_2.setValue(0);
                        textview_engine_data2_value.setText("N/A");
                        break;
                    case INTAKE_AIR_TEMPERATURE: // 进气温度
                        scalePlate_data_horizontal_engine_1.setValue(0);
                        textview_engine_data1_value.setText("N/A");
                        break;
                    case ATMOSPHERIC_PRESSURE: // 大气压
                        scalePlate_data_horizontal_engine_8.setValue(0);
                        textview_engine_data8_value.setText("N/A");
                        break;
                    case ENGINE_DUTY: // 发动机负载
                        scalePlate_data_horizontal_engine_7.setValue(0);
                        textview_engine_data7_value.setText("N/A");
                        break;
                    case AIR_FLOW: // 空气流量
                        scalePlate_data_horizontal_engine_4.setValue(0);
                        textview_engine_data4_value.setText("N/A");
                        textview_oil_data4_value.setText("N/A"); // 瞬时速度
                        textview_oil_data5_value.setText("N/A"); // 平均油耗
                        textview_oil_data2_value.setText("N/A"); // 续航里程
                        scalePlate_data_horizontal_1.setValue(0);
                        textview_oil_data3_value.setText("N/A");

                        break;
                    case INTAKE_PRESURE: // 进气压力
                        scalePlate_data_horizontal_engine_3.setValue(0);
                        textview_engine_data3_value.setText("N/A");
                        break;
                    case CATALYST_TEMPERATURE: // 催化剂温度
                        scalePlate_data_horizontal_engine_5.setValue(0);
                        textview_engine_data5_value.setText("N/A");
                        break;
                    case FUEL_LEVEL: // 剩余油量 /*燃油液位输入*/
                        textview_oil_data1_value.setText("N/A");
                        break;
                    case IGNITION_ADVANCE_ANGLE: // 点火提前角

                        engine_pointer_1.setValue(0, 0);
                        textview_engine_data9_value.setText("N/A");
                        break;
                    case FUEL_RAIL_PRESSURE: // 油轨压力(表压力
                        scalePlate_data_horizontal_engine_6.setValue(0);
                        textview_engine_data6_value.setText("N/A");
                        break;
                    case FUEL_TRIM_LONG_TREM: // 燃油修正（长期）
                        textview_engine_data10_value.setText("N/A");
                        break;
                    case FUEL_TRIM_SHORT_TREM: // 燃油修正（短期）
                        textview_engine_data11_value.setText("N/A");
                        break;

                }
            }
        }
    }

    /**
     * 解析收到的数据流数据
     * @param dataFlows
     * @since DBS V100
     */
    private void parseDataFlow(byte[] dataFlows)
    {
        int dataFlowLength = dataFlows[9];
        if (dataFlowLength != (dataFlows.length - 11) / 4)
        {
            if (D)
                Log.d(TAG, "收到的数据有误！");
            return;
        }

        if (supportPids != null && supportPids.length == dataFlowLength)
        {
            byte[] data = new byte[dataFlowLength * 4];
            System.arraycopy(dataFlows, 10, data, 0, dataFlowLength * 4);
            for (int i = 0; i < supportPids.length; i++)
            {
                byte pid = supportPids[i];
                byte[] dataByte = new byte[] { data[4 * i], data[4 * i + 1], data[4 * i + 2], data[4 * i + 3] };
                try
                {
                    synchronized (this)
                    {
                        dataflowMap.remove(pid);
                        String value = new String(searchIdUtils.getResultWithCalc(pid, dataByte), "GB2312");
                        if (D)
                            Log.d(TAG, "收到pid的值" + pid + " : " + value);
                        dataflowMap.put(pid, value);
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    popErrorInfo(e.toString(),e);
                }
            }
            handler.post(new Runnable()
            {

                @Override
                public void run()
                {

                    try
                    {
                        freashView(dataflowMap);
                        freashNotSupportPid();
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                        popErrorInfo(e.toString(),e);
                    }
                }
            });
        }
        else
        {
            if (D)
                Log.d(TAG, "收到的数据和当前的pid组不匹配！");
        }
    }

    float fuelTrimShort = 0.0f;// 短期燃油修正
    float fuelTrimLong = 0.0f; // 长期燃油修正

    private void freashView(HashMap dataMap)
    {
        if (dataMap != null && dataMap.size() > 0)
        {
            fuelTrimShort = 0.0f;// 短期燃油修正
            fuelTrimLong = 0.0f; // 长期燃油修正
            Set<Byte> pidSet = dataMap.keySet();
            for (Byte pid : pidSet)
            {
                switch (pid)
                {
                    case ROTATE_SPEED: // 发动机转速
                        textview_data_value_engine_speed.setText(dataMap.get(pid).toString().trim());
                        break;
                    case CAR_SPEED: // 车速
                        textview_data_value_carspeed.setText(dataMap.get(pid).toString().trim());
                        float carspeed = Float.parseFloat(dataMap.get(pid).toString().trim());
                        powerPointer_carspeed.setValue((int) (carspeed / 2));
                        break;
                    case BATTERY_VOLTAGE: // 电瓶电压
                        scalePlate_data3.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()));
                        textview_data3_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case AMBIENT_TEMPERATURE: // 环境温度
                        scalePlate_data2.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_data2_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case COOLANT_TEMPERATURE: // 冷却液温度
                        scalePlate_data_horizontal_engine_2.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data2_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case INTAKE_AIR_TEMPERATURE: // 进气温度
                        scalePlate_data_horizontal_engine_1.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data1_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case ATMOSPHERIC_PRESSURE: // 大气压力
                        scalePlate_data_horizontal_engine_8.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data8_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case ENGINE_DUTY: // 发动机负载
                        scalePlate_data_horizontal_engine_7.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data7_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case AIR_FLOW: // 空气流量

                        scalePlate_data_horizontal_engine_4.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data4_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case INTAKE_PRESURE: // 进气压力
                        scalePlate_data_horizontal_engine_3.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data3_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case CATALYST_TEMPERATURE: // 催化剂温度
                        scalePlate_data_horizontal_engine_5.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 100);
                        textview_engine_data5_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case FUEL_LEVEL: // 剩余油量 /*燃油液位输入*/
                        textview_oil_data1_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case IGNITION_ADVANCE_ANGLE: // 点火提前角
                        float value = Float.parseFloat(dataMap.get(pid).toString().trim());
                        engine_pointer_1.setValue(value, value);
                        textview_engine_data9_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case FUEL_RAIL_PRESSURE: // 油轨压力(表压力
                        scalePlate_data_horizontal_engine_6.setValue(Float.parseFloat(dataMap.get(pid).toString().trim()) / 10);
                        textview_engine_data6_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case FUEL_TRIM_LONG_TREM: // 燃油修正（长期）

                        fuelTrimLong = Float.parseFloat(dataMap.get(pid).toString().trim()); // 长期燃油修正
                        textview_engine_data10_value.setText(dataMap.get(pid).toString().trim());
                        break;
                    case FUEL_TRIM_SHORT_TREM: // 燃油修正（短期）
                        fuelTrimShort = Float.parseFloat(dataMap.get(pid).toString().trim());// 短期燃油修正
                        textview_engine_data11_value.setText(dataMap.get(pid).toString().trim());
                        break;

                }
            }
            // 刷新加速度
            try
            {
                calAcceleration();
                analysisFuel.run();
                engine_pointer_2.setValue(fuelTrimLong, fuelTrimShort);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                popErrorInfo(e.toString(),e);
            }
        }
    }

    final static BigDecimal zero = new BigDecimal(0);
    final static BigDecimal big2 = new BigDecimal(2);
    final static BigDecimal big3 = new BigDecimal(3);
    final static BigDecimal big7 = new BigDecimal(7);
    final static BigDecimal big10 = new BigDecimal(10);
    final static BigDecimal big12 = new BigDecimal(12);
    final static BigDecimal big50 = new BigDecimal(50);
    final static BigDecimal big235 = new BigDecimal(235);
    final static BigDecimal big237 = new BigDecimal(237);
    final static BigDecimal big235divide7 = big235.divide(big7, BigDecimal.ROUND_HALF_EVEN);
    static boolean isStart = false; // 是否开始累积
    AnalysisFuel analysisFuel = new AnalysisFuel();

    class AnalysisFuel
    {

        private BigDecimal immediateFuel = zero; // 瞬时油耗
        private BigDecimal mileage = zero; // 续航里程
        private BigDecimal hourFuel = zero; // 每小时油耗
        private BigDecimal accumulateFuel = zero; // 累积油耗值
        private BigDecimal averageFuel = zero; // 平均油耗
        private BigDecimal carSpeedValue = zero; // 车速
        private BigDecimal airFlowValue = zero; // 空气流量
        private BigDecimal fuelLevel = zero; // 燃油水平
        private long counter = 0; // 累积次数

        public void run()
        {
            if (isStart)
            {
                counter++;
                if (!checkParam())
                {
                    return;
                }

                carSpeedValue = new BigDecimal(dataflowMap.get(CAR_SPEED).toString().trim());
                airFlowValue = new BigDecimal(dataflowMap.get(AIR_FLOW).toString().trim());
                fuelLevel = new BigDecimal(dataflowMap.get(FUEL_LEVEL).toString().trim());
                if (D)
                    Log.i(TAG, "carSpeedValue ： " + carSpeedValue);
                if (D)
                    Log.i(TAG, "airFlowValue ： " + airFlowValue);
                if (D)
                    Log.i(TAG, "fuelLevel ： " + fuelLevel);
                if (carSpeedValue.doubleValue() > 0 && airFlowValue.doubleValue() > 0)
                {
                    // 第一步:先算出瞬时油耗:
                    immediateFuel = (airFlowValue.divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN)).multiply(big235divide7);

                    // 累积油耗
                    accumulateFuel = accumulateFuel.add(immediateFuel);

                    // 平均油耗
                    // averageFuel = accumulateFuel.divide(new
                    // BigDecimal(counter),BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal i = airFlowValue.divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal j = big235divide7.multiply(big10);
                    averageFuel = i.multiply(j).divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN);

                    // 燃油里程
                    mileage = big50.multiply(fuelLevel).divide(averageFuel, BigDecimal.ROUND_HALF_EVEN);

                    // 每小时油耗
                    // hourFuel = big50.multiply(fuelLevel).divide(mileage,
                    // BigDecimal.ROUND_HALF_EVEN);
                    hourFuel = new BigDecimal(averageFuel.floatValue() + 0.1);

                    if (D)
                        Log.i(TAG, "immediateFuel ： " + immediateFuel);
                    if (D)
                        Log.i(TAG, "accumulateFuel ： " + accumulateFuel);
                    if (D)
                        Log.i(TAG, "averageFuel ： " + averageFuel);
                    if (D)
                        Log.i(TAG, "mileage ： " + mileage);
                    textview_oil_data4_value.setText(immediateFuel.toString().trim()); // 瞬时速度
                    textview_oil_data5_value.setText(averageFuel.toString().trim()); // 平均油耗
                    textview_oil_data2_value.setText(mileage.toString().trim()); // 续航里程
                    scalePlate_data_horizontal_1.setValue(hourFuel.floatValue() / 10);
                    textview_oil_data3_value.setText(hourFuel.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                }
            }
            else
            {
                init();
            }

        }

        public boolean checkParam()
        {
            if (dataflowMap.get(CAR_SPEED) == null)
            {
                if (D)
                    Log.i(TAG, "车速为空 ! ");
                return false;
            }
            if (dataflowMap.get(AIR_FLOW) == null)
            {
                if (D)
                    Log.i(TAG, "空气流量为空 ! ");
                return false;
            }
            if (dataflowMap.get(FUEL_LEVEL) == null)
            {
                if (D)
                    Log.i(TAG, "燃油水平为空 ! ");
                return false;
            }
            return true;
        }

        public void setStart()
        {
            Log.i(TAG, "开始累积油耗!");
            isStart = true;
            new AccumulateFuelThread().start();
        }

        public void setEnd()
        {
            Log.i(TAG, "停止累积油耗!");
            isStart = false;
            init();
        }

        private void init()
        {
            immediateFuel = zero; // 瞬时油耗
            mileage = zero; // 续航里程
            hourFuel = zero; // 每小时油耗
            accumulateFuel = zero; // 累积油耗值
            averageFuel = zero; // 平均油耗
            carSpeedValue = zero; // 车速
            airFlowValue = zero; // 空气流量
            fuelLevel = zero; // 燃油水平
            counter = 0; // 累积次数
            isStart = false; // 是否开始累积
        }
    }

    BigDecimal accumulateFuel = zero;
    float[] threeHourFuel = new float[17];
    BigDecimal carSpeedValue = zero;
    BigDecimal airFlowValue = zero;
    BigDecimal sigleFuel = zero;
    long currentTime = 0;
    float accumlateTime = 0;

    class AccumulateFuelThread extends Thread
    {
        public AccumulateFuelThread()
        {
            accumulateFuel = zero;
            currentTime = System.currentTimeMillis();
        }

        @Override
        public void run()
        {
            while (isStart)
            {
                if (D)Log.i(TAG, "当前时间currentTime:" + currentTime);
                Object carSpeed = dataflowMap.get(CAR_SPEED);
                Object airFlow = dataflowMap.get(AIR_FLOW);
                // a = 235/ carSpeedValue / airFlowValue *
                // 7.0;(每1.5~2.0秒系统扫描数据流一次，扫描出来的值不断累加)
                if (carSpeed != null && airFlow != null)
                {
                    float immediateFuel;
          
                        
                        carSpeedValue = new BigDecimal(carSpeed.toString().trim());
                        airFlowValue = new BigDecimal(airFlow.toString().trim());
                        if(carSpeedValue.intValue() == 0 || airFlowValue.intValue() == 0)
                        {
                            continue;
                        }
                        if (D)Log.i(TAG, "车速carSpeedValue:" + carSpeedValue);
                        if (D)Log.i(TAG, "空气流量airFlowValue:" + airFlowValue);
                        sigleFuel = big235.divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN).divide(airFlowValue, BigDecimal.ROUND_HALF_EVEN).multiply(big7);
                        if (D)Log.i(TAG, "单次油耗sigleFuel:" + sigleFuel);

                        accumulateFuel = accumulateFuel.add(accumulateFuel).add(sigleFuel);

                        long endTime = System.currentTimeMillis();
                        accumlateTime += ((endTime - currentTime) / 1000);
                        if (D)Log.i(TAG, "时间差:" + (endTime - currentTime));
                        currentTime = endTime;
                        immediateFuel = (airFlowValue.divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN)).multiply(big235divide7).floatValue();
                        freshThreeHourFuel(accumlateTime, immediateFuel);
  
                }
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    popErrorInfo(e.toString(),e);
                }
            }
            super.run();
        }
    }

    private void freshThreeHourFuel(float accumlateTime, final float immediateFuel)
    {

        if (accumlateTime > 0 && accumlateTime < 60)// 更新第一条
        {
            threeHourFuel[0] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 60 && accumlateTime < 60 * 2)
        {
            threeHourFuel[1] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 60 * 2 && accumlateTime < 60 * 3)
        {
            threeHourFuel[2] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 60 * 3 && accumlateTime < 60 * 4)
        {
            threeHourFuel[3] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 60 * 4 && accumlateTime < 60 * 5)
        {
            threeHourFuel[4] = accumulateFuel.floatValue();
            threeHourFuel[5] = accumulateFuel.floatValue();

        }
        else if (accumlateTime > 300 && accumlateTime < 300 * 2)
        {
            threeHourFuel[6] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 300 * 2 && accumlateTime < 300 * 3)
        {
            threeHourFuel[7] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 300 * 3 && accumlateTime < 300 * 4)
        {
            threeHourFuel[8] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 300 * 4 && accumlateTime < 300 * 5)
        {
            threeHourFuel[9] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 300 * 5 && accumlateTime < 300 * 6)
        {
            threeHourFuel[10] = accumulateFuel.floatValue();
            threeHourFuel[11] = accumulateFuel.floatValue();
        }

        else if (accumlateTime > 1800 * 1 && accumlateTime < 1800 * 2)
        {
            threeHourFuel[12] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 1800 * 2 && accumlateTime < 1800 * 3)
        {
            threeHourFuel[13] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 1800 * 3 && accumlateTime < 1800 * 4)
        {
            threeHourFuel[14] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 1800 * 4 && accumlateTime < 1800 * 5)
        {
            threeHourFuel[15] = accumulateFuel.floatValue();
        }
        else if (accumlateTime > 1800 * 5 && accumlateTime < 1800 * 6)
        {
            threeHourFuel[16] = accumulateFuel.floatValue();
        }
        handler.post(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                fuel_analysis_bar.setValue(threeHourFuel);
                scalePlate_data_oil.setValue(immediateFuel);
            }
        });
    }

    BigDecimal startTime = new BigDecimal(0); // 起始时刻
    BigDecimal startSpeed = new BigDecimal(0);// 起始速度
    final static BigDecimal TIME_UNIT = new BigDecimal(1000);
    final static BigDecimal SPEED_UNIT = new BigDecimal(3.6);
    final static BigDecimal SCALE_UNIT = new BigDecimal(100);

    /**
     * 计算加速度
     * @since DBS V100
     */

    private void calAcceleration()
    {
        if (D)
            Log.i(TAG + "calAcceleration", "起始时刻:" + startTime.doubleValue());
        if (D)
            Log.i(TAG + "calAcceleration", "起始速度:" + startSpeed.doubleValue());
        // 先记录当前时间
        BigDecimal endTime = new BigDecimal(System.currentTimeMillis());
        if (dataflowMap.get(CAR_SPEED) == null)
        {
            if (D)
                Log.i(TAG + "calAcceleration", "车速为空" + dataflowMap.toString());
            return;
        }
        if (D)
            Log.i(TAG + "calAcceleration", "下位机读数:" + dataflowMap.get(CAR_SPEED));
        BigDecimal endSpeed = new BigDecimal(dataflowMap.get(CAR_SPEED).toString().trim());
        if (D)
            Log.i(TAG + "calAcceleration", "终止时刻:" + endTime.doubleValue());
        if (D)
            Log.i(TAG + "calAcceleration", "终止速度:" + endSpeed.doubleValue());
        if (startTime == null || startTime.intValue() == 0)
        {
            startTime = endTime;
            startSpeed = endSpeed;
            return;
        }

        BigDecimal incrementTime = endTime.subtract(startTime).divide(TIME_UNIT, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal incrementSpeed = endSpeed.subtract(startSpeed).multiply(SPEED_UNIT);

        if (D)
            Log.i(TAG + "calAcceleration", "时间增量:" + incrementTime.doubleValue());
        if (D)
            Log.i(TAG + "calAcceleration", "速度增量:" + incrementSpeed.doubleValue());
        BigDecimal acceleration = new BigDecimal(0);
        if (incrementTime.intValue() > 0)
        {

            acceleration = incrementSpeed.divide(incrementTime.multiply(SCALE_UNIT), BigDecimal.ROUND_HALF_EVEN);
        }
        startTime = endTime;
        startSpeed = endSpeed;
        if (D)
            Log.i(TAG + "calAcceleration", "☆☆☆☆☆加速度:" + acceleration.doubleValue());
        ;
        scalePlate_data1.setValue(acceleration.floatValue());
        textview_data1_value.setText(acceleration.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }

    private void popErrorInfo(final String message, final Exception e)
    {
        handler.post(new Runnable()
        {

            @Override
            public void run()
            {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
                PrintWriter p = new PrintWriter(byteArrayOutputStream);
                e.printStackTrace(p);
                p.flush();
                // TODO Auto-generated method stub
                CustomAlertDialog dialog = new CustomAlertDialog(DataFlowMain.this);
                dialog.setTitle("error");
                dialog.setMessage(message + new String(byteArrayOutputStream.toByteArray()));
                dialog.setNegativeButton(getResources().getString(R.string.ok), new OnClickListener()
                {
                    
                    @Override
                    public void onClick(View v)
                    {
                        // TODO Auto-generated method stub
                        DataFlowMain.this.finish();
                    }
                });
                dialog.show();

            }
        });

    }
}
