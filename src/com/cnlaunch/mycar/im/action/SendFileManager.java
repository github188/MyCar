package com.cnlaunch.mycar.im.action;

import java.io.File;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.action.SendFileThread.OnSendFileProgress;
import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.SendFileRequest;

public class SendFileManager {
	private ContentObserver mSendFileTaskObserver;
	private SendFileThread mSendFileThread;

	public SendFileManager() {
	}

	public void sendFile(final Context context, final File file,
			final int taskId) {
		Log.e("IM", "sendFile(" + file + ")");
		final ContentResolver resolver = context.getContentResolver();
		final Uri uri = Uri.withAppendedPath(SendFileTask.CONTENT_URI,
				String.valueOf(taskId));

		// 创建消息管理器
		final UploadFileNoticeManager noticeManager = new UploadFileNoticeManager(
				context, taskId);

		// 创建回调函数
		SendFileThread.OnSendFileProgress onSendFileFinished = new OnSendFileProgress() {

			@Override
			public void onSucc(int taskId, UUID uuid) {

				// 更新数据库
				ContentValues values1 = new ContentValues();
				values1.put(SendFileTask.STATE,
						SendFileTask.STATE_SENDER_UPLOAD_FINISHED);
				values1.put(SendFileTask.FILE_ID, uuid.toString());
				resolver.update(uri, values1, null, null);

				sendMessageToReceiverFromSocket(context, taskId);
				noticeManager
						.updateNotice(
								context.getResources()
										.getString(
												R.string.im_sendfile_status_sender_upload_succ),
								false);

				// 更新数据库
				ContentValues values2 = new ContentValues();
				values2.put(SendFileTask.STATE,
						SendFileTask.STATE_SENDER_SEND_REQUEST);
				values2.put(SendFileTask.FILE_ID, uuid.toString());
				resolver.update(uri, values2, null, null);

				resolver.unregisterContentObserver(mSendFileTaskObserver);

			}

			@Override
			public void onFailed(int taskId, String errorMsg) {
				final Uri uri = Uri.withAppendedPath(SendFileTask.CONTENT_URI,
						String.valueOf(taskId));
				Log.e("IM", "SendFileManager -> sendFile failed -> " + errorMsg);
				noticeManager
						.updateNotice(
								context.getString(R.string.im_sendfile_status_receiver_download_failed)
										+ " " + errorMsg, false);

				// 更新数据库
				ContentValues values = new ContentValues();
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_SENDER_UPLOAD_FAILED);
				ContentResolver resolver = context.getContentResolver();
				resolver.update(uri, values, null, null);

				resolver.unregisterContentObserver(mSendFileTaskObserver);

			}

			@Override
			public void onStart(int taskId) {
				noticeManager.setupNotice();
				resolver.registerContentObserver(uri, false,
						mSendFileTaskObserver);
			}

			@Override
			public void onProgressUpdate(int taskId, int progress) {
				noticeManager.updateNotice(context.getString(R.string.im_upload_percent) + progress + "%", true);
			}
		};

		// 开启工作线程
		mSendFileThread = new SendFileThread(context,taskId, file, onSendFileFinished);
		// 创建数据库侦听者，侦听任务是否被中断或取消
		mSendFileTaskObserver = new ContentObserver(new Handler(
				context.getMainLooper())) {
			@Override
			public void onChange(boolean selfChange) {
				if (!selfChange && isTaskCanceled(context, taskId)) {
					mSendFileThread.interruptUpload();
				}
				super.onChange(selfChange);
			}
		};

		mSendFileThread.start();

	}

	/**
	 * 检测上传任务是否被取消
	 * 
	 * @param context
	 * @param taskId
	 * @return
	 */
	private boolean isTaskCanceled(Context context, int taskId) {
		ContentResolver resolver = context.getContentResolver();
		Cursor c = null;
		try {
			c = resolver.query(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(taskId)),
					new String[] { SendFileTask.STATE }, null, null, null);
			if (c.moveToNext()) {
				final int state = c.getInt(0);
				if (state == SendFileTask.STATE_SENDER_UPLOADING) {
					return false;
				}
			}
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		return true;
	}

	public static void sendMessageToReceiverFromSocket(Context context,
			int sendFileTaskId) {
		ContentResolver resolver = context.getContentResolver();
		Cursor c = null;
		try {
			c = resolver.query(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)), new String[] {
							SendFileTask.FILE_ID, SendFileTask.RECEIVE_USERUID,
							SendFileTask.FILE_PATH }, null, null, null);
			if (c.moveToNext()) {
				final String fileId = c.getString(0);
				final String receiver = c.getString(1);
				final String filePath = c.getString(2);
				File file = new File(filePath);

				Letter letter = new Letter();
				letter.setSender(ImSession.getInstence().getUseruid());
				letter.setReceiver(receiver);
				SendFileRequest sendFileRequest = new SendFileRequest(fileId,
						file.getName(), file.length());
				letter.setContent(JsonConvert.toJson(sendFileRequest));
				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.CLIENT_SENDFILE_REQUEST,
						letter);
				ImSession.getInstence().getPostBox().send(envelope);

			}
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
	}

}
