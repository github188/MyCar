package com.cnlaunch.mycar.updatecenter.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
/**
 * VIN 码的校验
 * VIN 码的规则是 17位的数字+字母混合字符串
 * */
public class VINChecker
{
	private final static int VIN_VALID_LENGTH = 17;
	Context context;
	
	public interface Error
	{
		public final static int LENGTH_INVALID = -1;// 长度不正确
		public final static int FORMAT_INVALID = -2;// 格式不正确
	}
	
	public interface Listener
	{
		public void onCheck(boolean isValid);
		public void onRecheck(String trim,int error);
	}
	
	Listener listener;
	
	public VINChecker(Context c,Listener l)
	{
		this.listener = l;
		this.context = c;
	}
	
	public void doCheck(final String vin)
	{
		new Thread()
		{
			public void run()
			{
				if(context==null || listener==null)
					return;
				if(vin.length() == VIN_VALID_LENGTH)// 长度正确
				{
					Pattern p =Pattern.compile("[0-9|A-Z]{17}");// 正则匹配规则
					Matcher m = p.matcher(vin);
					if(m.matches())// 符合规则
					{
						listener.onCheck(true);						
					}
					else
					{
						listener.onCheck(false);
					}
				}
				else
				{
					String trim  = "";
					if(vin.length() > VIN_VALID_LENGTH)
					{
						trim = vin.substring(0, VIN_VALID_LENGTH - 1);
						listener.onRecheck(trim,Error.LENGTH_INVALID);
					}else
					{
						listener.onRecheck(vin,Error.LENGTH_INVALID);
					}
				}
			}
		}.start();
	}
}
