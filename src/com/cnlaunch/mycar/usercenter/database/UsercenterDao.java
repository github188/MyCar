package com.cnlaunch.mycar.usercenter.database;

import java.sql.SQLException;

import android.content.res.Resources;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.updatecenter.Device;
import com.cnlaunch.mycar.usercenter.model.WSUser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * @description 
 * @author ��Զï
 * @date��2012-4-16
 */
public class UsercenterDao {
	 
	public static void createOrUpdateDevice(Dao<Device, Integer> dao ,Device device)
	{
		try {
			dao.createOrUpdate(device);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * �����û���
     * ���û�ע��ɹ����Ϊ���û������Ը��û�CC�� + ��.db��Ϊ���Ƶ����ݿ⣬
     * ͬʱ������һ���û�������Ĭ������CC�ţ��û��ǳƣ��ֻ����룬�����ַ
     * �ĸ�Ĭ����Ҫ�ֶΣ�������û���Ϣ���û��������
     * @param cc
     */
    public static void updateUser(Resources resources ,Dao<User, Integer> dao ,WSUser wsUser)
    {
    	
    	// ����ձ������������
		DeleteBuilder<User,Integer> db = dao.deleteBuilder();
		try {
			db.where().gt("id", 0);
			dao.delete(db.prepare());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// �ٲ���
    	User userCC = new User();
    	User userName = new User();
    	User userMobile = new User();
    	User userEmail = new User();
    	User userNickname = new User();
    	//User isBindEmail = new User();
    	//User isBindMobile = new User();
    	userCC.setLabel(resources.getString(R.string.usercenter_cc_));
    	userCC.setValue(wsUser.cc);
    	userName.setLabel(resources.getString(R.string.usercenter_username_));
    	userName.setValue(wsUser.userName);
//    	isBindEmail.setLabel(resources.getString(R.string.usercenter_activate));
//    	isBindMobile.setValue(wsUser.isBindMobile);
//    	isBindMobile.setLabel(resources.getString(R.string.usercenter_bind));
//    	isBindEmail.setValue(wsUser.isBindEmail);
    	userNickname.setLabel(resources.getString(R.string.usercenter_nickname_));
    	userNickname.setValue(wsUser.nickname);
    	userMobile.setLabel(resources.getString(R.string.usercenter_mobile_));
    	userMobile.setValue(wsUser.mobile);
    	userEmail.setLabel(resources.getString(R.string.usercenter_email_));
    	userEmail.setValue(wsUser.email);

    	try {
			dao.createOrUpdate(userCC);
			dao.createOrUpdate(userNickname);
			dao.createOrUpdate(userName);
			dao.createOrUpdate(userEmail);
			dao.createOrUpdate(userMobile);
//			dao.createOrUpdate(isBindMobile);
//			dao.createOrUpdate(isBindEmail);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * �����û�����չ��Ϣ��
     * @param cc
     */
    public static void updateExUser(Dao<ExUser, Integer> dao ,ExUser exUser)
    {
    	// ����ձ������������
		try {
			if(dao.queryForAll().size() > 0)
			{
				DeleteBuilder<ExUser,Integer> db = dao.deleteBuilder();
				db.where().ge("id", 0);
				dao.delete(db.prepare());
			}
			dao.createOrUpdate(exUser);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}



