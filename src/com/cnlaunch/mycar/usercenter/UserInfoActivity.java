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
 * �û���ϸ��Ϣ����
 * @author xiangyuanmao
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener
{
    // ����log��Ϣtarget
    private static final String TAG = "UserInfoActivity";
    private static final boolean D = true;
    private static final int SET_PASSWORD_QUESTION = 1; // ���������һ�����
    private static final int CHANGE_PASSWORD = 0; // �޸�����
    /*
     * Ϊ��ʹ���б����ʽ��ʾ�û���ϸ��������Ļ����Ҫ�õ���������ListView���̳���BaseAdapter������UserInfoAdapter��
     * ��һ�������б�ArrayList�������ģ����һ��Map���洢��ʾ���ı�
     */
    private ListView listView; // �û���Ϣ���б�ListView����
    private UserInfoAdapter adapter; // �û���Ϣ�б��Adapter
    private ArrayList<HashMap<String, Object>> userinfoList; // �û���Ϣ����
    final Dao<User, Integer> dao = getHelper().getDao(User.class);
    ProgressDialog pdlg; // �Ի��򣬵�������ͨ��ʱ���ڸ����û�����Ľ��ȶԻ���
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
     * �����˻���ķ���
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
        initViews(); // ��ʼ���б�
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /**
     * ��ʼ���б�
     */
    private void initViews()
    {

        /*
         * UIԪ�س�ʼ��
         */
        listView = (ListView) this.findViewById(R.id.usercenter_lv_userinfo); // ��ȡListView������
        userinfoList = new ArrayList<HashMap<String, Object>>(); // ��ʼ���洢�û���Ϣ��ArrayList

        /*
         * ���ݿ��������ĳ�ʼ�����õ�һ�����ݿ�����ӿڵ�ʵ����������User�࣬�൱����User.java����Ϊ
         * ģ�幹���ṹ����Ϊ����Ϊÿ���û�������һ�����ݿ⣬����User��Ľṹ��������ID��ֻ���û�����
         * ����ʾ���ƺ���ʾֵ������������Ҫ��һ������洢�û�������м�¼��϶����������û���Ϣ
         */
        List<User> userList; // ע�⣬�˴��Ľṹ���û��ĵ������ԣ������������û���Ϣ
        try
        {
            userList = dao.queryForAll(); // �õ��û���Ϣ���������Բ�ѹ��userinfoList����
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

        // ��ʼ��Adapter
        adapter = new UserInfoAdapter(this, userinfoList);

        // ����Adapter��ListView����ʱ�û���Ϣ�б���ֵ��������ʾ����Ļ��Ҳ����ͨ������adapterˢ����Ļ
        listView.setAdapter(adapter);

        btnRefresh = (Button) findViewById(R.id.uc_userinfo_refresh);
        btnPasswordManage = (Button) findViewById(R.id.uc_password_manage);
        btnLogout = (Button) findViewById(R.id.uc_userinfo_logout);
        btnRefresh.setOnClickListener(this);
        btnPasswordManage.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    /**
     * ȡ���������ύ�����ֶε�����key
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
     * У������û���Ϣ�������ʽ
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
     * �������ݿ�
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
            // �������ݿ�
            dao.createOrUpdate(user);

            // ˢ��UI
            HashMap<String, Object> item = userinfoList.get(updatePosition);
            HashMap<String, Object> newItem = new HashMap<String, Object>();
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_ID, user.getId());
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_LABEL, user.getLabel());
            newItem.put(UsercenterConstants.USERCENTER_USERINFO_VALUE, user.getValue() == null ? "" : user.getValue());
            userinfoList.remove(item); // ���б������Ƴ�
            userinfoList.add(updatePosition, newItem); // ���Ƴ���λ���������
            listView.setAdapter(adapter); // ˢ��UI

            // ͬ����������
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
     * ����һ���̶߳��������첽��������������ͬ���û���Ϣ
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

                        // �õ��޸��û���Ϣ�������
                        if (wSBaseResult != null && object.getProperty(0) != null)
                        {
                            SoapObject so = (SoapObject) object.getProperty(0);
                            // �Ƿ�ɹ�
                            int isSuccess = new Integer(so.getProperty("code") == null ? "-1" : so.getProperty("code").toString()).intValue();

                            String message = UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess);
                                
                            // ֪ͨUI���߳��޸��û���Ϣ���
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
                            Log.d(TAG, "ͬ���û���Ϣ��������ʧ��");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
                        break;
                    case 2:
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "ͬ���û���Ϣ������������IO�쳣");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin)).sendToTarget();
                        break;
                    case 3:
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "ͬ���û���Ϣ������������Xml�����쳣");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,resources.getString(R.string.usercenter_service_parse_xml_exception)).sendToTarget();
                        break;
                    default:

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (D)
                    Log.d(TAG, "ͬ���û���Ϣ���������쳣");
                // ֪ͨUI���̵߳�¼���
                sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
            }
        }
    }

    /**
     * ��ʾ�޸���ʾ
     */
    protected void showUpdate(String attName, int position)
    {
        final String userAttName = attName;
        final HashMap<String, Object> item = userinfoList.get(position); // ���޸ĵ�Ԫ��
        final String label = item.get(UsercenterConstants.USERCENTER_USERINFO_LABEL).toString(); // ��ʾ����
        final String value = item.get(UsercenterConstants.USERCENTER_USERINFO_VALUE) == null ? "" : item.get(UsercenterConstants.USERCENTER_USERINFO_VALUE).toString(); // ��ʾֵ
        final EditText etValue = new EditText(UserInfoActivity.this); // ʵ����һ���༭�����ڽ����û��޸ĵ���Ϣ
        etValue.setText(value);
        etValue.setBackgroundDrawable(resources.getDrawable(R.drawable.main_edit));
        final Integer itemId = new Integer(item.get(UsercenterConstants.USERCENTER_USERINFO_ID).toString()); // ����ID
        final int itemPostion = position; // ���ڴ���Ի����λ��
        final int updatePosition = position;// ���޸ĵ�λ��

        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.setTitle(label); // ����
        customDialog.setIcon(android.R.drawable.ic_dialog_info); // ͼƬ
        customDialog.setView(etValue); // �༭��
        customDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // �����û���Ϣ
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
     * �û���ϢAdapter����Ҫ���ڼ����û���ϸ��Ϣ
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
            final TextView tvLabel; // ��ǩ
            final TextView tvValue; // ����
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
     * ��ʾ�������Ϣ
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
            // ��װ�������
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
                        // ֪ͨUI���̵߳�¼���
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
     * ��Ӧ�ײ��˵���ť�¼�
     * @param v
     */
    public void MenuButton_ClickHandler(View v)
    {
        switch (v.getId())
        {
            case R.id.uc_userinfo_refresh:
                // ͬ������˵����ݵ�����
                syncUserinfoFromService(cc);
                break;
            case R.id.uc_userinfo_change_pwd:
                // �������
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
     * �˳���¼
     */
    private void logout()
    {

        // ���SharedPreferences����
        SharedPreferences settings = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        MyCarActivity.failLogin(); // �����������״̬
        // �����¼��Ϣ
        settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // δ��¼
        settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, false).commit(); // ���Զ���¼
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
     * ִ�и�������
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
        // ָ���Ǵӵ�¼������ת���һ���������
        intent.putExtra(PasswordQuestionActvity.ORIGINAL_ACTIVITY, PasswordQuestionActvity.ACTIVITY_USERINFO);
        startActivity(intent);
    }

    /**
     * ͬ������˵����ݵ�����
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
                    // ��װ�������
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
                                // ֪ͨUI���̵߳�¼���
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, wSUser).sendToTarget();
                                ;
                            }
                            else
                            {
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_connect_service_timeout)).sendToTarget();
                            }
                        }
                    }
                    // ����IO�쳣
                    else if (wSBaseResult.responseCode == 2)
                    {
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin)).sendToTarget();
                    }
                    // ����xml�����쳣
                    else if (wSBaseResult.responseCode == 3)
                    {
                        if (pdlg != null && pdlg.isShowing())
                        {
                            pdlg.dismiss();
                        }
                        if (D)
                            Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����Xml�����쳣");
                        // ֪ͨUI���̵߳�¼���
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
                        Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
                    // ֪ͨUI���̵߳�¼���
                    mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception)).sendToTarget();
                }
            }
        }.start();
    }

    // ������Ϣ
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
                case UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT: // ͬ�����ݵ�������

                    // �����Ի�����ʾ�û�ͬ�����ݽ��
                    dialog(msg.obj.toString());
                    break;
                case UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT: // �ӷ����ͬ���û���Ϣ���ֻ�
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.RESULT_SUCCESS: // ��ȡ�û����ϳɹ�

                            // ��ô�WebService���ص��û���Ϣ�������
                            WSUser wsUser = (WSUser) msg.obj;

                            // ˢ�±������ݿ��е�User��
                            Dao<User, Integer> dao = getHelper().getDao(User.class);
                            UsercenterDao.updateUser(resources, dao, wsUser);

                            // ˢ��UI
                            initViews();
                            break;
                        case UsercenterConstants.RESULT_FAIL: // ��ȡ�û�����ʧ��
                            // �����Ի�����ʾ�û�ʧ��ԭ��
                            pdlg.cancel();
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
                            // �����Ի�����ʾ�û�ʧ��ԭ��
                            dialog(msg.obj.toString());
                            break;
                    }
            }
        }
    };

    /**
     * ��¼�쳣�����Ի���
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
     * ִ���һ�����
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
                // ͬ������˵����ݵ�����
                syncUserinfoFromService(cc);
                break;
            case R.id.uc_password_manage:
                // �������
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
