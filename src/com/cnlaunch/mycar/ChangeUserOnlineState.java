package com.cnlaunch.mycar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.cnlaunch.mycar.common.config.MyCarConfig;

public class ChangeUserOnlineState extends Thread
{
    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        super.run();
        executeAfterLogin();
    }
    public void executeAfterLogin()
    {
        List<Class<IUserOnlineState>> classList = 
            getAllSubClass(IUserOnlineState.class, MyCarConfig.classesForUserOnlineState);
        if (classList != null)
        {
            executeAllSubClassMethod(classList, "loginSuccess");
        }
    }
    public void executeAfterLogout()
    {
        List<Class<IUserOnlineState>> classList = 
            getAllSubClass(IUserOnlineState.class, MyCarConfig.classesForUserOnlineState);
        if (classList != null)
        {
            executeAllSubClassMethod(classList, "logout");
        }
    }
    private void executeAllSubClassMethod(List<Class<IUserOnlineState>> classList, String methodName)
    {
        for (Class<IUserOnlineState> clazz : classList)
        {
            try
            {
                Object obj = clazz.getConstructor().newInstance();
                Method method = clazz.getMethod(methodName);
                method.invoke(obj);
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
