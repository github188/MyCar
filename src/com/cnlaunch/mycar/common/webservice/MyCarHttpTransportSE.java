package com.cnlaunch.mycar.common.webservice;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

/**
 * HttpTransportSE�����࣬��Ҫ����Ϊksoap2��2.5.4
 * ��֧���������ӳ�ʱ�쳣������չ����ʵ��֮
 * @author xiangyuanmao
 *
 */
public class MyCarHttpTransportSE extends HttpTransportSE {

	private int timeout = 15 * 1000; // ���ó�ʱʱ��Ϊ15��
	
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

