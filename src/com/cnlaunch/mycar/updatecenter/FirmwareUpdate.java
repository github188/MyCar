package com.cnlaunch.mycar.updatecenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.cnlaunch.mycar.diagnose.formal.DiagnoseSelectVersionActivity;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.connection.ConnectionListener;
import com.cnlaunch.mycar.updatecenter.connection.ConnectionManager;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateListener;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateService;
import com.cnlaunch.mycar.updatecenter.device.DiagConfigUpdateServiceThread;
import com.cnlaunch.mycar.updatecenter.device.ProgressInfo;
import com.cnlaunch.mycar.updatecenter.model.BreakPointInfo;
import com.cnlaunch.mycar.updatecenter.model.UpdateFileInfo;
import com.cnlaunch.mycar.updatecenter.model.UpdateInfo;
import com.cnlaunch.mycar.updatecenter.step.CheckSerialNo;
import com.cnlaunch.mycar.updatecenter.step.ConnectDPU;
import com.cnlaunch.mycar.updatecenter.step.GetBreakPointInfo;
import com.cnlaunch.mycar.updatecenter.step.SendFileContent;
import com.cnlaunch.mycar.updatecenter.step.SendUpdateFileInfo;
import com.cnlaunch.mycar.updatecenter.step.ValidateAllFilesMd5;
import com.cnlaunch.mycar.updatecenter.step.WriteDiagnoseSoftInfo;
import com.cnlaunch.mycar.updatecenter.step.WriteDownloadBin;
import com.cnlaunch.mycar.updatecenter.step.WriteDpusysini;
import com.cnlaunch.mycar.updatecenter.tools.DPUParamTools;
import com.cnlaunch.mycar.updatecenter.tools.FileIntegrityChecker;
import com.cnlaunch.mycar.updatecenter.tools.FileLengthUtil;
import com.cnlaunch.mycar.updatecenter.tools.FileMD5Checker;
import com.cnlaunch.mycar.updatecenter.tools.FileScanner;
import com.cnlaunch.mycar.updatecenter.tools.SDCardChecker;

/**
 * 固件升级 需要传入升级的配置 1.车型 2.版本 3.语言 开始升级文件之前，要做一系列的检查 [顺序很重要!]: 车型配置参数检查 --->
 * SD卡挂载状态检查 ---> 文件目录状态检查 ----> 扫描文件列表 ---> 文件完整性检查 ----> 蓝牙连接检查 ---->升级文件
 * 的MD5检查
 */
public class FirmwareUpdate extends BaseActivity implements ConnectionListener,DeviceUpdateListener,DeviceRequest.OnDeviceTimeoutListener
{
    private boolean D = false;
    private final static String TAG = "FirmwareUpdate";
    private Context context = FirmwareUpdate.this;

    String serialNumber = "";
    String vehiecle = "";
    String version = "";
    String language = "";
    String fileAbsolutePath = "";
    int updateType = 0;

    boolean isFront = false;
    boolean isBackground = false;

    File sdPath = Environment.getExternalStorageDirectory();
    String vehicle_dir = UpdateCenterConstants.VEHICLES_DIR;// 诊断升级文件夹目录

    File zipFile; // 下载诊断压缩文件
    File[] updateFileList;// 升级的文件列表
    FileScanner fileScanner;
    DeviceResponseHandler deviceResponseHandler;

    HashMap<String, String> md5info;
    Connection connection;
    //Connection bluetoothConnection;
    UpdateProgressListener updateServiceProgressListener;
    DeviceUpdateService updateService;
    DiagConfigUpdateServiceThread updateServiceThread;
    OnServiceConnected serviceConnnection;

    final Handler handler = new Handler();

    public static int cellState = 0; //手机来电状态
    //http://wenku.baidu.com/view/fdea3819c5da50e2524d7fc6.html
    
