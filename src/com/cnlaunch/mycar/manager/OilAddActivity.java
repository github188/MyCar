package com.cnlaunch.mycar.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.bll.UserCarBll;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.database.UserCar;

public class OilAddActivity extends BaseActivity {
	private Resources r;
	private Oil oil;
	private List<UserCar> userCarList;
	private String[] userCars;
	private String[] oilTypes;
	private UserCarBll userCarBll;

	@Override
	public void onStart() {
		super.onStart();
		
		// 如果是修改,则为自动填充表文本框,并修改按钮文字为"修改"
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		if (intent.hasExtra("com.cnlaunch.mycar.manager.Edit")) {
			OilBll oilBll = new OilBll(OilAddActivity.this, getHelper());
			oil = oilBll.find(intent.getIntExtra(
					"com.cnlaunch.mycar.manager.Edit.id", 0));

			if (oil != null) {
				Button button_save = (Button) findViewById(R.id.manager_oil_save);
				button_save
						.setText(r.getString(R.string.manager_editsave_full));
			} else {
				displayToast(R.string.manager_err_db_readnull);
				return;
			}
		} else {
			if (oil == null) {
				// 初使值
				oil = new Oil();
				oil.setExpenseTime(new Date());
				if (userCarList != null && userCarList.size() != 0) {
					oil.setUserCarId(userCarList.get(0).getUserCarId());
				}
				if (oilTypes != null && oilTypes.length > 0) {
					String lastOilType = (new ManagerSettingBll(this,
							getHelper())).find(ManagerSettingNames.lastOilType
							.toString());
	
					if (lastOilType != null) {
						oil.setOilType(lastOilType);
					} else {
						oil.setOilType(oilTypes[0]);
					}
				}
				((EditText) findViewById(R.id.edittext_manager_oil_price))
						.setText((new ManagerSettingBll(this, getHelper()))
								.find(ManagerSettingNames.oilPrice.toString()));

			}
		}
		freshEditView();
	}

	private void freshEditView() {
		EditText edittext_manager_oil_amount = (EditText) findViewById(R.id.edittext_manager_oil_amount);
		EditText edittext_manager_oil_price = (EditText) findViewById(R.id.edittext_manager_oil_price);
		EditText edittext_manager_oil_mileage = (EditText) findViewById(R.id.edittext_manager_oil_mileage);
		EditText edittext_manager_oil_expense_time = (EditText) findViewById(R.id.edittext_manager_oil_expense_time);
		EditText edittext_manager_oil_remark = (EditText) findViewById(R.id.edittext_manager_oil_remark);

		if ((int) oil.getAmount() != 0) {
			edittext_manager_oil_amount
					.setText(String.valueOf(oil.getAmount()));
		} else {
			edittext_manager_oil_amount.setText("");
		}

		if ((int) oil.getQuantity() != 0) {
			edittext_manager_oil_price.setText(Format
					.doubleToCommercialNoSeparatorNoEndZeroString(oil
							.getAmount() / oil.getQuantity()));
		} else {
			edittext_manager_oil_amount.setText("");
		}

		if (oil.getMileage() != 0) {
			edittext_manager_oil_mileage.setText(String.valueOf(oil
					.getMileage()));
		} else {
			edittext_manager_oil_amount.setText("");
		}
		edittext_manager_oil_expense_time.setText(Format.DateStr.getDate(oil
				.getExpenseTime()));
		edittext_manager_oil_remark.setText(oil.getRemark());

		// 车辆名称
		Spinner spinner_manager_oil_car_id = (Spinner) findViewById(R.id.spinner_manager_oil_car_id);
		ArrayAdapter<String> adapterCarId = new ArrayAdapter<String>(this,
				R.layout.spinner_textview, userCars);
		adapterCarId
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_manager_oil_car_id.setAdapter(adapterCarId);
		String userCarId = oil.getUserCarId();
		if (userCarId != null) {
			int len = userCarList.size();
			for (int i = 0; i < len; i++) {
				if (userCarId.equals(userCarList.get(i).getUserCarId())) {
					spinner_manager_oil_car_id.setSelection(i);
					break;
				}
			}
		} else {
			spinner_manager_oil_car_id.setSelection(0);
		}

		// 油品类型
		Spinner spinner_manager_oil_type = (Spinner) findViewById(R.id.spinner_manager_oil_type);
		ArrayAdapter<String> adapterOilType = new ArrayAdapter<String>(this,
				R.layout.spinner_textview, oilTypes);
		adapterOilType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_manager_oil_type.setAdapter(adapterOilType);

		String oilType = oil.getOilType();
		if (oilType != null) {
			int len = oilTypes.length;
			for (int i = 0; i < len; i++) {
				if (oilType.equals(oilTypes[i])) {
					spinner_manager_oil_type.setSelection(i);
					break;
				}
			}
		} else {
			spinner_manager_oil_type.setSelection(0);
		}

	}

