package com.cnlaunch.mycar.updatecenter.tools;

/**
 *	这个是一个简化版的条件锁.
 *  使用方法如下:
 *	<PRE>
 *  先初始化一个实例:
 *	ConditionVariable flag = new ConditionVariable(false);
 *
 * 	线程 1:
 * 		flag.waitForTrue();
 *
 * 	线程 2:
 * 		flag.set(true);
 *  线程 1 等待线程2将 flag 置为真值.
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
