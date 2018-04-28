package com.cnlaunch.mycar.obd2.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import launch.obd2.OBD2SearchIdUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.obd2.model.DataFlowIds;
import com.cnlaunch.mycar.obd2.model.DataFlowModel;
import com.cnlaunch.mycar.obd2.model.MsgIds;
import com.cnlaunch.mycar.obd2.model.MsgObserver;
import com.cnlaunch.mycar.obd2.model.MsgQueue;
import com.cnlaunch.mycar.obd2.util.DataExchange;
import com.cnlaunch.mycar.obd2.util.DiagnosisDataMapping;

public class Obd2DiagnoseService extends Service implements BluetoothInterface
{
    private static final boolean D = true;
    private static final String TAG = "Obd2DiagnoseService";
    /*****************/
    private OBD2SearchIdUtils searchIdUtils;
    private DataFlowModel model = DataFlowModel.getModel();
    private MsgObserver mStopSericeMsgObserver;
    public int mEngineSpeed = 0;
    public int mCarSpeed = 0;
    private int[] mDataFlows;
    private byte[] mIntArrayTransitByteArray;
    HashMap<Integer, String> baseDataMap = new HashMap<Integer, String>();
    private float[] oilAnalysisValue = new float[17];
    String mDataFlowsStr = null;
    /***************/
    private static int[] newDataFlows;
    private static byte[] newPid;

    private Context context = Obd2DiagnoseService.this;
    BluetoothDataService m_blue_data_service = BluetoothDataService.getInstance();
    String s = null;
    final static BigDecimal zero   = new BigDecimal(0);
    final static BigDecimal big237 = new BigDecimal(237);
    final static BigDecimal big235 = new BigDecimal(235);
    final static BigDecimal big7   = new BigDecimal(7);
    final static BigDecimal big50  = new BigDecimal(50);
    final static BigDecimal big12  = new BigDecimal(12);
    final static BigDecimal big2   = new BigDecimal(2);
    final static BigDecimal big3   = new BigDecimal(3);
    final static BigDecimal big235divide7 = big235.divide(big7,BigDecimal.ROUND_HALF_EVEN);
    static boolean isStart            = false;   // �Ƿ�ʼ�ۻ�
    AccumulateFuel accumulateFuel = new AccumulateFuel();
    // ����ƽ���ͺ�
    private float m_oial_all = 0;
    private int m_oial_time = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_blue_data_service.AddObserver(this);
        accumulateFuel.init();
        createObserver();
        registerObserver();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy()
    {
        unRegisterObserver();
        m_blue_data_service.DelObserver(this);
        super.onDestroy();
    }

    private void createObserver()
    {
        mStopSericeMsgObserver = new MsgObserver(MsgIds.ORDER_STOP_SERVICE, context)
        {
            @Override
            public void dealMessage(Message msg)
            {
                Obd2DiagnoseService.this.stopSelf();
                if (D)
                    Log.i("service1", "Obd2DiagnoseService.this.stopSelf();");
            }
        };
    }

    private void registerObserver()
    {
        MsgQueue.getMsgQueue().registerObserver(mStopSericeMsgObserver);
    }