	public static String getCurrentTime(String timeFormat)
    {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        Date d = new Date();
        return format.format(d);
    }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_oil_add, R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_oil_add);
//		setCustomeTitleRight(Format.DateStr.getMonthDayWeek());
		setCustomeTitleRight(getCurrentTime("yyyy-MM-dd HH:mm"));
		r = this.getResources();
		// changeToolbarIcon();

		userCarBll = new UserCarBll(OilAddActivity.this, getHelper());

		userCarList = userCarBll.getAllUserCar();
		if (userCarList == null) {
			displayToast(R.string.manager_err_db_readnull);
			return;
		}
		userCars = new String[userCarList.size()];
		for (int i = 0; i < userCarList.size(); i++) {
			userCars[i] = userCarList.get(i).getUserCarName();
		}
		oilTypes = (new ManagerSettingBll(OilAddActivity.this, getHelper()))
				.getAllLocalOilType(OilAddActivity.this);

		if (oilTypes == null) {
			displayToast(R.string.manager_err_db_readnull);
			return;
		}

		Button manager_oil_save = (Button) findViewById(R.id.manager_oil_save);
		manager_oil_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkEditTextValue()) {
					saveData();
					displayToast(r.getString(R.string.manager_saved));
					showStatistics();
				}
			}
		});

		// 修改加油日期
		final EditText edittext_manager_oil_expense_time = (EditText) findViewById(R.id.edittext_manager_oil_expense_time);
		edittext_manager_oil_expense_time
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showDatePicker(oil.getExpenseTime());
					}
				});

	}

	/**
	 * 显示时间选择器
	 * 
	 * @param date
	 *            初始日期
	 */
	protected void showDatePicker(Date date) {
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

						Date rDate = oil.getExpenseTime();
						rDate.setYear(datePicker.getYear() - 1900);
						rDate.setMonth(datePicker.getMonth());
						rDate.setDate(datePicker.getDayOfMonth());
						oil.setExpenseTime(rDate);
						showExpenseTime();
						customAlertDialog.dismiss();
					}
				});
		customAlertDialog.show();

	}

	/**
	 * 修改表单中的消费时间
	 */
	protected void showExpenseTime() {
		final EditText edittext_manager_oil_expense_time = (EditText) findViewById(R.id.edittext_manager_oil_expense_time);
		edittext_manager_oil_expense_time.setText(Format.DateStr.getDate(oil
				.getExpenseTime()));
	}

	/**
	 * 跳转到油耗分析页
	 */
	protected void showStatistics() {
		Intent intent = new Intent();
		Intent intentPassIn = getIntent();
		intent.setClass(OilAddActivity.this, OilDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * 保存表单数据到数据库
	 */
	protected void saveData() {
		if (getIntent().hasExtra("com.cnlaunch.mycar.manager.Edit")) {
			OilBll oilBll = new OilBll(OilAddActivity.this, getHelper());
			oilBll.update(oil);
		} else {
			Map<String,String> oilTypeMap = (new ManagerSettingBll(OilAddActivity.this,
					getHelper())).getAllLocalOilTypeByName(OilAddActivity.this);
			String oilTypeId = "";
			//根据文本查找到相关ID
			if(oilTypeMap.containsKey(oil.getOilType())){
				oilTypeId = oilTypeMap.get(oil.getOilType());
			}
			OilBll oilBll = new OilBll(OilAddActivity.this, getHelper());
			String id = UUID.randomUUID().toString().replace("-", "");
			oil.setId(id);
			oil.setLastOperate(OilBll.LAST_OPERATE_ADD);
			oil.setSyncFlag(OilBll.SYNC_FLAG_NO);			
			oil.setCurrentLanguage(Env.GetCurrentLanguage().trim());
			oil.setOilTypeId(oilTypeId);
			oilBll.save(oil);
		}
		oil = null;
	}

	/**
	 * 检查表单数据有效性,并将数据填充到 oil对象
	 * 
	 * @return
	 */
	private boolean checkEditTextValue() {

		float amount;
		float quantity;
		int mileage;

		// 车辆名称
		long carIdSelectedItemId = ((Spinner) findViewById(R.id.spinner_manager_oil_car_id))
				.getSelectedItemId();
		if (carIdSelectedItemId != android.widget.AdapterView.INVALID_ROW_ID) {
			if (userCars.length < carIdSelectedItemId) {
				displayToast(R.string.manager_err_car_id_null);
				return false;
			} else {
				oil.setUserCarId(userCarList.get((int) carIdSelectedItemId)
						.getUserCarId());
			}
		} else {
			displayToast(R.string.manager_err_car_id_null);
			return false;
		}

		// 加油金额
		try {
			amount = Float
					.parseFloat(((EditText) findViewById(R.id.edittext_manager_oil_amount))
							.getText().toString());
			oil.setAmount(((EditText) findViewById(R.id.edittext_manager_oil_amount))
					.getText().toString());
		} catch (NumberFormatException e) {
			displayToast(R.string.manager_err_oil_amount);
			return false;
		}

		// 加油量
		try {
			String priceStr = ((EditText) findViewById(R.id.edittext_manager_oil_price))
					.getText().toString();
			Float price = Float.parseFloat(priceStr);
			if (price > 0) {
				quantity = amount / price;
				oil.setQuantity(quantity);
			} else {
				displayToast(R.string.manager_err_oil_price);
				return false;
			}
			//将本次单价存数据库
			(new ManagerSettingBll(this, getHelper())).update(
					ManagerSettingNames.oilPrice.toString(), priceStr);

		} catch (NumberFormatException e) {
			displayToast(R.string.manager_err_oil_price);
			return false;
		}

		// 油品型号
		long oilTypeSelectedItemId = ((Spinner) findViewById(R.id.spinner_manager_oil_type))
				.getSelectedItemId();
		if (oilTypeSelectedItemId != android.widget.AdapterView.INVALID_ROW_ID) {
			if (oilTypes.length < oilTypeSelectedItemId) {
				displayToast(R.string.manager_err_oil_type_null);
				return false;
			} else {
				String oildType = oilTypes[(int) oilTypeSelectedItemId];
				oil.setOilType(oildType);
				//将本次油品存数据库
				(new ManagerSettingBll(this, getHelper())).update(
						ManagerSettingNames.lastOilType.toString(), oildType);
			}
		} else {
			displayToast(R.string.manager_err_oil_type_null);
			return false;
		}

		// 里程表读数
		try {
			mileage = Integer
					.parseInt(((EditText) findViewById(R.id.edittext_manager_oil_mileage))
							.getText().toString());
			oil.setMileage(mileage);
		} catch (NumberFormatException e) {
			displayToast(R.string.manager_err_oil_mileage);
			return false;
		}

		// 加油日期
		// 加油日期的值一直保存在oil对象中，无需验证

		// 备注
		oil.setRemark(((EditText) findViewById(R.id.edittext_manager_oil_remark))
				.getText().toString());

		return true;
	}

	/**
	 * 绑定底部菜单点击事件
	 * 
	 * @param v
	 */
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.manager_oil_textbutton_menu_back_main:
			backMain(OilAddActivity.this);
			break;
		case R.id.manager_oil_textbutton_menu_oil_add:
			// do Nothing
			break;
		case R.id.manager_oil_textbutton_menu_oil_detail:
			showOilDetail();
			break;
		case R.id.manager_oil_textbutton_menu_oil_chart:
			showOilChart();
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到油耗分析页
	 */

	private void showOilChart() {
		Intent intent = new Intent();
		intent.setClass(OilAddActivity.this, OilDetailBarChartActivity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(0, 0);
	}

	/**
	 * 跳转到加油记录页
	 */
	private void showOilDetail() {
		Intent intent = new Intent();
		intent.setClass(OilAddActivity.this, OilDetailActivity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(0, 0);
	}

}