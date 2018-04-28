package com.cnlaunch.mycar.usercenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cnlaunch.mycar.R;

import android.content.res.Resources;

/**
 * 用户中心的一些通用方法，用来检测帐号规则，密码规则等
 * 
 * @author jiangjun
 * 
 */
public class UserCenterCommon {

	/* 帐号规则检查，匹配帐号规则 */
	public static boolean checkAccountRegular(String strAccount) {

		if (!isUserName(strAccount)&&!checkCcStr(strAccount) && !checkMobilePhoneStr(strAccount)
				&& !checkEmailStr(strAccount)) {
			// 既不符合CC帐号规则，也不符合手机号码规则，也不符合邮箱规则
			return false;
		}

		// 规则匹配上了
		return true;
	}

	/* 密码规则检查，匹配密码规则 */
	public static boolean checkPasswordRegular(String strPwd) {

		if (strPwd == null
				|| strPwd.length() > UsercenterConstants.USERCENTER_PWD_LENGTH_MAX_LIMIT
				|| strPwd.length() < UsercenterConstants.USERCENTER_PWD_LENGTH_MIN_LIMIT) {

			return false;
		}
		return true;
	}

	/************************************************************************************************************
	 * 合法E-mail地址： 1. 必须包含一个并且只有一个符号“@” 2. 第一个字符不得是“@”或者“.” 3. 不允许出现“@.”或者.@ 4.
	 * 结尾不得是字符“@”或者“.” 5. 允许“@”前的字符中出现“＋” 6. 不允许“＋”在最前面，或者“＋@”
	 * 
	 * 正则表达式如下：
	 * -----------------------------------------------------------------------
	 * ^( \w+((-\w+)|(\.\w+))*)\+\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-
	 * z0 -9]+)*\.[A-Za-z0-9]+$
	 * -----------------------------------------------------------------------
	 * 
	 * 字符描述： ^ ：匹配输入的开始位置。 \：将下一个字符标记为特殊字符或字面值。 ：匹配前一个字符零次或几次。 + ：匹配前一个字符一次或多次。
	 * (pattern) 与模式匹配并记住匹配。 x|y：匹配 x 或 y。 [a-z] ：表示某个范围内的字符。与指定区间内的任何字符匹配。 \w
	 * ：与任何单词字符匹配，包括下划线。 $ ：匹配输入的结尾。
	 *************************************************************************************************************/
	public static boolean checkEmailStr(String strEmail) {

		if (strEmail == null) {

			return false;
		}

		// 电子邮件正则表达式匹配
		Pattern regex = Pattern
				.compile(UsercenterConstants.USERCENTER_EMAIL_REGULAR_PATTERN);
		Matcher matcher = regex.matcher(strEmail);

		return matcher.matches();

	}

	/*
	 * 现在的手机号码增加了150,153,156,158,159，157，187，188，189 。所以正则表达式如下: string s =
	 * "^(13[0-9]|15[0|3|6|7|8|9]|18[7|8|9])+[0-9]{8}$";
	 */
	public static boolean checkMobilePhoneStr(String strMobilePhone) {

		if (strMobilePhone == null) {

			return false;
		}

		// 长度匹配
		if (strMobilePhone.length() != UsercenterConstants.USERCENTER_MOBILE_PHONE_LENGTH) {

			return false;
		}

		// 正则表达式匹配
		Pattern regex = Pattern
				.compile(UsercenterConstants.USERCENTER_MOBILE_PHONE_REGULAR_PATTERN);
		Matcher matcher = regex.matcher(strMobilePhone);

		return matcher.matches();

	}

