package com.cnlaunch.mycar.im.action;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.SendFileActivity;

public class UploadFileNoticeManager {
	private Context mContext;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private int mNoticeId = 0;
	private PendingIntent mContentIntent;

	public UploadFileNoticeManager(Context context, int id) {
		mContext = context;
		mNoticeId = id;
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void initContentIntent() {
		Intent intent = new Intent(mContext, SendFileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		mContentIntent = PendingIntent.getActivity(mContext, 0, intent,
				Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
	}

	public void setupNotice() {
		int icon = R.drawable.ic_im_notice_upload;
		String noticeContent = mContext.getString(R.string.im_sendfile_upload);
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, noticeContent, when);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		initContentIntent();
	}

	public void updateNotice(String noticeContent, boolean isOnGoing) {
		if (isOnGoing) {
			mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		} else {
			mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
			mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
		mNotificationManager.cancel(mNoticeId);
		mNotification.setLatestEventInfo(mContext,
				mContext.getString(R.string.im_send_file), noticeContent,
				mContentIntent);
		mNotificationManager.notify(mNoticeId, mNotification);
	}
}
