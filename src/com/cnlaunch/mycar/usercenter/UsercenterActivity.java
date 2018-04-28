package com.cnlaunch.mycar.usercenter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.updatecenter.Device;
import com.cnlaunch.mycar.updatecenter.DeviceActivateGuideActivity;
import com.cnlaunch.mycar.updatecenter.DiagSoftConfigureActivity;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.usercenter.database.User;
import com.j256.ormlite.dao.Dao;

/**
 * @description
 * @author 向远茂
 * @date：2012-4-16
 */
public class UsercenterActivity extends BaseActivity
{
    // 调试log信息target
    private static final String TAG = "UsercenterActivity";
    private static final boolean D = true;

    Resources resources;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvExUserinfo;
    RelativeLayout rl;
    LinearLayout buyDeviceLinarLayout;
    Button btnBuy;
    Button btnActivate;
    private ListView listView; // 设备信息的列表ListView对象
    private DeviceInfoAdapter adapter; // 设备信息列表的Adapter
    private ArrayList<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String, Object>>(); // 设备信息内容
    Dao<User, Integer> userDao;
    Dao<Device, Integer> deviceDao;
    private MyCarApplication application;
    /**
     * 覆盖了基类的方法
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usercenter_user_device_info, R.layout.custom_title);
        setCustomeTitleLeft(R.string.device);
        setCustomeTitleRight("");
        application = (MyCarApplication) getApplication();
        resources = getResources();
        if (MyCarActivity.isLogin)
        {
            initViews(); // 初始化列表
        }
        else
        {
            Intent intent = new Intent(UsercenterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
       // 
    }
    public void releaseSource() 
    {
        BluetoothDataService m_blue_service = BluetoothDataService.getInstance();
        try
        {
            if (m_blue_service.IsConnected())
            {
               m_blue_service.StopBlueService(this);
                m_blue_service.finalize();
            }
        }
        catch (Throwable e)
        {
            // TODO: handle exception
            if (D)
                Log.e(TAG, e.getMessage());
        }
    }
    @Override
    protected void onRestart()
    {
        initViews(); // 初始化列表
        super.onRestart();
    }

    private void initViews()
    {
        tvUsername = (TextView) findViewById(R.id.tv_value_username);
        tvEmail = (TextView) findViewById(R.id.tv_value_email);
        tvExUserinfo = (TextView) findViewById(R.id.tv_userinfo_ex);
        listView = (ListView) findViewById(R.id.list_device_info);
        buyDeviceLinarLayout = (LinearLayout) findViewById(R.id.usercenter_linelayout04);
        //btnBuy = (Button) findViewById(R.id.btn_buy);
        btnActivate = (Button) findViewById(R.id.btn_activate);
//        btnBuy.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("http://www.dbscar.com");
//                startActivity(new Intent(Intent.ACTION_VIEW, uri));
//                UsercenterActivity.this.finish();
//                
//            }
//        });
        btnActivate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(UsercenterActivity.this, DeviceActivateGuideActivity.class);
                intent.putExtra("update_method", 0);// 在线升级
                startActivity(intent);
                //releaseSource();
                UsercenterActivity.this.finish();
            }
        });
        rl = (RelativeLayout) findViewById(R.id.rl_userinfo);
        rl.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UsercenterActivity.this, ExUserInfoActivity.class);
                startActivity(intent);
            }
        });

        List<User> userList; // 注意，此处的结构是用户的单个属性，而不是整个用户信息
        List<String> list;
        try
        {
            
            userDao = getHelper().getDao(User.class);
            userList = userDao.queryForAll(); // 得到用户信息的所有属性并压入userinfoList数组
            if (userList != null && userList.size() > 0)
            {
                for (User user : userList)
                {
                    if (user.getLabel().equals("用户名") || user.getLabel().equals(resources.getString(R.string.usercenter_username_)))
                    {
                        tvUsername.setText(user.getValue());
                    }
                    if (user.getLabel().equals("邮箱") || user.getLabel().equals(resources.getString(R.string.usercenter_email_)))
                    {
                        tvEmail.setText(user.getValue());
                    }
                }
            }
//            deviceDao = getHelper().getDao(Device.class);
//            Device dev = new Device();
//            dev.setSerialNum("9809912345678900");
//            dev.setSerialPasswd("410033000247303531353232");
//            dev.setDeviceName("DBS_BMW");
//            dev.setStatus("1");
//            dev.setMac("00:18:E4:29:76:81");
//            try
//           {
//               deviceDao.createOrUpdate(dev);
//           }
//           catch (SQLException e1)
//           {
//               // TODO Auto-generated catch block
//               e1.printStackTrace();
//           }
//            list = deviceDao.queryForAll();
//            deviceList.clear();
            
            //list = application.getDevice();
            list = application.getValues(MyCarActivity.cc,Constants.SP_DEVICE_INFO_SERIAL_NUMBER);
            if (list != null && list.size() > 0)
            {
                deviceList.clear();
                for (String device : list)
                {
                    HashMap<String, Object> deviceMap = new HashMap<String, Object>();
                    deviceMap.put(UsercenterConstants.DEVICE_SERIAL, device);
//                    deviceMap.put(UsercenterConstants.DEVICE_NAME_, device.getDeviceName());
//                    deviceMap.put(UsercenterConstants.DEVICE_STATUS, device.getStatus());
//                    deviceMap.put(UsercenterConstants.DEVICE_MAC, device.getMac());
//                    deviceMap.put(UsercenterConstants.DEVICE_CHIP_ID, device.getChipId());
                    deviceList.add(deviceMap);
                }
            }
            else
            {
                buyDeviceLinarLayout.setVisibility(View.VISIBLE);
            }
            // 初始化Adapter
            adapter = new DeviceInfoAdapter(this, deviceList);

            // 设置Adapter到ListView，此时用户信息列表有值，可以显示在屏幕，也可以通过更新adapter刷新屏幕
            listView.setAdapter(adapter);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 设备信息Adapter，主要用于加载设备详细信息
     * @author xiangyuanmao
     */
    class DeviceInfoAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> listItems;

        public DeviceInfoAdapter(Context c, ArrayList<HashMap<String, Object>> list)
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
           // final TextView tvDeviceName; // 标签
            final TextView tvSerialNo; // 内容
            final int item = position;
            ImageView ivEdit;
            if (view == null)
            {
                view = this.inflater.inflate(R.layout.usercenter_device_info, null);
            }
            //tvDeviceName = (TextView) view.findViewById(R.id.tv_value_device_name);
            tvSerialNo = (TextView) view.findViewById(R.id.tv_value_serial_no);
            ivEdit = (ImageView) view.findViewById(R.id.userinfo_edit);
            //final String deviceName = listItems.get(position).get(UsercenterConstants.DEVICE_NAME_).toString();
            final String serialName = listItems.get(position).get(UsercenterConstants.DEVICE_SERIAL).toString();
            //final String chipId = listItems.get(position).get(UsercenterConstants.DEVICE_SERIAL).toString();
            //tvDeviceName.setText(deviceName);
            //tvDeviceName.setVisibility(View.INVISIBLE);
            tvSerialNo.setText(serialName);
            LinearLayout deviceinfo2 = (LinearLayout) view.findViewById(R.id.ll_deviceinfo2);
            deviceinfo2.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(UsercenterActivity.this,DiagSoftConfigureActivity.class);
                    Log.d(TAG,"注册成功 序列号,芯片ID: "+serialName);
                    application.setBluetoothMac(serialName);
                    SerialInfo siInfo = new SerialInfo(serialName,"");
                    intent.putExtra("serialInfo", siInfo);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
