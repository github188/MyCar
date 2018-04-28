package com.cnlaunch.mycar.updatecenter.onekeydiag;

import android.content.Context;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.ChoiceGroup;

public class OneKeyDiag implements ChoiceGroup
{
	String conditionId;
	int endFlag = 1;
	String value;
	Context context;
	int index  = -1;
	ConditonIdTable idTable;
	
	public OneKeyDiag(Context c)
	{
		this.context = c;
		idTable = new ConditonIdTable(c);
	}
	
	public String getId()
	{
		return conditionId;
	}
	
	public void setConditionId(String conditionId)
	{
		this.conditionId = conditionId;
	}
	
	public String getConditionId()
	{
		return this.conditionId;
	}
	
	public int getEndFlag()
	{
		return endFlag;
	}
	
	public void setEndFlag(int endFlag)
	{
		this.endFlag = endFlag;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public String getType()
	{
		return idTable.idToString(conditionId);
	}
	
	@Override
	public String[] getItems()
	{
		if(value==null)
	    {
			return null;
	    }
		return value.split(",");
	}
	
	@Override
	public String getItemAt(int index)
	{
		if(index == -1)
			return  this.context.getString(R.string.upc_choose);
		String[] sa = value.split(",");
		if(index >= 0 && index < sa.length)
			return sa[index];
		return null;
	}
	
	@Override
	public int getCount()
	{
		return value.split(",").length;
	}
	
	@Override
	public int getCurrentIndex()
	{
		return index;
	}
	
	@Override
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	@Override
	public int getFlag()
	{
		return endFlag;
	}
}
