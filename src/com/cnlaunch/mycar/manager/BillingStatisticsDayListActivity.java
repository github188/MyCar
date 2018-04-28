package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.StatisticsBll;
import com.cnlaunch.mycar.manager.database.StatisticsByCategory;
import com.cnlaunch.mycar.manager.database.StatisticsByDate;

public class BillingStatisticsDayListActivity extends BaseActivity {
	private Context context;
	GestureDetector gestureDetector;

	private List<StatisticsByDate> model_day_List = null;
	private List<StatisticsByCategory> model_category_list = null;

	private ArrayList<HashMap<String, String>> my_date_list = null;
	private ArrayList<HashMap<String, String>> my_category_list = null;

	private SimpleAdapter day_list_adapter = null;
	private SimpleAdapter category_list_adapter = null;

	private String year = null;
	private String month = null;

	// 返回时刷新列表
	@Override
	public void onRestart() {
		super.onRestart();
		refreshDayListView(year, month);
		refreshCategoryListView(year, month);
		updateTotalAmount();
	}

	/**读取数据库数据,并更新mylist,modelList,再更新listview
	 * @param year
	 * @param month
	 */
	private void refreshDayListView(String year, String month) {
		// 生成动态数组，并且转载数据
		my_date_list.clear();
		model_day_List.clear();

		// 从数据据读取列表
		model_day_List = (new StatisticsBll(this, getHelper()))
				.getStatisticForDay(year, month);

		if (model_day_List != null) {
			fillDateList();
		}
		if (day_list_adapter != null) {
			day_list_adapter.notifyDataSetChanged();
		}
	}

	/**读取数据库数据,并更新mylist,modelList,再更新listview
	 * @param year
	 * @param month
	 */
	private void refreshCategoryListView(String year, String month) {
		// 生成动态数组，并且转载数据
		if (my_category_list != null) {
			my_category_list.clear();
		}
		if (model_category_list != null) {
			model_category_list.clear();
		}

		// 从数据据读取列表
		model_category_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForCategorySomeMonth(year, month);

		if (model_category_list != null) {
			fillCategoryList();
		}
		if (category_list_adapter != null) {
			category_list_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_billing_statistics_list_day,
				R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_account_statistics_month);
		context = this;

		// 修改统计栏目标题
		TextView statistics_list_category_head = (TextView) findViewById(R.id.statistics_list_category_head);
		TextView statistics_list_date_head = (TextView) findViewById(R.id.statistics_list_date_head);
		statistics_list_category_head
				.setText(getText(R.string.manager_account_category_list));
		statistics_list_date_head
				.setText(getText(R.string.manager_statistics_month_list));

		year = Format.DateStr.getYear();
		month = Format.DateStr.getMonth();
		Intent intent = getIntent();
		if (intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.year")
				&& intent.hasExtra("com.cnlaunch.mycar.manager.Statistics.month")) {
			year = intent
					.getStringExtra("com.cnlaunch.mycar.manager.Statistics.year");
			month = intent
					.getStringExtra("com.cnlaunch.mycar.manager.Statistics.month");
		}

		// 绑定listview容器
		ListView listview_date = (ListView) findViewById(R.id.list_date);
		// 绑定listview容器
		ListView listview_category = (ListView) findViewById(R.id.list_category);

		// 修改标题
		setCustomeTitleRight(year + getText(R.string.year) + month
				+ getText(R.string.month));

		// 生成动态数组，并且绑定数据
		my_date_list = new ArrayList<HashMap<String, String>>();

		// 从数据库读取列表
		model_day_List = (new StatisticsBll(this, getHelper()))
				.getStatisticForDay(year, month);

		if (model_day_List != null) {
			fillDateList();
			day_list_adapter = new SimpleAdapter(this, my_date_list,
					R.layout.manager_billing_statistics_list_item_date_day,
					new String[] { "item_date_title", "item_date_text" },
					new int[] { R.id.item_date_title, R.id.item_date_text });
			listview_date.setAdapter(day_list_adapter);
			// 为listview绑定点击事件处理
			listview_date.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					showDetail(model_day_List.get((int) id));
				}

			});

		}

		// 从数据库读取支出分类统计数据,显示列表
		model_category_list = (new StatisticsBll(this, getHelper()))
				.getStatisticForCategorySomeMonth(year, month);
		my_category_list = new ArrayList<HashMap<String, String>>();

		if (model_category_list != null) {
			fillCategoryList();

			// 生成适配器，数组===》ListItem
			category_list_adapter = new SimpleAdapter(this, my_category_list,// 数据来源
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
					showCategoryDayList(model_category_list.get((int) id));
				}
			});

			// 在显示"总计"
			updateTotalAmount();
		}

	}

	/**
	 * 将按日期统计的结果，写入用于适配器的Map中
	 */
	private void fillDateList() {
		for (StatisticsByDate model : model_day_List) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("item_date_title", model.getDayStr(this));
			map.put("item_date_text", model.getTotalAmountStr(context));
			my_date_list.add(map);
		}
	}

	/**
	 * 将按类别统计的结果，写入用于适配器的Map中
	 */
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

	/**
	 * 更新总金额
	 */
	private void updateTotalAmount() {
		TextView statistics_list_foot_textview_amount = (TextView) findViewById(R.id.statistics_list_foot_textview_amount);
		statistics_list_foot_textview_amount
				.setText(StringUtil.getCurrency(context)
						+ Format.doubleToCommercialString(getTotal(model_category_list)));
	}

	/**跳转到详情页
	 * @param statisticsModel
	 */
	private void showDetail(StatisticsByDate statisticsModel) {
		Intent intent = new Intent();
		intent.setClass(BillingStatisticsDayListActivity.this,
				BillingDetailActivity.class);
		intent.putExtra("com.cnlaunch.mycar.manager.year", statisticsModel.getYear());
		intent.putExtra("com.cnlaunch.mycar.manager.month",
				statisticsModel.getMonth());
		intent.putExtra("com.cnlaunch.mycar.manager.day", statisticsModel.getDay());
		startActivity(intent);
	}

	/**计算总金额
	 * @param modelList 查询结果列表
	 * @return 总金额
	 */
	private Double getTotal(List<StatisticsByCategory> modelList) {
		Double total = new Double(0);
		if (modelList != null) {
			for (StatisticsByCategory model : modelList) {
				total += model.getTotalAmount();
			}
		}
		return total;
	}

	/**跳转到某月某一分类按天的分布页
	 * @param model
	 */
	private void showCategoryDayList(StatisticsByCategory model) {
		Intent intent = new Intent();
		intent.setClass(BillingStatisticsDayListActivity.this,
				BillingCategoryDayListActivity.class);
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.year",
				model.getYear());
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.month",
				model.getMonth());
		intent.putExtra("com.cnlaunch.mycar.manager.Statistics.categoryId",
				model.getCategoryId());
		startActivity(intent);

	}
}