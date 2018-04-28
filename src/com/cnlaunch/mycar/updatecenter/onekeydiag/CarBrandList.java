package com.cnlaunch.mycar.updatecenter.onekeydiag;

import java.util.ArrayList;

import android.content.Context;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.updatecenter.onekeydiag.ui.ChoiceGroup;
/**
 * 车系选项组列表
 * @author luxingsong
 */
public class CarBrandList implements ChoiceGroup
{
	ArrayList<CarBrand> list = new ArrayList<CarBrand>();
	int index = -1;
	Context context;
	String conditionId = "1";
	
	public CarBrandList(Context c)
	{
		this.context = c;
	}
	
	@Override
	public String getId()
	{
		return conditionId;
	}
	
	@Override
	public String getType()
	{
		return new ConditonIdTable(context).idToString(conditionId);
	}

	@Override
	public String[] getItems()
	{
		String[] items = new String[list.size()];
		int len =  list.size();
		for (int i = 0; i < len; i++) 
		{
			items[i] = list.get(i).getCarBrandName();
		}
		return items;
	}

	@Override
	public String getItemAt(int index)
	{
		if(index==-1)
		{
			return getString(R.string.upc_choose);
		}
		return list.get(index).getCarBrandName();
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
		return 1;
	}
	
	public void addItem(CarBrand cb)
	{
		list.add(cb);
	}
	
	public String getString(int resId)
	{
		return context.getString(resId);
	}
	
	public String getCarBrandIdAt(int index)
	{
		return list.get(index).getCarBrandId();
	}
	public String getCarBrandNameAt(int index)
	{
		return list.get(index).getCarBrandName();
	}
}