	/* CC号码字符串校验 */
	public static boolean checkCcStr(String strCc) {

		// CC校验
		if ((strCc == null) || (strCc.length() < 5) ) {

			return false;
		}
		
		for (int i=0; i < strCc.length(); i++){
			
			if( !Character.isDigit(strCc.charAt(i))){
				
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 校验字符串是否合适长度
	 * @param str
	 * @param length
	 * @param expression 0:等于; 1: 小于; 2: 大于
	 * @return
	 */
	public static boolean checkStrLength(String str, int length, int expression)
	{
		if (str == null)
		{
			return false;
		}
		switch (expression)
		{
		case 0:
			if (str.length() != length)
			{
				return false;
			}
			break;
		case 1:
			if (str.length() > length)
			{
				return false;
			}
			break;
		case 2:
			if (str.length() < length)
			{
				return false;
			}
			break;
			default: 
				return true;
				
		}
	
		return true;
	}
	
	public static boolean isMobileNO(String mobile)
    {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,2-3,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    public static boolean isEmail(String email)
    {
        String str = "^([a-zA-Z0-9]*[-_\\.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    
    public static boolean isUserName(String userName)
    {
        String str = "^[a-zA-Z][a-zA-Z0-9_]{4,19}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(userName);
        return m.matches();
    }
    
    public static boolean isNumber(String number){
        Pattern pattern = Pattern.compile("[1-9][0-9]{4,9}");      
        return pattern.matcher(number).matches();         
    }
    public static String getWebserviceResponseMessage(Resources resources, int code)
    {
        String message = null;
        switch(code)
        {
            case UsercenterConstants.OPREATE_SUCCESS:
                message = resources.getString(R.string.usercenter_operate_success);
                break;
            /**
             * 参数不能为空
             */
            case UsercenterConstants.PARAMETE_IS_NULL_ERROR : 
                message = resources.getString(R.string.parameter_is_null_error);
                break;

            /**
             * 参数格式错误
             */
            case UsercenterConstants.PARAMETE_FORMAT_ERROR : 
                message = resources.getString(R.string.parameter_format_error);
                break;
            /**
             * 系统异常
             */
            case UsercenterConstants.SYSTEM_ERROR : 
                message = resources.getString(R.string.system_error);
                break;

            /**
             * 网络异常
             */
            case UsercenterConstants.NETWORK_ERROR : 
                message = resources.getString(R.string.network_error);
                break;
            /************************用户中心错误码，递减分配***************************/
            /**
             * 登陆失败
             */
            case UsercenterConstants.LOGIN_FAIL : 
                message = resources.getString(R.string.usercenter_login_fial);
                break;

            /**
             * 用户状态错误
             */
            case UsercenterConstants.USER_STATUS_ERROR : 
                message = resources.getString(R.string.usercenter_user_status_error);
                break;
            /**
             * 邮箱未设置异常
             */
            case UsercenterConstants.EMAIL_NOT_SET_ERROR : 
                message = resources.getString(R.string.usercenter_email_not_set_error);
                break;

            /**
             * 用户名重复错误
             */
            case UsercenterConstants.USERNAME_DOUBLE_ERROR : 
                message = resources.getString(R.string.usercenter_username_double_error);
                break;

            /**
             * 邮箱格式错误
             */
            case UsercenterConstants.EMAIL_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_email_format_error);
                break;

            /**
             * 手机格式错误
             */
            case UsercenterConstants.MOBILE_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_mobile_format_error);
                break;

            /**
             * 用户名格式错误
             */
            case UsercenterConstants.USERNAME_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_username_forat_error);
                break;

            /**
             * 密码格式错误
             */
            case UsercenterConstants.PASSWORD_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_password_format_error);
                break;
            /**
             * 密码保护问题重复错误
             */
            case UsercenterConstants.SECURITY_QUESTION_DOUBLE_ERROR : 
                message = resources.getString(R.string.usercenter_security_question_double_error);
                break;
            /**
             * 电话格式错误
             */
            case UsercenterConstants.PHONE_FORMAT_ERROR :
                message = resources.getString(R.string.usercenter_phone_format_error);
                break;

            /**
             * 邮箱已经绑定
             */
            case UsercenterConstants.MOBILE_IS_BIND_ERROR : 
                message = resources.getString(R.string.usercenter_mobile_is_bind_error);
                break;
            /**
             * 
             */
            case UsercenterConstants.EMAIL_IS_BIND_ERROR : 
                message = resources.getString(R.string.usercenter_email_is_bind_error);
                break;

            /**
             * 密码保护问题已存在异常
             */
            case UsercenterConstants.SECURITY_ANSWER_EXIST_ERROR : 
                message = resources.getString(R.string.usercenter_security_answer_exist_error);
                break;

            /**
             * 用户未登录异常
             */
            case UsercenterConstants.NOT_LOGIN_ERROR : 
                message = resources.getString(R.string.usercenter_not_login_error);
                break;

            /**
             * 验证密码保护问题失败
             */
            case UsercenterConstants.VALIDATE_ANSWER_FAIL : 
                message = resources.getString(R.string.usercenter_vaildate_answer_fial);
                break;

            /**
             * 用户不存在错误
             */
            case UsercenterConstants.USER_NOT_EXIST_ERROR : 
                message = resources.getString(R.string.usercenter_user_not_exist_error);
                break;

            /**
             * 密码错误
             */
            case UsercenterConstants.PASSWORD_ERROR : 
                message = resources.getString(R.string.usercenter_password_error);
                break;

            /**32
             * 邮箱重复错误
             */
            case UsercenterConstants.EMAIL_DOUBLE_ERROR : 
                message = resources.getString(R.string.usercenter_email_double_error);
                break;
                default :
                    message = resources.getString(R.string.usercenter_unknown_error);
                    break;
        }
        return message;
    }
}
