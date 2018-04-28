package com.cnlaunch.mycar.updatecenter.onekeydiag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.cnlaunch.mycar.R;
/**
 * 条件ID 翻译表.由于有多语言要求
 * 必须与资源文件绑定
 * */
public class ConditonIdTable
{
	public final static String CONDITION_ID_CAR_BRAND 			= 	"1";	//车系        
	public final static String CONDITION_ID_VIN 	   			= 	"2";	//VIN码             
	public final static String CONDITION_ID_PRODUCING_AREA	    = 	"3";	//产地    
	public final static String CONDITION_ID_CAR_TYPE  			= 	"4";	//车型          
	public final static String CONDITION_ID_YEAR_BRAND 			= 	"5";	//年款            
	public final static String CONDITION_ID_ENGINE_TYPE 		= 	"6";	//发动机类型
	public final static String CONDITION_ID_DISPLACEMENT 		= 	"7";	//排量    
	public final static String CONDITION_ID_GEARBOX_TYPE 		= 	"8";	//波箱类型 
	public final static String CONDITION_ID_SOFT_LANGUAGE 		= 	"9";	//语言 
	
	static Map<String,String> table = new HashMap<String, String>();

	Context context;
	/**
	 * 条件ID 转义表
	 * @param c
	 */
	public ConditonIdTable(Context c)
	{
		this.context = c;
		table.put(CONDITION_ID_CAR_BRAND, getString(R.string.upc_vehicle));
		table.put(CONDITION_ID_CAR_TYPE,  getString(R.string.upc_car_type));
		table.put(CONDITION_ID_DISPLACEMENT, getString(R.string.upc_displacement));
		table.put(CONDITION_ID_ENGINE_TYPE, getString(R.string.upc_engine));
		table.put(CONDITION_ID_GEARBOX_TYPE, getString(R.string.upc_gearbox));
		table.put(CONDITION_ID_PRODUCING_AREA, getString(R.string.upc_produced_area));
		table.put(CONDITION_ID_YEAR_BRAND, getString(R.string.upc_year_brand));
		table.put(CONDITION_ID_SOFT_LANGUAGE, getString(R.string.upc_software_language));
	}
	
	private String getString(int resId)
	{
		return this.context.getResources().getString(resId);
	}
	
	public  String idToString(String condId)
	{
		return table.get(condId);
	}
}
