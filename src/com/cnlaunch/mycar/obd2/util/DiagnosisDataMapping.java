package com.cnlaunch.mycar.obd2.util;

import java.util.HashMap;
import java.util.Map;

import com.cnlaunch.mycar.obd2.model.DataFlowIds;

/**
 * <功能简述>数据流名称和pid的映射 <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class DiagnosisDataMapping {
	public static Map<Integer, Byte> getData() {
		Map<Integer, Byte> diagnosisDataMap = new HashMap<Integer, Byte>();
		diagnosisDataMap.put(DataFlowIds.TEST, (byte) 0xff);// 
		diagnosisDataMap.put(DataFlowIds.ENGINESPEED, (byte) 0x0C);// 发动机转速
		diagnosisDataMap.put(DataFlowIds.CARSPEED, (byte) 0x0d);// 车速
		diagnosisDataMap.put(DataFlowIds.COOLANTTEMPERATURE, (byte) 0x05);// 冷却液温度
		diagnosisDataMap.put(DataFlowIds.INTAKEAIRTEMPERATURE, (byte) 0x0f);// 进气温度
		diagnosisDataMap.put(DataFlowIds.ATMOSPHERICPRESSURE, (byte) 0x33);// 大气压力
		diagnosisDataMap.put(DataFlowIds.ENGINELOAD, (byte) 0x04);// 发动机负载
		diagnosisDataMap.put(DataFlowIds.AIRFLOW, (byte) 0x10);// 空气流量
		diagnosisDataMap.put(DataFlowIds.INLETPRESSURE, (byte) 0x0b);// 进气压力
		diagnosisDataMap.put(DataFlowIds.CATALYSTTEMPERATURE, (byte) 0x3c);// 催化剂温度
		diagnosisDataMap.put(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION,
				(byte) 0x10);// 通过空气流量和车速得到瞬时油耗
		diagnosisDataMap.put(DataFlowIds.REMAININGFUEL, (byte) 0x2f);// 剩余油量  /*燃油液位输入*/
		diagnosisDataMap.put(DataFlowIds.Acceleration, (byte) 0x0d);// 加速度
		diagnosisDataMap.put(DataFlowIds.fuelAmendmentsToLongTerm, (byte) 0X07);// 燃油修正（长期）
		diagnosisDataMap
				.put(DataFlowIds.fuelAmendmentsToShortTerm, (byte) 0X06);// 燃油修正（短期）
		diagnosisDataMap.put(DataFlowIds.Mileage, (byte) 0x10);// 续航里程
		diagnosisDataMap.put(DataFlowIds.moreFuelConsumption, (byte) 0x10);//
		diagnosisDataMap.put(DataFlowIds.IGNITIONADVANCEANGLE, (byte) 0x0e);// 点火提前角

		diagnosisDataMap.put(DataFlowIds.fuelRailPressure, (byte) 0x0a);// 油轨压力(表压力)

		diagnosisDataMap.put(DataFlowIds.HOURLYFUELCONSUMPTION, (byte) 0x10);// 每小时油耗
		diagnosisDataMap.put(DataFlowIds.averageFuelConsumption, (byte) 0x10);// 平均油耗

		diagnosisDataMap.put(DataFlowIds.BatteryVoltage, (byte) 0x42);// 电瓶电压
		diagnosisDataMap.put(DataFlowIds.AmbientTemperature, (byte) 0x46);// 环境温度
		return diagnosisDataMap;
	}
}