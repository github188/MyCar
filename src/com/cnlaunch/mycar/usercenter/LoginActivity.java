package com.cnlaunch.mycar.usercenter;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cnlaunch.mycar.ChangeUserOnlineState;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.ui.SpinnerView;
import com.cnlaunch.mycar.common.ui.SpinnerView.SpinnserPopuViewListener;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.DiagSoftUpdateConfigParams;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.usercenter.database.User;
import com.cnlaunch.mycar.usercenter.database.UsercenterDao;
import com.cnlaunch.mycar.usercenter.model.LoginResult;
import com.cnlaunch.mycar.usercenter.model.WSUser;
import com.j256.ormlite.dao.Dao;

/**
 * 登录界面
 * @author xiangyuanmao
 */
public class LoginActivity extends BaseActivity implements OnClickListener, SpinnserPopuViewListener
{

    // 调试log信息target
    private static final String TAG = "LoginActivity";
    private static final boolean D = true;

    ProgressDialog pdlg; // 登录对话框

    // ------------------ 界面元素--------------------------------
    private Button btnLogin; // 登录按钮
    private Button btnRegister; // 注册按钮
    private Button btnForgetPwd; // 忘记密码按钮
    private Button btnSkip; // 跳过按钮
    private CheckBox cbAutoLogin; // 自动登录复选框
    private CheckBox cbSavePwd; // 保存密码复选框
    private EditText etAccount; // 账号编辑框
    private EditText etPwd; // 密码编辑框

    // ------------------
    // 用于回显注册成功后从服务端得到的CC号和初始密码--------------------------------
    private String account; // 账号
    private String password; // 密码
    private boolean isFinish = false;

    // ------------------标题栏上的图片及------------------
    ImageView main_title_imageview_weather;
    TextView main_title_tv_temperature;
    ImageView main_title_imageview_tour;
    ImageView main_title_imageview_washcar;
    TextView main_title_tv_washcar;

    private static final int RETRIEVED_PASSWORD_BY_MOBILE = 0; // 通过手机找回密码
    private static final int RETRIEVED_PASSWORD_BY_QUESTION = 1; // 通过密码提示问题找回密码

    // ------------------ 用于回显登录记录的域--------------------------------
    SharedPreferences loginSP; // 登录的SharedPreferences
    Map accountsMap; // 账号Map，主要用于回显已登录记录

    private SpinnerView spinnerView; // 弹出已登录的记录框
    private PopupWindow hiphop = null; // 弹出窗口，用于实现弹出登录记录
    private boolean isOpenProgress = false; // 是否打开进度框

    private Resources resources;
    private int forward = 0; // 登录成功后的跳转去向：0:finish掉自己，1:回到主界面，2:用户中心详细界面

