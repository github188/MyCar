package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.Format.DateStr;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.StatisticsBll;
import com.cnlaunch.mycar.manager.database.StatisticsByCategory;
import com.cnlaunch.mycar.manager.database.StatisticsByDate;

public class BillingStatisticsMonthListActivity extends BaseActivity {
	GestureDetector gestureDetector;
	private Context context;
	private String[] mYearList;

	private List<StatisticsByDate> model_date_list = null;
	private List<StatisticsByCategory> model_category_list = null;

	private ArrayList<HashMap<String, String>> my_date_list = null;
	private ArrayList<HashMap<String, String>> my_category_list = null;

	SimpleAdapter date_list_adapter = null;
	SimpleAdapter category_list_adapter = null;

	private String mYear;

	// ����ʱˢ���б�
	@Override
	public void onRestart() {
		super.onRestart();
		refreshAllData();
	}

	private void updateTotalAmount() {
		TextView statistics_list_foot_textview_amount = (TextView) findViewById(R.id.statistics_list_foot_textview_amount);
		statistics_list_foot_textview_amount
				.setText(StringUtil.getCurrency(context)
						+ Format.doubleToCommercialString(getTotal(model_category_list)));
	}

	private void refreshAllData() {
		setCustomeTitleRight(mYear + getText(R.string.year));
		refreshDayListView(mYear);
		refreshCategoryListView(mYear);
		updateTotalAmount();
	}

	// ��ȡ���ݿ�����,������mylist,modelList,�ٸ���listview
	private void refreshDayListView(String year) {
		// ���ɶ�̬���飬����ת������
		my_date_list.clear();
		model_date_list.clear();

		// �����ݾݶ�ȡ�б�
		model_date_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForMonth(year);

		if (model_date_list != null) {
			fillDateList();
		}
		if (date_list_adapter != null) {
			date_list_adapter.notifyDataSetChanged();
		}
	}

	// ��ȡ���ݿ�����,������mylist,modelList,�ٸ���listview
	private void refreshCategoryListView(String year) {
		// ���ɶ�̬���飬����ת������
		my_category_list.clear();
		model_category_list.clear();

		// �����ݾݶ�ȡ�б�
		model_category_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForCategorySomeYear(year);

		if (model_category_list != null) {
			fillCategoryList();
		}
		if (category_list_adapter != null) {
			category_list_adapter.notifyDataSetChanged();
		}
	}

	private void fillDateList() {
		for (StatisticsByDate model : model_date_list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("item_date_title", model.getMonthStr(this));
			map.put("item_date_text", model.getTotalAmountStr(context));
			my_date_list.add(map);
		}
	}

