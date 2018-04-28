package com.cnlaunch.mycar.manager.net;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * @author xuzhuowei 同步加油记录
 */
public class SyncOilJob extends SyncJob {
	private final static int PAGE_SIZE = 10;

	private OilBll oilBll;
	private UserDbHelper helper;
	private ManagerSettingBll userSettingBll;
	private List<OilDTO> oilDTOList;

	public SyncOilJob(Context context, Handler mainHandler) {
		super(context, mainHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SyncJob#setRequestPara()
	 */
	@Override
	public void setRequestPara() {
		helper = OpenHelperManager.getHelper(context, UserDbHelper.class);
		userSettingBll = new ManagerSettingBll(context, helper);
		oilBll = new OilBll(context, helper);
		String syncDownOilFinished = userSettingBll
				.find(ManagerSettingNames.syncDownOilFinished.toString());
		if (syncDownOilFinished != null) {
			if (syncDownOilFinished.equals("Yes")) {
				addUploadOilJob();
			} else {
				addDownOilJob();
			}
		}
	}

	/**
	 * 添加任务，去上传加油数据
	 */
	private void addUploadOilJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_UPLOAD_OIL;
		List<Oil> list = oilBll.getAllOilForSync();
		if (list != null && list.size() > 0) {
			oilDTOList = new ArrayList<OilDTO>();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				OilDTO oilDTO = new OilDTO();
				Oil oil = list.get(i);
				oilDTO.setAmount(String.valueOf(oil.getAmount()));
				oilDTO.setExpenseTime(Format.DateStr.getDateTime(oil
						.getExpenseTime()));
				oilDTO.setId(oil.getId());
				oilDTO.setLastOperate(oil.getLastOperate());
				oilDTO.setRemark(oil.getRemark());
				oilDTO.setMileage(oil.getMileage());
				oilDTO.setQuantity(String.valueOf(oil.getQuantity()));
				oilDTO.setUserCarId(oil.getUserCarId());
				oilDTO.setOilType(oil.getOilType());
				oilDTO.setCurrentLanguage(oil.getCurrentLanguage());
				oilDTO.setOilTypeId(oil.getOilTypeId());
				oilDTOList.add(oilDTO);
			}
			addJob(new TreeMap<String, Object>(), oilDTOList, methodName);
		}
	}

	/**
	 * 添加任务，去下载加油数据
	 */
	private void addDownOilJob() {
		if (!userSettingBll.find(
				ManagerSettingNames.syncDownOilFinished.toString()).equals(
				"Yes")) {
			if (!userSettingBll.find(
					ManagerSettingNames.syncDownOilPageFinished.toString())
					.equals("Yes")) {
				addJobToDownOilPageCount();
			} else {
				addJobToDownOilContent();
			}
		}
	}

