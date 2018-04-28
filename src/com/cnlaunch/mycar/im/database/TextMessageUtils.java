package com.cnlaunch.mycar.im.database;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.LastChat;
import com.cnlaunch.mycar.im.model.ChatMessageModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class TextMessageUtils {
	private static byte[] LOCK = new byte[0];

	/**
	 * ���ҷ��͸����ѵ���Ϣ���������ݿ�
	 * 
	 * @param context
	 * @param sender
	 * @param receiver
	 * @param content
	 */
	public static void saveMessageOfMine(Context context, String sender,
			String receiver, String content) {
		synchronized (LOCK) {

			ChatMessageModel chatMessageModel = new ChatMessageModel(sender,
					receiver, content);

			Letter letter = new Letter();
			letter.setSender(sender);
			letter.setReceiver(receiver);
			letter.setContent(JsonConvert.toJson(chatMessageModel));

			Envelope envelope = new Envelope(
					ImConstant.MessageSource.CLIENT_TO_SERVER,
					ImConstant.MessageCategory.CHAT_TEXT_MESSAGE, letter);
			ImSession.getInstence().getPostBox().send(envelope);

			// �������¼���в�����Ϣ��¼
			ContentResolver resoler = context.getContentResolver();
			ContentValues valuesChatLog = new ContentValues();
			valuesChatLog.put(ChatLog.CONTENT, content);
			valuesChatLog.put(ChatLog.SENDER, sender);
			valuesChatLog.put(ChatLog.RECEIVER, receiver);
			valuesChatLog.put(ChatLog.SENDTIME, new Date().getTime());
			valuesChatLog.put(ChatLog.ISREAD, true);
			resoler.insert(ChatLog.CONTENT_URI, valuesChatLog);

			// ����������¼���в�����Ϣ
			ContentValues valuesLastChat = new ContentValues();
			valuesLastChat.put(LastChat.CONTENT, content);
			valuesLastChat.put(LastChat.USERUID, receiver);
			valuesLastChat.put(LastChat.SENDTIME, new Date().getTime());
			valuesLastChat.put(LastChat.UNREAD_COUNT, 0);

			Cursor c = null;
			try {
				c = resoler.query(LastChat.CONTENT_URI, new String[] {
						LastChat._ID, LastChat.UNREAD_COUNT }, LastChat.USERUID
						+ "=?", new String[] { receiver }, null);

				if (c != null) {
					if (c.moveToNext()) {
						int id = c.getInt(0);
						c.close();
						c = null;

						resoler.update(Uri.withAppendedPath(
								LastChat.CONTENT_URI, String.valueOf(id)),
								valuesLastChat, null, null);
					} else {
						resoler.insert(LastChat.CONTENT_URI, valuesLastChat);
					}
				}
			} finally {
				if (c != null) {
					c.close();
				}
			}
		}
	}

	/**
	 * �����ѷ��͸��ҵ���Ϣ���������ݿ�
	 * 
	 * @param context
	 * @param sender
	 * @param receiver
	 * @param content
	 */
	public static void saveMessgeFromFriend(Context context, String sender,
			String receiver, String content) {
		synchronized (LOCK) {
			ContentResolver resoler = context.getContentResolver();
			ContentValues valuesChatLog = new ContentValues();
			valuesChatLog.put(ChatLog.CONTENT, content);
			valuesChatLog.put(ChatLog.SENDER, sender);
			valuesChatLog.put(ChatLog.RECEIVER, receiver);
			// TODO �Խ��յ�ʱ��Ϊ׼
			// values.put(ChatLog.SENDTIME, model.getSendTime());
			valuesChatLog.put(ChatLog.SENDTIME, System.currentTimeMillis());
			// �����ǰ��������û����죬����յ���Ϣ�ᱻ��Ϊ�����Ķ���״̬
			boolean isRead = false;
			if (ImSession.getInstence().getCurrentActivity() == ImSession.ACTIVITY_CHAT
					&& ImSession.getInstence().getCurrentTargetUserUid()
							.equals(sender)) {
				isRead = true;
			}
			valuesChatLog.put(ChatLog.ISREAD, isRead);
			resoler.insert(ChatLog.CONTENT_URI, valuesChatLog);

			Cursor c = null;
			try {
				c = resoler.query(LastChat.CONTENT_URI, new String[] {
						LastChat._ID, LastChat.UNREAD_COUNT }, LastChat.USERUID
						+ "=?", new String[] { sender }, null);

				if (c != null) {
					if (c.moveToNext()) {
						int id = c.getInt(0);
						int unreadCount = c.getInt(1);
						c.close();
						c = null;

						ContentValues valuesLastChat = new ContentValues();
						// ���δ�����򽫸�δ����Ϣ����1
						if (isRead) {
							valuesLastChat.put(LastChat.UNREAD_COUNT,
									unreadCount);
						} else {
							valuesLastChat.put(LastChat.UNREAD_COUNT,
									unreadCount + 1);
						}
						valuesLastChat.put(LastChat.CONTENT, content);
						valuesLastChat.put(LastChat.SENDTIME,
								System.currentTimeMillis());
						valuesLastChat.put(LastChat.USERUID, sender);
						resoler.update(Uri.withAppendedPath(
								LastChat.CONTENT_URI, String.valueOf(id)),
								valuesLastChat, null, null);
					} else {
						ContentValues valuesLastChat = new ContentValues();
						// ���δ�����򽫸�δ����Ϣ����Ϊ1
						if (isRead) {
							valuesLastChat.put(LastChat.UNREAD_COUNT, 0);
						} else {
							valuesLastChat.put(LastChat.UNREAD_COUNT, 1);
						}
						valuesLastChat.put(LastChat.CONTENT, content);
						valuesLastChat.put(LastChat.SENDTIME,
								System.currentTimeMillis());
						valuesLastChat.put(LastChat.USERUID, sender);
						resoler.insert(LastChat.CONTENT_URI, valuesLastChat);
					}
				}

			} finally {
				if (c != null) {
					c.close();
				}
			}
		}

	}
}
