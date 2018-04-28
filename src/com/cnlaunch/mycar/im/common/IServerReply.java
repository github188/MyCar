package com.cnlaunch.mycar.im.common;

public interface IServerReply {

	public boolean getStatus();

	public void setStatus(boolean status);

	public String getCode();

	public void setCode(String code);

	public String getDescription();

	public void setDescription(String description);

	public String getData();

	public void setData(String data);

}
