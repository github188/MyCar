package com.cnlaunch.mycar.im.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.common.ImConstant;

import android.content.Context;
import android.util.Log;

public class SendFileThread extends Thread {

	private OnSendFileProgress mOnSendFileProgress = null;
	private File mFile = null;
	private int mTaskId = 0;
	private boolean mIsInterrupted = false;
	private Context mContext;

	public SendFileThread(Context context, int taskId, File file,
			OnSendFileProgress onSendFileProgress) {
		mTaskId = taskId;
		mOnSendFileProgress = onSendFileProgress;
		mFile = file;
		mContext = context;
	}

	@Override
	public void run() {
		if (!mFile.exists()) {
			Log.e("IM", "文件不存在：" + mFile.getAbsolutePath());
			return;
		}
		try {
			mOnSendFileProgress.onStart(mTaskId);

			String end = "\r\n";
			String twoHyphens = "--"; // 两个连字符
			String boundary = "******"; // 分界符的字符串

			URL url = new URL(ImConstant.WEB_SERVER_UPLOAD_URL);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();

			// if (httpURLConnection.getResponseCode() != 200) {
			// mOnSendFileProgress.onFailed(mTaskId, "找不到服务器");
			// Log.e("IM", "找不到服务器");
			// return;
			// }

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(5 * 1000);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Charsert", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			// 设置分界符，加end表示为单独一行
			dos.writeBytes(twoHyphens + boundary + end);
			// 设置与上传文件相关的信息
			dos.writeBytes("Content-Disposition: form-data; name=\"fileData\"; filename=\""
					+ mFile.getName() + "\"" + end);
			// 在上传文件信息与文件内容之间必须有一个空行
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(mFile);
			byte[] buffer = new byte[1024];
			int count = 0;

			long sendSize = 0;
			long totalSize = mFile.length();
			int precent = 0;
			// 读取文件内容，并写入OutputStream对象
			while (!mIsInterrupted && (count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
				sendSize += count;
				int newPrecent = (int) (sendSize * 100 / totalSize);
				if (newPrecent - 5 > precent) {
					precent = newPrecent;
					mOnSendFileProgress.onProgressUpdate(mTaskId,
							newPrecent * 2 / 3);
				}
				dos.flush();
			}

			if (mIsInterrupted) {
				mOnSendFileProgress.onFailed(mTaskId,
						mContext.getString(R.string.im_upload_being_stopped));
				Log.e("IM", "上传被中断了");
				return;
			}
			fis.close();
			// 新起一行
			dos.writeBytes(end);
			// 设置结束符号（在分界符后面加两个连字符）
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			mOnSendFileProgress.onProgressUpdate(mTaskId, 90);

			// 开始读取从服务端传过来的信息
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			Log.e("IM", "SendFileThread.run() -> " + result);
			dos.close();
			is.close();

			mOnSendFileProgress.onProgressUpdate(mTaskId, 100);

			if (result != null) {
				try {
					mOnSendFileProgress
							.onSucc(mTaskId, UUID.fromString(result));
				} catch (IllegalArgumentException e) {
					mOnSendFileProgress
							.onFailed(
									mTaskId,
									mContext.getString(R.string.im_net_error_sendfile_server_response_invalid));
					Log.e("IM", "返回的UUID有误");
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			mOnSendFileProgress.onFailed(mTaskId, mContext.getString(R.string.im_net_error) + e.toString());
		}

	}

	public void interruptUpload() {
		mIsInterrupted = true;
	}

	public static interface OnSendFileProgress {
		public void onStart(int taskId);

		public void onProgressUpdate(int taskId, int progress);

		public void onSucc(int taskId, UUID uuid);

		public void onFailed(int taskId, String errorMsg);
	}

}
