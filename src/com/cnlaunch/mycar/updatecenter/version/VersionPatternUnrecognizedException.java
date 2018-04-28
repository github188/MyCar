package com.cnlaunch.mycar.updatecenter.version;

public class VersionPatternUnrecognizedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String msg = "";
	
	public VersionPatternUnrecognizedException(String m)
	{
		this.msg = m ;
	}
}
