package com.cnlaunch.mycar.usercenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cnlaunch.mycar.R;

import android.content.res.Resources;

/**
 * �û����ĵ�һЩͨ�÷�������������ʺŹ�����������
 * 
 * @author jiangjun
 * 
 */
public class UserCenterCommon {

	/* �ʺŹ����飬ƥ���ʺŹ��� */
	public static boolean checkAccountRegular(String strAccount) {

		if (!isUserName(strAccount)&&!checkCcStr(strAccount) && !checkMobilePhoneStr(strAccount)
				&& !checkEmailStr(strAccount)) {
			// �Ȳ�����CC�ʺŹ���Ҳ�������ֻ��������Ҳ�������������
			return false;
		}

		// ����ƥ������
		return true;
	}

	/* ��������飬ƥ��������� */
	public static boolean checkPasswordRegular(String strPwd) {

		if (strPwd == null
				|| strPwd.length() > UsercenterConstants.USERCENTER_PWD_LENGTH_MAX_LIMIT
				|| strPwd.length() < UsercenterConstants.USERCENTER_PWD_LENGTH_MIN_LIMIT) {

			return false;
		}
		return true;
	}

	/************************************************************************************************************
	 * �Ϸ�E-mail��ַ�� 1. �������һ������ֻ��һ�����š�@�� 2. ��һ���ַ������ǡ�@�����ߡ�.�� 3. ��������֡�@.������.@ 4.
	 * ��β�������ַ���@�����ߡ�.�� 5. ����@��ǰ���ַ��г��֡����� 6. ��������������ǰ�棬���ߡ���@��
	 * 
	 * ������ʽ���£�
	 * -----------------------------------------------------------------------
	 * ^( \w+((-\w+)|(\.\w+))*)\+\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-
	 * z0 -9]+)*\.[A-Za-z0-9]+$
	 * -----------------------------------------------------------------------
	 * 
	 * �ַ������� ^ ��ƥ������Ŀ�ʼλ�á� \������һ���ַ����Ϊ�����ַ�������ֵ�� ��ƥ��ǰһ���ַ���λ򼸴Ρ� + ��ƥ��ǰһ���ַ�һ�λ��Ρ�
	 * (pattern) ��ģʽƥ�䲢��סƥ�䡣 x|y��ƥ�� x �� y�� [a-z] ����ʾĳ����Χ�ڵ��ַ�����ָ�������ڵ��κ��ַ�ƥ�䡣 \w
	 * �����κε����ַ�ƥ�䣬�����»��ߡ� $ ��ƥ������Ľ�β��
	 *************************************************************************************************************/
	public static boolean checkEmailStr(String strEmail) {

		if (strEmail == null) {

			return false;
		}

		// �����ʼ�������ʽƥ��
		Pattern regex = Pattern
				.compile(UsercenterConstants.USERCENTER_EMAIL_REGULAR_PATTERN);
		Matcher matcher = regex.matcher(strEmail);

		return matcher.matches();

	}

	/*
	 * ���ڵ��ֻ�����������150,153,156,158,159��157��187��188��189 ������������ʽ����: string s =
	 * "^(13[0-9]|15[0|3|6|7|8|9]|18[7|8|9])+[0-9]{8}$";
	 */
	public static boolean checkMobilePhoneStr(String strMobilePhone) {

		if (strMobilePhone == null) {

			return false;
		}

		// ����ƥ��
		if (strMobilePhone.length() != UsercenterConstants.USERCENTER_MOBILE_PHONE_LENGTH) {

			return false;
		}

		// ������ʽƥ��
		Pattern regex = Pattern
				.compile(UsercenterConstants.USERCENTER_MOBILE_PHONE_REGULAR_PATTERN);
		Matcher matcher = regex.matcher(strMobilePhone);

		return matcher.matches();

	}

	/* CC�����ַ���У�� */
	public static boolean checkCcStr(String strCc) {

		// CCУ��
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
	 * У���ַ����Ƿ���ʳ���
	 * @param str
	 * @param length
	 * @param expression 0:����; 1: С��; 2: ����
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
             * ��������Ϊ��
             */
            case UsercenterConstants.PARAMETE_IS_NULL_ERROR : 
                message = resources.getString(R.string.parameter_is_null_error);
                break;

            /**
             * ������ʽ����
             */
            case UsercenterConstants.PARAMETE_FORMAT_ERROR : 
                message = resources.getString(R.string.parameter_format_error);
                break;
            /**
             * ϵͳ�쳣
             */
            case UsercenterConstants.SYSTEM_ERROR : 
                message = resources.getString(R.string.system_error);
                break;

            /**
             * �����쳣
             */
            case UsercenterConstants.NETWORK_ERROR : 
                message = resources.getString(R.string.network_error);
                break;
            /************************�û����Ĵ����룬�ݼ�����***************************/
            /**
             * ��½ʧ��
             */
            case UsercenterConstants.LOGIN_FAIL : 
                message = resources.getString(R.string.usercenter_login_fial);
                break;

            /**
             * �û�״̬����
             */
            case UsercenterConstants.USER_STATUS_ERROR : 
                message = resources.getString(R.string.usercenter_user_status_error);
                break;
            /**
             * ����δ�����쳣
             */
            case UsercenterConstants.EMAIL_NOT_SET_ERROR : 
                message = resources.getString(R.string.usercenter_email_not_set_error);
                break;

            /**
             * �û����ظ�����
             */
            case UsercenterConstants.USERNAME_DOUBLE_ERROR : 
                message = resources.getString(R.string.usercenter_username_double_error);
                break;

            /**
             * �����ʽ����
             */
            case UsercenterConstants.EMAIL_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_email_format_error);
                break;

            /**
             * �ֻ���ʽ����
             */
            case UsercenterConstants.MOBILE_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_mobile_format_error);
                break;

            /**
             * �û�����ʽ����
             */
            case UsercenterConstants.USERNAME_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_username_forat_error);
                break;

            /**
             * �����ʽ����
             */
            case UsercenterConstants.PASSWORD_FORMAT_ERROR : 
                message = resources.getString(R.string.usercenter_password_format_error);
                break;
            /**
             * ���뱣�������ظ�����
             */
            case UsercenterConstants.SECURITY_QUESTION_DOUBLE_ERROR : 
                message = resources.getString(R.string.usercenter_security_question_double_error);
                break;
            /**
             * �绰��ʽ����
             */
            case UsercenterConstants.PHONE_FORMAT_ERROR :
                message = resources.getString(R.string.usercenter_phone_format_error);
                break;

            /**
             * �����Ѿ���
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
             * ���뱣�������Ѵ����쳣
             */
            case UsercenterConstants.SECURITY_ANSWER_EXIST_ERROR : 
                message = resources.getString(R.string.usercenter_security_answer_exist_error);
                break;

            /**
             * �û�δ��¼�쳣
             */
            case UsercenterConstants.NOT_LOGIN_ERROR : 
                message = resources.getString(R.string.usercenter_not_login_error);
                break;

            /**
             * ��֤���뱣������ʧ��
             */
            case UsercenterConstants.VALIDATE_ANSWER_FAIL : 
                message = resources.getString(R.string.usercenter_vaildate_answer_fial);
                break;

            /**
             * �û������ڴ���
             */
            case UsercenterConstants.USER_NOT_EXIST_ERROR : 
                message = resources.getString(R.string.usercenter_user_not_exist_error);
                break;

            /**
             * �������
             */
            case UsercenterConstants.PASSWORD_ERROR : 
                message = resources.getString(R.string.usercenter_password_error);
                break;

            /**32
             * �����ظ�����
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
