package com.cnlaunch.mycar.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.AccountBll;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.bll.OilBll;
import com.cnlaunch.mycar.manager.database.Account;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.cnlaunch.mycar.manager.database.Oil;
import com.cnlaunch.mycar.manager.net.SyncJob;

public class UserSettingsActivity extends BaseActivity {

	private final int ITEM_MYCAR_MANAGE = 0;
	private final int ITEM_BUDGET = 1;
	private final int ITEM_DB_BACKUP = 2;
	private final int ITEM_DB_RECOVERY = 3;
	private final int ITEM_DB_EXPORT = 4;
	private final int ITEM_SYNC = 5;
	private final ManagerSettingBll managerSettingBll = new ManagerSettingBll(
			this, getHelper());
	private Resources r;
	private SimpleAdapter sAdapter;

	private final int EXPORT_SUCC = 0;
	private final int EXPORT_FAIL = 1;
	private final int BACKUP_SUCC = 2;
	private final int BACKUP_FAIL = 3;
	private final int RECOVERY_SUCC = 4;
	private final int RECOVERY_FAIL = 5;

	private Handler handler;
	private Handler syncHandler;
	private ProgressDialog progressDialog;

	/**
	 * 同步进度对话框中的根VIEW，同步时，将动态向其中添加或修改内容
	 */
	private LinearLayout syncDialogLayout;

	private String[] settingItemsTitle = null;
	private String[] settingItemsDetail = null;
	private int[] settingItemsId = null;

	private Context context;
	private ViewHolder msgHolder;

	private int stepCount = 0;
	private CustomAlertDialog syncDialog;

