package com.cnlaunch.mycar.im.model;

public class SendFileRequest {
	private String FileId;
	private String FileName;
	private long FileSize;
	private long SendTime;

	public SendFileRequest(String fileId, String fileName, long fileSize) {
		this(fileId, fileName, fileSize, System.currentTimeMillis());
	}

	public SendFileRequest(String fileId, String fileName, long fileSize,
			long sendTime) {
		this.FileId = fileId;
		this.FileName = fileName;
		this.FileSize = fileSize;
		this.SendTime = sendTime;
	}

	public String getFileId() {
		return FileId;
	}

	public void setFileId(String fileId) {
		FileId = fileId;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public long getFileSize() {
		return FileSize;
	}

	public void setFileSize(long fileSize) {
		FileSize = fileSize;
	}

	public long getSendTime() {
		return SendTime;
	}

	public void setSendTime(long sendTime) {
		SendTime = sendTime;
	}

}
