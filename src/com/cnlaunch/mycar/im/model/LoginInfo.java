package com.cnlaunch.mycar.im.model;

public class LoginInfo {
    private String Username = null;
    private String Password = null;
    private String AppID = "";
    private String ClientSoftwarever = "";
    private boolean IsRelogin = false;
    private String Sign = "";
    
    public LoginInfo() { }
    public LoginInfo(String username, String password)
    {
        this.Username = username;
        this.Password = password;
    }


    public LoginInfo(String appID, String clientSoftWareVer, String username, String password, String sign)
    {
    	this.Username = username;
        this.Password = password;
        this.AppID = appID;
        this.ClientSoftwarever = clientSoftWareVer;
        this.Sign = sign;
    }

    public LoginInfo(String appID, String clientSoftWareVer, String username, String password, boolean isrelogin, String sign)
    {
        this.Username = username;
        this.Password = password;
        this.AppID = appID;
        this.IsRelogin = isrelogin;
        this.ClientSoftwarever = clientSoftWareVer;
        this.Sign = sign;
    }
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		this.Username = username;
	}
	public String getPassword() {
		return this.Password;
	}
	public void setPassword(String password) {
		this.Password = password;
	}
	public String getAppID() {
		return this.AppID;
	}
	public void setAppID(String appId) {
		this.AppID = appId;
	}
	public String getClientSoftwarever() {
		return this.ClientSoftwarever;
	}
	public void setClientSoftwarever(String clientSoftwarever) {
		this.ClientSoftwarever = clientSoftwarever;
	}
	public boolean isrelogin() {
		return this.IsRelogin;
	}
	public void setIsRelogin(boolean isrelogin) {
		this.IsRelogin = isrelogin;
	}
	public String getSign() {
		return this.Sign;
	}
	public void setSign(String sign) {
		this.Sign = sign;
	}
    
    
    
}
