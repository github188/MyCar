package com.cnlaunch.mycar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.DBSCarSummaryInfo.IDBSCarObserve;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.ExtSlidingDrawer;
import com.cnlaunch.mycar.common.ui.ExtSlidingDrawer.OnDrawerScrollListener;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.FileUtils;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;
import com.cnlaunch.mycar.diagnose.formal.DiagnoseSelectVersionActivity;
import com.cnlaunch.mycar.diagnose.simplereport.DiagnoseSimpleReportActivity;
import com.cnlaunch.mycar.im.ImLoginActivity;
import com.cnlaunch.mycar.manager.BillingAddActivity;
import com.cnlaunch.mycar.manager.OilDetailActivity;
import com.cnlaunch.mycar.manager.SyncThread;
import com.cnlaunch.mycar.manager.net.SyncOilJob;
import com.cnlaunch.mycar.manager.net.SyncUserCarJob;
import com.cnlaunch.mycar.obd2.DataFlowMain;
import com.cnlaunch.mycar.updatecenter.DeviceActivateGuideActivity;
import com.cnlaunch.mycar.updatecenter.DiagSoftUpdateConfigParams;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.UpdateCenterMainActivity;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.listener.ApkDownloadListener;
import com.cnlaunch.mycar.updatecenter.listener.ApkUpdateInfoListener;
import com.cnlaunch.mycar.updatecenter.listener.DownloadBinDownloadListener;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.LoginThread;
import com.cnlaunch.mycar.usercenter.UserInfoActivity;
import com.cnlaunch.mycar.usercenter.UsercenterActivity;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;
import com.cnlaunch.mycar.usercenter.model.LoginResult;

public class MyCarActivity extends BaseActivity
{

    // 调试log信息target
    private static final String TAG = "MyCarActivity";
    private static final boolean D = true;
    protected static String city;
    public static boolean isRuning = false;

    // ---------------- 自动登录-----------------------------
    public static String account; // 账号
    public static String password; // 密码
    public static String[] accountsArray; // 登录账号记录
    public static boolean isLogin = false; // 是否登录
    private static int loginCount = 0;
    public static String cc; // cc号码
    public static String email;
    public static String mobile;
    public static String token; // 令牌
    public static long csInterval; // 客户端和服务端的时间间隔
    public static int loginState = Constants.LOGIN_STATE_LOGOUTED; // 登录状态：0未登录
                                                                   // 1登录中 2已登录
    public static String language = "zh";
    private SharedPreferences sp; // 取得系统SharedPreferences
    private MyCarApplication application;
    // ------------------标题栏上的图片及------------------
    View main_title_weather_area;
    ImageView main_title_imageview_weather;
    TextView main_title_tv_temperature;
    ImageView main_title_imageview_tour;
    ImageView main_title_imageview_washcar;
    TextView main_title_tv_washcar;

    TextView main_logout_notify;
    TextView tvTemperature;
    TextView tvEngineFault;
    public static TextView tvOnline;
    /* 弹出浮动窗口的控件 */
    private View main_drag_bar_bg;
    private GridView gvSquared; // 九宮格

    ExtSlidingDrawer extSlidingDrawer;
    private ImageButton ibAdv;

    Resources resources;
    private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(); // list
    private ListView dbsInfoListView; // 周边店铺的简要信息
    private DbsInfoListAdapter dbsInfoListAdapter; // 周边店铺简要信息适配器

    // 向上卷起的图片
    ImageView ivDragUp;
    // 是否登录图片
    public static TextView isLoginImage;
    // 用户中心的布局
    LinearLayout usercenterLinearLayout;
    // 爱车体检的布局
    LinearLayout loveCarDiagLinearLayout;

    // ------------------标题栏上的图片及------------------

    View drag_ll_weather;
    View drag_textview_edit_weathe_city;
    ImageView drag_imageview_weather;
    TextView drag_textview_weathe_city;
    TextView drag_textview_weathe_general;
    ImageView drag_imageview_tour;
    TextView drag_textview_tour_info;
    RatingBar drag_ratingbar_tour;
    ImageView drag_imageview_washcar;
    TextView drag_textview_washcar_info;
    RatingBar drag_ratingbar_washcar;
    TextView drag_textview_refresh_weather;

    public static TextView slidingDrawerlogin;

    static final int REQUEST_CODE_WEATHER_SET_CITY = 0;

    // dtc
    private TextView dtcTextView;
    TextView drag_tv_dtc;
    private TextView main_title_tv_engine_fault;

    String str;// = resources.getString(R.string.default_dtc_content);
    // 体检分数
    private String exam_num = "";
    // 故障码个数
    private Integer doc_num = 0;

    WebServiceOperator webservice;
    HttpDownloadManager httpDownloader;
    DefaultHttpListener downloadBinDownloadListener;
    DefaultHttpListener apkDownloadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        application = (MyCarApplication) getApplication();
        // 去掉标题，全屏显示启动动画
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        resources = getResources();
        // 获取最后一次登录账号的SharedPreferences
        sp = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        setContentView(R.layout.main);
        language = Locale.getDefault().getLanguage();
        if (language.equals("zh"))
        {
            language = "CN";
        }
        initInstructions();
        findView();
        initDiagInfo();
        // 注册广播更新标题栏的
        registerTitleReceive();

        displayAll();

        prepareAndShowWeather();
        // 获取当前车型与版本
        getCurrentCarTypeAndVer();

