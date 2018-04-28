package com.cnlaunch.mycar;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;
import com.cnlaunch.mycar.diagnose.formal.DiagnoseSelectVersionActivity;
import com.cnlaunch.mycar.diagnose.simplereport.DiagnoseSimpleReportActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PushSampleReportSummary implements IPushSummary {
   
	@Override
	public void push(String cc, Context context) {
		final DBSCarSummaryInfo dd = new DBSCarSummaryInfo(context);
		final Context mContext = context;
		new Thread() {
			public void run() {

		
				SharedPreferences preExamNum=mContext.getSharedPreferences(DiagnoseConstant.PRE_EXAM_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
				String numStr ="";
				if(preExamNum.contains(DiagnoseConstant.PRE_EXAM_NUM)){
					numStr=preExamNum.getString(DiagnoseConstant.PRE_EXAM_NUM, " ");
				}
				SharedPreferences preDocNum=mContext.getSharedPreferences(DiagnoseConstant.PRE_DOC_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
				Integer total =0;
				if(preDocNum.contains(DiagnoseConstant.PRE_DOC_NUM)){
					total=preDocNum.getInt(DiagnoseConstant.PRE_DOC_NUM, 0);
				}
				//上一次扫描车型版式信息
				SharedPreferences preQuestionInfo=mContext.getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
				String pre_car_type=preQuestionInfo.getString(Constants.DBSCAR_CURRENT_CAR_TYPE, "");
				String currentLanguage=preQuestionInfo.getString(DiagnoseConstant.CURRENT_LANGUAGE, "");			
				if(!Env.GetCurrentLanguage().equals(currentLanguage)){
					numStr="";
				}
				//当前车型版本信息
				SharedPreferences carTypeVer=mContext.getSharedPreferences(DiagnoseConstant.DIAG_CAR_TYPE_VER, Context.MODE_WORLD_WRITEABLE);
				final String vehicleType=carTypeVer.getString(Constants.DBSCAR_CURRENT_CAR_TYPE, "");
				final String version=carTypeVer.getString(Constants.DBSCAR_CURRENT_VERSION, "");
				if(!vehicleType.equals("")){
					if(!pre_car_type.equals(vehicleType)){
						numStr="";
					}
				}
				else{
					numStr="";
				}
				final String isEnterStr=numStr;
				DBSCarSummaryInfo.IDBSCarObserve observe = new DBSCarSummaryInfo.IDBSCarObserve() {
					@Override
					public void execute() {
						Intent intent = null;
						if (!isEnterStr.equals("")) {
							intent = new Intent(mContext,
									DiagnoseSimpleReportActivity.class);
							intent.putExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY,
									DiagnoseConstant.DIAG_SP_PUSH_VALUE);
							intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
									vehicleType); // 车型
							intent.putExtra(Constants.DBSCAR_CURRENT_VERSION,
									version); // 版本
							mContext.startActivity(intent);
						} else {
							intent = new Intent(mContext,
									DiagnoseSelectVersionActivity.class);
							intent.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
									vehicleType); // 车型
							intent.putExtra(Constants.DBSCAR_CURRENT_VERSION,
									version); // 版本
							mContext.startActivity(intent);
						}
					}
				};
				DBSCarInfo dbsCarInfo=null;
				if (!numStr.equals("")) {
					if(!vehicleType.equals("")){
						dbsCarInfo = new DBSCarInfo(
								mContext.getString(R.string.diag_sp_push_str),
								String.valueOf(total),
								mContext.getString(R.string.diag_sp_push_unit));				
						dd.register(PushKeys.KEY_SAMPLEREPORT_PREFIX, dbsCarInfo,
								observe);
					}
					else{
						preExamNum.edit().clear().commit();
						preDocNum.edit().clear().commit();
						preQuestionInfo.edit().clear().commit();
						dd.unRegister(PushKeys.KEY_SAMPLEREPORT_PREFIX);
					}
				}
				else {
					if(!vehicleType.equals("")){
						dbsCarInfo = new DBSCarInfo(
								mContext.getString(R.string.diag_car_not_check),
								mContext.getString(R.string.diag_sp_push_str3),
								mContext.getString(R.string.diag_sp_push_str3));
						dd.register(PushKeys.KEY_SAMPLEREPORT_PREFIX, dbsCarInfo,
								observe);
					}
					else{
						dd.unRegister(PushKeys.KEY_SAMPLEREPORT_PREFIX);
					}
				}

			}

		}.start();
	}
}
