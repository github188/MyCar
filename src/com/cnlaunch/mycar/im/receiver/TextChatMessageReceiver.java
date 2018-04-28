package com.cnlaunch.mycar.im.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.im.ChatActivity;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.common.TextMessageNoticeManager;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.database.TextMessageUtils;
import com.cnlaunch.mycar.im.model.ChatMessageModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class TextChatMessageReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.CHAT_TEXT_MESSAGE;
	private Context mContext;

	public static final String START_FROM_NOTIFICATION = "startFromNotification";

	public TextChatMessageReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {

		ChatMessageModel model = JsonConvert.fromJson(letter.getContent(),
				ChatMessageModel.class);

		TextMessageUtils.saveMessgeFromFriend(mContext, model.getSenderUID(),
				model.getReceiverUID(), model.getContent());

		sendMessage(model.getSenderUID(), model.getContent());

		TextMessageNoticeManager.playSound(mContext);

		// 如果当前不在聊天界面，则在状态栏显示通知
		if (ImSession.getInstence().getCurrentActivity() != ImSession.ACTIVITY_CHAT) {
			TextMessageNoticeManager.setupNotice(mContext,letter.getSender(),
					packContent(letter.getSender(), model.getContent()));

			// 发广播到用户中心,更新推送信息栏
			sendBroadcastToUpdateDBScarSummary();

			// 发广播，提醒收到新的消息
			sendBroadcastNewTextChatMessage();

		}

	}

	private String packContent(String sender, String content) {
		Cursor c = null;
		try {
			c = mContext.getContentResolver().query(
					Friends.CONTENT_URI,
					new String[] { Friends.NICKNAME, Friends.NAME_REMARK,
							Friends.CCNO }, Friends.USERUID + "=?",
					new String[] { sender }, null);
			if (c != null && c.moveToNext()) {
				String nickname = c.getString(0);
				String nameRemark = c.getString(1);
				String ccno = c.getString(2);
				if (nameRemark != null && nameRemark.length() > 0) {
					return nameRemark + " 说:" + content;
				} else if (nickname != null && nickname.length() > 0) {
					return nickname + " 说:" + content;
				} else if (ccno != null && ccno.length() > 0) {
					return nickname + " 说:" + content;
				}
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return content;
	}

	private void sendBroadcastNewTextChatMessage() {
		Intent intent = new Intent(ImConstant.BROADCAST_NEW_TEXT_CHAT_MESSAGE);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 发广播到用户中心,更新推送信息栏
	 */
	private void sendBroadcastToUpdateDBScarSummary() {
		if (MyCarActivity.isRuning) {
			Intent intent = new Intent(Constants.MAIN_DBSCAR_SUMMARY);
			intent.putExtra("message", "refresh");
			mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 发送消息,通知UI
	 * 
	 * @param senderUid
	 * @param content
	 */
	private void sendMessage(String senderUid, String content) {
		Message msg = new Message();
		msg.what = ImMsgIds.NOTICE_RECEIVE_CHAT_MESSAGE;
		Bundle data = new Bundle();
		data.putString(ChatLog.SENDER, senderUid);
		data.putString(ChatLog.CONTENT, content);
		msg.setData(data);
		ImMsgQueue.getInstance().addMessage(msg);
	}





}
