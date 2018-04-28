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
 * ��¼����
 * @author xiangyuanmao
 */
public class LoginActivity extends BaseActivity implements OnClickListener, SpinnserPopuViewListener
{

    // ����log��Ϣtarget
    private static final String TAG = "LoginActivity";
    private static final boolean D = true;

    ProgressDialog pdlg; // ��¼�Ի���

    // ------------------ ����Ԫ��--------------------------------
    private Button btnLogin; // ��¼��ť
    private Button btnRegister; // ע�ᰴť
    private Button btnForgetPwd; // �������밴ť
    private Button btnSkip; // ������ť
    private CheckBox cbAutoLogin; // �Զ���¼��ѡ��
    private CheckBox cbSavePwd; // �������븴ѡ��
    private EditText etAccount; // �˺ű༭��
    private EditText etPwd; // ����༭��

    // ------------------
    // ���ڻ���ע��ɹ���ӷ���˵õ���CC�źͳ�ʼ����--------------------------------
    private String account; // �˺�
    private String password; // ����
    private boolean isFinish = false;

    // ------------------�������ϵ�ͼƬ��------------------
    ImageView main_title_imageview_weather;
    TextView main_title_tv_temperature;
    ImageView main_title_imageview_tour;
    ImageView main_title_imageview_washcar;
    TextView main_title_tv_washcar;

    private static final int RETRIEVED_PASSWORD_BY_MOBILE = 0; // ͨ���ֻ��һ�����
    private static final int RETRIEVED_PASSWORD_BY_QUESTION = 1; // ͨ��������ʾ�����һ�����

    // ------------------ ���ڻ��Ե�¼��¼����--------------------------------
    SharedPreferences loginSP; // ��¼��SharedPreferences
    Map accountsMap; // �˺�Map����Ҫ���ڻ����ѵ�¼��¼

    private SpinnerView spinnerView; // �����ѵ�¼�ļ�¼��
    private PopupWindow hiphop = null; // �������ڣ�����ʵ�ֵ�����¼��¼
    private boolean isOpenProgress = false; // �Ƿ�򿪽��ȿ�

    private Resources resources;
    private int forward = 0; // ��¼�ɹ������תȥ��0:finish���Լ���1:�ص������棬2:�û�������ϸ����

