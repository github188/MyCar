package com.cnlaunch.mycar.obd2.model;

public class DataFlowIds {
	/**************************** 正式环境数据 ********************************************/
	/*
	 * 数据流名称：瞬时油耗
	 */
	public static final int INSTANTANEOUSFUELCONSUMPTION = 0;
	/*
	 * 数据流名称：发动机转速 对应pid：0x0C value区间：0~262140 单位：rpm
	 */
	public static final int ENGINESPEED = 1;
	/*
	 * 数据流名称：车速 对应pid：0x0d 单位：km/h
	 */
	public static final int CARSPEED = 2;
	/*
	 * 加速度
	 */
	public static final int Acceleration = 3;
	/*
	 * 数据流名称：催化剂温度 数据流二级名称： 催化剂温度(缸组1传感器1),CATEMP11","℃" 0x3c
	 * 催化剂温度(缸组2传感器1),CATEMP21","℃" 0x3d 催化剂温度(缸组1传感器2),CATEMP12","℃" 0x3e
	 * 催化剂温度(缸组2传感器2),CATEMP22","℃" 0x3f 对应pid： −40°C~6513.5 °C
	 */
	public static final int CATALYSTTEMPERATURE = 4;
	/*
	 * 数据流名称：燃油修正（长期）
	 */
	public static final int fuelAmendmentsToLongTerm = 5;

	/*
	 * 数据流名称：点火提前角 对应pid：0x0e value区间：-64~63.5 °
	 */
	public static final int IGNITIONADVANCEANGLE = 6;
	/*
	 * 数据流名称：燃油修正（短期）
	 */
	public static final int fuelAmendmentsToShortTerm = 7;
	/*
	 * 数据流名称：冷却液温度 对应pid：0x5 value范围：-40~215 ℃
	 */
	public static final int COOLANTTEMPERATURE = 8;
	/*
	 * 数据流名称：进气温度 对应pid：0xf value范围：-40~215 ℃
	 */
	public static final int INTAKEAIRTEMPERATURE = 9;

	/*
	 * 数据流名称：大气压力 对应pid：0x33 0~255 kPa
	 */
	public static final int ATMOSPHERICPRESSURE = 11;
	/*
	 * 数据流名称：发动机负荷 对应pid：0x04 0~100 %
	 */
	public static final int ENGINELOAD = 12;
	/*
	 * 数据流名称：空气流量 对应pid：0x10 0~655.35 g/s
	 */
	public static final int AIRFLOW = 13;
    /*
     * 数据流名称：剩余油量
     */
    public static final int REMAININGFUEL = 10;
	   /*
     * 油耗累计值
     */
    public static final int OIAL_ALL = 21;
    /*
     * 获取瞬时油耗次数
     */
    public static final int OIAL_TIME = 22;
	/*
	 * 续航里程
	 */
	public static final int Mileage = 15;

	/*
	 * 很多油耗
	 */
	public static final int moreFuelConsumption = 16;
	/*
	 * 数据流名称：每小时油耗
	 */
	public static final int HOURLYFUELCONSUMPTION = 17;
	/*
	 * 数据流名称：平均油耗
	 */
	public static final int averageFuelConsumption = 18;
	/*
	 * 数据流名称：充数
	 */
	public static final int TEST = 19;
	/*
	 * 数据流名称：油轨压力
	 */
	public static final int fuelRailPressure = 20;

	/*
	 * 电瓶电压
	 */
	public static final int BatteryVoltage = 23;
	/*
	 * 环境温度
	 */
	public static final int AmbientTemperature = 24;
	   /*
     * 数据流名称：进气压力 对应pid：0x0b 0~255 kPa
     */
    public static final int INLETPRESSURE = 14;
}