    TextView title;
    TextView detail;
    TextView error_notice;
    TextView time_remaining;
    ProgressBar progress;
    Button ok;
    Button re_update;
    Button cancel;
    Button continue_update;
    Resources resources;
    MyCarApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(D)Log.d(TAG, "FirmwareUpdate Activity onCreate() ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firware_update, R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_firmware_update);
        setCustomeTitleRight("");
        resources = getResources();
        try
        {
            Env.acquireWakeLock(this);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        deviceResponseHandler = new DeviceResponseHandler(context);

        new SDcardChecker();

        Bundle data = this.getIntent().getExtras(); // 获取配置 初始化升级参数
        if (data != null)
        {
            DiagSoftUpdateConfigParams params = (DiagSoftUpdateConfigParams) data.get("diagsoft_update_config_params");
            if (params != null)
            {
                serialNumber = params.getSerialNumber();
                vehiecle = params.getVehiecle();
                version = params.getVersion();
                language = params.getLanguage();
                updateType = params.getUpadteType();
            }
        }

        // 用于测试
        // vehiecle = "TOYOTA";
        // version = "V03.05";
        // language = "CN";
        // fileAbsolutePath = "/mnt/sdcard/mycar";

        new UpdateConfigParamChecker();// 配置参数检查

        String diagSoftDir = UpdateCenterConstants.VEHICLES_DIR + File.separator + vehiecle + File.separator + version + File.separator;

        fileScanner = new FileScanner(this);// 扫描出需要升级的文件
        fileScanner.setDirToScan(new File(diagSoftDir));

        fileScanner.setScanListener(new FileScanListener());
        fileScanner.doScan();

        updateServiceProgressListener = new UpdateProgressListener(false); // 升级过程监听
                                                                           // ,这个是一个内部的观察者

        serviceConnnection = new OnServiceConnected();
        // 启动服务
        startService(new Intent(this, DeviceUpdateService.class));
        // 绑定服务
        bindService(new Intent(this, DeviceUpdateService.class), serviceConnnection, Context.BIND_AUTO_CREATE);
        application = (MyCarApplication) getApplication();
        findView();
    }

   
    

    class OnServiceConnected implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if(D)Log.d(TAG, "设备升级服务已经连接!");
            //updateService = ((DeviceUpdateService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            if(D)Log.d(TAG, "设备升级服务已经断开连接!");
            //updateService = null;
        }

    }

    @Override
    protected void onDestroy()
    {
        isFront = false;
        super.onDestroy();
        try
        {
            Env.releaseWakeLock();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        unbindService(serviceConnnection);
        if(D)Log.d(TAG, "onDestroy() " + System.currentTimeMillis());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(D)Log.d(TAG, "onStart() " + System.currentTimeMillis());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MyCarActivity.updating = true;
        isFront = true;
        if(D)Log.d(TAG, "☆☆☆☆☆isFront by onResume()" + isFront);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (MyCarActivity.incomingFlag)
        {
            this.finish();
        }
        else
        {
            MyCarActivity.updating = false;
        }
        if(D)Log.d(TAG, "☆☆☆☆☆isFront by onPause()" + isFront);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (MyCarActivity.incomingFlag)
        {
            this.finish();
        }
        else
        {
            MyCarActivity.updating = false;
        }
        if(D)Log.d(TAG, "onRestart() " + System.currentTimeMillis());
    }
//    if (state == TelephonyManager.CALL_STATE_RINGING)
//    // 结束电话，空闲状态0 自己主动挂断或对方挂断都是0
//    else if (state == TelephonyManager.CALL_STATE_IDLE)
//    // 通话中 忙音状态2
//    else if (state == TelephonyManager.CALL_STATE_OFFHOOK)
    @Override
    protected void onStop()
    {
        super.onStop();
        if (connection != null && cellState == TelephonyManager.CALL_STATE_IDLE)
        {
            if(D)Log.d(TAG, "connection is unRegister...");
            connection.removeConnectListener(this);
        }
        if (MyCarActivity.incomingFlag)
        {
            this.finish();
        }
        else
        {
            MyCarActivity.updating = false;
        }
        isFront = false;
        if(D)Log.d(TAG, "☆☆☆☆☆isFront onStop()" + isFront);
        // unbindService(serviceConnnection);
    }

    // 文件扫描监听
    class FileScanListener implements FileScanner.Listener
    {
        @Override
        public void onScanStart(File dir)
        {
        }

        @Override
        public void onScanning(File dir)
        {
        }

        @Override
        public void onScanFinished(final File dir, final File[] result)
        {
            if (result != null)
            {
                updateFileList = result;
                for (int i = 0; i < result.length; i++)
                {
                    if (D)
                        Log.d(TAG, "升级文件:" + result[i].getName());
                }
                // 文件的完整性检查,确保没有遗漏
                FileIntegrityChecker integCheck = new FileIntegrityChecker(result, new FileIntegrityChecker.Listener()
                {
                    @Override
                    public void onCheck(final boolean isComplete, final Object reason)
                    {
                        if (isComplete)// 文件完整
                        {
                            new FileMD5Checker(updateFileList, new MD5CalculateListener());
                            if (D)
                                Log.d("FileIntegrityChecker:", "升级文件是完整的!");
                        }
                        else
                        {
                            if (D)
                                Log.d("FileIntegrityChecker:", "升级文件不完整!");
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    setErrorNotice(reason.toString());
                                    
                                }
                            });
                        }
                    }
                });
                integCheck.doCheck();
            }
            else
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setErrorNotice(resources.getString(R.string.upc_no_files_found));
                       
                    }
                });
            }
        }

        @Override
        public void onScanFailed(final int err, final Object reason)
        {
            if (D)
                Log.e(TAG, reason.toString());
            // 弹出提示菜单,退出
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    final CustomAlertDialog dlg = new CustomAlertDialog(FirmwareUpdate.this);
                    dlg.setTitle(getString(R.string.upc_error));

                    String msg = "扫描文件目录失败!";
                    if (err == FileScanner.Error.FILE_NOT_FOUND)
                    {
                        msg = getString(R.string.upc_no_files_found);
                    }
                    
                    setErrorNotice(msg);
                }
            });
        }
    }

    class SDcardChecker extends Thread
    {
        public SDcardChecker()
        {
            this.start();
        }

        public void run()
        {
            if (!SDCardChecker.isSDCardMounted())// 未挂载SDcard
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setErrorNotice(resources.getString(R.string.upc_sdcard_unmounted_error));
                      
                    }
                });
            }
            else
            {
                File vehiecleDir = new File(UpdateCenterConstants.VEHICLES_DIR);
                if (!vehiecleDir.exists())
                {
                    vehiecleDir.mkdirs();
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            
                            setErrorNotice(resources.getString(R.string.upc_no_files_found));
                        }
                    });
                }
            }
        }
    }

    /**
     * 升级参数检查
     * @author luxingsong
     */
    class UpdateConfigParamChecker extends Thread
    {
        public UpdateConfigParamChecker()
        {
            this.start();
        }

        public void run()
        {
            if (TextUtils.isEmpty(vehiecle) || TextUtils.isEmpty(version))
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setErrorNotice(resources.getString(R.string.upc_errors_in_update_params));
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // md5校验监听
    class MD5CalculateListener implements FileMD5Checker.Listener
    {
        ProgressDialog dlg;

        @Override
        public void onValidateMD5Info()
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (dlg == null)
                    {
                        dlg = new ProgressDialog(context);
                        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    }
                    if (isFront)
                        dlg.show();
                }
            });
        }

        @Override
        public void onMD5CalculateException(final File file, final Object reason)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    if (dlg != null)
                    {
                        dlg.dismiss();
                    }
                    Toast.makeText(context, R.string.fmd5_calc_error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onMD5Calculating(final File file, final int percent, final int restMinutes, final int restSeconds)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    if (dlg != null)
                    {
                        dlg.setMessage(getString(R.string.fmd5_calculating));
                    }
                }
            });
        }

        @Override
        public void onMD5CalculationCompleted(final File[] files, HashMap<String, String> result)
        {
            md5info = result;
            handler.post(new Runnable()
            { 
                public void run()
                {
                    if (dlg != null)
                    {
                        dlg.dismiss();
                    }
                    if(D)Log.d(TAG, "☆☆☆☆☆onMD5CalculationCompleted" );
                    connection = ConnectionManager.getSingletonConnection(FirmwareUpdate.this);
                    if (!connection.isConnected())
                    {
                        connection.addConnectListener(FirmwareUpdate.this);
                        connection.openConnection("bt");
                    }
                    else
                    {
                        connection.removeConnectListener(FirmwareUpdate.this);
                        connection.addConnectListener(FirmwareUpdate.this);
                        startUpdate();
                    }
                }
            });
        }


        @Override
        public void onGeneratingMD5Record(File file)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    if (dlg != null)
                    {
                        dlg.setTitle(R.string.fmd5_create_cookie);
                    }
                }
            });
        }

    }
    private void startUpdate()
    {
        new UpdateThread().start();
//        switch(updateType)
//        {
//            case 0:
//                new UpdateSysIniThread().start();
//                break;
//            case 1: 
//                updateServiceThread = new DiagConfigUpdateServiceThread(FirmwareUpdate.this, deviceResponseHandler, connection, UpdateCenterConstants.DBSCAR_DIR, new String[] { vehiecle, version,
//                    language }, updateFileList, md5info, serialNumber);
//                updateServiceThread.setUpdateListener(FirmwareUpdate.this);
//                updateServiceThread.start(); 
//                break;
//            case 2:
//                new UpdateThread().start();
//                break;
//            default:
//                    break;
//        }
    }
    // 升级过程监听
    class UpdateProgressListener implements DeviceUpdateListener
    {
        boolean isRebindService = false;
        CustomProgressDialog updateProgressDlg;

        public UpdateProgressListener(boolean bind)
        {
            isRebindService = bind;
        }

        @Override
        public void onDeviceUpdateStart()// 开始升级
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    createDialog();
                }
            });
        }

        private void createDialog()
        {
            if (updateProgressDlg == null)
            {
                updateProgressDlg = new CustomProgressDialog(context);
            }
            updateProgressDlg.setTitle(getString(R.string.upc_prepare_to_update));

            updateProgressDlg.setOnKeyListener(new OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) && event.getAction() == KeyEvent.ACTION_UP)// 按返回键的时候提示用户是否退出
                    {
                        final CustomAlertDialog dlg = new CustomAlertDialog(FirmwareUpdate.this);
                        dlg.setTitle(getString(R.string.upc_tips));
                        dlg.setCancelable(false);
                        dlg.setMessage(R.string.upc_exit_confirm);
                        dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (updateProgressDlg != null && updateProgressDlg.isShowing())
                                {
                                    updateProgressDlg.dismiss();
                                    //updateService.forceStop();
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
                            dlg.show();
                    }
                    return false;
                }
            });
            updateProgressDlg.setTitle(getString(R.string.upc_start_update));
            updateProgressDlg.setCancelable(false);
            if(D)Log.d(TAG, "isFront " + isFront);
            if (isFront)
            {
                updateProgressDlg.show();
            }
        }

        @Override
        public void onUpdateProgress(final int action, final ProgressInfo progress)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    if (updateProgressDlg == null && isRebindService)
                    {
                        createDialog();
                    }

                    if (updateProgressDlg != null)
                    {
                        String message = "Loading";
                        if (action == ActionEvent.ACTION_CODE_DATA_TRANSFERING)
                        {
                            message = getString(R.string.upc_update_file_progress);
                        }
                        updateProgressDlg.setTitle(message + "(" + progress.getCurrent() + "/" + progress.getFileSum() + ")...");
                        updateProgressDlg.setProgress(progress.getPercent());
                        updateProgressDlg.setProgressDetail(String.valueOf(progress.getSentBytes() / 1024) + "/" + String.valueOf(progress.getTotalBytes() / 1024) + "KB");
                        final String SECONDS = getString(R.string.upc_seconds);
                        final String MINUTES = getString(R.string.upc_minutes);
                        // final String HOURS = getString(R.string.upc_hours);
                        final String TIME_LEFT_BEFORE_FINISH = getString(R.string.upc_time_left_before_finish);

                        if (progress.getLeftMinites() == 0)
                        {
                            updateProgressDlg.setMessage(TIME_LEFT_BEFORE_FINISH + ": " + progress.getLeftSeconds() + " " + SECONDS);
                        }
                        else if (progress.getLeftHours() == 0)
                        {
                            updateProgressDlg.setMessage(TIME_LEFT_BEFORE_FINISH + ": " + progress.getLeftMinites() + " " + MINUTES + " " + progress.getLeftSeconds() + " " + SECONDS);
                        }
                    }
                }
            });

        }

        @Override
        public void onDeviceUpdateFinish(final String message)
        {
            // 移除所有的观察者
            connection.removeConnectListener(FirmwareUpdate.this);

            handler.post(new Runnable()
            {
                public void run()
                {
                    if (updateProgressDlg != null)
                    {
                        updateProgressDlg.setTitle(getString(R.string.upc_update_finished));
                        updateProgressDlg.setMessage(getString(R.string.upc_update_finish_confirm));
                        updateProgressDlg.setPositiveButtonEnabled(true);
                        updateProgressDlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                updateProgressDlg.dismiss();
                                Intent intent = new Intent(FirmwareUpdate.this, DiagnoseSelectVersionActivity.class);
                                intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE, vehiecle); // 车型
                                intent.putExtra(Constants.DBSCAR_CURRENT_VERSION, version); // 版本
                                startActivity(intent);
                                finish();
                            }
                        });
                        updateProgressDlg.setNegativeButtonEnabled(true);
                        updateProgressDlg.setNegativeButton(R.string.upc_later, new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                updateProgressDlg.dismiss();
                                finish();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onDeviceUpdateException(final int error, final Object detail)
        {
            try
            {
                Env.acquireWakeLock(FirmwareUpdate.this);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connection.removeConnectListener(FirmwareUpdate.this);

            handler.post(new Runnable()
            {
                public void run()
                {
                    String msg = "error in update device !";
                    if (error == ActionEvent.ERROR_DEVICE_NO_REPLY)
                    {
                        msg = getString(R.string.upc_device_response_timeout);
                    }
                    else if (error == ActionEvent.ERROR_UPDATE_SERIALS_NOT_SEEM)
                    {
                        msg = getString(R.string.upc_device_sn_not_match);
                    }

                    if (updateProgressDlg == null)
                    {
                        updateProgressDlg = new CustomProgressDialog(context);
                    }
                    if (updateProgressDlg != null)
                    {
                        updateProgressDlg.setTitle(getString(R.string.upc_error));
                        updateProgressDlg.setMessage(msg);

                        updateProgressDlg.setPositiveButtonEnabled(true);
                        updateProgressDlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                updateProgressDlg.cancel();
                                finish();
                            }
                        });
                        if (isFront)
                        {
                            updateProgressDlg.show();
                        }
                    }
                }
            });

        }

        @Override
        public void onDeviceUpdateMessages(final int action, final String detail)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    String message = "update is in progress";
                    if (action == ActionEvent.ACTION_CODE_CONNECT_DEVICE)
                    {
                        message = getString(R.string.upc_connecting_device) + "...";
                    }

                    if (action == ActionEvent.ACTION_CODE_CALC_FILE_MD5)
                    {
                        message = getString(R.string.fmd5_calculating);
                    }

                    if (action == ActionEvent.ACTION_CODE_PREPARE_FILE_INFO)
                    {
                        message = getString(R.string.upc_prepare_to_update);
                    }

                    if (updateProgressDlg != null)
                    {
                        updateProgressDlg.setTitle(message);
                    }
                }
            });
        }

        public void dismissDiaglogs()
        {
            if (updateProgressDlg != null)
            {
                updateProgressDlg.dismiss();
                updateProgressDlg = null;
            }
        }
    }

    @Override
    public void onConnectionStart(String aAddr, Object aExtra)
    {

    }

    @Override
    public void onConnecting(String aAddr, String aName)
    {

    }

    @Override
    public void onConnectionEstablished(String aAddr, String aName)
    {
 
       // new FileMD5Checker(updateFileList, new MD5CalculateListener());
        startUpdate();
//        ut.start();
    }

    @Override
    public void onConnectionLost(String aAddr, String aName)
    {

    }

    @Override
    public void onResponse(final byte[] data, Object aExtra)
    {
        deviceResponseHandler.handleResponse(data);
    }

    @Override
    public void onTimeout()
    {
    }

    @Override
    public void onConnectionCancel()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
            }
        });
    }

    @Override
    public void onDeviceUpdateStart()
    {
        handler.post(new Runnable()
        {
            
            @Override
            public void run()
            {
                setUpdateProgressMsg(getString(R.string.upc_prepare_to_update));
            }
        });
       
    }

    @Override
    public void onDeviceUpdateMessages(final int action, String details)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                String message = "update is in progress";
                if (action == ActionEvent.ACTION_CODE_CONNECT_DEVICE)
                {
                    message = getString(R.string.upc_connecting_device) + "...";
                }

                if (action == ActionEvent.ACTION_CODE_CALC_FILE_MD5)
                {
                    message = getString(R.string.fmd5_calculating);
                }

                if (action == ActionEvent.ACTION_CODE_PREPARE_FILE_INFO)
                {
                    message = getString(R.string.upc_prepare_to_update);
                }
                detail.setText(message);
            }
        });
        
    }

    @Override
    public void onUpdateProgress(int action1, ProgressInfo progressInfo1)
    {
        final int action = action1;
        final ProgressInfo progressInfo = progressInfo1;
        handler.post(new Runnable()
        {
            
            @Override
            public void run()
            {
                String message = "Loading";
                if (action == ActionEvent.ACTION_CODE_DATA_TRANSFERING)
                {
                    message = getString(R.string.upc_update_file_progress);
                }
                title.setText(message + "(" + progressInfo.getCurrent() + "/" + progressInfo.getFileSum() + ")...");
                progress.setProgress(progressInfo.getPercent());
                detail.setText(String.valueOf(progressInfo.getSentBytes() / 1024) + "/" + String.valueOf(progressInfo.getTotalBytes() / 1024) + "KB");
                final String SECONDS = getString(R.string.upc_seconds);
                final String MINUTES = getString(R.string.upc_minutes);
                if (progressInfo.getLeftMinites() == 0)
                {
                    time_remaining.setText(getString(R.string.upc_time_left_before_finish) + ": " + progressInfo.getLeftSeconds() + " " + SECONDS);
                }
                else if (progressInfo.getLeftHours() == 0)
                {
                    time_remaining.setText(getString(R.string.upc_time_left_before_finish) + ": " + progressInfo.getLeftMinites() + " " + MINUTES + " " + progressInfo.getLeftSeconds() + " " + SECONDS);
                }
                
            }
        });
       
    }

    @Override
    public void onDeviceUpdateFinish(String message)
    {
        // 移除所有的观察者
        connection.removeConnectListener(FirmwareUpdate.this);
        
        handler.post(new Runnable()
        {
            public void run()
            {
                final CustomDialog updateProgressDlg = new CustomDialog(context);
                if (updateProgressDlg != null)
                {
                    updateProgressDlg.setTitle(getString(R.string.upc_update_finished));
                    updateProgressDlg.setMessage(getString(R.string.upc_update_finish_confirm));
                    updateProgressDlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            updateProgressDlg.dismiss();
                            Intent intent = new Intent(FirmwareUpdate.this, DiagnoseSelectVersionActivity.class);
                            intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE, vehiecle); // 车型
                            intent.putExtra(Constants.DBSCAR_CURRENT_VERSION, version); // 版本
                            startActivity(intent);
                            finish();
                        }
                    });
                    updateProgressDlg.setNegativeButton(R.string.upc_later, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            updateProgressDlg.dismiss();
                            finish();
                        }
                    });
                    updateProgressDlg.show();
                }
            }
        });
        
    }

    @Override
    public void onDeviceUpdateException(final int error, Object detail)
    {
        try
        {
            Env.acquireWakeLock(FirmwareUpdate.this);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        connection.removeConnectListener(FirmwareUpdate.this);

        handler.post(new Runnable()
        {
            public void run()
            {
                String msg = "error in update device !";
                if (error == ActionEvent.ERROR_DEVICE_NO_REPLY)
                {
                    msg = getString(R.string.upc_device_response_timeout);
                }
                else if (error == ActionEvent.ERROR_UPDATE_SERIALS_NOT_SEEM)
                {
                    msg = getString(R.string.upc_device_sn_not_match);
                }
                setErrorNotice(msg);
               
            }
        });
        
    }
    /**
     * 需要的命令请求
     * */
    HashMap<String,Object> Queue = new HashMap<String, Object>();
    
    ConnectDPU connectDPU;        // 请求连接
    CheckSerialNo checkSerialNo ;     // 验证序列号
    GetBreakPointInfo getBreakPointInfo; // 获得断点信息
    WriteDiagnoseSoftInfo writeDiagnoseSoftInfo; // 写需要升级的诊断软件信息
    SendFileContent sendFileContent ;  // 发送升级文件
    ValidateAllFilesMd5 validateAllFilesMd5; //校验MD信息
    WriteDpusysini writeDpusysini; // 写系统INI文件
    WriteDownloadBin writeDownloadBin; // 写Downloadbin
    public void initRequest()
    {
        connectDPU = new ConnectDPU(context, connection, FirmwareUpdate.this, deviceResponseHandler,Queue);
        checkSerialNo = new CheckSerialNo(context, serialNumber, connection, FirmwareUpdate.this,deviceResponseHandler,Queue);
        getBreakPointInfo = new GetBreakPointInfo(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue);
        ArrayList<File> fileArray = new ArrayList<File>();
        for(int i=0;i < updateFileList.length;i++)
        {
            fileArray.add(updateFileList[i]);
        }
        UpdateInfo updateInfo = new UpdateInfo(fileArray, vehiecle, version, language,UpdateCenterConstants.DBSCAR_DIR);
        writeDiagnoseSoftInfo = new WriteDiagnoseSoftInfo(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, updateInfo);
        validateAllFilesMd5 = new ValidateAllFilesMd5(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, md5info);
        File fileSYSINI = new File(UpdateCenterConstants.DBSCAR_DIR,"/vehicles"+"/"+vehiecle+"/"+version+"/"+"dpusys.ini");
        writeDpusysini = new WriteDpusysini(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, fileSYSINI);
     
    }
    private boolean isBreakPointGoOn = false; // 是否断点续传
    /**
     * 升级线程
     * <功能简述>
     * <功能详细描述>
     * @author xiangyuanmao
     * @version 1.0 2012-12-18
     * @since DBS V100
     */
    class UpdateThread extends Thread
    {
        public UpdateThread()
        {
            initRequest();
        }
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            super.run();
          
            // 第一步：发出连接请求
            connectDPU.execute();
            // 第二步：校验序列号是否一致
            checkSerialNo.execute();
            // 第三步：检查是否需要升级Download.bin
            if (updateType == 1 || application.isNeedUpdateDownloadBin())
            {
                WriteDownloadBin writeDownloadBin = new WriteDownloadBin(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue);
                if(writeDownloadBin.execute())
                {
                    application.setIsNeedUpdateDownloadBin(false);
                }
                else
                {
                    return;
                }
            }
          
            // 查看是否升级配置文件
            if (updateType == 3)
            {
                writeDpusysini.execute(); // 写系统INI文件
                return;
                
            }
            // 查看断点信息,如果是来电中断，需要读取 断点信息，进行续传，其他情况直接升级
            BreakPointInfo breakPointInfo  = null;
            boolean isForceUpdate = false;
            if (updateType == 0)
            {
                return;
            } 
            else if(updateType == 2) 
            {
                breakPointInfo = getBreakPointInfo.execute();
                if(D)Log.d(TAG, "断点信息：" + breakPointInfo.toString());
            }
            // 断点续传
            if (breakPointInfo != null) 
            {
                isBreakPointGoOn = !breakPointInfo.isUpdated && breakPointInfo.receivedByteCount > 0;
            }
            else // 重新升级
            {
                isForceUpdate = true;
                breakPointInfo = new BreakPointInfo(); 
            }
           
            
            Map<String, Boolean> updatedFilesName = new HashMap<String, Boolean>();
            ArrayList<String> updatedFileNameArray = breakPointInfo.fileArray;
            ArrayList<File> allUpdateFiles = new ArrayList<File>(); // 所有需要升级的文件列表
            ArrayList<File> updatedFiles = new ArrayList<File>();   // 已经升级完成的文件列表
            Map<String, File> allFileMap = new HashMap<String, File>();
            long receivedByteCount = 0;// 已经升级完成的字节数
            long totalBytes = 0;// 总字节数
            for (File file : updateFileList)
            {
                allFileMap.put(file.getName().trim().toUpperCase(), file);
                allUpdateFiles.add(file);
            }
            
            totalBytes = FileLengthUtil.calcTotalBytesInFileList(allUpdateFiles); // 总字节数
            
            
            if (updatedFileNameArray != null && updatedFileNameArray.size() > 0)
            {
                for (String string : updatedFileNameArray)
                {
                    updatedFilesName.put(string, true);
                    updatedFiles.add(allFileMap.get(string));
                    receivedByteCount += allFileMap.get(string.trim()).length();
                }
                
            }
            receivedByteCount += breakPointInfo.receivedByteCount; // 已经升级完成的字节数
            long breakPointPos = breakPointInfo.receivedByteCount;
            
            if(D)Log.d(TAG, isForceUpdate ? "强制升级" : "非强制升级！");
            byte[] updateFileInfoBytes = DPUParamTools.getUpdateInfoBytes(vehiecle, version, language, allUpdateFiles.size(), totalBytes, isForceUpdate, updatedFiles.size(), updatedFileNameArray);
            // 准备升级，发送升级文件的整体信息
            SendUpdateFileInfo sendUpdateFileInfo = new SendUpdateFileInfo(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, updateFileInfoBytes);
            if(!sendUpdateFileInfo.execute())
            {
                return ;
            }
            boolean isSuccess = false;
            if(D)Log.d(TAG, "breakPointInfo.completeFileCount" + breakPointInfo.completeFileCount);
            if(D)Log.d(TAG, "updateFileList.length" + updateFileList.length);
            for(int i = 0; i < updateFileList.length; i++)
            {
                File file = updateFileList[i];
                String fileName = file.getName().toUpperCase().trim();
                if (updatedFilesName.get(fileName) != null && updatedFilesName.get(fileName))
                {
                    continue;
                }
                if (breakPointInfo.updatingFileName != null && !breakPointInfo.updatingFileName.equals(fileName))
                {
                    continue;
                }
                if(D)Log.d(TAG, "开始发送文件" + fileName);
                UpdateFileInfo updateFileInfo = new UpdateFileInfo(file, 
                    totalBytes, 
                    receivedByteCount,  
                    isBreakPointGoOn, 
                    breakPointPos, 
                    allUpdateFiles.size(), 
                    i + 1);
                sendFileContent = new SendFileContent(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, updateFileInfo, md5info);
                isSuccess = sendFileContent.execute();
                if(D)Log.d(TAG, "发送文件" + fileName + isSuccess);
                if (isSuccess)
                {
                    isBreakPointGoOn = false; // 不是断点续传
                    updatedFiles.add(file);
                    receivedByteCount += file.length(); // 已经升级完成的字节数
                    breakPointPos = 0;
                    breakPointInfo.updatingFileName = null;
                    updatedFilesName.put(fileName, true);
                }
                else
                {
                    break;
                }
            }
            if (isSuccess)
            {
                validateAllFilesMd5.execute(); //校验MD信息
                writeDpusysini.execute(); // 写系统INI文件
                
            }
            else
            {
                deviceResponseHandler.removeAllListener();
                return;
            }
        }
    }
    class UpdateSysIniThread extends Thread
    {
        @Override
        public void run()
        {
            if(D)Log.d(TAG, "UpdateSysIniThread start..." );
            super.run();
            File dpu_sys_iniFile = new File(UpdateCenterConstants.VEHICLES_DIR, File.separator + vehiecle + File.separator + version + File.separator + "dpusys.ini");
            new WriteDpusysini(context, connection, FirmwareUpdate.this, deviceResponseHandler, Queue, dpu_sys_iniFile).execute();
            if(D)Log.d(TAG, "UpdateSysIniThread end..." );
        }
    }
   
    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
    
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,deviceRequest);
    }
    public void notifyError(int err,DeviceRequest rq)
    {
        onDeviceUpdateException(err, rq);
    }
    private void setErrorNotice(String message)
    {
        if (error_notice != null && error_notice.getVisibility() == View.INVISIBLE)
        {
            error_notice.setVisibility(View.VISIBLE);
            error_notice.setText(message);
        }
    }
    private void setUpdateProgressMsg(String message)
    {
        
        detail.setText(message);
    }
    private void findView()
    {

        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.update_detail);
        time_remaining = (TextView) findViewById(R.id.time_remaining);
        error_notice = (TextView) findViewById(R.id.error_notice);
        progress = (ProgressBar) findViewById(R.id.progress);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (connection != null)
                {
                    connection.removeConnectListener(FirmwareUpdate.this);
                }
                FirmwareUpdate.this.finish();
                
            }
        });
    }
}


