package com.cnlaunch.mycar.im.common;

import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.database.ImData.LastChat;

public class ImConstant {

	public static final boolean DEBUG = false;

	public static final String STR_IM_JUMP_TARGET = "str_im_jump_target";
	public static final String JUMP_TARGET_CHAT_LOG = "jump_target_chat_log";
	public static final String IM_SERVICE_NAME = "com.cnlaunch.mycar.im.intent.action.ImService";

	public static final String WEB_SERVER_DOWNLOAD_URL = "http://webapi.dbscar.com:8081/IM/DownloadFile.ashx";
	public static final String WEB_SERVER_UPLOAD_URL = "http://webapi.dbscar.com:8081/IM/UploadFileAshx.ashx";
	public static final String WEB_SERVER_SEARCH_FRIEND = "http://webapi.dbscar.com:8081/IM/SearchFriend.ashx";
	public static final String WEB_SERVER_GET_USER_INFO = "http://webapi.dbscar.com:8081/IM/RequestSomeOneInfo.ashx";

	public static final int UPLOAD_FILE_SIZE_MAX = 4 * 1024 * 1024;
	/**
	 * 测试环境IP
	 */
	public static final String SERVER_SOCKET_IP_FOR_TEST = "192.168.19.62";
	/**
	 * 线上环境域名，用于动态解析IP
	 */
	public static final String SERVER_DOMAIN_NAME = "tcpconn1.dbscar.com";
	/**
	 * 服务器socket端口
	 */
	public static final int SERVER_SOCKET_PORT = 2488;

	public static final String SYS_LOGIN_SERVER = "SERVER_LOGIN_00001";

	public static final String IM_SHARED_PREFERENCES_NAME = "IM_SHARED_PREFERENCES";

	public static final String CONFIG_SOUND = "config_sound";

	public static final String CONFIG_DB_NAME_PREFIX = "im_db_";
	public static final String CONFIG_DB_NAME_ANONYMOUS = "im_db_anonymous";

	public static final String DIR_NAME_RECEIVED = "fileReceived";

	public static final String BROADCAST_NEW_TEXT_CHAT_MESSAGE = "com.cnlaunch.mycar.broadcast_new_text_chat_message";

	/** 消息接收者 */
	public static class Reciver {
		public static final int LOGIN = 0x000003;
		public static final int LOGINOUT = 0x000004;
		public static final int CHAT_TEXT_MESSAGE = 0x000007;

		public static final int SOURCE_REQ_ADD_FRIEND = 0x000009; // 发起人的添加好友请求
		public static final int SERVER_DEST_RUQ_ADD_FRIEND = 0x00000A; // 服务器转发给被添加人的添加请求(通过TaskID进行标识),并等待回复
		public static final int DEST_SERVER_ACK_ADD_FRIEND = 0x00000B; // 被添加人给服务器的的关于添加请求的回复(通过TaskID进行标识)
		public static final int SERVER_SOURCE_ACK_ADD_FRIEND = 0x00000C; // 服务器将给加好友的发起人发送的添加结果(成功或失败)
		public static final int SERVER_DEST_ACK_ADD_FRIEND = 0x00000D; // 服务器给被添加人发送其已被添加为好友的成功信息

		public static final int SOURCE_REQ_DEL_FRIEND = 0x00000F; // 发起人的删除好友请求
		public static final int SERVER_SOURCE_ACK_DEL_FRIEND = 0x00001F; // 服务器将给删除好友的发起人发送的删除结果
		public static final int SERVER_DEST_ACK_DEL_FRIEND = 0x000002F; // 服务器给被删除人发送其已被删除好友的消息

		public static final int GET_ONLINEUSERINFO_LIST = 0x000011;

		public static final int SERVER_CMD_CLIENT_UPDATE_ONLINEUSERINFO_LIST = 0x000012;// 服务器强制命令客户端刷新在线用户列表
		public static final int SERVER_CALLALL_SOMEONEONLINE = 0x000021;// 服务器广播某客户端登录在线了
		public static final int SERVER_CALLALL_SOMEONEOFFLINE = 0x000022;// 服务器广播某客户端下线了
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFONLINE = 0x000023;// 服务器通知某上线客户端的好友，其已上线了(使用IMMyFriendComModel
																					// )
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFOFFLINE = 0x000024;// 服务器通知某下线客户端的好友，其已下线了(使用IMMyFriendComModel
																					// )