    private void unRegisterObserver()
    {
        MsgQueue.getMsgQueue().unRegisterObserver(mStopSericeMsgObserver);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    private IBinder mBinder = new IDiagnoseServiceRemote.Stub()
    {

        @Override
        public String getValue(int dataFlowName) throws RemoteException
        {
            return DataFlowModel.getModel().getValue(dataFlowName);
        }

        @Override
        public void prepareValue(int[] dataFlowNames) throws RemoteException
        {
            Obd2DiagnoseService.this.prepareValue(dataFlowNames);
        }
    };

    protected void prepareValue(final int[] dataFlowNames)
    {
        if (D)
            System.out.println("protected void prepareValue(final int[] dataFlowNames) ǰ");
        sendCommand(dataFlowNames);
        if (D)
            System.out.println("protected void prepareValue(final int[] dataFlowNames) ��");
    }

    /*******************************************/

    public static final String bytesToHexStringNoBar(int[] bArray)
    {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        int len = bArray.length;
        for (int i = 0; i < len; i++)
        {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append("0");
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    // ���ս��������������������ظ�UI
    private void provideDataToUI(HashMap<Integer, String> hashMapData)
    {
        if (hashMapData != null && hashMapData.size() != 0)
        {
            Set<Integer> set = hashMapData.keySet();
            // for (Iterator<Integer> iter = set.iterator(); iter.hasNext();) {
            // int key = (Integer) iter.next();

            if (mDataFlowsStr.equals("0608090D0E131302"))
            {
                /******************************** ������ begin ***********************************************************/

                // �����ǰ��
                model.setValue(DataFlowIds.IGNITIONADVANCEANGLE, baseDataMap.get(DataFlowIds.IGNITIONADVANCEANGLE));

                if (D)
                    System.out.println("hashMap.get(DataFlowIds.IGNITIONADVANCEANGLE)" + model.getValue(DataFlowIds.IGNITIONADVANCEANGLE));
                // �����¶�
                model.setValue(DataFlowIds.INTAKEAIRTEMPERATURE, baseDataMap.get(DataFlowIds.INTAKEAIRTEMPERATURE));

                // ��ȴҺ�¶�
                model.setValue(DataFlowIds.COOLANTTEMPERATURE, baseDataMap.get(DataFlowIds.COOLANTTEMPERATURE));
                // ��������
                model.setValue(DataFlowIds.AIRFLOW, baseDataMap.get(DataFlowIds.AIRFLOW));
                // ����ѹ��
                model.setValue(DataFlowIds.INLETPRESSURE, baseDataMap.get(DataFlowIds.INLETPRESSURE));
                /******************************** ������ end ***********************************************************/
            }
            //
            if (mDataFlowsStr.equals("0405070B0C020D14"))
            {
                /******************************** ������ begin ***********************************************************/
                // �߻����¶�
                model.setValue(DataFlowIds.CATALYSTTEMPERATURE, baseDataMap.get(DataFlowIds.CATALYSTTEMPERATURE));
                // ȼ�����������ڣ�
                model.setValue(DataFlowIds.fuelAmendmentsToLongTerm, baseDataMap.get(DataFlowIds.fuelAmendmentsToLongTerm));
                // ȼ�����������ڣ�
                model.setValue(DataFlowIds.fuelAmendmentsToShortTerm, baseDataMap.get(DataFlowIds.fuelAmendmentsToShortTerm));
                // ����ѹ��
                model.setValue(DataFlowIds.ATMOSPHERICPRESSURE, baseDataMap.get(DataFlowIds.ATMOSPHERICPRESSURE));
                // ����������
                model.setValue(DataFlowIds.ENGINELOAD, baseDataMap.get(DataFlowIds.ENGINELOAD));
                // �͹�ѹ��
                model.setValue(DataFlowIds.fuelRailPressure, baseDataMap.get(DataFlowIds.fuelRailPressure));
                /******************************** ������ end ***********************************************************/
            }

            if (mDataFlowsStr.equals("0102030D17181313"))
            {
                /******************************** ��һ�� begin ************************************/
                // ������ת��
                model.setValue(DataFlowIds.ENGINESPEED, baseDataMap.get(DataFlowIds.ENGINESPEED));
                // ����
                model.setValue(DataFlowIds.CARSPEED, baseDataMap.get(DataFlowIds.CARSPEED));
                // �����¶�
                model.setValue(DataFlowIds.AmbientTemperature, baseDataMap.get(DataFlowIds.AmbientTemperature));
                // ��ƿ��ѹ
                model.setValue(DataFlowIds.BatteryVoltage, baseDataMap.get(DataFlowIds.BatteryVoltage));
                // ���ٶ�
                calAcceleration();
                /******************************** ��һ��end ****************************************/
            }

            if (mDataFlowsStr.equals("000A0F10020D1211"))
            {
                /******************************** �ڶ��� begin *************************************/
                accumulateFuel.setStart();
                accumulateFuel.run();
                // ʣ������
//                if (baseDataMap.get(DataFlowIds.REMAININGFUEL) != null)
//                {
//                    // �޸�����Ϊ Fuel Level Input ��ʵ��ֵ
//                    model.setValue(DataFlowIds.REMAININGFUEL, String.valueOf(Float.valueOf(baseDataMap.get(DataFlowIds.REMAININGFUEL))));
//                }
//
//                String value = null;
//                for (Iterator<Integer> iter = set.iterator(); iter.hasNext();)
//                {
//                    int key = (Integer) iter.next();
//                    // ����˲ʱ�ͺ�
//                    if (key == DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)
//                    {
//                        // String value = null;
//                        float carSpeedValue = Float.parseFloat(baseDataMap.get(DataFlowIds.CARSPEED));
//                        float airFlowValue = Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION));
//
//                        if (carSpeedValue == 0 || airFlowValue == 0)
//                        {
//                            value = "0";
//                            model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, value);
//                        }
//                        else
//                        {
//                            float i = airFlowValue / carSpeedValue;
//                            final double j = 235 / 7.0;
//                            value = String.valueOf(j * i);
//                            model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, value);
//                            m_oial_all += Float.parseFloat(value);
//                            m_oial_time++;
//                        }
//                    }
//                    // �������
//                    if (key == DataFlowIds.Mileage)
//                    {
//                        if (baseDataMap.get(DataFlowIds.REMAININGFUEL) != null)
//                        {
//                            float i11 = 50 * Float.valueOf(baseDataMap.get(DataFlowIds.REMAININGFUEL));// ȼ��Һλ
//                            if (Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)) != 0)
//                            {
//                                String i12 = String.valueOf(i11 / Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)));
//                                if (D)
//                                    Log.i("i12", i12);
//                                model.setValue(DataFlowIds.Mileage, i12);
//                            }
//                            else
//                            {
//                                model.setValue(DataFlowIds.Mileage, "N/A");
//                            }
//                        }
//                    }
//
//                    // ƽ���ͺ�
//                    if (key == DataFlowIds.averageFuelConsumption)
//                    {
//                        float carSpeedValue = Float.parseFloat(baseDataMap.get(DataFlowIds.CARSPEED));
//                        float airFlowValue = Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION));
//
//                        if (carSpeedValue == 0 && airFlowValue == 0)
//                        {
//                            value = "N/A";
//                            model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, value);
//                        }
//                        else if (carSpeedValue == 0 || airFlowValue == 0)
//                        {
//                            value = "0";
//                            model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, value);
//                        }
//                        else
//                        {
//                            float i = airFlowValue / (carSpeedValue);
//                            final double j = 235 / 7.0 * 10;
//                            value = String.valueOf(j * i / carSpeedValue);
//                            model.setValue(DataFlowIds.averageFuelConsumption, value);
//                        }
//
//                    }
//                    // ÿСʱ�ͺ�
//                    if (key == DataFlowIds.HOURLYFUELCONSUMPTION)
//                    {
//                        if (model.getValue(DataFlowIds.averageFuelConsumption) != null)
//                        {
//                            if (Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)) != 0)
//                            {
//                                model.setValue(DataFlowIds.HOURLYFUELCONSUMPTION, String.valueOf((Float.parseFloat(model.getValue(DataFlowIds.averageFuelConsumption)) + 0.1)));
//                            }
//                            else
//                            {
//                                model.setValue(DataFlowIds.HOURLYFUELCONSUMPTION, String.valueOf((Float.parseFloat(model.getValue(DataFlowIds.averageFuelConsumption)))));
//                            }
//                        }
//                    }
//                }
                /******************************** �ڶ��� end **************************************/
            }
            else
            {
                accumulateFuel.setEnd();
            }
            double a;
            long oilTimes1 = 0l;
            long oilTimes2 = 0l;

//            for (Iterator<Integer> iter = set.iterator(); iter.hasNext();)
//            {
//                int key = (Integer) iter.next();
//                // N�����ͺ�
//                if (key == DataFlowIds.moreFuelConsumption)
//                {
//                    float carSpeedValueM = Float.parseFloat(baseDataMap.get(DataFlowIds.CARSPEED));
//                    float airFlowValueM = Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION));
//                    if (carSpeedValueM == 0 || airFlowValueM == 0)
//                    {
//                        // if
//                        // (Float.parseFloat(hashMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION))
//                        // != 0.0 ||
//                        // hashMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)
//                        // != "N/A" ||
//                        // Float.parseFloat(hashMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION))
//                        // != 0) {
//                        if (baseDataMap.get(DataFlowIds.CARSPEED) != null && baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION) != null)
//                        {
//                            a = (235 / Float.parseFloat(baseDataMap.get(DataFlowIds.CARSPEED)) / Float.parseFloat(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION)) * 7.0) / 10;
//
//                            OilRecordsConstants.setOilRecord(OilRecordsConstants.getOilRecord() + a);
//
//                            if (OilRecordsConstants.isRecordTheSpeedOfTime1())
//                            {
//                                oilTimes1 = System.currentTimeMillis();
//                                OilRecordsConstants.setCarSpeedTimes1(oilTimes1);
//                                OilRecordsConstants.setRecordTheSpeedOfTime1(false);
//                                OilRecordsConstants.setRecordTheSpeedOfTime2(true);
//                            }
//                            else if (OilRecordsConstants.isRecordTheSpeedOfTime2())
//                            {
//                                oilTimes2 = System.currentTimeMillis();
//                                OilRecordsConstants.setCarSpeedTimes2(oilTimes2);
//                                OilRecordsConstants.setRecordTheSpeedOfTime1(false);
//                            }
//
//                            long acceleration = OilRecordsConstants.getCarSpeedTimes2() - OilRecordsConstants.getCarSpeedTimes1();
//                            if (acceleration >= 65000)
//                            {
//                                oilAnalysis((float) OilRecordsConstants.getOilRecord());
//                                OilRecordsConstants.setRecordTheSpeedOfTime1(true);
//                                for (int k = 0; k < 17; k++)
//                                {
//                                    s += oilAnalysisValue[k] + ",";
//                                }
//                                s = s.substring(4);
//                                s = s.substring(0, s.lastIndexOf(','));
//                                model.setValue(DataFlowIds.moreFuelConsumption, s);
//                                s = null;
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (D)
//                Log.i("mDataFlows", "mDataFlows�� �� " + bytesToHexStringNoBar(mDataFlows));
//            model.notifyChange(mDataFlows);
        }
        else
        {
            model.notifyChange(mDataFlows);
        }

    }


    
    class AccumulateFuel 
    {

        private BigDecimal immediateFuel   = zero;    // ˲ʱ�ͺ�
        private BigDecimal mileage         = zero;    // �������
        private BigDecimal hourFuel        = zero;    // ÿСʱ�ͺ�
        private BigDecimal accumulateFuel  = zero;    // �ۻ��ͺ�ֵ
        private BigDecimal averageFuel     = zero;    // ƽ���ͺ�
        private BigDecimal carSpeedValue   = zero;    // ����
        private BigDecimal airFlowValue    = zero;    // ��������
        private BigDecimal fuelLevel       = zero;    // ȼ��ˮƽ
        private BigDecimal fuel5min        = zero;    // 5�����ͺ�
        private BigDecimal fuel30min       = zero;    // 30�����ͺ�
        private BigDecimal fuel3hour       = zero;    // 3Сʱ�ͺ�
        private long counter               = 0;       // �ۻ�����
        public void run()
        {
            if(isStart)
            {
                counter++;
                if (!checkParam())
                {
                    return ;
                }
                
                carSpeedValue = new BigDecimal(baseDataMap.get(DataFlowIds.CARSPEED));
                airFlowValue  = new BigDecimal(baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION));
                fuelLevel     = new BigDecimal(baseDataMap.get(DataFlowIds.REMAININGFUEL));
                if (D)Log.i(TAG, "carSpeedValue �� " + carSpeedValue);
                if (D)Log.i(TAG, "airFlowValue �� " + airFlowValue);
                if (D)Log.i(TAG, "fuelLevel �� " + fuelLevel);
                if(carSpeedValue.doubleValue() > 0 && airFlowValue.doubleValue() > 0)
                {
                    // ��һ��:�����˲ʱ�ͺ�:
                    immediateFuel = (airFlowValue.divide(carSpeedValue, BigDecimal.ROUND_HALF_EVEN)).multiply(big235divide7);
  
                    // �ۻ��ͺ�
                    accumulateFuel = accumulateFuel.add(immediateFuel);
                    
                    // ƽ���ͺ�
                    averageFuel = accumulateFuel.divide(new BigDecimal(counter));
                    
                    // ȼ�����
                    mileage = big50.multiply(fuelLevel).divide(averageFuel, BigDecimal.ROUND_HALF_EVEN);
                    
                    // ÿСʱ�ͺ�
                    hourFuel = big50.multiply(fuelLevel).divide(mileage, BigDecimal.ROUND_HALF_EVEN);
                    if (D)Log.i(TAG, "immediateFuel �� " + immediateFuel);
                    if (D)Log.i(TAG, "accumulateFuel �� " + accumulateFuel);
                    if (D)Log.i(TAG, "averageFuel �� " + averageFuel);
                    if (D)Log.i(TAG, "mileage �� " + mileage);
                    
                    fuel5min        = hourFuel.divide(big12,BigDecimal.ROUND_HALF_EVEN);    // 5�����ͺ�
                    fuel30min       = hourFuel.divide(big2,BigDecimal.ROUND_HALF_EVEN);    // 30�����ͺ�
                    fuel3hour       = hourFuel.multiply(big3);    // 3Сʱ�ͺ�
                    String moreFuel = fuel5min.toString() + fuel30min.toString() + fuel3hour.toString();
                    model.setValue(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION, immediateFuel.toString());//˲ʱ�ͺ�
                    model.setValue(DataFlowIds.averageFuelConsumption, averageFuel.toString());// ƽ���ͺ�
                    model.setValue(DataFlowIds.REMAININGFUEL, fuelLevel.toString());// ȼ��ˮƽ
                    model.setValue(DataFlowIds.HOURLYFUELCONSUMPTION, hourFuel.toString()); //ÿСʱ�ͺ�
                    model.setValue(DataFlowIds.Mileage, mileage.toString()); // �������
                    model.setValue(DataFlowIds.moreFuelConsumption, moreFuel); // ����
                    
                }
            }
            else
            {
                init();
            }
       
        }
        public boolean checkParam()
        {
            if (baseDataMap.get(DataFlowIds.CARSPEED) == null)
            {
                if (D)Log.i(TAG, "����Ϊ�� ! ");
                return false;
            }
            if (baseDataMap.get(DataFlowIds.INSTANTANEOUSFUELCONSUMPTION) == null)
            {
                if (D)Log.i(TAG, "��������Ϊ�� ! ");
                return false;
            }
            if (baseDataMap.get(DataFlowIds.REMAININGFUEL) == null)
            {
                if (D)Log.i(TAG, "ȼ��ˮƽΪ�� ! ");
                return false;
            }
            return true;
        }
        public void setStart()
        {
            Log.i(TAG, "��ʼ�ۻ��ͺ�!");
            isStart = true;
        }
        public  void setEnd()
        {
            Log.i(TAG, "ֹͣ�ۻ��ͺ�!");
            isStart = false;
            init();
        }
        private void init()
        {
              immediateFuel   = zero;    // ˲ʱ�ͺ�
              mileage         = zero;    // �������
              hourFuel        = zero;    // ÿСʱ�ͺ�
              accumulateFuel  = zero;    // �ۻ��ͺ�ֵ
              averageFuel     = zero;    // ƽ���ͺ�
              carSpeedValue   = zero;    // ����
              airFlowValue    = zero;    // ��������
              fuelLevel       = zero;    // ȼ��ˮƽ
              fuel5min        = zero;    // 5�����ͺ�
              fuel30min       = zero;    // 30�����ͺ�
              fuel3hour       = zero;    // 3Сʱ�ͺ�
              counter               = 0;       // �ۻ�����
              isStart = false;   // �Ƿ�ʼ�ۻ�
        }
    }
    
    BigDecimal startTime = new BigDecimal(0); // ��ʼʱ��
    BigDecimal startSpeed = new BigDecimal(0);// ��ʼ�ٶ�
    final static BigDecimal TIME_UNIT = new BigDecimal(1000);
    final static BigDecimal SPEED_UNIT = new BigDecimal(3.6);
    final static BigDecimal SCALE_UNIT = new BigDecimal(100);

    /**
     * ������ٶ�
     * @since DBS V100
     */
    private void calAcceleration()
    {
        if (D)
            Log.i(TAG, "��ʼʱ��:" + startTime.doubleValue());
        if (D)
            Log.i(TAG, "��ʼ�ٶ�:" + startSpeed.doubleValue());
        // �ȼ�¼��ǰʱ��
        BigDecimal endTime = new BigDecimal(System.currentTimeMillis());
        if (baseDataMap.get(DataFlowIds.CARSPEED) == null)
        {
            return;
        }
        if (D)
            Log.i(TAG, "��λ������:" + baseDataMap.get(DataFlowIds.CARSPEED));
        BigDecimal endSpeed = new BigDecimal(baseDataMap.get(DataFlowIds.CARSPEED).trim());
        if (D)
            Log.i(TAG, "��ֹʱ��:" + endTime.doubleValue());
        if (D)
            Log.i(TAG, "��ֹ�ٶ�:" + endSpeed.doubleValue());
        if (startTime == null || startTime.intValue() == 0)
        {
            startTime = endTime;
            startSpeed = endSpeed;
            return;
        }

        BigDecimal incrementTime = endTime.subtract(startTime).divide(TIME_UNIT, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal incrementSpeed = endSpeed.subtract(startSpeed).multiply(SPEED_UNIT);

        if (D)
            Log.i(TAG, "ʱ������:" + incrementTime.doubleValue());
        if (D)
            Log.i(TAG, "�ٶ�����:" + incrementSpeed.doubleValue());
        BigDecimal acceleration = new BigDecimal(0);
        if (incrementTime.intValue() > 0)
        {
            
            acceleration = incrementSpeed.divide(incrementTime.multiply(SCALE_UNIT), BigDecimal.ROUND_HALF_EVEN);
        }
        startTime = endTime;
        startSpeed = endSpeed;
        if (D)
            Log.i(TAG, "��������ٶ�:" + acceleration.doubleValue());
        model.setValue(DataFlowIds.Acceleration, acceleration.toString());

    }

    @Override
    public void GetDataFromService(byte[] databuf, int datalen)
    {
        if (D)
            Log.i("databuf", Arrays.toString(databuf));
        byte[] dpuPackage = OrderUtils.filterReturnDataPackage(databuf);
        if (D)
            Log.i("tag", Arrays.toString(dpuPackage));
        byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
        byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);// �������ֽ�
        byte[] param1 = OrderUtils.filterOBD2CmdParameters(dpuPackage);// ���������ֽ�
        if (D)
            Log.i("param", "param" + Arrays.toString(param));
        if (D)
            Log.i("param", "param1" + Arrays.toString(param1));
        // �õ�OBD2�����������ֽ�
        String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);
        if (cmd_subcmdString.equals("6912"))
        {
            byte[] obd2DSByte = OrderUtils.filterOBD2CmdParameters(dpuPackage);
            if (param1.length > 0)
            {
                try
                {
                    mDataFlowsStr = bytesToHexStringNoBar(mDataFlows);
                    if (D)
                        Log.i("mDataFlows", "mDataFlowsǰ �� " + bytesToHexStringNoBar(mDataFlows));
                    // hashMap =
                    // getArrayPIDData(mDataFlows,mIntArrayTransitByteArray,
                    // obd2DSByte);
                    baseDataMap = getArrayPIDData(newDataFlows, newPid, obd2DSByte);
                    if (baseDataMap != null)
                    {
                        provideDataToUI(baseDataMap);
                    }
                    else
                    {
                        baseDataMap = getArrayPIDData(mDataFlows, mIntArrayTransitByteArray, obd2DSByte);// ��hashMapΪnull��ʱ��
                        provideDataToUI(baseDataMap);
                    }
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    model.notifyChange(mDataFlows);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                    model.notifyChange(mDataFlows);
                }
            }
            else
            {
                // Toast.makeText(context, "û��֧�ֵ���������", 1).show();
            }
        }
        else if (cmd_subcmdString.equals("6902"))
        {
            getSpecifiedPID();
        }
    }

    // �õ�ָ��������PID����
    private void getSpecifiedPID()
    {
        byte[] v_cmd = new byte[] { 0x29, 0x12 };
        if (mIntArrayTransitByteArray.length != 0)
        {
            /******************* ������һ�δ�����ȥ����֧��PID��ƥ��mDataFlows***********begin ***********/
            int[] s0 = mDataFlows;
            byte[] s1 = mIntArrayTransitByteArray;
            byte[] s2 = DataExchange.getPidsMeter();
            Map allPidsMap = DataExchange.getPidsMap();
            ArrayList pids = new ArrayList();
            for (int pid : mDataFlows)
            {
                if(allPidsMap.get((byte)pid) != null && (Boolean)allPidsMap.get((byte)pid))
                {
                    pids.add(pids);
                }
            }
           
            ArrayList<Byte> a1 = new ArrayList<Byte>();
            ArrayList<Integer> a2 = new ArrayList<Integer>();
            for (int i = 0; i < s2.length; i++)
            {
                for (int j = 0; j < s1.length; j++)
                {
                    if (s2[i] == s1[j])
                    {
                        a1.add(s2[i]);
                        a2.add(s0[j]);
                    }
                }
            }
            byte[] buff = new byte[a1.size()];
            for (int i = 0; i < buff.length; i++)
            {
                buff[i] = a1.get(i);
            }

            int[] buff1 = new int[a2.size()];
            for (int i = 0; i < buff1.length; i++)
            {
                buff1[i] = a2.get(i);
            }

            newPid = new byte[buff.length];
            newDataFlows = new int[buff1.length];
            if (buff1 != null && buff1.length != 0)
            {
                newDataFlows = buff1;
            }
            if (buff != null && buff.length != 0)
            {
                newPid = buff;
            }
            /******************* ������һ�δ�����ȥ����֧��PID��ƥ��mDataFlows************end **********/
            if (buff != null && buff.length != 0)
            {
                byte[] v_sendbuf = OrderUtils.appendByteArray(new byte[] { (byte) buff.length }, buff);

                if (m_blue_data_service != null)
                    m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode, v_cmd, v_sendbuf, v_sendbuf.length, 2500);
            }
        }
    }

