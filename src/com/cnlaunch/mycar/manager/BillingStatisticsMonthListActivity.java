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

	// 返回时刷新列表
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

	// 读取数据库数据,并更新mylist,modelList,再更新listview
	private void refreshDayListView(String year) {
		// 生成动态数组，并且转载数据
		my_date_list.clear();
		model_date_list.clear();

		// 从数据据读取列表
		model_date_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForMonth(year);

		if (model_date_list != null) {
			fillDateList();
		}
		if (date_list_adapter != null) {
			date_list_adapter.notifyDataSetChanged();
		}
	}

	// 读取数据库数据,并更新mylist,modelList,再更新listview
	private void refreshCategoryListView(String year) {
		// 生成动态数组，并且转载数据
		my_category_list.clear();
		model_category_list.clear();

		// 从数据据读取列表
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

		// 绑定listview容器
		ListView listview_date = (ListView) findViewById(R.id.list_date);
		ListView listview_category = (ListView) findViewById(R.id.list_category);

		// 修改标题
		setCustomeTitleRight(mYear + getText(R.string.year));
		// 修改统计栏目标题
		TextView statistics_list_category_head = (TextView) findViewById(R.id.statistics_list_category_head);
		TextView statistics_list_date_head = (TextView) findViewById(R.id.statistics_list_date_head);
		statistics_list_category_head
				.setText(getText(R.string.manager_account_category_list));
		statistics_list_date_head
				.setText(getText(R.string.manager_statistics_year_list));

		//为弹出菜单准备年份列表数据
		List<StatisticsByDate> yearList = (new StatisticsBll(this, getHelper()))
				.getStatisticForYear();
		if (yearList != null && yearList.size() > 0) {
			mYearList = new String[yearList.size()];
			for (int i = 0; i < yearList.size(); i++) {
				mYearList[i] = yearList.get(i).getYear();
			}
		}

		// 从数据据读取列表,显示月份列表
		model_date_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForMonth(mYear);
		my_date_list = new ArrayList<HashMap<String, String>>();

		if (model_date_list != null) {
			fillDateList();

			// 生成适配器，数组===》ListItem
			date_list_adapter = new SimpleAdapter(this, // 没什么解释
					my_date_list,// 数据来源
					R.layout.manager_billing_statistics_list_item_date_month,// ListItem的XML实现

					// 动态数组与ListItem对应的子项
					new String[] { "item_date_title", "item_date_text" },

					// ListItem的XML文件里面的两个TextView ID
					new int[] { R.id.item_date_title, R.id.item_date_text });
			// 添加并且显示
			listview_date.setAdapter(date_list_adapter);
			// 为listview绑定点击事件处理
			listview_date.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					showDayList(model_date_list.get((int) id));
				}

			});
		}

		// 从数据据读取支出分类统计数据,显示列表
		model_category_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForCategorySomeYear(mYear);
		my_category_list = new ArrayList<HashMap<String, String>>();

		if (model_category_list != null) {
			fillCategoryList();

			// 生成适配器，数组===》ListItem
			category_list_adapter = new SimpleAdapter(this, // 没什么解释
					my_category_list,// 数据来源
					R.layout.manager_billing_statistics_list_item_category,// ListItem的XML实现

					// 动态数组与ListItem对应的子项
					new String[] { "statistics_detail_category",
							"statistics_detail_amount",
							"statistics_detail_item_frequency" },

					// ListItem的XML文件里面的两个TextView ID
					new int[] { R.id.statistics_detail_category,
							R.id.statistics_detail_amount,
							R.id.statistics_detail_item_frequency });
			// 添加并且显示
			listview_category.setAdapter(category_list_adapter);
			// 为listview绑定点击事件处理
			listview_category.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					showCategoryDalList(model_category_list.get((int) id));
				}
			});

		}

		// 在显示"总计"
		updateTotalAmount();
	}

	// 跳转至按日统计页
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

	// 计算总金额
	private Double getTotal(List<StatisticsByCategory> modelList) {
		Double total = new Double(0);
		if (modelList != null) {
			for (StatisticsByCategory model : modelList) {
				total += model.getTotalAmount();
			}
		}
		return total;
	}

	// 显示某月某一分类按天的分布
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