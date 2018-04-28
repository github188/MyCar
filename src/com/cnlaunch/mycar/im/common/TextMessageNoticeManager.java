package com.cnlaunch.mycar.im.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.ChatActivity;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.receiver.TextChatMessageReceiver;

public class TextMessageNoticeManager {
	/**
	 * 在通知栏显示消息
	 */
	public static void setupNotice(Context context,String userUid, String messageContent) {
		NotificationManager notifactionManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int noticeId = R.drawable.ic_im_notice_new_chat_message;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(noticeId, messageContent, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, ChatActivity.class);
		intent.putExtra(TextChatMessageReceiver.START_FROM_NOTIFICATION, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(ImConstant.LastChatKeys.USERUID, userUid);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notifactionManager.cancel(noticeId);
		notification.setLatestEventInfo(context, "有新的消息", messageContent,
				contentIntent);
		notifactionManager.notify(noticeId, notification);

	}
	
	public static void cancelAllNotice(Context context){
		NotificationManager notifactionManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int noticeId = R.drawable.ic_im_notice_new_chat_message;
		notifactionManager.cancel(noticeId);

	}
	
	/**
	 * 播放提示声音
	 */
	public static void playSound(Context context) {
		SharedPreferences store = context.getSharedPreferences(
				ImConstant.IM_SHARED_PREFERENCES_NAME
						+ ImSession.getInstence().getUseruid(),
				Context.MODE_PRIVATE);
		boolean soundFlag = store.getBoolean(ImConstant.CONFIG_SOUND, true);
		if (!soundFlag) {
			return;
		}

		// 播放声音"滴滴滴"
		MediaPlayer mMediaPlayer = null;
		mMediaPlayer = MediaPlayer.create(context, R.raw.im_msg_dididi);
		mMediaPlayer.start();
		

	}
}
