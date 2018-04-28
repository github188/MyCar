package com.cnlaunch.mycar.common.webservice;

/**
 * 为了处理登录失效问题，每次WebService返回的结果被封装到WSBaseResult
 * 各自模块取得响应头里的代码responseCode，如果为-1，说明未登录或者登陆失效，如果为0，说明响应成功
 * 调用WebService的操作顺序如下：
 * 查询MyCarActivity里静态变量isLogin，
 *      如果isLogin == true
 *          实例化WebServiceManager.java对象，
 *          调用execute()方法发送WebService请求
 *          接收返回值WSBaseResult，取到里面的responseCode属性，
 *          如果responseCode == 0，说明请求发送成功，
 *              取到WSBaseResult对象的object对象，这个即为服务端返回的对象
 *          别的情况如果responseCode == -1，说明登录失效，请参考MyCarActivity中的autoLogin()方法登录
 *          再调用WebServiceManager对象的execute()方法发送WebService请求
 *      别的情况（未登录）
 *          请参考MyCarActivity中的autoLogin()方法登录
 *          再调用WebServiceManager对象的execute()方法发送WebService请求
 *       其他异常情况，responseCode == 2 表示IO发生异常
 *       responseCode == 3 表示Xml解析错误
 *       "501", "用户名或者密码错误"
 * 		"401", "参数不能为空"
 * 		"402", "参数不能为空或者参数类型错误"
 * 		"411", "原密码错误"
 * 		"502", "用户状态错误"
 * 		"503", "用户未设置邮箱错误"
 * 		"511", "用户不存在"
 * 		"512", "用户手机已被其他用户使用"
 * 		"521", "用户未登录或者已失效"
 * 		"513", "用户邮箱已被其他用户使用"
 * @author xiangyuanmao
 *
 */
public class WSBaseResult {

	public int responseCode; // 返回结果代码 -1登录超时；0成功
	public Object object; // 返回结果对象
	
	@Override
	public String toString()
	{
		return "WSBaseResult [responseCode=" + responseCode + ", object="
				+ object + "]";
	}
	
}
