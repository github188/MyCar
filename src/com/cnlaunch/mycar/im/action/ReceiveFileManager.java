package com.cnlaunch.mycar.im.action;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.SendFileReply;

public class ReceiveFileManager {
	private static ReceiveFileManager instence = new ReceiveFileManager();

	private ReceiveFileManager() {
	}

	public static ReceiveFileManager getInstence() {
		return instence;
	}

	public void receiveFile(final Context context, final int taskId) {

		// 创建消息管理器
		final DownloadFileNoticeManager noticeManager = new DownloadFileNoticeManager(
				context, taskId);

		ReceiveFileThread.OnReceiveFileProgress onReceiveFileProgress = new ReceiveFileThread.OnReceiveFileProgress() {

			@Override
			public void onSucc(int taskId, File file) {

				// 更新数据库
				ContentValues values = new ContentValues();
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_RECEIVER_DOWNLOAD_FINISHED);
				context.getContentResolver().update(
						Uri.withAppendedPath(SendFileTask.CONTENT_URI,
								String.valueOf(taskId)), values, null, null);

				// 向文件发送者发送消息：已成功接收
				sendMessageToSenderFromSocket(context, taskId, true);

				noticeManager.updateNotice(
						context.getResources().getString(
								R.string.im_download_succ)
								+ ":\"" + file.getName() + "\"", false);
				// Toast.makeText(context,
				// "文件下载成功,已保存至：" + file.getAbsolutePath(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onStart(int taskId) {
				noticeManager.setupNotice();
			}

			@Override
			public void onProgressUpdate(int taskId, int progress) {
				noticeManager.updateNotice(
						context.getResources().getString(
								R.string.im_donwload_percent)
								+ progress + "%", true);

			}

			@Override
			public void onFailed(int taskId, String errorMsg) {
				ContentValues values = new ContentValues();
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_RECEIVER_DOWNLAOD_FAILED);
				context.getContentResolver().update(
						Uri.withAppendedPath(SendFileTask.CONTENT_URI,
								String.valueOf(taskId)), values, null, null);

			}
		};

		new ReceiveFileThread(context, taskId, onReceiveFileProgress).start();

	}

	/**
	 * 向文件发送者发送消息：已成功接收
	 * 
	 * @param context
	 * @param sendFileTaskId
	 * @param isAccept
	 */
	public static void sendMessageToSenderFromSocket(Context context,
			int sendFileTaskId, boolean isAccept) {
		Cursor c = null;
		try {
			c = context.getContentResolver().query(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)),
					new String[] { SendFileTask.FILE_ID,
							SendFileTask.SEND_USERUID }, null, null, null);
			if (c.moveToNext()) {
				final String fileId = c.getString(0);
				final String receiver = c.getString(1);
				final String sender = ImSession.getInstence().getUseruid();

				int acceptState = SendFileTask.REPLY_ACCEPT;
				if (!isAccept) {
					acceptState = SendFileTask.REPLY_REFUSE;
				}

				SendFileReply model = new SendFileReply(fileId, acceptState,
						System.currentTimeMillis());

				Letter letter = new Letter();
				letter.setSender(sender);
				letter.setReceiver(receiver);
				letter.setContent(JsonConvert.toJson(model));

				Envelope envelope = new Envelope(
						ImConstant.MessageSource.CLIENT_TO_SERVER,
						ImConstant.MessageCategory.CLIENT_SENDFILE_RESPONE,
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
