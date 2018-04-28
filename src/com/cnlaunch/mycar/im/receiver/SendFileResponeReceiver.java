package com.cnlaunch.mycar.im.receiver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cnlaunch.mycar.im.action.ImSendFileNoticeManager;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.model.SendFileReply;

public class SendFileResponeReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.CLIENT_SENDFILE_RESPONE;
	private Context mContext;

	public SendFileResponeReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		SendFileReply model = JsonConvert.fromJson(letter.getContent(),
				SendFileReply.class);
		String taskId = null;
		String fileName = null;
		ContentResolver resolver = mContext.getContentResolver();
		if (model != null) {
			Cursor c = null;
			try {
				c = resolver.query(SendFileTask.CONTENT_URI, new String[] {
						SendFileTask._ID, SendFileTask.FILE_NAME },
						SendFileTask.FILE_ID + "=?",
						new String[] { model.getFileId() }, null);
				if (c != null && c.moveToNext()) {
					taskId = c.getString(0);
					fileName = c.getString(1);
				}
			} finally {
				if (c != null) {
					c.close();
				}
			}
		}

		if (taskId != null) {
			ContentValues values = new ContentValues();
			String noticeStr = "";
			if (model.getAcceptState() == SendFileTask.REPLY_ACCEPT) {
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_SENDER_GET_RESPONSE_ACCEPTED);
				noticeStr = "对方已成功接收文件：\"" + fileName + "\"";
			} else {
				values.put(SendFileTask.STATE,
						SendFileTask.STATE_SENDER_GET_RESPONSE_REFUSED);
				noticeStr = "对方拒绝接收文件：\"" + fileName + "\"";
			}
			resolver.update(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(taskId)), values, null, null);
			
			//更新通知消息
			ImSendFileNoticeManager imSendFileNoticeManager = new ImSendFileNoticeManager(
					mContext, Integer.parseInt(taskId));

			imSendFileNoticeManager.setupNotice();
			imSendFileNoticeManager.updateNotice(noticeStr, false);
		}

	}
}
