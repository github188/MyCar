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
		// 登录
		mLoginOrderObserver = new ImMsgObserver(ImMsgIds.ORDER_LOGIN, mContext) {

			@Override
			public void dealMessage(final Message msg) {
				if (ImSession.isLogined()) {
					Message replyMsg = new Message();
					replyMsg.what = ImMsgIds.REPLY_LOGIN;
					replyMsg.arg1 = Login.LOGIN_REPEAT;
					ImMsgQueue.getInstance().addMessage(replyMsg);
					Log.e("IM", "server -> 用户已登录");
					return;
				}

				new Thread() {
					public void run() {
						Log.e("IM", "server -> 用户登录线程");

						OnLoginListener onLoginReplay = new OnLoginListener() {

							@Override
							public void onSucc(LoginReply loginReply) {
								// 用户登录后，切换用户数据库
								ImDataProvider.changeUserDatabase(mContext);

								// 将当前登录的用户信息写入SESSION
								ImSession.getInstence().setmUserModel(
										loginReply.getUsermodel());
								ImSession.getInstence().setLoginState(
										ImSession.LOGIN_STATE_LOGINED);

								// 向UI发送消息
								Message replyMsg = new Message();
								replyMsg.what = ImMsgIds.REPLY_LOGIN;
								replyMsg.arg1 = Login.LOGIN_SUCC;
								ImMsgQueue.getInstance().addMessage(replyMsg);

								// 启动postbox
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

						Log.e("IM", "server -> 登录前cleanSession()");
						ImSession.cleanSession();

						Log.e("IM", "server -> 收到登录请求，socket连接中。。。");

						if (!SocketManager.getInstance().createSocket()) {
							Message replyMsg = new Message();
							replyMsg.what = ImMsgIds.REPLY_LOGIN;
							replyMsg.arg1 = Login.LOGOUT_TIMEOUT;
							ImMsgQueue.getInstance().addMessage(replyMsg);
							return;
						}

						SocketManager.getInstance().startAcceptMessage();
						Log.e("IM", "socket连接成功，验证登录信息中。。。");

						Bundle data = msg.getData();

						new Login().doLogin(data.getString("username"),
								data.getString("password"), onLoginReplay);
					}
				}.start();
			}
		};

		// 登出
		mLogoutOrderObserver = new ImMsgObserver(ImMsgIds.ORDER_LOGOUT,
				mContext) {

			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> 用户尚未登录");
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

				Log.e("IM", "mLogoutOrderObserver -> 登出");
				ImSession.cleanSession();
			}
		};

		// 获取在线列表
		mOnlineListObserver = new ImMsgObserver(
				ImMsgIds.ORDER_UPDATE_ONLINE_LIST, mContext) {
			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> 用户尚未登录");
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

		// 发送聊天消息
		mSendChatMessageObserver = new ImMsgObserver(
				ImMsgIds.ORDER_SEND_CHAT_MESSAGE, mContext) {
			@Override
			public void dealMessage(Message msg) {
				if (!ImSession.isLogined()) {
					Log.e("IM", "mLogoutOrderObserver -> 用户尚未登录");
					return;
				}

				String content = msg.getData().getString(ChatLog.CONTENT);
				String sender = msg.getData().getString(ChatLog.SENDER);
				String receiver = msg.getData().getString(ChatLog.RECEIVER);

				Log.e("IM", "mSendChatMessageObserver -> 发送消息" + content);

				TextMessageUtils.saveMessageOfMine(mContext, sender, receiver,
						content);
			}
		};

		// 传送文件
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
							// 上传文件
							new SendFileManager().sendFile(mContext, file,
									taskId);
						} else {
							Log.e("IM", "mSendFileObserver -> 文件都不存在，传什么传？");
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

		// 拒绝接收文件
		mRefuseSendFileObserver = new ImMsgObserver(
				ImMsgIds.ORDER_SEND_FILE_REFUSED, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int sendFileTaskId = data.getInt(SendFileTask._ID);
				sendMessageToSenderFromSocket(sendFileTaskId, false);
			}
		};

		// 下载文件
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

				Log.e("IM", "mDownloadFileObserver -> 收到下载任务" + sendFileTaskId);
				ReceiveFileManager.getInstence().receiveFile(mContext,
						sendFileTaskId);

			}
		};

		// 通知文件接收者，去下载文件
		mSendFileRequestMessageObserver = new ImMsgObserver(
				ImMsgIds.NOTICE_SEND_FILE_REQUEST_MESSAGE, mContext) {

			@Override
			public void dealMessage(Message msg) {
				Bundle data = msg.getData();
				final int sendFileTaskId = data.getInt(SendFileTask._ID);
				sendSendFileRequest(sendFileTaskId);
			}
		};

		// 请求好友列表
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

		// 请求好友在线列表
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

		// 添加好友
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

		// 删除好友
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
				Log.e("IM","删除好友");
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
