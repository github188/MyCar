package com.cnlaunch.mycar;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cnlaunch.mycar.gps.MapDisplayActivity;
import com.cnlaunch.mycar.im.ImLoginActivity;
import com.cnlaunch.mycar.manager.BillingAddActivity;
import com.cnlaunch.mycar.manager.OilDetailActivity;
import com.cnlaunch.mycar.manager.net.SyncOilJob;
import com.cnlaunch.mycar.manager.net.SyncUserCarJob;
import com.cnlaunch.mycar.obd2.DataFlowMain;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.UsercenterActivity;

public class MainMenuMoreActivity extends Activity
{
    GridView gvSquared;
    RelativeLayout main_menu_more;
    ImageView main_menu_iv_more;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // ȥ�����⣬ȫ����ʾ��������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_menu_more);
        gvSquared = (GridView) findViewById(R.id.main_gv_nine_grid);
        main_menu_more = (RelativeLayout) findViewById(R.id.main_menu_more);
        main_menu_more.setBackgroundResource(R.drawable.manager_toolbar_bg_pressed);
        main_menu_iv_more = (ImageView) findViewById(R.id.main_menu_iv_more);
        main_menu_iv_more.setImageResource(R.drawable.main_menu_more_down);
        // ���þŹ�������
        SimpleAdapter adpter = new SimpleAdapter(this, getNineGrid(), R.layout.main_gv_item, new String[] { "itemImage", "itemText" }, new int[] { R.id.main_iv_item, R.id.main_tv_item });

