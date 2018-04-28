package com.cnlaunch.mycar.manager.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.manager.bll.ManagerSettingBll;
import com.cnlaunch.mycar.manager.bll.UserCarBll;
import com.cnlaunch.mycar.manager.database.ManagerSettingNames;
import com.cnlaunch.mycar.manager.database.UserCar;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * @author xuzhuowei ͬ���û�����
 */
public class SyncUserCarJob extends SyncJob {
	private final String TAG = "SyncUserCarJob";

	private UserCarBll userCarBll;
	private UserDbHelper helper;
	private ManagerSettingBll userSettingBll;
	private static int resultCode = 0;

	public SyncUserCarJob(Context context, Handler mainHandler) {
		super(context, mainHandler);
	}

	@Override
	public void setRequestPara() {
		helper = OpenHelperManager.getHelper(context, UserDbHelper.class);
		userSettingBll = new ManagerSettingBll(context, helper);
		userCarBll = new UserCarBll(context, helper);
		String syncDownUserCarFinished = userSettingBll
				.find(ManagerSettingNames.syncDownUserCarFinished.toString());
		if (syncDownUserCarFinished != null) {
			if (syncDownUserCarFinished.equals("Yes")) {
				addUploadUserCarJob();
			} else {
				addDownUserCarJob();
			}
		}

	}

	private void addDownUserCarJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_USER_CAR;
		addJob(null, methodName);
	}

	/**
	 * ���������ݿ�ġ��û����������ݴ���������ϴ����񣬲������ϴ��������
	 */
	private void addUploadUserCarJob() {
		String methodName = Constants.SERVICE_MANAGER_METHOD_UPLOAD_USER_CAR;
		List<UserCar> list = userCarBll.getAllUserCarForSync();
		if (list != null && list.size() > 0) {
			List<UserCarDTO> dtoList = new ArrayList<UserCarDTO>();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				UserCarDTO userCarDTO = new UserCarDTO();
				UserCar userCar = list.get(i);
				userCarDTO
						.setUserCarId(userCar.getUserCarId().replace("-", ""));
				userCarDTO.setUserCarName(userCar.getUserCarName());
				dtoList.add(userCarDTO);
			}
			addJob(new TreeMap<String, Object>(), dtoList, methodName);
		}
	}

	@Override
	public void dealResult(Map<String, Object> map, SoapObject result) {
		if (map == null) {
			syncJobResultCode = SYNC_JOB_RESULT_CODE_SUCC;
		} else {

			if (result != null && result.getProperty(0) != null) {
				SoapObject so = (SoapObject) result.getProperty(0);

				int resultCode = new Integer(
						so.getProperty("code") == null ? "-1" : so.getProperty(
								"code").toString()).intValue();

				if (resultCode == 0) {
					String methodName = map.get("methodName").toString();
					if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_UPLOAD_USER_CAR)) {
						Log.e(TAG, "UPLOAD_USER_CAR_succ");
					} else if (methodName
							.equals(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_USER_CAR)) {
						saveUserCar(so.getProperty("data").toString());
					}
					resultCode = 0;
				} else {
					resultCode = -1;
				}
			}
		}
	}

	/**
	 * �����ص����û�������Ϣ�������ݿ�
	 * 
	 * @param userCarListStr
	 */
	private void saveUserCar(String userCarListStr) {
		Log.e(TAG, userCarListStr);
		final int CAR_FIELD_COUNT = 2;// ����ʱ���ӿ��ж���ģ��û�������Ϣ���ֶθ���
		String[] cars;
		if (userCarListStr.contains("$")) {
			cars = userCarListStr.split("\\$");
		} else {
			cars = new String[] { userCarListStr };
		}

		if (cars != null && cars.length > 0) {
			int len = cars.length;
			for (int i = 0; i < len; i++) {
				if (cars[i].contains("|")) {
					String[] car = cars[i].split("\\|");
					if (car.length == CAR_FIELD_COUNT) {
						String userCarName = car[1];
						String userCarId = car[0];
						// �����Ĭ�ϳ���ͬ������������ͬ���õ��ĳ���ID���Ǳ��صģ��������ͺļ�¼��
						if (userCarName
								.equals(context
										.getResources()
										.getString(
												R.string.manager_usercar_default_nickname))) {
							List<UserCar> list = userCarBll.find(userCarName);
							// �������Ĭ�ϳ�������δ������������ͬ�����ĳ���ID���Ǳ��صģ��������ͺļ�¼��
							if (list != null && list.size() > 0) {
								UserCar userCar = list.get(0);
								userCar.setUserCarId(userCarId);
								userCarBll.update(userCar);
								// TODO ���¼��ͼ�¼

							} else {
								// ���������Ĭ�ϣ����ѱ���������ֱ�ӱ���
								userCarBll.save(new UserCar(userCarId,
										userCarName));
							}
						} else {
							// �������Ĭ�ϳ�����ֱ�ӱ���
							userCarBll
									.save(new UserCar(userCarId, userCarName));
						}

					}
				}
			}
		}

		// ���������û�������ϢΪ �����
		userSettingBll.update(
				ManagerSettingNames.syncDownUserCarFinished.toString(), "Yes");
	}

	@Override
	protected void sendMsgToUI() {
		if (resultCode == 0) {
			sendMsgToUi(SyncJob.SYNC_MSG_USER_CAR_SUCC);
		} else {
			sendMsgToUi(SyncJob.SYNC_MSG_USER_CAR_ERR);
		}

	}

}