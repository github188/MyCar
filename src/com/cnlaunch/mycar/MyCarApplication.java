package com.cnlaunch.mycar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.updatecenter.Device;
import com.cnlaunch.mycar.updatecenter.DiagSoftUpdateConfigParams;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

public class MyCarApplication extends Application
{
    File sdPath = Environment.getExternalStorageDirectory();
    String logFileDir = Environment.getExternalStorageDirectory() + File.separator + "mycar/log";
    private SharedPreferences deviceInfoSP;
    private SharedPreferences setting;
    public static DiagSoftUpdateConfigParams params ;
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        deviceInfoSP = getSharedPreferences(Constants.SHARE_PREFERENCES_DEVICE_INFO, Context.MODE_WORLD_WRITEABLE);
        setting = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
    }

    public File getSdPath()
    {
        return sdPath;
    }

    public void setSdPath(File sdPath)
    {
        this.sdPath = sdPath;
    }

    public String getLogFileDir()
    {
        return logFileDir;
    }

    public void setLogFileDir(String logFileDir)
    {
        this.logFileDir = logFileDir;
    }

    // -------------------------���������Ż� ��Զï----------------------------
    /**
     * Ŀ�ģ�DBScar���������ĵĶ��弰������ʽ,������Ϊ�� DPU��ʼ����DPU�̼���download.bin�����¡����������¡����������ø���
     * �ĸ����֡� �ڴˣ���Ҫ���ж��Ƿ��Ѿ���ʼ��,�̼����޸���
     */
    /**
     * �ж�DPU�Ƿ��Ѿ���ʼ��������豸��δ��ʼ������������ʾ��
     * @return
     * @since DBS V100
     */
    public boolean isDPUInit()
    {

        return true;
    }

    public String getCC()
    {
        return setting.getString(UsercenterConstants.LOGIN_CC, null);
    }

    /**
     * �����豸��Ϣ
     * @return
     * @since DBS V100
     */
    public ArrayList<Device> getDevice()
    {
        ArrayList<Device> devices = null;
        String bluetoothName = deviceInfoSP.getString(Constants.SP_DEVICE_INFO_BLUETOOTH_NAME, null); // �˺ţ�����
        String macAddress = deviceInfoSP.getString(Constants.SP_DEVICE_INFO_MAC_ADDRESS, null);
        String chipId = deviceInfoSP.getString(Constants.SP_DEVICE_INFO_CHIP_ID, null);
        String serialNumber = deviceInfoSP.getString(Constants.SP_DEVICE_INFO_SERIAL_NUMBER, null);
        if (bluetoothName == null)
        {
            return null;
        }
        devices = new ArrayList<Device>();
        String[] nameArray = bluetoothName.split(Constants.DEVICE_INFO_SEPARATED);
        String[] macArray = macAddress.split(Constants.DEVICE_INFO_SEPARATED);
        String[] chipIds = chipId.split(Constants.DEVICE_INFO_SEPARATED);
        String[] serialNumbers = serialNumber.split(Constants.DEVICE_INFO_SEPARATED);
        if (getCC().equals(macArray[0]))
        {
            for (int i = 0; i < nameArray.length - 1; i++)
            {
                Device device = new Device();
                device.setCc(nameArray[0]);
                device.setDeviceName(nameArray[i + 1]);
                device.setMac(macArray[i + 1]);
                device.setChipId(chipIds[i + 1]);
                device.setSerialNum(serialNumbers[i + 1]);
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * �����������ƺ�������ַ
     * @param name
     * @param address
     * @since DBS V100
     */
    public boolean saveDeviceInfo(String name, String address, String chipID, String serialNumber)
    {
        if (getCC() == null)
        {
            return false;
        }
        StringBuffer deviceInfoBuffer = new StringBuffer();
        deviceInfoBuffer.append(getCC()).append(Constants.DEVICE_INFO_SEPARATED).append(serialNumber).append(Constants.DEVICE_INFO_SEPARATED).append(name).append(Constants.DEVICE_INFO_SEPARATED)
            .append(address).append(Constants.DEVICE_INFO_SEPARATED).append(chipID).append(Constants.DEVICE_INFO_SEPARATED);
        deviceInfoSP.edit().putString(deviceInfoBuffer.toString(), Constants.SP_DEVICE_INFO).commit();
        return true;
    }

    public boolean setBluetoothMac(String serialNumber)
    {
        if (getCC() == null)
        {
            return false;
        }
        List<String> values = getValuesOfStartWith(getCC() + Constants.DEVICE_INFO_SEPARATED + serialNumber, Constants.SP_DEVICE_INFO);
        if (values != null && values.size() > 0)
        {

            String deviceInfo = values.get(0);
            String[] deviceInfoes = deviceInfo.split(Constants.DEVICE_INFO_SEPARATED);

            deviceInfoSP.edit().putString(Constants.SP_DEVICE_INFO_MAC_ADDRESS, deviceInfoes[3]).commit();
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean removeBluetoothMac()
    {
        if (getCC() == null)
        {
            return false;
        }
        if (deviceInfoSP.contains(Constants.SP_DEVICE_INFO_MAC_ADDRESS))
        {
            deviceInfoSP.edit().remove(Constants.SP_DEVICE_INFO_MAC_ADDRESS);

        }
        return true;
    }

    /**
     * ��ȡDPU����mac��ַ
     * @param serialNumber
     * @return
     * @since DBS V100
     */
    public String getBluetoothMacAddress()
    {
        if (getCC() == null)
        {
            return null;

        }
        return deviceInfoSP.getString(Constants.SP_DEVICE_INFO_MAC_ADDRESS, null);

    }

    /**
     * ����VIN
     * @param name
     * @param address
     * @since DBS V100
     */
    public boolean saveVIN(String vin)
    {
        if (getCC() == null)
        {
            return false;
        }
        String VIN = deviceInfoSP.getString(Constants.SP_DEVICE_INFO_VIN, null); // �˺ţ�����
        if (VIN == null)
        {
            // ����VIN
            deviceInfoSP.edit().putString(Constants.SP_DEVICE_INFO_VIN, getCC() + Constants.DEVICE_INFO_SEPARATED + vin).commit();
        }
        else
        {
            // ����VIN
            deviceInfoSP.edit().putString(Constants.SP_DEVICE_INFO_VIN, VIN + Constants.DEVICE_INFO_SEPARATED + vin).commit();
        }
        return true;
    }

    public boolean saveDownloadBinVersion(String newVersion, String newLanguage)
    {
        if (getCC() == null)
        {
            return false;
        }
        // ����汾
        deviceInfoSP.edit().putString(Constants.SP_DEVICE_INFO_DOWNLOAD_BIN_VERSION, newVersion).commit();
        deviceInfoSP.edit().putString(Constants.SP_DEVICE_INFO_DOWNLOAD_BIN_LANGUAGE, newLanguage).commit();

        return true;
    }

    public boolean setIsNeedUpdateDiagnoseSW(boolean flag)
    {
        if (getCC() == null)
        {
            return false;
        }
        // ����汾
        deviceInfoSP.edit().putBoolean(Constants.SP_DEVICE_INFO_IS_NEED_UPDATE_DIAGNOSE_SW, flag).commit();
        return true;
    }

    public boolean setIsNeedUpdateDownloadBin(boolean flag)
    {
        if (getCC() == null)
        {
            return false;
        }
        // ����汾
        deviceInfoSP.edit().putBoolean(Constants.SP_DEVICE_INFO_IS_NEED_UPDATE_DOWNLOAD_BIN, flag).commit();
        return true;
    }

    /**
     * �Ƿ���Ҫ����download.bin
     * @return
     * @since DBS V100
     */
    public boolean isNeedUpdateDownloadBin()
    {
        if (deviceInfoSP != null)
        {
            return deviceInfoSP.getBoolean(Constants.SP_DEVICE_INFO_IS_NEED_UPDATE_DOWNLOAD_BIN, false);
        }
        return false;
    }

    /**
     * �Ƿ���Ҫ����������
     * @return
     * @since DBS V100
     */
    public boolean isNeedUpdateDiagnose()
    {
        if (deviceInfoSP != null)
        {
            return deviceInfoSP.getBoolean(Constants.SP_DEVICE_INFO_IS_NEED_UPDATE_DIAGNOSE_SW, false);
        }
        return false;
    }

    /**
     * �������к�
     * @param serials
     * @return
     * @since DBS V100
     */
    public boolean setSerialNumber(List<String> serials)
    {
        if (serials != null && serials.size() > 0)
        {
            String serialNumbers = getCC();
            for (String string : serials)
            {
                serialNumbers += (Constants.DEVICE_INFO_SEPARATED + string);
            }
            deviceInfoSP.edit().putString(serialNumbers, Constants.SP_DEVICE_INFO_SERIAL_NUMBER).commit();
        }
        return true;
    }

    public boolean setLastUpdateConfig(String serials, String vehicle, String version, String language, String destfile)
    {
        StringBuffer lastUpdateConfig = new StringBuffer();
        lastUpdateConfig.append(getCC()).append(Constants.DEVICE_INFO_SEPARATED).append(serials).append(Constants.DEVICE_INFO_SEPARATED).append(vehicle).append(Constants.DEVICE_INFO_SEPARATED)
            .append(version).append(Constants.DEVICE_INFO_SEPARATED).append(language).append(Constants.DEVICE_INFO_SEPARATED).append(destfile);
        List<String> updateInfoes = getUpdateInfo(getCC());
        if (updateInfoes != null && updateInfoes.size() > 0)
        {
            for (String string : updateInfoes)
            {
                String serialHistory = string.split(Constants.DEVICE_INFO_SEPARATED)[1];
                if (serialHistory.equals(serials))
                {
                    deviceInfoSP.edit().remove(string).commit();
                }
            }
        }
        deviceInfoSP.edit().putString(lastUpdateConfig.toString(), Constants.SP_DEVICE_INFO_LAST_UPDATE_INFO).commit();
        return true;
    }

    public boolean clearRecord(String realKey)
    {
        // ��һ����ȡ�ð���������SP�е�����Map
        Map all = deviceInfoSP.getAll();
        List<String> values = new ArrayList<String>();
        // �ڶ�����ȡ��Map�е�����key
        if (all != null && all.size() > 0)
        {
            Set<String> keies = all.keySet();
            // ������������ÿһ��key
            for (String key : keies)
            {
                // �����������ֵ������Ҫ���key��ͬ,��������������Ҫ��CC��ͷ���ַ�����ȡ��
                String value = all.get(key).toString();
                if (value.equals(realKey) && key.startsWith(getCC()))
                {
                    deviceInfoSP.edit().remove(key).commit();
                }
            }
        }
        return true;
    }

    /**
     * ����ǰ׺�͹ؼ���ȡ��������Ϣ
     * @param startWith
     * @param realKey
     * @return
     * @since DBS V100
     */
    public List<String> getValuesOfStartWith(String startWith, String realKey)
    {
        if (startWith == null || realKey == null)
        {
            return null;
        }
        // ��һ����ȡ�ð���������SP�е�����Map
        Map all = deviceInfoSP.getAll();
        List<String> values = new ArrayList<String>();
        // �ڶ�����ȡ��Map�е�����key
        if (all != null && all.size() > 0)
        {
            Set<String> keies = all.keySet();
            // ������������ÿһ��key
            for (String key : keies)
            {
                // �����������ֵ������Ҫ���key��ͬ,��������������Ҫ��CC��ͷ���ַ�����ȡ��
                String value = all.get(key).toString();
                if (value.equals(realKey) && key.startsWith(startWith))
                {

                    values.add(key);
                }
            }
        }
        return values;
    }

    public void removeValuesByKey(String realKey)
    {
        if (realKey == null)
        {
            return;
        }
        // ��һ����ȡ�ð���������SP�е�����Map
        Map all = deviceInfoSP.getAll();

        // �ڶ�����ȡ��Map�е�����key
        if (all != null && all.size() > 0)
        {
            Set<String> keies = all.keySet();
            // ������������ÿһ��key
            for (String key : keies)
            {
                // �����������ֵ������Ҫ���key��ͬ,��������������Ҫ��CC��ͷ���ַ�����ȡ��
                String value = all.get(key).toString();
                if (value.equals(realKey))
                {
                    deviceInfoSP.edit().remove(key).commit();

                }
            }
        }

    }

    public List<String> getUpdateInfo(String cc)
    {
        if (cc == null)
        {
            return null;
        }
        // ��һ����ȡ�ð���������SP�е�����Map
        Map all = deviceInfoSP.getAll();
        List<String> values = new ArrayList<String>();
        // �ڶ�����ȡ��Map�е�����key
        if (all != null && all.size() > 0)
        {
            Set<String> keies = all.keySet();
            // ������������ÿһ��key
            for (String key : keies)
            {
                // �����������ֵ������Ҫ���key��ͬ,��������������Ҫ��CC��ͷ���ַ�����ȡ��
                String value = all.get(key).toString();
                if (value.equals(Constants.SP_DEVICE_INFO_LAST_UPDATE_INFO) && key.startsWith(cc))
                {

                    values.add(key);
                }
            }
        }
        return values;
    }

    public List<String> getVehicles()
    {
        List<String> updateInfo = getUpdateInfo(getCC());
        List<String> vehicles = null;
        if (updateInfo != null && updateInfo.size() > 0)
        {
            vehicles = new ArrayList<String>();
            for (String string : updateInfo)
            {
                String vehicle = string.split(Constants.DEVICE_INFO_SEPARATED)[2];
                vehicles.add(vehicle);
            }
        }
        return vehicles;
    }

    /**
     * ���ĳ�˻��µ���Ӧ����
     * @param realKey
     * @return
     * @since DBS V100
     */
    public List<String> getValues(String cc, String realKey)
    {
        if (cc == null || realKey == null)
        {
            return null;
        }
        // ��һ����ȡ�ð���������SP�е�����Map
        Map all = deviceInfoSP.getAll();
        List<String> values = new ArrayList<String>();
        // �ڶ�����ȡ��Map�е�����key
        if (all != null && all.size() > 0)
        {
            Set<String> keies = all.keySet();
            // ������������ÿһ��key
            for (String key : keies)
            {
                // �����������ֵ������Ҫ���key��ͬ,��������������Ҫ��CC��ͷ���ַ�����ȡ��
                String value = all.get(key).toString();
                if (value.equals(realKey) && key.startsWith(cc))
                {

                    String[] valuesArray = key.split(Constants.DEVICE_INFO_SEPARATED);
                    for (int i = 1; i < valuesArray.length; i++)
                    {
                        values.add(valuesArray[i]);
                    }
                }
            }
        }
        return values;
    }
    
}
