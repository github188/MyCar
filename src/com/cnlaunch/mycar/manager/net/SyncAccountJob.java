package com.cnlaunch.mycar.manager.net;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.manager.bll.AccountBll;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.database.Account;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * @author xuzhuowei ͬ����Ŀ
 */
public class SyncAccountJob extends SyncJob {
	private final static int PAGE_SIZE = 10;
    private final static boolean D = false;
    private final static String TAG = "SyncAccountJob";
	private AccountBll accountBll;
	private UserDbHelper helper;
	private ManagerSettingBll userSettingBll;
	private List<AccountDTO> accountDTOList;

	// private static int resultCode = 0;

	public SyncAccountJob(Context context, Handler mainHandler) {
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
		accountBll = new AccountBll(context, helper);
		String syncDownAccountFinished = userSettingBll
				.find(ManagerSettingNames.syncDownAccountFinished.toString());
		if (syncDownAccountFinished != null) {
			if (syncDownAccountFinished.equals("Yes")) {
				addUploadAccountJob();
			} else {
				addDownAccountJob();
			}
		}
	}

	/**
	 * �������ȥ�ϴ���Ŀ����
	 */
	private void addUploadAccountJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_UPLOAD_ACCOUNT;
		List<Account> list = accountBll.getAllAccountForSync();
		if (list != null && list.size() > 0) {
			accountDTOList = new ArrayList<AccountDTO>();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				AccountDTO accountDTO = new AccountDTO();
				Account account = list.get(i);
				accountDTO.setAmount(String.valueOf(account.getAmount()));
				accountDTO.setCategory(account.getCategory());
				accountDTO.setExpenseTime(Format.DateStr.getDateTime(account
						.getExpenseTime()));
				accountDTO.setId(account.getId());
				accountDTO.setLastOperate(account.getLastOperate());
				accountDTO.setRemark(account.getRemark());
				accountDTO.setCurrentLanguage(account.getCurrentLanguage());
				accountDTO.setCategoryId(account.getCategoryId());
				accountDTOList.add(accountDTO);
			}
			addJob(new TreeMap<String, Object>(), accountDTOList, methodName);
		}
	}

	/**
	 * �������ȥ������Ŀ����
	 */
	private void addDownAccountJob() {
		if (!userSettingBll.find(
				ManagerSettingNames.syncDownAccountFinished.toString()).equals(
				"Yes")) {
			if (!userSettingBll.find(
					ManagerSettingNames.syncDownAccountPageFinished.toString())
					.equals("Yes")) {
				addJobToDownAccountPageCount();
			} else {
				addJobToDownAccountContent();
			}
		}
	}

	/**
	 * �����������ȥ�������з�ҳ����
	 */
	private void addJobToDownAccountContent() {
		int totalPage = 0;
		int pageAchieved = 0;
		try {
			totalPage = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownAccountTotalPage
							.toString()));
			pageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownAccountPageAchieved
							.toString()));
			if (pageAchieved < totalPage) {
				addJobToDownAccountContent(totalPage, pageAchieved);
			}
		} catch (Exception e) {
			Log.e("SyncAccountJob", e.toString());
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
	private void addJobToDownAccountContent(int totalPage, int pageAchieved) {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_ACCOUNT;

		int startPageNo = pageAchieved + 1;
		for (int i = startPageNo; i <= totalPage; i++) {
			TreeMap<String, Object> paraMap = new TreeMap<String, Object>();
			AccountCondition accountCondition = new AccountCondition("", "");
			paraMap.put("pageNo", i);
			paraMap.put("pageSize", PAGE_SIZE);
			paraMap.put("condition", accountCondition);
			addJob(paraMap, methodName);
		}
	}

	/**
	 * �������ȥ����ܵķ�ҳ��
	 */
	private void addJobToDownAccountPageCount() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_ACCOUNT;
		TreeMap<String, Object> paraMap = new TreeMap<String, Object>();

		paraMap = new TreeMap<String, Object>();
		AccountCondition accountCondition = new AccountCondition("", "");
		paraMap.put("pageNo", 1);
		paraMap.put("pageSize", PAGE_SIZE);
		paraMap.put("condition", accountCondition);
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
							.equals(Constants.SERVICE_MANAGER_METHOD_UPLOAD_ACCOUNT)) {
						saveSyncFlag();
					} else if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_ACCOUNT)) {
						savePageCount(so.getProperty("data").toString());
						saveAccount(so.getProperty("data").toString());
						if(D) Log.d(TAG, "������downloadAccount" + so.getProperty("data").toString());
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
	 * �����յ�����Ŀ�б����ݣ����浽���ݿ�
	 * 
	 * @param accountResultsObject
	 *            �� webservice�л�ȡ������Ŀ�б�����
	 */
	private void saveAccount(String accountResultsStr) {
		final int ACCOUNT_FIELD_COUNT = 7;// ����ʱ���ӿ��ж���ģ�������Ϣ���ֶθ���
		if (accountResultsStr == null || accountResultsStr.length() == 0) {
			return;
		}
		String[] accoutsArr;
		if (accountResultsStr.contains("$")) {
			accoutsArr = accountResultsStr.split("\\$");
		} else {
			accoutsArr = new String[] { accountResultsStr };
		}

		int len = accoutsArr.length;

		// ����ĵ�һάΪҳ�룬���������� i=1
		for (int i = 1; i < len; i++) {
			String accountStr = accoutsArr[i];
			if (accountStr.contains("|")) {
				String[] accountArr = accountStr.split("\\|");
				try {
					if (accountArr.length == ACCOUNT_FIELD_COUNT) {
						Account account = new Account();
						account.setId(accountArr[0]);
						account.setCategory(accountArr[1]);
						account.setAmount(accountArr[2]);
						account.setRemark(accountArr[3]);
						account.setExpenseTime(Format.DateStr.strToDate(
								"yyyy-MM-dd HH:mm:ss", accountArr[4]));
						account.setCurrentLanguage(accountArr[5]);
						account.setCategoryId(accountArr[6]);
						account.setLastOperate(AccountBll.LAST_OPERATE_DOWNLOAD);
						account.setSyncFlag(AccountBll.SYNC_FLAG_YES);
						accountBll.save(account);
					}
				} catch (NumberFormatException e) {
					Log.e("SyncAccountJob", e.toString());
				} catch (ParseException e) {
					Log.e("SyncAccountJob", e.toString());
				}
			}
		}

		addDownAccountPageAchieved();
		checkAndSetDownAccountFinished();
	}

	/**
	 * �������м�¼��ǰ������ɵ�ҳ����
	 */
	private void addDownAccountPageAchieved() {
		
		try {
			Integer downAccountPageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownAccountPageAchieved
							.toString()));
			downAccountPageAchieved++;
			userSettingBll.update(
					ManagerSettingNames.syncDownAccountPageAchieved.toString(),
					String.valueOf(downAccountPageAchieved));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��⣬���������м�¼��������Ŀ�Ƿ������
	 */
	private void checkAndSetDownAccountFinished() {
		try {
			Integer downAccountPageAchieved = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownAccountPageAchieved
							.toString()));
			Integer downAccountTotalPage = Integer.parseInt(userSettingBll
					.find(ManagerSettingNames.syncDownAccountTotalPage.toString()));

			if (downAccountPageAchieved >= downAccountTotalPage) {
				userSettingBll.update(
						ManagerSettingNames.syncDownAccountFinished.toString(),
						"Yes");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �����ܷ�ҳ������������������������������Ŀ�б����ݵ�����
	 * 
	 * @param accountPageCountObject
	 *            webservice����ֵ
	 */
	private void savePageCount(String accountResultsStr) {
		if (!userSettingBll.find(
				ManagerSettingNames.syncDownAccountPageFinished.toString())
				.equals("Yes")) {
			int totalPage = 0;
			if (accountResultsStr == null || accountResultsStr.length() == 0) {
				return;
			}
			String[] accoutsArr;
			if (accountResultsStr.contains("$")) {
				accoutsArr = accountResultsStr.split("\\$");
			} else {
				accoutsArr = new String[] { accountResultsStr };
			}

			// ����ĵ�һ��Ԫ����ҳ����Ϣ
			String accountStr = accoutsArr[0];
			if (accountStr.contains("|")) {
				String[] accountArr = accountStr.split("\\|");
				final int RESULT_LEN = 3;
				if (accountArr.length == RESULT_LEN) {// ����ֵ
														// ��������0��ʾ��ǰҳ��1��ʾÿҳ������2��ʾ�ܼ�¼��������Ϊ3
					int totalCount = Integer.parseInt(accountArr[2]);
					int perCount = Integer.parseInt(accountArr[1]);
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
						ManagerSettingNames.syncDownAccountPageFinished
								.toString(), "Yes");
				userSettingBll
						.update(ManagerSettingNames.syncDownAccountTotalPage
								.toString(), String.valueOf("0"));
				userSettingBll.update(
						ManagerSettingNames.syncDownAccountPageAchieved
								.toString(), "0");
				userSettingBll.update(
						ManagerSettingNames.syncDownAccountFinished.toString(),
						"Yes");
			} else {
				userSettingBll.update(
						ManagerSettingNames.syncDownAccountPageFinished
								.toString(), "Yes");
				userSettingBll
						.update(ManagerSettingNames.syncDownAccountTotalPage
								.toString(), String.valueOf(totalPage));
				userSettingBll.update(
						ManagerSettingNames.syncDownAccountPageAchieved
								.toString(), "0");

				addJobToDownAccountContent(totalPage, 1);// ����ҳ��ʱ���Ѿ��� ���˵�һҳ������
			}
		}

	}

	/**
	 * ͬ����ɺ󣬵��ø÷����������ݿ���д��ͬ����ʶλ
	 */
	private void saveSyncFlag() {

		if (accountDTOList != null && accountDTOList.size() != 0) {
			for (AccountDTO accountDTO : accountDTOList) {
				String id = accountDTO.getId();
				int lastOperate = accountDTO.getLastOperate();
				if (lastOperate == AccountBll.LAST_OPERATE_DEL) {
					accountBll.delete(id);
				} else {
					accountBll.setSyncFlag(true, id);
				}
			}

		}

	}

	@Override
	protected void sendMsgToUI() {
		switch (syncJobResultCode) {
		case SYNC_JOB_RESULT_CODE_SUCC:
			sendMsgToUi(SyncJob.SYNC_MSG_ACCOUNT_SUCC);
			break;
		case SYNC_JOB_RESULT_CODE_ERR:
			sendMsgToUi(SyncJob.SYNC_MSG_ACCOUNT_ERR);
			break;
		default:
			sendMsgToUi(SyncJob.SYNC_MSG_ACCOUNT_NO_DATA_TO_SYNC);
			break;
		}
	}

}