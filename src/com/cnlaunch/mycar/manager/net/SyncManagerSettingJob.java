package com.cnlaunch.mycar.manager.net;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.database.ManagerSetting;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * @author xuzhuowei 同步配置
 */
public class SyncManagerSettingJob extends SyncJob {
	private final String TAG = "SyncManagerSettingJob";

	private ManagerSettingBll managerSettingBll;
	private UserDbHelper helper;
	private ManagerSettingBll userSettingBll;
	private HashSet<String> itemSetNeedSync;

	public SyncManagerSettingJob(Context context, Handler mainHandler) {
		super(context, mainHandler);
	}

	@Override
	public void setRequestPara() {
		helper = OpenHelperManager.getHelper(context, UserDbHelper.class);
		userSettingBll = new ManagerSettingBll(context, helper);
		managerSettingBll = new ManagerSettingBll(context, helper);

		String syncDownManagerSettingFinished = userSettingBll
				.find(ManagerSettingNames.syncDownManagerSettingFinished
						.toString());

		if (syncDownManagerSettingFinished != null) {
			if (syncDownManagerSettingFinished.equals("Yes")) {
				addUploadCustomCategoryJob();
			} else {
				addDownCustomCategoryJob();
			}
		}

	}

	private void addDownCustomCategoryJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_MANAGER_SETTING;
		addJob(null, methodName);
	}

	/**
	 * 将本地数据库的“用户自定义记账类别”数据打包，创建上传任务，并加入上传任务队列
	 */
	private void addUploadCustomCategoryJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_UPLOAD_MANAGER_SETTING;
		List<ManagerSetting> list = managerSettingBll
				.getAllManagerSettingForSync();
		if (list != null && list.size() > 0) {
			List<ManagerSettingDTO> dtoList = new ArrayList<ManagerSettingDTO>();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				ManagerSettingDTO managerSettingDTO = new ManagerSettingDTO();
				ManagerSetting managerSetting = list.get(i);
				managerSettingDTO.setKey(managerSetting.getKey());
				managerSettingDTO.setValue(managerSetting.getValue());
				dtoList.add(managerSettingDTO);
			}
			addJob(new TreeMap<String, Object>(), dtoList, methodName);
		}
	}

	@Override
	public void dealResult(Map<String, Object> map, SoapObject result) {
		if (map == null) {
			syncJobResultCode = SYNC_JOB_RESULT_CODE_SUCC;
		} else {

			if (result != null && result.getProperty(0) != null) {
				SoapObject so = (SoapObject) result.getProperty(0);

				int resultCode = new Integer(
						so.getProperty("code") == null ? "-1" : so.getProperty(
								"code").toString()).intValue();

				if (resultCode == 0) {
					String methodName = map.get("methodName").toString();
					if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_UPLOAD_MANAGER_SETTING)) {
						Log.e(TAG, "UPLOAD_MANAGER_SETTING_succ");
					} else if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_MANAGER_SETTING)) {
						saveCategory(so.getProperty("data").toString());
					}
					syncJobResultCode = SYNC_JOB_RESULT_CODE_SUCC;
				} else {
					syncJobResultCode = SYNC_JOB_RESULT_CODE_ERR;
				}
			} else {
				syncJobResultCode = SYNC_JOB_RESULT_CODE_ERR;
			}
		}
	}

	/**
	 * 将下载到的用户配置信息存入数据库
	 * 
	 * @param userCarListStr
	 */
	private void saveCategory(String customCategoryStr) {
		Log.e(TAG, customCategoryStr);
		final int CUSTOM_CATEGORY_FIELD_COUNT = 2;// 下载时，接口中定义的，用户配置信息的字段个数
		String[] managerSettings;
		if (customCategoryStr.contains("$")) {
			managerSettings = customCategoryStr.split("\\$");
		} else {
			managerSettings = new String[] { customCategoryStr };
		}

		if (managerSettings != null && managerSettings.length > 0) {
			int len = managerSettings.length;
			for (int i = 0; i < len; i++) {
				if (managerSettings[i].contains("|")) {
					String[] managerSetting = managerSettings[i].split("\\|");
					if (managerSetting.length == CUSTOM_CATEGORY_FIELD_COUNT) {
						if (isNeedSync(managerSetting[0])) {
							managerSettingBll.update(managerSetting[0],
									managerSetting[1]);
						}
					}
				}
			}
		}

		// 配置下载用户配置信息为 已完成
		userSettingBll.update(
				ManagerSettingNames.syncDownManagerSettingFinished.toString(),
				"Yes");
	}

	private boolean isNeedSync(String item) {
		if (itemSetNeedSync == null) {
			itemSetNeedSync = new HashSet<String>();
			itemSetNeedSync.add(ManagerSettingNames.budget.toString());
			itemSetNeedSync.add(ManagerSettingNames.oilType.toString());
			itemSetNeedSync.add(ManagerSettingNames.oilPrice.toString());
			itemSetNeedSync.add(ManagerSettingNames.lastOilType.toString());

		}
		return itemSetNeedSync.contains(item);

	}

	@Override
	protected void sendMsgToUI() {
		if (syncJobResultCode == 0) {
			sendMsgToUi(SyncJob.SYNC_MSG_USER_SETTING_SUCC);
		} else {
			sendMsgToUi(SyncJob.SYNC_MSG_USER_SETTING_ERR);
		}

	}

}