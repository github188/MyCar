package com.cnlaunch.mycar.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;

public interface IDatabaseInit {

	/**
	 * 创建数据库结构
	 */
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource,Context context);

	/**
	 * 升级数据库结构
	 */
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,Context context);

	/**
	 * 创建数据库后，填充初始数据
	 */
	public void afterCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource,Context context);

	/**
	 * 升级数据库后，批量操作低版本数据库中的数据
	 */
	public void afterUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,Context context);
}
