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
 * @author 向远茂
 * @date：2012-4-16
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
     * 创建用户表
     * 当用户注册成功后会为该用户生成以该用户CC号 + “.db”为名称的数据库，
     * 同时会生成一张用户表，里面默认生成CC号，用户昵称，手机号码，邮箱地址
     * 四个默认主要字段，其余的用户信息由用户自行添加
     * @param cc
     */
    public static void updateUser(Resources resources ,Dao<User, Integer> dao ,WSUser wsUser)
    {
    	
    	// 先清空表里的所有数据
		DeleteBuilder<User,Integer> db = dao.deleteBuilder();
		try {
			db.where().gt("id", 0);
			dao.delete(db.prepare());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 再插入
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
     * 创建用户表扩展信息表
     * @param cc
     */
    public static void updateExUser(Dao<ExUser, Integer> dao ,ExUser exUser)
    {
    	// 先清空表里的所有数据
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



