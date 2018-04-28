package com.cnlaunch.mycar.common.database;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

public abstract class MyCarDbHelper extends OrmLiteSqliteOpenHelper {
	
	private String[] classesForDatabaseInit; 
	private Context context;

	public MyCarDbHelper(Context context, String databaseName,
			CursorFactory factory, int databaseVersion,String[] classesForDatabaseInit) {
		super(context, databaseName, factory, databaseVersion);
		this.classesForDatabaseInit = classesForDatabaseInit;
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource) {
		List<Class<IDatabaseInit>> classList = getAllSubClass(
				IDatabaseInit.class, classesForDatabaseInit);
		if (classList != null) {
			executeAllSubClassMethod(classList, "onCreate", sqliteDatabase,
					connectionSource,context, new Class<?>[] { SQLiteDatabase.class,
							ConnectionSource.class,Context.class });
		}
		if (classList != null) {
			executeAllSubClassMethod(classList, "afterCreate", sqliteDatabase,
					connectionSource,context, new Class<?>[] { SQLiteDatabase.class,
							ConnectionSource.class,Context.class });
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		List<Class<IDatabaseInit>> classList = getAllSubClass(
				IDatabaseInit.class, classesForDatabaseInit);
		if (classList != null) {
			executeAllSubClassMethod(classList, "onUpgrade", sqliteDatabase,
					connectionSource,context, new Class<?>[] { SQLiteDatabase.class,
							ConnectionSource.class, int.class, int.class,Context.class });
			executeAllSubClassMethod(classList, "afterUpgrade", sqliteDatabase,
					connectionSource,context, new Class<?>[] { SQLiteDatabase.class,
							ConnectionSource.class, int.class, int.class,Context.class });
		}
	}

	/**
	 * 反射执行数据库初始化某方法
	 * @param classList 实现了数据库初始化接口的类
	 * @param methodName 方法名
	 * @param sqliteDatabase 
	 * @param connectionSource
	 * @param para 参数类型列表
	 */
	private void executeAllSubClassMethod(List<Class<IDatabaseInit>> classList,
			String methodName, SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource,Context context, Class<?>[] paras) {
		for (Class<IDatabaseInit> clazz : classList) {
			try {
				Object obj = clazz.getConstructor(new Class[] {}).newInstance(
						new Object[] {});
				Method method = clazz.getMethod(methodName, paras);
				method.invoke(obj, new Object[] { sqliteDatabase,
						connectionSource,context});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param clazz 接口类
	 * @param initClassNames 已在配置文件中已注册，并实现了该接口的类
	 * @return 所有实现该接口的类
	 */
	@SuppressWarnings("unchecked")
	private <T> List<Class<T>> getAllSubClass(Class<T> clazz,
			String[] initClassNames) {
		ArrayList<Class<T>> list = new ArrayList<Class<T>>();
		for (String className : initClassNames) {
			try {
				list.add((Class<T>) Class.forName(className));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) {
		try {
			return super.getDao(clazz);
		} catch (Exception e) {
			Log.e("MyCarDbHelper", e.toString());
			return null;
		}
	}

}
