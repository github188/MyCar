package com.cnlaunch.mycar.usercenter.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DatabaseFieldId;
import com.j256.ormlite.table.DatabaseTable;

/**
 * ����Ϊ���ݿ�ģ�ͣ���Ҫ������ʾ�û���ϸ��Ϣ��
 * ���û�ע��ɹ����Ϊ���û������Ը��û�CC�� + ��.db��Ϊ���Ƶ����ݿ⣬
 * ͬʱ������һ���û�������Ĭ������CC�ţ��û��ǳƣ��ֻ����룬�����ַ
 * �ĸ�Ĭ����Ҫ�ֶΣ�������û���Ϣ���û��������
 * @author xiangyuanmao
 *
 */
@DatabaseTable
public class User implements Serializable {

	/**
	 * ���кŰ汾��ʶ
	 */
	private static final long serialVersionUID = 1805023402967542328L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String label; // ��ʾ�û���Ϣ�ı�ǩ
	@DatabaseField
	private String value; // ��ʾ�û���Ϣ������
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
