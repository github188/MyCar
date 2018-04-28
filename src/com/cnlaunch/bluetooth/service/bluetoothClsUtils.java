package com.cnlaunch.bluetooth.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class bluetoothClsUtils {
	/** 
     * ���豸��� �ο�Դ�룺platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method createBondMethod = btClass.getMethod("createBond");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
  
    /** 
     * ���豸������ �ο�Դ�룺platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  

    /**
     * ����PIN��
     */
    static public boolean setPin(Class btClass,BluetoothDevice btDevice,String str) throws Exception{
    	try {
			Method setPinMethod = btClass.getDeclaredMethod("setPin",(Class[]) new Object[]{byte[].class});
			Boolean returnValue = (Boolean)setPinMethod.invoke(btDevice, new Object[]{str.getBytes()});
			Log.e("setPin","" + returnValue);
		} catch (SecurityException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return true;
    }
    /** 
     *  
     * @param clsShow 
     */  
    static public void printAllInform(Class clsShow) {  
        try {  
            // ȡ�����з���  
            Method[] hideMethod = clsShow.getMethods();  
            int i = 0;  
            for (; i < hideMethod.length; i++) {  
                Log.e("method name", hideMethod[i].getName());  
            }  
            // ȡ�����г���  
            Field[] allFields = clsShow.getFields();  
            for (i = 0; i < allFields.length; i++) {  
                Log.e("Field name", allFields[i].getName());  
            }  
        } catch (SecurityException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
}
