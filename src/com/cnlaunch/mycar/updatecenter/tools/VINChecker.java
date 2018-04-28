package com.cnlaunch.mycar.updatecenter.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
/**
 * VIN ���У��
 * VIN ��Ĺ����� 17λ������+��ĸ����ַ���
 * */
public class VINChecker
{
	private final static int VIN_VALID_LENGTH = 17;
	Context context;
	
	public interface Error
	{
		public final static int LENGTH_INVALID = -1;// ���Ȳ���ȷ
		public final static int FORMAT_INVALID = -2;// ��ʽ����ȷ
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
				if(vin.length() == VIN_VALID_LENGTH)// ������ȷ
				{
					Pattern p =Pattern.compile("[0-9|A-Z]{17}");// ����ƥ�����
					Matcher m = p.matcher(vin);
					if(m.matches())// ���Ϲ���
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
