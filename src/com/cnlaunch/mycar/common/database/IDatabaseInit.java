package com.cnlaunch.mycar.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;

public interface IDatabaseInit {

	/**
	 * �������ݿ�ṹ
	 */
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource,Context context);

	/**
	 * �������ݿ�ṹ
	 */
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,Context context);

	/**
	 * �������ݿ������ʼ����
	 */
	public void afterCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource,Context context);

	/**
	 * �������ݿ�����������Ͱ汾���ݿ��е�����
	 */
	public void afterUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion,Context context);
}
