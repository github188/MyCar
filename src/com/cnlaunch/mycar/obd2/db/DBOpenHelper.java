package com.cnlaunch.mycar.obd2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * <功能简述>数据流数据库
 * <功能详细描述> 多语言要用到，
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class DBOpenHelper extends SQLiteOpenHelper
{

    private static final String DATABASENAME = "DBS.db"; // 数据库名称
    private static final int DATABASEVERSION = 2;// 数据库版本

    public DBOpenHelper(Context context)
    {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table pidsInformation_cn(id integer primary key autoincrement, pid integer unique,pidName varchar,pidName_simple varchar,unit varchar,min integer,max integer)");
        db.execSQL("create table pidsInformation_en(id integer primary key autoincrement, pid integer unique,pidName varchar,pidName_simple varchar,unit varchar,min integer,max integer)");
        db.execSQL("create table pidsInformation_es(id integer primary key autoincrement, pid integer unique,pidName varchar,pidName_simple varchar,unit varchar,min integer,max integer)");

        db.execSQL("create table dataStreamChoice_cn(id integer primary key autoincrement ,message varchar unique,time varchar)");
        db.execSQL("create table dataStreamChoice_en(id integer primary key autoincrement ,message varchar unique,time varchar)");
        db.execSQL("create table dataStreamChoice_es(id integer primary key autoincrement ,message varchar unique,time varchar)");

        db.execSQL("insert into dataStreamChoice_cn(message) values('0,发动机转数--1,车速--2,空气流量--3,节气门位置--4,发动机计算负荷')");
        db.execSQL("insert into dataStreamChoice_en(message) values('0,Engine RPM--1,Vehicle Speed--2,Air Flow Rate from Mass Air Flow Sensor--3,Absolute Throttle Position--4,Calculated LOAD Value')");
        db.execSQL("insert into dataStreamChoice_es(message) values('0,RPM del motor--1,Sensor de velocidad del vehículo--2,Rango de flujo de aire desde el caudalímetro--3,Posición Absoluta de la Mariposa--4,Valor de CARGA calculado')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS pidsInformation_cn");
        db.execSQL("DROP TABLE IF EXISTS dataStreamChoice_cn");

        db.execSQL("DROP TABLE IF EXISTS pidsInformation_en");
        db.execSQL("DROP TABLE IF EXISTS dataStreamChoice_en");

        db.execSQL("DROP TABLE IF EXISTS pidsInformation_es");
        db.execSQL("DROP TABLE IF EXISTS dataStreamChoice_es");

        onCreate(db);
    }
}
