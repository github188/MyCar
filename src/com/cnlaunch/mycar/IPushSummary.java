package com.cnlaunch.mycar;

import android.content.Context;

/**
 * 
 * <���ܼ���> ��������ʾ����ҳժҪ��Ϣ
 * <������ϸ����>
 * @author xiangyuanmao
 * @version 1.0 2012-5-17
 * @since DBS V100
 */
public interface IPushSummary
{

    /**
     * ���û���¼�ɹ��󣬸�����ģ����Ҫ������Ϣ����ҳ
     * ʵ�����͵Ĺ�������ע�⣺
     * �ڵ���DBSCarSummaryInfo����ע����Ϣʱ���벻Ҫ�����ڸ���
     * ��Ϣʱ����UnRegister����ע����
     * @param cc
     * @since DBS V100
     */
    public void push(String cc,Context context);
}
