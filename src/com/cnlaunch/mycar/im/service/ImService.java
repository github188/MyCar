package com.cnlaunch.mycar.im.service;

import java.io.File;
import java.util.Timer;
import java.util.UUID;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.cnlaunch.mycar.im.action.Login;
import com.cnlaunch.mycar.im.action.Login.OnLoginListener;
import com.cnlaunch.mycar.im.action.ReceiveFileManager;
import com.cnlaunch.mycar.im.action.SendFileManager;
import com.cnlaunch.mycar.im.action.SocketManager;
import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImConstant.FriendKeys;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.database.ImDataProvider;
import com.cnlaunch.mycar.im.database.TextMessageUtils;
import com.cnlaunch.mycar.im.model.IMAddFriendComModel;
import com.cnlaunch.mycar.im.model.IMDelFriendComModel;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.LoginReply;

public class ImService extends Service {

	private ImMsgObserver mLoginOrderObserver;
	private ImMsgObserver mLogoutOrderObserver;
	private ImMsgObserver mOnlineListObserver;
	private ImMsgObserver mSendChatMessageObserver;
	private ImMsgObserver mSendFileObserver;
	private ImMsgObserver mDownloadFileObserver;
	private ImMsgObserver mRefuseSendFileObserver;
	private ImMsgObserver mSendFileRequestMessageObserver;

	private ImMsgObserver mFriendListMessageObserver;
	private ImMsgObserver mFriendOnlineListMessageObserver;
	private ImMsgObserver mAddFriendMessageObserver;
	private ImMsgObserver mDelFriendMessageObserver;

	private Context mContext;

	private Timer mHeartbeatTimer;
	private HeartbeatTask mHeartbeatTask = null;
	private long HEARTBEAT_RATE = 15 * 1000;
	private ContentResolver mResolver;

	@Override
	public void onCreate() {

		mContext = this;
		createMsgObserver();
		registerMsgObserver();
		Log.e("IM", "ImService -> onCreate()");
		initHeartbeat();
		mResolver = mContext.getContentResolver();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("IM", "ImService -> onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.e("IM", "ImService -> onDestroy()");
		stopHeartbeat();
		unRegisterMsgObserver();
		ImSession.cleanSession();
		super.onDestroy();
	}

	private void initHeartbeat() {
		mHeartbeatTimer = new Timer("imHeartbeat", true);
		if (mHeartbeatTask != null) {
			mHeartbeatTask.cancel();
			mHeartbeatTask = null;
		}
		mHeartbeatTask = new HeartbeatTask();
		mHeartbeatTimer
				.schedule(mHeartbeatTask, HEARTBEAT_RATE, HEARTBEAT_RATE);
	}

	private void stopHeartbeat() {
		if (mHeartbeatTask != null) {
			mHeartbeatTask.cancel();
			mHeartbeatTask = null;
		}
		mHeartbeatTimer.cancel();
		mHeartbeatTimer.purge();
	}

	/**
	 * 
	 */
	private void createMsgObserver() {
		// ��¼
		mLoginOrderObserver = new ImMsgObserver(ImMsgIds.ORDER_LOGIN, mContext) {

			@Override
			public void dealMessage(final Message msg) {
				if (ImSession.isLogined()) {
					Message replyMsg = new Message();
					replyMsg.what = ImMsgIds.REPLY_LOGIN;
					replyMsg.arg1 = Login.LOGIN_REPEAT;
					ImMsgQueue.getInstance().addMessage(replyMsg);
					Log.e("IM", "server -> �û��ѵ�¼");
					return;
				}

				new Thread() {
					public void run() {
						Log.e("IM", "server -> �û���¼�߳�");

						OnLoginListener onLoginReplay = new OnLoginListener() {

							@Override
							public void onSucc(LoginReply loginReply) {
								// �û���¼���л��û����ݿ�
								ImDataProvider.changeUserDatabase(mContext);

								// ����ǰ��¼���û���Ϣд��SESSION
								ImSession.getInstence().setmUserModel(
										loginReply.getUsermodel());
								ImSession.getInstence().setLoginState(
										ImSession.LOGIN_STATE_LOGINED);

								// ��UI������Ϣ
								Message replyMsg = new Message();
								replyMsg.what = ImMsgIds.REPLY_LOGIN;
								replyMsg.arg1 = Login.LOGIN_SUCC;
								ImMsgQueue.getInstance().addMessage(replyMsg);

								// ����postbox
								ImSession.initSession(mContext);
							}

							@Override
							public void onFail(LoginReply loginReply) {
								Message replyMsg = new Message();
								replyMsg.what = ImMsgIds.REPLY_LOGIN;
								replyMsg.arg1 = Login.LOGOUT_FAIL;
								Bundle data = new Bundle();
								data.putString("data",
										loginReply.getDescription());
								replyMsg.setData(data);
								ImMsgQueue.getInstance().addMessage(replyMsg);
							}
						};

						Log.e("IM", "server -> ��¼ǰcleanSession()");
						ImSession.cleanSession();

						Log.e("IM", "server -> �յ���¼����socket�����С�����");

						if (!SocketManager.getInstance().createSocket()) {
							Message replyMsg = new Message();
							replyMsg.what = ImMsgIds.REPLY_LOGIN;
							replyMsg.arg1 = Login.LOGOUT_TIMEOUT;
							ImMsgQueue.getInstance().addMessage(replyMsg);
							return;
						}

						SocketManager.getInstance().startAcceptMessage();
						Log.e("IM", "socket���ӳɹ�����֤��¼��Ϣ�С�����");

						Bundle data = msg.getData();

						new Login().doLogin(data.getString("username"),
								data.getString("password"), onLoginReplay);
					}
				}.start();
			}
		};

		// �ǳ�
		mLogoutOrderObserver = new ImMsgObserver(ImMsgIds.ORDER_LOGOUT,
				mContext) {

			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> �û���δ��¼");
					return;
				}

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);
				letter.setContent("");
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.LOGIN_OUT, letter);
				ImSession.getInstence().getPostBox().send(envelope);

