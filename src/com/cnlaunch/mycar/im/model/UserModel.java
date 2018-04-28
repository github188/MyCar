package com.cnlaunch.mycar.im.model;

public class UserModel {
    private String UserUID;
    private  String Username;
    private String CCNO;
    private String NickName;
    private UserInfoModel UserInfoDB;
    private int FaceType;
    private int FaceId;
    private String Ip = "127.0.0.1";
    private int TcpPort;
    private int UdpPort;
	public String getUseruid() {
		return UserUID;
	}
	public void setUseruid(String useruid) {
		this.UserUID = useruid;
	}
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		this.Username = username;
	}
	public String getCcno() {
		return CCNO;
	}
	public void setCcno(String ccno) {
		this.CCNO = ccno;
	}
	public String getNickname() {
		return NickName;
	}
	public void setNickname(String nickname) {
		this.NickName = nickname;
	}
	public UserInfoModel getUserinfodb() {
		return UserInfoDB;
	}
	public void setUserinfodb(UserInfoModel userinfodb) {
		this.UserInfoDB = userinfodb;
	}
	public int getFaceType() {
		return FaceType;
	}
	public void setFaceType(int faceType) {
		this.FaceType = faceType;
	}
	public int getFaceId() {
		return FaceId;
	}
	public void setFaceId(int faceId) {
		this.FaceId = faceId;
	}
	public String getIp() {
		return Ip;
	}
	public void setIp(String ip) {
		this.Ip = ip;
	}
	public int getTcpport() {
		return TcpPort;
	}
	public void setTcpport(int tcpport) {
		this.TcpPort = tcpport;
	}
	public int getUdpport() {
		return UdpPort;
	}
	public void setUdpport(int udpport) {
		this.UdpPort = udpport;
	}
	@Override
	public String toString() {
		return "UserModel [Useruid=" + UserUID + ", Username=" + Username
				+ ", ccno=" + CCNO + ", nickname=" + NickName + ", userinfodb="
				+ UserInfoDB + ", faceType=" + FaceType + ", faceId=" + FaceId
				+ ", ip=" + Ip + ", tcpport=" + TcpPort + ", udpport="
				+ UdpPort + "]";
	}
	
	
    
    
}
