package com.cnlaunch.mycar.obd2.util;

import java.util.HashMap;
import java.util.Map;

import com.cnlaunch.mycar.obd2.model.DataFlowIds;

/**
 * <���ܼ���>���������ƺ�pid��ӳ�� <������ϸ����>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class DiagnosisDataMapping {
	public static Map<Integer, Byte> getData() {
		Map<Integer, Byte> diagnosisDataMap = new HashMap<Integer, Byte>();
		diagnosisDataMap.put(DataFlowIds.TEST, (byte) 0xff);// 
		diagnosisDataMap.put(DataFlowIds.ENGINESPEED, (byte) 0x0C);// ������ת��
		diagnosisDataMap.put(DataFlowIds.CARSPEED, (byte) 0x0d);// ����
		diagnosisDataMap.put(DataFlowIds.COOLANTTEMPERATURE, (byte) 0x05);// ��ȴҺ�¶�
		diagnosisDataMap.put(DataFlowIds.INTAKEAIRTEMPERATURE, (byte) 0x0f);// �����¶�
		diagnosisDataMap.put(DataFlowIds.ATMOSPHERICPRESSURE, (byte) 0x33);// ����ѹ��
		diagnosisDataMap.put(DataFlowIds.ENGINELOAD, (byte) 0x04);// ����������
		diagnosisDataMap.put(DataFlowIds.AIRFLOW, (byte) 0x10);// ��������
		diagnosisDataMap.put(DataFlowIds.INLETPRESSURE, (byte) 0x0b);// ����ѹ��
		diagnosisDataMap.put(DataFlowIds.CATALYSTTEMPERATURE, (byte) 0x3c);// �߻����¶�
		diagnosisDataMap.put(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION,
				(byte) 0x10);// ͨ�����������ͳ��ٵõ�˲ʱ�ͺ�
		diagnosisDataMap.put(DataFlowIds.REMAININGFUEL, (byte) 0x2f);// ʣ������  /*ȼ��Һλ����*/
		diagnosisDataMap.put(DataFlowIds.Acceleration, (byte) 0x0d);// ���ٶ�
		diagnosisDataMap.put(DataFlowIds.fuelAmendmentsToLongTerm, (byte) 0X07);// ȼ�����������ڣ�
		diagnosisDataMap
				.put(DataFlowIds.fuelAmendmentsToShortTerm, (byte) 0X06);// ȼ�����������ڣ�
		diagnosisDataMap.put(DataFlowIds.Mileage, (byte) 0x10);// �������
		diagnosisDataMap.put(DataFlowIds.moreFuelConsumption, (byte) 0x10);//
		diagnosisDataMap.put(DataFlowIds.IGNITIONADVANCEANGLE, (byte) 0x0e);// �����ǰ��

		diagnosisDataMap.put(DataFlowIds.fuelRailPressure, (byte) 0x0a);// �͹�ѹ��(��ѹ��)

		diagnosisDataMap.put(DataFlowIds.HOURLYFUELCONSUMPTION, (byte) 0x10);// ÿСʱ�ͺ�
		diagnosisDataMap.put(DataFlowIds.averageFuelConsumption, (byte) 0x10);// ƽ���ͺ�

		diagnosisDataMap.put(DataFlowIds.BatteryVoltage, (byte) 0x42);// ��ƿ��ѹ
		diagnosisDataMap.put(DataFlowIds.AmbientTemperature, (byte) 0x46);// �����¶�
		return diagnosisDataMap;
	}
}