    Dao<User, Integer> dao;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 去掉标题
        setContentView(R.layout.usercenter_login, R.layout.custom_title);
        setCustomeTitleLeft(R.string.usercenter_dbscar_login);
        setCustomeTitleRight("");
        Intent intent = getIntent();
        forward = intent.getIntExtra("forward", 0);
        /* 获取界面元素的引用，并添加监听事件 */
        getPreferenceAndListerner();
        resources = getResources();
        spinnerView = (SpinnerView) findViewById(R.id.lesson_alldata);
        spinnerView.setPopuView(this);
        dao = getHelper().getDao(User.class);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isOpenProgress = true;
    }



    @Override
    protected void onStop()
    {
        super.onStop();
        stopProgressDialog();
        isOpenProgress = false;
    }

    /**
     * 实现下拉列表的弹出方式
     * @param v 动画对象
     * @param isShow
     * @see com.cnlaunch.mycar.common.ui.SpinnerView.SpinnserPopuViewListener#show(android.widget.ViewFlipper, boolean)
     * @since DBS V100
     */
    @Override
    public void show(ViewFlipper v, boolean isShow)
    {
        if (isShow)
        {
            // 实例化一个弹出窗口
            hiphop = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            // 设置其背景图片
            hiphop.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            hiphop.setFocusable(true);
            hiphop.setTouchable(true);
            hiphop.setOnDismissListener(new OnDismissListener()
            {

                @Override
                public void onDismiss()
                {
                    spinnerView.setBackgroundResource(R.drawable.usercenter_button2_over);
                    spinnerView.setFlag(false);
                }
            });
            // 分别设置弹出框向X方向的偏移量为200个像素，Y的方向为2个像素，
            // 但请注意，这个偏移量是相对于父控件来说的
            hiphop.showAsDropDown(spinnerView, -260, 2);
        }
        else
        {
            if (hiphop != null || hiphop.isShowing())
            {
                hiphop.dismiss();
            }
        }
    }

    /**
     */
    private void nextForward()
    {
        Intent intent;
        switch (forward)
        {
            case 0:
                LoginActivity.this.finish();
                break;
            case 1:
                intent = new Intent(this, MyCarActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                break;
            case 2:
                intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
        }
    }

    /**
     * 某一选项被选中的事件处理
     * @param listView 下拉列表的引用
     * @param position 该选项在列表中的位置
     * @since DBS V100
     */
    @Override
    public void onItemClick(ListView listView, int position)
    {
        String[] accountsArray = getAccounts();
        String account = accountsArray[position];
        etAccount.setText(account);
        etPwd.setText(accountsMap.get(account).toString());
    }

    /**
     * 从SharedPreferences里面取出登录记录，key是CC号码，value就是密码
     * @return
     */
    private String[] getAccounts()
    {
        // 从SharedPreferences里面取出登录记录
        loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        accountsMap = loginSP.getAll();
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
     * 重写OnClickListener接口的方法
     */
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.usercenter_btn_login:// 登录
                executeLogin(); // 执行登录
//              DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
//              params.setSerialNumber("963890001047");
//              params.setVehiecle("BENZ");
//              params.setVersion("V10.01");
//              params.setLanguage("EN");
//              params.setFileAbsolutePath("/mnt/sdcard/cnlaunch");
//              params.setUpadteType(2);
//              Intent intent1 = new Intent(this,FirmwareUpdate.class);
//              intent1.putExtra("diagsoft_update_config_params", params);
//              startActivity(intent1);
                break;

            case R.id.usercenter_btn_register:// 注册
                executeRegister(mHandler);
                break;

            case R.id.usercenter_btn_forget_pwd: // 忘记密码
                retrievedPassword(mHandler);
                break;

            case R.id.usercenter_cb_auto_login: // 自动登录
                break;

            case R.id.usercenter_cb_save_pwd: // 保存密码
                break;

            case R.id.usercenter_btn_skip: // 跳过
                Log.d(TAG, "直接跳过登录到主界面");
                Intent intent = new Intent(LoginActivity.this, MyCarActivity.class);
                startActivity(intent);
                this.finish();
            default:
                break;
        }
    }

    /**
     * 执行注册
     * @param handler
     */
    private void executeRegister(Handler handler)
    {

        Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 隐藏软键盘 根据SE闫工的要求，在密码输入框获得焦点时，系统弹出软件盘，点击屏幕的任意区域关闭软键盘
     * @author xiangyuanmao
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (etPwd.requestFocus())
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPwd.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 执行登录
     */
    private void executeLogin()
    {

        String account = etAccount.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        if (account == null || account.equals(""))
        {
            dialog(resources.getString(R.string.uc_account_is_null));
            return;
        }
        if (!UserCenterCommon.checkAccountRegular(account))
        {
            dialog(resources.getString(R.string.uc_account_is_illegal));
            return;
        }

        if (!UserCenterCommon.checkPasswordRegular(password))
        {

            dialog(resources.getString(R.string.uc_password_is_illegal));
            return;
        }
        // 弹出登录对话框
        pdlg = new ProgressDialog(this);
        // pdlg.setProgressDrawable(new Drawable(R.drawable.waiting));
        pdlg.setMessage(resources.getString(R.string.usercenter_logining));
        pdlg.show();

        // 执行登录
        new LoginThread(account, password, Constants.SERVICE_LOGIN_METHOD_NAME, mHandler,this).start();
    }

    class DialogOnClickListener implements DialogInterface.OnClickListener
    {
        private String loginKey;

        DialogOnClickListener(String loginKey)
        {
            this.loginKey = loginKey;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            // TODO Auto-generated method stub
            switch (which)
            {
                case RETRIEVED_PASSWORD_BY_MOBILE:
                    showPasswordNoticeQuestion(loginKey);
                    break;
                case RETRIEVED_PASSWORD_BY_QUESTION:
                    showPasswordNoticeQuestion(loginKey);
                    break;
            }
            dialog.dismiss();
        }
    }

    /**
     * 执行找回密码
     */
    private void retrievedPassword(Handler handler)
    {


        // 帐号控件不能为空
        String account = etAccount.getText().toString().trim();
        if (account.toString().trim().length() == 0)
        {
            dialog(resources.getString(R.string.usercenter_account_is_not_null));
            return;
        }
        if (!UserCenterCommon.checkAccountRegular(account.toString()))
        {
            dialog(resources.getString(R.string.usercenter_illegal_account));
            return;
        }

        final DialogOnClickListener docl = new DialogOnClickListener(account);
        new CustomDialog(this)
        .setTitle(resources.getString(R.string.usercenter_plese_choice))
        .setIcon(android.R.drawable.ic_dialog_info)
        .setItems(new String[] { resources.getString(R.string.usercenter_password_tip_question) }, docl)
        .show();
    }

    private int questionId;

    class SelectQuestionListener implements OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            // TODO Auto-generated method stub
            questionId = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            // TODO Auto-generated method stub

        }
    }

    private void showPasswordNoticeQuestion(String loginKey)
    {
        Intent intent = new Intent(LoginActivity.this, PasswordQuestionActvity.class);
        // 指明是从登录界面跳转到找回密码界面的
        intent.putExtra("loginKey", loginKey);
        intent.putExtra(PasswordQuestionActvity.ORIGINAL_ACTIVITY, PasswordQuestionActvity.ACTIVITY_LOGIN);
        startActivity(intent);
    }

    /**
     * 登录异常弹出对话框
     * @param message
     */
    protected void dialog(String message)
    {

        if (isOpenProgress)
        {
            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
            customAlertDialog.setMessage(message);
            customAlertDialog.setTitle(resources.getString(R.string.uc_notice));
            customAlertDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    customAlertDialog.dismiss();
                    etAccount.setText(account);
                    etPwd.setText(password);
                }
            });
            customAlertDialog.show();
        }
    }
    // 处理登录消息
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            SharedPreferences settings = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
            switch (msg.what)
            {
                case UsercenterConstants.LOGIN_RESULT: // 登录结果
                    if (D)
                        Log.i(TAG, "登录结果: " + msg.obj);
                    // 获得SharedPreferences对象
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.RESULT_SUCCESS: // 登录成功
                            // 保存登录信息
                            saveLoginInfo(settings, msg.obj);
                            LoginResult loginResult = (LoginResult) msg.obj;
                            syncUserinfoFromService(loginResult.cc);

                            break;
                        case UsercenterConstants.RESULT_FAIL: // 登录失败
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                            // 关闭进度对话框
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // 更新类变量的状态
                            // 保存登录信息
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                            // 关闭进度对话框
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // 更新类变量的状态
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                            // 关闭进度对话框
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // 更新类变量的状态
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                            dialog(msg.obj.toString());
                            break;
                            default:
                                MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                                // 关闭进度对话框
                                stopProgressDialog();
                                MyCarActivity.failLogin(); // 更新类变量的状态
                                settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
                                dialog(msg.obj.toString());
                                break;
                    }
                    break;
                case UsercenterConstants.USERCENTER_RESULT_RETRIVED_PWD: // 找回密码
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_SUCCESS: // 找回密码成功
                            // 关闭进度对话框
                            stopProgressDialog();
                            // 弹出对话框，显示CC号和密码
                            dialog(resources.getString(R.string.usercenter_retrieve_password_success));
                            break;
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_FAILED: // 找回密码失败
                            // 关闭进度对话框
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_EXCEPTION: // 找回密码异常
                            // 关闭进度对话框
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;

                        default:
                            // 关闭进度对话框
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;
                    }
                    break;
                case UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT: // 从服务端同步用户信息到手机
                    switch(msg.arg1)
                    {
                    case UsercenterConstants.RESULT_SUCCESS: // 获取用户资料成功
                        Log.d(TAG, "获取用户资料成功！");
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGINED; // 已登录
                        // 获得从WebService返回的用户信息结果对象
                        WSUser wsUser = (WSUser)msg.obj;
                        
                        // 刷新本地数据库中的User表
                        dao = getHelper().getDao(User.class);
                        UsercenterDao.updateUser(resources,dao,wsUser); 
                        // 关闭进度对话框
                        stopProgressDialog();
                        ChangeUserOnlineState cuos = new ChangeUserOnlineState();
                        cuos.executeAfterLogin();
                        nextForward();
                        break;
                    case UsercenterConstants.RESULT_FAIL: // 获取用户资料失败
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                        // 关闭进度对话框
                        stopProgressDialog();
                        // 弹出对话框提示用户失败原因
                        dialog(msg.obj.toString());
                        break;
                    case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
                        // 关闭进度对话框
                        stopProgressDialog();
                        // 弹出对话框提示用户失败原因
                        dialog(resources.getString(R.string.usercenter_login_exception));
                        break;
                    }
                    break;
            }
        }
    };

    /**
     * 保存登录信息
     * @param settings
     * @param obj
     */
    private void saveLoginInfo(SharedPreferences settings, Object obj)
    {
        // 保存登录信息
        settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGIN).commit(); // 已登录

        // 如果选择了自动登录复选框
        if (cbAutoLogin.isChecked())
        {
            settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, true).commit(); // 自动登录
            settings.edit().putString(UsercenterConstants.LAST_LOGIN_PWD, etPwd.getText().toString().trim()).commit(); // 密码
        }
        else
        {
            settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, false).commit(); // 不自动登录
        }
        // 如果选择了保存密码
        if (cbSavePwd.isChecked())
        {
            settings.edit().putString(UsercenterConstants.LAST_LOGIN_PWD, etPwd.getText().toString().trim()).commit();
        }
        LoginResult loginResult = (LoginResult) obj;
        settings.edit().putString(UsercenterConstants.LAST_LOGIN_ACCOUNT, etAccount.getText().toString().trim()).commit(); // 登录账号
        settings.edit().putString(UsercenterConstants.LOGIN_TOKEN, loginResult.token).commit(); // 登录令牌
        settings.edit().putLong(UsercenterConstants.LOGIN_SERVICE_TIME, loginResult.serverSystemTime).commit(); // 服务器时间
        settings.edit().putString(UsercenterConstants.LOGIN_CC, loginResult.cc).commit(); // cc

        // 指定当前数据库名称
        MyCarConfig.currentCCToDbName = loginResult.cc + ".db";
        loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

        if (cbSavePwd.isChecked() || cbAutoLogin.isChecked())
        {
            loginSP.edit().remove(etAccount.getText().toString().trim()).commit();
            loginSP.edit().putString(etAccount.getText().toString().trim(), etPwd.getText().toString().trim()).commit(); // 记录登录
        }
        else
        {
            loginSP.edit().putString(etAccount.getText().toString().trim(), "").commit(); // 记录登录
        }
        MyCarActivity.cc = loginResult.cc; // CC号码
        MyCarActivity.password = etPwd.getText().toString().trim() ;
        MyCarActivity.isLogin = true; // 是否登录： 是
        MyCarActivity.token = loginResult.token; // 令牌
        MyCarActivity.csInterval = new Date().getTime() - loginResult.serverSystemTime; // 客户端和服务端的时间间隔
        MyCarActivity.accountsArray = getAccounts(); // 给登录记录重新赋值

        // 发送广播更新登录状态
        Intent intent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
        intent.putExtra("isLoginFlag", true);
        sendBroadcast(intent);

        // 发送广播更新登录状态
        Intent titleIntent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
        intent.putExtra("isLoginFlag", true);
        sendBroadcast(titleIntent);
    }

    /**
     * 获取界面元素的引用并为界面元素设置单击监听事件
     */
    private void getPreferenceAndListerner()
    {
        btnLogin = (Button) findViewById(R.id.usercenter_btn_login); // 获得登录按钮的引用
        btnRegister = (Button) findViewById(R.id.usercenter_btn_register); // 获得注册按钮的引用
        btnForgetPwd = (Button) findViewById(R.id.usercenter_btn_forget_pwd); // 获得忘记密码按钮的引用
        btnSkip = (Button) findViewById(R.id.usercenter_btn_skip);
        cbAutoLogin = (CheckBox) findViewById(R.id.usercenter_cb_auto_login); // 获得自动登录复选框引用
        cbSavePwd = (CheckBox) findViewById(R.id.usercenter_cb_save_pwd); // 获得保存密码复选框引用
        etAccount = (EditText) findViewById(R.id.usercenter_et_account); // 账号编辑框
        etPwd = (EditText) findViewById(R.id.usercenter_et_pwd); // 密码编辑框
        cbAutoLogin.setChecked(true); // 总是自动登录
        cbSavePwd.setChecked(true); // 总是保存密码
        /* 为界面元素设置单击监听事件 */
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgetPwd.setOnClickListener(this);
        cbAutoLogin.setOnClickListener(this);
        cbSavePwd.setOnClickListener(this);
        cbAutoLogin.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    /**
     * 从WebService公共层发来的广播，当向WebService向服务端请求数据的令牌（token）
     * 实效时，需要通知登录模块启动登录重新获取令牌。
     * @author xiangyuanmao
     */
    private class WebServiceBroadcastReciver extends BroadcastReceiver
    {
        SharedPreferences myCarShared = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // 如果是WebService公共层发来的广播
            if (action.equals("com.cnlaunch.webservice.broadcast"))
            {
                // 提取响应代码
                int responseCode = intent.getIntExtra("responseCode", 0);

                // 如果令牌实效，重新登陆
                if (responseCode == UsercenterConstants.RESPONSE_TOKEN_TIMEOUT)
                {
                    // 取得MyCarActivity里面的账号密码
                    String account;
                    String password;
                    // 如果自动登录
                    if (myCarShared.getBoolean(UsercenterConstants.LAST_LOGIN_ACCOUNT, false))
                    {
                        account = myCarShared.getString(UsercenterConstants.LAST_LOGIN_PWD, null);
                        password = myCarShared.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null);
                        new LoginThread(account, password, Constants.SERVICE_LOGIN_METHOD_NAME, mHandler,LoginActivity.this).start();
                    }
                }
            }
        }
    }

    /**
     * 从其他界面返回本界面时调用
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode)
        {
            case UsercenterConstants.REQUEST_CODE_WEBSERVICE: // 从Webservice发送过来的意图
                // 设置是否结束位true，这样当登录成功后关闭本页面
                if (resultCode == Activity.RESULT_OK)
                {
                    isFinish = true;
                }
                break;
        }
    }

    /**
     * 关闭进度对话框
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
     * 开启进度对话框
     * @param pdlg
     * @param message
     */

    private void startProgressDialog(String message)
    {
        if (isOpenProgress)
        {
            if (pdlg == null)
            {
                // 实例化一个进度框
                pdlg = new ProgressDialog(this);
            }
            else if (pdlg.isShowing())
            {
                pdlg.dismiss();
            }
            pdlg.setMessage(message);
            pdlg.show();
        }
    }
    /**
     * 同步服务端的数据到本地
     * @param cc
     */
    private void syncUserinfoFromService(final String cc)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    // 封装请求参数
                    TreeMap paraMap = new TreeMap();
                    paraMap.put("cc", cc);
                    RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USERCENTER, "getUser", null, paraMap,true);
                    WebServiceManager wsm = new WebServiceManager(LoginActivity.this,requestParameter);
                    SoapObject result;
                    WSBaseResult wSBaseResult = (WSBaseResult)wsm.execute();  
                    if (wSBaseResult.responseCode == 0)
                    {
                        if (wSBaseResult.object instanceof SoapObject)
                        {
                            result = (SoapObject)wSBaseResult.object;
                            if (result != null && result.getProperty(0) != null)
                            {
                                SoapObject so = (SoapObject)result.getProperty(0);
                                WSUser wSUser = new WSUser();
                                so.hasProperty("");
                                wSUser.cc = so.hasProperty("userId") ?  so.getProperty("userId").toString() : "";
                                wSUser.nickname = so.hasProperty("nickName") ? so.getProperty("nickName").toString() : "";
                                wSUser.userName = so.hasProperty("userName") ? so.getProperty("userName").toString() : "";
                                wSUser.mobile = so.hasProperty("mobile") ? so.getProperty("mobile").toString() : "";
                                wSUser.email = so.hasProperty("email") ?  so.getProperty("email").toString() : "";
                                wSUser.isBindEmail = so.hasProperty("isBindEmail") ?  so.getProperty("isBindEmail").toString() : "";
                                wSUser.isBindMobile = so.hasProperty("isBindMobile") ?  so.getProperty("isBindMobile").toString() : "";
                                int isSuccess = new Integer(so.getProperty("code").toString()).intValue();
                                // 通知UI主线程登录结果
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, wSUser).sendToTarget();;
                            }
                            else
                            {
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
                                .sendToTarget();
                            }
                        }
                    }
                    // 发生IO异常
                    else if (wSBaseResult.responseCode == 2)
                    {
                        if (D) Log.d(TAG, "从服务器同步用户信息发生IO异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin))
                        .sendToTarget();
                    }
                    // 发生xml解析异常
                    else if (wSBaseResult.responseCode == 3)
                    {
                        if (D) Log.d(TAG, "从服务器同步用户信息发生Xml解析异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,  resources.getString(R.string.usercenter_service_parse_xml_exception))
                        .sendToTarget();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (D) Log.d(TAG, "从服务器同步用户信息发生异常");
                    // 通知UI主线程登录结果
                    mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception))
                    .sendToTarget();
                }   
            }
         }.start();
     }

}
