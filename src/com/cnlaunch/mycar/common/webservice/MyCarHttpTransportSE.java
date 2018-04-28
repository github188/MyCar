package com.cnlaunch.mycar.common.webservice;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

/**
 * HttpTransportSE的子类，主要是因为ksoap2的2.5.4
 * 不支持设置链接超时异常，故扩展此类实现之
 * @author xiangyuanmao
 *
 */
public class MyCarHttpTransportSE extends HttpTransportSE {

	private int timeout = 15 * 1000; // 设置超时时间为15秒
	
	public MyCarHttpTransportSE(String url) {
		super(url);
	}
	
	public MyCarHttpTransportSE(String url, int timeout) {
		super(url);
		this.timeout = timeout;
	}
    @Override
    protected ServiceConnection getServiceConnection() throws IOException {
    	ServiceConnectionSE serviceConnection = new ServiceConnectionSE(this.url,timeout);
        return serviceConnection;
    }
}