	private void fillCategoryList() {
		for (StatisticsByCategory model : model_category_list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("statistics_detail_category", model.getCategory());
			map.put("statistics_detail_amount", model.getTotalAmountStr());
			map.put("statistics_detail_item_frequency",
					String.valueOf(model.getFrequency())
							+ getText(R.string.manager_frequency_unit));
			my_category_list.add(map);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_billing_statistics_list_month,
				R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_account_statistics_year);
		context = this;

		mYear = DateStr.getYear();
		Intent intent = getIntent();
		if (intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.year")) {
			mYear = intent
					.getStringExtra("com.cnlaunch.mycar.manager.Statistics.year");
		}

		// ��listview����
		ListView listview_date = (ListView) findViewById(R.id.list_date);
		ListView listview_category = (ListView) findViewById(R.id.list_category);

		// �޸ı���
		setCustomeTitleRight(mYear + getText(R.string.year));
		// �޸�ͳ����Ŀ����
		TextView statistics_list_category_head = (TextView) findViewById(R.id.statistics_list_category_head);
		TextView statistics_list_date_head = (TextView) findViewById(R.id.statistics_list_date_head);
		statistics_list_category_head
				.setText(getText(R.string.manager_account_category_list));
		statistics_list_date_head
				.setText(getText(R.string.manager_statistics_year_list));

		//Ϊ�����˵�׼������б�����
		List<StatisticsByDate> yearList = (new StatisticsBll(this, getHelper()))
				.getStatisticForYear();
		if (yearList != null && yearList.size() > 0) {
			mYearList = new String[yearList.size()];
			for (int i = 0; i < yearList.size(); i++) {
				mYearList[i] = yearList.get(i).getYear();
			}
		}

		// �����ݾݶ�ȡ�б�,��ʾ�·��б�
		model_date_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForMonth(mYear);
		my_date_list = new ArrayList<HashMap<String, String>>();

		if (model_date_list != null) {
			fillDateList();

			// ����������������===��ListItem
			date_list_adapter = new SimpleAdapter(this, // ûʲô����
					my_date_list,// ������Դ
					R.layout.manager_billing_statistics_list_item_date_month,// ListItem��XMLʵ��

					// ��̬������ListItem��Ӧ������
					new String[] { "item_date_title", "item_date_text" },

					// ListItem��XML�ļ����������TextView ID
					new int[] { R.id.item_date_title, R.id.item_date_text });
			// ��Ӳ�����ʾ
			listview_date.setAdapter(date_list_adapter);
			// Ϊlistview�󶨵���¼�����
			listview_date.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					showDayList(model_date_list.get((int) id));
				}

			});
		}

		// �����ݾݶ�ȡ֧������ͳ������,��ʾ�б�
		model_category_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForCategorySomeYear(mYear);
		my_category_list = new ArrayList<HashMap<String, String>>();

		if (model_category_list != null) {
			fillCategoryList();

			// ����������������===��ListItem
			category_list_adapter = new SimpleAdapter(this, // ûʲô����
					my_category_list,// ������Դ
					R.layout.manager_billing_statistics_list_item_category,// ListItem��XMLʵ��

					// ��̬������ListItem��Ӧ������
					new String[] { "statistics_detail_category",
							"statistics_detail_amount",
							"statistics_detail_item_frequency" },

					// ListItem��XML�ļ����������TextView ID
					new int[] { R.id.statistics_detail_category,
							R.id.statistics_detail_amount,
							R.id.statistics_detail_item_frequency });
			// ��Ӳ�����ʾ
			listview_category.setAdapter(category_list_adapter);
			// Ϊlistview�󶨵���¼�����
			listview_category.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					showCategoryDalList(model_category_list.get((int) id));
				}
			});

		}

		// ����ʾ"�ܼ�"
		updateTotalAmount();
	}

	// ��ת������ͳ��ҳ
	private void showDayList(StatisticsByDate model) {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.year",
				model.getYear());
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.month",
				model.getMonth());
		intent.setClass(BillingStatisticsMonthListActivity.this,
				BillingStatisticsDayListActivity.class);
		startActivityForResult(intent, 0);
	}

	// �����ܽ��
	private Double getTotal(List<StatisticsByCategory> modelList) {
		Double total = new Double(0);
		if (modelList != null) {
			for (StatisticsByCategory model : modelList) {
				total += model.getTotalAmount();
			}
		}
		return total;
	}

	// ��ʾĳ��ĳһ���ఴ��ķֲ�
	private void showCategoryDalList(StatisticsByCategory model) {
		Intent intent = new Intent();
		intent.setClass(BillingStatisticsMonthListActivity.this,
				BillingCategoryDayListActivity.class);
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.year",
				model.getYear());
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.categoryId",
				model.getCategoryId());
		startActivity(intent);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		showYearListDialog();
		menu.clear();
		menu.add(0, 1, 1, R.string.billing_statistics_year_list);
		return false;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			showYearListDialog();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void showYearListDialog() {
		if(mYearList==null || mYearList.length<1){
			mYearList = new String[]{DateStr.getYear()};
		}
		

		CustomDialog customDialog=new CustomDialog(this);
		customDialog.setTitle(getResources().getText(R.string.billing_statistics_year_choose).toString());
		customDialog.setItems(mYearList, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mYear = mYearList[which];
				refreshAllData();
			}
		});
		customDialog.show();

	}

}