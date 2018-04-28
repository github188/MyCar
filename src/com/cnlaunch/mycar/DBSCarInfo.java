package com.cnlaunch.mycar;

/**
 * 
 * <���ܼ���> ��ҳ��ʾ������ժҪ��Ϣ��ʽ
 * <������ϸ����>
 * @author xiangyuanmao
 * @version 1.0 2012-5-18
 * @since DBS V100
 */
public class DBSCarInfo
{
    public String label; // ��ǩ
    public String count; // ����
    public String unit; // ��λ
    
    /**
     * ������
     * ���ʼ��ʱ���õĹ�����
     * @param label
     * @param count
     * @param unit
     * @since DBS V100
     */
    public DBSCarInfo(String label, String count, String unit)
    {
        this.label = label;
        this.count = count;
        this.unit = unit;
        
    }
    
    public DBSCarInfo(String displayInfo)
    {
        this.label = displayInfo;
    }
}
