package com.cnlaunch.mycar;

import android.content.Context;
import android.content.Intent;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.UserSession;
import com.cnlaunch.mycar.manager.BillingAddActivity;
import com.cnlaunch.mycar.manager.bll.AccountBll;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;

/**
 * 我的账单，推送的消息
 * 
 * @author xuzhuowei
 * 
 */
public class PushBillingSummary implements IPushSummary {

	@Override
	public void push(String cc, Context context) {
		final DBSCarSummaryInfo dd = new DBSCarSummaryInfo(context);
		final Context mContext = context;

		new Thread() {
			public void run() {

				DBSCarSummaryInfo.IDBSCarObserve observe = new DBSCarSummaryInfo.IDBSCarObserve() {
					@Override
					public void execute() {
						Intent intent = new Intent(mContext,
								BillingAddActivity.class);
						mContext.startActivity(intent);

					}
				};
				UserDbHelper newHelper = new UserDbHelper(mContext);
				Integer total = (new AccountBll(mContext, newHelper))
						.getMonthTotal();
				String strBudget = (new ManagerSettingBll(mContext, newHelper))
						.find(ManagerSettingNames.budget.toString());
				Integer budget = 0;
				if (strBudget != null && strBudget.length() > 0) {
					budget = Integer.parseInt(strBudget);
				}
				if (UserSession.IsSomeoneLogined()) {

					DBSCarInfo dbsCarInfo = null;
					if (budget != 0) {
						dbsCarInfo = new DBSCarInfo(
								String.format(
										mContext.getString(R.string.manager_push_str_with_budget),
										budget)+String.valueOf(total)+mContext.getString(R.string.manager_push_unit_yuan),
								null,
								null);
					} else {
						dbsCarInfo = new DBSCarInfo(
								mContext.getString(R.string.manager_push_str)+String.valueOf(total)+mContext.getString(R.string.manager_push_unit_yuan),
								null,
								null);

					}

					dd.register(PushKeys.KEY_BILLING_PREFIX, dbsCarInfo,
							observe);

				} else {
					dd.unRegister(PushKeys.KEY_BILLING_PREFIX);
				}

			};
		}.start();

	}
}
