package com.cnlaunch.mycar.usercenter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.ChangeUserOnlineState;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.usercenter.database.User;
import com.cnlaunch.mycar.usercenter.database.UsercenterDao;
import com.cnlaunch.mycar.usercenter.model.WSUser;
import com.j256.ormlite.dao.Dao;

/**
 * 用户详细信息界面
 * @author xiangyuanmao
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener
{
    // 调试log信息target
    private static final String TAG = "UserInfoActivity";
    private static final boolean D = true;
    private static final int SET_PASSWORD_QUESTION = 1; // 设置密码找回问题
    private static final int CHANGE_PASSWORD = 0; // 修改密码
    /*
     * 为了使用列表的形式显示用户详细资料在屏幕，需要用到三个对象ListView、继承自BaseAdapter的子类UserInfoAdapter、
     * 和一个泛型列表ArrayList，里面的模板是一个Map，存储显示的文本
     */
    private ListView listView; // 用户信息的列表ListView对象
    private UserInfoAdapter adapter; // 用户信息列表的Adapter
    private ArrayList<HashMap<String, Object>> userinfoList; // 用户信息内容
    final Dao<User, Integer> dao = getHelper().getDao(User.class);
    ProgressDialog pdlg; // 对话框，当向服务端通信时用于改善用户体验的进度对话框
    private static String cc;
    private Button btnRefresh;
    private Button btnPasswordManage;
    private Button btnLogout;
    private Resources resources;

    private boolean isRuning = false;
    @Override
    protected void onStart()
    {
        super.onStart();
        isRuning = true;
    }

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        isRuning = false;
    }
    /**
     * 覆盖了基类的方法
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usercenter_info, R.layout.custom_title);
        setCustomeTitleLeft(R.string.usercenter);
        setCustomeTitleRight(R.string.usercenter_userinfo);
        resources = getResources();
        cc = MyCarActivity.cc;
        initViews(); // 初始化列表
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /**
     * 初始化列表
     */
    private void initViews()
    {

        /*
         * UI元素初始化
         */
        listView = (ListView) this.findViewById(R.id.usercenter_lv_userinfo); // 获取ListView的引用
        userinfoList = new ArrayList<HashMap<String, Object>>(); // 初始化存储用户信息的ArrayList

        /*
         * 数据库操作对象的初始化，得到一个数据库操作接口的实例，参数是User类，相当于以User.java类作为
         * 模板构造表结构，因为我们为每个用户创建了一个数据库，所以User表的结构除了主键ID就只有用户属性
         * 的显示名称和显示值，所以我们需要用一个数组存储用户表的所有记录组合而成完整的用户信息
         */
        List<User> userList; // 注意，此处的结构是用户的单个属性，而不是整个用户信息
        try
        {
            userList = dao.queryForAll(); // 得到用户信息的所有属性并压入userinfoList数组
            if (userList != null && userList.size() > 0)
            {
                for (User user : userList)
                {
                    HashMap<String, Object> userMap = new HashMap<String, Object>();
                    userMap.put(UsercenterConstants.USERCENTER_USERINFO_LABEL, user.getLabel());
                    userMap.put(UsercenterConstants.USERCENTER_USERINFO_VALUE, user.getValue() == null ? "" : user.getValue());
                    userMap.put(UsercenterConstants.USERCENTER_USERINFO_ID, user.getId());
                    userinfoList.add(userMap);
                }
            }
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 初始化Adapter
        adapter = new UserInfoAdapter(this, userinfoList);

        // 设置Adapter到ListView，此时用户信息列表有值，可以显示在屏幕，也可以通过更新adapter刷新屏幕
        listView.setAdapter(adapter);

        btnRefresh = (Button) findViewById(R.id.uc_userinfo_refresh);
        btnPasswordManage = (Button) findViewById(R.id.uc_password_manage);
        btnLogout = (Button) findViewById(R.id.uc_userinfo_logout);
        btnRefresh.setOnClickListener(this);
        btnPasswordManage.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    /**
     * 取得向服务端提交更新字段的名称key
     * @param position
     * @return
     */
    private String getAttName(int position)
    {
        String attName = "";
        switch (position)
        {
            case 1:
                attName = "nickName";
                break;
            case 2:
                attName = "userName";
                break;
            case 3:
                attName = "email";
                break;
            case 4:
                attName = "mobile";
                break;
            default:
                attName = "";
                break;
        }
        return attName;
    }

    /**
     * 校验更新用户信息的输入格式
     * @param itemPostion
     * @param value
     */
    private boolean checkUpdate(int itemPostion, String value)
    {
        switch (itemPostion)
        {
            case 1:
                if (!UserCenterCommon.checkStrLength(value, 20, 1) || !UserCenterCommon.checkStrLength(value, 1, 2))
                {
                    dialog(resources.getString(R.string.uc_nickname_illegal));
                    return false;
                }
                break;
            case 2:
                if (!UserCenterCommon.checkStrLength(value, 20, 1) || !UserCenterCommon.checkStrLength(value, 5, 2))
                {
                    dialog(resources.getString(R.string.uc_username_is_illegal_1));
                    return false;
                }
                else if (!UserCenterCommon.isUserName(value))
                {
                    dialog(resources.getString(R.string.uc_username_is_illegal));
                    return false;
                }
                break;
            case 3:
                if (!UserCenterCommon.checkEmailStr(value))
                {
                    dialog(resources.getString(R.string.uc_email_is_illegal));
                    return false;
                }
                break;
            case 4:
                if (!UserCenterCommon.checkMobilePhoneStr(value))
                {
                    dialog(resources.getString(R.string.uc_mobile_illegal));
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }

    /**
     * 更新数据库
     * @param itemId
     * @param label
     * @param value
     * @param updatePosition
     * @param userAttName
     */
    private void updateDatabase(int itemId, String label, String value, int updatePosition, String userAttName)
    {
        User user = new User();
        user.setId(itemId);
        user.setLabel(label);
        user.setValue(value);
        try
        {
            // 更新数据库
            dao.createOrUpdate(user);

            // 刷新UI
            HashMap<String, Object> item = userinfoList.get(updatePosition);
            HashMap<String, Object> newItem = new HashMap<String, Object>();
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_ID, user.getId());
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_LABEL, user.getLabel());
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_VALUE, user.getValue() == null ? "" : user.getValue());
            userinfoList.remove(item); // 从列表里面移除
            userinfoList.add(updatePosition, newItem); // 在移除的位置重新添加
            listView.setAdapter(adapter); // 刷新UI

            // 同步到服务器
            TreeMap paraMap = new TreeMap();
            paraMap.put("cc", UserInfoActivity.cc);
            paraMap.put("userAttName", userAttName);
            paraMap.put("userAttValue", value);
            new SyncUserInfoThread(paraMap, mHandler).start();
            // new SyncUserInfoTask().execute(new
            // String[]{UserInfoActivity.cc,label,value});
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 构建一个线程对象，用于异步发送请求道服务端同步用户信息
     * @author xiangyuanmao
     */
    class SyncUserInfoThread extends Thread
    {
        TreeMap paraMap;
        Handler sHandler;

        public SyncUserInfoThread(TreeMap map, Handler handler)
        {
            this.paraMap = map;
            this.sHandler = handler;
        }

        @Override
        public void run()
        {
            try
            {
                RequestParameter rp = new RequestParameter(Constants.SERVICE_USERCENTER, "modifyUserInfo", null, paraMap, true);
                WebServiceManager wsm = new WebServiceManager(UserInfoActivity.this, rp);
                SoapObject object;
                WSBaseResult wSBaseResult = wsm.execute();
                switch (wSBaseResult.responseCode)
                {
                    case 0:
                        object = (SoapObject) wSBaseResult.object;

                        // 得到修改用户信息结果对象
                        if (wSBaseResult != null && object.getProperty(0) != null)
                        {
                            SoapObject so = (SoapObject) object.getProperty(0);
                            // 是否成功
                            int isSuccess = new Integer(so.getProperty("code") == null ? "-1" : so.getProperty("code").toString()).intValue();

                            String message = UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess);
                                
                            // 通知UI主线程修改用户信息结果
                            sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT, isSuccess, 0, message).sendToTarget();
                        }
                        else
                        {
                            sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
                        }
                        break;
                    case -1:
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "同步用户信息到服务器失败");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
                        break;
                    case 2:
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "同步用户信息到服务器发生IO异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin)).sendToTarget();
                        break;
                    case 3:
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "同步用户信息到服务器发生Xml解析异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,resources.getString(R.string.usercenter_service_parse_xml_exception)).sendToTarget();
                        break;
                    default:

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (D)
                    Log.d(TAG, "同步用户信息到服务器异常");
                // 通知UI主线程登录结果
                sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
            }
        }
    }

    /**
     * 显示修改提示
     */
    protected void showUpdate(String attName, int position)
    {
        final String userAttName = attName;
        final HashMap<String, Object> item = userinfoList.get(position); // 被修改的元素
        final String label = item.get(UsercenterConstants.USERCENTER_USERINFO_LABEL).toString(); // 显示名称
        final String value = item.get(UsercenterConstants.USERCENTER_USERINFO_VALUE) == null ? "" : item.get(UsercenterConstants.USERCENTER_USERINFO_VALUE).toString(); // 显示值
        final EditText etValue = new EditText(UserInfoActivity.this); // 实例化一个编辑框用于接收用户修改的信息
        etValue.setText(value);
        etValue.setBackgroundDrawable(resources.getDrawable(R.drawable.main_edit));
        final Integer itemId = new Integer(item.get(UsercenterConstants.USERCENTER_USERINFO_ID).toString()); // 属性ID
        final int itemPostion = position; // 用于传入对话框的位置
        final int updatePosition = position;// 被修改的位置

        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.setTitle(label); // 标题
        customDialog.setIcon(android.R.drawable.ic_dialog_info); // 图片
        customDialog.setView(etValue); // 编辑框
        customDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 更新用户信息
                String value = etValue.getText().toString().trim();
                if (checkUpdate(itemPostion, value))
                {
                    customDialog.dismiss();
                    updateDatabase(itemId, label, value, updatePosition, userAttName);
                }
            }
        });
        customDialog.setNegativeButton(R.string.cancel, new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    /**
     * 用户信息Adapter，主要用于加载用户详细信息
     * @author xiangyuanmao
     */
    class UserInfoAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> listItems;

        public UserInfoAdapter(Context c, ArrayList<HashMap<String, Object>> list)
        {
            context = c;
            inflater = LayoutInflater.from(c);
            listItems = list;
        }

        @Override
        public int getCount()
        {
            return listItems.size();
        }

        @Override
        public Object getItem(int position)
        {
            return listItems.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            final TextView tvLabel; // 标签
            final TextView tvValue; // 内容
            final int item = position;
            String email = "";
            ImageView ivEdit;
            ImageView ivBind;
            view = this.inflater.inflate(R.layout.usercenter_lv_userinfo, null);
            tvLabel = (TextView) view.findViewById(R.id.usercenter_tv_userinfo_label);
            tvValue = (TextView) view.findViewById(R.id.usercenter_tv_userinfo_value);
            ivEdit = (ImageView) view.findViewById(R.id.userinfo_edit);
            tvLabel.setText(listItems.get(position).get(UsercenterConstants.USERCENTER_USERINFO_LABEL).toString());
            tvValue.setText(listItems.get(position).get(UsercenterConstants.USERCENTER_USERINFO_VALUE).toString());
            ivBind = (ImageView) view.findViewById(R.id.userinfo_bind);
            if (item == 0)
            {
                ivEdit.setVisibility(View.INVISIBLE);
            }
            else
            {
                ivEdit.setVisibility(View.VISIBLE);
            }

            ivEdit.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (item == 0)
                    {
                        return;
                    }
                    showUpdate(getAttName(item), item);
                }
            });
            ivBind = (ImageView) view.findViewById(R.id.userinfo_bind);
            if (item == 3)
            {
                ivBind.setVisibility(View.VISIBLE);
                email = listItems.get(position).get(UsercenterConstants.USERCENTER_USERINFO_VALUE).toString();
            }
            final String formalEmail = email;
            ivBind.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                 
                    showMessageBind(resources.getString(R.string.ensure_bind_email), new BindThread(formalEmail));
                }
            });
            return view;
        }
    }

    /**
     * 显示邮箱绑定信息
     * @param message
     */
    protected void showMessageBind(String message,final BindThread bindThread)
    {
        if (isRuning)
        {
            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
            customAlertDialog.setMessage(message);
            customAlertDialog.setTitle(resources.getString(R.string.uc_notice));
            customAlertDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    if (bindThread != null)
                    {
                        bindThread.start();
                    }
                    customAlertDialog.dismiss();
                }
            });
            if (bindThread != null)
            {
                customAlertDialog.setNegativeButton(resources.getString(R.string.cancel), new OnClickListener()
                {
                    
                    @Override
                    public void onClick(View v)
                    {
                        customAlertDialog.dismiss();
                    }
                });
            }
            customAlertDialog.show();
        }
    }
  
    public final static int BIND_EMAIL = 8;
    class BindThread extends Thread
    {
        String email;

        public BindThread(String email)
        {
            this.email = email;
        }

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            super.run();
            // 封装请求参数
            TreeMap paraMap = new TreeMap();
            paraMap.put("cc", cc);
            paraMap.put("password", LoginThread.getMd5Pawword(MyCarActivity.password));
            paraMap.put("email", email);
            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_BIND_EMAIL, "sendBindingEail", null, paraMap, true);
            WebServiceManager wsm = new WebServiceManager(UserInfoActivity.this, requestParameter);
            SoapObject result;
            WSBaseResult wSBaseResult = (WSBaseResult) wsm.execute();
 
            if (wSBaseResult.responseCode == 0)
            {
                if (wSBaseResult.object instanceof SoapObject)
                {
                    result = (SoapObject) wSBaseResult.object;
                    String message = "";
                    if (result != null && result.getProperty(0) != null)
                    {
                        SoapObject so = (SoapObject) result.getProperty(0);
                        int isSuccess = new Integer(so.getProperty("code").toString()).intValue();
                        message = UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess);
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(BIND_EMAIL, isSuccess, 0, message).sendToTarget();
                        ;
                    }
                    else
                    {
                        mHandler.obtainMessage(BIND_EMAIL, 2, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1)).sendToTarget();
                    }
                }
            }
        }
    }

    /**
     * 响应底部菜单按钮事件
     * @param v
     */
    public void MenuButton_ClickHandler(View v)
    {
        switch (v.getId())
        {
            case R.id.uc_userinfo_refresh:
                // 同步服务端的数据到本地
                syncUserinfoFromService(cc);
                break;
            case R.id.uc_userinfo_change_pwd:
                // 密码管理
                managePassword();
                break;
            case R.id.uc_userinfo_logout:
                logout();
                break;
            case R.id.uc_userinfo_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 退出登录
     */
    private void logout()
    {

        // 获得SharedPreferences对象
        SharedPreferences settings = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        MyCarActivity.failLogin(); // 更新类变量的状态
        // 保存登录信息
        settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // 未登录
        settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, false).commit(); // 不自动登录
        if (MyCarActivity.tvOnline != null)
        {
            MyCarActivity.tvOnline.setBackgroundResource(R.drawable.main_title_logout);
        }
        if (MyCarActivity.isLoginImage != null)
        {
            MyCarActivity.isLoginImage.setBackgroundResource(R.drawable.main_title_logout);
        }
        if (MyCarActivity.slidingDrawerlogin != null)
        {
            MyCarActivity.slidingDrawerlogin.setText(resources.getString(R.string.login_state_display_logout));
        }
        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED;
        ChangeUserOnlineState cuos = new ChangeUserOnlineState();
        cuos.executeAfterLogout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * 执行更改密码
     */
    private void changePassword()
    {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("account", cc);
        startActivity(intent);
    }

    private void setPasswordQuestion()
    {
        Intent intent = new Intent(UserInfoActivity.this, PasswordQuestionActvity.class);
        // 指明是从登录界面跳转到找回密码界面的
        intent.putExtra(PasswordQuestionActvity.ORIGINAL_ACTIVITY, PasswordQuestionActvity.ACTIVITY_USERINFO);
        startActivity(intent);
    }

    /**
     * 同步服务端的数据到本地
     * @param cc
     */
    private void syncUserinfoFromService(final String cc)
    {

        pdlg = new ProgressDialog(this);
        // pdlg.setProgressDrawable()
        pdlg.setMessage(resources.getString(R.string.usercenter_request_userinfo));
        pdlg.show();

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    // 封装请求参数
                    TreeMap paraMap = new TreeMap();
                    paraMap.put("cc", cc);
                    RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USERCENTER, "getUser", null, paraMap, true);
                    WebServiceManager wsm = new WebServiceManager(UserInfoActivity.this, requestParameter);
                    SoapObject result;
                    WSBaseResult wSBaseResult = (WSBaseResult) wsm.execute();
                    if (wSBaseResult.responseCode == 0)
                    {
                        if (wSBaseResult.object instanceof SoapObject)
                        {
                            result = (SoapObject) wSBaseResult.object;
                            if (result != null && result.getProperty(0) != null)
                            {
                                pdlg.cancel();
                                SoapObject so = (SoapObject) result.getProperty(0);
                                WSUser wSUser = new WSUser();
                                so.hasProperty("");
                                wSUser.cc = so.hasProperty("userId") ? so.getProperty("userId").toString() : "";
                                wSUser.nickname = so.hasProperty("nickName") ? so.getProperty("nickName").toString() : "";
                                wSUser.userName = so.hasProperty("userName") ? so.getProperty("userName").toString() : "";
                                wSUser.mobile = so.hasProperty("mobile") ? so.getProperty("mobile").toString() : "";
                                wSUser.email = so.hasProperty("email") ? so.getProperty("email").toString() : "";
                                wSUser.isBindEmail = so.hasProperty("isBindEmail") ? so.getProperty("isBindEmail").toString() : "";
                                wSUser.isBindMobile = so.hasProperty("isBindMobile") ? so.getProperty("isBindMobile").toString() : "";
                                int isSuccess = new Integer(so.getProperty("code").toString()).intValue();
                                // 通知UI主线程登录结果
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, wSUser).sendToTarget();
                                ;
                            }
                            else
                            {
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_connect_service_timeout)).sendToTarget();
                            }
                        }
                    }
                    // 发生IO异常
                    else if (wSBaseResult.responseCode == 2)
                    {
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "从服务器同步用户信息发生IO异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin)).sendToTarget();
                    }
                    // 发生xml解析异常
                    else if (wSBaseResult.responseCode == 3)
                    {
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "从服务器同步用户信息发生Xml解析异常");
                        // 通知UI主线程登录结果
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,  resources.getString(R.string.usercenter_service_parse_xml_exception)).sendToTarget();
                    }

                }
                catch (Exception e)
                {
                    if (pdlg != null && pdlg.isShowing())
                    {
                        pdlg.dismiss();
                    }
                    e.printStackTrace();
                    if (D)
                        Log.d(TAG, "从服务器同步用户信息发生异常");
                    // 通知UI主线程登录结果
                    mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
                }
            }
        }.start();
    }

    // 处理消息
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BIND_EMAIL:
                    if (msg.arg1 == 0)
                    {
                        showMessageBind(resources.getString(R.string.bind_emial_success), null);
                    }
                    else
                    {
                        
                        showMessageBind(msg.obj.toString(), null);
                    }
                    break;
                case UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT: // 同步数据到服务器

                    // 弹出对话框提示用户同步数据结果
                    dialog(msg.obj.toString());
                    break;
                case UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT: // 从服务端同步用户信息到手机
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.RESULT_SUCCESS: // 获取用户资料成功

                            // 获得从WebService返回的用户信息结果对象
                            WSUser wsUser = (WSUser) msg.obj;

                            // 刷新本地数据库中的User表
                            Dao<User, Integer> dao = getHelper().getDao(User.class);
                            UsercenterDao.updateUser(resources, dao, wsUser);

                            // 刷新UI
                            initViews();
                            break;
                        case UsercenterConstants.RESULT_FAIL: // 获取用户资料失败
                            // 弹出对话框提示用户失败原因
                            pdlg.cancel();
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
                            // 弹出对话框提示用户失败原因
                            dialog(msg.obj.toString());
                            break;
                    }
            }
        }
    };

    /**
     * 登录异常弹出对话框
     * @param message
     */
    protected void dialog(String message)
    {
        if (isRuning)
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
                }
            });
            customAlertDialog.show();
        }
    }

    /**
     * 执行找回密码
     */
    private void managePassword()
    {

        final DialogOnClickListener docl = new DialogOnClickListener();
        new CustomDialog(this).setTitle(resources.getString(R.string.usercenter_plese_choice)).setIcon(android.R.drawable.ic_dialog_info)
            .setItems(new String[] { resources.getString(R.string.uc_change_password), resources.getString(R.string.uc_set_password_question) }, docl).show();
    }

    class DialogOnClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            // TODO Auto-generated method stub
            switch (which)
            {
                case SET_PASSWORD_QUESTION:
                    setPasswordQuestion();
                    break;
                case CHANGE_PASSWORD:
                    changePassword();
                    break;
            }
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.uc_userinfo_refresh:
                // 同步服务端的数据到本地
                syncUserinfoFromService(cc);
                break;
            case R.id.uc_password_manage:
                // 密码管理
                managePassword();
                break;
            case R.id.uc_userinfo_logout:
                logout();
                break;
            case R.id.uc_userinfo_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
