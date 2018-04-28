package com.cnlaunch.mycar.obd2.model;

public class DataFlowIds {
	/**************************** ��ʽ�������� ********************************************/
	/*
	 * ���������ƣ�˲ʱ�ͺ�
	 */
	public static final int INSTANTANEOUSFUELCONSUMPTION = 0;
	/*
	 * ���������ƣ�������ת�� ��Ӧpid��0x0C value���䣺0~262140 ��λ��rpm
	 */
	public static final int ENGINESPEED = 1;
	/*
	 * ���������ƣ����� ��Ӧpid��0x0d ��λ��km/h
	 */
	public static final int CARSPEED = 2;
	/*
	 * ���ٶ�
	 */
	public static final int Acceleration = 3;
	/*
	 * ���������ƣ��߻����¶� �������������ƣ� �߻����¶�(����1������1),CATEMP11","��" 0x3c
	 * �߻����¶�(����2������1),CATEMP21","��" 0x3d �߻����¶�(����1������2),CATEMP12","��" 0x3e
	 * �߻����¶�(����2������2),CATEMP22","��" 0x3f ��Ӧpid�� �6�140��C~6513.5 ��C
	 */
	public static final int CATALYSTTEMPERATURE = 4;
	/*
	 * ���������ƣ�ȼ�����������ڣ�
	 */
	public static final int fuelAmendmentsToLongTerm = 5;

	/*
	 * ���������ƣ������ǰ�� ��Ӧpid��0x0e value���䣺-64~63.5 ��
	 */
	public static final int IGNITIONADVANCEANGLE = 6;
	/*
	 * ���������ƣ�ȼ�����������ڣ�
	 */
	public static final int fuelAmendmentsToShortTerm = 7;
	/*
	 * ���������ƣ���ȴҺ�¶� ��Ӧpid��0x5 value��Χ��-40~215 ��
	 */
	public static final int COOLANTTEMPERATURE = 8;
	/*
	 * ���������ƣ������¶� ��Ӧpid��0xf value��Χ��-40~215 ��
	 */
	public static final int INTAKEAIRTEMPERATURE = 9;

	/*
	 * ���������ƣ�����ѹ�� ��Ӧpid��0x33 0~255 kPa
	 */
	public static final int ATMOSPHERICPRESSURE = 11;
	/*
	 * ���������ƣ����������� ��Ӧpid��0x04 0~100 %
	 */
	public static final int ENGINELOAD = 12;
	/*
	 * ���������ƣ��������� ��Ӧpid��0x10 0~655.35 g/s
	 */
	public static final int AIRFLOW = 13;
    /*
     * ���������ƣ�ʣ������
     */
    public static final int REMAININGFUEL = 10;
	   /*
     * �ͺ��ۼ�ֵ
     */
    public static final int OIAL_ALL = 21;
    /*
     * ��ȡ˲ʱ�ͺĴ���
     */
    public static final int OIAL_TIME = 22;
	/*
	 * �������
	 */
	public static final int Mileage = 15;

	/*
	 * �ܶ��ͺ�
	 */
	public static final int moreFuelConsumption = 16;
	/*
	 * ���������ƣ�ÿСʱ�ͺ�
	 */
	public static final int HOURLYFUELCONSUMPTION = 17;
	/*
	 * ���������ƣ�ƽ���ͺ�
	 */
	public static final int averageFuelConsumption = 18;
	/*
	 * ���������ƣ�����
	 */
	public static final int TEST = 19;
	/*
	 * ���������ƣ��͹�ѹ��
	 */
	public static final int fuelRailPressure = 20;

	/*
	 * ��ƿ��ѹ
	 */
	public static final int BatteryVoltage = 23;
	/*
	 * �����¶�
	 */
	public static final int AmbientTemperature = 24;
	   /*
     * ���������ƣ�����ѹ�� ��Ӧpid��0x0b 0~255 kPa
     */
    public static final int INLETPRESSURE = 14;
}
