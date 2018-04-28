package com.cnlaunch.mycar.manager.database;

/**
 * @author xuzhuowei 用户设置项
 */
public enum ManagerSettingNames {
	budget,//预算金额 
	oilType,// 油品类型
	categoryGroup, // 默认支出类别组别
	lastBackupDateTime, // 上次备份时间
	lastSyncDateTime, // 上次同步时间
	lastExportDateTime, // 上次导出时间
	syncDownAccountTotalPage, // 需同步下载的账目总分页数
	syncDownAccountPageAchieved, // 同步下载账目的已保存的分页数
	syncDownAccountPageFinished, //  账目同步下载，第一步操作（读取总分页数）的操作是否已完成
	syncDownAccountFinished, // 同步下载账目数据，是否已完成
	syncDownOilTotalPage, // 需同步下载的加油数据总分页数
	syncDownOilPageAchieved, // 同步下载加油数据的已保存的分页数
	syncDownOilPageFinished, // 加油同步下载，第一步操作（读取总分页数）的操作是否已完成
	syncDownOilFinished, // 同步下载加油数据，是否已完成
	syncDownManagerSettingFinished, // 同步下载配置信息同否已完成
	syncDownCustomCategoryFinished, // 同步下载自定义支出类别，是否已完成
	syncDownUserCarFinished, // 同步下载用户车辆，是否已完成
	syncDownFinished, // 同步下载所有数据，是否已完成
	oilPrice, //上次加油时，填写的油价
	lastOilType, //上次加油时，填写的油品
}