    Dao<User, Integer> dao;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // ȥ������
        setContentView(R.layout.usercenter_login, R.layout.custom_title);
        setCustomeTitleLeft(R.string.usercenter_dbscar_login);
        setCustomeTitleRight("");
        Intent intent = getIntent();
        forward = intent.getIntExtra("forward", 0);
        /* ��ȡ����Ԫ�ص����ã�����Ӽ����¼� */
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
     * ʵ�������б�ĵ�����ʽ
     * @param v ��������
     * @param isShow
     * @see com.cnlaunch.mycar.common.ui.SpinnerView.SpinnserPopuViewListener#show(android.widget.ViewFlipper, boolean)
     * @since DBS V100
     */
    @Override
    public void show(ViewFlipper v, boolean isShow)
    {
        if (isShow)
        {
            // ʵ����һ����������
            hiphop = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            // �����䱳��ͼƬ
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
            // �ֱ����õ�������X�����ƫ����Ϊ200�����أ�Y�ķ���Ϊ2�����أ�
            // ����ע�⣬���ƫ����������ڸ��ؼ���˵��
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
     * ĳһѡ�ѡ�е��¼�����
     * @param listView �����б������
     * @param position ��ѡ�����б��е�λ��
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
     * ��SharedPreferences����ȡ����¼��¼��key��CC���룬value��������
     * @return
     */
    private String[] getAccounts()
    {
        // ��SharedPreferences����ȡ����¼��¼
        loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        accountsMap = loginSP.getAll();
        String[] accountsArray;
        // key��CC���룬value��������
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
     * ��дOnClickListener�ӿڵķ���
     */
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.usercenter_btn_login:// ��¼
                executeLogin(); // ִ�е�¼
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

            case R.id.usercenter_btn_register:// ע��
                executeRegister(mHandler);
                break;

            case R.id.usercenter_btn_forget_pwd: // ��������
                retrievedPassword(mHandler);
                break;

            case R.id.usercenter_cb_auto_login: // �Զ���¼
                break;

            case R.id.usercenter_cb_save_pwd: // ��������
                break;

            case R.id.usercenter_btn_skip: // ����
                Log.d(TAG, "ֱ��������¼��������");
                Intent intent = new Intent(LoginActivity.this, MyCarActivity.class);
                startActivity(intent);
                this.finish();
            default:
                break;
        }
    }

    /**
     * ִ��ע��
     * @param handler
     */
    private void executeRegister(Handler handler)
    {

        Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * ��������� ����SE�ƹ���Ҫ��������������ý���ʱ��ϵͳ��������̣������Ļ����������ر������
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
     * ִ�е�¼
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
        // ������¼�Ի���
        pdlg = new ProgressDialog(this);
        // pdlg.setProgressDrawable(new Drawable(R.drawable.waiting));
        pdlg.setMessage(resources.getString(R.string.usercenter_logining));
        pdlg.show();

        // ִ�е�¼
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
     * ִ���һ�����
     */
    private void retrievedPassword(Handler handler)
    {


        // �ʺſؼ�����Ϊ��
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
        // ָ���Ǵӵ�¼������ת���һ���������
        intent.putExtra("loginKey", loginKey);
        intent.putExtra(PasswordQuestionActvity.ORIGINAL_ACTIVITY, PasswordQuestionActvity.ACTIVITY_LOGIN);
        startActivity(intent);
    }

    /**
     * ��¼�쳣�����Ի���
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
    // �����¼��Ϣ
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            SharedPreferences settings = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
            switch (msg.what)
            {
                case UsercenterConstants.LOGIN_RESULT: // ��¼���
                    if (D)
                        Log.i(TAG, "��¼���: " + msg.obj);
                    // ���SharedPreferences����
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.RESULT_SUCCESS: // ��¼�ɹ�
                            // �����¼��Ϣ
                            saveLoginInfo(settings, msg.obj);
                            LoginResult loginResult = (LoginResult) msg.obj;
                            syncUserinfoFromService(loginResult.cc);

                            break;
                        case UsercenterConstants.RESULT_FAIL: // ��¼ʧ��
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // �����������״̬
                            // �����¼��Ϣ
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // δ��¼
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // �����������״̬
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // δ��¼
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
                            MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            MyCarActivity.failLogin(); // �����������״̬
                            settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // δ��¼
                            dialog(msg.obj.toString());
                            break;
                            default:
                                MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                                // �رս��ȶԻ���
                                stopProgressDialog();
                                MyCarActivity.failLogin(); // �����������״̬
                                settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGOUT); // δ��¼
                                dialog(msg.obj.toString());
                                break;
                    }
                    break;
                case UsercenterConstants.USERCENTER_RESULT_RETRIVED_PWD: // �һ�����
                    switch (msg.arg1)
                    {
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_SUCCESS: // �һ�����ɹ�
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            // �����Ի�����ʾCC�ź�����
                            dialog(resources.getString(R.string.usercenter_retrieve_password_success));
                            break;
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_FAILED: // �һ�����ʧ��
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;
                        case UsercenterConstants.USERCENTER_RESULT_RETRIVED_EXCEPTION: // �һ������쳣
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;

                        default:
                            // �رս��ȶԻ���
                            stopProgressDialog();
                            dialog(msg.obj.toString());
                            break;
                    }
                    break;
                case UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT: // �ӷ����ͬ���û���Ϣ���ֻ�
                    switch(msg.arg1)
                    {
                    case UsercenterConstants.RESULT_SUCCESS: // ��ȡ�û����ϳɹ�
                        Log.d(TAG, "��ȡ�û����ϳɹ���");
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGINED; // �ѵ�¼
                        // ��ô�WebService���ص��û���Ϣ�������
                        WSUser wsUser = (WSUser)msg.obj;
                        
                        // ˢ�±������ݿ��е�User��
                        dao = getHelper().getDao(User.class);
                        UsercenterDao.updateUser(resources,dao,wsUser); 
                        // �رս��ȶԻ���
                        stopProgressDialog();
                        ChangeUserOnlineState cuos = new ChangeUserOnlineState();
                        cuos.executeAfterLogin();
                        nextForward();
                        break;
                    case UsercenterConstants.RESULT_FAIL: // ��ȡ�û�����ʧ��
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                        // �رս��ȶԻ���
                        stopProgressDialog();
                        // �����Ի�����ʾ�û�ʧ��ԭ��
                        dialog(msg.obj.toString());
                        break;
                    case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
                        MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
                        // �رս��ȶԻ���
                        stopProgressDialog();
                        // �����Ի�����ʾ�û�ʧ��ԭ��
                        dialog(resources.getString(R.string.usercenter_login_exception));
                        break;
                    }
                    break;
            }
        }
    };

    /**
     * �����¼��Ϣ
     * @param settings
     * @param obj
     */
    private void saveLoginInfo(SharedPreferences settings, Object obj)
    {
        // �����¼��Ϣ
        settings.edit().putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGIN).commit(); // �ѵ�¼

        // ���ѡ�����Զ���¼��ѡ��
        if (cbAutoLogin.isChecked())
        {
            settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, true).commit(); // �Զ���¼
            settings.edit().putString(UsercenterConstants.LAST_LOGIN_PWD, etPwd.getText().toString().trim()).commit(); // ����
        }
        else
        {
            settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, false).commit(); // ���Զ���¼
        }
        // ���ѡ���˱�������
        if (cbSavePwd.isChecked())
        {
            settings.edit().putString(UsercenterConstants.LAST_LOGIN_PWD, etPwd.getText().toString().trim()).commit();
        }
        LoginResult loginResult = (LoginResult) obj;
        settings.edit().putString(UsercenterConstants.LAST_LOGIN_ACCOUNT, etAccount.getText().toString().trim()).commit(); // ��¼�˺�
        settings.edit().putString(UsercenterConstants.LOGIN_TOKEN, loginResult.token).commit(); // ��¼����
        settings.edit().putLong(UsercenterConstants.LOGIN_SERVICE_TIME, loginResult.serverSystemTime).commit(); // ������ʱ��
        settings.edit().putString(UsercenterConstants.LOGIN_CC, loginResult.cc).commit(); // cc

