package com.cnlaunch.mycar.im.model;


public class SendFileReply {
    private String FileId; 
    private int AcceptState; 
    private long SendTime;

    
    public SendFileReply(String fileId, int acceptState)
    {
        this(fileId,acceptState,System.currentTimeMillis());
    }

    public SendFileReply(String fileId, int acceptState,long sendTime)
    {
        this.FileId = fileId;
        this.AcceptState = acceptState;
        this.SendTime = sendTime;
    }


	public String getFileId() {
		return FileId;
	}


	public void setFileId(String fileId) {
		FileId = fileId;
	}


	public int getAcceptState() {
		return AcceptState;
	}


	public void setAcceptState(int acceptState) {
		AcceptState = acceptState;
	}


	public long getSendTime() {
		return SendTime;
	}


	public void setSendTime(long sendTime) {
		SendTime = sendTime;
	}



}
