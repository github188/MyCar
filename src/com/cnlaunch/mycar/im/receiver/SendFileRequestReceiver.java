package com.cnlaunch.mycar.im.receiver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.action.DownloadFileNoticeManager;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.model.SendFileRequest;

public class SendFileRequestReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.CLIENT_SENDFILE_REQUEST;
	private Context mContext;

	public SendFileRequestReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		SendFileRequest model = JsonConvert.fromJson(letter.getContent(),
				SendFileRequest.class);
		if (model != null) {

			// 将记录保存至数据库
			ContentValues values = new ContentValues();
			values.put(SendFileTask.FILE_ID, model.getFileId());
			values.put(SendFileTask.FILE_NAME, model.getFileName());
			values.put(SendFileTask.FILE_SIZE, model.getFileSize());
			values.put(SendFileTask.SEND_USERUID, letter.getSender());
			values.put(SendFileTask.RECEIVE_USERUID, letter.getReceiver());
			values.put(SendFileTask.SEND_TIME, model.getSendTime());
			values.put(SendFileTask.STATE,
					SendFileTask.STATE_RECEIVER_GET_REQUEST);

			ContentResolver resoler = mContext.getContentResolver();
			resoler.insert(SendFileTask.CONTENT_URI, values);

			// 发送通知到状态栏
			DownloadFileNoticeManager downloadFileNoticeManager = new DownloadFileNoticeManager(
					mContext, R.drawable.ic_im_notice_download);
			downloadFileNoticeManager.setupNotice();
			downloadFileNoticeManager.updateNotice("有好友想给您发送文件", false);
		}

	}

}