	private ArrayList<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onStart() {
		super.onStart();
		showFinishedDialog();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		r = getResources();
		context = UserSettingsActivity.this;
		initData();

		setContentView(R.layout.manager_user_settings, R.layout.custom_title);
		setCustomeTitleLeft(R.string.manager_user_settings);
		setCustomeTitleRight("");

		ListView manager_user_settings_listview = (ListView) findViewById(R.id.manager_user_settings_listview);

		for (int i = 0; i < settingItemsTitle.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("item_title", settingItemsTitle[i]);
			map.put("item_detail", settingItemsDetail[i]);
			mapList.add(map);
		}

		sAdapter = new SimpleAdapter(UserSettingsActivity.this, mapList,
				R.layout.manager_user_settings_item, new String[] {
						"item_title", "item_detail" }, new int[] {
						R.id.manager_user_settings_item_title,
						R.id.manager_user_settings_item_detail });

		manager_user_settings_listview
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (settingItemsId[(int) id]) {
						case ITEM_MYCAR_MANAGE:
							showMyCarManage();
							break;
						case ITEM_BUDGET:
							showBudget();
							break;
						case ITEM_DB_BACKUP:
							showDbBackup();
							break;
						case ITEM_DB_RECOVERY:
							showDbRecovery();
							break;
						case ITEM_DB_EXPORT:
							showDbexport();
							break;
						case ITEM_SYNC:
							showSync();
							break;
						default:
							break;
						}
					}
				});
		manager_user_settings_listview.setAdapter(sAdapter);

		progressDialog = new ProgressDialog(this);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case EXPORT_SUCC:
					saveResult(EXPORT_SUCC);
					break;
				case EXPORT_FAIL:
					saveResult(EXPORT_FAIL);
					break;
				case BACKUP_SUCC:
					saveResult(BACKUP_SUCC);
					break;
				case BACKUP_FAIL:
					saveResult(BACKUP_FAIL);
					break;
				case RECOVERY_SUCC:
					saveResult(RECOVERY_SUCC);
					break;
				case RECOVERY_FAIL:
					saveResult(RECOVERY_FAIL);
					break;
				default:
					break;
				}
			}

		};

		syncHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SyncJob.SYNC_MSG_USER_SETTING_SUCC:
					msgHolder.textview_user_setting
							.setText(R.string.manager_synced_user_setting);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_USER_SETTING_ERR:
					msgHolder.textview_user_setting
							.setText(R.string.manager_sync_err_user_setting);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_CUSTOM_CATEGORY_SUCC:
					msgHolder.textview_custom_category
							.setText(R.string.manager_synced_custom_category);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_CUSTOM_CATEGORY_ERR:
					msgHolder.textview_custom_category
							.setText(R.string.manager_sync_err_custom_category);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_CUSTOM_NO_DATA_TO_SYNC:
					msgHolder.textview_custom_category
							.setText(R.string.manager_sync_err_custom_no_data_to_sync);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_ACCOUNT_SUCC:
					msgHolder.textview_account
							.setText(R.string.manager_synced_account);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_ACCOUNT_ERR:
					msgHolder.textview_account
							.setText(R.string.manager_sync_err_account);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_ACCOUNT_NO_DATA_TO_SYNC:
					msgHolder.textview_account
							.setText(R.string.manager_sync_err_account_no_data_to_sync);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_USER_CAR_SUCC:
					msgHolder.textview_user_car
							.setText(R.string.manager_synced_user_car);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_USER_CAR_ERR:
					msgHolder.textview_user_car
							.setText(R.string.manager_sync_err_user_car);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_OIL_SUCC:
					msgHolder.textview_oil.setText(R.string.manager_synced_oil);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_OIL_ERR:
					msgHolder.textview_oil
							.setText(R.string.manager_sync_err_oil);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_OIL_NO_DATA_TO_SYNC:
					msgHolder.textview_oil
							.setText(R.string.manager_sync_err_oil_no_data_to_sync);
					sycnCountAdd();
					break;
				case SyncJob.SYNC_MSG_NOT_LOGIN:
					syncDialog.dismiss();
					displayToast(R.string.sync_interrupt);
					return;
				default:
					break;
				}
				showFinishedDialog();
				// Log.e("test", "---" + stepCount);

			}

			private void sycnCountAdd() {
				if (stepCount != -1) {
					stepCount++;
				}
			}
		};

	}

	/**
	 * 显示同步完成信息
	 * 
	 * @param stepCount
	 */
	protected void showFinishedDialog() {
		final int TOTAL_STEP = 5;
		if (stepCount >= TOTAL_STEP) {
			stepCount = -1;
			// Log.e("test", "SyncFinished");
			msgHolder.button_ensure.setClickable(true);
			msgHolder.button_ensure.setText(R.string.manager_ensure);
			syncDialog.setCancelable(true);
		}
	}

	/**
	 * 显示网络同步备份对话框
	 */
	static class ViewHolder {
		TextView textview_user_setting;
		TextView textview_custom_category;
		TextView textview_account;
		TextView textview_user_car;
		TextView textview_oil;
		Button button_ensure;
	}

	protected void showSync() {
		stepCount = 0;
		syncDialog = new CustomAlertDialog(this);
		syncDialog.setCancelable(false);
		syncDialog.setTitle(r.getString(R.string.item_db_sync_title));

		if (!Env.isNetworkAvailable(context)) {
			syncDialog.setMessage(r.getString(R.string.manager_net_invalid));
			syncDialog.setCancelable(true);
			syncDialog.setPositiveButton(R.string.manager_ensure,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							syncDialog.dismiss();
						}
					});
		} else if (!MyCarActivity.isLogin) {
			syncDialog.setMessage(r.getString(R.string.sync_interrupt));
			syncDialog.setCancelable(true);
			syncDialog.setPositiveButton(R.string.manager_ensure,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							syncDialog.dismiss();
						}
					});
		} else {
			msgHolder = new ViewHolder();
			msgHolder.textview_user_setting = new TextView(context);
			msgHolder.textview_custom_category = new TextView(context);
			msgHolder.textview_account = new TextView(context);
			msgHolder.textview_user_car = new TextView(context);
			msgHolder.textview_oil = new TextView(context);
			msgHolder.button_ensure = new Button(context);

			msgHolder.textview_user_setting
					.setText(R.string.manager_syncing_user_setting);
			msgHolder.textview_custom_category
					.setText(R.string.manager_syncing_custom_category);
			msgHolder.textview_account
					.setText(R.string.manager_syncing_account);
			msgHolder.textview_user_car
					.setText(R.string.manager_syncing_user_car);
			msgHolder.textview_oil.setText(R.string.manager_syncing_oil);
			msgHolder.button_ensure.setText(R.string.sync_wait);
			msgHolder.button_ensure
					.setOnClickListener(new Button.OnClickListener() {

						@Override
						public void onClick(View v) {
							syncDialog.dismiss();
							freshListValue();
						}

					});
			msgHolder.button_ensure.setClickable(false);

			syncDialogLayout = new LinearLayout(context);
			syncDialogLayout.setOrientation(LinearLayout.VERTICAL);

			syncDialogLayout.addView(msgHolder.textview_user_setting);
			syncDialogLayout.addView(msgHolder.textview_custom_category);
			syncDialogLayout.addView(msgHolder.textview_account);
			syncDialogLayout.addView(msgHolder.textview_user_car);
			syncDialogLayout.addView(msgHolder.textview_oil);
			syncDialogLayout.addView(msgHolder.button_ensure);

			syncDialog.setView(syncDialogLayout);

			new SyncThread(context, syncHandler).start();
		}
		syncDialog.show();
	}

	/**
	 * @param result
	 */
	protected void saveResult(int result) {
		progressDialog.dismiss();

		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
		customAlertDialog.setTitle(r.getString(R.string.manager_result));
		String message = "";
		switch (result) {
		case EXPORT_SUCC:
			message = r.getString(R.string.manager_result_export_succ);
			managerSettingBll.update(
					ManagerSettingNames.lastExportDateTime.toString(),
					Format.DateStr.getDateTime());

			break;
		case EXPORT_FAIL:
			message = r.getString(R.string.manager_result_export_fail);
			break;
		case BACKUP_SUCC:
			message = r.getString(R.string.manager_result_backup_succ);
			managerSettingBll.update(
					ManagerSettingNames.lastBackupDateTime.toString(),
					Format.DateStr.getDateTime());
			break;
		case BACKUP_FAIL:
			message = r.getString(R.string.manager_result_backup_fail);
			break;
		case RECOVERY_SUCC:
			message = r.getString(R.string.manager_result_recovery_succ);
			break;
		case RECOVERY_FAIL:
			message = r.getString(R.string.manager_result_recovery_fail);
			break;
		default:
			break;
		}

		customAlertDialog.setMessage(message);
		customAlertDialog.setNegativeButton(
				r.getString(R.string.manager_ensure), new OnClickListener() {
					@Override
					public void onClick(View v) {
						freshListValue();
						customAlertDialog.dismiss();
					}
				});
		customAlertDialog.show();
	}

	private void initData() {
		settingItemsTitle = new String[] {
				r.getString(R.string.item_mycar_manage_title),
				r.getString(R.string.item_budget_title),
				r.getString(R.string.item_db_backup_title),
				r.getString(R.string.item_db_recovery_title),
				r.getString(R.string.item_db_export_title),
				r.getString(R.string.item_db_sync_title),
				};
		settingItemsDetail = new String[] {
				r.getString(R.string.item_mycar_manage_title),
				r.getString(R.string.item_budget_title),
				r.getString(R.string.item_db_backup_title),
				r.getString(R.string.item_db_recovery_title),
				r.getString(R.string.item_db_export_title),
				r.getString(R.string.item_db_sync_detail), 
				};
		settingItemsId = new int[] { ITEM_MYCAR_MANAGE, ITEM_BUDGET,
				ITEM_DB_BACKUP, ITEM_DB_RECOVERY, ITEM_DB_EXPORT, 
				ITEM_SYNC, 
				};
		setSettingsDetailValue();
	}

	private void setSettingsDetailValue() {
		// 填恢复数据库提醒
		setManagerSettingItemDetail(ITEM_DB_RECOVERY,
				r.getString(R.string.item_db_recovery_detail));

		// 填充最后备份时间
		setManagerSettingItemDetail(
				ITEM_DB_BACKUP,
				r.getString(R.string.item_db_backup_detail).replace(
						"[DATETIME]",
						managerSettingBll
								.find(ManagerSettingNames.lastBackupDateTime
										.toString())));

		// 填充最后导出时间
		setManagerSettingItemDetail(
				ITEM_DB_EXPORT,
				r.getString(R.string.item_db_export_detail).replace(
						"[DATETIME]",
						managerSettingBll
								.find(ManagerSettingNames.lastExportDateTime
										.toString())));

		// 预算
		setManagerSettingItemDetail(
				ITEM_BUDGET,
				r.getString(R.string.item_budget_detail)
						.replace(
								"[BUDGET]",
								managerSettingBll
										.find(ManagerSettingNames.budget
												.toString()))
						.replace(
								"[TOTAL]",
								(new AccountBll(this, getHelper()))
										.getMonthTotal().toString()));

	}

	private void setManagerSettingItemDetail(int ItemId, String value) {
		settingItemsDetail[ItemId] = value;
	}

	/**
	 * 显示预算编辑对话框
	 */
	protected void showBudget() {
		final Resources r = getResources();
		final CustomDialog customDialog = new CustomDialog(this);

		customDialog.setTitle(r.getString(R.string.item_budget_title));
		final EditText edittext = new EditText(this);
		edittext.setMaxHeight(10);
		edittext.setFilters(new  InputFilter[]{ new  InputFilter.LengthFilter(10)});  
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		edittext.setBackgroundResource(R.drawable.main_edit);
		customDialog.setView(edittext);

		customDialog.setPositiveButton(r.getString(R.string.manager_ensure),
				new OnClickListener() {
					public void onClick(View view) {
						String str = StringUtil.filterBlank(edittext.getText()
								.toString().trim());
						if (str.length() > 0) {
							try {
								Integer budget_amout = Integer.parseInt(str);
								managerSettingBll.update(
										ManagerSettingNames.budget.toString(),
										budget_amout.toString());
								Log.e("budget_amout","budget_amout="+budget_amout);
								freshListValue();
							} catch (NumberFormatException e) {
								displayToast(r
										.getString(R.string.manager_err_budget_amount));
							}
						}
						customDialog.dismiss();
					}
				});

		customDialog.setNegativeButton(r.getString(R.string.manager_cancel),
				new OnClickListener() {
					public void onClick(View view) {
						customDialog.dismiss();
					}
				});
		customDialog.show();

	}

	/**
	 * 刷新整个页面的数据
	 */
	private void freshListValue() {
		mapList.clear();
		setSettingsDetailValue();
		for (int i = 0; i < settingItemsTitle.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("item_title", settingItemsTitle[i]);
			map.put("item_detail", settingItemsDetail[i]);
			mapList.add(map);
		}

		sAdapter.notifyDataSetChanged();
	}

	/**
	 * 显示数据导出对话框
	 */
	protected void showDbexport() {
		if (!Env.isSDCardAvailable(UserSettingsActivity.this)) {
			displayToast(R.string.manager_err_sdcard_invalid);
			return;
		}

		progressDialog.setTitle(settingItemsTitle[ITEM_DB_EXPORT]);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(r.getString(R.string.manager_exporting));
		progressDialog.setCancelable(false);
		new ExportThread(handler).start();
		progressDialog.show();

	}

	/**
	 * 显示数据恢复对话框
	 */
	protected void showDbRecovery() {
		if (!Env.isSDCardAvailable(UserSettingsActivity.this)) {
			displayToast(R.string.manager_err_sdcard_invalid);
			return;
		}

		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
		customAlertDialog.setMessage(r
				.getString(R.string.manager_recovery_ensure));
		customAlertDialog.setTitle(r.getString(R.string.manager_notice));

		customAlertDialog.setPositiveButton(
				r.getString(R.string.manager_ensure), new OnClickListener() {

					@Override
					public void onClick(View v) {
						customAlertDialog.dismiss();
						progressDialog
								.setTitle(settingItemsTitle[ITEM_DB_RECOVERY]);
						progressDialog.setIndeterminate(true);
						progressDialog.setMessage(r
								.getString(R.string.manager_recoverying));
						progressDialog.setCancelable(false);
						new RecoveryThread(handler).start();
						progressDialog.show();
					}
				});

		customAlertDialog.setNegativeButton(
				r.getString(R.string.manager_cancel), new OnClickListener() {

					@Override
					public void onClick(View v) {
						customAlertDialog.dismiss();
					}
				});

		customAlertDialog.show();
	}

	/**
	 * 显示数据库备份对话框
	 */
	protected void showDbBackup() {
		if (!Env.isSDCardAvailable(UserSettingsActivity.this)) {
			displayToast(R.string.manager_err_sdcard_invalid);
			return;
		}

		progressDialog.setTitle(settingItemsTitle[ITEM_DB_BACKUP]);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(r.getString(R.string.manager_backuping));
		progressDialog.setCancelable(false);
		progressDialog.show();
		new BackupThread(handler).start();
	}

	/**
	 * 跳转到用户车辆管理页
	 */
	protected void showMyCarManage() {
		startActivity(new Intent(this, UserCarAddActivity.class));

	}

	/**
	 * @author xuzhuowei 数据恢复线程
	 */
	private class RecoveryThread extends Thread {
		private Handler handler;

		public RecoveryThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {

			File backupDir = new File(Env.getAppRootDirInSdcard(),
					BillingConstants.DIR_NAME_BILLING);
			if (!backupDir.exists()) {
				if (!backupDir.mkdirs()) {
					return;
				}
			}
			String dbFileName = UserDbHelper
					.getCurrentUserDbName(UserSettingsActivity.this);
			String appDir = BillingConstants.APP_ROOT_DIR + "/databases";
			File recoveryFile = new File(appDir, dbFileName);
			File inputFile = new File(backupDir, dbFileName);
			try {
				InputStream is = new BufferedInputStream(new FileInputStream(
						inputFile));
				FileOutputStream fos = new FileOutputStream(recoveryFile);
				byte[] buf = new byte[1024];
				int numRead = 0;
				while ((numRead = is.read(buf)) > 0) {
					fos.write(buf, 0, numRead);
				}
				fos.close();
				is.close();

				Message msg = new Message();
				msg.what = RECOVERY_SUCC;
				handler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = RECOVERY_FAIL;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * @author xuzhuowei 数据库备份线程
	 */
	private class BackupThread extends Thread {
		private Handler handler;

		public BackupThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {

			File backupDir = new File(Env.getAppRootDirInSdcard(),
					BillingConstants.DIR_NAME_BILLING);
			if (!backupDir.exists()) {
				if (!backupDir.mkdirs()) {
					return;
				}
			}
			String dbFileName = UserDbHelper
					.getCurrentUserDbName(UserSettingsActivity.this);
			String appDir = BillingConstants.APP_ROOT_DIR + "/databases";
			File backupFile = new File(backupDir, dbFileName);
			File inputFile = new File(appDir, dbFileName);
			try {
				InputStream is = new BufferedInputStream(new FileInputStream(
						inputFile));
				FileOutputStream fos = new FileOutputStream(backupFile);
				byte[] buf = new byte[1024];
				int numRead = 0;
				while ((numRead = is.read(buf)) > 0) {
					fos.write(buf, 0, numRead);
				}
				fos.close();
				is.close();

				Message msg = new Message();
				msg.what = BACKUP_SUCC;
				handler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = BACKUP_FAIL;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * @author xuzhuowei 数据导出线程
	 */
	private class ExportThread extends Thread {
		private Handler handler;

		public ExportThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			AccountBll accountBll = new AccountBll(UserSettingsActivity.this,
					getHelper());
			OilBll oilBll = new OilBll(UserSettingsActivity.this, getHelper());
			List<Account> listBilling = accountBll.getDataForExport();
			List<Oil> listOil = oilBll.getDataForExport();
			StringBuilder sbBilling = new StringBuilder(
					getString(R.string.manager_oil_field_category) + ","
							+ getString(R.string.manager_oil_field_amount)
							+ ","
							+ getString(R.string.manager_oil_field_datetime)
							+ ","
							+ getString(R.string.manager_oil_field_remark)
							+ "\n");
			for (Account account : listBilling) {
				sbBilling.append(account.getCategory());
				sbBilling.append(",");
				sbBilling.append(account.getAmount());
				sbBilling.append(",");
				sbBilling.append(Format.DateStr.getDateTime(account
						.getExpenseTime()));
				sbBilling.append(",");
				sbBilling.append(account.getRemark());
				sbBilling.append("\n");
			}

			StringBuilder sbOil = new StringBuilder(
					getString(R.string.manager_oil_field_mileage) + ","
							+ getString(R.string.manager_oil_field_amount)
							+ ","
							+ getString(R.string.manager_oil_field_quantity)
							+ ","
							+ getString(R.string.manager_oil_field_oil_type)
							+ ","
							+ getString(R.string.manager_oil_field_datetime)
							+ ","
							+ getString(R.string.manager_oil_field_remark)
							+ "\n");
			for (Oil oil : listOil) {
				sbOil.append(oil.getMileage());
				sbOil.append(",");
				sbOil.append(oil.getAmount());
				sbOil.append(",");
				sbOil.append(oil.getQuantity());
				sbOil.append(",");
				sbOil.append(oil.getOilType());
				sbOil.append(",");
				sbOil.append(Format.DateStr.getDateTime(oil.getExpenseTime()));
				sbOil.append(",");
				sbOil.append(oil.getRemark());
				sbOil.append("\n");
			}

			File backupDir = new File(Env.getAppRootDirInSdcard(),
					BillingConstants.DIR_NAME_BILLING);
			if (!backupDir.exists()) {
				if (!backupDir.mkdirs()) {
					return;
				}
			}
			File exprotFileBilling = new File(backupDir,
					BillingConstants.MANAGER_EXPORT_CSV_BILLING_FILE_NAME);
			File exprotFileOil = new File(backupDir,
					BillingConstants.MANAGER_EXPORT_CSV_OIL_FILE_NAME);
			try {
				// 导出记账记录
				FileOutputStream fos = new FileOutputStream(exprotFileBilling);

				OutputStreamWriter writer = new OutputStreamWriter(fos,
						"gb2312");
				writer.write(sbBilling.toString());
				writer.flush();
				writer.close();

				fos.close();

				// 导出加油记录
				fos = new FileOutputStream(exprotFileOil);

				writer = new OutputStreamWriter(fos, "gb2312");
				writer.write(sbOil.toString());
				writer.flush();
				writer.close();

				fos.close();

				Message msg = new Message();
				msg.what = EXPORT_SUCC;
				handler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = EXPORT_FAIL;
				handler.sendMessage(msg);
			}
		}
	}

}