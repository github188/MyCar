package com.cnlaunch.mycar.im.action;

import java.util.ArrayList;

import android.util.Log;

import com.cnlaunch.mycar.im.action.SocketManager.OnReceiveEnvelopeListener;
import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.IEnvelope;
import com.cnlaunch.mycar.im.common.ILetterReceiver;
import com.cnlaunch.mycar.im.common.IPostBox;
import com.cnlaunch.mycar.im.model.ImSession;

public class PostBox implements IPostBox {

	private String mId = null;
	private ArrayList<ILetterReceiver> mReceivers = null;
	private ArrayList<IEnvelope> mEnvelopes = null;
	private boolean mIsOpened = false;

	public PostBox(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id can not be null");
		}

		this.mId = id;
		this.mReceivers = new ArrayList<ILetterReceiver>();
		this.mEnvelopes = new ArrayList<IEnvelope>();
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public synchronized void send(IEnvelope envelope) {
		SocketManager.getInstance().send(envelope);

	}

	@Override
	public void dispatchEnvelope() {
		Log.i("IM", "dispatchEnvelope()");
		IEnvelope envelope = null;
		while ((envelope = envelopeOut()) != null) {
			final IEnvelope e = envelope;
			// TODO 优化：开线程池管理线程
			for (final ILetterReceiver receiver : mReceivers) {
				if (receiver.getId() == e.getCategory()) {
					// 开线程处理信件
					new Thread() {
						public void run() {
							Log.e("IM", "receiverId -> " + receiver.getId());
							receiver.dealLetter(e.getLetter());
						}
					}.start();
				}
			}
		}

	}

	@Override
	public void regiestReceiver(ILetterReceiver letterReceiver) {
		synchronized (mReceivers) {
			letterReceiver.setPostBox(this);
			mReceivers.add(letterReceiver);
		}
	}

	@Override
	public void unRegiestReceiver(ILetterReceiver letterReceiver) {
		synchronized (mReceivers) {
			mReceivers.remove(letterReceiver);
		}
	}

	@Override
	public void open() {
		Log.e("IM", "PostBox.open()");
		if (mIsOpened) {
			return;
		}
		mIsOpened = true;

		SocketManager.getInstance().setOnReceiveEnvelopeListener(
				new OnReceiveEnvelopeListener() {

					@Override
					public void dealEnvelope(Envelope envelope) {
						if (ImSession.isLogined()) {
							envelopeIn(envelope);
							dispatchEnvelope();
						} else {
							// TODO 异常处理，用户已退出登录，但却收到了消息
						}
					}
				});

	}

	private void envelopeIn(IEnvelope envelope) {
		synchronized (mEnvelopes) {
			mEnvelopes.add(envelope);
		}
	}

	private IEnvelope envelopeOut() {
		synchronized (mEnvelopes) {
			if (mEnvelopes.size() > 0) {
				return mEnvelopes.remove(0);
			} else {
				return null;
			}
		}
	}

	@Override
	public void close() {
		synchronized (mReceivers) {
			mReceivers.clear();
			mIsOpened = false;
		}
	}

}
