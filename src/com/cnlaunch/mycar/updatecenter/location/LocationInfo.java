package com.cnlaunch.mycar.updatecenter.location;
/**
 * 经纬度信息
 * */
public class LocationInfo
{
	double latitude;
	double longitude;
	
	public LocationInfo(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	@Override
	public String toString()
	{
		return "位置信息 [经度=" + latitude + ", 纬度="
				+ longitude + "]";
	}
	
}