    public synchronized void sendCommand(int dataFlows[])
    {
        mDataFlows = dataFlows;
        mIntArrayTransitByteArray = new byte[mDataFlows.length];
        searchIdUtils = new OBD2SearchIdUtils(context);
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    intTransitByte();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                getSpecifiedPID();
            };
        }.start();
    }

    /**
     * �ͺ��ۼ�ͳ�ƣ�����Ϊ�ۼ��㷨 TODO ���ոù�����Ҫ����Service��
     * @param parseFloat
     */
    private void oilAnalysis(float parseFloat)
    {
        for (int i = oilAnalysisValue.length - 1; i > 0; i--)
        {
            oilAnalysisValue[i] = oilAnalysisValue[i - 1];
        }
        oilAnalysisValue[0] = parseFloat;
        if (oilAnalysisValue[5] > 0)
        {
            oilAnalysisValue[5] = oilAnalysisValue[0] + oilAnalysisValue[1] + oilAnalysisValue[2] + oilAnalysisValue[3] + oilAnalysisValue[4];
        }
        if (oilAnalysisValue[11] > 0)
        {
            oilAnalysisValue[11] = oilAnalysisValue[5] + oilAnalysisValue[6] + oilAnalysisValue[7] + oilAnalysisValue[8] + oilAnalysisValue[9] + oilAnalysisValue[10];
        }
    }

    /*
     * int����ת����byte����
     */
    private byte[] intTransitByte() throws Exception
    {
        if (mIntArrayTransitByteArray.length != 0)
        {
            for (int i = 0; i < mDataFlows.length; i++)
            {
                mIntArrayTransitByteArray[i] = (byte) DiagnosisDataMapping.getData().get(mDataFlows[i]);
            }
            return mIntArrayTransitByteArray;
        }
        else
        {
            return null;
        }
    }

    public synchronized HashMap<Integer, String> getArrayPIDData(int[] newDataF, byte[] pid, byte[] readBuffer) throws NumberFormatException, UnsupportedEncodingException
    {
        HashMap<Integer, String> valueMap = new HashMap<Integer, String>();
        if (pid != null && pid.length != 0)
        {
            try
            {
                for (int i = 0; i < pid.length; i++)
                {
                    byte[] dataBuffer = new byte[] { readBuffer[4 * i], readBuffer[4 * i + 1], readBuffer[4 * i + 2], readBuffer[4 * i + 3] };
                    byte[] dataValue = searchIdUtils.getResultWithCalc(pid[i], dataBuffer);
                    String value = new String(dataValue, "GB2312");
                    if (newDataF != null && newDataF.length != 0)
                    {
                        valueMap.put(newDataF[i], value);
                    }
                    else
                    {
                        // valueMap.put(newDataF[i], value);
                    }
                }
                return valueMap;
            }
            catch (Exception e)
            {
                if (D)
                    Log.i("getArrayPIDData", "Խ���ˣ���������");
                return null;
            }
        }
        return null;
    }

    @Override
    public void GetDataTimeout()
    {
    }

    @Override
    public void BlueConnectLost(String name, String mac)
    {
    }

    @Override
    public void BlueConnected(String name, String mac)
    {
    }

    // �󽻼���ּ�ڹ���ECU��֧�ֵ�PID
    @SuppressWarnings("unused")
    @Deprecated
    private static byte[] intersection(byte[] s1, byte[] s2)
    {
        ArrayList<Byte> a1 = new ArrayList<Byte>();

        for (int i = 0; i < s2.length; i++)
        {
            for (int j = 0; j < s1.length; j++)
            {
                if (s2[i] == s1[j])
                {
                    a1.add(s2[i]);
                }
            }
        }
        byte[] buff = new byte[a1.size()];
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = a1.get(i);
        }
        if (D)
            System.out.println(Arrays.toString(buff));
        return buff;
    }

    @Override
    public void BlueConnectClose()
    {
    }

}
