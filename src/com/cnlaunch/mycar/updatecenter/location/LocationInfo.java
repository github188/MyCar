package com.cnlaunch.mycar.updatecenter.location;
/**
 * ��γ����Ϣ
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
		return "λ����Ϣ [����=" + latitude + ", γ��="
				+ longitude + "]";
	}
	
}
