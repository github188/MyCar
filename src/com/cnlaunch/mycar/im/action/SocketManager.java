package com.cnlaunch.mycar.im.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.util.Log;

import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.IEnvelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.MessageStream;
import com.cnlaunch.mycar.im.common.TcpMessage;
import com.cnlaunch.mycar.im.model.ImSession;

public class SocketManager {

	private static SocketManager mInstence = new SocketManager();
	private Socket mSocket;
	private MessageStream mMessageStream = new MessageStream();
	private BufferedInputStream mIn;
	private BufferedOutputStream mOut;
	private OnReceiveEnvelopeListener mOnReceiveEnvelopeListener;
	private boolean isStarted = false;
	private String mIp;

	private TcpMessage mTcpm = new TcpMessage();

	private SocketManager() {
		mSocket = new Socket();
	}

	public boolean createSocket() {
		Log.e("IM", "SocketManager.createSocket()");
		try {
			InetAddress mInetAddress = InetAddress
					.getByName(ImConstant.SERVER_DOMAIN_NAME);

			mIp = mInetAddress.getHostAddress();

			if (ImConstant.DEBUG) {
				mIp = ImConstant.SERVER_SOCKET_IP_FOR_TEST;
			}
			Log.e("IM", "RemoteIp:" + mIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		SocketAddress mRemoteAddr = new InetSocketAddress(mIp,
				ImConstant.SERVER_SOCKET_PORT);
		try {
			if (mSocket != null && !mSocket.isClosed()) {
				mSocket.close();
			}
			mSocket = new Socket();
			mSocket.connect(mRemoteAddr);

			mSocket.setKeepAlive(true);
			mOut = new BufferedOutputStream(mSocket.getOutputStream());
			mIn = new BufferedInputStream(mSocket.getInputStream());
			Log.i("IM", "port: " + mSocket.getLocalPort() + " isBound:"
					+ mSocket.isBound() + "  isClosed:" + mSocket.isClosed()
					+ "  isConnected:" + mSocket.isConnected());
			return true;

		} catch (IllegalArgumentException e) {
			Log.e("IM", "createSocket() ->" + e.toString());
		} catch (IOException e) {
			Log.e("IM", "createSocket() ->" + e.toString());
		}
		return false;
	}

	public void setOnReceiveEnvelopeListener(
			OnReceiveEnvelopeListener onReceiveEnvelopeListener) {
		this.mOnReceiveEnvelopeListener = onReceiveEnvelopeListener;
	}

	public void startAcceptMessage() {

		if (isStarted) {
			Log.e("IM", "已开始接收Socket数据，启动请求被忽略");
			return;
		}
		isStarted = true;

		new Thread() {
			public void run() {
				int i = 0;
				while (!mSocket.isClosed()) {

//					Log.i("IM",
//							"读取TCPMsg" + (i++) + " port: "
//									+ mSocket.getLocalPort());
					mTcpm.setContent(null);
					mTcpm.setFlag((byte) 0);
					mTcpm.setSize(0);
					mTcpm.setTypecode((byte) 0);

					// 提取消息
					if (mMessageStream.read(mTcpm)) {
						if (mTcpm.getContent() != null) {
							Log.i("IM", "onReceiveEnvelope()");
							final byte[] tcpMsgContent = mTcpm.getContent();
							if (tcpMsgContent != null) {
								new Thread() {
									public void run() {
										mOnReceiveEnvelopeListener
												.dealEnvelope(new Envelope(
														tcpMsgContent));
									}
								}.start();
							}else{
								Log.e("IM","TcpMessage只有消息头，没有消息体");
							}
						}
					}

					// 从socket中读取数据
					final int PACKET_SIZE = 8192;
					byte[] packetBuff = new byte[PACKET_SIZE];
					int len = 0;
					try {
//						Log.i("IM", "isBound:" + mSocket.isBound()
//								+ "  isClosed:" + mSocket.isClosed()
//								+ "  isConnected:" + mSocket.isConnected());

						len = mIn.read(packetBuff);

						//Log.i("IM", "读取到" + len + "字节");

						if (len > 0) {// 将数据写入MessageStream
							mMessageStream.write(packetBuff, 0, len);
						} else {
							break;
						}
					} catch (IOException e) {
						Log.e("IM", "Socket连接中断：" + e.toString());
						Log.i("IM", "port: " + mSocket.getLocalPort()
								+ " isBound:" + mSocket.isBound()
								+ "  isClosed:" + mSocket.isClosed()
								+ "  isConnected:" + mSocket.isConnected());
						try {
							mSocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}

				}
				Log.e("IM", "Socket关闭,退出socket数据接收");

				try {
					mOut.close();
					mIn.close();
					mOut = null;
					mIn = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

				ImSession.setNetworkDisconnected();
			}
		}.start();

	}

	public static SocketManager getInstance() {
		if (mInstence == null) {
			mInstence = new SocketManager();
		}
		return mInstence;
	}

	public synchronized void send(IEnvelope envelope) {
		try {
			mOut.write(new TcpMessage((byte) 1, (byte) 2, envelope.toBinary())
					.ToBytes());

			mOut.flush();
		} catch (IOException e) {
			ImSession.setNetworkDisconnected();
		}

	}

	public static interface OnReceiveEnvelopeListener {
		public void dealEnvelope(Envelope envelopeListener);
	}

	public void dismiss() {
		// 关闭socket
		try {
			if (!mSocket.isClosed()) {
				Log.e("IM",
						"disconnect() -> mSocket.close() port: "
								+ mSocket.getLocalPort());
				mSocket.close();
			}
		} catch (IOException e) {
			Log.e("IM", "关闭socket时异常" + e.toString());
		}
		mInstence = null;
	}

}
