package com.cnlaunch.mycar.updatecenter.onekeydiag;

import java.util.ArrayList;

import android.content.Context;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.ChoiceGroup;

public class SoftwareLanguageList implements ChoiceGroup
{
	Context context;
	String conditionId = "9";
	ConditonIdTable idTable;
	int index = -1;
	ArrayList<SoftLanguageItem> list = new ArrayList<SoftLanguageItem>(); 
	
	public SoftwareLanguageList(Context c,SoftLanguageItem[] items)
	{
		this.context = c;
		idTable = new ConditonIdTable(c);
		
		if(items == null)
		{
			return;
		}
		
		for (int i = 0; i < items.length; i++)
		{
			list.add(items[i]);
		}
	}
	
	@Override
	public String getId()
	{
		return conditionId;
	}

	@Override
	public String getType()
	{
		return idTable.idToString(conditionId);
	}

	@Override
	public String[] getItems()
	{
		String[] items = new String[list.size()];
		for(int i=0;i< list.size();i++)
		{
			items[i] = list.get(i).getLanguageName();
		}
		return items;
	}
	
	public SoftLanguageItem getLanguageItemAt(int index)
	{
		return list.get(index);
	}
	
	@Override
	public String getItemAt(int index)
	{
		if(index == -1)
			return  this.context.getString(R.string.upc_choose);
		return list.get(index).getLanguageName();
	}
	/**
	 * 获取选定的语言明细ID
	 * */
	public int getSelectedVersionDetailId()
	{
		if(index!= -1)
		{
			return list.get(index).getVersionDetailId();			
		}
		return 0;
	}
	
	@Override
	public int getCount()
	{
		return list.size();
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
		return 0;
	}
}
