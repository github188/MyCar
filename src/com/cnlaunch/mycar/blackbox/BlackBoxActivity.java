package com.cnlaunch.mycar.blackbox;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueCallback;
import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueStateEvent;
import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.updatecenter.tools.SDCardChecker;


/**
 * 
 * <���ܼ���>
 * <������ϸ����>
 * @author xiangyuanmao
 * @version 1.0 2012-9-6
 * @since DBS V100
 */
public class BlackBoxActivity extends BaseActivity implements BluetoothInterface,BlueCallback
{
    // ----------------------------
    // ����log��Ϣtarget-------------------------------------
    private static final String TAG = "BlackBoxActivity";
    private static final boolean D = true;

    private static final byte[] COMMAND_SWITCH_MODEL = {0x26, 0x01}; // �л�ģʽ��Crecod
    private static final byte[] COMMAND_QUERY_FILE_NAME = {0x26, 0x02, 0x00}; // ��ѯ�ļ������б�
    private static final byte[] COMMAND_READ_FILE = {0x26, 0x03}; // ���ļ�
    private static final byte[] COMMAND_DELETE_SPECIAL_FILE = {0x26, 0x04}; // ɾ���ض��ļ�
    private static final byte[] COMMAND_DELETE_ALL_FILE = {0x26, 0x05}; // ɾ�������ļ�
    
    private Resources resources;
    // ---------------------------- ����ؼ�
    private Button btn_muti_operate; //
    private Button btn_delete;
    private ListView lv_black_box; // 
    private CheckBox cb_select_all;

    private FileNameListAdapter fileNameListAdapter; //
    private List<String> mData = new ArrayList<String>(); // list����ʾ��������Դ
    private BluetoothDataService bluetoothDataService;
    Handler mHandler = new Handler();
    ProgressDialog pdlg; // 
    private boolean isOpenProgress = false; // �Ƿ�򿪽��ȿ�
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        // ͳһ�ı���������
        setContentView(R.layout.black_box, R.layout.custom_title);
        setCustomeTitleLeft(R.string.main_indestructible_black_box);
        setCustomeTitleRight("");
        bluetoothDataService = BluetoothDataService.getInstance();
        bluetoothDataService.AddObserver(this);
        if (!bluetoothDataService.IsConnected())
        {
            bluetoothDataService.ShowBluetoothConnectActivity(this);// �����������ӶԻ���
        }
        // ��ʼ�������ؼ�
        initData();
    }
    @Override
    protected void onStart()
    {

        // TODO Auto-generated method stub
        super.onStart();
    }
    @Override
    protected void onResume()
    {

        // �����л�ģʽ
        //bluetoothDataService.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, COMMAND_SWITCH_MODEL, null, 0, 2000);
        isOpenProgress = true;
        
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        isOpenProgress = false;
      
        super.onPause();
    }
    @Override
    protected void onStop()
    {
        bluetoothDataService.DelObserver(this); 
        // TODO Auto-generated method stub
        super.onStop();
    }
    /**
     * ���SD���Ƿ�װ��
     * @param activity
     * @since DBS V100
     */
    private void checkSDCardIsExist(Activity activity)
    {
        if (!SDCardChecker.isSDCardMounted())
        {
            final CustomAlertDialog dlg = new CustomAlertDialog(activity);
            dlg.setTitle(R.string.upc_tips);
            dlg.setMessage(R.string.upc_sdcard_unmounted_error);
            dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dlg.dismiss();
                }
            });
            dlg.setOnKeyListener(new OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            dlg.show();
            return;
        }
    }

    /**
     * ��ʼ�������ؼ�
     */
    private void initData()
    {
        btn_muti_operate = (Button) findViewById(R.id.btn_muti_operate); //
        btn_delete = (Button) findViewById(R.id.btn_delete);
        lv_black_box = (ListView) findViewById(R.id.lv_black_box); //
        cb_select_all = (CheckBox) findViewById(R.id.cb_select_all);
        btn_muti_operate.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bluetoothDataService.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, COMMAND_QUERY_FILE_NAME, null, 0, 2000);
                
            }
        });
    }

    /**
     * �����Ի���
     * @param message
     */
    protected void dialog(String message)
    {
        final CustomDialog customDialog = new CustomDialog(BlackBoxActivity.this);
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
        customDialog.show();
    }

    /**
     * �Զ����б�����������ʾ�����绰�б�
     * @author xiangyuanmao
     */
    private class FileNameListAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<String> data;// ����Դ

        public FileNameListAdapter(Context context, List<String> data)
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = layoutInflater.inflate(R.layout.black_box_file_list_item, null);
            CheckBox cb_select_file = (CheckBox) convertView.findViewById(R.id.cb_select_file);
            cb_select_file.setText(data.get(position));
            return convertView;
        }
    }

    @Override
    public void BlueConnectLost(String name, String mac)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void BlueConnected(String name, String mac)
    {

        setOrGetMode(new byte[]{0x04});
        
    }

    @Override
    public void GetDataFromService(byte[] databuf, int datalen)
    {
        String v_show = BluetoothDataService.bytesToHexString(databuf, datalen);
        if(D) Log.i(TAG,"�յ���" + v_show); 
        // ȡ������faultCode;
        //09-10 10:10:44.059: I/BlackBoxActivity(20024): �յ���55 AA F8 F0 00 05 00 61 09 04 00 61
        if (datalen < 10)
        {
            return;
        }
        // ģʽ�л��ɹ�
        if((databuf[7]& 0xFF) == 0x61 && (databuf[8]& 0xFF) == 0x09 && (databuf[9]& 0xFF) == 0x04) 
        {
            // ����Creacoder
            bluetoothDataService.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, COMMAND_SWITCH_MODEL, null, 0, 2000);
            
        }
        if((databuf[4]& 0xFF) == 0xC7) 
        {
            stopProgressDialog();
            refreshListView();
        }
        
    }

    @Override
    public void GetDataTimeout()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void BlueConnectClose()
    {
        // TODO Auto-generated method stub
        
    }

    
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
    private void refreshListView()
    {
        mHandler.post(new Runnable()
        {
            
            @Override
            public void run()
            {
                // TODO Auto-generated method stub
            }
        });
    }
    @Override
    public void GetDataFromBlueSocket(byte[] buf, int len)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void GetBluetoothState(BlueStateEvent state)
    {
        // TODO Auto-generated method stub
        
    }
    /**
     * ��������(�õ�)ģʽ����
     * @param modeCommand GetMode=0 SMARTBOX=1 MYCAR=2 CREADER=3 CRECORDER=4
     *        OBD=5
     * @since DBS V100
     */
    public void setOrGetMode(byte[] modeCommand)
    {
        byte[] v_cmd = new byte[] { 0x21, 0x09 };
        byte[] v_sendbuf = modeCommand;
        if(D) Log.i(TAG,"���ͣ�" + BluetoothDataService.bytesToHexString(v_cmd, v_cmd.length));
        if(D) Log.i(TAG,"���ͣ�" + BluetoothDataService.bytesToHexString(modeCommand, modeCommand.length));
        if (bluetoothDataService != null)
            bluetoothDataService.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf, v_sendbuf.length, 1500);
    }
}
