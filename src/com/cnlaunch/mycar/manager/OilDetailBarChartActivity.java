package com.cnlaunch.mycar.manager;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.time.DateUtils;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.BarChart;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.database.Oil;

public class OilDetailBarChartActivity extends BaseActivity {
	private Resources r;
	private Date endDate = new Date();
	private Date startDate = DateUtils.addMonths(endDate, -1);
	BarChart barchart_average_expense;
	BarChart barchart_average_oil;
	Random rnd = new Random();

	private List<Oil> oilList;

	// 返回时刷新列表
	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_oil_detail_barchart,
				R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_oil_detail);
		setCustomeTitleRight("");

		r = this.getResources();
		changeToolbarIcon();

		barchart_average_expense = (BarChart) findViewById(R.id.barchart_average_expense);
		barchart_average_oil = (BarChart) findViewById(R.id.barchart_average_oil);

		// 从数据据读取列表
		oilList = (new OilBll(this, getHelper())).getList(startDate, endDate);

		barchart_average_oil.setItemValues(getItemLabelsForOil(oilList),
				getItemValuesForOil(oilList));
		barchart_average_oil
				.setTitle(getString(R.string.manager_oil_per_hundred_kilomi));

		barchart_average_expense.setItemValues(
				getItemLabelsForExpense(oilList),
				getItemValuesForExpense(oilList));
		barchart_average_expense
				.setTitle(getString(R.string.manager_oil_cost_per_day));

	}

	/**
	 * 突出显示当前页面对应的导航栏按钮
	 */
	private void changeToolbarIcon() {
		// final int LEFT = 0;
		final int TOP = 1;
		// final int RIGHT = 2;
		// final int BOTTOM = 3;

		Button button = (Button) findViewById(R.id.manager_oil_textbutton_menu_oil_chart);
		button.setBackgroundResource(R.drawable.manager_toolbar_bg_selected);

		Drawable drawable = r
				.getDrawable(R.drawable.manager_toolbar_oil_chart_pressed);
		Drawable[] drawables = button.getCompoundDrawables();
		drawable.setBounds(drawables[TOP].getBounds());
		button.setCompoundDrawables(null, drawable, null, null);
	}

	private int[] getItemValuesForOil(List<Oil> list) {
		int[] arr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = rnd.nextInt(20);
		}
		return arr;
	}

	private String[] getItemLabelsForOil(List<Oil> list) {
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = String.valueOf(list.get(i).getMileage());
		}

		return arr;
	}

	private int[] getItemValuesForExpense(List<Oil> list) {
		int[] arr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = rnd.nextInt(20);
		}
		return arr;
	}

	private String[] getItemLabelsForExpense(List<Oil> list) {
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i).getExpenseTime().getMonth() + "/"
					+ list.get(i).getExpenseTime().getDate();
		}
		return arr;
	}

	/**
	 * 绑定底部菜单点击事件
	 * 
	 * @param v
	 */
	// TODO
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.manager_oil_textbutton_menu_back_main:
			backMain(OilDetailBarChartActivity.this);
			break;
		case R.id.manager_oil_textbutton_menu_oil_add:
			showOilAdd();
			break;
		case R.id.manager_oil_textbutton_menu_oil_detail:

			showOilDetail();
			break;
		case R.id.manager_oil_textbutton_menu_oil_chart:
			break;

		default:
			break;
		}
	}

	/**
	 * 跳转到加油记录页
	 */

	private void showOilDetail() {
		Intent intent = new Intent();
		intent.setClass(OilDetailBarChartActivity.this, OilDetailActivity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(0, 0);
	}

	/**
	 * 跳转到加油页
	 */
	private void showOilAdd() {
		Intent intent = new Intent();
		intent.setClass(OilDetailBarChartActivity.this, OilAddActivity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(0, 0);
	}

}