		public static final int SERVER_CALL_CLIENT_LOGINEDATANOTHER = 0x000013;// 服务器通知某客户端，此帐户已在别处登录了

		//
		public static final int REQ_ADD_FRIEND = 0x000009;// 添加好友请求
		public static final int ACK_ADD_FRIEND = 0x00000A; // 添加好友请求的回复
		public static final int CLIENT_SERVER_LOGIN_HEARTBEAT = 0x000031; // 客户端向登录服务器发出的心跳包
		public static final int SERVER_LOGIN_CLIENT_HEARTBEAT = 0x000032; // 登录服务器向客户端发出的心跳包

		public static final int CLIENT_GETLATEST_USERINFO = 0x000041; // 客户端向登录服务器发起更新个人属性的请求
		public static final int SERVER_RETURN_LATEST_USERINFO = 0x000042; // 服务器段收到0x0041的请求后，反馈客户端向指定的CC号的个人属性
		public static final int CLIENT_MODIFIED_USERINFO = 0x000045; // 客户端向登录服务器发起修改个人属性的请求

		public static final int CLIENT_SENDFILE_REQUEST = 0x000051; // 客户端向登录服务器发起传送文件到另外客户端请求
		public static final int CLIENT_SENDFILE_RESPONE = 0x000052; // //客户端向登录服务器返回请求传送文件回复

		public static final int CLIENT_GETFRIENDLISTBYUID_REQUEST = 0x000061;// 客户端根据其登录UID获取其好友列表请求
		public static final int CLIENT_GETFRIENDLISTBYGROUPUID_REQUEST = 0x000062;// 客户端根据其登录UID和组UID获取其好友列表请求
		public static final int CLIENT_GETFRIENDONLINESTATUSBYUID = 0x000064;// 客户端查询某用户在线状态信息

		public static final int CLIENT_GETGROUPLISTBYUID_REQUEST = 0x000071;// 客户端根据其登录UID获取其分组列表请求

		public static final int CLIENT_GETFRIENDONLINELISTBYUID_REQUEST = 0x000063;// 客户端根据其登录UID获取其在线好友列表请求
	}

	/** 消息来源 */
	public static class MessageSource {
		public static final int CLIENT_TO_SERVER = 1; // 客户端到服务器端消息
		public static final int SERVER_TO_SERVER = 2; // 服务器端到服务器端消息
		public static final int SERVER_TO_CLIENT = 3; // 服务器端到客户端消息
		public static final int CLIENT_TO_CLIENT = 4; // 客户端到客户端消息
	}

	/** 消息种类 */
	public static class MessageCategory {
		public static final int NOTICE = 0x000001; // 通知类型
		public static final int REPLY = 0x000002; // 回复类型
		public static final int LOGIN_IN = 0x000003; // 登入类型
		public static final int LOGIN_OUT = 0x000004; // 登出类型
		public static final int CHAT_TEXT_MESSAGE = 0x000007; // 聊天消息
		public static final int GET_ONLINEUSERINFO_LIST = 0x000011;// 获取当前在线用户列表
		public static final int SERVER_CMD_CLIENT_UPDATE_ONLINEUSERINFO_LIST = 0x000012;// 服务器强制命令客户端刷新在线用户列表
		public static final int SOURCE_REQ_ADD_FRIEND = 0x000009; // 发起人的添加好友请求
		public static final int SERVER_DEST_RUQ_ADD_FRIEND = 0x00000A; // 服务器转发给被添加人的添加请求(通过TaskID进行标识),并等待回复
		public static final int DEST_SERVER_ACK_ADD_FRIEND = 0x00000B; // 被添加人给服务器的的关于添加请求的回复(通过TaskID进行标识)
		public static final int SERVER_SOURCE_ACK_ADD_FRIEND = 0x00000C; // 服务器将给加好友的发起人发送的添加结果(成功或失败)
		public static final int SERVER_DEST_ACK_ADD_FRIEND = 0x00000D; // 服务器给被添加人发送其已被添加为好友的成功信息

