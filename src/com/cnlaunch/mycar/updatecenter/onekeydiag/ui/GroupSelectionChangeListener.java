package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * @author luxingsong
 * 选项变化监听接口
 * */
public interface GroupSelectionChangeListener
{
	/**
	 * 组选项改变时会调用这个接口
	 * @param group  当前改变的选项组
	 * @param groupPosition 选项组在组列表里的索引位置
	 * @param oldIndex 旧选项的索引
	 * @param newIndex 新选择项的索引
	 */
	public void onSelectionChanged(ChoiceGroup group,int groupPosition,int oldIndex,int newIndex);
}