	/**
	 * 批量添加任务，去下载所有分页数据
	 */
	private void addJobToDownOilContent() {
		int totalPage = 0;
		int pageAchieved = 0;
		try {
			totalPage = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownOilTotalPage.toString()));
			pageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownOilPageAchieved
							.toString()));
			if (pageAchieved < totalPage) {
				addJobToDownOilContent(totalPage, pageAchieved);
			}
		} catch (Exception e) {
			Log.e("SyncOilJob", e.toString());
		}

	}

	/**
	 * 批量添加任务，从上次下载未下完的页数，下载剩下的所有分页数据
	 * 
	 * @param totalPage
	 *            总分页数
	 * @param pageAchieved
	 *            当前已下载完成的页码数
	 */
	private void addJobToDownOilContent(int totalPage, int pageAchieved) {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_OIL;

		int startPageNo = pageAchieved + 1;
		for (int i = startPageNo; i <= totalPage; i++) {
			TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
			OilCondition oilCondition = new OilCondition("", "");
			paraMap.put("pageNo", i);
			paraMap.put("pageSize", PAGE_SIZE);
			paraMap.put("condition", oilCondition);
			addJob(paraMap, methodName);
		}
	}

	/**
	 * 添加任务，去获得总的分页数
	 */
	private void addJobToDownOilPageCount() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_OIL;
		TreeMap<String, Object> paraMap = new TreeMap<String, Object>();

		paraMap = new TreeMap<String, Object>();
		OilCondition oilCondition = new OilCondition("", "");
		paraMap.put("pageNo", 1);
		paraMap.put("pageSize", PAGE_SIZE);
		paraMap.put("condition", oilCondition);
		addJob(paraMap, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SyncJob#dealResult(java.util.Map,
	 * org.ksoap2.serialization.SoapObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dealResult(Map<String, Object> map, SoapObject result) {
		if (map == null) {
			syncJobResultCode = SYNC_JOB_RESULT_CODE_NO_DATA_TO_SYNC;
		} else {
			if (result != null && result.getProperty(0) != null) {
				SoapObject so = (SoapObject) result.getProperty(0);

				int resultCode = new Integer(
						so.getProperty("code") == null ? "-1" : so.getProperty(
								"code").toString()).intValue();

				if (resultCode == 0) {
					String methodName = map.get("methodName").toString();
					if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_UPLOAD_OIL)) {
						saveSyncFlag();
					} else if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_OIL)) {
						savePageCount(so.getProperty("data").toString());
						saveOil(so.getProperty("data").toString());
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
	 * 将接收到的加油列表数据，保存到数据库
	 * 
	 * @param oilResultsObject
	 *            从 webservice中获取到了加油列表数据
	 */
	private void saveOil(String oilResultsStr) {
		final int OIL_FIELD_COUNT = 10;// 下载时，接口中定义的，加油信息的字段个数
		if (oilResultsStr == null || oilResultsStr.length() == 0) {
			return;
		}
		String[] accoutsArr;
		if (oilResultsStr.contains("$")) {
			accoutsArr = oilResultsStr.split("\\$");
		} else {
			accoutsArr = new String[] { oilResultsStr };
		}

		int len = accoutsArr.length;

		// 数组的第一维为页码，跳过解析， i=1
		for (int i = 1; i < len; i++) {
			String oilStr = accoutsArr[i];
			if (oilStr.contains("|")) {
				String[] oilArr = oilStr.split("\\|");
				try {
					if (oilArr.length == OIL_FIELD_COUNT) {
						Oil oil = new Oil();
						oil.setId(oilArr[0]);
						oil.setUserCarId(oilArr[1]);
						oil.setOilType(oilArr[2]);
						oil.setAmount(oilArr[3]);
						oil.setQuantity(Float.parseFloat(oilArr[4]));
						oil.setMileage(Integer.parseInt(oilArr[5]));
						oil.setRemark(oilArr[6]);
						oil.setExpenseTime(Format.DateStr.strToDate(
								"yyyy-MM-dd HH:mm:ss", oilArr[7]));
						oil.setCurrentLanguage(oilArr[8]);
						oil.setOilTypeId(oilArr[9]);
						oil.setLastOperate(OilBll.LAST_OPERATE_DOWNLOAD);
						oil.setSyncFlag(OilBll.SYNC_FLAG_YES);
						oilBll.save(oil);
					}
				} catch (NumberFormatException e) {
					Log.e("SyncOilJob", e.toString());
				} catch (ParseException e) {
					Log.e("SyncOilJob", e.toString());
				}
			}
		}

		addDownOilPageAchieved();
		checkAndSetDownOilFinished();
	}

	/**
	 * 在配置中记录当前请求完成的页码数
	 */
	private void addDownOilPageAchieved() {
		try {
			Integer downOilPageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownOilPageAchieved.toString()));
			downOilPageAchieved++;
			userSettingBll.update(
					ManagerSettingNames.syncDownOilPageAchieved.toString(),
					String.valueOf(downOilPageAchieved));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 检测，并在配置中记录，下载加油是否完成了
	 */
	private void checkAndSetDownOilFinished() {
		try {
			Integer downOilPageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownOilPageAchieved.toString()));
			Integer downOilTotalPage = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownOilTotalPage.toString()));
			if (downOilPageAchieved >= downOilTotalPage) {
				userSettingBll.update(
						ManagerSettingNames.syncDownOilFinished.toString(), "Yes");
			}
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
	}

	/**
	 * 保存总分页数，并向任务队列中批量添加下载加油列表数据的任务
	 * 
	 * @param oilPageCountObject
	 *            webservice返回值
	 */
	private void savePageCount(String oilResultsStr) {
		if (!userSettingBll.find(
				ManagerSettingNames.syncDownOilPageFinished.toString()).equals(
				"Yes")) {
			int totalPage = 0;
			if (oilResultsStr == null || oilResultsStr.length() == 0) {
				return;
			}
			String[] accoutsArr;
			if (oilResultsStr.contains("$")) {
				accoutsArr = oilResultsStr.split("\\$");
			} else {
				accoutsArr = new String[] { oilResultsStr };
			}

			// 数组的第一个元素是页码信息
			String oilStr = accoutsArr[0];
			if (oilStr.contains("|")) {
				String[] oilArr = oilStr.split("\\|");
				final int RESULT_LEN = 3;
				if (oilArr.length == RESULT_LEN) {// 返回值
													// ，包含：0表示当前页，1表示每页条数，2表示总记录数，长度为3
					int totalCount = Integer.parseInt(oilArr[2]);
					int perCount = Integer.parseInt(oilArr[1]);
					if (perCount == 0) {
						return;
					}
					// 计算总页数
					totalPage = totalCount / perCount;
					if (totalCount % perCount != 0) {
						totalPage++;
					}

				}

			}

			if (totalPage == 0) {
				userSettingBll.update(
						ManagerSettingNames.syncDownOilPageFinished.toString(),
						"Yes");
				userSettingBll.update(
						ManagerSettingNames.syncDownOilTotalPage.toString(),
						String.valueOf("0"));
				userSettingBll.update(
						ManagerSettingNames.syncDownOilPageAchieved.toString(),
						"0");
				userSettingBll.update(
						ManagerSettingNames.syncDownOilFinished.toString(),
						"Yes");
			} else {
				userSettingBll.update(
						ManagerSettingNames.syncDownOilPageFinished.toString(),
						"Yes");
				userSettingBll.update(
						ManagerSettingNames.syncDownOilTotalPage.toString(),
						String.valueOf(totalPage));
				userSettingBll.update(
						ManagerSettingNames.syncDownOilPageAchieved.toString(),
						"0");

				addJobToDownOilContent(totalPage, 1);// 请求页码时，已经拿 到了第一页的数据
			}
		}

	}

	/**
	 * 同步完成后，调用该方法，在数据 库中写入同步标识位
	 */
	private void saveSyncFlag() {

		if (oilDTOList != null && oilDTOList.size() != 0) {
			for (OilDTO oilDTO : oilDTOList) {
				String id = oilDTO.getId();
				int lastOperate = oilDTO.getLastOperate();
				if (lastOperate == OilBll.LAST_OPERATE_DEL) {
					oilBll.delete(id);
				} else {
					oilBll.setSyncFlag(true, id);
				}
			}
		}

	}

	@Override
	protected void sendMsgToUI() {
		switch (syncJobResultCode) {
		case SYNC_JOB_RESULT_CODE_SUCC:
			sendMsgToUi(SyncJob.SYNC_MSG_OIL_SUCC);
			break;
		case SYNC_JOB_RESULT_CODE_ERR:
			sendMsgToUi(SyncJob.SYNC_MSG_OIL_ERR);
			break;
		default:
			sendMsgToUi(SyncJob.SYNC_MSG_OIL_NO_DATA_TO_SYNC);
			break;
		}
	}

}