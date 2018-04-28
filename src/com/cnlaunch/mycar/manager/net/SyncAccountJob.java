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
 * @author xuzhuowei 同步账目
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
	 * 添加任务，去上传账目数据
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
	 * 添加任务，去下载账目数据
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
	 * 批量添加任务，去下载所有分页数据
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
	 * 批量添加任务，从上次下载未下完的页数，下载剩下的所有分页数据
	 * 
	 * @param totalPage
	 *            总分页数
	 * @param pageAchieved
	 *            当前已下载完成的页码数
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
	 * 添加任务，去获得总的分页数
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
						if(D) Log.d(TAG, "☆☆☆☆☆downloadAccount" + so.getProperty("data").toString());
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
	 * 将接收到的账目列表数据，保存到数据库
	 * 
	 * @param accountResultsObject
	 *            从 webservice中获取到了账目列表数据
	 */
	private void saveAccount(String accountResultsStr) {
		final int ACCOUNT_FIELD_COUNT = 7;// 下载时，接口中定义的，记账信息的字段个数
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

		// 数组的第一维为页码，跳过解析， i=1
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
	 * 在配置中记录当前请求完成的页码数
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
	 * 检测，并在配置中记录，下载账目是否完成了
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
	 * 保存总分页数，并向任务队列中批量添加下载账目列表数据的任务
	 * 
	 * @param accountPageCountObject
	 *            webservice返回值
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

			// 数组的第一个元素是页码信息
			String accountStr = accoutsArr[0];
			if (accountStr.contains("|")) {
				String[] accountArr = accountStr.split("\\|");
				final int RESULT_LEN = 3;
				if (accountArr.length == RESULT_LEN) {// 返回值
														// ，包含：0表示当前页，1表示每页条数，2表示总记录数，长度为3
					int totalCount = Integer.parseInt(accountArr[2]);
					int perCount = Integer.parseInt(accountArr[1]);
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

				addJobToDownAccountContent(totalPage, 1);// 请求页码时，已经拿 到了第一页的数据
			}
		}

	}

	/**
	 * 同步完成后，调用该方法，在数据库中写入同步标识位
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