package com.cnlaunch.mycar.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.UserSession;
import com.cnlaunch.mycar.manager.bll.AccountBll;
import com.cnlaunch.mycar.manager.bll.CategoryBll;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.database.Account;
import com.cnlaunch.mycar.manager.database.Category;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;

/**
 * @author xuzhuowei
 * 
 */
public class BillingAddActivity extends BaseActivity {
	private PopupWindow pop_keyboard;
	private View viewKeyBoard;
	private String amountValue = "";
	private String categoryValue = "";
	private Date expenseTime;
	private EditText edittext_amount;
	private EditText edittext_category;
	private EditText edittext_expense_time;
	private ImageView imageview_arrow_category;
	private ImageView imageview_arrow_amount;
	private GridView gridveiw_button_area;

	private Resources r;
	private Context context;

	@Override
	public void onBackPressed() {
		// 如果当前显示为数字键盘，则仅将其关闭
		if (edittext_amount.isFocused()) {
			setCategroyFocused();
			return;
		} else {
			super.onBackPressed();
		}

	}

	/*
	 * 处理详情页返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		switch (resultCode) {
		case RESULT_OK:
			if (intent.hasExtra("com.cnlaunch.mycar.manager.AddContinue")) {
				this.amountValue = "";
				this.categoryValue = "";
				this.expenseTime = new Date();
				freshEditView();
				edittext_category.requestFocus();
			}
			if (intent.hasExtra("com.cnlaunch.mycar.manager.Exit")) {
				BillingAddActivity.this.finish();
			}
			if (intent
					.hasExtra("com.cnlaunch.mycar.manager.OtherCategoryChoosed")) {
				this.categoryValue = intent
						.getStringExtra("com.cnlaunch.mycar.manager.OtherCategoryChoosed");
				if (this.categoryValue != null
						&& this.categoryValue.length() > 0) {
					setCategroy(this.categoryValue);
				}
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pop_keyboard != null) {
			pop_keyboard.dismiss();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// 如果是修改,则为自动填充表文本框,并修改按钮文字为"修改"
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		if (intent.hasExtra("com.cnlaunch.mycar.manager.Edit")) {
			AccountBll accountBll = new AccountBll(BillingAddActivity.this,
					getHelper());
			Account account = accountBll.find(intent.getIntExtra(
					"com.cnlaunch.mycar.manager.Edit.id", 0));

			if (account != null) {
				Button button_save = (Button) findViewById(R.id.manager_button_save);
				this.categoryValue = account.getCategory();
				this.amountValue = Format
						.doubleToCommercialNoSeparatorNoEndZeroString(account
								.getAmount());
				this.expenseTime = account.getExpenseTime();

				freshEditView();
				button_save.setText(r.getString(R.string.manager_editsave));
				setCustomeTitleLeft(R.string.billing_edit);
				setCustomeTitleRight(Format.DateStr.getYearMonthDayWeek(this,
						account.getExpenseTime()));
			} else {
				Toast.makeText(this, R.string.manager_err_db_readnull,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			clearActivity();
			setCategroyFocused();
		}
		showButtonArea();
		showBudgetNotify();
	}

	/**
	 * 显示预算超支提醒
	 */
	private void showBudgetNotify() {
		TextView manager_billing_notify = (TextView) findViewById(R.id.manager_billing_notify);
		Integer total = (new AccountBll(this, getHelper())).getMonthTotal();
		String strBudget = (new ManagerSettingBll(this, getHelper()))
				.find(ManagerSettingNames.budget.toString());
		Integer budget = 0;
		if (strBudget != null && strBudget.length() > 0) {
			budget = Integer.parseInt(strBudget);
		}
		if (budget != 0) {
			// 超预算90%则提醒
			if (total > budget * 9 / 10) {
				manager_billing_notify.setVisibility(View.VISIBLE);
				manager_billing_notify.setText(r
						.getString(R.string.item_budget_detail)
						.replace("[BUDGET]", strBudget)
						.replace("[TOTAL]", total.toString()));
			} else {
				manager_billing_notify.setVisibility(View.GONE);
			}
		} else {
			manager_billing_notify.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!UserSession.IsSomeoneLogined()) {
			UserSession.jumpToLoginWithToast(this);
			this.finish();
		}
		context = this;
		setContentView(R.layout.manager_billing_add, R.layout.custom_title);
		setCustomeTitleLeft(R.string.billing_add);
		setCustomeTitleRight("");

		r = this.getResources();

		edittext_amount = ((EditText) findViewById(R.id.manager_edittext_amount));
		edittext_category = ((EditText) findViewById(R.id.manager_edittext_category));
		edittext_expense_time = ((EditText) findViewById(R.id.manager_edittext_expense_time));

		imageview_arrow_category = ((ImageView) findViewById(R.id.imageview_arrow_category));
		imageview_arrow_amount = ((ImageView) findViewById(R.id.imageview_arrow_amount));

		// 初始自定义键盘
		viewKeyBoard = this.getLayoutInflater().inflate(
				R.layout.manager_keyboard_number, null);
		pop_keyboard = new PopupWindow(viewKeyBoard,
				Env.getScreenWidth(BillingAddActivity.this),
				getKeyboardHeight());

		// 绑定失焦事件,当"数值"文本框失去焦点时,关闭输入法;获得焦点时,将里面的值清空
		edittext_amount
				.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						hideIME();
						if (!hasFocus) {
							hideCustomKeyboard();
							imageview_arrow_amount
									.setVisibility(View.INVISIBLE);
							imageview_arrow_category
									.setVisibility(View.VISIBLE);
						} else {
							BillingAddActivity.this.amountValue = "";
							freshEditView();
							showCustomKeyboard();
							imageview_arrow_amount.setVisibility(View.VISIBLE);
							imageview_arrow_category
									.setVisibility(View.INVISIBLE);
						}
					}
				});
		// 绑定"支出类别"时间选择器
		edittext_expense_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePicker(BillingAddActivity.this.expenseTime);
			}
		});
	}

	private int getKeyboardHeight() {
		LinearLayout input_area = (LinearLayout) findViewById(R.id.manager_input_area);
		View menu_area = findViewById(R.id.manager_billing_menu_area);

		return Env.getScreenWidth(this)
		// 屏幕高度,输入区域高度,菜单高度
				- input_area.getHeight() - menu_area.getHeight()
				// 标题高度
				- R.dimen.window_title_size;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideIME() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edittext_amount.getWindowToken(), 0);
	}

	/**
	 * 显示自定义键盘
	 */
	private void showCustomKeyboard() {
		pop_keyboard.showAsDropDown(findViewById(R.id.manager_input_area));
		gridveiw_button_area.setVisibility(View.INVISIBLE);
	}

	/**
	 * 隐藏自定义键盘
	 */
	private void hideCustomKeyboard() {
		pop_keyboard.dismiss();
		gridveiw_button_area.setVisibility(View.VISIBLE);
	}

	protected void freshEditView() {
		edittext_amount.setText(this.amountValue);
		edittext_category.setText(this.categoryValue);
		edittext_expense_time.setText(Format.DateStr
				.getDateTime(this.expenseTime));
	}

	/**
	 * 检查表单数据有效性
	 * 
	 * @return false表单数据填写有误
	 */
	private boolean checkEditTextValue() {
		final Resources r = getResources();

		if (this.categoryValue.trim().length() == 0) {
			displayToast(r.getString(R.string.manager_err_null_category));
			return false;
		}

		if (this.amountValue.trim().length() == 0) {
			displayToast(r.getString(R.string.manager_err_null_amount));
			return false;
		}

		final Float minAmount = new Float(0.001);
		try {
			if (Float.valueOf(this.amountValue.trim()) < minAmount) {
				displayToast(r.getString(R.string.manager_err_invalid_amount));
				return false;
			}
		} catch (Exception e) {
			displayToast(r.getString(R.string.manager_err_invalid_amount));
			return false;
		}

		return true;
	}

	/**
	 * 保存账目
	 */
	private void saveData() {
		Intent intent = getIntent();
		String strCategory = this.categoryValue.trim();
		String strAmount = this.amountValue.trim();

		AccountBll accountBll = new AccountBll(BillingAddActivity.this,
				getHelper());
		String strCategoryId = "";
		Map<String,String> categroyMap = (new CategoryBll(BillingAddActivity.this,
				getHelper())).getAllLocalCategory(BillingAddActivity.this);
		boolean flag = true;
		//判断是否是自定义的类别还是本地的
		if(categroyMap.containsKey(strCategory)){
			strCategoryId = categroyMap.get(strCategory);
			flag = false;
		}
		else{
			strCategoryId = strCategory;
		}
		if (intent.hasExtra("com.cnlaunch.mycar.manager.Edit")) {
			Account account = accountBll.find(intent.getIntExtra(
					"com.cnlaunch.mycar.manager.Edit.id", 0));
			if (account != null) {
				account.setCategory(strCategory);
				account.setAmount(strAmount);
				account.setCurrentLanguage(Env.GetCurrentLanguage().trim());
				account.setCategoryId(strCategoryId);
				accountBll.update(account);
			} else {
				displayToast(R.string.manager_err_db_readnull);
			}
		} else {
			String remark = "";
			String id = UUID.randomUUID().toString().replace("-", "");
			Account account = new Account();
			account.setAmount(strAmount);
			account.setCategoryId(strCategoryId);
			account.setCategory(strCategory);
			account.setId(id);
			account.setExpenseTime(this.expenseTime);
			account.setRemark(remark);
			account.setCurrentLanguage(Env.GetCurrentLanguage().trim());
			account.setLastOperate(AccountBll.LAST_OPERATE_ADD);
			account.setSyncFlag(AccountBll.SYNC_FLAG_NO);
			accountBll.save(account);
		}
		if(flag)
		   increaseOrderid(strCategory);
	}

	/**
	 * 添加自定义类别
	 * 
	 * @param strCategory
	 *            自定义类别
	 */
	private void saveCustomCategory(String strCategory) {
		CategoryBll categoryBll = new CategoryBll(BillingAddActivity.this,
				getHelper());
		Category category = new Category();
		category.setCategory(strCategory);
		category.setCategoryId(strCategory);
		category.setOrderId(0);
		category.setType("2");
		category.setCurrentLanguage(Env.GetCurrentLanguage().trim());
		categoryBll.save(category);
	}

	/**
	 * 判断是否是系统类别
	 * @param strCategory
	 * @return
	 */
	private boolean isSysCategory(String strCategory) {
		return new CategoryBll(BillingAddActivity.this, getHelper())
				.isSystemCategory(context, strCategory);
	}

	/**
	 * 将类别选中过的类别,排序值+1
	 * 
	 * @param strCategory
	 *            类别名称
	 */
	private void increaseOrderid(String strCategory) {
		CategoryBll categoryBll = new CategoryBll(BillingAddActivity.this,
				getHelper());
		Category category = categoryBll.find(strCategory);
		if (category != null) {
			category.setOrderId(category.getOrderId() + 1);
			category.setCurrentLanguage(Env.GetCurrentLanguage().trim());
			categoryBll.update(category);
		} else {
			saveCustomCategory(strCategory);
		}
	}

	/**
	 * 响应类别按钮点击事件
	 * 
	 * @param v
	 */
	public void CategoryButton_ClickHandler(View v) {
		Resources r = BillingAddActivity.this.getResources();
		switch (v.getId()) {
		case R.id.manager_button_save:
			hideIME();
			if (checkEditTextValue()) {
				saveData();
				displayToast(r.getString(R.string.manager_saved));
				showDetail();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到当日账目详情页
	 */
	private void showDetail() {
		Intent intent = new Intent();
		Intent intentPassIn = getIntent();
		if (intentPassIn.hasExtra("com.cnlaunch.mycar.manager.Edit")) {
			BillingAddActivity.this.finish();
		} else {
			intent.putExtra("com.cnlaunch.mycar.manager.showShortCutButton",
					"yes");
			intent.setClass(BillingAddActivity.this,
					BillingDetailActivity.class);
			startActivityForResult(intent, 0);
			// overridePendingTransition(0,0);
		}

	}

	/**
	 * 响应自定义数据键盘点击事件
	 * 
	 * @param v
	 */
	public void CustomKeyboardButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.keyboard_num_0:
			setInputAdd("0");
			break;
		case R.id.keyboard_num_1:
			setInputAdd("1");
			break;
		case R.id.keyboard_num_2:
			setInputAdd("2");
			break;
		case R.id.keyboard_num_3:
			setInputAdd("3");
			break;
		case R.id.keyboard_num_4:
			setInputAdd("4");
			break;
		case R.id.keyboard_num_5:
			setInputAdd("5");
			break;
		case R.id.keyboard_num_6:
			setInputAdd("6");
			break;
		case R.id.keyboard_num_7:
			setInputAdd("7");
			break;
		case R.id.keyboard_num_8:
			setInputAdd("8");
			break;
		case R.id.keyboard_num_9:
			setInputAdd("9");
			break;
		case R.id.keyboard_num_point:
			setInputPoint();
			break;
		case R.id.keyboard_num_del:
			setInputDel();
			break;
		default:
			break;
		}
	}

	/**
	 * 判断当前输入到“金额”中的值，是否有效，是则写入表单，否则忽略输入
	 * 
	 * @param i
	 *            输入值
	 */
	private void setInputAdd(String i) {
		String text = this.amountValue;
		final int MaxLengthTotal = 7;
		final int MaxLengthAfterPoint = 2;
		if (text.equals("0") || text.length() == 0) {
			text = i;
		} else {
			if (text.length() < MaxLengthTotal) {
				if (text.indexOf(".") == -1
						|| (text.indexOf(".") != -1 && text.length()
								- (text.indexOf(".") + 1) < MaxLengthAfterPoint)) {
					text += i;
				}
			}
		}
		this.amountValue = text;
		freshEditView();
		edittext_amount.setSelection(text.length());
	}

	/**
	 * 判断当前输入到“金额”中的小数点，是否有效，是则写入表单，否则忽略输入
	 */
	private void setInputPoint() {
		String text = this.amountValue;
		final int MaxLengthTotal = 7;
		if (text.length() >= MaxLengthTotal) {
			return;
		}
		if (text.length() == 0) {
			text = ".";
		} else {
			if (text.indexOf(".") == -1) {
				if (text.length() > 0) {
					text += ".";
				}
			}
		}
		this.amountValue = text;
		freshEditView();
		edittext_amount.setSelection(text.length());
	}

	/**
	 * 判断当前输入到“金额”中的删除动作，是否有效，是则删除末尾的字符，否则忽略输入
	 */
	private void setInputDel() {
		String text = this.amountValue;
		if (text.length() > 0) {
			text = text.substring(0, text.length() - 1);
		}
		this.amountValue = text;
		freshEditView();
		edittext_amount.setSelection(text.length());
	}

	/**
	 * 清理表单数据
	 */
	private void clearActivity() {
		this.expenseTime = new Date();
		this.amountValue = "";
		this.categoryValue = "";
		freshEditView();
	}

	/**
	 * 使"类型"文本框获得焦点
	 */
	private void setCategroyFocused() {
		edittext_category.requestFocus();
	}

	/**
	 * 组织并显示按钮区域，并进行事件处理函数绑定
	 */
	private void showButtonArea() {
		final int DEFAULT_CATEGORY_COUNT = 11;// 系统默认的类别数量
		Resources r = getResources();
		// 绑定按钮所在gridview
		gridveiw_button_area = (GridView) findViewById(R.id.manager_gridview_button_area);

		List<Category> list = (new CategoryBll(BillingAddActivity.this,
				getHelper())).getSystemCategory(context);
		ArrayList<HashMap<String, Object>> list_button_value = new ArrayList<HashMap<String, Object>>();

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (i == DEFAULT_CATEGORY_COUNT) {
					break;
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemValue", list.get(i).getCategory());
				map.put("ItemText", list.get(i).getCategory());
				map.put("type", list.get(i).getType());
				list_button_value.add(map);
			}
		}
		// 其它按钮
		HashMap<String, Object> mapOther = new HashMap<String, Object>();
		mapOther.put("ItemValue", "manager_custom");
		mapOther.put("ItemText", r.getString(R.string.manager_custom));
		mapOther.put("type", "-1");
		list_button_value.add(mapOther);

		SimpleAdapter adapter = new SimpleAdapter(this, list_button_value,
				R.layout.manager_billing_add_gridview_item,
				new String[] { "ItemText" },
				new int[] { R.id.manager_gridview_button }) {

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				@SuppressWarnings("unchecked")
				final HashMap<String, Object> map = (HashMap<String, Object>) this
						.getItem(position);
				Button button = (Button) view
						.findViewById(R.id.manager_gridview_button);
				if(Env.GetCurrentLanguage().trim().equals("EN")){
					button.setTextSize(12);
				}
				// 为类别按钮设置背景图片
				button.setBackgroundResource(getCategoryButtonBg((String) map
						.get("ItemValue")));

				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String value = map.get("ItemValue").toString();
						if (value.equals("manager_custom")) {
							showCustomCategory();
						} else {
							setCategroy(value);
						}
					}

				});
				return view;
			}

		};
		// 添加并且显示
		gridveiw_button_area.setAdapter(adapter);
	}

	/**
	 * 为类别按钮选择背景图片
	 * 
	 * @param category
	 *            类别名称
	 * @return 图片资源ID
	 */
	protected int getCategoryButtonBg(String category) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("system_custom",
				R.drawable.manager_billing_button_system_custom);
		map.put("保险", R.drawable.manager_billing_button_baoxian);
		map.put("罚款", R.drawable.manager_billing_button_fakuan);
		map.put("购置", R.drawable.manager_billing_button_gouzhi);
		map.put("规费", R.drawable.manager_billing_button_guifei);
		map.put("加油", R.drawable.manager_billing_button_jiayou);
		map.put("美容", R.drawable.manager_billing_button_meirong);
		map.put("停车", R.drawable.manager_billing_button_tingche);
		map.put("通行", R.drawable.manager_billing_button_tongxing);
		map.put("维修", R.drawable.manager_billing_button_weixiu);
		map.put("保养", R.drawable.manager_billing_button_baoyang);

		map.put("Insur", R.drawable.manager_billing_button_baoxian);
		map.put("Penalty", R.drawable.manager_billing_button_fakuan);
		map.put("Pur", R.drawable.manager_billing_button_gouzhi);
		map.put("Fees", R.drawable.manager_billing_button_guifei);
		map.put("Gas", R.drawable.manager_billing_button_jiayou);
		map.put("Beauty", R.drawable.manager_billing_button_meirong);
		map.put("Parking", R.drawable.manager_billing_button_tingche);
		map.put("Commu", R.drawable.manager_billing_button_tongxing);
		map.put("Repair", R.drawable.manager_billing_button_weixiu);
		map.put("Mtnce", R.drawable.manager_billing_button_baoyang);

		Integer ret = map.get(category);
		if (ret == null) {
			ret = R.drawable.manager_billing_button_system_custom;
		}
		return ret;
	}

	/**
	 * 显示自定义类别
	 */
	private void showCustomCategory() {
		final Resources r = getResources();
		List<Category> list = (new CategoryBll(BillingAddActivity.this,
				getHelper())).getCustomCategory(context);
		int len = list.size();
		String[] mItems = new String[len];
		for (int i = 0; i < len; i++) {
			mItems[i] = list.get(i).getCategory();
		}

		if (mItems.length == 0) {
			displayToast(r.getString(R.string.manager_no_other_category));
			return;
		}

		Intent intent = new Intent(BillingAddActivity.this,
				DialogOtherCategoryActivity.class);
		intent.putExtra("com.cnlaunch.mycar.manager.CustomCategorys", mItems);
		startActivityForResult(intent, 0);
	}

	/**
	 * 显示时间选择器
	 * 
	 * @param dateStr
	 *            初始时间字符串
	 */
	protected void showDatePicker(final String dateStr) {
		try {
			showDatePicker(Format.DateStr.strToDate("yyyy-MM-dd HH:mm:ss",
					dateStr));
		} catch (ParseException e) {
			showDatePicker(new Date());
		}
	}

	/**
	 * 显示时间选择器
	 * 
	 * @param date
	 *            初始时间
	 */
	protected void showDatePicker(final Date date) {
		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);

		customAlertDialog.setTitle(R.string.date_picker);
		final DatePicker datePicker = new DatePicker(this);

		datePicker.init(date.getYear() + 1900, date.getMonth(), date.getDate(),
				null);
		datePicker.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		customAlertDialog.setView(datePicker);

		customAlertDialog.setPositiveButton(R.string.manager_ensure,
				new OnClickListener() {

					public void onClick(View view) {

						// 验证控件中的日期是否合法
						Date chooseDate = new Date(datePicker.getYear() - 1900,
								0, 0);
						if (datePicker.getYear() < 1900
								&& datePicker.getYear() > 9999) {
							displayToast(R.string.date_picker_year_error);
							return;
						}

						if (datePicker.getMonth() < 1
								&& datePicker.getMonth() > 12) {
							displayToast(R.string.date_picker_month_error);
							return;
						}
						chooseDate.setMonth(datePicker.getMonth());
						Calendar c = Calendar.getInstance();
						c.set(chooseDate.getYear(), chooseDate.getMonth(), 1);
						int max = c.getActualMaximum(Calendar.DATE);

						if (datePicker.getDayOfMonth() < 0
								&& datePicker.getDayOfMonth() > max) {
							displayToast(R.string.date_picker_day_error);
							return;
						}

						Date rDate = date;
						rDate.setYear(datePicker.getYear() - 1900);
						rDate.setMonth(datePicker.getMonth());
						rDate.setDate(datePicker.getDayOfMonth());
						setExpenseTime(date);
						customAlertDialog.dismiss();
					}
				});
		customAlertDialog.show();

	}

	/**
	 * 修改表单中的消费时间
	 * 
	 * @param date
	 */
	private void setExpenseTime(Date date) {
		this.expenseTime = date;
		freshEditView();
	}

	/**
	 * 修改表单中的支出类别
	 * 
	 * @param str
	 */
	private void setCategroy(String str) {
		this.categoryValue = str;
		freshEditView();
		setAmmountFocused();
	}

	/**
	 * "金额"表单值为空,则使"金额"文本框获得焦点
	 */
	private void setAmmountFocused() {
		if (this.amountValue.length() == 0) {
			edittext_amount.setFocusable(true);
			edittext_amount.requestFocus();
		}
	}

	/**
	 * 响应底部菜单按钮事件
	 * 
	 * @param v
	 */
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.manager_oil_textbutton_menu_back_main:
			backMain(BillingAddActivity.this);
			break;
		case R.id.manager_textbutton_menu_detail:
			showDetail();
			break;
		case R.id.manager_textbutton_menu_setting:
			showSetting();
			break;
		case R.id.manager_textbutton_menu_statistics:
			showMonthList();
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到加油页
	 */
	private void showOilAdd() {
		Intent intent = new Intent();
		intent.setClass(BillingAddActivity.this, OilAddActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转到设置页
	 */
	private void showSetting() {
		Intent intent = new Intent();
		intent.setClass(BillingAddActivity.this, UserSettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转到统计页
	 */
	private void showMonthList() {
		Intent intent = new Intent();
		intent.setClass(BillingAddActivity.this,
				BillingStatisticsMonthListActivity.class);
		startActivity(intent);
	}

}