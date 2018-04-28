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
	 * ���Ի���IP
	 */
	public static final String SERVER_SOCKET_IP_FOR_TEST = "192.168.19.62";
	/**
	 * ���ϻ������������ڶ�̬����IP
	 */
	public static final String SERVER_DOMAIN_NAME = "tcpconn1.dbscar.com";
	/**
	 * ������socket�˿�
	 */
	public static final int SERVER_SOCKET_PORT = 2488;

	public static final String SYS_LOGIN_SERVER = "SERVER_LOGIN_00001";

	public static final String IM_SHARED_PREFERENCES_NAME = "IM_SHARED_PREFERENCES";

	public static final String CONFIG_SOUND = "config_sound";

	public static final String CONFIG_DB_NAME_PREFIX = "im_db_";
	public static final String CONFIG_DB_NAME_ANONYMOUS = "im_db_anonymous";

	public static final String DIR_NAME_RECEIVED = "fileReceived";

	public static final String BROADCAST_NEW_TEXT_CHAT_MESSAGE = "com.cnlaunch.mycar.broadcast_new_text_chat_message";

	/** ��Ϣ������ */
	public static class Reciver {
		public static final int LOGIN = 0x000003;
		public static final int LOGINOUT = 0x000004;
		public static final int CHAT_TEXT_MESSAGE = 0x000007;

		public static final int SOURCE_REQ_ADD_FRIEND = 0x000009; // �����˵���Ӻ�������
		public static final int SERVER_DEST_RUQ_ADD_FRIEND = 0x00000A; // ������ת����������˵��������(ͨ��TaskID���б�ʶ),���ȴ��ظ�
		public static final int DEST_SERVER_ACK_ADD_FRIEND = 0x00000B; // ������˸��������ĵĹ����������Ļظ�(ͨ��TaskID���б�ʶ)
		public static final int SERVER_SOURCE_ACK_ADD_FRIEND = 0x00000C; // �����������Ӻ��ѵķ����˷��͵���ӽ��(�ɹ���ʧ��)
		public static final int SERVER_DEST_ACK_ADD_FRIEND = 0x00000D; // ��������������˷������ѱ����Ϊ���ѵĳɹ���Ϣ

		public static final int SOURCE_REQ_DEL_FRIEND = 0x00000F; // �����˵�ɾ����������
		public static final int SERVER_SOURCE_ACK_DEL_FRIEND = 0x00001F; // ����������ɾ�����ѵķ����˷��͵�ɾ�����
		public static final int SERVER_DEST_ACK_DEL_FRIEND = 0x000002F; // ����������ɾ���˷������ѱ�ɾ�����ѵ���Ϣ

		public static final int GET_ONLINEUSERINFO_LIST = 0x000011;

		public static final int SERVER_CMD_CLIENT_UPDATE_ONLINEUSERINFO_LIST = 0x000012;// ������ǿ������ͻ���ˢ�������û��б�
		public static final int SERVER_CALLALL_SOMEONEONLINE = 0x000021;// �������㲥ĳ�ͻ��˵�¼������
		public static final int SERVER_CALLALL_SOMEONEOFFLINE = 0x000022;// �������㲥ĳ�ͻ���������
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFONLINE = 0x000023;// ������֪ͨĳ���߿ͻ��˵ĺ��ѣ�����������(ʹ��IMMyFriendComModel
																					// )
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFOFFLINE = 0x000024;// ������֪ͨĳ���߿ͻ��˵ĺ��ѣ�����������(ʹ��IMMyFriendComModel
																					// )

		public static final int SERVER_CALL_CLIENT_LOGINEDATANOTHER = 0x000013;// ������֪ͨĳ�ͻ��ˣ����ʻ����ڱ𴦵�¼��

		//
		public static final int REQ_ADD_FRIEND = 0x000009;// ��Ӻ�������
		public static final int ACK_ADD_FRIEND = 0x00000A; // ��Ӻ�������Ļظ�
		public static final int CLIENT_SERVER_LOGIN_HEARTBEAT = 0x000031; // �ͻ������¼������������������
		public static final int SERVER_LOGIN_CLIENT_HEARTBEAT = 0x000032; // ��¼��������ͻ��˷�����������

		public static final int CLIENT_GETLATEST_USERINFO = 0x000041; // �ͻ������¼������������¸������Ե�����
		public static final int SERVER_RETURN_LATEST_USERINFO = 0x000042; // ���������յ�0x0041������󣬷����ͻ�����ָ����CC�ŵĸ�������
		public static final int CLIENT_MODIFIED_USERINFO = 0x000045; // �ͻ������¼�����������޸ĸ������Ե�����

		public static final int CLIENT_SENDFILE_REQUEST = 0x000051; // �ͻ������¼�������������ļ�������ͻ�������
		public static final int CLIENT_SENDFILE_RESPONE = 0x000052; // //�ͻ������¼�����������������ļ��ظ�

		public static final int CLIENT_GETFRIENDLISTBYUID_REQUEST = 0x000061;// �ͻ��˸������¼UID��ȡ������б�����
		public static final int CLIENT_GETFRIENDLISTBYGROUPUID_REQUEST = 0x000062;// �ͻ��˸������¼UID����UID��ȡ������б�����
		public static final int CLIENT_GETFRIENDONLINESTATUSBYUID = 0x000064;// �ͻ��˲�ѯĳ�û�����״̬��Ϣ

		public static final int CLIENT_GETGROUPLISTBYUID_REQUEST = 0x000071;// �ͻ��˸������¼UID��ȡ������б�����

		public static final int CLIENT_GETFRIENDONLINELISTBYUID_REQUEST = 0x000063;// �ͻ��˸������¼UID��ȡ�����ߺ����б�����
	}

	/** ��Ϣ��Դ */
	public static class MessageSource {
		public static final int CLIENT_TO_SERVER = 1; // �ͻ��˵�����������Ϣ
		public static final int SERVER_TO_SERVER = 2; // �������˵�����������Ϣ
		public static final int SERVER_TO_CLIENT = 3; // �������˵��ͻ�����Ϣ
		public static final int CLIENT_TO_CLIENT = 4; // �ͻ��˵��ͻ�����Ϣ
	}

	/** ��Ϣ���� */
	public static class MessageCategory {
		public static final int NOTICE = 0x000001; // ֪ͨ����
		public static final int REPLY = 0x000002; // �ظ�����
		public static final int LOGIN_IN = 0x000003; // ��������
		public static final int LOGIN_OUT = 0x000004; // �ǳ�����
		public static final int CHAT_TEXT_MESSAGE = 0x000007; // ������Ϣ
		public static final int GET_ONLINEUSERINFO_LIST = 0x000011;// ��ȡ��ǰ�����û��б�
		public static final int SERVER_CMD_CLIENT_UPDATE_ONLINEUSERINFO_LIST = 0x000012;// ������ǿ������ͻ���ˢ�������û��б�
		public static final int SOURCE_REQ_ADD_FRIEND = 0x000009; // �����˵���Ӻ�������
		public static final int SERVER_DEST_RUQ_ADD_FRIEND = 0x00000A; // ������ת����������˵��������(ͨ��TaskID���б�ʶ),���ȴ��ظ�
		public static final int DEST_SERVER_ACK_ADD_FRIEND = 0x00000B; // ������˸��������ĵĹ����������Ļظ�(ͨ��TaskID���б�ʶ)
		public static final int SERVER_SOURCE_ACK_ADD_FRIEND = 0x00000C; // �����������Ӻ��ѵķ����˷��͵���ӽ��(�ɹ���ʧ��)
		public static final int SERVER_DEST_ACK_ADD_FRIEND = 0x00000D; // ��������������˷������ѱ����Ϊ���ѵĳɹ���Ϣ

		public static final int SOURCE_REQ_DEL_FRIEND = 0x00000F; // �����˵�ɾ����������
		public static final int SERVER_SOURCE_ACK_DEL_FRIEND = 0x00001F; // ����������ɾ�����ѵķ����˷��͵�ɾ�����
		public static final int SERVER_DEST_ACK_DEL_FRIEND = 0x000002F; // ����������ɾ���˷������ѱ�ɾ�����ѵ���Ϣ

		public static final int SERVER_CALL_CLIENT_LOGINEDATANOTHER = 0x000013;// ������֪ͨĳ�ͻ��ˣ����ʻ����ڱ𴦵�¼��

		public static final int SERVER_CALLALL_SOMEONEONLINE = 0x000021;// �������㲥ĳ�ͻ��˵�¼������
		public static final int SERVER_CALLALL_SOMEONEOFFLINE = 0x000022;// �������㲥ĳ�ͻ���������
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFONLINE = 0x000023;// ������֪ͨĳ���߿ͻ��˵ĺ��ѣ�����������(ʹ��IMMyFriendComModel
																					// )
		public static final int SERVER_CALLSOMEONEFRIEND_HIMSELFOFFLINE = 0x000024;// ������֪ͨĳ���߿ͻ��˵ĺ��ѣ�����������(ʹ��IMMyFriendComModel
																					// )

		public static final int CLIENT_SERVER_LOGIN_HEARTBEAT = 0x000031; // �ͻ������¼������������������
		public static final int SERVER_LOGIN_CLIENT_HEARTBEAT = 0x000032; // ��¼��������ͻ��˷�����������

		public static final int CLIENT_GETLATEST_USERINFO = 0x000041; // �ͻ������¼������������¸������Ե�����
		public static final int SERVER_RETURN_LATEST_USERINFO = 0x000042; // ���������յ�0x0041������󣬷����ͻ�����ָ����CC�ŵĸ�������
		public static final int CLIENT_MODIFIED_USERINFO = 0x000045; // �ͻ������¼�����������޸ĸ������Ե�����

		public static final int CLIENT_SENDFILE_REQUEST = 0x000051; // �ͻ������¼�������������ļ�������ͻ�������
		public static final int CLIENT_SENDFILE_RESPONE = 0x000052; // //�ͻ������¼�����������������ļ��ظ�

		public static final int CLIENT_GETFRIENDLISTBYUID_REQUEST = 0x000061;// �ͻ��˸������¼UID��ȡ������б�����
		public static final int CLIENT_GETFRIENDLISTBYGROUPUID_REQUEST = 0x000062;// �ͻ��˸������¼UID����UID��ȡ������б�����
		public static final int CLIENT_GETFRIENDONLINESTATUSBYUID = 0x000064;// �ͻ��˲�ѯĳ�û�����״̬��Ϣ
		public static final int CLIENT_GETGROUPLISTBYUID_REQUEST = 0x000071;// �ͻ��˸������¼UID��ȡ������б�����

		public static final int CLIENT_GETFRIENDONLINELISTBYUID_REQUEST = 0x000063;// �ͻ��˸������¼UID��ȡ�����ߺ����б�����
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
