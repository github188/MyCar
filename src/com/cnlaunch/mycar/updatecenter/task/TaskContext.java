package com.cnlaunch.mycar.updatecenter.task;

import android.content.Context;

import com.cnlaunch.mycar.updatecenter.connection.Connection;
/**
 * 任务的上下文
 * @author luxingsong
 */
public class TaskContext
{
	Object[] params;
	Connection connection;
	Context  context;
	
	public TaskContext(Context ctx,Object[] params, Connection connection)
	{
		this.params = params;
		this.connection = connection;
		this.context = ctx;
	}

	public Object[] getParams()
	{
		return params;
	}

	public void setParams(Object[] params)
	{
		this.params = params;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
}