        webservice = new WebServiceOperator(this);
        httpDownloader = new HttpDownloadManager(this);
        downloadBinDownloadListener = new DownloadBinDownloadListener(MyCarActivity.this, mHandler, httpDownloader);
        apkDownloadListener = new ApkDownloadListener(MyCarActivity.this, mHandler, httpDownloader);
        // if (application.getDevice() == null)
        // {
        // showGotoUpdateCenter();
        // }
        registerPhoneState();
    }

    // 读取爱车体检故障码信息
    public void initDiagInfo()
    {
        exam_num = "";
        doc_num = 0;
        SharedPreferences preExamNum = this.getSharedPreferences(DiagnoseConstant.PRE_EXAM_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
        if (preExamNum.contains(DiagnoseConstant.PRE_EXAM_NUM))
        {
            exam_num = preExamNum.getString(DiagnoseConstant.PRE_EXAM_NUM, " ");
        }
        SharedPreferences preDocNum = this.getSharedPreferences(DiagnoseConstant.PRE_DOC_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
        if (preDocNum.contains(DiagnoseConstant.PRE_DOC_NUM))
        {
            doc_num = preDocNum.getInt(DiagnoseConstant.PRE_DOC_NUM, 0);
        }
        SharedPreferences preQuestionInfo = this.getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
        String currentLanguage = preQuestionInfo.getString(DiagnoseConstant.CURRENT_LANGUAGE, "");
        String pre_car_type = preQuestionInfo.getString(Constants.DBSCAR_CURRENT_CAR_TYPE, "");
        if (!Env.GetCurrentLanguage().equals(currentLanguage))
        {
            exam_num = "";
        }
    }

    private void findView()
    {
        main_logout_notify = (TextView) findViewById(R.id.main_logout_notify);
        tvOnline = (TextView) findViewById(R.id.main_title_tv_online);
        main_drag_bar_bg = findViewById(R.id.main_drag_bar_bg);

        extSlidingDrawer = (ExtSlidingDrawer) findViewById(R.id.sliding_drawer);
        // 绑定天气相关控件
        main_title_weather_area = findViewById(R.id.main_title_weather_area);
        main_title_imageview_weather = (ImageView) findViewById(R.id.main_title_imageview_weather);
        main_title_tv_temperature = (TextView) findViewById(R.id.main_title_tv_temperature);
        main_title_tv_washcar = (TextView) findViewById(R.id.main_title_tv_washcar);
        main_title_imageview_tour = (ImageView) findViewById(R.id.main_title_imageview_tour);
        main_title_imageview_washcar = (ImageView) findViewById(R.id.main_title_imageview_washcar);

        tvTemperature = (TextView) findViewById(R.id.main_title_tv_temperature);
        tvEngineFault = (TextView) findViewById(R.id.main_title_tv_engine_fault);
        tvOnline = (TextView) findViewById(R.id.main_title_tv_online);

        usercenterLinearLayout = (LinearLayout) findViewById(R.id.drag_ll_usercenter);
         loveCarDiagLinearLayout = (LinearLayout)
         findViewById(R.id.drag_love_car_diagnosis);
        slidingDrawerlogin = (TextView) findViewById(R.id.drag_tv_isLogin);
        isLoginImage = (TextView) findViewById(R.id.drag_tv_isLogin_bg);
        dtcTextView = (TextView) findViewById(R.id.dtc_content_text);
        main_title_tv_engine_fault = (TextView) findViewById(R.id.main_title_tv_engine_fault);
        drag_tv_dtc = (TextView) findViewById(R.id.drag_tv_dtc);

        tvTemperature = (TextView) findViewById(R.id.main_title_tv_temperature);
        tvEngineFault = (TextView) findViewById(R.id.main_title_tv_engine_fault);
        tvOnline = (TextView) findViewById(R.id.main_title_tv_online);

        drag_ll_weather = findViewById(R.id.drag_ll_weather);
        drag_textview_edit_weathe_city = findViewById(R.id.drag_textview_edit_weathe_city);
        drag_imageview_weather = (ImageView) findViewById(R.id.drag_imageview_weather);
        drag_textview_refresh_weather = (TextView) findViewById(R.id.drag_textview_refresh_weather);
        drag_textview_weathe_city = (TextView) findViewById(R.id.drag_textview_weathe_city);
        drag_textview_weathe_general = (TextView) findViewById(R.id.drag_textview_weathe_general);
        drag_imageview_tour = (ImageView) findViewById(R.id.drag_imageview_tour);
        drag_textview_tour_info = (TextView) findViewById(R.id.drag_textview_tour_info);
        drag_ratingbar_tour = (RatingBar) findViewById(R.id.drag_ratingbar_tour);
        drag_imageview_washcar = (ImageView) findViewById(R.id.drag_imageview_washcar);
        drag_textview_washcar_info = (TextView) findViewById(R.id.drag_textview_washcar_info);
        drag_ratingbar_washcar = (RatingBar) findViewById(R.id.drag_ratingbar_washcar);

        dbsInfoListView = (ListView) findViewById(R.id.main_lv_dbs_info);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // 加载登录记录列表
        accountsArray = getAccounts();

        // 登录次数，如果是第一次到本页面，设置未登录
        if (loginCount++ == 0)
        {
            sp.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
            Log.i(TAG, "返回主页面的次数: " + loginCount);
        }

        // 是否自动登录
        if (sp != null)
        {
            account = sp.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null); // 账号，密码
            password = sp.getString(UsercenterConstants.LAST_LOGIN_PWD, null);
            if (sp.getString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT).equals(UsercenterConstants.LOGIN_STATE_LOGOUT)
                && sp.getBoolean(UsercenterConstants.IS_AUTO_LOGIN, false))
            {

                autologin();// 自动登录

            }
        }
        updateTitleState();
        registerDBSSummaryReceive();
        freshWeatherInfo();
    }

    @Override
    protected void onRestart()
    {

        super.onRestart();
        if (extSlidingDrawer.isOpened())
        {
            extSlidingDrawer.toggle();
        }
        loginCount++;
        // 更新登录状态
        updateTitleState();
        if (isLogin)
        {
            tvOnline.setBackgroundResource(R.drawable.main_title_login);
            setCustomeTitleRight(resources.getString(R.string.login_state_display_login)); // 已登录
        }
        else
        {
            setCustomeTitleRight(resources.getString(R.string.login_state_display_logout)); // 未登录
            tvOnline.setBackgroundResource(R.drawable.main_title_logout);
        }
        updateLogoutNotify();
        
    }

    @Override
    public void onBackPressed()
    {
        if (extSlidingDrawer.isOpened())
        {
            extSlidingDrawer.animateToggle();
        }
        else
        {
            super.onBackPressed();
        }
        return;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mDBSCarSummaryBroadcastReceiver != null)
        {
            unregisterReceiver(mDBSCarSummaryBroadcastReceiver);
        }
    }

    @Override
    protected void onResume()
    {
        loadVehicleLogo();
        getCurrentCarTypeAndVer();
        refreshDbsInfoSummary();
        initDiagInfo();
        updateDtcTitleState();
        isRuning = true;
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isRuning = false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sp.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
        // 注销广播接收
        this.unregisterReceiver(mTitleReceiver);
        Log.i("MycarActivity close!", "Onddetory");
        BluetoothDataService m_blue_service = BluetoothDataService.getInstance();
        m_blue_service.StopBlueService(this);
        try
        {
            m_blue_service.finalize();
        }
        catch (Throwable e)
        {
            // TODO: handle exception
            if (D)
                Log.e(TAG, e.getMessage());
        }
        unRegisterPhoneState();
        // 触发车辆管家数据同步上传
        //new SyncThread(MyCarActivity.this, null).start();
    }

    // @Override
    // public boolean onPrepareOptionsMenu(Menu menu) {
    // menu.clear();
    // // menu.add(1, 1, 1, "软件升级").setIcon(android.R.drawable.ic_input_get);
    // menu.add(1, 1, 1, "软件升级").setIcon(android.R.drawable.ic_menu_compass);
    // menu.add(1, 2, 2, "关于软件").setIcon(
    // android.R.drawable.ic_menu_info_details);
    // menu.add(1, 3, 3, "退出").setIcon(android.R.drawable.ic_lock_power_off);
    // // menu.add(1, 4, 4,
    // // "TestDialog").setIcon(android.R.drawable.ic_lock_power_off);
    // return true;
    // }

    /**
     * @author luxingsong 菜单点击事件处理 各个模块根据需要在这里添加自己的入口
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        Intent intent = null;
        switch (item.getItemId())
        {
            case 1:
                // 跳转到升级中心界面
                intent = new Intent(MyCarActivity.this, UpdateCenterMainActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MyCarActivity.this, About.class);
                startActivity(intent);
                break;
            case 3:
                // 退出 MyCar
                this.finish();
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 将从服务器请求到的天气数据，显示到界面上
     * @param weatherData
     */
    private void showWeatherInfo(String weatherData)
    {
        if (weatherData != null && weatherData != "")
        {
            WeatherModel.setWeatherData(weatherData);
            freshWeatherInfo();
        }
    }

    /**
     * 从网络获取，并显示天气相关信息：天气图标，洗车指数，出行指数
     */
    private void prepareAndShowWeather()
    {
        String loc = Locale.getDefault().getLanguage();
        if (loc.equals("zh"))
        {
            showWeatherArea();
            new WeatherThread(this).start();
        }
        else
        {
            hideWeatherArea();
        }
    }

    private void showWeatherArea()
    {
        main_title_weather_area.setVisibility(View.VISIBLE);
        drag_ll_weather.setVisibility(View.VISIBLE);
    }

    private void hideWeatherArea()
    {
        main_title_weather_area.setVisibility(View.GONE);
        drag_ll_weather.setVisibility(View.GONE);
    }

    /**
     * 更新是否登录状态栏
     * @since DBS V100
     */
    private void updateLogoutNotify()
    {
        if (main_logout_notify != null && isLogin)
        {
            main_logout_notify.setVisibility(View.GONE);
        }
        else
        {
            main_logout_notify.setVisibility(View.VISIBLE);
        }
    }

    public void displayAll()
    {
        initDbsInfo();

        main_logout_notify.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(MyCarActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        updateLogoutNotify();

        initSlidingDrawer();

        extSlidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener()
        {

            @Override
            public void onScrollStarted()
            {
                main_drag_bar_bg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onScrollEnded()
            {
            }
        });

        extSlidingDrawer.setOnDrawerOpenListener(new ExtSlidingDrawer.OnDrawerOpenListener()
        {

            @Override
            public void onDrawerOpened()
            {
                main_drag_bar_bg.setVisibility(View.VISIBLE);

            }
        });
        extSlidingDrawer.setOnDrawerCloseListener(new ExtSlidingDrawer.OnDrawerCloseListener()
        {

            @Override
            public void onDrawerClosed()
            {

                main_drag_bar_bg.setVisibility(View.INVISIBLE);
            }
        });
        freshWeatherInfo();

        main_drag_bar_bg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                main_drag_bar_bg.setVisibility(View.INVISIBLE);
            }
        });

        if (isLogin)
        {
            // 发送广播更新登录状态
            Intent intent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
            intent.putExtra("isLoginFlag", isLogin);
            sendBroadcast(intent);

            tvOnline.setBackgroundResource(R.drawable.main_title_login);
            updateTitleState();
        }
        else
        {
            tvOnline.setBackgroundResource(R.drawable.main_title_logout);
            updateTitleState();
        }

    }

    /**
     * 自动登录
     */
    private void autologin()
    {
        if (account != null && password != null)
        {
            // 开启自动登录线程
            new LoginThread(account, password, Constants.SERVICE_LOGIN_METHOD_NAME, mHandler, MyCarActivity.this).start();
            loginState = Constants.LOGIN_STATE_LOGINING; // 登录状态，登录中
        }
    }

    /**
     * 从SharedPreferences里面取出登录记录，key是CC号码，value就是密码
     * @return
     */
    private String[] getAccounts()
    {
        // 从SharedPreferences里面取出登录记录
        SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        Map accountsMap = loginSP.getAll(); // 得到所有的登录记录
        String[] accountsArray;
        // key是CC号码，value就是密码
        Set accountsSet = accountsMap.keySet();
        if (accountsSet != null && accountsSet.size() > 0)
        {
            accountsArray = new String[accountsSet.size()];
            Iterator it = accountsSet.iterator();
            int i = 0;
            while (it.hasNext())
            {
                accountsArray[i++] = it.next().toString();
            }
            return accountsArray;
        }
        return null;
    }

    /**
     * 保存登录信息
     * @param obj
     */
    private void saveLoginInfo(Object obj)
    {
        sp.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGIN).commit(); // 已登录
        sp.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, true).commit(); // 自动登录
        sp.edit().putString(UsercenterConstants.LAST_LOGIN_ACCOUNT, account).commit(); // 登录账号
        sp.edit().putString(UsercenterConstants.LAST_LOGIN_PWD, password).commit(); // 密码
        LoginResult loginResult = (LoginResult) obj;
        sp.edit().putString(UsercenterConstants.LOGIN_TOKEN, loginResult.token).commit(); // 登录令牌
        sp.edit().putLong(UsercenterConstants.LOGIN_SERVICE_TIME, loginResult.serverSystemTime).commit(); // 服务器时间
        // setCustomeTitleRight("已登录");

        isLogin = true; // 是否登录： 是
        token = loginResult.token; // 令牌
        csInterval = new Date().getTime() - loginResult.serverSystemTime; // 客户端和服务端的时间间隔
        cc = loginResult.cc; // CC号码
        sp.edit().putString(UsercenterConstants.LOGIN_CC, loginResult.cc).commit(); // CC
        // 指定当前数据库名称
        MyCarConfig.currentCCToDbName = loginResult.cc + ".db";

    }
    class CheckUpdateInfoThread extends Thread
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            super.run();
            webservice.removeAllListeners();
            ApkUpdateInfoListener apkUpdateInfoListener = new ApkUpdateInfoListener(MyCarActivity.this, mHandler, httpDownloader, apkDownloadListener, webservice);
            webservice.addListener(apkUpdateInfoListener);
            webservice.queryApkUpdateInfo(Constants.MYCAR_VERSION);
        final Intent serviceIntent = new Intent(MyCarActivity.this, AutoUpdateService.class);
        startService(serviceIntent);
        }
    }
    // 处理登录消息
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case UsercenterConstants.LOGIN_RESULT: // 登录结果
                    if (D)
                        Log.i(TAG, "登录结果: " + msg.obj);
                    // 关闭进度对话框

                    switch (msg.arg1)
                    {
                        case UsercenterConstants.RESULT_SUCCESS: // 登录成功
                            // 保存登录信息
                            saveLoginInfo(msg.obj);
                            if (tvOnline != null)
                            {
                                tvOnline.setBackgroundResource(R.drawable.main_title_login);
                                updateTitleState();
                            }
                            updateLogoutNotify();
                            refreshDbsInfoSummary();
                            ChangeUserOnlineState cuos = new ChangeUserOnlineState();
                            
                            cuos.start();
                            loginState = Constants.LOGIN_STATE_LOGINED; // 已登录
                                // -----------------第二步：检查apk文件是否有更新--------------------------
                            new CheckUpdateInfoThread().start();
                            // -----------------第一步：同步服务器上该用户已经注册的序列号------------------
                            break;
                        case UsercenterConstants.RESULT_FAIL: // 登录失败
                            failLogin();
                            setCustomeTitleRight(resources.getString(R.string.default_login_state));
                            // 保存登录信息
                            sp.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                            loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                            break;
                        case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
                            failLogin();
                            setCustomeTitleRight(resources.getString(R.string.default_login_state));
                            // 保存登录信息
                            sp.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                            loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                            break;
                        default:
                            break;
                    }
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    public String getLocalDownloadBinVersion()
    {
        File tempFile = new File(UpdateCenterConstants.DBSCAR_DIR, "/temp");
        String[] files = tempFile.list();
        for (String string : files)
        {
            if (string.endsWith("Download_DBScar_"))
            {
                return (string.substring("Download_DBScar_".length() - 1, string.length() - 7)).replace("_", ".");
            }
        }
        return null;
    }

  
    
    public void refreshDbsInfoSummary()
    {
        DisplayDBSCarInfo ddbsci = new DisplayDBSCarInfo(cc, MyCarActivity.this);
        ddbsci.execute();
    }

    /**
     * 登录失败的处理，主要更新几个类变量的状态
     */
    public static void failLogin()
    {
        MyCarActivity.cc = null;
        MyCarActivity.isLogin = false; // 是否登录： 否
        MyCarActivity.token = null; // 令牌
        MyCarActivity.csInterval = 0l; // 客户端和服务端的时间间隔
    }

    /**
     * 注册广播更新标题栏的状态
     */
    private void registerTitleReceive()
    {
        // 注册广播
        IntentFilter filter = new IntentFilter(Constants.MAIN_TITLE_ACTION_WEATHER);
        this.registerReceiver(mTitleReceiver, filter);
        filter = new IntentFilter(Constants.MAIN_TITLE_ACTION_USERCENTER);
        this.registerReceiver(mTitleReceiver, filter);
        filter = new IntentFilter(Constants.Main_TITLE_ACTION_ENGINEFAULT);
        this.registerReceiver(mTitleReceiver, filter);
    }

    // 广播接收器：监听标题栏状态改变时，更新图片
    private final BroadcastReceiver mTitleReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // 天气
            if (Constants.MAIN_TITLE_ACTION_WEATHER.equals(action))
            {
                if (intent.hasExtra("weatherData"))
                {
                    String weatherData = intent.getStringExtra("weatherData");
                    if (weatherData.length() > 0)
                    {
                        showWeatherInfo(weatherData);
                    }
                }
            }
            // 用户中心
            else if (Constants.MAIN_TITLE_ACTION_USERCENTER.equals(action))
            {
                if (intent.getBooleanExtra("isLoginFlag", false))
                {
                    tvOnline.setBackgroundResource(R.drawable.main_title_login);
                    updateTitleState();
                }

            }
            // 机器故障
            else if (Constants.Main_TITLE_ACTION_ENGINEFAULT.equals(action))
            {

            }
        }
    };

    private void initSlidingDrawer()
    {

        addListenter();
        updateTitleState();
        updateDtcTitleState();
        loveCarDiagLinearLayout.setOnClickListener(new OnClickListener()
         {
          @Override
         public void onClick(View v)
         {
	         Intent intent;
	         //判断历史记录车型，是否与当前车相同，设置分数信息
	         setExamNumStr();
	         if (!exam_num.equals("")) {
		         intent = new Intent(MyCarActivity.this,DiagnoseSimpleReportActivity.class);
		         if (vehicleType != null && vehicleType.length > 0){
		         intent.putExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY,
			         DiagnoseConstant.DIAG_SP_PUSH_VALUE);
			         intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE, vehicleType[0]);
			         // 车型
			         intent.putExtra(Constants.DBSCAR_CURRENT_VERSION, version[0]); // 版本
		         }
		         startActivity(intent);
	         }
	         else{
		         intent = new Intent(MyCarActivity.this,
		         DiagnoseSelectVersionActivity.class);
		         if (vehicleType != null && vehicleType.length > 0)
		         {
			         intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE, vehicleType[0]);
			         // 车型
			         intent.putExtra(Constants.DBSCAR_CURRENT_VERSION, version[0]); // 版本
			         startActivity(intent);
		        
		         }
		         else{
		         showGotoUpdateCenter();
		         }       
	         }
         }
         });
        usercenterLinearLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent;
                // 首先判断用户是否登录
                if (isLogin)
                {
                    // 直接进入用户详细资料界面
                    intent = new Intent(MyCarActivity.this, UserInfoActivity.class);
                }
                else
                {
                    // 直接进入用户登录界面
                    intent = new Intent(MyCarActivity.this, LoginActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    private void updateDtcTitleState()
    {
    	//判断历史记录车型，是否与当前车相同，设置分数信息
         setExamNumStr();
         if(!exam_num.equals("")){
	         if(doc_num==0){
	             str = resources.getString(R.string.default_dtc_content);       
	             main_title_tv_engine_fault.setBackgroundResource(0);
	             dtcTextView.setText(str);
	         }
	         else{
		         str=resources.getString(R.string.diag_car_pre_check_num)+" "+exam_num;
		         str+=resources.getString(R.string.diag_all_test_doc)+" "+String.valueOf(doc_num)+" "+resources.getString(R.string.diag_sp_push_unit);
		         str+=resources.getString(R.string.diag_some_good_advise);
		         main_title_tv_engine_fault.setBackgroundResource(R.drawable.have_fault_code);
		         dtcTextView.setText(str);
	         }
         }
         else{
        	 if (vehicleType != null && vehicleType.length > 0)
	         {
        		 str =resources.getString(R.string.diag_all_test);
        		 main_title_tv_engine_fault.setBackgroundResource(0);
      	         dtcTextView.setText(str);
	        
	         }
	         else{
	        	 str =resources.getString(R.string.main_unregister);
	        	 main_title_tv_engine_fault.setBackgroundResource(0);
      	         dtcTextView.setText(str);
	         }   
	      
         }
    }

    private void updateTitleState()
    {
        if (MyCarActivity.isLogin && isLoginImage != null && slidingDrawerlogin != null)
        {
            isLoginImage.setBackgroundResource(R.drawable.main_title_login);
            slidingDrawerlogin.setText("CC：" + this.cc + " " + resources.getString(R.string.login_state_display_login));
        }
        else
        {
            if (isLoginImage != null)
            {
                isLoginImage.setBackgroundResource(R.drawable.main_title_logout);
            }
            if (slidingDrawerlogin != null)
            {
                slidingDrawerlogin.setText(resources.getString(R.string.login_state_display_logout));
            }
        }
    }

    public void freshWeatherInfo()
    {
        int weatherIconId = WeatherModel.getWeatherIconId();
        // 判断天气model中的是否有最新的数据
        if (weatherIconId != -1 && main_title_imageview_weather != null)
        {

            // 在状态栏中，显示天气相关图标及文字
            int weatherSmallIconResId = WeatherModel.getWeatherSmallIconResId();
            int weatherMidIconResId = WeatherModel.getWeatherMidIconResId();
            String temprature = WeatherModel.getTemprature();
            String washCarInfo = WeatherModel.getWashCarInfo();
            int washCarIconResId = WeatherModel.getWashCarIconResId();
            int tourIconResId = WeatherModel.getTourIconResId();
            String city = WeatherModel.getCity();
            String general = WeatherModel.getGeneral();
            int tourIconId = WeatherModel.getTourIconId();
            String tourInfo = WeatherModel.getTourInfo();
            int washCarIconId = WeatherModel.getWashCarIconId();

            // 天气图标
            if (weatherSmallIconResId != -1)
            {
                main_title_imageview_weather.setImageResource(weatherSmallIconResId);
                main_title_imageview_weather.setVisibility(View.VISIBLE);
            }
            else
            {
                main_title_imageview_weather.setVisibility(View.GONE);
            }

            // 气温
            if (temprature != null && temprature.length() > 0)
            {
                main_title_tv_temperature.setVisibility(View.VISIBLE);
                main_title_tv_temperature.setText(temprature);
            }
            else
            {
                main_title_tv_temperature.setVisibility(View.GONE);
            }

            // 洗车指数说明
            if (washCarInfo != null && washCarInfo.length() > 0)
            {
                main_title_tv_washcar.setVisibility(View.VISIBLE);
                main_title_tv_washcar.setText(washCarInfo);
            }
            else
            {
                main_title_tv_washcar.setVisibility(View.GONE);
            }

            // 洗车指数图标
            if (washCarIconResId != -1)
            {
                main_title_imageview_washcar.setImageResource(washCarIconResId);
                main_title_imageview_washcar.setVisibility(View.VISIBLE);
            }
            else
            {
                main_title_imageview_washcar.setVisibility(View.GONE);
            }

            // 出行指数图标
            if (tourIconResId != -1)
            {
                main_title_imageview_tour.setImageResource(tourIconResId);
                main_title_imageview_tour.setVisibility(View.VISIBLE);
            }
            else
            {
                main_title_imageview_tour.setVisibility(View.GONE);
            }
            // 在下拉区域显示天气相关图标及文字
            if (city != null && city.length() > 0)
            {
                drag_ll_weather.setVisibility(View.VISIBLE);
                // 天气
                if (weatherMidIconResId != -1)
                {
                    drag_imageview_weather.setImageResource(weatherMidIconResId);
                    drag_imageview_weather.setVisibility(View.VISIBLE);
                }
                else
                {
                    drag_imageview_weather.setVisibility(View.GONE);
                }

                drag_textview_weathe_city.setText(city);
                drag_textview_weathe_general.setText(general + " " + temprature);
                // 出行指数
                if (tourIconResId != -1)
                {
                    drag_imageview_tour.setImageResource(tourIconResId);
                    drag_imageview_tour.setVisibility(View.VISIBLE);
                    drag_textview_tour_info.setVisibility(View.VISIBLE);
                    drag_ratingbar_tour.setVisibility(View.VISIBLE);
                    drag_textview_tour_info.setText(tourIconId + " " + tourInfo);
                    drag_ratingbar_tour.setRating(tourIconId);
                }
                else
                {
                    drag_imageview_tour.setVisibility(View.GONE);
                    drag_textview_tour_info.setVisibility(View.GONE);
                    drag_ratingbar_tour.setVisibility(View.GONE);
                }

                // 洗车指数
                if (washCarIconResId != -1)
                {
                    drag_imageview_washcar.setImageResource(washCarIconResId);
                    drag_imageview_washcar.setVisibility(View.VISIBLE);
                    drag_textview_washcar_info.setVisibility(View.VISIBLE);
                    drag_ratingbar_washcar.setVisibility(View.VISIBLE);
                    drag_textview_washcar_info.setText(washCarIconId + " " + washCarInfo);
                    drag_ratingbar_washcar.setRating(washCarIconId);
                }
                else
                {
                    drag_imageview_washcar.setVisibility(View.GONE);
                    drag_textview_washcar_info.setVisibility(View.GONE);
                    drag_ratingbar_washcar.setVisibility(View.GONE);
                }
            }
        }
    }

    private void addListenter()
    {
        drag_textview_edit_weathe_city.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                startActivityForResult(new Intent(MyCarActivity.this, WeatherSetCityActivity.class), REQUEST_CODE_WEATHER_SET_CITY);

            }
        });
        drag_textview_refresh_weather.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                prepareAndShowWeather();
                Toast.makeText(getBaseContext(), R.string.refreshing_weather, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_WEATHER_SET_CITY:
                // 选择城市后 重新从服务器获取天气信息
                if (intent != null)
                {
                    if (intent.hasExtra(WeatherSetCityActivity.RESULT_WEATHER_CITY_NAME))
                    {
                        getWeatherData(intent.getStringExtra(WeatherSetCityActivity.RESULT_WEATHER_CITY_NAME));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据城市名获取天气信息
     * @param cityName
     */
    private void getWeatherData(String cityName)
    {
        new WeatherThread(this, cityName).start();

    }

    /**
     * 响应底部菜单按钮事件
     * @param v
     */
    public void MenuButton_ClickHandler(View v)
    {

        switch (v.getId())
        {
            case R.id.main_menu_my_bbs:
                Intent intent = new Intent(this, ImLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.main_menu_my_instrument:
//                if (MyCarActivity.isLogin)
//                {
//                    new SyncUserCarJob(MyCarActivity.this, new Handler()
//                    {}).doSync();
//                    new SyncOilJob(MyCarActivity.this, new Handler()
//                    {
//                        @Override
//                        public void handleMessage(Message msg)
//                        {
//                            // TODO Auto-generated method stub
//                            super.handleMessage(msg);
//                            Log.d("☆☆☆☆☆☆☆☆☆☆☆☆☆☆", "SyncOilJob success!");
//                            startActivity(new Intent(MyCarActivity.this, OilDetailActivity.class));
//                        }
//                    }).doSync();
//
//                }
//                else
//                {
//                    startActivity(new Intent(MyCarActivity.this, LoginActivity.class));
//                }
                startActivity(new Intent(MyCarActivity.this, DataFlowMain.class));
                break;
            case R.id.main_menu_my_bill:
                Intent intentBill;
                if (MyCarActivity.isLogin)
                {
                    intentBill = new Intent(MyCarActivity.this, BillingAddActivity.class);
                }
                else
                {
                    intentBill = new Intent(MyCarActivity.this, LoginActivity.class);
                }
                startActivity(intentBill);
                break;
            case R.id.main_menu_more:
                Intent intentMore = new Intent(MyCarActivity.this, MainMenuMoreActivity.class);
                startActivity(intentMore);
                overridePendingTransition(0, 0);
                break;
            default:
                break;
        }
    }

    private boolean ensureLogin()
    {
        if (isLogin)
        {
            return true;
        }
        else
        {
            if (account != null && password != null)
            {
                // 开启自动登录线程
                new LoginThread(account, password, Constants.SERVICE_LOGIN_METHOD_NAME, mHandler, MyCarActivity.this).start();
            }
            else
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            return false;
        }
    }

    /**
     * 初始化首页中车云网的摘要信息的ListView
     * @since DBS V100
     */
    private void initDbsInfo()
    {
        loadVehicleLogo();
        // loadLeftAndRight();
        dbsInfoListAdapter = new DbsInfoListAdapter(this, data);
        dbsInfoListView.setAdapter(dbsInfoListAdapter);
        dbsInfoListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (DBSCarSummaryInfo.lisenerList.size() >= position)
                {
                    IDBSCarObserve dbsCarObserve = DBSCarSummaryInfo.lisenerList.get(position);
                    dbsCarObserve.execute();
                }
            }
        });
    }

    // private VerticalTextView main_left_tv;
    // private VerticalTextView main_right_tv;
    //
    // private void loadLeftAndRight()
    // {
    // main_left_tv = (VerticalTextView) findViewById(R.id.main_left_tv);
    // main_right_tv = (VerticalTextView) findViewById(R.id.main_right_tv);
    // main_left_tv.setOnClickListener(new OnClickListener()
    // {
    //
    // @Override
    // public void onClick(View v)
    // {
    // // Intent intent = new Intent(MyCarActivity.this,
    // // DiagnoseSimpleReportActivity.class);
    // Intent intent = new Intent(MyCarActivity.this,
    // DiagnoseSelectVersionActivity.class);
    // if (vehiclePosition != -1)
    // {
    // intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
    // vehicleType[vehiclePosition]); // 车型
    // intent.putExtra(Constants.DBSCAR_CURRENT_VERSION,
    // version[vehiclePosition]); // 版本
    // Log.d(TAG, "start rapid diagonse: version = " + version[vehiclePosition]
    // + " hehicle type is:" + vehicleType[vehiclePosition]);
    // startActivity(intent);
    // }
    // else
    // {
    // showGotoUpdateCenter();
    // }
    // }
    // });
    // main_right_tv.setOnClickListener(new OnClickListener()
    // {
    //
    // @Override
    // public void onClick(View v)
    // {
    // Intent intent = new Intent(MyCarActivity.this,
    // DiagnoseSelectVersionActivity.class);
    // if (vehiclePosition != -1)
    // {
    // intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
    // vehicleType[vehiclePosition]); // 车型
    // intent.putExtra(Constants.DBSCAR_CURRENT_VERSION,
    // version[vehiclePosition]); // 版本
    // startActivity(intent);
    //
    // Log.d(TAG, "start advance diagonse: version = " +
    // version[vehiclePosition] + " hehicle type is:" +
    // vehicleType[vehiclePosition]);
    // }
    // else
    // {
    // showGotoUpdateCenter();
    // }
    // }
    // });
    // }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    class DbsInfoListAdapter extends BaseAdapter
    {

        private Context context; // 上下文
        private LayoutInflater layoutInflater; // 加载器
        private List<HashMap<String, String>> data;// 数据源

        public DbsInfoListAdapter(Context context, List<HashMap<String, String>> data)
        {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.main_dbs_info_list_item, null);
            }
            TextView tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            TextView tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);
            tvLabel.setText(data.get(position).get("label"));
            tvCount.setText(data.get(position).get("count"));
            tvUnit.setText(data.get(position).get("unit"));

            return convertView;
        }
    }

    private void registerDBSSummaryReceive()
    {
        // 注册广播
        IntentFilter filter = new IntentFilter(Constants.MAIN_DBSCAR_SUMMARY);
        this.registerReceiver(mDBSCarSummaryBroadcastReceiver, filter);
    }

    private final BroadcastReceiver mDBSCarSummaryBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String msg = intent.getStringExtra("message");
            Log.d(TAG, msg);

            if (msg.equals("refresh"))
            {
                refreshDbsInfoSummary();
            }
            else
            {
                data.clear();
                if (DBSCarSummaryInfo.notifyList.size() > 0)
                {
                    for (DBSCarInfo dbsCarInfo : DBSCarSummaryInfo.notifyList)
                    {
                        HashMap<String, String> item2 = new HashMap<String, String>();
                        item2.put("label", dbsCarInfo.label);
                        item2.put("count", dbsCarInfo.count);
                        item2.put("unit", dbsCarInfo.unit);
                        data.add(item2);
                    }
                }
                dbsInfoListAdapter.notifyDataSetChanged();
            }

        }
    };
    Bitmap[] bit = null;
    String[] version = null;
    String[] vehicleType = null;

    /**
     * 加载车标图片
     * @since DBS V100
     */
    private void loadVehicleLogo()
    {

        String[] vehicleTypePathItems;
        String path = android.os.Environment.getExternalStorageDirectory() + File.separator + Constants.ROOT_DIR + File.separator + "vehicles";

        Log.d(TAG, "车标路径：" + path);
        File files = new File(path);
        if (files == null)
        {
            return;
        }
        String[] filesFiltered = files.list(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String filename)
            {
                boolean flag = false; 
                List<String> vehicles = application.getVehicles(); 
                if(vehicles!= null && vehicles.size() > 0)
                {
                    for (String vehicle : vehicles)
                    {
                        vehicle = vehicle.toUpperCase();
                        if (vehicle.equals(filename.toUpperCase()))
                        {
                            flag = true;
                            break;
                        }
                    }
                }// TODO Auto-generated method stub
                return flag;
            }
        });
        if (filesFiltered != null && filesFiltered.length > 0)
        {
            int length = new Long(filesFiltered.length).intValue();
            Bitmap[] bitTemp = new Bitmap[length];
            String[] versionTemp = new String[length];
            String[] vehicleTypeTemp = new String[length];
            vehicleTypePathItems = filesFiltered;
            int i = 0;
            for (String vehicleTypePath : vehicleTypePathItems)
            {
                File vehicleTypeFile = new File(path + "/" + vehicleTypePath);
                // 确定该路径指向一个目录
                if (vehicleTypeFile.exists() && vehicleTypeFile.isDirectory())
                {
                    //
                    File[] versionFiles = vehicleTypeFile.listFiles(new FilenameFilter()
                    {
                        @Override
                        public boolean accept(File dir, String name)
                        {
                            return name.startsWith("V");
                        }
                    });
                    FileUtils fileUtils = new FileUtils();
                    fileUtils.sort(versionFiles, FileUtils.FileSorter.TYPE_SIZE_DOWN);
                    // 首先取得车型下面的所有版本目录
                    //
                    Map languageMap = null;
                    if (versionFiles != null && versionFiles.length > 0)
                    {
                        StringBuffer versionBuffer = new StringBuffer();
                        for (int j = 0; j < versionFiles.length; j++)
                        {
                            languageMap = new HashMap();
                            String version = versionFiles[j].getName();

                            // 取得该版本下的支持的语言
                            File versionDir = new File(path + "/" + vehicleTypePath + "/" + version);
                            // 确定该路径指向一个目录
                            if (versionDir.exists() && versionDir.isDirectory())
                            {
                                // 列出所有结尾为ggp的文件
                                final String vehicleType = vehicleTypePath;
                                File[] ggps = versionDir.listFiles(new FilenameFilter()
                                {
                                    @Override
                                    public boolean accept(File dir, String name)
                                    {
                                        return (name.endsWith(".GGP") || name.endsWith(".ggp")) && name.startsWith(vehicleType);
                                    }
                                });
                                if (ggps != null && ggps.length > 0)
                                {
                                    for (int k = 0; k < ggps.length; k++)
                                    {
                                        String ggpName = ggps[k].getName();
                                        String languageTemp = ggpName.substring(vehicleTypePath.length() + 1, ggpName.indexOf("."));
                                        languageMap.put(languageTemp, ggpName);
                                    }
                                }
                            }
                            if (languageMap.containsKey(language.toUpperCase()) || languageMap.containsKey("EN"))
                            {
                                versionBuffer.append(version);
                                versionBuffer.append("  ");
                            }
                        }
                        if (versionBuffer.length() > 0)
                        {
                            vehicleTypeTemp[i] = vehicleTypePath;
                            versionTemp[i] = versionBuffer.toString().trim();
                        }
                    }
                }
                if (versionTemp.length > 0 && versionTemp[i] != null)
                {

                    String pngPath = path + "/" + vehicleTypePath + "/ICON_" + language.toUpperCase() + ".png";
                    File png = new File(pngPath);
                    if (png != null && png.exists())
                    {
                        bitTemp[i] = BitmapFactory.decodeFile(pngPath);
                        i++;
                    }
                    else
                    {
                        pngPath = path + "/" + vehicleTypePath + "/ICON_" + "EN.png";
                        png = new File(pngPath);
                        if (png != null && png.exists())
                        {
                            bitTemp[i] = BitmapFactory.decodeFile(pngPath);
                            i++;
                        }
                        else
                        {
                            pngPath = path + "/" + vehicleTypePath + "/ICON.png";
                            png = new File(pngPath);
                            if (png != null && png.exists())
                            {
                                bitTemp[i] = BitmapFactory.decodeFile(pngPath);
                                i++;
                            }
                        }
                    }
                }
            }
            int realLength = i;
            bit = new Bitmap[realLength];
            version = new String[realLength];
            vehicleType = new String[realLength];
            for (int c = 0; c < realLength; c++)
            {
                bit[c] = bitTemp[c];
            }
            for (int j = 0; j < realLength; j++)
            {
                version[j] = versionTemp[j];
            }
            for (int k = 0; k < realLength; k++)
            {
                vehicleType[k] = vehicleTypeTemp[k];
            }
        }
        initVehicleLogo();
    }

    ViewPager viewPager;
    ArrayList<View> list;
    ViewGroup group;
    ImageView imageView;
    ImageView[] imageViews;
    int vehiclePosition = 0;
    int showBuyCount = 0; // 提示用户购买，只提示一次
    
    private void initVehicleLogo()
    {
        LayoutInflater inflater = getLayoutInflater();
        list = new ArrayList<View>();
        int length = bit == null ? 0 : bit.length;
        if (length > 0)
        {
            for (int i = 0; i < length; i++)
            {
                View view = inflater.inflate(R.layout.main_vehicle_logo_paper, null);
                LinearLayout main_vehicle_logo_ll = (LinearLayout) view.findViewById(R.id.main_vehicle_logo_ll);
                final int postion = i;
                main_vehicle_logo_ll.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MyCarActivity.this, DiagnoseSelectVersionActivity.class);
                        intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE, vehicleType[postion]); // 车型
                        intent.putExtra(Constants.DBSCAR_CURRENT_VERSION, version[postion]); // 版本
                        startActivity(intent);

                    }
                });
                ImageView main_im_vehicle_logo = (ImageView) view.findViewById(R.id.main_im_vehicle_logo);
                TextView main_vehicle_config = (TextView) view.findViewById(R.id.main_vehicle_config);
                TextView main_tv_vehicle_type = (TextView) view.findViewById(R.id.main_tv_vehicle_type);
                main_im_vehicle_logo.setImageBitmap(bit[i]);
                main_tv_vehicle_type.setText(vehicleType[i]);
                main_vehicle_config.setText(version[i]);
                list.add(view);
            }
        }
        else
        {
            vehiclePosition = -1;
            View view = inflater.inflate(R.layout.main_vehicle_logo_paper, null);

            LinearLayout main_vehicle_logo_ll = (LinearLayout) view.findViewById(R.id.main_vehicle_logo_ll);
            // main_vehicle_logo_ll.setOnClickListener(new OnClickListener()
            // {
            //
            // @Override
            // public void onClick(View v)
            // {
            // Intent intent = new Intent(MyCarActivity.this,
            // DiagnoseSelectVersionActivity.class);
            // startActivity(intent);
            //
            // }
            // });
            showGotoUpdateCenter();
            showBuyCount++;
            ImageView main_im_vehicle_logo = (ImageView) view.findViewById(R.id.main_im_vehicle_logo);
            TextView main_vehicle_config = (TextView) view.findViewById(R.id.main_vehicle_config);
            TextView main_tv_vehicle_type = (TextView) view.findViewById(R.id.main_tv_vehicle_type);
            main_im_vehicle_logo.setImageResource(R.drawable.icon);
            main_tv_vehicle_type.setText("http://www.dbscar.com");
            main_vehicle_config.setText("http://www.cnlaunch.com");
            main_im_vehicle_logo.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    startActivity(new Intent(MyCarActivity.this, UsercenterActivity.class));
                }
            });
