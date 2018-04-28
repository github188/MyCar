package com.cnlaunch.mycar.updatecenter.connection;

public abstract class Connection
{
	public abstract String getConnectionType();
	public abstract boolean isConnected();
	public abstract String getAddress();
	public abstract void write(byte[] data);
	public abstract void write(byte[] data1,byte[] data2);
	public abstract void addConnectListener(ConnectionListener listener);
	public abstract void removeConnectListener(ConnectionListener listener);
	public abstract void openConnection(String addr);
	public abstract void autoConnection(String mac);
	public abstract void reOpenConn();
    public abstract void setCounter(byte count);
}
