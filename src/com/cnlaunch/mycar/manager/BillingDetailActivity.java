package com.cnlaunch.mycar.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.manager.bll.AccountBll;
import com.cnlaunch.mycar.manager.database.Account;

public class BillingDetailActivity extends BaseActivity {
	private Context context;
	private String year;
	private String month;
	private String day;

	private List<Account> accountList;
	private ListView detail_list;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> mylist;

	/* 
	 * 返回时刷新列表
	 */
	@Override
	public void onRestart() {
		super.onRestart();
		refreshListView(year, month, day);
		updateTotalAmount(accountList);
	}

	/**读取数据库数据,并更新mylist,accountList,再更新listview
	 * @param year
	 * @param month
	 * @param day
	 */
	private void refreshListView(String year, String month, String day) {
		// 生成动态数组，并且转载数据
		mylist.clear();
		accountList.clear();

		// 从数据据读取列表
		accountList = (new AccountBll(this, getHelper())).getDataForDay(year,
				month, day);

		if (accountList != null) {
			for (Account account : accountList) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("detail_category", account.getCategory());
				map.put("detail_amount", com.cnlaunch.mycar.common.utils.StringUtil
						.getCurrency(context)
						+ account.getAmountStr());
				map.put("detail_time", account.getTimeStr());
				mylist.add(map);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_billing_detail, R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_account_detail);
		context = this;

		// 为文本标签加上时间后缀
		Intent intent = getIntent();

		if (intent.hasExtra("com.cnlaunch.mycar.manager.year")
				&& intent.hasExtra("com.cnlaunch.mycar.manager.month")
				&& intent.hasExtra("com.cnlaunch.mycar.manager.day")) {
			year = intent.getStringExtra("com.cnlaunch.mycar.manager.year");
			month = intent.getStringExtra("com.cnlaunch.mycar.manager.month");
			day = intent.getStringExtra("com.cnlaunch.mycar.manager.day");
		} else {
			year = Format.DateStr.getYear();
			month = Format.DateStr.getMonth();
			day = Format.DateStr.getDay();
		}

		// 绑定listview容器
		detail_list = (ListView) findViewById(R.id.manager_billing_detail_list);


		// 加列表头
		detail_list.addHeaderView(LayoutInflater.from(this).inflate(
				R.layout.manager_billing_detail_head, null));
		String dateStr;
		try {
			dateStr = Format.DateStr.getYearMonthDayWeek(context,Format.DateStr
					.strToDate(year + month + day + "000000"));
		} catch (ParseException e) {
			dateStr = month + getText(R.string.month) + day
					+ getText(R.string.day);
		}

		boolean isShowShortCutButton = false;
		if (intent.hasExtra("com.cnlaunch.mycar.manager.showShortCutButton")) {
			isShowShortCutButton = true;
		}

		// 如果是新添账目后跳转到该页,则显示"继续新增"及"退出软件"这两个按钮
		if (isShowShortCutButton) {
			// 将时间显示在标题栏右侧
//			setCustomeTitleRight(dateStr);
		    setCustomeTitleRight("");
			findViewById(R.id.manager_billing_detail_title_layout).setVisibility(
					View.GONE);
			// 绑定按钮
			Button button_add_continue = (Button) findViewById(R.id.manager_billing_add_continue);
			Button button_close = (Button) findViewById(R.id.manager_billing_close);

			// 绑定事件
			button_add_continue
					.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							backAdd();
						}
					});
			button_close.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					close();
				}
			});
		} else {
			LinearLayout shortcut_buttons = (LinearLayout) findViewById(R.id.manager_billing_shortcut_buttons);
			shortcut_buttons.setVisibility(View.GONE);
			TextView textView = (TextView) findViewById(R.id.manager_billing_detail_title_textview);
			textView.setVisibility(View.VISIBLE);
			textView.setText(dateStr);
			setCustomeTitleRight("");
		}

		// 生成动态数组，并且转载数据
		mylist = new ArrayList<HashMap<String, String>>();

		// 从数据据读取列表
		accountList = (new AccountBll(this, getHelper())).getDataForDay(year,
				month, day);

		if (accountList != null) {
			for (Account account : accountList) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("detail_category", account.getCategory());
				map.put("detail_amount", com.cnlaunch.mycar.common.utils.StringUtil
						.getCurrency(context)
						+ account.getAmountStr());
				map.put("detail_time", account.getTimeStr());
				mylist.add(map);
			}

			adapter = new SimpleAdapter(this,
					mylist,
					R.layout.manager_billing_detail_item,
					new String[] { "detail_category", "detail_amount",
							"detail_time" },
					new int[] { R.id.manager_billing_detail_category, R.id.manager_billing_detail_amount,
							R.id.manager_billing_detail_time });
			// 添加并且显示
			detail_list.setAdapter(adapter);

			// 为listview绑定点击事件处理
			detail_list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					// position==0时,为listview的标题
					// position==accountList.size()+1时,为listview的结尾
					if (position > 0 && position <= accountList.size()) {
						dialogEditDel(accountList.get((int) id), position);
					}
				}
			});

		}

		// 加列表尾
		detail_list.addFooterView(LayoutInflater.from(this).inflate(
				R.layout.manager_billing_detail_foot, null));
		updateTotalAmount(accountList);

	}

	/**计算总金额
	 * @param accountList 查詢結果列表
	 * @return 总金额
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

	/**弹出是否要编辑或删除
	 * @param _account 账目对象
	 * @param _position 在列表中的位置
	 */
	private void dialogEditDel(Account _account, int _position) {
		// 资源
		final Account account = _account;
		final int position = _position;

		Resources r = getResources();
		String[] mItems = { r.getString(R.string.manager_edit),
				r.getString(R.string.manager_delete),
				r.getString(R.string.manager_cancel) };
		CustomDialog customDialog = new CustomDialog(this);
		customDialog.setTitle(r.getString(R.string.manager_op_account));
		customDialog.setItems(mItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					showEdit(account);
				} else if (which == 1) {
					deleteAccount(account);
					updateListViewAfterDeleteAccount(position);
					updateTotalAmount(accountList);
				}
			}
		});
		customDialog.show();
	}

	/**转到修改账目页面
	 * @param account 账目对象
	 */
	private void showEdit(Account account) {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Edit.id", account.getDaoId());
		intent.putExtra("com.cnlaunch.mycar.manager.Edit", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		startActivityForResult(intent, 0);
	}

	/**删除账目
	 * @param account 账目对象
	 */
	private void deleteAccount(Account account) {
		(new AccountBll(this, getHelper()))
				.setDelFlag(account.getDaoId());
		(new AccountBll(this, getHelper())).setSyncFlag(false,
				account.getDaoId());
	}


	/**删除后更新列表及总计值
	 * @param deleteItemPosition 在列表中的位置
	 */
	private void updateListViewAfterDeleteAccount(int deleteItemPosition) {
		mylist.remove(deleteItemPosition - 1);// 因为position==0处是头,position以1开始,比mylist中的序号大1
		accountList.remove(deleteItemPosition - 1);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 转到继续添加
	 */
	private void backAdd() {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.AddContinue", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		BillingDetailActivity.this.setResult(RESULT_OK, intent);
		BillingDetailActivity.this.finish();
	}

	/**
	 * 关闭软件
	 */
	private void close() {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Exit", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		BillingDetailActivity.this.setResult(RESULT_OK, intent);
		BillingDetailActivity.this.finish();
	}

	/**更新总金额
	 * @param accountList 查询结果列表
	 */
	private void updateTotalAmount(List<Account> accountList) {
		TextView detail_head_textview_amount = (TextView) findViewById(R.id.manager_detail_foot_textview_amount);
		detail_head_textview_amount
				.setText(com.cnlaunch.mycar.common.utils.StringUtil
						.getCurrency(context)
						+ Format.doubleToCommercialString(getTotal(accountList)));
	}
}