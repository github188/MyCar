package com.cnlaunch.mycar;

/**
 * 
 * <功能简述> 用户登录状态
 * <功能详细描述>
 * @author xiangyuanmao
 * @version 1.0 2012-6-8
 * @since DBS V100
 */
public interface IUserOnlineState
{

    /**
     * 登录成功后的操作
     * 
     * @since DBS V100
     */
    public void loginSuccess();
    /**
     * 注销登录后的操作
     * 
     * @since DBS V100
     */
    public void logout();
}
