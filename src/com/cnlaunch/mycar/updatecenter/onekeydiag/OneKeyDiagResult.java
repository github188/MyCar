package com.cnlaunch.mycar.updatecenter.onekeydiag;

import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.ChoiceGroup;

public class OneKeyDiagResult implements ChoiceGroup
{
	OneKeyDiag oneKeyDiag;
	CarBaseInfo carBaseInfo;
	
	public OneKeyDiag getOneKeyDiag()
	{
		return oneKeyDiag;
	}
	public void setOneKeyDiag(OneKeyDiag oneKeyDiag)
	{
		this.oneKeyDiag = oneKeyDiag;
	}
	public CarBaseInfo getCarBaseInfo()
	{
		return carBaseInfo;
	}
	public void setCarBaseInfo(CarBaseInfo carBaseInfo)
	{
		this.carBaseInfo = carBaseInfo;
	}
	@Override
	public String getId()
	{
		return oneKeyDiag.getId();
	}
	@Override
	public String getType()
	{
		return oneKeyDiag.getType();
	}
	@Override
	public String[] getItems()
	{
		return oneKeyDiag.getItems();
	}
	@Override
	public String getItemAt(int index)
	{
		return oneKeyDiag.getItemAt(index);
	}
	@Override
	public int getCount()
	{
		return oneKeyDiag.getCount();
	}
	@Override
	public int getCurrentIndex()
	{
		return oneKeyDiag.getCurrentIndex();
	}
	@Override
	public void setIndex(int index)
	{
		oneKeyDiag.setIndex(index);
	}
	@Override
	public int getFlag()
	{
	    if (oneKeyDiag != null)
	    {
	        
	        return oneKeyDiag.getFlag();
	    }
	    return 0;
	}
}
