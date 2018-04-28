package com.cnlaunch.mycar.rescuevehicles;

import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**
 * @author zhangweiwei
 * @version 2011-12-14下午1:33:09 类说明
 */
public class AroundStoresDetailInfoActivity extends BaseActivity
{
    private ServiceMerchantDTO serviceMerchantDTO;
    private Resources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rescue_vehicles_store_detailinfo_layout, R.layout.custom_title);
        setCustomeTitleLeft(R.string.rv_stores_information);
        setCustomeTitleRight("");
        serviceMerchantDTO = new ServiceMerchantDTO();
        String serviceIdStr = getIntent().getExtras().getString("serviceId");
        int serviceId = Integer.parseInt(serviceIdStr);
        // 请求详细商户信息
        resources = getResources();
        requestAroundStoresDetailInfo(serviceId);
        diaplayAroundStoresDetailInfo();
    }

    /*
     * 显示数据
     */
    private void diaplayAroundStoresDetailInfo()
    {
        TextView tv_name = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_name);
        TextView tv_creatTime = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_creatTime);
        TextView tv_primaryService = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_primaryService);
        TextView tv_contacter = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_contacter);
        final Button bt_mobile = (Button) findViewById(R.id.bt_rescue_vehicles_store_detailinfo_mobile);
        final Button bt_phone = (Button) findViewById(R.id.bt_rescue_vehicles_store_detailinfo_phone);
        TextView tv_address = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_address);
        TextView tv_gprsX = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_gprsX);
        TextView tv_gprsY = (TextView) findViewById(R.id.tv_rescue_vehicles_store_detailinfo_gprsY);
        tv_name.setText(serviceMerchantDTO.getCompanyName());
        tv_creatTime.setText(serviceMerchantDTO.getCreateTime());
        tv_primaryService.setText(serviceMerchantDTO.getPrimaryService());
        tv_contacter.setText(serviceMerchantDTO.getContacter());
        bt_mobile.setText(serviceMerchantDTO.getMobile());
        bt_phone.setText(serviceMerchantDTO.getPhone());
        tv_address.setText(serviceMerchantDTO.getCompanyAddress());
        tv_gprsX.setText(serviceMerchantDTO.getGprsX());
        tv_gprsY.setText(serviceMerchantDTO.getGprsY());
        bt_mobile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

                if (bt_mobile.getText() != null && bt_mobile.getText().toString().length() > 3)
                {
                    final CustomDialog customDialog = new CustomDialog(AroundStoresDetailInfoActivity.this);
                    customDialog.setTitle(resources.getString(R.string.uc_notice));
                    customDialog.setMessage(resources.getString(R.string.rescue_vehicle_ensure_call) + bt_mobile.getText().toString() + "?");
                    customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok), new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bt_mobile.getText().toString()));
                            startActivity(intent);
                        }
                    });
                    customDialog.setNegativeButton(resources.getString(R.string.bluetoothconnect_input_cancel), new OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {
                            // TODO Auto-generated method stub
                            customDialog.cancel();
                        }
                    });
                    customDialog.show();
                }
            }

        });
        bt_phone.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (bt_phone.getText() != null && bt_phone.getText().toString().length() > 3)
                {
                    final CustomDialog customDialog = new CustomDialog(AroundStoresDetailInfoActivity.this);
                    customDialog.setTitle(resources.getString(R.string.uc_notice));
                    customDialog.setMessage(resources.getString(R.string.rescue_vehicle_ensure_call)+ bt_phone.getText().toString() + "?");
                    customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok), new OnClickListener()
                    {
                        @Override
                        public void onClick(View V)
                        {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bt_phone.getText().toString()));
                            startActivity(intent);
                        }
                    });
                    customDialog.setNegativeButton(resources.getString(R.string.bluetoothconnect_input_cancel), new OnClickListener()
                    {

                        @Override
                        public void onClick(View V)
                        {
                            customDialog.cancel();
                        }
                    });
                    customDialog.show();
                }
            }

        });
    }

    /*
     * 从服务器获取数据
     */
    private void requestAroundStoresDetailInfo(int serviceId)
    {
        // 方法：查询详细商户信息getServiceMerchant (Integer serviceId)
        String methodName = "getServiceMerchant";// 请求的方法名
        String soapAction = null;// 不管是请求什么方法，这里始终为null
        TreeMap paraMap = new TreeMap<String, Integer>();
        paraMap.put("serviceId", serviceId);// 放请求方法需要的请求参数
        boolean isSign = true;
        RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_RESCUE_VEHICLES, methodName, soapAction, paraMap, isSign);
        WebServiceManager webServiceManager = new WebServiceManager(this, requestParameter);
        WSBaseResult wSBaseResult = new WSBaseResult();
        wSBaseResult = webServiceManager.execute();
        // 打印出获取到的结果
        // System.out.println("wSBaseResult.responseCode为"+wSBaseResult.responseCode);
        // System.out.println("object为"+wSBaseResult.object);
        SoapObject object = (SoapObject) wSBaseResult.object;
        SoapObject object2 = (SoapObject) object.getProperty(0);
        String code = object2.getProperty("code").toString();
        SoapObject serviceMerchantDTOSoapObject = (SoapObject) object2.getProperty("serviceMerchantDTO");
        serviceMerchantDTO.setCompanyAddress(serviceMerchantDTOSoapObject.hasProperty("companyAddress") ? serviceMerchantDTOSoapObject.getProperty("companyAddress").toString() : "");
        serviceMerchantDTO.setContacter(serviceMerchantDTOSoapObject.hasProperty("contacter") ? serviceMerchantDTOSoapObject.getProperty("contacter").toString() : "");
        serviceMerchantDTO.setCompanyName(serviceMerchantDTOSoapObject.hasProperty("companyName") ? serviceMerchantDTOSoapObject.getProperty("companyName").toString() : "");
        serviceMerchantDTO.setCreateTime(serviceMerchantDTOSoapObject.hasProperty("createTime") ? serviceMerchantDTOSoapObject.getProperty("createTime").toString() : "");
        serviceMerchantDTO.setGprsX(serviceMerchantDTOSoapObject.hasProperty("gprsX") ? serviceMerchantDTOSoapObject.getProperty("gprsX").toString() : "");
        serviceMerchantDTO.setGprsY(serviceMerchantDTOSoapObject.hasProperty("gprsY") ? serviceMerchantDTOSoapObject.getProperty("gprsY").toString() : "");
        serviceMerchantDTO.setMobile(serviceMerchantDTOSoapObject.hasProperty("mobile") ? serviceMerchantDTOSoapObject.getProperty("mobile").toString() : "");
        serviceMerchantDTO.setPhone(serviceMerchantDTOSoapObject.hasProperty("phone") ? serviceMerchantDTOSoapObject.getProperty("phone").toString() : "");
        serviceMerchantDTO.setPrimaryService(serviceMerchantDTOSoapObject.hasProperty("primaryService") ? serviceMerchantDTOSoapObject.getProperty("primaryService").toString() : "");
    }
}
