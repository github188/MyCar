package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * 选项组接口
 * */
public interface ChoiceGroup
{
	public String getId();// 组的id 
	public String getType();// 组类型
	public int getFlag();// 组的标志位
	public int getCount(); // 选项组的选项个数
	public String[] getItems(); // 获取所有的选项
	public int getCurrentIndex(); // 当前选定的选项
	public String getItemAt(int index);// 获取index索引位置的选项
	public void setIndex(int index);// 选择index处的选项
}
