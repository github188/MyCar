package com.cnlaunch.mycar.usercenter.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DatabaseFieldId;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 该类为数据库模型，主要用来显示用户详细信息，
 * 当用户注册成功后会为该用户生成以该用户CC号 + “.db”为名称的数据库，
 * 同时会生成一张用户表，里面默认生成CC号，用户昵称，手机号码，邮箱地址
 * 四个默认主要字段，其余的用户信息由用户自行添加
 * @author xiangyuanmao
 *
 */
@DatabaseTable
public class User implements Serializable {

	/**
	 * 序列号版本标识
	 */
	private static final long serialVersionUID = 1805023402967542328L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String label; // 显示用户信息的标签
	@DatabaseField
	private String value; // 显示用户信息的内容
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public User() {
		super();
	}
	
	
	
}
