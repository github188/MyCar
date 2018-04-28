package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.bll.UserCarBll;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.database.UserCar;

public class OilDetailActivity extends BaseActivity {
	private Resources r;
	private Context context;
	private Date endDate = new Date();
	private Date startDate = DateUtils.addMonths(endDate, -1);
	private String carId;

	private List<Oil> oilList;
	private ListView detail_list;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> mylist;

	private ImageView oil_detail_left;
	private ImageView oil_detail_right;

	private UserCarBll userCarBll;
	private List<UserCar> userCarList;
	private String[] userCars;
	private int average_quantity;

	// 返回时刷新列表
	@Override
	public void onRestart() {
		super.onRestart();
		refreshListView();
		updateStatisticsAmount();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_oil_detail, R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_toolbar_oil_chart);
		setCustomeTitleRight("");
		context = this;

		r = this.getResources();
		// changeToolbarIcon();

		// 车辆名称
		try
        {
            userCarBll = new UserCarBll(OilDetailActivity.this, getHelper());
            userCarList = userCarBll.getAllUserCar();

            userCars = new String[userCarList.size()];
            for (int i = 0; i < userCarList.size(); i++) {
            	userCars[i] = userCarList.get(i).getUserCarName();
            }

            final Spinner spinner_manager_oil_car_id = (Spinner) findViewById(R.id.spinner_manager_oil_car_id);
            ArrayAdapter<String> adapterCarId = new ArrayAdapter<String>(this,
            		R.layout.spinner_textview, userCars);
            adapterCarId
            		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_manager_oil_car_id.setAdapter(adapterCarId);

            carId = userCarList.get(0).getUserCarId();
            spinner_manager_oil_car_id.setSelection(0);

            spinner_manager_oil_car_id
            		.setOnItemSelectedListener(new OnItemSelectedListener() {

            			@Override
            			public void onItemSelected(AdapterView<?> arg0, View arg1,
            					int arg2, long arg3) {
            				if (mylist != null && oilList != null) {
            					carId = userCarList.get(arg2).getUserCarId();
            					refreshListView();
            					updateStatisticsAmount();
            				}
            			}

            			// 默认选中第一个
            			@Override
            			public void onNothingSelected(AdapterView<?> arg0) {
            				carId = userCarList.get(0).getUserCarId();
            				spinner_manager_oil_car_id.setSelection(0);
            				refreshListView();
            			}
            		});

            // 绑定listview容器
            detail_list = (ListView) findViewById(R.id.manager_oil_detail_list);

            // 生成动态数组，并且转载数据
            mylist = new ArrayList<HashMap<String, String>>();

            // 从数据据读取列表
            oilList = (new OilBll(this, getHelper())).getList(startDate, endDate,
            		carId);
    		Map<String,String> oilTypeMap = (new ManagerSettingBll(this,
    					getHelper())).getAllLocalOilTypeByID(this);
            if (oilList != null) {
            	for (Oil oil : oilList) {
            		HashMap<String, String> map = new HashMap<String, String>();
            		if(oilTypeMap.containsKey(oil.getOilTypeId())){
        				map.put("oil_type",oilTypeMap.get(oil.getOilTypeId()));
        			}else{
        				map.put("oil_type",oil.getOilType());
        			}    
            		map.put("amount",
            				StringUtil.getCurrency(context)
            						+ Format.doubleToCommercialNoSeparatorNoEndZeroString(oil
            								.getAmount()));
            		map.put("quantity",
            				Format.doubleToCommercialNoSeparatorNoEndZeroString(oil
            						.getQuantity()) + "L");
            		map.put("mileage", String.valueOf(oil.getMileage()) + "km");
            		if (oil.getExpenseTime() != null) {
            			map.put("datetime",
            					Format.DateStr.getDateTime(oil.getExpenseTime()));
            		} else {
            			map.put("datetime", "");
            		}
            		mylist.add(map);
            	}
            }

            // 生成适配器，数组===》ListItem
            adapter = new SimpleAdapter(this, mylist,
            		R.layout.manager_oil_detail_item, new String[] { "oil_type",
            				"amount", "quantity", "mileage", "datetime" },
            		new int[] { R.id.manager_oil_detail_oil_type,
            				R.id.manager_oil_detail_amount,
            				R.id.manager_oil_detail_quantity,
            				R.id.manager_oil_detail_mileage,
            				R.id.manager_oil_detail_datetime, });
            // 添加并且显示
            detail_list.setAdapter(adapter);

            // 为listview绑定点击事件处理
            detail_list.setOnItemClickListener(new OnItemClickListener() {
            	@Override
            	public void onItemClick(AdapterView<?> arg0, View arg1,
            			int position, long id) {
            		if (position < oilList.size()) {
            			dialogEditDel(oilList.get((int) id), position);
            		}
            	}
            });

            updateStatisticsAmount();

            final TextView textview_manager_oil_detail_startTime = (TextView) findViewById(R.id.textview_manager_oil_detail_startTime);
            final TextView textview_manager_oil_detail_endTime = (TextView) findViewById(R.id.textview_manager_oil_detail_endTime);
            findViewById(R.id.manager_oil_textbutton_menu_oil_detail)
            		.setBackgroundResource(R.drawable.manager_toolbar_bg_selected);

            textview_manager_oil_detail_startTime.setText(Format.DateStr
            		.getDate(startDate));
            textview_manager_oil_detail_endTime.setText(Format.DateStr
            		.getDate(endDate));

            textview_manager_oil_detail_startTime
            		.setOnClickListener(new OnClickListener() {

            			@Override
            			public void onClick(View v) {
            				showDatePicker(startDate,
            						textview_manager_oil_detail_startTime);
            			}
            		});

            textview_manager_oil_detail_endTime
            		.setOnClickListener(new OnClickListener() {

            			@Override
            			public void onClick(View v) {
            				showDatePicker(endDate,
            						textview_manager_oil_detail_endTime);
            			}
            		});
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/**
	 * 突出显示当前页面对应的导航栏按钮
	 */
	// private void changeToolbarIcon() {
	// // final int LEFT = 0;
	// final int TOP = 1;
	// // final int RIGHT = 2;
	// // final int BOTTOM = 3;
	//
	// View view = findViewById(R.id.manager_oil_textbutton_menu_oil_detail);
	// view.setBackgroundResource(R.drawable.manager_toolbar_bg_selected);
	//
	// ImageView icon =
	// (ImageView)findViewById(R.id.manager_oil_textbutton_menu_oil_detail_icon);
	//
	// Drawable drawable = r
	// .getDrawable(R.drawable.manager_toolbar_oil_detail_pressed);
	// icon.setImageDrawable(drawable);
	// }

	protected void showDatePicker(final Date date, final TextView textview) {
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
						Date chooseDate = new Date(datePicker.getYear() - 1900,
								datePicker.getMonth(), datePicker
										.getDayOfMonth());
						if (textview.getId() == R.id.textview_manager_oil_detail_startTime) {
							if (chooseDate.after(endDate)) {
								displayToast(R.string.date_picker_error);
								return;
							}
							startDate.setYear(datePicker.getYear() - 1900);
							startDate.setMonth(datePicker.getMonth());
							startDate.setDate(datePicker.getDayOfMonth());
							textview.setText(Format.DateStr.getDate(startDate));
						} else if (textview.getId() == R.id.textview_manager_oil_detail_endTime) {
							if (chooseDate.before(startDate)) {
								displayToast(R.string.date_picker_error);
								return;
							}
							endDate.setYear(datePicker.getYear() - 1900);
							endDate.setMonth(datePicker.getMonth());
							endDate.setDate(datePicker.getDayOfMonth());
							textview.setText(Format.DateStr.getDate(endDate));
						}
						customAlertDialog.dismiss();
						refreshListView();
						updateStatisticsAmount();
					}
				});
		customAlertDialog.show();
	}

	// 弹出是否要编辑或删除
	private void dialogEditDel(Oil _oil, int _position) {

		final Oil oil = _oil;
		final int position = _position;

		Resources r = getResources();
		String[] mItems = { r.getString(R.string.manager_edit),
				r.getString(R.string.manager_delete),
				r.getString(R.string.manager_cancel) };
		CustomDialog customDialog = new CustomDialog(OilDetailActivity.this);
		customDialog.setTitle(r.getString(R.string.manager_op_account));
		customDialog.setItems(mItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					showEdit(oil);
				} else if (which == 1) {
					deleteAccount(oil);
					updateListViewAfterDeleteAccount(position);
					updateStatisticsAmount();
				}
			}
		});
		customDialog.show();
	}

	// 更新总计值
	private void updateStatisticsAmount() {
		Double totalQuantity = new Double(0);
		Double totalAmount = new Double(0);
		Double averageQuantity = new Double(0);
		Double averageAmount = new Double(0);

		if (oilList != null && oilList.size() > 1) {
			for (int i = 1; i < oilList.size(); i++) {
				Oil oil = oilList.get(i);
				totalQuantity += oil.getQuantity();
				totalAmount += oil.getAmount();
			}
			Integer mileageDist = oilList.get(0).getMileage()
					- oilList.get(oilList.size() - 1).getMileage();
			if (mileageDist > 0) {
				averageQuantity = (totalQuantity * 100) / mileageDist;
			}
			Integer dateDist = (int) dataPuls(oilList.get(oilList.size() - 1)
					.getExpenseTime(), oilList.get(0).getExpenseTime());
			if (dateDist > 0) {
				averageAmount = totalAmount / dateDist;
			}
		}
		Log.e("OilDetailAcitvity", "-》 " + averageAmount);
		oil_detail_left = (ImageView) findViewById(R.id.oil_detail_left);
		oil_detail_right = (ImageView) findViewById(R.id.oil_detail_right);
		// TextView manager_oil_detail_head_average_oil = (TextView)
		// findViewById(R.id.manager_oil_detail_head_average_oil);
		// manager_oil_detail_head_average_oil.setText(Format
		// .doubleToCommercialNoSeparatorString(averageQuantity));
		if (averageQuantity > 99 || averageQuantity < 0) {
			setImageRight(-1);
			setImageLeft(0);
		} else if (averageQuantity > 10) {
			setImageRight((averageQuantity.intValue()) % 10);
			setImageLeft((averageQuantity.intValue()) / 10);
		} else {
			setImageRight(-1);
			setImageLeft((averageQuantity.intValue()));
		}
	}

	private void setImageLeft(int i) {
		if (i == -1) {
			oil_detail_left.setVisibility(View.GONE);
		} else {
			oil_detail_left.setVisibility(View.VISIBLE);
			oil_detail_left.setImageResource(getImage(i));
		}
	}

	private void setImageRight(int i) {
		if (i == -1) {
			oil_detail_right.setVisibility(View.GONE);
		} else {
			oil_detail_right.setVisibility(View.VISIBLE);
			oil_detail_right.setImageResource(getImage(i));
		}

	}

	private int getImage(int i) {
		switch (i) {
		case 0:
			return R.drawable.num_0;
		case 1:
			return R.drawable.num_1;
		case 2:
			return R.drawable.num_2;
		case 3:
			return R.drawable.num_3;
		case 4:
			return R.drawable.num_4;
		case 5:
			return R.drawable.num_5;
		case 6:
			return R.drawable.num_6;
		case 7:
			return R.drawable.num_7;
		case 8:
			return R.drawable.num_8;
		case 9:
			return R.drawable.num_9;
		default:
			return 0;
		}
	}

	// 日期相减
	private long dataPuls(Date startTime, Date endTime) {
		return (endTime.getTime() - startTime.getTime()) / 1000 / 60 / 60 / 24;
	}

	// 转到修改账目页面
	private void showEdit(Oil oil) {
		Intent intent = new Intent();
		intent.putExtra("com.cnlaunch.mycar.manager.Edit.id", oil.getDaoId());
		intent.putExtra("com.cnlaunch.mycar.manager.Edit", "yes");
		intent.setClass(OilDetailActivity.this, OilAddActivity.class);
		startActivityForResult(intent, 0);
	}

	// 删除账目
	private void deleteAccount(Oil oil) {
		OilBll oilBll = new OilBll(this, getHelper());
		oilBll.setDelFlag(oil.getDaoId());
		oilBll.setSyncFlag(false, oil.getDaoId());
	}

	// 删除后更新列表及总计值
	private void updateListViewAfterDeleteAccount(int deleteItemPosition) {
		mylist.remove(deleteItemPosition);
		oilList.remove(deleteItemPosition);
		adapter.notifyDataSetChanged();
	}

	// 读取数据库数据,并更新mylist,oilList,再更新listview
	private void refreshListView() {
		// 生成动态数组，并且转载数据
		if(mylist != null)
			mylist.clear();
		if(oilList != null)
			oilList.clear();

		// 从数据据读取列表
		oilList = (new OilBll(this, getHelper())).getList(startDate, endDate,
				carId);

		if (oilList != null) {
			for (Oil oil : oilList) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("oil_type", oil.getOilType().toString());
				map.put("amount",
						StringUtil.getCurrency(context)
								+ Format.doubleToCommercialNoSeparatorNoEndZeroString(oil
										.getAmount()));
				map.put("quantity",
						Format.doubleToCommercialNoSeparatorNoEndZeroString(oil
								.getQuantity()) + "L");
				map.put("mileage", String.valueOf(oil.getMileage()) + "km");
				if (oil.getExpenseTime() != null) {
					map.put("datetime",
							Format.DateStr.getDateTime(oil.getExpenseTime()));
				} else {
					map.put("datetime", "");
				}
				mylist.add(map);
			}
		}
		try {
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			backMain(OilDetailActivity.this);
			break;
		case R.id.manager_oil_textbutton_menu_oil_add:
			showOilAdd();
			break;
		case R.id.manager_oil_textbutton_menu_oil_detail:
			// do Nothing
			// showOilDetail();
			break;
		case R.id.manager_oil_textbutton_menu_oil_chart:
			showOilChart();
			break;

		default:
			break;
		}
	}

	/**
	 * 跳转到油耗分析图表页
	 */

	private void showOilChart() {
		Intent intent = new Intent();
		intent.setClass(OilDetailActivity.this, OilDetailBarChartActivity.class);
		startActivity(intent);
		overridePendingTransition(0, 0);
	}

	/**
	 * 跳转到加油页
	 */
	private void showOilAdd() {
		Intent intent = new Intent();
		intent.setClass(OilDetailActivity.this, OilAddActivity.class);
		startActivity(intent);
		overridePendingTransition(0, 0);
	}

}
