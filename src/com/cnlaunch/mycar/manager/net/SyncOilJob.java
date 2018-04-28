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
 * @author xuzhuowei ͬ�����ͼ�¼
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
	 * �������ȥ�ϴ���������
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
	 * �������ȥ���ؼ�������
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
	 * �����������ȥ�������з�ҳ����
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
	 * ����������񣬴��ϴ�����δ�����ҳ��������ʣ�µ����з�ҳ����
	 * 
	 * @param totalPage
	 *            �ܷ�ҳ��
	 * @param pageAchieved
	 *            ��ǰ��������ɵ�ҳ����
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
	 * �������ȥ����ܵķ�ҳ��
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
	 * �����յ��ļ����б����ݣ����浽���ݿ�
	 * 
	 * @param oilResultsObject
	 *            �� webservice�л�ȡ���˼����б�����
	 */
	private void saveOil(String oilResultsStr) {
		final int OIL_FIELD_COUNT = 10;// ����ʱ���ӿ��ж���ģ�������Ϣ���ֶθ���
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

		// ����ĵ�һάΪҳ�룬���������� i=1
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
	 * �������м�¼��ǰ������ɵ�ҳ����
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
	 * ��⣬���������м�¼�����ؼ����Ƿ������
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
	 * �����ܷ�ҳ���������������������������ؼ����б����ݵ�����
	 * 
	 * @param oilPageCountObject
	 *            webservice����ֵ
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

			// ����ĵ�һ��Ԫ����ҳ����Ϣ
			String oilStr = accoutsArr[0];
			if (oilStr.contains("|")) {
				String[] oilArr = oilStr.split("\\|");
				final int RESULT_LEN = 3;
				if (oilArr.length == RESULT_LEN) {// ����ֵ
													// ��������0��ʾ��ǰҳ��1��ʾÿҳ������2��ʾ�ܼ�¼��������Ϊ3
					int totalCount = Integer.parseInt(oilArr[2]);
					int perCount = Integer.parseInt(oilArr[1]);
					if (perCount == 0) {
						return;
					}
					// ������ҳ��
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

				addJobToDownOilContent(totalPage, 1);// ����ҳ��ʱ���Ѿ��� ���˵�һҳ������
			}
		}

	}

	/**
	 * ͬ����ɺ󣬵��ø÷����������� ����д��ͬ����ʶλ
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