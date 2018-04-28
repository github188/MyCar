package com.cnlaunch.mycar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cnlaunch.mycar.common.config.MyCarConfig;

public class DisplayDBSCarInfo
{
    
    private String cc;
    private MyCarActivity context;
    
    public DisplayDBSCarInfo(String cc, MyCarActivity context)
    {
        this.cc = cc;
        this.context = context;
    }

    public void execute()
    {

        List<Class<IPushSummary>> classList = getAllSubClass(IPushSummary.class, 
            MyCarConfig.classesForDBSCarSummary);
        if (classList != null)
        {
            executeAllSubClassMethod(classList, "push", cc, context,new Class<?>[]{String.class, Context.class});
        }
        Intent intent = new Intent("com.cnlaunch.mycar.DBSCarSummaryBroadcastReceiver");
        intent.putExtra("message", "收到车云网摘要信息");
        context.sendBroadcast(intent);
    }


    private void executeAllSubClassMethod(
        List<Class<IPushSummary>> classList, 
        String methodName,
        String cc, 
        Context context, 
        Class<?>[] paras)
    {
        for (Class<IPushSummary> clazz : classList)
        {
            try
            {
                Object obj = clazz.getConstructor(new Class[] {}).newInstance(new Object[] {});
                Method method = clazz.getMethod(methodName, paras);
                method.invoke(obj, new Object[] { cc, context });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param clazz 接口类
     * @param initClassNames 已在配置文件中已注册，并实现了该接口的类
     * @return 所有实现该接口的类
     */
    private <T> List<Class<T>> getAllSubClass(Class<T> clazz, String[] initClassNames)
    {
        ArrayList<Class<T>> list = new ArrayList<Class<T>>();
        for (String className : initClassNames)
        {
            try
            {
                list.add((Class<T>) Class.forName(className));
            }
            catch (ClassNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }
}
