package com.cnlaunch.mycar.obd2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import launch.obd2.OBD2SearchIdUtils;
import android.content.Context;
import android.util.Log;

/**
 * 
 * <���ܼ���>��¼��ȡ�������ݵĸ�������
 * <������ϸ����>
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class GetDataWithOBD2
{
    private OBD2SearchIdUtils searchIdUtils;
    private PidsDbUtils dbUtils;
    public GetDataWithOBD2(Context context)
    {
        searchIdUtils = new OBD2SearchIdUtils(context);
        dbUtils = new PidsDbUtils(context);
    }

    // �õ�������
    public List<Map<String, Object>> getCodes(byte[] content)
    {
        if (content != null && content.length != 0)
        {
            List<Map<String, Object>> codes = new ArrayList<Map<String, Object>>();
            for (int i = 0; 3 * i + 2 < content.length; i++)
            {
                try
                {
                    Map<String, Object> code_item = new HashMap<String, Object>();
                    byte[] codeBuffer = searchIdUtils.getTextFromLibReturnByte((content[3 * i] & 0xff) * 0x100 + (content[3 * i + 1] & 0xff), 6);
                    for (int j = codeBuffer.length - 1; j >= 0; j--)
                    {
                        if (codeBuffer[j] == 0)
                        {
                            codeBuffer[j] = 32;
                        }
                    }
                    byte[] re = { (byte) content[3 * i], (byte) content[3 * i + 1] };
                    String code_t = new String(searchIdUtils.getResultWithCalc((short) 0x02, re), "gb2312");
                    String code = "";
                    if (codeBuffer != null && codeBuffer.length > 0)
                    {
                        code = new String(codeBuffer, "gb2312").substring(6);
                    }
                    if (content[3 * i + 2] == 0x02)
                    {
                        // /*hwy 2012-04-14*/code_t += "pd";
                    }
                    code_item.put("title", code_t);
                    if (code != null && code != "")
                    {
                        code_item.put("message", code);
                    }
                    else
                    {
                        code = "û�в�ѯ��������";
                        code_item.put("message", code);
                    }
                    codes.add(code_item);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return codes;
        }
        else
        {
            return null;
        }
    }
    
 // ����������֧��pid��all���õ�������������ʾ
    // �õ�ȫ��������������ʾ
    public List<Map<String, Object>> getAllDataStream()
    {
        List<String> pidNames = null;
        // �õ�֧�ֵ�������pid
        if (DataExchange.getPids() != null && DataExchange.getPids().length != 0)
        {
            pidNames = dbUtils.hwygetPidNames(DataExchange.getPids());
        }
        // ���ݵõ���������pidȥ��������
        List<Map<String, Object>> dataStreams = new ArrayList<Map<String, Object>>();
        for (int i = 0; 4 * i + 3 < DataExchange.getPidConvertedIntoDataStreamData().length; i++)
        {
            Map<String, Object> dataStreamMap = new HashMap<String, Object>();
            try
            {
                byte[] dataBuffer = { DataExchange.getPidConvertedIntoDataStreamData()[4 * i], DataExchange.getPidConvertedIntoDataStreamData()[4 * i + 1],
                    DataExchange.getPidConvertedIntoDataStreamData()[4 * i + 2], DataExchange.getPidConvertedIntoDataStreamData()[4 * i + 3] };
                // byte[] dataStreamBuffer
                // =searchIdUtils.getResultWithCalc(pids[i], dataBuffer);
                byte[] dataStreamBuffer = searchIdUtils.getResultWithCalc(DataExchange.getPids()[i], dataBuffer);
                String s = new String(dataStreamBuffer, "UTF-8");
                String pidName = null;
                if (pidNames != null)
                {
                    pidName = pidNames.get(i);
                }
                // String pidInformation = dbUtils.getPidInformation(pids[i]);
                String pidInformation = dbUtils.getPidInformation(DataExchange.getPids()[i]);
                if (pidName != null || "".equals(pidName))
                {// hwy 2012-04-16 �����������
                    dataStreamMap.put("pidName", pidName);
                }

                if (pidInformation != null && !"".equals(pidInformation.trim()))
                {
                    dataStreamMap.put("pidInformation", pidInformation);
                }
                else
                {
                    dataStreamMap.put("pidInformation", "�Բ���û���ҵ������������ʾ��Ϣ��");
                }
                String pidUnit = dbUtils.getPidUnit(pidName);
                if (!s.trim().equals("C3437"))
                    dataStreamMap.put("value", s + "  " + pidUnit);
                // �ж�ֵ �Ƿ�Ϊ�� ������ ��list
                if (s != null && s.length() > 0)
                {

                    dataStreams.add(dataStreamMap);
                }
                Log.i("pidNameNum", pidNames.size() + "");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i("cError", "this is a mistake!");
            }
        }
        return dataStreams;
    }
}
