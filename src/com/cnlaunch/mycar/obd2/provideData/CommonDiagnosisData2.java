package com.cnlaunch.mycar.obd2.provideData;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import launch.obd2.OBD2SearchIdUtils;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.obd2.model.DataFlowIds;
import com.cnlaunch.mycar.obd2.model.DataFlowModel;
import com.cnlaunch.mycar.obd2.util.AccelerationRecordsConstants;
import com.cnlaunch.mycar.obd2.util.DiagnosisDataMapping;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

/**
 * <���ܼ���>�����ṩ�������Ǳ���ʾ������ <������ϸ����>
 * @author huangweiyong
 * @version 1.0 2012-5-15
 * @since DBS V100
 */
public class CommonDiagnosisData2 extends Thread implements BluetoothInterface
{


	@Override
	public void GetDataFromService(byte[] databuf, int datalen)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void GetDataTimeout()
	{
		// TODO Auto-generated method stub
		
	}
////    // debugging
//    private static final boolean D = false;
//    private OBD2SearchIdUtils searchIdUtils;
//    public static boolean flag = true;
//    private DataFlowModel model = DataFlowModel.getModel();
//    private Context context = null;
//    public int mEngineSpeed = 0;
//    public int mCarSpeed = 0;
//    private int[] mDataFlows;
//    private byte[] mIntArrayTransitByteArray;
//    public String engineSpeed;// ������ת��
//    public String carSpeed;// ����
//    public String idleTime;// ����ʱ��
//    public String ignitionAdvanceAngle;// �����ǰ��
//    public String coolantTemperature;// �����ǰ��
//    public String intakeairTemperature;// �����¶�
////    private BluetoothDataHandler bluetoothDataHandler;
//    HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
//    ConditionVariable syncLock = new ConditionVariable(false);
//    private float[] oilAnalysisValue = new float[17];
//
//    // �в�������
//    public CommonDiagnosisData(Context context/* , int mDataFlows[] */)
//    {
//        Log.i("CommonDiagnosisData", "������*******************");
//        this.context = context;
////        bluetoothDataHandler = BluetoothDataHandler.BluetoothDataHandInstance(bluetoothResponseHandler);
//        // if (aaa1(mDataFlows) && aaa2(mDataFlows))
//        // {
//        // this.mDataFlows = mDataFlows;
//        // mIntArrayTransitByteArray = new byte[mDataFlows.length];
//
//        // }
//        // else
//        // {
//        // this.mDataFlows = OrderUtils.appendIntArray(mDataFlows, new int[] {
//        // 0x0c, 0x0d });
//        // mIntArrayTransitByteArray = new byte[mDataFlows.length + 2];
//        // intTransitByte();
//        // }
//
//    }
//
//    private static boolean aaa1(int data[])
//    {
//        for (int i = 0; i < data.length; i++)
//        {
//            if (0x0c == data[i])
//            {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static boolean aaa2(int data[])
//    {
//        for (int i = 0; i < data.length; i++)
//        {
//            if (0x0d == data[i])
//            {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static final String bytesToHexStringNoBar(int[] bArray)
//    {
//        StringBuffer sb = new StringBuffer(bArray.length);
//        String sTemp;
//        int len = bArray.length;
//        int last = len - 1;
//        for (int i = 0; i < len; i++)
//        {
//            sTemp = Integer.toHexString(0xFF & bArray[i]);
//            if (sTemp.length() < 2)
//                sb.append("0");
//            sb.append(sTemp.toUpperCase());
//        }
//        return sb.toString();
//    }
//
//    private Handler bluetoothResponseHandler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            switch (msg.what)
//            {
//                case BluetoothDataHandler.CMD_Recive_OneToOne:
//                    byte[] receive = (byte[]) msg.obj;
//                    Log.i("tag", Arrays.toString(receive));
//                    byte[] dpuPackage = OrderUtils.filterReturnDataPackage(receive);
//                    Log.i("tag", Arrays.toString(dpuPackage));
//                    byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
//                    byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);
//                    // �õ�OBD2�����������ֽ�
//
//                    String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);
//
//                    if (cmd_subcmdString.equals("6912"))
//                    {
//                        byte[] obd2DSByte = OrderUtils.filterOBD2CmdParameters(dpuPackage);
//                        if (param.length > 0)
//                        {
//                            try
//                            {
//                                String s = bytesToHexStringNoBar(mDataFlows);
//                                Log.i("mDataFlows", "mDataFlowsǰ �� " + bytesToHexStringNoBar(mDataFlows));
//                                hashMap = getArrayPIDData(mDataFlows, mIntArrayTransitByteArray, obd2DSByte);
//                                Set<Integer> set = hashMap.keySet();
//                                for (Iterator<Integer> iter = set.iterator(); iter.hasNext();)
//                                {
//                                    int key = (Integer) iter.next();
//                                    if (s.equals("0102030D0D"))
//                                    {
//                                        /******************************** ��һ�� begin ***********************************************************/
//                                        // ������ת��
//                                        model.setValue(DataFlowIds.ENGINESPEED, hashMap.get(DataFlowIds.ENGINESPEED));
//                                        // ����
//                                        model.setValue(DataFlowIds.CARSPEED, hashMap.get(DataFlowIds.CARSPEED));
//                                        // ���ٶ�
//                                        // model.setValue(DataFlowIds.Acceleration,
//                                        // hashMap.get(DataFlowIds.Acceleration));
//                                        // ������ٶ�
//                                        long carSpeedTimes1 = 0l;
//                                        long carSpeedTimes2 = 0l;
//                                        if (key == DataFlowIds.Acceleration)
//                                        {
//                                            if (AccelerationRecordsConstants.isRecordTheSpeedOfTime1())
//                                            {
//                                                carSpeedTimes1 = System.currentTimeMillis();
//                                                Log.i("carSpeedTimes", "carSpeedTimes1:" + carSpeedTimes1);
//                                                AccelerationRecordsConstants.setCarSpeedTimes1(carSpeedTimes1);
//                                                AccelerationRecordsConstants.setCarSpeed1((long) Double.parseDouble(hashMap.get(DataFlowIds.CARSPEED)));
//                                                AccelerationRecordsConstants.setRecordTheSpeedOfTime1(false);
//                                                AccelerationRecordsConstants.setRecordTheSpeedOfTime2(true);
//                                            }
//                                            else if (AccelerationRecordsConstants.isRecordTheSpeedOfTime2())
//                                            {
//                                                carSpeedTimes2 = System.currentTimeMillis();
//                                                Log.i("carSpeedTimes", "carSpeedTimes2:" + carSpeedTimes2);
//                                                AccelerationRecordsConstants.setCarSpeedTimes2(carSpeedTimes2);
//                                                AccelerationRecordsConstants.setCarSpeed2((long) Double.parseDouble(hashMap.get(DataFlowIds.CARSPEED)));
//                                                AccelerationRecordsConstants.setRecordTheSpeedOfTime2(false);
//                                                AccelerationRecordsConstants.setRecordTheSpeedOfTime1(true);
//                                            }
//                                            else
//                                            {
//                                            }
//                                            long acceleration = AccelerationRecordsConstants.getCarSpeedTimes2() - AccelerationRecordsConstants.getCarSpeedTimes1();
//                                            Log.i("carSpeedTimes", "acceleration:" + acceleration);
//                                            Log.i("acceleration", "AccelerationRecordsConstants.getCarSpeed2()--" + AccelerationRecordsConstants.getCarSpeed2()
//                                                + ";;AccelerationRecordsConstants.getCarSpeed1()--" + AccelerationRecordsConstants.getCarSpeed1());
//                                            Log.i("acceleration", String.valueOf((AccelerationRecordsConstants.getCarSpeed2() - AccelerationRecordsConstants.getCarSpeed1()) / 2.5));
//                                            if (acceleration < 0)
//                                                acceleration = -acceleration;
//                                            if (acceleration >= 150)
//                                            {
//                                                Log.i("acceleration", "------" + String.valueOf((AccelerationRecordsConstants.getCarSpeed2() - AccelerationRecordsConstants.getCarSpeed1()) / 0.15));
//                                                model.setValue(DataFlowIds.Acceleration,
//                                                    String.valueOf((AccelerationRecordsConstants.getCarSpeed2() - AccelerationRecordsConstants.getCarSpeed1()) / 0.15));
//                                            }
//                                        }
//                                        /******************************** ��һ��end ***********************************************************/
//                                    }
//                                    
//                                    if (s.equals("000A0F1002"))
//                                    {
//
//                                        /******************************** �ڶ��� begin ***********************************************************/
//                                        // ˲ʱ�ͺ�
//                                        model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, hashMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION));
//                                        // ʣ������
//                                        model.setValue(DataFlowIds.REMAININGFUEL, hashMap.get(DataFlowIds.REMAININGFUEL));
//                                        // �������
//                                        model.setValue(DataFlowIds.Mileage, hashMap.get(DataFlowIds.Mileage));
//                                        // �ܶ��ͺ�
//                                        model.setValue(DataFlowIds.moreFuelConsumption, hashMap.get(DataFlowIds.moreFuelConsumption));
//                                        /******************************** �ڶ��� end ***********************************************************/
//                                    }
////
//                                    if (s.equals("0608090D0E"))
//                                    {
//                                        /******************************** ������ begin ***********************************************************/
//
//                                         //�����ǰ��
//                                        model.setValue(DataFlowIds.IGNITIONADVANCEANGLE, hashMap.get(DataFlowIds.IGNITIONADVANCEANGLE));
//                                        
//                                        System.out.println("hashMap.get(DataFlowIds.IGNITIONADVANCEANGLE :" + model.getValue(DataFlowIds.IGNITIONADVANCEANGLE));
//                                        // �����¶�
//                                        model.setValue(DataFlowIds.INTAKEAIRTEMPERATURE, hashMap.get(DataFlowIds.INTAKEAIRTEMPERATURE));
//
//                                        // ��ȴҺ�¶�
//                                        model.setValue(DataFlowIds.COOLANTTEMPERATURE, hashMap.get(DataFlowIds.COOLANTTEMPERATURE));
//                                        // ��������
//                                        model.setValue(DataFlowIds.AIRFLOW, hashMap.get(DataFlowIds.AIRFLOW));
//                                        // ����ѹ��
//                                        model.setValue(DataFlowIds.INLETPRESSURE, hashMap.get(DataFlowIds.INLETPRESSURE));
//                                        /******************************** ������ end ***********************************************************/
//                                    }
////                                    
//                                    if (s.equals("0405070B0C"))
//                                    {
//                                        /******************************** ������ begin ***********************************************************/
//                                        // �߻����¶�
//                                        model.setValue(DataFlowIds.CATALYSTTEMPERATURE, hashMap.get(DataFlowIds.CATALYSTTEMPERATURE));
//
//                                        // ȼ�����������ڣ�
//                                        model.setValue(DataFlowIds.fuelAmendmentsToLongTerm, hashMap.get(DataFlowIds.fuelAmendmentsToLongTerm));
//                                        // ȼ�����������ڣ�
//                                        model.setValue(DataFlowIds.fuelAmendmentsToShortTerm, hashMap.get(DataFlowIds.fuelAmendmentsToShortTerm));
//                                        // ����ѹ��
//                                        model.setValue(DataFlowIds.ATMOSPHERICPRESSURE, hashMap.get(DataFlowIds.ATMOSPHERICPRESSURE));
//                                        // ����������
//                                        model.setValue(DataFlowIds.ENGINELOAD, hashMap.get(DataFlowIds.ENGINELOAD));
//
//                                        /******************************** ������ end ***********************************************************/
//                                    }                                    
//                                }
//                                Log.i("mDataFlows", "mDataFlows�� �� " + bytesToHexStringNoBar(mDataFlows));
//                                model.notifyChange(mDataFlows);
//                            }
//                            catch (NumberFormatException e)
//                            {
//                                e.printStackTrace();
//                                model.notifyChange(mDataFlows);
//                            }
//                            catch (UnsupportedEncodingException e)
//                            {
//                                e.printStackTrace();
//                                model.notifyChange(mDataFlows);
//                            }
//                        }
//                    }
//                    else if (cmd_subcmdString.equals("6902"))
//                    {
//                        getSpecifiedPID();
//                    }
//                    break;
//            }
//        }
//    };
//
//    // �õ�ָ��������PID����
//    private void getSpecifiedPID()
//    {
//        byte[] v_cmd = new byte[] { 0x29, 0x12 };
//        if (mIntArrayTransitByteArray.length != 0)
//        {
//            byte[] v_sendbuf = OrderUtils.appendByteArray(new byte[] { (byte) mIntArrayTransitByteArray.length }, mIntArrayTransitByteArray);
//            if (bluetoothDataHandler != null)
//                bluetoothDataHandler.SendDataToBluetooth(BluetoothDataHandler.CMD_ReadMode, v_cmd, v_sendbuf, v_sendbuf.length, 2500);
//            Log.i("command", Arrays.toString(v_cmd));
//            Log.i("command", Arrays.toString(v_sendbuf));
//        }
//    }
//
//    /*
//     * int����ת����byte����
//     */
//    private byte[] intTransitByte()
//    {
//        if (mIntArrayTransitByteArray.length != 0)
//        {
//            for (int i = 0; i < mDataFlows.length; i++)
//            {
//                mIntArrayTransitByteArray[i] = (byte) DiagnosisDataMapping.getData().get(mDataFlows[i]);
//            }
//            return mIntArrayTransitByteArray;
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    public synchronized HashMap<Integer, String> getArrayPIDData(int[] mDataFlows, byte[] pid, byte[] readBuffer) throws NumberFormatException, UnsupportedEncodingException
//    {
//        // getArrayPIDData(mDataFlows, mIntArrayTransitByteArray, obd2DSByte)
//        HashMap<Integer, String> valueMap = new HashMap<Integer, String>();
//        if (pid != null && pid.length != 0)
//        {
//            for (int i = 0, j = 0; i < mIntArrayTransitByteArray.length && j < mDataFlows.length; i++)
//            {
//                byte[] dataBuffer = new byte[] { readBuffer[4 * i], readBuffer[4 * i + 1], readBuffer[4 * i + 2], readBuffer[4 * i + 3] };
//                byte[] dataValue = searchIdUtils.getResultWithCalc(pid[i], dataBuffer);
//                String value = new String(dataValue, "GB2312");
//                valueMap.put(mDataFlows[i], value);
//            }
//            return valueMap;
//        }
//        return null;
//    }
//
//    public synchronized void sendCommand(Context context, int dataFlows[])
//    {
//        this.mDataFlows = dataFlows;
//        this.context = context;
//        bluetoothDataHandler = BluetoothDataHandler.BluetoothDataHandInstance(bluetoothResponseHandler);
//        mIntArrayTransitByteArray = new byte[mDataFlows.length];
//        searchIdUtils = new OBD2SearchIdUtils(context);
//        new Thread()
//        {
//            @Override
//            public void run()
//            {
//                intTransitByte();
//                // bluetoothDataHandler =
//                // BluetoothDataHandler.BluetoothDataHandInstance(bluetoothResponseHandler);
//                getSpecifiedPID();
//            };
//        }.start();
//    }
//
//    @Override
//    public void run()
//    {
//        super.run();
//        // intTransitByte();
//        bluetoothDataHandler = BluetoothDataHandler.BluetoothDataHandInstance(bluetoothResponseHandler);
//        // // intTransitByte();
//        // getSpecifiedPID();
//
//        // syncLock.set(false);
//        // try
//        // {
//        // syncLock.waitForTrue();
//        // }
//        // catch (InterruptedException e1)
//        // {
//        // e1.printStackTrace();
//        // }
//        // new GetDataThead().start();
//    }
//
//    /**
//     * �ͺ��ۼ�ͳ�ƣ�����Ϊ�ۼ��㷨 TODO ���ոù�����Ҫ����Service��
//     * @param parseFloat
//     */
//    private void oilAnalysis(float parseFloat)
//    {
//        for (int i = oilAnalysisValue.length - 1; i > 0; i--)
//        {
//            oilAnalysisValue[i] = oilAnalysisValue[i - 1];
//        }
//        oilAnalysisValue[0] = parseFloat;
//        if (oilAnalysisValue[5] > 0)
//        {
//            oilAnalysisValue[5] = oilAnalysisValue[0] + oilAnalysisValue[1] + oilAnalysisValue[2] + oilAnalysisValue[3] + oilAnalysisValue[4];
//        }
//        if (oilAnalysisValue[11] > 0)
//        {
//            oilAnalysisValue[11] = oilAnalysisValue[5] + oilAnalysisValue[6] + oilAnalysisValue[7] + oilAnalysisValue[8] + oilAnalysisValue[9] + oilAnalysisValue[10];
//        }
//    }

    @Override
    public void BlueConnectLost(String name, String mac)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void BlueConnected(String name, String mac)
    {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}
