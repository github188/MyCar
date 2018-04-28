package com.cnlaunch.mycar.usercenter.model;

import com.cnlaunch.mycar.common.webservice.WSResult;

/**
 * 从服务端同步用户信息到手机的返回对象
 * @author xiangyuanmao
 *
 */
public class WSUser extends WSResult{

	public String cc	;//	Cc号码
	public String nickname;//		昵称
	public String userName; // 用户名
	public String isBindEmail; //是否绑定邮箱  0 ：否 1 ：是
	public String isBindMobile; // 是否绑定手机 0 ：否 1 ：是
	public String mobile		;//手机号
	public String email		;//邮箱

}
