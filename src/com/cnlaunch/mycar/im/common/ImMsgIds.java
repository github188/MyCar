package com.cnlaunch.mycar.im.common;

public class ImMsgIds {

	//登录
	public static final int ORDER_LOGIN = 0;
	public static final int REPLY_LOGIN = 1;
	
	//登出
	public static final int ORDER_LOGOUT = 2;
	public static final int REPLY_LOGOUT = 3;
	
	//在线列表
	public static final int ORDER_UPDATE_ONLINE_LIST = 4;
	public static final int REPLY_ONLINE_LIST_UPDATED = 5;
	
	//好友列表
	public static final int ORDER_UPDATE_FRIEND_LIST = 6;
	public static final int REPLY_FRIEND_LIST_UPDATED = 7;

	//好友在线列表
	public static final int ORDER_UPDATE_FRIEND_ONLINE_LIST = 20;
	public static final int REPLY_FRIEND_ONLINE_LIST_UPDATED = 21;

	
	//发送消息
	public static final int ORDER_SEND_CHAT_MESSAGE = 8;
	public static final int REPLY_SNED_CHAT_MESSAGE = 9;
	
	
	//发送文件(此动作 包含ORDER_UPLOAD_FILE的动作)
	public static final int ORDER_SNED_FILE = 10;
	public static final int REPLY_SNED_FILE = 11;
	
	//下载文件
	public static final int ORDER_DOWNLOAD_FILE = 12;
	public static final int REPLY_DOWNLOAD_FILE = 13;
	
	//上传文件
	public static final int ORDER_UPLOAD_FILE = 14;
	public static final int REPLY_UPLOAD_FILE = 15;
	
	//拒绝接收文件
	public static final int ORDER_SEND_FILE_REFUSED = 16;
	public static final int REPLY_SEND_FILE_REFUSE = 17;
	
	//通知文件接收者，去下载文件
	public static final int NOTICE_SEND_FILE_REQUEST_MESSAGE = 18;
	
	//通知，接收到聊天消息
	public static final int NOTICE_RECEIVE_CHAT_MESSAGE = 19;
	
	//添加好友
	public static final int ORDER_ADD_FRIEND = 22;
	public static final int REPLY_ADD_FRIEND = 23;

	//删除好友
	public static final int ORDER_DEL_FRIEND = 24;
	public static final int REPLY_DEL_FRIEND = 25;

	
}
