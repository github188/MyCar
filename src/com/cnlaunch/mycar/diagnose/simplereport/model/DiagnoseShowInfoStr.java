package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.io.Serializable;

import android.content.Context;
import android.content.res.Resources;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.diagnose.service.DiagnoseSimpleReportDataService;

public class DiagnoseShowInfoStr implements Serializable {
	
	private Context context;
	public void setContext(Context context) {
		this.context = context;
	}
	// 单例此服务
	private static DiagnoseShowInfoStr showInfoStr = null;

	public synchronized static DiagnoseShowInfoStr getInstance() {
		if (showInfoStr == null)
			showInfoStr = new DiagnoseShowInfoStr();
		return showInfoStr;
	}

	//获取错误ID信息
	public  int GetDiagErrorID(int err_id)
	{
		int v_id = 0;
		switch(err_id)
		{
		case 0:
			v_id = R.string.diag_sp_error_00;
			break;
		case 1:
			v_id = R.string.diag_sp_error_01;
			break;
		case 2:
			v_id = R.string.diag_sp_error_02;
			break;
		case 3:
			v_id = R.string.diag_sp_no_sys_list;
			break;
		case 4:
			v_id = R.string.diag_sp_no_appoint_data_list;
			break;
		case 5:
			v_id = R.string.diag_sp_no_appoint_id_list;
			break;
		case 6:
			v_id = R.string.diag_sp_bluetooth_connection_lost;
			break;
		case 7:
			v_id = R.string.diag_sp_sys_no_support;
			break;
		case 8:
			v_id = R.string.diag_sp_sys_data_empty;
			break;
		default:
			v_id = R.string.diag_error_00;
			break;
		}
		return v_id;
	}
	//获得数据名称未定义字符串
	public String getDataNameNotdefinedStr(){
	   String str = context.getResources().getString(R.string.diag_sp_data_name_not_defined);
	   return str;
	}
	//获得ID未定义字符串
	public String getIDNotdefinedStr(){
	   String str = context.getResources().getString(R.string.diag_sp_id_not_defined);
	   return str;
	}
	//获得未支持字符串
	public String getNoSupport(){
		 String str = context.getResources().getString(R.string.diag_sp_sys_no_support);
		 return str;
	}
	

}