        // ָ����ǰ���ݿ�����
        MyCarConfig.currentCCToDbName = loginResult.cc + ".db";
        loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

        if (cbSavePwd.isChecked() || cbAutoLogin.isChecked())
        {
            loginSP.edit().remove(etAccount.getText().toString().trim()).commit();
            loginSP.edit().putString(etAccount.getText().toString().trim(), etPwd.getText().toString().trim()).commit(); // ��¼��¼
        }
        else
        {
            loginSP.edit().putString(etAccount.getText().toString().trim(), "").commit(); // ��¼��¼
        }
        MyCarActivity.cc = loginResult.cc; // CC����
        MyCarActivity.password = etPwd.getText().toString().trim() ;
        MyCarActivity.isLogin = true; // �Ƿ��¼�� ��
        MyCarActivity.token = loginResult.token; // ����
        MyCarActivity.csInterval = new Date().getTime() - loginResult.serverSystemTime; // �ͻ��˺ͷ���˵�ʱ����
        MyCarActivity.accountsArray = getAccounts(); // ����¼��¼���¸�ֵ

        // ���͹㲥���µ�¼״̬
        Intent intent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
        intent.putExtra("isLoginFlag", true);
        sendBroadcast(intent);

        // ���͹㲥���µ�¼״̬
        Intent titleIntent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
        intent.putExtra("isLoginFlag", true);
        sendBroadcast(titleIntent);
    }

    /**
     * ��ȡ����Ԫ�ص����ò�Ϊ����Ԫ�����õ��������¼�
     */
    private void getPreferenceAndListerner()
    {
        btnLogin = (Button) findViewById(R.id.usercenter_btn_login); // ��õ�¼��ť������
        btnRegister = (Button) findViewById(R.id.usercenter_btn_register); // ���ע�ᰴť������
        btnForgetPwd = (Button) findViewById(R.id.usercenter_btn_forget_pwd); // ����������밴ť������
        btnSkip = (Button) findViewById(R.id.usercenter_btn_skip);
        cbAutoLogin = (CheckBox) findViewById(R.id.usercenter_cb_auto_login); // ����Զ���¼��ѡ������
        cbSavePwd = (CheckBox) findViewById(R.id.usercenter_cb_save_pwd); // ��ñ������븴ѡ������
        etAccount = (EditText) findViewById(R.id.usercenter_et_account); // �˺ű༭��
        etPwd = (EditText) findViewById(R.id.usercenter_et_pwd); // ����༭��
        cbAutoLogin.setChecked(true); // �����Զ���¼
        cbSavePwd.setChecked(true); // ���Ǳ�������
        /* Ϊ����Ԫ�����õ��������¼� */
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgetPwd.setOnClickListener(this);
        cbAutoLogin.setOnClickListener(this);
        cbSavePwd.setOnClickListener(this);
        cbAutoLogin.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    /**
     * ��WebService�����㷢���Ĺ㲥������WebService�������������ݵ����ƣ�token��
     * ʵЧʱ����Ҫ֪ͨ��¼ģ��������¼���»�ȡ���ơ�
     * @author xiangyuanmao
     */
    private class WebServiceBroadcastReciver extends BroadcastReceiver
    {
        SharedPreferences myCarShared = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // �����WebService�����㷢���Ĺ㲥
            if (action.equals("com.cnlaunch.webservice.broadcast"))
            {
                // ��ȡ��Ӧ����
                int responseCode = intent.getIntExtra("responseCode", 0);

                // �������ʵЧ�����µ�½
                if (responseCode == UsercenterConstants.RESPONSE_TOKEN_TIMEOUT)
                {
                    // ȡ��MyCarActivity������˺�����
                    String account;
                    String password;
                    // ����Զ���¼
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
     * ���������淵�ر�����ʱ����
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode)
        {
            case UsercenterConstants.REQUEST_CODE_WEBSERVICE: // ��Webservice���͹�������ͼ
                // �����Ƿ����λtrue����������¼�ɹ���رձ�ҳ��
                if (resultCode == Activity.RESULT_OK)
                {
                    isFinish = true;
                }
                break;
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
     * ͬ������˵����ݵ�����
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
                    // ��װ�������
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
                                // ֪ͨUI���̵߳�¼���
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, wSUser).sendToTarget();;
                            }
                            else
                            {
                                mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
                                .sendToTarget();
                            }
                        }
                    }
                    // ����IO�쳣
                    else if (wSBaseResult.responseCode == 2)
                    {
                        if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin))
                        .sendToTarget();
                    }
                    // ����xml�����쳣
                    else if (wSBaseResult.responseCode == 3)
                    {
                        if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����Xml�����쳣");
                        // ֪ͨUI���̵߳�¼���
                        mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,  resources.getString(R.string.usercenter_service_parse_xml_exception))
                        .sendToTarget();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
                    // ֪ͨUI���̵߳�¼���
                    mHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_exception))
                    .sendToTarget();
                }   
            }
         }.start();
     }

}
