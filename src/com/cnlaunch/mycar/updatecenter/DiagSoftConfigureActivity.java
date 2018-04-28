package com.cnlaunch.mycar.updatecenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.ZipHelper;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.device.DeviceConfigState;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.listener.ApkDownloadListener;
import com.cnlaunch.mycar.updatecenter.listener.DownloadBinDownloadListener;
import com.cnlaunch.mycar.updatecenter.listener.DownloadBinUpdateInfoListener;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.onekeydiag.CarBaseInfo;
import com.cnlaunch.mycar.updatecenter.onekeydiag.CarBrandList;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ConditonIdTable;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ConfigControlFlag;
import com.cnlaunch.mycar.updatecenter.onekeydiag.OneKeyDiag;
import com.cnlaunch.mycar.updatecenter.onekeydiag.OneKeyDiagResult;
import com.cnlaunch.mycar.updatecenter.onekeydiag.RemainConfigurationCount;
import com.cnlaunch.mycar.updatecenter.onekeydiag.SoftLanguageItem;
import com.cnlaunch.mycar.updatecenter.onekeydiag.SoftwareLanguageList;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.ChoiceGroup;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.GroupSelectionChangeListener;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.PopupSelectDialog;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.WebRequestListener;
import com.cnlaunch.mycar.updatecenter.tools.FileTrunc;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter.Callback;
import com.cnlaunch.mycar.updatecenter.tools.VINChecker.Error;
import com.cnlaunch.mycar.updatecenter.tools.VINChecker;
import com.cnlaunch.mycar.updatecenter.webservice.SoapMethod;
import com.cnlaunch.mycar.updatecenter.webservice.SoapRequest;
import com.cnlaunch.mycar.updatecenter.webservice.SoapResponse;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceListener;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.UserCenterCommon;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;
import com.cnlaunch.mycar.usercenter.model.WSUser;

/**
 * ��������̬����ѡ�����
 */
public class DiagSoftConfigureActivity extends BaseActivity implements OnClickListener
{
    private boolean D = true;
    private final static String TAG = "DiagSoftConfigureActivity";
    private final static int VIN_VALID_LENGTH = 17;
    public final static int LENGTH_INVALID = -1;// ���Ȳ���ȷ
    public final static int FORMAT_INVALID = -2;// ��ʽ����ȷ
    private final static int CONFIG_PROCESS_DONE = 0;// ���ý���
    private final static int CONFIG_PROCESS_NOT_DONE = 1;// ����δ���

    private final static String SHARE_PREF_CONFIG_CACHE = "SHARE_PREF_CONFIG";
    private SharedPreferences sharePrefConfigCache;// ���û���
    HashMap<String, String> conditionIdTranslate = new HashMap<String, String>();

    Context context = DiagSoftConfigureActivity.this;
    Activity activity = DiagSoftConfigureActivity.this;

    String deviceName;

    String vehicle = "";
    String version = "";
    String language = "";

    String carBrandId = "";// ȫ�ֵ�

    String VIN = "";

    TimeoutCounter timeoutCounter;// ��ʱ������
    CustomProgressDialog downloadProgressDialog;
    CustomProgressDialog unzipFileDialog;

    ProgressDialog progressDialog;

    Resources resources;
    ViewGroup vinArea;

    Button btGoback;
    Button btPurchase;
    Button btGoNext;
    Button btVinCheck;
    Button btVinHelp;
    Button btDownload;
    EditText etVin;
    EditText edtVinInput;
    TextView tvSerialNumber;

    ChoiceListAdapter choiceListAdapter;
    ListView lvChoiceList;

    String serialNumber = "";
    String chipID = "";
    String diagEntranceId = "";
    int remainingConfigCount = 6;
 
    /** ���ñ�־ */
    int endFlag = CONFIG_PROCESS_NOT_DONE;
    int versionDetailId = 0;
    boolean isHasHistory = false;

    WebServiceOperator webservice;
    HttpDownloadManager httpDownloader;// �������ع���
    MyCarApplication application;
    DefaultHttpListener downloadListener;
    DefaultHttpListener downloadBinDownloadListener;

