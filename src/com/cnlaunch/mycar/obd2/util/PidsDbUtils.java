package com.cnlaunch.mycar.obd2.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import launch.obd2.OBD2SearchIdUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.obd2.db.DBOpenHelper;

/**
 * 
 * <���ܼ���>pid��ȡ���ݿ����������
 * <������ϸ����>
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class PidsDbUtils
{
    private DBOpenHelper helper;
    private OBD2SearchIdUtils searchIdUtils;
    private String language;
    String obdGgpFileName;
    public PidsDbUtils(Context context) {
        super();
        helper = new DBOpenHelper(context);
        searchIdUtils = new OBD2SearchIdUtils(context);
        /*
         * cn: language = _cn en: language = _en es: language = _es
         */
        language = "_"
                + context.getResources().getString(R.string.obdGgpFileName)
                        .split("_")[1].substring(0, 2);
        obdGgpFileName = context.getResources().getString(R.string.obdGgpFileName);
    }
    
 // ����һ��������pid��Ӧ����������Ϣ�����pidsInformation
    public void insertPidsInformation(int[] pids) {
        if (pids != null) {
            for (int pid : pids) {
                insertPidInformation(pid);
            }
        }
    }

    // ���뵱ǰ������pid��Ӧ����������Ϣ�����pidsInformation
    public void insertPidInformation(int pid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (!if_has_pid(pid)) {
            ContentValues values = getPrepareInsertPids(pid);
            if (values != null) {
                db.insert("pidsInformation" + language, null, values);
            }
        }
        // if (db.isOpen()) {
        // db.close();
        // }
    }

    // ����������pid�õ�Ҫ���뻺���pidsInformation��contentValues
    public ContentValues getPrepareInsertPids(int pid) {
        ContentValues values = new ContentValues();
        String message = searchIdUtils.getMessage(0x00000000 + pid * 0x100, 3);
        Log.i("message", message);
        if (message != null && !"buffer is NULL".equals(message.trim())) {
            // ���洢��������pid
            values.put("pid", pid);
            String[] messages = message.split("\0");
            // ���洢����������λ
            if (messages.length > 1) {
                String unit = messages[1];
                values.put("unit", unit);
            }

            String informationContent = messages[0];
            String[] informations = informationContent.split(",");
            // ���洢������������
            if ("_cn".equals(language.trim())) {
                String pidName_simple = informations[0];
                values.put("pidName", pidName_simple);
                values.put("pidName_simple", pidName_simple);
            }

            if ("_en".equals(language.trim())) {
                // try {
                String pidName_simple = informations[0];
                values.put("pidName", pidName_simple);
                // String pidName_simple = informations[1];
                values.put("pidName_simple", pidName_simple);
                // } catch (Exception e) {
                // String pidName = informations[0];
                // values.put("pidName", pidName);
                // }
            }

            if ("_es".equals(language.trim())) {
                // try {
                String pidName_simple = informations[0];
                values.put("pidName", pidName_simple);
                // String pidName_simple = informations[1];
                values.put("pidName_simple", pidName_simple);
                // } catch (Exception e) {
                // String pidName = informations[0];
                // values.put("pidName", pidName);
                // }
            }

            // if ("_en".equals(language.trim())) {
            // try {
            // String pidName = informations[0];
            // values.put("pidName", pidName);
            // String pidName_simple = informations[1];
            // values.put("pidName_simple", pidName_simple);
            // } catch (Exception e) {
            // String pidName = informations[0];
            // values.put("pidName", pidName);
            // }
            // }

            // if("_es".equals(language.trim())){
            // try {
            // String pidName = informations[0];
            // values.put("pidName", pidName);
            // String pidName_simple = informations[1];
            // values.put("pidName_simple", pidName_simple);
            // } catch (Exception e) {
            // String pidName = informations[0];
            // values.put("pidName", pidName);
            // }
            // }

            if (informations.length > 2) {
                String minANDmax = informations[2];
                String[] minORmax = minANDmax.split("\\|");
                // ���惦������������Сֵ
                try {
                    int min = Integer.parseInt(minORmax[0]);
                    values.put("min", min);
                } catch (Exception e) {
                    try {
                        throw new Exception("����������������");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                // ���洢�������������ֵ
                try {
                    int max = Integer.parseInt(minORmax[1]);
                    values.put("max", max);
                } catch (Exception e) {
                    try {
                        throw new Exception("����������������");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return values;
        } else {
            return null;
        }
    }
    
 // ����һ��������pid�黺���õ���Ӧ��һ������������
    public List<String> hwygetPidNames(byte[] pids) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> pidNames = new ArrayList<String>();
        for (int i = 0; i < pids.length; i++) {
            if (!if_has_pid(pids[i])) {
                insertPidInformation(pids[i]);
                Log.i("insert", "do insert! ");
            }
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pidName" }, "pid=?", new String[] { pids[i]
                            + "" }, null, null, null);
            try {
                c.moveToFirst();
                String pidName = c.getString(c.getColumnIndex("pidName"));
                pidNames.add(pidName);
            } catch (Exception e) {
                Log.i("uError", "There is a mistake!" + "," + pids[i]);
            }
            if (!c.isClosed())
                c.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return pidNames;
    }

    // ����һ��������pid�黺���õ���Ӧ��һ������������
    public List<String> getPidNames(int[] pids) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> pidNames = new ArrayList<String>();
        for (int i = 0; i < pids.length; i++) {
            if (!if_has_pid(pids[i])) {
                insertPidInformation(pids[i]);
                Log.i("insert", "do insert! ");
            }
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pidName" }, "pid=?", new String[] { pids[i]
                            + "" }, null, null, null);
            try {
                c.moveToFirst();
                String pidName = c.getString(c.getColumnIndex("pidName"));
                pidNames.add(pidName);
            } catch (Exception e) {
                Log.i("uError", "There is a mistake!" + "," + pids[i]);
            }
            if (!c.isClosed())
                c.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return pidNames;
    }

    // ����һ��������pid�黺���õ���Ӧ��һ������������
    public List<String> getPidNames_s(List<Integer> pids) {
        if (pids == null) {
            return null;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            List<String> pidNames = new ArrayList<String>();
            for (int i = 0; i < pids.size(); i++) {
                if (!if_has_pid(pids.get(i))) {
                    insertPidInformation(pids.get(i));
                    Log.i("insert", "do insert! ");
                }
                Cursor c = db.query("pidsInformation" + language,
                        new String[] { "pidName_simple" }, "pid=?",
                        new String[] { pids.get(i) + "" }, null, null, null);
                try {
                    c.moveToFirst();
                    String pidName = c.getString(c
                            .getColumnIndex("pidName_simple"));
                    pidNames.add(pidName);
                } catch (Exception e) {

                    Log.i("uError", "There is a mistake!" + "," + pids.get(i));
                }
                if (!c.isClosed())
                    c.close();
            }
            if (db.isOpen()) {
                db.close();
            }
            return pidNames;
        }
    }

    // �������������Ƶõ���Ӧ����������λ
    public String getPidUnit(String pidName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("pidsInformation" + language,
                new String[] { "unit" }, "pidName=?", new String[] { pidName },
                null, null, null);
        String unit = "";
        try {
            c.moveToFirst();
            unit = c.getString(c.getColumnIndex("unit"));
            if (unit == null) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        if (!c.isClosed())
            c.close();
        // if (db.isOpen()) {
        // db.close();
        // }
        return unit;
    }

    // �������������Ƶõ���Ӧ����������λ
    public String getPidUnit_s(String pidName_s) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("pidsInformation" + language,
                new String[] { "unit" }, "pidName_simple=?",
                new String[] { pidName_s }, null, null, null);
        String unit = "";
        try {
            c.moveToFirst();
            unit = c.getString(c.getColumnIndex("unit"));
            if (unit == null) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        if (!c.isClosed())
            c.close();
        // if (db.isOpen()) {
        // db.close();
        // }
        return unit;
    }

    // �������������Ƶõ���Ӧ�����������ֵ
    public Integer getPidMax(String pidName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("pidsInformation" + language,
                new String[] { "max" }, "pidName=?", new String[] { pidName },
                null, null, null);
        Integer pidMax = null;
        try {
            c.moveToFirst();
            pidMax = c.getInt(c.getColumnIndex("max"));
            Log.i("number", "max" + pidMax);
        } catch (Exception e) {
            return null;
        } finally {
            if (!c.isClosed())
                c.close();
            // if (db.isOpen()) {
            // db.close();
            // }
        }
        return pidMax;
    }

    // �������������Ƶõ���Ӧ����������Сֵ
    public Integer getPidMin(String pidName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("pidsInformation" + language,
                new String[] { "min" }, "pidName=?", new String[] { pidName },
                null, null, null);
        Integer pidMin = null;
        try {
            c.moveToFirst();
            pidMin = c.getInt(c.getColumnIndex("min"));
            Log.i("number", "min" + pidMin);
        } catch (Exception e) {
            return null;
        } finally {
            if (!c.isClosed())
                c.close();
            if (db.isOpen()) {
                db.close();
            }
        }
        return pidMin;
    }

    // �������������Ƶõ���Ӧ��������pid
    public Integer getPid(String pidName) {
        if (pidName == null) {
            return null;
        }
        if ("".equals(pidName)) {
            return 0;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pid" }, "pidName=?",
                    new String[] { pidName }, null, null, null);
            Integer pid = null;
            try {
                c.moveToFirst();
                pid = c.getInt(c.getColumnIndex("pid"));
            } catch (Exception e) {
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            // if (db.isOpen()) {
            // db.close();
            // }
            return pid;
        }
    }

    // ������������Ƶõ���Ӧ��������pid
    public Integer getPid_s(String pidName_simple) {
        if (pidName_simple == null) {
            return null;
        }
        if ("".equals(pidName_simple)) {
            return 0;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pid" }, "pidName_simple=?",
                    new String[] { pidName_simple }, null, null, null);
            Integer pid = null;
            try {
                c.moveToFirst();
                pid = c.getInt(c.getColumnIndex("pid"));
            } catch (Exception e) {
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            // if (db.isOpen()) {
            // db.close();
            // }
            return pid;
        }
    }

    // �ж������pidsInformation���Ƿ��Ѱ�����ǰpid
    public Boolean if_has_pid(int pid) {

        Cursor c = helper.getReadableDatabase().query(
                "pidsInformation" + language, new String[] { "pidName" },
                "pid=?", new String[] { pid + "" }, null, null, null);
        try {
            c.moveToFirst();
            String pidName = c.getString(c.getColumnIndex("pidName"));
            if (pidName != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        } finally {
            c.close();
        }
    }

    // ���ݵ�ǰ��������ѡ��õ���Ӧ��������pid��
    public int[] getPids(String currentChoiceMessage) {
        if (currentChoiceMessage == null) {
            return null;
        }
        String[] currentPidNames = currentChoiceMessage.split("\n");
        int[] pids = new int[currentPidNames.length];
        for (int i = 0; i < currentPidNames.length; i++) {
            Integer pid = getPid(currentPidNames[i]);
            if (pid != null) {
                pids[i] = pid;

            }
        }
        return pids;
    }

    // ���ݵ�ǰ��������ѡ��õ���Ӧ��������pid��
    public List<Integer> getPids(List<String> pidNames) {
        if (pidNames == null) {
            return null;
        } else {
            List<Integer> pids = new ArrayList<Integer>();
            for (int i = 0; i < pidNames.size(); i++) {
                Integer c = getPid(pidNames.get(i));
                if (c != null && c != 0 && !pids.contains(c))
                    pids.add(c);
            }
            return pids;
        }
    }

    // ���ݵ�ǰ��������ѡ��õ���Ӧ��pidName������
    public List<String> getSimplePidName(List<String> pidNames) {
        List<String> simplePidNames = new ArrayList<String>();
        int size = pidNames.size();
        for (int i = size - 1; i >= 0; i--) {
            String pidName = pidNames.get(i);
            if (!simplePidNames.contains(pidName)) {
                simplePidNames.add(pidName);
            }
        }
        return simplePidNames;
    }

    // ���ݵ�ǰ�����������Ƶõ���Ӧ����������Ϣ��ʾ
    public String getPidInformation(String pidName) {
        int pid = getPid(pidName);
        byte[] informationBuffer = searchIdUtils.getTextFromLibReturnByte(
                0x00 + 0x100 * pid, 4);
        for (int i = 0; i < informationBuffer.length; i++) {
            if (informationBuffer[i] == 0x00) {
                informationBuffer[i] = 32;
            }
        }
        String information = "";
        try {
            information = new String(informationBuffer, "gb2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return information;
    }

    // ���ݵ�ǰ��������pid�õ���Ӧ����������Ϣ��ʾ
    public String getPidInformation(int pid) {
        byte[] informationBuffer = searchIdUtils.getTextFromLibReturnByte(
                0x00 + 0x100 * pid, 4);
        for (int i = 0; i < informationBuffer.length; i++) {
            if (informationBuffer[i] == 0x00) {
                informationBuffer[i] = 32;
            }
        }
        String information = "";
        try {
            information = new String(informationBuffer, "gb2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("hi", "yes");
        }
        return information;
    }

    // ���ݵ�ǰ���������Ƶõ����������ļ�д����
    public String getPidName_s(String pidName) {
        if (pidName == null) {
            return null;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pidName_simple" }, "pidName=?",
                    new String[] { pidName }, null, null, null);
            String unit = "";
            try {
                c.moveToFirst();
                unit = c.getString(c.getColumnIndex("pidName_simple"));
                if (unit == null) {
                    return "";
                }
            } catch (Exception e) {
                // /*hwy 20120413 16:15�޸�*/ return
                // "obd2_cn.ggp".equals(ggpFileName) ? pidName : "";
                return ("obd2" + language + ".ggp").equals(obdGgpFileName) ? pidName
                        : "";
            }
            if (!c.isClosed())
                c.close();
            // if (db.isOpen()) {
            // db.close();
            // }
            return unit;
        }
    }

    public String getPidName(String panel_choice_s) {
        // TODO Auto-generated method stub
        if (panel_choice_s == null) {
            return null;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("pidsInformation" + language,
                    new String[] { "pidName" }, "pidName_simple=?",
                    new String[] { panel_choice_s }, null, null, null);
            String unit = "";
            try {
                c.moveToFirst();
                unit = c.getString(c.getColumnIndex("pidName"));
                if (unit == null) {
                    return "";
                }
            } catch (Exception e) {
                return ("obd2" + language + ".ggp").equals(obdGgpFileName) ? panel_choice_s
                        : "";
                // /*hwy 20120413 16:15�޸�*/return "";
            }
            if (!c.isClosed())
                c.close();
            // if (db.isOpen()) {
            // db.close();
            // }
            return unit;
        }
    }
}
