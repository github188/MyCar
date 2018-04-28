package com.cnlaunch.mycar.obd2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * <���ܼ���>���������ݿ�
 * <������ϸ����> ������Ҫ�õ���
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class DBOpenHelper extends SQLiteOpenHelper
{

    private static final String DATABASENAME = "DBS.db"; // ���ݿ�����
    private static final int DATABASEVERSION = 2;// ���ݿ�汾

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

        db.execSQL("insert into dataStreamChoice_cn(message) values('0,������ת��--1,����--2,��������--3,������λ��--4,���������㸺��')");
        db.execSQL("insert into dataStreamChoice_en(message) values('0,Engine RPM--1,Vehicle Speed--2,Air Flow Rate from Mass Air Flow Sensor--3,Absolute Throttle Position--4,Calculated LOAD Value')");
        db.execSQL("insert into dataStreamChoice_es(message) values('0,RPM del motor--1,Sensor de velocidad del veh��culo--2,Rango de flujo de aire desde el caudal��metro--3,Posici��n Absoluta de la Mariposa--4,Valor de CARGA calculado')");
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
