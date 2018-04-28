package com.cnlaunch.mycar;

/**
 * 
 * <功能简述> 首页显示车云网摘要信息格式
 * <功能详细描述>
 * @author xiangyuanmao
 * @version 1.0 2012-5-18
 * @since DBS V100
 */
public class DBSCarInfo
{
    public String label; // 标签
    public String count; // 数量
    public String unit; // 单位
    
    /**
     * 构造器
     * 类初始化时调用的构造器
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