    boolean isFront = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_configure, R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_diasoft_config);
        setCustomeTitleRight("");
        application = (MyCarApplication) getApplication();
        // �ӵ��õ�activity ����Ĳ���
        Bundle data = this.getIntent().getExtras();
        if (data != null)
        {
            SerialInfo serialInfo = (SerialInfo) data.get("serialInfo");
            if (serialInfo != null)
            {
                serialNumber = serialInfo.getSerialNumber();
                chipID = serialInfo.getChipId();
            }
        }

        // �������
        if (D)
            Log.e(TAG, "���кţ�" + serialNumber);
        if (D)
            Log.e(TAG, "оƬ���кţ�" + chipID);
        if (serialNumber.equals(""))
        {
            if (D)
                Log.e(TAG, "���к���Ϣ��������,����Ϊ��! Intent ��Ҫ�������кŲ��� SerialInfo����DiagSoftConfigureActivity");
            errorInfoDialog(getString(R.string.upc_diag_config_parameter_error), null);
            return;
        }

        webservice = new WebServiceOperator(this);
        webservice.addListener(new QueryDeviceHistoricalConfigListener(this, serialNumber));
        webservice.queryDeviceHistoricalConfig(serialNumber);

        /**
         * For test diag soft config VIN : WDDG1231231231231 SN : 980990001012
         **/
        // serialNumber ="980990001012";
        // chipID = "123456";
        // VIN = "WDDG1231231231231";

        resources = getResources();
        sharePrefConfigCache = getSharedPreferences(SHARE_PREF_CONFIG_CACHE, 0);
        initViews();
        httpDownloader = new HttpDownloadManager(this);
        downloadListener = new ApkDownloadListener(DiagSoftConfigureActivity.this, handler, httpDownloader);
        downloadBinDownloadListener = new DownloadBinDownloadListener(DiagSoftConfigureActivity.this, handler, httpDownloader);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        isFront = true;
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        isFront = false;
    }

    final Handler handler = new Handler();

    private void inputVin()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                final CustomDialog dialog;
                final EditText input;
                final String title = getString(R.string.upc_vin_validation);
                dialog = new CustomDialog(context);
                dialog.setTitle(title);
                input = new EditText(DiagSoftConfigureActivity.this);
                input.setHint(R.string.upc_please_input_vin_number_here);

                String lastVin = sharePrefConfigCache.getString("vin", "");
                if (!lastVin.equals(""))
                {
                    input.setText(lastVin);
                }
                input.setFocusable(true);
                dialog.setView(input);
                dialog.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        VIN = input.getText().toString().toUpperCase();
                        if (VIN.length() > 0)// ������ȷ
                        {
                            Pattern p = Pattern.compile("^[A-Za-z0-9]+$");// ����ƥ�����
                            Matcher m = p.matcher(VIN);
                            if (m.matches())// ���Ϲ���
                            {
                                VIN = (input.getText().toString()).toUpperCase();
                                application.saveVIN(VIN);
                                sharePrefConfigCache.edit().putString("vin", VIN).commit();
                                dialog.dismiss();
                                webservice.addListener(choiceListAdapter);
                                webservice.queryCarBrandListByVIN(VIN, serialNumber);
                            }
                            else
                            {
                                if (dialog != null)
                                {
                                    dialog.setTitle(getResources().getString(R.string.upc_vin_illegal));
                                }
                            }
                        }
                        // else
                        // {
                        // dialog.setTitle(getResources().getString(R.string.upc_vin_illegal));
                        //
                        // }
                    }
                });
                dialog.setNegativeButton(R.string.upc_cancel, new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                        finish();
                    }
                });
                if (isFront)
                {
                    dialog.show();
                }
            }
        });

    }

    class VINInputValidate implements VINChecker.Listener
    {
        CustomDialog dialog;
        EditText input;
        private final String title = getString(R.string.upc_vin_validation);

        @Override
        public void onCheck(boolean isValid)
        {
            if (isValid)
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (dialog != null)
                        {
                            VIN = (input.getText().toString()).toUpperCase();
                            application.saveVIN(VIN);
                            sharePrefConfigCache.edit().putString("vin", VIN).commit();
                            dialog.dismiss();
                        }
                    }
                });
                webservice.addListener(choiceListAdapter);
                webservice.queryCarBrandListByVIN(VIN, serialNumber);
            }
            else
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (dialog != null)
                        {
                            dialog.setTitle(title);
                            input.setText("");
                        }
                    }
                });
            }
        }

        @Override
        public void onRecheck(final String trim, final int error)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (dialog != null)
                    {
                        dialog.setTitle(title);
                        input.setText(trim);
                    }
                }
            });
        }

        public void checkVIN()
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    dialog = new CustomDialog(context);
                    dialog.setTitle(title);
                    // dialog.setCancelable(false);
                    input = new EditText(DiagSoftConfigureActivity.this);
                    input.setHint(R.string.upc_please_input_vin_number_here);
                    String lastVin = sharePrefConfigCache.getString("vin", "");
                    if (!lastVin.equals(""))
                    {
                        input.setText(lastVin);
                    }
                    input.setFocusable(true);
                    dialog.setView(input);
                    dialog.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            VINChecker cheker = new VINChecker(context, VINInputValidate.this);
                            VIN = input.getText().toString().toUpperCase();
                            cheker.doCheck(VIN);
                        }
                    });
                    dialog.setNegativeButton(R.string.upc_cancel, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    if (isFront)
                    {
                        dialog.show();
                    }
                }
            });
        }

    }

    /**
     * ��ѹ�ļ�
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

    // �ļ���ѹ����
    class UnzipFileListener implements ZipHelper.UnzipListener
    {
        @Override
        public void onUnzipping(final String file, final String destDir, final int percent)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (unzipFileDialog == null)
                    {
                        unzipFileDialog = new CustomProgressDialog(context);
                    }
                    unzipFileDialog.setTitle(R.string.zip_decompressing_now);
                    unzipFileDialog.setProgress(percent);
                    unzipFileDialog.setMessage(getResources().getString(R.string.zip_get_the_file) + file + getResources().getString(R.string.zip_decompress_to) + destDir);
                    if (isFront)
                    {
                        unzipFileDialog.show();
                    }
                }
            });
        }

        /**
         * zip�ļ���ѹ���
         */
        @Override
        public void onUnzipFinished(final File file, final String destFile)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (unzipFileDialog != null)
                    {
                        unzipFileDialog.dismiss();
                    }
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
                            Log.e(TAG, "���ش���,����ZIP��!");
                        return;
                    }
                    String[] sa = subString.split("_");

                    if (sa.length < 4)
                    {
                        if (D)
                            Log.e(TAG, "�汾��Ϣ��������");
                        return;
                    }
                    language = sa[sa.length - 1];
                    version = sa[sa.length - 3] + "." + sa[sa.length - 2];
                    vehicle = (sa.length == 4) ? sa[sa.length - 4] : sa[sa.length - 5] + "_" + sa[sa.length - 4];
                    if (D)
                        Log.d(TAG, "���صõ���������" + "����: " + vehicle + "�汾: " + version + "����: " + language);

                    // �汾����v10.38�Ĳ���Ҫ���� INI �ļ�
                    boolean ignoreIniFile = false;
                    // try {
                    // if(new Version(version).LessEqualTo(new
                    // Version("V10.38")))
                    // {
                    // ignoreIniFile = true;
                    // }
                    // } catch (VersionStyleNotMatchException e) {
                    // e.printStackTrace();
                    // }
                    if (!ignoreIniFile)
                    {
                        // ��ȡdpusys.ini�ļ� �������֮��д���豸��
                        // ���ļ��ĳ���, 4 �ֽ�
                        byte[] temp = FileTrunc.getByteRegion(file, file.length() - 4, file.length());

                        int initFileLen = ((temp[0] & 0xff) << 24) | ((temp[1] & 0xff) << 16) | ((temp[2] & 0xff) << 8) | (temp[3] & 0xff);

                        Log.e(TAG, "dpusys.ini�ļ��ĳ���Ϊ: " + initFileLen + " Bytes");
                        // INI �ļ��ı���λ��
                        File dpu_sys_iniFile = new File(UpdateCenterConstants.VEHICLES_DIR, File.separator + vehicle + File.separator + version + File.separator + "dpusys.ini");
                        // ��ȡ�ļ�
                        FileTrunc.trunc(file, dpu_sys_iniFile, file.length() - (temp.length + initFileLen), file.length() - temp.length - 1);
                    }
                    application.setLastUpdateConfig(serialNumber, vehicle, version, language, destFile);
                    final CustomAlertDialog dlg = new CustomAlertDialog(activity);
                    dlg.setTitle(R.string.upc_tips);
                    dlg.setMessage(R.string.upc_download_success);
                    dlg.setOnKeyListener(new OnKeyListener()
                    {
                        @Override
                        public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
                        {
                            if (aKeyCode == KeyEvent.KEYCODE_BACK && aEvent.getAction() == KeyEvent.ACTION_UP)
                            {
                                dlg.dismiss();
                                finish();
                            }
                            return true;
                        }
                    });
                    dlg.setPositiveButton(R.string.upc_update_right_now, new OnClickListener()// ��������
                        {
                            @Override
                            public void onClick(View aV)
                            {
                                dlg.dismiss();
                                DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
                                params.setSerialNumber(serialNumber);
                                params.setVehiecle(vehicle);
                                params.setVersion(version);
                                params.setLanguage(language);
                                params.setFileAbsolutePath(destFile);
                                params.setUpadteType(4);
                                Intent intent = new Intent(DiagSoftConfigureActivity.this, FirmwareUpdate.class);
                                intent.putExtra("diagsoft_update_config_params", params);
                                MyCarApplication.params = params;

                                startActivity(intent);
                                finish();
                            }
                        });
                    dlg.setNegativeButton(R.string.upc_later, new OnClickListener()// �Ժ���˵,��������
                        {
                            @Override
                            public void onClick(View aV)
                            {
                                dlg.dismiss();
                                finish();
                            }
                        });
                    if (isFront)
                    {
                        dlg.show();
                    }
                }
            });
        }

        @Override
        public void onUnzipError(final File zipFile, final Object reason)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    // zipFile.delete();// �ѳ�����ļ�ɾ��!
                    if (unzipFileDialog != null)
                        unzipFileDialog.dismiss();
                    final CustomDialog dlg = new CustomDialog(context);
                    dlg.setTitle(R.string.zip_decompress_error);
                    dlg.setMessage(reason.toString());
                    dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dlg.dismiss();
                            finish();
                        }
                    });
                    if (isFront)
                    {
                        dlg.show();
                    }
                }
            });
        }
    }

    class LanguageListListener implements WebRequestListener
    {
        ProgressDialog pdlg;

        @Override
        public void onStartRequest()
        {
        }

        @Override
        public void onRequestResult(final Object result)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                    if (result instanceof SoftLanguageItem[])
                    {
                        final SoftLanguageItem[] sa = (SoftLanguageItem[]) result;
                        final String[] textArray = new String[sa.length];
                        for (int i = 0; i < sa.length; i++)
                        {
                            Log.e(TAG, "��ϸid:" + sa[i].getVersionDetailId() + " ����:" + sa[i].getLanguageName());
                            textArray[i] = sa[i].getLanguageName();
                        }
                        // ����ѡ��Ի���
                        final CustomAlertDialog dlg = new CustomAlertDialog(DiagSoftConfigureActivity.this);
                        dlg.setTitle(getResources().getString(R.string.upc_please_choose));
                        ListView lv = new ListView(DiagSoftConfigureActivity.this);
                        lv.setDividerHeight(1);
                        lv.setDivider(resources.getDrawable(R.drawable.main_divider));
                        lv.setScrollingCacheEnabled(false);
                        dlg.setView(lv);
                        lv.setAdapter(new CustomAdapter(context, textArray));
                        lv.setOnItemClickListener(new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                dlg.dismiss();
                                Log.e(TAG, "ѡ�����ϸID:" + sa[position].getVersionDetailId());
                                language = sa[position].getLanguageName().equals("���ļ���") ? "CN" : "EN";
                                httpDownloader = new HttpDownloadManager(context);
                                httpDownloader.addListener(new DiagSoftDownloadListener());
                                httpDownloader.downloadDiagSoft(MyCarActivity.cc, serialNumber, sa[position].getVersionDetailId(), UpdateCenterConstants.TEMP_DIR);
                            }
                        });
                        dlg.setPositiveButton(R.string.upc_cancel, new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dlg.dismiss();
                            }
                        });
                        if (isFront)
                        {
                            dlg.show();
                        }
                    }
                }
            });
        }

        @Override
        public void onFinished(Object result)
        {
        }

        @Override
        public void onResponseError(final Object reason)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                    errorInfoDialog(reason.toString(), new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            retry();
                        }
                    });
                }
            });
        }
    }

    private class CustomAdapter extends BaseAdapter
    {
        ArrayList<String> data;
        Context cont;
        LayoutInflater inf;

        public CustomAdapter(Context c, ArrayList<String> data)
        {
            cont = c;
            inf = LayoutInflater.from(cont);
            this.data = data;
        }

        public CustomAdapter(Context c, String[] d)
        {
            cont = c;
            inf = LayoutInflater.from(cont);
            this.data = new ArrayList<String>();
            for (int i = 0; i < d.length; i++)
            {
                this.data.add(d[i]);
            }
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = inf.inflate(R.layout.diagsoft_config_list, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.text);
            tv.setText(data.get(position));
            return convertView;
        }
    }

    /**
     * ������ʾ�Ի���
     * @param message �������Ϣ
     * @param retry ���Եķ���
     */
    private void errorInfoDialog(final String message, final Runnable retry)
    {
        final CustomAlertDialog dlg = new CustomAlertDialog(this);
        dlg.setTitle(R.string.upc_error);
        dlg.setCancelable(false);
        dlg.setMessage(message);
        dlg.setPositiveButton(R.string.upc_exit, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
                finish();
            }
        });
        if (retry != null)
        {
            dlg.setNegativeButton(R.string.upc_retry, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dlg.dismiss();
                    handler.post(retry);
                }
            });
        }
        if (isFront)
        {
            dlg.show();
        }
    }

    // �Ƿ����ĳ������ѡ��
    private boolean skipItem(String str)
    {
        if (str == null || str.contains("*"))// Ϊ�ջ��߰���**�ľͲ���ʾ
        {
            return true;
        }
        return false;
    }

    /**
     * ��ʾ��ʷ������Ϣ,�����һ��
     * @param cbi ����������Ϣ
     */
    private void showHistorialConfigInfoDialog(final CarBaseInfo cbi)
    {
        StringBuilder sb = new StringBuilder();
        if (cbi == null)
        {
            errorInfoDialog(getResources().getString(R.string.upc_server_error), null);
        }
        else
        {
            if (!skipItem(cbi.getCarBrandName()))// ��ϵ����
            {
                sb.append(getString(R.string.upc_car_type) + ":" + cbi.getCarBrandName() + "\n");
            }
            if (!skipItem(cbi.getCarModel()))// ��������
            {
                sb.append(getString(R.string.upc_vehicle) + ":" + cbi.getCarModel() + "\n");
            }
            if (!skipItem(cbi.getCarDisplacement()))// ����
            {
                sb.append(getString(R.string.upc_displacement) + ":" + cbi.getCarDisplacement() + "\n");
            }
            if (!skipItem(cbi.getCarEngineType()))// ����������
            {
                sb.append(getString(R.string.upc_engine) + ":" + cbi.getCarEngineType() + "\n");
            }
            if (!skipItem(cbi.getCarGearboxType()))// ��������
            {
                sb.append(getString(R.string.upc_gearbox) + ":" + cbi.getCarGearboxType() + "\n");
            }
            if (!skipItem(cbi.getCarProducingYear()))// ���
            {
                sb.append(getString(R.string.upc_year_brand) + ":" + cbi.getCarProducingYear() + "\n");
            }

            final CustomAlertDialog dlg = new CustomAlertDialog(this);
            dlg.setTitle(getString(R.string.upc_latest_config_info));
            dlg.setMessage(sb.toString());

            dlg.setOnKeyListener(new OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP)
                    {
                        dialog.dismiss();
                        finish();
                    }
                    return false;
                }
            });

            dlg.setPositiveButton(R.string.upc_modify_config, new OnClickListener()// �޸�����
                {
                    @Override
                    public void onClick(View v)
                    {
                        dlg.dismiss();
                        webservice.addListener(new QueryRemainConfiguableCountListener(context, serialNumber));
                        webservice.getRemainingConfigCount(serialNumber);
                    }
                });

            dlg.setNegativeButton(R.string.upc_download_config, new OnClickListener()// ֱ����������
                {
                    @Override
                    public void onClick(View v)
                    {
                        dlg.dismiss();
                        // ֱ��ȥ��������
                        new GetLanguageListThread(cbi.getCarBrandId(), Env.GetCurrentLanguage(), new WebRequestListener()
                        {
                            @Override
                            public void onStartRequest()
                            {
                            }

                            @Override
                            public void onResponseError(final Object reason)
                            {
                                handler.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        errorInfoDialog(reason.toString(), null);
                                    }
                                });
                            }

                            @Override
                            public void onRequestResult(Object result)
                            {
                                // �����������ȡ��ϸid������
                                if (result instanceof SoftwareLanguageList)
                                {
                                    SoftwareLanguageList languageList = (SoftwareLanguageList) result;

                                    if (languageList.getCount() > 0)// �п����������԰汾
                                    {
                                        int detailId = languageList.getLanguageItemAt(0).getVersionDetailId();
                                        httpDownloader = new HttpDownloadManager(context);
                                        httpDownloader.addListener(new DiagSoftDownloadListener());
                                        httpDownloader.downloadDiagSoft(MyCarActivity.cc, serialNumber, detailId, UpdateCenterConstants.TEMP_DIR);
                                    }
                                    else
                                    {
                                        handler.post(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                errorInfoDialog("No available languages!", null);
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFinished(Object result)
                            {
                            }
                        }).start();
                    }
                });
            if (isFront)
            {
                dlg.show();
            }
        }

    }

    /**
     * ����
     */
    public void retry()
    {
        choiceListAdapter.removeAllItems();
        endFlag = CONFIG_PROCESS_NOT_DONE;
        webservice.removeAllListeners();
        webservice.addListener(choiceListAdapter);
        webservice.queryCarBrandListByVIN(VIN, serialNumber);
    }

    /**
     * ������ʾ���Բ�ѯ��ϵ�����б�,���û�и�������Ĭ�Ϸ���Ӣ��
     */
    class GetLanguageListThread extends Thread
    {
        WebRequestListener listener;
        String carBrandId;
        String dispLanguage;
        TimeoutCounter timer;

        public GetLanguageListThread(String cId, String displayLang, WebRequestListener listener)
        {
            this.listener = listener;
            this.carBrandId = cId;
            this.dispLanguage = displayLang;
        }

        @Override
        public void run()
        {
            if (listener != null)
                listener.onStartRequest();
            // ��װ�������
            TreeMap<String, String> params = new TreeMap<String, String>();
            params.put("carBrandId", this.carBrandId);
            params.put("displayLan", this.dispLanguage);
            Log.d(TAG, "GetLanguageListThread() carbrandId :" + carBrandId);
            Log.d(TAG, "GetLanguageListThread() displayLan :" + dispLanguage);
            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_CLASS_ONE_KEY_DIAG, "getLatestDiagSoftLan", null, params, true);
            requestParameter.wsUrl = UpdateCenterConstants.UPDATE_DIAG_ONKEY_CONFIG_SERVICE;// һ��ɨ�����ʽURL
            WebServiceManager wsm = new WebServiceManager(requestParameter);

            Object ret = wsm.execute().object;

            timer = new TimeoutCounter(15, new Callback()
            {
                @Override
                public void onTimeout()
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getResources().getString(R.string.upc_server_no_response));
                    }
                }
            });

            if (ret == null || ret instanceof SoapFault)
            {
                if (listener != null)
                {
                    listener.onResponseError(getResources().getString(R.string.upc_server_no_response));
                }
                return;
            }

            SoapObject result = (SoapObject) ret;
            if (result != null && result.getProperty(0) != null)
            {
                timer.cancel();
                SoapObject so = (SoapObject) result.getProperty(0);

                String msg = so.hasProperty("message") ? so.getProperty("message").toString() : "";

                int code = so.hasProperty("code") ? Integer.valueOf((so.getProperty("code").toString())) : -1;

                if (code == UsercenterConstants.RESULT_SUCCESS)
                {
                    // ��ȡ�����б�
                    SoapObject soLanguageList = so.hasProperty("softLanguageList") ? (SoapObject) so.getProperty("softLanguageList") : null;
                    if (soLanguageList != null && soLanguageList.getPropertyCount() > 0)
                    {
                        int count = soLanguageList.getPropertyCount();
                        SoftLanguageItem[] languageItems = new SoftLanguageItem[count];
                        for (int i = 0; i < count; i++)
                        {
                            SoftLanguageItem item = new SoftLanguageItem();
                            SoapObject soLanguage = (SoapObject) (soLanguageList.getProperty(i));
                            if (soLanguage.hasProperty("versionDetailId"))
                            {
                                item.setVersionDetailId(Integer.valueOf((soLanguage.getProperty("versionDetailId").toString())));
                            }
                            if (soLanguage.hasProperty("lanName"))
                            {
                                item.setLanguageName(((SoapObject) soLanguageList.getProperty(i)).getProperty("lanName").toString());
                            }
                            languageItems[i] = item;
                        }
                        SoftwareLanguageList languageList = new SoftwareLanguageList(context, languageItems);
                        if (listener != null)
                        {
                            listener.onRequestResult(languageList);
                        }
                    }
                    else
                    {
                        if (listener != null)
                            listener.onResponseError("soap parse error , no property softLanguageList found!");
                    }
                }
                else
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getResources().getString(R.string.upc_querying_failed));
                    }
                }

            }
            else
            {
                timer.cancel();
                if (listener != null)
                {
                    listener.onResponseError(getResources().getString(R.string.upc_querying_failed));
                }
            }
        }
    };

    // ��ʼһ�����
    class BeginOneKeyCalcThread extends Thread
    {
        String carBrandId;
        String vin;
        String serialNum;
        WebRequestListener listener;
        TimeoutCounter timer;

        public BeginOneKeyCalcThread(String carBrandId, String vin, String serialNum, WebRequestListener listener)
        {
            this.carBrandId = carBrandId;
            this.vin = vin;
            this.serialNum = serialNum;
            this.listener = listener;
        }

        public WebRequestListener getListener()
        {
            return listener;
        }

        public void setListener(WebRequestListener listener)
        {
            this.listener = listener;
        }

        @Override
        public void run()
        {
            TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
            paraMap.put("carBrandId", carBrandId);
            paraMap.put("VIN", vin);
            paraMap.put("serialNo", serialNum);

            if (listener != null)
                listener.onStartRequest();

            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_CLASS_ONE_KEY_DIAG, "beginOneKeyDiagCalc", null, paraMap, true);
            requestParameter.wsUrl = UpdateCenterConstants.UPDATE_DIAG_ONKEY_CONFIG_SERVICE;// һ��ɨ�����ʽURL

            WebServiceManager wsm = new WebServiceManager(requestParameter);

            timer = new TimeoutCounter(15, new Callback()
            {
                @Override
                public void onTimeout()
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getResources().getString(R.string.upc_server_no_response));
                    }
                }
            });

            Object wsRet = wsm.execute().object;
            if (!(wsRet instanceof SoapObject))
            {
                return;
            }
            SoapObject result = (SoapObject) wsRet;

            OneKeyDiagResult oneKeyDiagResult = new OneKeyDiagResult();

            if (result != null && result.getProperty(0) != null)
            {
                timer.cancel();
                SoapObject so = (SoapObject) result.getProperty(0);
                String msg = so.hasProperty("message") ? so.getProperty("message").toString() : "";
                int code = so.hasProperty("code") ? new Integer((so.getProperty("code").toString())) : -1;

                if (code == UsercenterConstants.RESULT_SUCCESS)
                {
                    // ��ʼһ�����
                    OneKeyDiag oneKeyDiag = null;

                    if (so.hasProperty("oneKeyDiag"))// ����һ����Ͻ��
                    {
                        Log.d(TAG, "--- oneKeyDiag");
                        oneKeyDiag = new OneKeyDiag(context);
                        SoapObject soOneKeyDiag = (SoapObject) so.getProperty("oneKeyDiag");

                        if (soOneKeyDiag.hasProperty("conditionId")) // ������
                        {
                            String conditionID = soOneKeyDiag.getProperty("conditionId").toString();
                            oneKeyDiag.setConditionId(conditionID);
                        }
                        if (soOneKeyDiag.hasProperty("value")) // ����ֵ�б�
                        {
                            oneKeyDiag.setValue(soOneKeyDiag.getProperty("value").toString());
                        }
                        if (soOneKeyDiag.hasProperty("endFlag"))// һ������Ľ�����־
                        {
                            int endFlag = Integer.valueOf(soOneKeyDiag.getProperty("endFlag").toString());
                            oneKeyDiag.setEndFlag(endFlag);
                        }
                        oneKeyDiagResult.setOneKeyDiag(oneKeyDiag);
                    }

                    if (so.hasProperty("carBaseInfo"))// ���ͻ�����Ϣ
                    {
                        SoapObject soCarBaseInfo = (SoapObject) so.getProperty("carBaseInfo");
                        CarBaseInfo cbi = new CarBaseInfo();

                        if (oneKeyDiag.getEndFlag() == CONFIG_PROCESS_DONE)// ���ý���
                        {
                            if (soCarBaseInfo.hasProperty("diagEntranceId"))// ��ȡ���ID
                            {
                                cbi.setDiagEntranceId(soCarBaseInfo.getProperty("diagEntranceId").toString());
                            }
                            if (listener != null)
                            {
                                listener.onFinished(cbi);
                            }
                        }
                        oneKeyDiagResult.setCarBaseInfo(cbi);
                    }
                    if (listener != null)
                    {
                        listener.onRequestResult(oneKeyDiagResult);
                    }
                }
                else
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getResources().getString(R.string.upc_server_error));
                    }
                }
            }
            else
            {
                timer.cancel();
                if (listener != null)
                {
                    listener.onResponseError(getResources().getString(R.string.upc_error));
                }
            }
        }
    };

    class OneKeyCalcThread extends Thread
    {
        String conditionId;
        String selectedValue;
        String calcResult;
        WebRequestListener listener;
        TimeoutCounter timer;

        public OneKeyCalcThread(String conditionId, String selectedValue, WebRequestListener listener)
        {
            this.conditionId = conditionId;
            this.selectedValue = selectedValue;
            this.listener = listener;
        }

        @Override
        public void run()
        {
            // ��װ�������
            TreeMap<String, String> paraMap = new TreeMap<String, String>();
            paraMap.put("conditionId", conditionId);
            paraMap.put("selectedValue", selectedValue);
            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_CLASS_ONE_KEY_DIAG, "OneKeyDiagCalc", null, paraMap, true);
            requestParameter.wsUrl = UpdateCenterConstants.UPDATE_DIAG_ONKEY_CONFIG_SERVICE;// һ��ɨ�����ʽURL
            WebServiceManager wsm = new WebServiceManager(requestParameter);

            if (listener != null)
            {
                listener.onStartRequest();
            }

            Object obj = wsm.execute().object;

            timer = new TimeoutCounter(15, new Callback()
            {
                @Override
                public void onTimeout()
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getString(R.string.upc_server_no_response));
                        return;
                    }
                }
            });

            SoapObject result = null;

            if (obj != null && obj instanceof SoapObject)
            {
                result = (SoapObject) obj;
            }
            else
            {
                if (listener != null)
                {
                    listener.onResponseError("soap result error!");
                }
                return;
            }

            if (result != null && result.getProperty(0) != null)
            {
                timer.cancel();

                SoapObject so = (SoapObject) result.getProperty(0);
                String msg = so.hasProperty("message") ? so.getProperty("message").toString() : "";

                int code = so.hasProperty("code") ? new Integer((so.getProperty("code").toString())) : -1;

                OneKeyDiagResult oneKeyDiagResult = new OneKeyDiagResult();
                if (code == UsercenterConstants.RESULT_SUCCESS)
                {
                    OneKeyDiag oneKeyDiag = null;

                    if (so.hasProperty("oneKeyDiag"))// ����һ����Ͻ��
                    {
                        Log.d(TAG, "--- oneKeyDiagDTO");
                        oneKeyDiag = new OneKeyDiag(context);
                        SoapObject soOneKeyDiag = (SoapObject) so.getProperty("oneKeyDiag");

                        if (soOneKeyDiag.hasProperty("conditionId")) // ������
                        {
                            oneKeyDiag.setConditionId(soOneKeyDiag.getProperty("conditionId").toString());
                        }

                        if (soOneKeyDiag.hasProperty("value")) // ����ֵ�б�
                        {
                            oneKeyDiag.setValue(soOneKeyDiag.getProperty("value").toString());
                        }

                        if (soOneKeyDiag.hasProperty("endFlag"))// һ������Ľ�����־
                        {
                            int endFlag = Integer.valueOf(soOneKeyDiag.getProperty("endFlag").toString());
                            oneKeyDiag.setEndFlag(endFlag);
                        }

                        oneKeyDiagResult.setOneKeyDiag(oneKeyDiag);
                    }
                    else
                    {
                        String err = "soap parse oneKeyDiag Error , not contain this property";
                        ;
                        if (D)
                            Log.e(TAG, err);
                        // if(listener!=null)listener.onResponseError(err);
                    }

                    if (so.hasProperty("carBaseInfo"))// ���ͻ�����Ϣ
                    {
                        SoapObject soCarBaseInfo = (SoapObject) so.getProperty("carBaseInfo");
                        CarBaseInfo cbi = new CarBaseInfo();
                        if (soCarBaseInfo.hasProperty("diagEntranceId"))// ��ȡ���ID
                        {
                            cbi.setDiagEntranceId(soCarBaseInfo.getProperty("diagEntranceId").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carBrandId"))// ��ϵID
                        {
                            cbi.setCarBrandId(soCarBaseInfo.getProperty("carBrandId").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carBrandVin"))// ����VIN��
                        {
                            cbi.setCarBrandVin(soCarBaseInfo.getProperty("carBrandVin").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carProducingAreaId"))// ����ID
                        {
                            cbi.setCarProducingAreaId(Integer.valueOf(soCarBaseInfo.getProperty("carProducingAreaId").toString()));
                        }
                        if (soCarBaseInfo.hasProperty("carModel"))// ����
                        {
                            cbi.setCarModel(soCarBaseInfo.getProperty("carModel").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carProducingYear"))// ���
                        {
                            cbi.setCarProducingYear(soCarBaseInfo.getProperty("carProducingYear").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carEngineType"))// ����������
                        {
                            cbi.setCarEngineType(soCarBaseInfo.getProperty("carEngineType").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carDisplacement"))// ����
                        {
                            cbi.setCarDisplacement(soCarBaseInfo.getProperty("carDisplacement").toString());
                        }
                        if (soCarBaseInfo.hasProperty("carGearboxType"))// ����
                        {
                            cbi.setCarGearboxType(soCarBaseInfo.getProperty("carGearboxType").toString());
                        }
                        oneKeyDiagResult.setCarBaseInfo(cbi);
                    }
                    else
                    {
                        String err = "soap parse carBaseInfo Error , not contain this property";
                        if (D)
                            Log.e(TAG, err);
                        // if(listener!=null)listener.onResponseError(err);
                    }
                    if (listener != null)
                    {
                        listener.onRequestResult(oneKeyDiagResult);
                    }
                }
                else
                {
                    if (listener != null)
                    {
                        listener.onResponseError(getResources().getString(R.string.upc_server_error));
                    }
                }
            }
            else
            {
                timer.cancel();
                if (listener != null)
                {
                    listener.onResponseError(getResources().getString(R.string.upc_querying_failed));
                }
            }
        }
    }

    private void initViews()
    {
        // ����ѡ���
        lvChoiceList = (ListView) findViewById(R.id.lv_choicelist);
        lvChoiceList.setDivider(null);
        lvChoiceList.setFooterDividersEnabled(true);
        ArrayList<ChoiceGroup> groupList = new ArrayList<ChoiceGroup>();
        choiceListAdapter = new ChoiceListAdapter(this, groupList);
        lvChoiceList.setAdapter(choiceListAdapter);

        btGoback = (Button) findViewById(R.id.bt_soft_configure_goback);
        btGoback.setOnClickListener(this);

        btVinHelp = (Button) findViewById(R.id.btn_vin_help);
        btVinHelp.setText("?");
        btVinHelp.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        // btDownload = (Button)findViewById(R.id.btn_download);
        // btDownload.setOnClickListener(new OnClickListener()
        // {
        // @Override
        // public void onClick(View v)
        // {
        // if(endFlag == CONFIG_PROCESS_DONE)
        // {
        // httpDownloader = new HttpDownloadWrapper(context);
        // httpDownloader.addListener(new DiagSoftDownloadListener());
        // httpDownloader.downloadDiagSoft(MyCarActivity.cc, serialNumber,
        // versionDetailId);
        // }
        // }
        // });

        edtVinInput = (EditText) findViewById(R.id.edt_vin_input);
        String lastVin = sharePrefConfigCache.getString("vin", "");
        if (lastVin != null && !lastVin.equals(""))
        {
            VIN = lastVin;
            edtVinInput.setText(lastVin);
        }

        btVinCheck = (Button) findViewById(R.id.btn_vin_check);
        btVinCheck.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edtVinInput.setFocusable(false);
            }
        });

        vinArea = (ViewGroup) findViewById(R.id.vin_area);
        vinArea.setVisibility(View.GONE);

        btGoNext = (Button) findViewById(R.id.bt_soft_configure_gonext);
        btGoNext.setOnClickListener(this);

        tvSerialNumber = (TextView) findViewById(R.id.tv_serial_number);
        tvSerialNumber.setText(serialNumber);
    }

    private class ChoiceListAdapter extends BaseAdapter implements GroupSelectionChangeListener, WebServiceListener
    {
        ArrayList<ChoiceGroup> groupList;
        Context cont;
        LayoutInflater inf;
        ProgressDialog pdlg;

        public ChoiceListAdapter(Context c, ArrayList<ChoiceGroup> data)
        {
            cont = c;
            inf = LayoutInflater.from(cont);
            this.groupList = data;
        }

        public ChoiceListAdapter(Context c, ChoiceGroup[] groups)
        {
            cont = c;
            inf = LayoutInflater.from(cont);
            this.groupList = new ArrayList<ChoiceGroup>();
            for (int i = 0; i < groups.length; i++)
            {
                this.groupList.add(groups[i]);
            }
        }

        public void addItem(ChoiceGroup group)
        {
            groupList.add(group);
            notifyDataSetChanged();
        }

        public void removeAllItems()
        {
            groupList.clear();
            notifyDataSetChanged();
        }

        public void removeItem(int index)
        {
            if (index >= 0 && index < groupList.size())
            {
                groupList.remove(index);
            }
            notifyDataSetChanged();
        }

        public void removeItemBelow(int from)
        {
            if (from != groupList.size())
            {
                int delcnt = groupList.size() - from;
                System.out.println("delcnt:" + delcnt);
                for (int i = 0; i < delcnt; i++)
                {
                    ChoiceGroup group = groupList.remove(from);
                    System.out.println("item been removed:" + group.getType());
                }
            }
            notifyDataSetChanged();
        }

        /**
         * �г������е���Ϣ
         */
        public String getAllRowItemsInfo()
        {
            StringBuilder sb = new StringBuilder();
            if (groupList.size() > 0)
            {
                for (int i = 0; i < groupList.size(); i++)
                {
                    ChoiceGroup group = groupList.get(i);
                    sb.append("\t" + group.getType() + " : " + group.getItemAt(group.getCurrentIndex()) + "\n");
                }
                return sb.toString();
            }
            return "";
        }

        @Override
        public int getCount()
        {
            return groupList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return groupList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = inf.inflate(R.layout.choice_list_item, null);

            TextView tv = (TextView) convertView.findViewById(R.id.left);
            Button bt = (Button) convertView.findViewById(R.id.button);

            bt.setOnClickListener(new ItemOnClickListener(position, this, this));
            tv.setText(groupList.get(position).getType());
            bt.setText(groupList.get(position).getItemAt(groupList.get(position).getCurrentIndex()));
            return convertView;
        }

        @Override
        public void onSelectionChanged(ChoiceGroup group, int groupPosition, int oldIndex, int newIndex)
        {
            removeItemBelow(groupPosition + 1);// ����

            if (group.getId().equalsIgnoreCase(ConditonIdTable.CONDITION_ID_CAR_BRAND))// ��ϵ
            {
                CarBrandList list = (CarBrandList) group;
                carBrandId = list.getCarBrandIdAt(newIndex);// carBrandId ��ȫ�ֵ�
                webservice.beginConfigurate(carBrandId, VIN, serialNumber);
            }
            else if (group.getId().equalsIgnoreCase(ConditonIdTable.CONDITION_ID_SOFT_LANGUAGE))// �����б�
            {
                SoftwareLanguageList result = (SoftwareLanguageList) group;
                final int downloadId = result.getSelectedVersionDetailId();// ��������Ҫ���id
                versionDetailId = downloadId;
                language = result.getLanguageItemAt(0).getLanguageName().toString().trim().equals("���ļ���") ? "CN" : "EN";
                String configInfos = getAllRowItemsInfo();// �г���ǰ����������:
                                                          // ѯ���û��Ƿ�Ҫ�������
                String title = getString(R.string.upc_diagsoft_download_confirm);

                final CustomAlertDialog dlg = new CustomAlertDialog(DiagSoftConfigureActivity.this);
                dlg.setTitle(title);
                dlg.setMessage(configInfos);
                // ȷ������
                dlg.setPositiveButton(R.string.upc_download, new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dlg.dismiss();
                        downloadDiagSoft(serialNumber,language, downloadId);
                    }
                });
                dlg.setNegativeButton(R.string.upc_reselect, new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dlg.dismiss();
                        retry();
                    }
                });
                if (isFront)
                {
                    dlg.show();
                }
            }
            else
            {
                OneKeyDiagResult result = (OneKeyDiagResult) group;
                OneKeyDiag okd = result.getOneKeyDiag();
                String conditionId = okd.getConditionId();
                String value = okd.getItemAt(newIndex);
                webservice.resumeConfigurate(conditionId, value);
            }
        }

        @Override
        public void onStartWebServiceRequest(Object service, SoapRequest request)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    pdlg = new ProgressDialog(context);
                    pdlg.setCancelable(false);
                    pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pdlg.setMessage(getString(R.string.upc_please_wait));
                    if (isFront)
                    {
                        pdlg.show();
                    }
                }
            });
        }

        @Override
        public void onWebServiceSuccess(Object service, final SoapResponse response)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                }
            });
            if (response.getMethod().equals(SoapMethod.QUERY_CAR_BRAND_LIST_BY_VIN))// ��ϵ�б�
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Object result = response.getResult();
                        CarBrandList group = (CarBrandList) result;
                        addItem(group);
                    }
                });
            }
            else if (response.getMethod().equals(SoapMethod.BEGIN_ONE_KEY_CALC) // ��ʼһ��ɨ��
                || response.getMethod().equals(SoapMethod.ONE_KEY_DIAG_CALC))//
            {

                Object result = response.getResult();
                final OneKeyDiagResult group = (OneKeyDiagResult) result;
                if (group.getFlag() != ConfigControlFlag.PROCESS_DONE)// һ��ɨ�����
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            addItem(group);
                        }
                    });
                }
                else
                {
                    endFlag = ConfigControlFlag.PROCESS_NOT_DONE;
                    webservice.queryLanguageList(String.valueOf(carBrandId), Env.GetCurrentLanguage());
                }
            }
            else if (response.getMethod().equals(SoapMethod.QUERY_DIAG_SOFT_LANGUAGE_LIST))
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Object result = response.getResult();
                        SoftwareLanguageList group = (SoftwareLanguageList) result;
                        addItem(group);
                    }
                });
            }
        }

        @Override
        public void onWebServiceErrors(Object service, final int code, SoapRequest request)
        {
            webservice.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                    {
                        pdlg.dismiss();
                    }
                    errorInfoDialog(getString(R.string.upc_server_error), null);
                }
            });
        }
    }

    class ItemOnClickListener implements OnClickListener
    {
        ChoiceListAdapter adapter;
        int position;
        GroupSelectionChangeListener listener;

        public ItemOnClickListener(int pos, ChoiceListAdapter adp, GroupSelectionChangeListener l)
        {
            this.adapter = adp;
            this.position = pos;
            this.listener = l;
        }

        @Override
        public void onClick(View v)
        {
            ChoiceGroup group = (ChoiceGroup) adapter.getItem(position);
            new PopupSelectDialog(position, activity, group, listener);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_soft_configure_goback:
                finish();
                break;

            case R.id.bt_soft_configure_gonext:
                if (endFlag == CONFIG_PROCESS_DONE)
                {
                    confirmDialog(serialNumber, versionDetailId);
                }
                else
                {
                    Toast.makeText(context, R.string.upc_config_not_finished, Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void confirmDialog(final String SerialNumber, final int versionDetailID)
    {
        String title = getString(R.string.upc_diagsoft_download_confirm);
        String configInfo = choiceListAdapter.getAllRowItemsInfo();

        final CustomDialog dlg = new CustomDialog(this);
        dlg.setTitle(title);
        dlg.setMessage(configInfo);
        dlg.setPositiveButton(R.string.upc_download, new OnClickListener()
        {
            @Override
            public void onClick(View v) // ��ʼ����������
            {
                dlg.dismiss();
                httpDownloader = new HttpDownloadManager(context);
                httpDownloader.addListener(new DiagSoftDownloadListener());
                httpDownloader.downloadDiagSoft(MyCarActivity.cc, serialNumber, versionDetailID, UpdateCenterConstants.TEMP_DIR);
            }
        });

        dlg.setNegativeButton(R.string.upc_cancel, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
                finish();
            }
        });
        if (isFront)
        {
            dlg.show();
        }
    }

    // ���������ؼ���
    class DiagSoftDownloadListener extends DefaultHttpListener
    {
        @Override
        public void onHttpStart(String url)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (downloadProgressDialog == null)
                    {
                        downloadProgressDialog = new CustomProgressDialog(context);
                        downloadProgressDialog.setOnKeyListener(new OnKeyListener()
                        {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                            {
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)// ���û����˷��ؼ���ʱ����ʾ
                                {
                                    final CustomAlertDialog dlg = new CustomAlertDialog(DiagSoftConfigureActivity.this);
                                    dlg.setTitle(getResources().getString(R.string.upc_tips));
                                    dlg.setCancelable(false);
                                    dlg.setMessage(R.string.upc_abort_operation_confirm);
                                    dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            if (downloadProgressDialog != null)
                                            {
                                                downloadProgressDialog.dismiss();
                                            }
                                            dlg.dismiss();
                                            finish();
                                        }
                                    });
                                    dlg.setNegativeButton(R.string.upc_cancel, new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            dlg.dismiss();
                                        }
                                    });
                                    if (isFront)
                                    {
                                        dlg.show();
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    downloadProgressDialog.setTitle(getString(R.string.upc_downloading));
                    if (isFront)
                    {
                        downloadProgressDialog.show();
                    }
                }
            });
        }

        @Override
        public void onHttpDownloadProgress(final int percent, int speed, int restHours, int restMinutes, int restSeconds)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (downloadProgressDialog != null)
                    {
                        downloadProgressDialog.setTitle(getString(R.string.upc_downloading));
                        downloadProgressDialog.setProgress(percent);
                    }
                }
            });
        }

        @Override
        public void onHttpException(final Object reason)
        {
            httpDownloader.removeListener(this);// dont forget this line
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    final CustomAlertDialog dlg = new CustomAlertDialog(activity);
                    dlg.setTitle(R.string.upc_error);
                    dlg.setCancelable(false);
                    dlg.setMessage(getString(R.string.upc_http_error_content_len_zero));
                    dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dlg.dismiss();
                            finish();
                        }
                    });
                    if (isFront)
                    {
                        dlg.show();
                    }
                }
            });
        }

        @Override
        public void onHttpFinish(final Object target, final Object extra)
        {
            httpDownloader.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (downloadProgressDialog != null)
                    {
                        downloadProgressDialog.setTitle(getString(R.string.upc_download_success));
                        downloadProgressDialog.dismiss();
                        if (target instanceof File)
                        {
                            unzipFile((File) target);// ��ѹ�ļ�
                        }
                        // ������Ҫ����������
                        application.setIsNeedUpdateDiagnoseSW(true);
                        if (!isHasHistory)
                        {
                            webservice.removeAllListeners();
                            webservice.addListener(new DownloadBinUpdateInfoListener(DiagSoftConfigureActivity.this, handler, httpDownloader, downloadBinDownloadListener, webservice));
                            // webservice.queryApkUpdateInfo(Constants.MYCAR_VERSION);
                            webservice.queryBinFileUpdateInfo(application.getCC(), serialNumber, Constants.DOWNLOAD_BIN_BASE_VERSION, "EN");
                        }
                    }
                }
            });
        }

    }

    class QueryDeviceHistoricalConfigListener implements WebServiceListener
    {
        ProgressDialog pdlg;
        String serialNum;
        Context context;
        Handler handler;

        public QueryDeviceHistoricalConfigListener(Context c, String sn)
        {
            this.serialNum = sn;
            this.context = c;
            this.handler = new Handler(context.getMainLooper());
        }

        @Override
        public void onStartWebServiceRequest(Object service, SoapRequest request)
        {
            // handler.post progress dialog
            Log.d(TAG, " " + request.toString());
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg == null)
                    {
                        pdlg = new ProgressDialog(context);
                        pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pdlg.setMessage(getString(R.string.upc_please_wait));
                        pdlg.setOnKeyListener(new OnKeyListener()
                        {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                            {
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                                {
                                    pdlg.dismiss();
                                    finish();
                                }
                                return false;
                            }
                        });
                        if (isFront)
                        {
                            pdlg.show();
                        }
                    }
                }
            });
        }

        @Override
        public void onWebServiceSuccess(Object service, SoapResponse response)
        {
            webservice.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                }
            });

            Log.d(TAG, " ��Ӧ :" + response.toString());

            if (response.getMethod().equals(SoapMethod.QUERY_HISTORICAL_CONFIG_INFO))
            {
                if (response.getCode() == DeviceConfigState.NO_HISTORICAL_CONFIG_DATA)// û��������Ϣ
                {
                    inputVin();
                    // new VINInputValidate().checkVIN();
                }
                else
                // ��ʾ��ʷ������Ϣ
                {
                    isHasHistory = true;
                    Object result = response.getResult();
                    final CarBaseInfo cbi = (CarBaseInfo) result;
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                showHistorialConfigInfoDialog(cbi);// ��ʾ��ʷ������Ϣ
                            }
                            catch (Exception e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onWebServiceErrors(Object service, final int code, SoapRequest request)
        {
            webservice.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                    errorInfoDialog(getString(R.string.upc_server_error), null);
                }
            });
        }
    }

    // ��ѯ�����޸����õĴ���
    class QueryRemainConfiguableCountListener implements WebServiceListener
    {
        ProgressDialog pdlg;
        String serialNum;
        Context context;
        Handler handler;

        public QueryRemainConfiguableCountListener(Context ctx, String sn)
        {
            this.context = ctx;
            this.serialNum = sn;
            this.handler = new Handler(context.getMainLooper());
        }

        @Override
        public void onStartWebServiceRequest(Object service, SoapRequest request)
        {
            Log.d(TAG, " " + request.toString());
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg == null)
                    {
                        pdlg = new ProgressDialog(context);
                        pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pdlg.setMessage(getString(R.string.upc_please_wait));
                        pdlg.setOnKeyListener(new OnKeyListener()
                        {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                            {
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                                {
                                    pdlg.dismiss();
                                    finish();
                                }
                                return false;
                            }
                        });
                        if (isFront)
                        {
                            pdlg.show();
                        }
                    }
                }
            });
        }

        @Override
        public void onWebServiceSuccess(Object service, SoapResponse response)
        {
            webservice.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                }
            });

            if (response.getMethod().equals(SoapMethod.QUERY_REMAINING_CONFIGURABLE_COUNT))
            {
                if (response.getCode() == 0)
                {
                    RemainConfigurationCount howmany = (RemainConfigurationCount) response.getResult();

                    if (howmany.getCount() == 0) // ���޸ĵ����ô����Ѿ�����
                    {
                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                errorInfoDialog(getString(R.string.upc_disallow_modify_config), null);
                            }
                        });
                    }
                    else
                    // �����޸�����
                    {
                        inputVin();
                        // new VINInputValidate().checkVIN();
                    }
                }
            }
        }

        @Override
        public void onWebServiceErrors(Object service, final int code, SoapRequest request)
        {
            webservice.removeListener(this);
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pdlg != null)
                        pdlg.dismiss();
                    errorInfoDialog(getString(R.string.upc_server_error), null);
                }
            });
        }
    }

    class DownloadDiagSoftThread extends Thread
    {
        String serialNo;
        String softDisplayLan;
        int softDetialId;
        public DownloadDiagSoftThread(String serialNo, String softDisplayLan, int softDetialId)
        {
            this.serialNo = serialNo;
            this.softDisplayLan = softDisplayLan;
            this.softDetialId = softDetialId;
        }

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            super.run();
            TreeMap paraMap = new TreeMap();
            paraMap.put("serialNo", serialNo);
            paraMap.put("softDisplayLan", language);
            RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_CLASS_ONE_KEY_DIAG, "getConfigedIniFileInfo", null, paraMap, true);
            WebServiceManager wsm = new WebServiceManager(DiagSoftConfigureActivity.this, requestParameter);
            SoapObject result;
            String iniFileContent = null;
            WSBaseResult wSBaseResult = (WSBaseResult) wsm.execute();
            if (wSBaseResult.responseCode == 0)
            {
                if (wSBaseResult.object instanceof SoapObject)
                {
                    result = (SoapObject) wSBaseResult.object;
                    if (result != null && result.getProperty(0) != null)
                    {
                        SoapObject so = (SoapObject) result.getProperty(0);
                        if (so != null && (so.getProperty("code").toString()).equals("0"))
                        {
                            iniFileContent = so.hasProperty("iniFileContent") ? so.getPropertyAsString("iniFileContent") : null;
                            updateDpusysini(iniFileContent,softDetialId);
                        }
                        else
                        {
                            handler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,
                                UserCenterCommon.getWebserviceResponseMessage(resources, -1)).sendToTarget();
                        }
                    }
                    else
                    {
                        handler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,
                            UserCenterCommon.getWebserviceResponseMessage(resources, -1)).sendToTarget();
                    }
                }
            }
            // ����IO�쳣
            else if (wSBaseResult.responseCode == 2)
            {
                if (D)
                    Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
                // ֪ͨUI���̵߳�¼���
                handler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_io_exceptin))
                    .sendToTarget();
            }
            // ����xml�����쳣
            else if (wSBaseResult.responseCode == 3)
            {
                if (D)
                    Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����Xml�����쳣");
                // ֪ͨUI���̵߳�¼���
                handler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0,
                    resources.getString(R.string.usercenter_service_parse_xml_exception)).sendToTarget();
            }

        }
    }
    
    /**
     * ����������
     * ���������
     * ���ȼ�鱾���Ƿ��Ѿ����ڸ���������������ڣ�ֻ��Ҫ����dpusys.ini�ļ�
     * ��������ز����ڸ�����������Ҫ����������װ��.
     * 
     * @since DBS V100
     */
    private void downloadDiagSoft(String serialNo, String softDisplayLan, int softDetialId)
    {
        
        new DownloadDiagSoftThread(serialNo, softDisplayLan,softDetialId).start();
    }
    
    private void updateDpusysini(String fileContent, int softDetialId)
    {
        String [] contentArray = fileContent.split("=");
        if (contentArray != null && contentArray.length > 0)
        {
            int i = 0;
            for (String string : contentArray)
            {
                i++;
                if (string.toUpperCase().endsWith("[SOFTWARE_INFO]VERSION"))
                {
                    version = "V" + contentArray[i].substring(0,contentArray[i].indexOf(";")).trim();
                }
                if (string.toUpperCase().endsWith("[CAR_INFO]CAR_NAME"))
                {
                    vehicle = contentArray[i].substring(0,contentArray[i].indexOf(";")).trim();
                }
            }
        }
        //01-10 14:33:43.839: W/System.err(22412): java.io.FileNotFoundException: /mnt/sdcard/cnlaunch/dbsCar/vehicles/BMW/V10.02/dpusys.ini (No such file or directory)

        //[USER_INFO]USERNAME=randy2289;CC_CODE=16706;USER_FIRSTNAME=;USER_LASTNAME=;[SOFTWARE_INFO]VERSION=10.02;LANGUAGE=zh_CN;[CAR_INFO]CAR_NAME=BMW;VIN=12345678901234567;SEARCH_ID=0xFEE1;SEARCH_INFO=;[ONE_KEY_SEARCH]STATE=0;[SYSTEM_LIST]SYS_NUM=0;[DS_LIST]DS_NUM=0;�Ѿ�����
        // INI �ļ��ı���λ��
        File dpu_sys_iniFile = new File(UpdateCenterConstants.VEHICLES_DIR, File.separator + vehicle + File.separator + version + File.separator + "dpusys.ini");
        if (dpu_sys_iniFile.exists())
        {
            FileTrunc.getDpusysini(fileContent, dpu_sys_iniFile);
            DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
            params.setSerialNumber(serialNumber);
            params.setVehiecle(vehicle);
            params.setVersion(version);
            params.setLanguage(language);
            params.setFileAbsolutePath(UpdateCenterConstants.VEHICLES_DIR + File.separator + vehicle + File.separator + version);
            params.setUpadteType(3);
            application.setLastUpdateConfig(serialNumber, vehicle, version, language, UpdateCenterConstants.CNLAUNCH_DIR);
            Intent intent = new Intent(DiagSoftConfigureActivity.this, FirmwareUpdate.class);
            intent.putExtra("diagsoft_update_config_params", params);
            MyCarApplication.params = params;

            startActivity(intent);
            finish();
        }
        else
        {
          webservice.removeAllListeners();
          httpDownloader = new HttpDownloadManager(context);
          httpDownloader.addListener(new DiagSoftDownloadListener());
          httpDownloader.downloadDiagSoft(MyCarActivity.cc, serialNumber, softDetialId, UpdateCenterConstants.TEMP_DIR);
        }

        
    }
}
