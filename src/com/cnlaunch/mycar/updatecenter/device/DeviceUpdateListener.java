package com.cnlaunch.mycar.updatecenter.device;


/**
 * 设备升级监听
 * */
public interface DeviceUpdateListener
{
	/**
	 * 开始升级
	 */
	public void onDeviceUpdateStart();
	/**
	 * 通知升级操作过程
	 * @param action 操作类型
	 * @param detail 操作描述
	 */
	public void onDeviceUpdateMessages(int action,String detail);
	/**
	 * 通知升级操作进度
	 * @param action 操作类型
	 * @param progress 进度信息
	 */
	public void onUpdateProgress(int action , ProgressInfo progress);
	/**
	 * 升级完成
	 * @param message
	 */
	public void onDeviceUpdateFinish(String message);
	/**
	 * 通知发生异常或者错误
	 * @param error 错误码
	 * @param detail 出错原因
	 */
	public void onDeviceUpdateException(int error,Object detail);
}