				Message replyMsg = new Message();
				replyMsg.what = ImMsgIds.REPLY_LOGOUT;
				replyMsg.arg1 = Login.LOGOUT_SUCC;
				ImMsgQueue.getInstance().addMessage(replyMsg);

				Log.e("IM", "mLogoutOrderObserver -> �ǳ�");
				ImSession.cleanSession();
			}
		};

		// ��ȡ�����б�
		mOnlineListObserver = new ImMsgObserver(
				ImMsgIds.ORDER_UPDATE_ONLINE_LIST, mContext) {
			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> �û���δ��¼");
					return;
				}
				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);
				letter.setContent("");
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.GET_ONLINEUSERINFO_LIST,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);
			}
		};

		// ����������Ϣ
		mSendChatMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_SEND_CHAT_MESSAGE, mContext) {
			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> �û���δ��¼");
					return;
				}

				String content = msg.getData().getString(ChatLog.CONTENT);
				String sender = msg.getData().getString(ChatLog.SENDER);
				String receiver = msg.getData().getString(ChatLog.RECEIVER);

				Log.e("IM", "mSendChatMessageObserver -> ������Ϣ" + content);

				TextMessageUtils.saveMessageOfMine(mContext, sender, receiver,
						content);
			}
		};

		// �����ļ�
		mSendFileObserver = new ImMsgObserver(ImMsgIds.ORDER_SNED_FILE,
				mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int taskId = Integer.parseInt(data
						.getString(SendFileTask._ID));

				Cursor c = null;
				try {
					c = mResolver.query(Uri.withAppendedPath(
							SendFileTask.CONTENT_URI, String.valueOf(taskId)),
							new String[] { SendFileTask._ID,
									SendFileTask.FILE_PATH }, null, null, null);
					if (c != null && c.moveToNext()) {
						String filePath = c.getString(1);
						final File file = new File(filePath);
						if (file.exists()) {
							// �ϴ��ļ�
							new SendFileManager().sendFile(mContext, file,
									taskId);
						} else {
							Log.e("IM", "mSendFileObserver -> �ļ��������ڣ���ʲô����");
						}

					}
				} finally {
					if (c != null) {
						c.close();
						c = null;
					}
				}

			}
		};

		// �ܾ������ļ�
		mRefuseSendFileObserver = new ImMsgObserver(
				ImMsgIds.ORDER_SEND_FILE_REFUSED, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int sendFileTaskId = data.getInt(SendFileTask._ID);
				sendMessageToSenderFromSocket(sendFileTaskId, false);
			}
		};

		// �����ļ�
		mDownloadFileObserver = new ImMsgObserver(ImMsgIds.ORDER_DOWNLOAD_FILE,
				mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int sendFileTaskId = data.getInt(SendFileTask._ID);

				ContentValues values = new ContentValues();
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_RECEIVER_DOWNLOADING);
				mResolver.update(
						Uri.withAppendedPath(SendFileTask.CONTENT_URI,
								String.valueOf(sendFileTaskId)), values, null,
						null);

				Log.e("IM", "mDownloadFileObserver -> �յ���������" + sendFileTaskId);
				ReceiveFileManager.getInstence().receiveFile(mContext,
						sendFileTaskId);

			}
		};

		// ֪ͨ�ļ������ߣ�ȥ�����ļ�
		mSendFileRequestMessageObserver = new ImMsgObserver(
				ImMsgIds.NOTICE_SEND_FILE_REQUEST_MESSAGE, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int sendFileTaskId = data.getInt(SendFileTask._ID);
				sendSendFileRequest(sendFileTaskId);
			}
		};

		// ��������б�
		mFriendListMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_UPDATE_FRIEND_LIST, mContext) {

			@Override
			public void dealMessage(Message msg) {
				IMMyFriendComModel model = new IMMyFriendComModel();
				model.setUserUID(ImSession.getInstence().getUseruid());
				model.setTaskID(UUID.randomUUID().toString());

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);

				letter.setContent(JsonConvert.toJson(model));
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.CLIENT_GETFRIENDLISTBYUID_REQUEST,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);
			}
		};

		// ������������б�
		mFriendOnlineListMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_UPDATE_FRIEND_ONLINE_LIST, mContext) {

			@Override
			public void dealMessage(Message msg) {

				IMMyFriendComModel model = new IMMyFriendComModel();
				model.setUserUID(ImSession.getInstence().getUseruid());
				model.setTaskID(UUID.randomUUID().toString());

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);

				letter.setContent(JsonConvert.toJson(model));
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.CLIENT_GETFRIENDONLINELISTBYUID_REQUEST,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);
			}
		};

		// ��Ӻ���
		mAddFriendMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_ADD_FRIEND, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				String targetUserUid = null;
				if (data != null) {
					targetUserUid = data.getString(FriendKeys.USERUID);
				}
				if (targetUserUid == null) {
					return;
				}

				IMAddFriendComModel model = new IMAddFriendComModel();
				IMMyFriendComModel sourceModel = new IMMyFriendComModel();
				IMMyFriendComModel destModel = new IMMyFriendComModel();
				sourceModel.setUserUID(ImSession.getInstence().getUseruid());
				destModel.setUserUID(targetUserUid);

				model.setSourceUserComModel(sourceModel);
				model.setDestUserComModel(destModel);

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);

				letter.setContent(JsonConvert.toJson(model));
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.SOURCE_REQ_ADD_FRIEND,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);
			}
		};

		// ɾ������
		mDelFriendMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_DEL_FRIEND, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				String targetUserUid = null;
				if (data != null) {
					targetUserUid = data.getString(FriendKeys.USERUID);
				}
				if (targetUserUid == null) {
					return;
				}

				IMDelFriendComModel model = new IMDelFriendComModel();

				model.setSourceUserUID(ImSession.getInstence().getUseruid());
				model.setDestUserUID(targetUserUid);

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);

				letter.setContent(JsonConvert.toJson(model));
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.SOURCE_REQ_DEL_FRIEND,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);
				Log.e("IM","ɾ������");
			}
		};

	}

	private void sendSendFileRequest(int sendFileTaskId) {
		SendFileManager.sendMessageToReceiverFromSocket(this, sendFileTaskId);
	}

	private void sendMessageToSenderFromSocket(int sendFileTaskId,
			boolean isAccept) {
		ReceiveFileManager.sendMessageToSenderFromSocket(this, sendFileTaskId,
				isAccept);
	}

	private void registerMsgObserver() {
		unRegisterMsgObserver();
		ImMsgQueue.getInstance().registerObserver(mLoginOrderObserver);
		ImMsgQueue.getInstance().registerObserver(mLogoutOrderObserver);
		ImMsgQueue.getInstance().registerObserver(mOnlineListObserver);
		ImMsgQueue.getInstance().registerObserver(mSendChatMessageObserver);
		ImMsgQueue.getInstance().registerObserver(mSendFileObserver);
		ImMsgQueue.getInstance().registerObserver(mDownloadFileObserver);
		ImMsgQueue.getInstance().registerObserver(mRefuseSendFileObserver);
		ImMsgQueue.getInstance().registerObserver(
				mSendFileRequestMessageObserver);
		ImMsgQueue.getInstance().registerObserver(mFriendListMessageObserver);
		ImMsgQueue.getInstance().registerObserver(
				mFriendOnlineListMessageObserver);
		ImMsgQueue.getInstance().registerObserver(mAddFriendMessageObserver);
		ImMsgQueue.getInstance().registerObserver(mDelFriendMessageObserver);

	}

	private void unRegisterMsgObserver() {
		ImMsgQueue.getInstance().unRegisterObserver(mLoginOrderObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mLogoutOrderObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mOnlineListObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mSendChatMessageObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mSendFileObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mDownloadFileObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mRefuseSendFileObserver);
		ImMsgQueue.getInstance().unRegisterObserver(
				mSendFileRequestMessageObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mFriendListMessageObserver);
		ImMsgQueue.getInstance().unRegisterObserver(
				mFriendOnlineListMessageObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mAddFriendMessageObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mDelFriendMessageObserver);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new IImServiceRemote.Stub() {

			@Override
			public boolean isLogined() throws RemoteException {
				return ImSession.isLogined();
			}
		};
	}

}