        gvSquared.setAdapter(adpter);
        gvSquared.setOnItemClickListener(new GridViewOnClickListener());
    }

    /**
     * ��װ�Ź���ͼƬ����
     * @author xiangyuanmao
     * @return
     */
    private ArrayList<HashMap<String, Object>> getNineGrid()
    {
        ArrayList<HashMap<String, Object>> nineGridList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> myReportMap = new HashMap<String, Object>(); // �ҵı���
        HashMap<String, Object> gasolineAnalysisMap = new HashMap<String, Object>(); // ���ͼ�¼
        HashMap<String, Object> carRescueMap = new HashMap<String, Object>(); // ������Ԯ
        HashMap<String, Object> trackMap = new HashMap<String, Object>(); // �㼣
        HashMap<String, Object> usercenterMap = new HashMap<String, Object>(); // �û�����
        HashMap<String, Object> dbsCarMap = new HashMap<String, Object>(); // ������
        HashMap<String, Object> deviceManageMap = new HashMap<String, Object>(); // ��������
        HashMap<String, Object> settingMap = new HashMap<String, Object>(); // ����
        HashMap<String, Object> aboutMap = new HashMap<String, Object>(); // ����

        myReportMap.put("itemImage", R.drawable.main_more_my_report);
        myReportMap.put("itemText", getResources().getText(R.string.main_my_report).toString());
        gasolineAnalysisMap.put("itemImage", R.drawable.main_more_oil_record);
        gasolineAnalysisMap.put("itemText", getResources().getText(R.string.main_more_oil_record).toString());
        carRescueMap.put("itemImage", R.drawable.main_more_rescue_vehicle);
        carRescueMap.put("itemText", getResources().getText(R.string.carrescue).toString());
        trackMap.put("itemImage", R.drawable.main_more_footprint);
        trackMap.put("itemText", getResources().getText(R.string.main_track).toString());
        usercenterMap.put("itemImage", R.drawable.main_more_updatecenter);
        usercenterMap.put("itemText", getResources().getText(R.string.device).toString());
        dbsCarMap.put("itemImage", R.drawable.main_more_dbscar);
        dbsCarMap.put("itemText", getResources().getText(R.string.app_name).toString());
        deviceManageMap.put("itemImage", R.drawable.main_more_updatecenter);
        deviceManageMap.put("itemText", getResources().getText(R.string.updatecenter).toString());
        settingMap.put("itemImage", R.drawable.main_more_setting);
        settingMap.put("itemText", getResources().getText(R.string.manager_setting).toString());
        aboutMap.put("itemImage", R.drawable.main_more_about);
        aboutMap.put("itemText", getResources().getText(R.string.main_more_about).toString());
        //nineGridList.add(myReportMap);
        //nineGridList.add(carRescueMap);
        nineGridList.add(trackMap);
        nineGridList.add(usercenterMap);
        nineGridList.add(dbsCarMap);
        //nineGridList.add(deviceManageMap);
        nineGridList.add(gasolineAnalysisMap);
        nineGridList.add(settingMap);
        nineGridList.add(aboutMap);
        return nineGridList;
    }

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
//                    new SyncUserCarJob(MainMenuMoreActivity.this, new Handler()
//                    {}).doSync();
//                    new SyncOilJob(MainMenuMoreActivity.this, new Handler()
//                    {
//                        @Override
//                        public void handleMessage(Message msg)
//                        {
//                            // TODO Auto-generated method stub
//                            super.handleMessage(msg);
//                            Log.d("���������������", "SyncOilJob success!");
//                            startActivity(new Intent(MainMenuMoreActivity.this, OilDetailActivity.class));
//                        }
//                    }).doSync();
//
//                }
//                else
//                {
//                    startActivity(new Intent(MainMenuMoreActivity.this, LoginActivity.class));
//                }
                //intent = new Intent(MainMenuMoreActivity.this, OilDetailActivity.class);
                startActivity(new Intent(MainMenuMoreActivity.this, DataFlowMain.class));
                break;
            case R.id.main_menu_my_bill:

                Intent intentBill;
                if (MyCarActivity.isLogin)
                {
                    intentBill = new Intent(MainMenuMoreActivity.this, BillingAddActivity.class);
                }
                else
                {
                    intentBill = new Intent(MainMenuMoreActivity.this, LoginActivity.class);
                }
                startActivity(intentBill);
                break;
            case R.id.main_menu_more:
                Intent intentMain = new Intent(MainMenuMoreActivity.this, MyCarActivity.class);
                startActivity(intentMain);
                overridePendingTransition(0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * �����Ź���ļ�����
     * @author xiangyuanmao
     */
    class GridViewOnClickListener implements OnItemClickListener
    {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
            Intent intent = null;
            switch (arg2)
            {

//                case 0: // �ҵı���
//                    intent = new Intent(MainMenuMoreActivity.this, DiagnoseSimpleReportActivity.class);
//                    break;
//                case 1: // ������Ԯ
//                    intent = new Intent(MainMenuMoreActivity.this, RescueVehiclesActivity.class);
//                    break;
//                case 2: // �㼣
//                    // ���㼣ǰ�����ж�google apps�Ƿ����
//                    try
//                    {
//                        intent = new Intent(MainMenuMoreActivity.this, MapDisplayActivity.class);
//                    }
//                    catch (NoClassDefFoundError e)
//                    {
//                        Toast.makeText(MainMenuMoreActivity.this, "Google Apps not found!", Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                case 3: // �û�����
//                    intent = new Intent(MainMenuMoreActivity.this, UsercenterActivity.class);
//                    break;
//                case 4: // ������
//                    Uri uri = Uri.parse("http://www.dbscar.com");
//                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    break;
//                case 5: // �豸����
//                    intent = new Intent(MainMenuMoreActivity.this, UpdateCenterMainActivity.class);
//                    break;
//                case 6: // ���ͼ�¼
//                    if (MyCarActivity.isLogin)
//                    {
//                        new SyncUserCarJob(MainMenuMoreActivity.this, new Handler()
//                        {}).doSync();
//                        new SyncOilJob(MainMenuMoreActivity.this, new Handler()
//                        {
//                            @Override
//                            public void handleMessage(Message msg)
//                            {
//                                // TODO Auto-generated method stub
//                                super.handleMessage(msg);
//                                Log.d("���������������", "SyncOilJob success!");
//                                startActivity(new Intent(MainMenuMoreActivity.this, OilDetailActivity.class));
//                            }
//                        }).doSync();
//
//                    }
//                    else
//                    {
//                        intent = new Intent(MainMenuMoreActivity.this, LoginActivity.class);
//                    }
//                    intent = new Intent(MainMenuMoreActivity.this, OilDetailActivity.class);
//
//                    break;
//                case 7: // ����
//                    intent = new Intent(MainMenuMoreActivity.this, SettingActivity.class);
//                    break;
//                case 8: // ����
//                    intent = new Intent(MainMenuMoreActivity.this, About.class);
//                    break;
//                default:
//                    intent = null;
//                    break;
//            }

//                case 0: // ������Ԯ
//                    intent = new Intent(MainMenuMoreActivity.this, RescueVehiclesActivity.class);
//                    break;
                case 0: // �㼣
                    // ���㼣ǰ�����ж�google apps�Ƿ����
                    try
                    {
                        intent = new Intent(MainMenuMoreActivity.this, MapDisplayActivity.class);
                    }
                    catch (NoClassDefFoundError e)
                    {
                        Toast.makeText(MainMenuMoreActivity.this, "Google Apps not found!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 1: // �û�����
                    if (MyCarActivity.isLogin)
                    {
                        intent = new Intent(MainMenuMoreActivity.this, UsercenterActivity.class);
                    }
                    else
                    {
                        intent = new Intent(MainMenuMoreActivity.this, LoginActivity.class);
                    }
                    break;
                case 2: // ������
                    Uri uri = Uri.parse("http://www.dbscar.com");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    break;
                case 3: // ���ͼ�¼
                    if (MyCarActivity.isLogin)
                    {
                        new SyncUserCarJob(MainMenuMoreActivity.this, new Handler()
                        {}).doSync();
                        new SyncOilJob(MainMenuMoreActivity.this, new Handler()
                        {
                            @Override
                            public void handleMessage(Message msg)
                            {
                                // TODO Auto-generated method stub
                                super.handleMessage(msg);
                                Log.d("���������������", "SyncOilJob success!");
                                startActivity(new Intent(MainMenuMoreActivity.this, OilDetailActivity.class));
                            }
                        }).doSync();

                    }
                    else
                    {
                        intent = new Intent(MainMenuMoreActivity.this, LoginActivity.class);
                    }
                    intent = new Intent(MainMenuMoreActivity.this, OilDetailActivity.class);

                    break;
                case 4: // ����
                    intent = new Intent(MainMenuMoreActivity.this, SettingActivity.class);
                    break;
                case 5: // ����
                    //LocalUpdateManager
                    intent = new Intent(MainMenuMoreActivity.this, About.class);
                    break;
                default:
                    intent = null;
                    break;
            }
            if (intent != null)
            {
                startActivity(intent);
            }
        }
    }

    private boolean ensureLogin()
    {
        if (MyCarActivity.isLogin)
        {
            return true;
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        }
    }
}
