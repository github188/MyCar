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
	 * ����ʱˢ���б�
	 */
	@Override
	public void onRestart() {
		super.onRestart();
		refreshListView(year, month, day);
		updateTotalAmount(accountList);
	}

	/**��ȡ���ݿ�����,������mylist,accountList,�ٸ���listview
	 * @param year
	 * @param month
	 * @param day
	 */
	private void refreshListView(String year, String month, String day) {
		// ���ɶ�̬���飬����ת������
		mylist.clear();
		accountList.clear();

		// �����ݾݶ�ȡ�б�
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

		// Ϊ�ı���ǩ����ʱ���׺
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

		// ��listview����
		detail_list = (ListView) findViewById(R.id.manager_billing_detail_list);


		// ���б�ͷ
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

		// �����������Ŀ����ת����ҳ,����ʾ"��������"��"�˳����"��������ť
		if (isShowShortCutButton) {
			// ��ʱ����ʾ�ڱ������Ҳ�
//			setCustomeTitleRight(dateStr);
		    setCustomeTitleRight("");
			findViewById(R.id.manager_billing_detail_title_layout).setVisibility(
					View.GONE);
			// �󶨰�ť
			Button button_add_continue = (Button) findViewById(R.id.manager_billing_add_continue);
			Button button_close = (Button) findViewById(R.id.manager_billing_close);

			// ���¼�
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

		// ���ɶ�̬���飬����ת������
		mylist = new ArrayList<HashMap<String, String>>();

		// �����ݾݶ�ȡ�б�
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
			// ��Ӳ�����ʾ
			detail_list.setAdapter(adapter);

			// Ϊlistview�󶨵���¼�����
			detail_list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					// position==0ʱ,Ϊlistview�ı���
					// position==accountList.size()+1ʱ,Ϊlistview�Ľ�β
					if (position > 0 && position <= accountList.size()) {
						dialogEditDel(accountList.get((int) id), position);
					}
				}
			});

		}

		// ���б�β
		detail_list.addFooterView(LayoutInflater.from(this).inflate(
				R.layout.manager_billing_detail_foot, null));
		updateTotalAmount(accountList);

	}

	/**�����ܽ��
	 * @param accountList ��ԃ�Y���б�
	 * @return �ܽ��
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

	/**�����Ƿ�Ҫ�༭��ɾ��
	 * @param _account ��Ŀ����
	 * @param _position ���б��е�λ��
	 */
	private void dialogEditDel(Account _account, int _position) {
		// ��Դ
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

	/**ת���޸���Ŀҳ��
	 * @param account ��Ŀ����
	 */
	private void showEdit(Account account) {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Edit.id", account.getDaoId());
		intent.putExtra("com.cnlaunch.mycar.manager.Edit", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		startActivityForResult(intent, 0);
	}

	/**ɾ����Ŀ
	 * @param account ��Ŀ����
	 */
	private void deleteAccount(Account account) {
		(new AccountBll(this, getHelper()))
				.setDelFlag(account.getDaoId());
		(new AccountBll(this, getHelper())).setSyncFlag(false,
				account.getDaoId());
	}


	/**ɾ��������б��ܼ�ֵ
	 * @param deleteItemPosition ���б��е�λ��
	 */
	private void updateListViewAfterDeleteAccount(int deleteItemPosition) {
		mylist.remove(deleteItemPosition - 1);// ��Ϊposition==0����ͷ,position��1��ʼ,��mylist�е���Ŵ�1
		accountList.remove(deleteItemPosition - 1);
		adapter.notifyDataSetChanged();
	}

	/**
	 * ת���������
	 */
	private void backAdd() {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.AddContinue", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		BillingDetailActivity.this.setResult(RESULT_OK, intent);
		BillingDetailActivity.this.finish();
	}

	/**
	 * �ر����
	 */
	private void close() {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Exit", "yes");
		intent.setClass(BillingDetailActivity.this, BillingAddActivity.class);
		BillingDetailActivity.this.setResult(RESULT_OK, intent);
		BillingDetailActivity.this.finish();
	}

	/**�����ܽ��
	 * @param accountList ��ѯ����б�
	 */
	private void updateTotalAmount(List<Account> accountList) {
		TextView detail_head_textview_amount = (TextView) findViewById(R.id.manager_detail_foot_textview_amount);
		detail_head_textview_amount
				.setText(com.cnlaunch.mycar.common.utils.StringUtil
						.getCurrency(context)
						+ Format.doubleToCommercialString(getTotal(accountList)));
	}
}