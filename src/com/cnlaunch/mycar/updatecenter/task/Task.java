package com.cnlaunch.mycar.updatecenter.task;
/**
 * 任务类，封装蓝牙请求以及网络请求
 * 完成分布式的任务
 * @author luxingsong
 *
 */
public abstract class Task
{
	private String name;
	private int id;
	public abstract void run();
}
