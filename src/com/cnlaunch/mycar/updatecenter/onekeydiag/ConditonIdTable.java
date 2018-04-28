package com.cnlaunch.mycar.updatecenter.onekeydiag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.cnlaunch.mycar.R;
/**
 * ����ID �����.�����ж�����Ҫ��
 * ��������Դ�ļ���
 * */
public class ConditonIdTable
{
	public final static String CONDITION_ID_CAR_BRAND 			= 	"1";	//��ϵ        
	public final static String CONDITION_ID_VIN 	   			= 	"2";	//VIN��             
	public final static String CONDITION_ID_PRODUCING_AREA	    = 	"3";	//����    
	public final static String CONDITION_ID_CAR_TYPE  			= 	"4";	//����          
	public final static String CONDITION_ID_YEAR_BRAND 			= 	"5";	//���            
	public final static String CONDITION_ID_ENGINE_TYPE 		= 	"6";	//����������
	public final static String CONDITION_ID_DISPLACEMENT 		= 	"7";	//����    
	public final static String CONDITION_ID_GEARBOX_TYPE 		= 	"8";	//�������� 
	public final static String CONDITION_ID_SOFT_LANGUAGE 		= 	"9";	//���� 
	
	static Map<String,String> table = new HashMap<String, String>();

	Context context;
	/**
	 * ����ID ת���
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