//            main_tv_vehicle_type.setOnClickListener(new OnClickListener()
//            {
//
//                @Override
//                public void onClick(View v)
//                {
//                    // TODO Auto-generated method stubdsfs
//                    Uri uri = Uri.parse("http://www.dbscar.com");
//                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
//                }
//            });
//            main_vehicle_config.setOnClickListener(new OnClickListener()
//            {
//
//                @Override
//                public void onClick(View v)
//                {
//                    Uri uri = Uri.parse("http://www.cnlaunch.com");
//                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
//
//                }
//            });
            list.add(view);
        }
        imageViews = new ImageView[list.size()];
        // ViewGroup main = (ViewGroup) inflater.inflate(R.layout.main_manual,
        // null);
        // group是R.layou.main中的负责包裹小圆点的LinearLayout.
        ViewGroup group = (ViewGroup) findViewById(R.id.main_point_viewgroup);
        group.removeAllViews();
        viewPager = (ViewPager) findViewById(R.id.main_vehicle_logo_viewpager);
        for (int i = 0; i < list.size(); i++)
        {
            imageView = new ImageView(MyCarActivity.this);
            imageView.setLayoutParams(new LayoutParams(10, 10));
            imageView.setPadding(10, 0, 10, 0);
            imageViews[i] = imageView;
            if (i == 0)
            {
                // 默认进入程序后第一张图片被选中;
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);
            }
            else
            {
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
            }
            group.addView(imageView);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListenerListener());
    }

    CustomAlertDialog customAlertDialog;

    /**
     * 询问是否购买
     * @since DBS V100
     */
    private void showGotoUpdateCenter()
    {
        if (showBuyCount == 0)
        {
            if (customAlertDialog == null)
            {
                customAlertDialog = new CustomAlertDialog(this);
            }
            else
            {
                customAlertDialog.dismiss();
                customAlertDialog = null;
                customAlertDialog = new CustomAlertDialog(this);
            }
            customAlertDialog.setMessage(resources.getString(R.string.main_unregister));
            customAlertDialog.setTitle(resources.getString(R.string.uc_notice));
            customAlertDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(new Intent(MyCarActivity.this, DeviceActivateGuideActivity.class));
                    customAlertDialog.dismiss();
                }
            });
            customAlertDialog.setNegativeButton(R.string.cancel, new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    customAlertDialog.dismiss();

                }
            });
            customAlertDialog.show();
        }

        
    }

    class MyPagerAdapter extends PagerAdapter
    {

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object)
        {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2)
        {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).removeView(list.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1)
        {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).addView(list.get(arg1));
            return list.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState()
        {

            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0)
        {
            // TODO Auto-generated method stub

        }
    }

    class MyOnPageChangeListenerListener implements OnPageChangeListener
    {

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0)
        {
            vehiclePosition = arg0;
            for (int i = 0; i < imageViews.length; i++)
            {
                imageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
                if (arg0 != i)
                {
                    imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
                }
            }
        }
    }

    LinearLayout main_title_ll;
    LinearLayout main_notify_ll;
    LinearLayout main_diagnose;
    LinearLayout main_dbs_info_ll;
    RelativeLayout main_menu_rl;
    ExtSlidingDrawer sliding_drawer;
    RelativeLayout main_instructions;
    // ImageView main_instructions_rapid_diagnose;
    // ImageView main_instructions_rapid_diagnose_arrow;
    // ImageView main_instructions_rapid_diagnose_goto;
    ImageView main_instructions_advanced_diagnose_arrow;
    ImageView main_instructions_advance_diagnose_goto;
    ImageView main_instructions_advanced_diagnose;
    ImageView main_instructions_more_goto;
    ImageView main_instructions_more;

    // private void hideRapidDiagnose()
    // {
    // main_instructions_rapid_diagnose_arrow.setVisibility(View.INVISIBLE);
    // main_instructions_rapid_diagnose_goto.setVisibility(View.INVISIBLE);
    // main_instructions_rapid_diagnose.setVisibility(View.INVISIBLE);
    // }

    private void hideAdvancedDiagnose()
    {
        main_instructions_advanced_diagnose_arrow.setVisibility(View.INVISIBLE);
        main_instructions_advance_diagnose_goto.setVisibility(View.INVISIBLE);
        main_instructions_advanced_diagnose.setVisibility(View.INVISIBLE);
    }

    private void hideMore()
    {
        main_instructions_more_goto.setVisibility(View.INVISIBLE);
        main_instructions_more.setVisibility(View.INVISIBLE);
    }

    private void openAdvanced()
    {
        main_instructions_advanced_diagnose_arrow.setVisibility(View.VISIBLE);
        main_instructions_advance_diagnose_goto.setVisibility(View.VISIBLE);
        main_instructions_advanced_diagnose.setVisibility(View.VISIBLE);
    }

    private void openMore()
    {
        main_instructions_more_goto.setVisibility(View.VISIBLE);
        main_instructions_more.setVisibility(View.VISIBLE);
    }

    private void initInstructions()
    {
        main_instructions = (RelativeLayout) findViewById(R.id.main_instructions);
        main_title_ll = (LinearLayout) findViewById(R.id.main_title_ll);
        main_notify_ll = (LinearLayout) findViewById(R.id.main_notify_ll);
        main_diagnose = (LinearLayout) findViewById(R.id.main_diagnose);
        main_dbs_info_ll = (LinearLayout) findViewById(R.id.main_dbs_info_ll);
        main_menu_rl = (RelativeLayout) findViewById(R.id.main_menu_rl);
        sliding_drawer = (ExtSlidingDrawer) findViewById(R.id.sliding_drawer);
        // main_instructions_rapid_diagnose = (ImageView)
        // findViewById(R.id.main_instructions_rapid_diagnose);
        // main_instructions_rapid_diagnose_arrow = (ImageView)
        // findViewById(R.id.main_instructions_rapid_diagnose_arrow);
        // main_instructions_rapid_diagnose_goto = (ImageView)
        // findViewById(R.id.main_instructions_rapid_diagnose_goto);
        // main_instructions_advanced_diagnose_arrow = (ImageView)
        // findViewById(R.id.main_instructions_advanced_diagnose_arrow);
        // main_instructions_advance_diagnose_goto = (ImageView)
        // findViewById(R.id.main_instructions_advance_diagnose_goto);
        // main_instructions_advanced_diagnose = (ImageView)
        // findViewById(R.id.main_instructions_advanced_diagnose);
        // main_instructions_more_goto = (ImageView)
        // findViewById(R.id.main_instructions_more_goto);
        // main_instructions_more = (ImageView)
        // findViewById(R.id.main_instructions_more);
        if (sp.getBoolean(Constants.IS_FIRST_USE, false))
        {
            main_instructions.setVisibility(View.VISIBLE);
            main_title_ll.setEnabled(false);
            main_notify_ll.setEnabled(false);
            main_diagnose.setEnabled(false);
            main_dbs_info_ll.setEnabled(false);
            main_menu_rl.setEnabled(false);
            sliding_drawer.setEnabled(false);
            main_instructions.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    main_title_ll.setEnabled(true);
                    main_notify_ll.setEnabled(true);
                    main_diagnose.setEnabled(true);
                    main_dbs_info_ll.setEnabled(true);
                    main_menu_rl.setEnabled(true);
                    sliding_drawer.setEnabled(true);
                    main_instructions.setVisibility(View.INVISIBLE);
                }
            });
            // hideMore();
            // openAdvanced();
            // main_instructions_rapid_diagnose.setOnClickListener(new
            // OnClickListener()
            // {
            //
            // @Override
            // public void onClick(View v)
            // {
            // hideRapidDiagnose();
            // openAdvanced();
            //
            // }
            // });

            // main_instructions_advanced_diagnose.setOnClickListener(new
            // OnClickListener()
            // {
            //
            // @Override
            // public void onClick(View v)
            // {
            // //hideRapidDiagnose();
            // hideAdvancedDiagnose();
            // openMore();
            //
            // }
            // });
            // main_instructions_more.setOnClickListener(new OnClickListener()
            // {
            //
            // @Override
            // public void onClick(View v)
            // {
            // main_instructions.setVisibility(View.INVISIBLE);
            // }
            // });
            sp.edit().putBoolean(Constants.IS_FIRST_USE, false).commit();
        }
        else
        {
            main_title_ll.setEnabled(true);
            main_notify_ll.setEnabled(true);
            main_diagnose.setEnabled(true);
            main_dbs_info_ll.setEnabled(true);
            main_menu_rl.setEnabled(true);
            sliding_drawer.setEnabled(true);
            main_instructions.setVisibility(View.INVISIBLE);
        }
    }

    // 获取当前车型与版本
    public void getCurrentCarTypeAndVer()
    {
        SharedPreferences carTypeVer = getSharedPreferences(DiagnoseConstant.DIAG_CAR_TYPE_VER, Context.MODE_WORLD_WRITEABLE);
        carTypeVer.edit().clear().commit();
        if (vehicleType != null && vehicleType.length > 0)
        {

            carTypeVer.edit().putString(Constants.DBSCAR_CURRENT_CAR_TYPE, vehicleType[0]).commit();
            carTypeVer.edit().putString(Constants.DBSCAR_CURRENT_VERSION, version[0]).commit();
        }
        else
        {
            carTypeVer.edit().putString(Constants.DBSCAR_CURRENT_CAR_TYPE, "").commit();
            carTypeVer.edit().putString(Constants.DBSCAR_CURRENT_VERSION, "").commit();
        }
    }
    //判断历史记录车型，是否与当前车相同，设置相关信息
    public void setExamNumStr(){
        SharedPreferences preQuestionInfo =MyCarActivity.this.getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
        String pre_car_type = preQuestionInfo.getString(Constants.DBSCAR_CURRENT_CAR_TYPE, "");
        if(vehicleType!=null && vehicleType.length>0){
	     	if(!pre_car_type.equals(vehicleType[0])){
	     		exam_num = "";
			}
        }else{
       	 	exam_num = "";
        }  	
    }
    
    public static boolean incomingFlag = false;  
    public static boolean updating = false;
    class CallReceiver extends BroadcastReceiver
    {
        private boolean D = true;
        private final static String TAG = "CallReceiver";
        private Context context;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if(D) Log.d(TAG, "CallReceiver is start...");
//            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            CallListener customPhoneListener = new CallListener(context);
//            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            this.context = context;
            Bundle bundle = intent.getExtras();
            
            String incoming_number = bundle.getString("incoming_number");
            Log.d(TAG, "CallReceiver Phone Number :" + incoming_number);
            //如果是拨打电话   
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                          
                    incomingFlag = false;  
                    String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          
                    Log.i(TAG, "call OUT:"+phoneNumber);                          
            }else{                          
                    //如果是来电   
                    TelephonyManager tm =   
                        (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
                    incomingFlag = true;//标识当前是来电  

                    switch (tm.getCallState()) {  
                    case TelephonyManager.CALL_STATE_RINGING:  
                             
                            incoming_number = intent.getStringExtra("incoming_number");  
                            Log.i(TAG, "RINGING :"+ incoming_number);  
                            break;  
                    case TelephonyManager.CALL_STATE_OFFHOOK:                                  
                            if(incomingFlag){  
                                    Log.i(TAG, "incoming ACCEPT :"+ incoming_number);  
                            }  
                            break;  
                      
                    case TelephonyManager.CALL_STATE_IDLE:       
                        Log.i(TAG, "incomingFlag :"+ incomingFlag);  
                        Log.i(TAG, "updating :"+ updating);  
                            if(incomingFlag && updating){  
                                    Log.i(TAG, "incoming IDLE");       
//                                    [vehiecle=USAFORD, version=V10.10, language=CN, serialNumber=963890000167, 
//                                        fileAbsolutePath=/mnt/sdcard/cnlaunch, upadteType=2]
//                                    DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
//                                    params.setSerialNumber("963890000167");
//                                    params.setVehiecle("USAFORD");
//                                    params.setVersion("V10.10");
//                                    params.setLanguage("CN");
//                                    params.setFileAbsolutePath("/mnt/sdcard/cnlaunch");
//                                    params.setUpadteType(2);
                                    if (MyCarApplication.params != null)
                                    {
                                        MyCarApplication.params.setUpadteType(2);
                                        Intent intent1 = new Intent(this.context,FirmwareUpdate.class);
                                        intent1.putExtra("diagsoft_update_config_params", MyCarApplication.params);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
                                    }
                                    incomingFlag = false;
                            }  
                            break;  
                    }   
            }  
        }
    }
    CallReceiver callReceiver = new CallReceiver();
    private void registerPhoneState()
    {
        IntentFilter counterActionFilter = new IntentFilter("android.intent.action.PHONE_STATE");    
        registerReceiver(callReceiver, counterActionFilter);   
    }
    private void unRegisterPhoneState()
    {
        unregisterReceiver(callReceiver);
    }
}
