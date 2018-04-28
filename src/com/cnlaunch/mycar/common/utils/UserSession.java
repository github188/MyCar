package com.cnlaunch.mycar.common.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.usercenter.LoginActivity;

public class UserSession {

	// 检测是否有账号登录
	public static boolean IsSomeoneLogined() {
		return MyCarActivity.isLogin;
	}

	// 跳转到登录页
	public static void jumpToLogin(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
	}

	// 跳转到登录页，并提示让其登录
	public static void jumpToLoginWithToast(Context context) {
		toastToLogin(context);
		context.startActivity(new Intent(context, LoginActivity.class));
	}

	// 提示用户去登录
	public static void toastToLogin(Context context) {
		Toast.makeText(context, R.string.im_please_login_usercenter_first,
				Toast.LENGTH_SHORT).show();
	}

}
