package com.cnlaunch.mycar.common.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.usercenter.LoginActivity;

public class UserSession {

	// ����Ƿ����˺ŵ�¼
	public static boolean IsSomeoneLogined() {
		return MyCarActivity.isLogin;
	}

	// ��ת����¼ҳ
	public static void jumpToLogin(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
	}

	// ��ת����¼ҳ������ʾ�����¼
	public static void jumpToLoginWithToast(Context context) {
		toastToLogin(context);
		context.startActivity(new Intent(context, LoginActivity.class));
	}

	// ��ʾ�û�ȥ��¼
	public static void toastToLogin(Context context) {
		Toast.makeText(context, R.string.im_please_login_usercenter_first,
				Toast.LENGTH_SHORT).show();
	}

}
