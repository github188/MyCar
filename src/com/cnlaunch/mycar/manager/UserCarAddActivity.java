package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.bll.UserCarBll;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.database.UserCar;

public class UserCarAddActivity extends BaseActivity {
	ListView listview;
	List<UserCar> listUserCar;
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	SimpleAdapter adapter;
	UserCarBll userCarBll;
	EditText textview_manager_billing_user_settings_usercar_name;
	Button button_manager_billing_user_settings_usercar_add;
	UserCar userCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_user_settings_usercar,
				R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_user_settings);
		setCustomeTitleRight(R.string.manager_user_settings_usercar);

		textview_manager_billing_user_settings_usercar_name = (EditText) findViewById(R.id.textview_manager_billing_user_settings_usercar_name);
		button_manager_billing_user_settings_usercar_add = (Button) findViewById(R.id.manager_billing_user_settings_usercar_add);

		button_manager_billing_user_settings_usercar_add
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//去除空格
						String userCarName = textview_manager_billing_user_settings_usercar_name
								.getText().toString().replace(" ", "");
						textview_manager_billing_user_settings_usercar_name.setText(userCarName);
						
						if (userCarName.length() == 0) {
							displayToast(R.string.manager_billing_user_settings_usercar_name_err_null);
						} else {
							if (userCar == null) {
								//检测该名称是否已存在
								List<UserCar> listUserCar = userCarBll.find(userCarName);
								if(listUserCar==null || listUserCar.size()>0){
									displayToast(R.string.manager_billing_user_settings_usercar_name_err_repeat);
									return;
								}
								
								
								userCar = new UserCar();
								userCar.setUserCarName(userCarName);
								userCar.setUserCarId(UUID.randomUUID()
										.toString().replace("-", ""));
								userCarBll.save(userCar);
								textview_manager_billing_user_settings_usercar_name
										.setText("");
								displayToast(R.string.manager_billing_user_settings_usercar_add_succ);
								freshList();
								userCar = null;
							} else {
								userCar.setUserCarName(userCarName);
								userCarBll.update(userCar);
								userCar = null;
								textview_manager_billing_user_settings_usercar_name
										.setText("");
								button_manager_billing_user_settings_usercar_add
										.setText(R.string.manager_billing_user_settings_usercar_add);
								displayToast(R.string.manager_billing_user_settings_usercar_edit_succ);
								freshList();
							}
						}
					}
				});

		listview = (ListView) findViewById(R.id.manager_billing_user_settings_usercar_list);
		userCarBll = new UserCarBll(UserCarAddActivity.this, getHelper());
		listUserCar = userCarBll.getAllUserCar();
		int len = listUserCar.size();
		for (int i = 0; i < len; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(("userCarName"), listUserCar.get(i).getUserCarName());
			mylist.add(map);

		}

		adapter = new SimpleAdapter(this, mylist,
				R.layout.manager_user_settings_usercar_item,
				new String[] { "userCarName" },
				new int[] { R.id.manager_user_settings_usercar_item });
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialogEditDel(listUserCar.get((int) id));

			}
		});

	}

	/**
	 * 刷新我的车辆列表
	 */
	private void freshList() {
		listUserCar.clear();
		mylist.clear();
		listUserCar = userCarBll.getAllUserCar();

		int len = listUserCar.size();
		for (int i = 0; i < len; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(("userCarName"), listUserCar.get(i).getUserCarName());
			mylist.add(map);

		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 显示编辑对话框
	 * @param _userCar
	 */
	protected void dialogEditDel(UserCar _userCar) {

		final UserCar userCar = _userCar;

		Resources r = getResources();
		String[] mItems = { r.getString(R.string.manager_edit),
				r.getString(R.string.manager_delete),
				r.getString(R.string.manager_cancel) };
		CustomDialog customDialog=new CustomDialog(UserCarAddActivity.this);
		customDialog.setTitle(r.getString(R.string.manager_op_account));
		customDialog.setItems(mItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					showEdit(userCar);
				} else if (which == 1) {
					if (listUserCar.size() > 1) {
						List<Oil> listOil = (new OilBll(
								UserCarAddActivity.this, getHelper()))
								.getList(userCar.getUserCarId());
						if (listOil != null && listOil.size() != 0) {
							displayToast(R.string.manager_billing_user_settings_usercar_err_connect_oil);
						} else {
							delete(userCar);
							freshList();
						}
					} else {
						displayToast(R.string.manager_billing_user_settings_usercar_err_hold_one);
					}
				}
			}
		});
		customDialog.show();
	}

	/**
	 * 删除我的车辆
	 * @param userCar
	 */
	protected void delete(UserCar userCar) {
		userCarBll.delete(userCar.getDaoId());
	}

	/**编辑我的车辆
	 * @param userCar
	 */
	protected void showEdit(UserCar userCar) {
		textview_manager_billing_user_settings_usercar_name.setText(userCar
				.getUserCarName());
		textview_manager_billing_user_settings_usercar_name
				.setSelection(textview_manager_billing_user_settings_usercar_name
						.getText().toString().length());
		button_manager_billing_user_settings_usercar_add
				.setText(R.string.manager_editsave);
		this.userCar = userCar;
	}
}