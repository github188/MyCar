package com.cnlaunch.mycar.manager.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.bll.CategoryBll;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.cnlaunch.mycar.manager.database.Category;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * @author xuzhuowei 同步自定义类别
 */
public class SyncCustomCategoryJob extends SyncJob {
	private final String TAG = "SyncCustomCategoryJob";
	private final static int PAGE_SIZE = 10;
	private CategoryBll categoryBll;
	private UserDbHelper helper;
	private ManagerSettingBll userSettingBll;

	public SyncCustomCategoryJob(Context context, Handler mainHandler) {
		super(context, mainHandler);
	}

	@Override
	public void setRequestPara() {
		helper = OpenHelperManager.getHelper(context, UserDbHelper.class);
		userSettingBll = new ManagerSettingBll(context, helper);
		categoryBll = new CategoryBll(context, helper);
		String syncDownCustomCategoryFinished = userSettingBll
				.find(ManagerSettingNames.syncDownCustomCategoryFinished
						.toString());
		if (syncDownCustomCategoryFinished != null) {
			if (syncDownCustomCategoryFinished.equals("Yes")) {
				addUploadCustomCategoryJob();
			} else {
				addDownCustomCategoryJob();
			}
		}

	}

	private void addDownCustomCategoryJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_BILL_CATEGORY;
		TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
		CategoryCondition categoryCondition = new CategoryCondition();
		paraMap.put("pageNo", 1);
		paraMap.put("pageSize", PAGE_SIZE);
		paraMap.put("condition", categoryCondition);
		addJob(paraMap, methodName);
	}

	/**
	 * 将本地数据库的“用户自定义记账类别”数据打包，创建上传任务，并加入上传任务队列
	 */
	private void addUploadCustomCategoryJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_UPLOAD_BILL_CATEGORY;
		List<Category> list = categoryBll.getUserCustomCategory();
		if (list != null && list.size() > 0) {
			List<CategoryDTO> dtoList = new ArrayList<CategoryDTO>();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				CategoryDTO categoryDTO = new CategoryDTO();
				Category category = list.get(i);
				categoryDTO.setCategory(category.getCategory());
				categoryDTO.setOrderId(category.getOrderId());
				categoryDTO.setType(category.getType());
				categoryDTO.setCategoryId(category.getCategoryId());
				categoryDTO.setCurrentLanguage(category.getCurrentLanguage());
				dtoList.add(categoryDTO);
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
							.equals(Constants.SERVICE_MANAGER_METHOD_UPLOAD_BILL_CATEGORY)) {
						Log.e(TAG, "UPLOAD_BILL_CATEGORY_succ");
					} else if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_BILL_CATEGORY)) {
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
	 * 将下载到的用户自定义类别存入数据库
	 * 
	 * @param userCarListStr
	 */
	private void saveCategory(String customCategoryStr) {
		Log.e(TAG, customCategoryStr);
		final int CUSTOM_CATEGORY_FIELD_COUNT = 5;// 下载时，接口中定义的，自定义类别信息的字段个数
		String[] cars;
		if (customCategoryStr.contains("$")) {
			cars = customCategoryStr.split("\\$");
		} else {
			cars = new String[] { customCategoryStr };
		}

		if (cars != null && cars.length > 0) {
			int len = cars.length;
			for (int i = 0; i < len; i++) {
				if (cars[i].contains("|")) {
					String[] category = cars[i].split("\\|");
					if (category.length == CUSTOM_CATEGORY_FIELD_COUNT) {
						try {
							if (Integer.parseInt(category[1]) == Category.TYPE_USER_CUSTOM) {
								categoryBll.save(new Category(category[0],
										category[1], category[2],category[3],category[4]));
							}
						} catch (Exception e) {
							
						}

					}
				}
			}
		}

		// 配置下载用户自定义类别为 已完成
		userSettingBll.update(
				ManagerSettingNames.syncDownCustomCategoryFinished.toString(),
				"Yes");
	}

	@Override
	protected void sendMsgToUI() {
		switch (syncJobResultCode) {
			case SYNC_JOB_RESULT_CODE_SUCC:
				sendMsgToUi(SyncJob.SYNC_MSG_CUSTOM_CATEGORY_SUCC);
				break;
			case SYNC_JOB_RESULT_CODE_ERR:
				sendMsgToUi(SyncJob.SYNC_MSG_CUSTOM_CATEGORY_ERR);
				break;
			default:
				sendMsgToUi(SyncJob.SYNC_MSG_CUSTOM_NO_DATA_TO_SYNC);
				break;
			}
	}

}