		public static final int SOURCE_REQ_DEL_FRIEND = 0x00000F; // 发起人的删除好友请求
		public static final int SERVER_SOURCE_ACK_DEL_FRIEND = 0x00001F; // 服务器将给删除好友的发起人发送的删除结果
		public static final int SERVER_DEST_ACK_DEL_FRIEND = 0x000002F; // 服务器给被删除人发送其已被删除好友的消息

		public static final int SERVER_CALL_CLIENT_LOGINEDATANOTHER = 0x000013;// 服务器通知某客户端，此帐户已在别处登录了

		public static final int SERVER_CALLALL_SOMEONEONLINE = 0x000021;// 服务器广播某客户端登录在线了
		public static final int SERVER_CALLALL_SOMEONEOFFLINE = 0x000022;// 服务器广播某客户端下线了
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFONLINE = 0x000023;// 服务器通知某上线客户端的好友，其已上线了(使用IMMyFriendComModel
																					// )
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFOFFLINE = 0x000024;// 服务器通知某下线客户端的好友，其已下线了(使用IMMyFriendComModel
																					// )

		public static final int CLIENT_SERVER_LOGIN_HEARTBEAT = 0x000031; // 客户端向登录服务器发出的心跳包
		public static final int SERVER_LOGIN_CLIENT_HEARTBEAT = 0x000032; // 登录服务器向客户端发出的心跳包

		public static final int CLIENT_GETLATEST_USERINFO = 0x000041; // 客户端向登录服务器发起更新个人属性的请求
		public static final int SERVER_RETURN_LATEST_USERINFO = 0x000042; // 服务器段收到0x0041的请求后，反馈客户端向指定的CC号的个人属性
		public static final int CLIENT_MODIFIED_USERINFO = 0x000045; // 客户端向登录服务器发起修改个人属性的请求

		public static final int CLIENT_SENDFILE_REQUEST = 0x000051; // 客户端向登录服务器发起传送文件到另外客户端请求
		public static final int CLIENT_SENDFILE_RESPONE = 0x000052; // //客户端向登录服务器返回请求传送文件回复

		public static final int CLIENT_GETFRIENDLISTBYUID_REQUEST = 0x000061;// 客户端根据其登录UID获取其好友列表请求
		public static final int CLIENT_GETFRIENDLISTBYGROUPUID_REQUEST = 0x000062;// 客户端根据其登录UID和组UID获取其好友列表请求
		public static final int CLIENT_GETFRIENDONLINESTATUSBYUID = 0x000064;// 客户端查询某用户在线状态信息
		public static final int CLIENT_GETGROUPLISTBYUID_REQUEST = 0x000071;// 客户端根据其登录UID获取其分组列表请求

		public static final int CLIENT_GETFRIENDONLINELISTBYUID_REQUEST = 0x000063;// 客户端根据其登录UID获取其在线好友列表请求
																					// }
	}

	public static class LastChatKeys {
		public static final String USERUID = Friends.USERUID;
		public static final String CONTENT = LastChat.CONTENT;
		public static final String SENDTIME = LastChat.SENDTIME;
		public static final String FACEID = Friends.FACEID;
		public static final String NICKNAME = Friends.NICKNAME;
		public static final String CCNO = Friends.CCNO;
		public static final String UNREAD_COUNT = LastChat.UNREAD_COUNT;
	}

	public static class FriendKeys {
		public static final String USERUID = Friends.USERUID;
		public static final String CONTENT = LastChat.CONTENT;
		public static final String SENDTIME = LastChat.SENDTIME;
		public static final String FACEID = Friends.FACEID;
		public static final String NICKNAME = Friends.NICKNAME;
		public static final String CCNO = Friends.CCNO;
		public static final String UNREAD_COUNT = LastChat.UNREAD_COUNT;
		public static final String IS_ONLINE = "isOnline";
	}

}
