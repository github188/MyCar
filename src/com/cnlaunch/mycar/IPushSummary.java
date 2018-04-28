package com.cnlaunch.mycar;

import android.content.Context;

/**
 * 
 * <功能简述> 车云网显示在首页摘要信息
 * <功能详细描述>
 * @author xiangyuanmao
 * @version 1.0 2012-5-17
 * @since DBS V100
 */
public interface IPushSummary
{

    /**
     * 当用户登录成功后，各个子模块需要推送信息到首页
     * 实现推送的过程中请注意：
     * 在调用DBSCarSummaryInfo对象注册消息时，请不要忘记在更新
     * 消息时调用UnRegister方法注销。
     * @param cc
     * @since DBS V100
     */
    public void push(String cc,Context context);
}
