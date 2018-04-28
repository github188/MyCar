package com.cnlaunch.mycar.im.model;

import com.cnlaunch.mycar.im.common.IServerReply;

public class LoginReply implements IServerReply {
	private boolean Status = false;
	private String Code = "";
	private String Description = "";
	private String Data = "";
	private UserModel LoginUserInfo = null;

	public LoginReply() {
	}

	public void setStatus(boolean status) {
		this.Status = status;
	}
	
	public boolean getStatus() {
		return this.Status;
	}

	public String getCode() {
		return this.Code;
	}

	public void setCode(String code) {
		this.Code = code;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getData() {
		return this.Data;
	}

	public void setData(String data) {
		this.Data = data;
	}

	public UserModel getUsermodel() {
		return this.LoginUserInfo;
	}

	public void setUsermodel(UserModel loginUserInfo) {
		this.LoginUserInfo = loginUserInfo;
	}

	public boolean isSucc() {
		return this.Status;
	}



}
