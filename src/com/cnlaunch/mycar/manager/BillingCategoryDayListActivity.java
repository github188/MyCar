package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.CategoryBll;
import com.cnlaunch.mycar.manager.bll.StatisticsBll;
import com.cnlaunch.mycar.manager.database.Account;

public class BillingCategoryDayListActivity extends BaseActivity{
	private List<Account> accountList;
	private Context context;
	private Map<String,String> categroyMap;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_billing_category_day_list, R.layout.custom_title);
		context = this;
		// ��listview����
		ListView list = (ListView) findViewById(R.id.manager_category_day_list);

		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

		String year = "";
		String month = "";
		//String category = "";
		String categoryId = "";
		Intent intent = getIntent();
		categroyMap = (new CategoryBll(this,
				getHelper())).getAllLocalCategoryNameByID(this);
		// �޸ı���
		setCustomeTitleLeft(getText(R.string.manager_account_category_day_detail));
		setCustomeTitleRight("");
		String title="";
		if (intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.year")
				&& intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.categoryId")) {
			year = intent
					.getStringExtra("com.cnlaunch.mycar.manager.Statistics.year");
			categoryId = intent
					.getStringExtra("com.cnlaunch.mycar.manager.Statistics.categoryId");
			if (intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.month")) {
				month = intent
						.getStringExtra("com.cnlaunch.mycar.manager.Statistics.month");
				// �����ݾݶ�ȡ�б�
				accountList = (new StatisticsBll(this,getHelper()))
						.getAccountForCategorySomeMonth(year, month, categoryId);
				// �޸ı���
				title = year + getText(R.string.year) + month
						+ getText(R.string.month) + " " + getCategoryName(categoryId);
			} else {
				// �����ݾݶ�ȡ�б�
				accountList = (new StatisticsBll(this,getHelper()))
						.getAccountForCategorySomeYear(year, categoryId);
				// �޸ı���
				title=year + getText(R.string.year)+" "+getCategoryName(categoryId);
			}
			((TextView) findViewById(R.id.manager_category_day_list_title_textview)).setText(title);
		}

		if (accountList != null) {
			for (Account account : accountList) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("category_day_list_item_title", account.getYearMonthDay());
				map.put("category_day_list_item_text", com.cnlaunch.mycar.common.utils.StringUtil
						.getCurrency(context)
						+ account.getAmountStr());
				mylist.add(map);
			}

			// ����������������===��ListItem
			SimpleAdapter adapter = new SimpleAdapter(this, mylist,
					R.layout.manager_billing_category_day_list_item, new String[] {
							"category_day_list_item_title",
							"category_day_list_item_text" }, new int[] {
							R.id.manager_category_day_list_item_title,
							R.id.manager_category_day_list_item_text });
			// ��Ӳ�����ʾ
			list.setAdapter(adapter);
		}

		updateTotalAmount(accountList);
	}

	/**�����ܽ��
	 * @param accountList �����ֿ��в�ѯ�����������ݵ��б�
	 */
	private void updateTotalAmount(List<Account> accountList) {
		TextView category_day_list_foot_textview_amount = (TextView) findViewById(R.id.manager_category_day_list_foot_textview_amount);
		category_day_list_foot_textview_amount.setText(StringUtil
				.getCurrency(context)
				+ Format
						.doubleToCommercialString(getTotal(accountList)));
	}

	/**�����ܽ��
	 * @param accountList �����ֿ��в�ѯ�����������ݵ��б�
	 * @return
	 */
	private Double getTotal(List<Account> accountList) {
		Double total = new Double(0);
		if (accountList != null) {
			for (Account account : accountList) {
				total += account.getAmount();
			}
		}
		return total;
	}
	//��ʾ�������
	public String  getCategoryName(String categroyId){
		String category = "";
		if(categroyMap != null && categroyMap.containsKey(categroyId)){
			 category = categroyMap.get(categroyId);
		 }
		 else{
			 category = categroyId;
		 }
		return category;
	}
}