package com.cnlaunch.mycar.updatecenter.tools;

/**
 *	�����һ���򻯰��������.
 *  ʹ�÷�������:
 *	<PRE>
 *  �ȳ�ʼ��һ��ʵ��:
 *	ConditionVariable flag = new ConditionVariable(false);
 *
 * 	�߳� 1:
 * 		flag.waitForTrue();
 *
 * 	�߳� 2:
 * 		flag.set(true);
 *  �߳� 1 �ȴ��߳�2�� flag ��Ϊ��ֵ.
 *	</PRE>
 */

public class ConditionVariable
{	private volatile boolean isTrue;

	public ConditionVariable( boolean isTrue ){ this.isTrue = isTrue; }

	public synchronized boolean isTrue()
	{	return isTrue;
	}

	public synchronized void set( boolean how )
	{	if( (isTrue = how) == true )
			notifyAll();
	}

	public final synchronized void waitForTrue() throws InterruptedException
	{	while( !isTrue )
			wait();
	}
}
