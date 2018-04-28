package com.cnlaunch.mycar.usercenter.model;

import com.cnlaunch.mycar.common.webservice.WSResult;

/**
 * 用户登录返回的结果对象
 * @author xiangyuanmao
 *
 */
public class LoginResult extends WSResult{
	public String cc; // cc号码
	public String token; // 令牌
	public long serverSystemTime; // 服务器端时间
}
