package com.cnlaunch.mycar.im.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;

public class ReceiveFileThread extends Thread {
	private OnReceiveFileProgress mOnReceiveFileProgress = null;
	private int mTaskId = 0;
	private Context mContext = null;

	public ReceiveFileThread(Context context, final int taskId,
			final OnReceiveFileProgress onReceiveFileProgress) {
		mOnReceiveFileProgress = onReceiveFileProgress;
		mTaskId = taskId;
		mContext = context;
	}

	@Override
	public void run() {
		Cursor c = null;
		String fileId = null;
		String fileName = null;
		try {
			c = mContext.getContentResolver()
					.query(Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(mTaskId)),
							new String[] { SendFileTask.FILE_ID,
									SendFileTask.FILE_NAME }, null, null, null);
			if (c.moveToNext()) {
				fileId = c.getString(0);
				fileName = c.getString(1);
			}
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}

		if (fileId == null || fileId.length() == 0 || fileName == null
				|| fileName.length() == 0) {
			Log.e("IM", "ReceiveFileThread -> 文件名及文件Id为空");
			return;
		}

		// 下载文件
		URL url;
		try {
			mOnReceiveFileProgress.onStart(mTaskId);

			url = new URL(ImConstant.WEB_SERVER_DOWNLOAD_URL + "?fileId="
					+ fileId);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();

			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setConnectTimeout(5 * 1000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);

			InputStream inputStream = httpURLConnection.getInputStream();
			File rootDir = new File(Env.getAppRootDirInSdcard(),
					ImConstant.DIR_NAME_RECEIVED);
			if (!ensureDirExist(rootDir)) {
				mOnReceiveFileProgress.onFailed(
						mTaskId,
						"TaskId="
								+ mTaskId
								+ mContext.getResources().getString(
										R.string.im_folder_create_failed));
				return;
			}
			File saveFile = new File(rootDir, fileName);
			FileOutputStream fos = new FileOutputStream(saveFile);

			byte[] buf = new byte[8192];
			long totalSize = httpURLConnection.getContentLength();
			int numRead = 0;
			int count = 0;
			int precent = 0;
			while ((numRead = inputStream.read(buf)) > 0) {
				fos.write(buf, 0, numRead);
				count += numRead;
				int newPrecent = (int) (count * 100 / totalSize);
				if (newPrecent - precent > 5) {
					precent = newPrecent;
					mOnReceiveFileProgress.onProgressUpdate(mTaskId, precent);
				}

			}
			fos.close();
			inputStream.close();
			mOnReceiveFileProgress.onSucc(mTaskId, saveFile);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			mOnReceiveFileProgress.onFailed(mTaskId, "TaskId=" + mTaskId + "\n"
					+ e.toString());
		}

		super.run();
	}

	private boolean ensureDirExist(File rootDir) {
		if (rootDir.exists()) {
			return true;
		} else {
			return rootDir.mkdirs();
		}
	}

	public static interface OnReceiveFileProgress {
		public void onStart(int taskId);

		public void onProgressUpdate(int taskId, int progress);

		public void onSucc(int taskId, File file);

		public void onFailed(int taskId, String errorMsg);
	}

}
