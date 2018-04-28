package launch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.cnlaunch.dbs.SearchId;
import android.util.Log;

public class SearchIdUtils {
	private static final String TAG = "SearchIdUtils";
    private static final boolean D = true;
    
	public final static int ID_TEXT_LIB_FILE = 1;				//菜单，对话框，动作测试按钮等使用
	public final static int ID_DATA_STREAM_BOUNDS_LIB_FILE = 2;		
	public final static int ID_DATA_STREAM_LIB_FILE = 3;		//数据流选择，显示，动作测试数据流
	public final static int ID_DATA_STREAM_HELP_LIB_FILE = 4;	//数据流帮助
	public final static int ID_DATA_STREAM_UNIT_LIB_FILE = 5;
	public final static int ID_TROUBLE_CODE_LIB_FILE = 6;       //故障码
	public final static int ID_TROUBLE_CODE_STATUS_LIB_FILE = 7;//故障码状态
	public final static int ID_TROUBLE_CODE_HELP_LIB_FILE = 8;	//故障码帮助
	public final static int ID_INFORMATION_FILE = 9;
	public final static int ID_SHOW_PROGRAM_HELP_FILE = 10;
	public final static int ID_LICENSE_FILE = 11;
	public final static int ID_PICTURE_FILE = 12;	
	public final static int ID_BMP_FILE_PATH = 13;
	private static SearchId searchId = null;
	public static int iRet;
	private static SearchIdUtils search = null;
	private static boolean m_isopen = false;
	static {
		System.loadLibrary("SearchId");
		searchId = new SearchId();
		 //iRet = searchId.ggpOpen(path);
	}
	//单例
	public static synchronized SearchIdUtils SearchIdInstance(String Pathname)
	{
		if(search == null)
		{
			if(Pathname == null)
				return null;
			search = new SearchIdUtils(Pathname);
		}
		else if(m_isopen == false)
		{
			if(Pathname != null)
			{
				if(FindFileInSDCard(Pathname) == true)
				{
					try {
						if(D) Log.i(TAG, "Path" + Pathname);
						int ret = searchId.ggpOpen(Pathname);
						if(D) Log.i(TAG,"ggpOpen = " + ret);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
					m_isopen = true;
				}
			}
		}
		return search;
	}
	public static boolean FindFileInSDCard(String Pathname)
	{
		boolean v_iRet = false;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(Pathname);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(fin == null)
		{
			Log.e("Open FIle ERROR",Pathname);
			return v_iRet;
		}
		int len = 0;
		try {
			len = fin.available();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(len <= 0)
		{
			Log.e("Open FIle ERROR",Pathname);
			return v_iRet;
		}
		try {
			fin.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		v_iRet = true; //验证正确
		return v_iRet;
	}
	
	public SearchIdUtils(String Pathname) {
		super();
		if(m_isopen == true)
			CloseFile();
		// TODO Auto-generated constructor stub
		if(FindFileInSDCard(Pathname) == false)
			return;
		try {
			if(D) Log.i(TAG, "Path" + Pathname);
			int ret = searchId.ggpOpen(Pathname);
			if(D) Log.i(TAG,"ggpOpen = " + ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		m_isopen = true;
	}
	public void CloseFile()
	{
		if(m_isopen == false)
			return;
		try
		{
			Log.i("Searchfile","CloseFile");
			if(searchId != null)
			{
				searchId.ggpClose();
				if(D) Log.i(TAG,"ggpClose");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		m_isopen = false;
	}
	public String getMessage(int lineId, int iFileName) {
		byte[] mes = searchId.getTextFromLibReturnByte(lineId, iFileName);
		String message = "";
		try {
			message = new String(mes, "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

	public byte[] getResultWithCalc(short mltPid, byte[] pDataBuffer) {

		return searchId.getResultWithCalc(mltPid, pDataBuffer);
	}

	public byte[] getTextFromLibReturnByte(int lineId, int iFileName) {
		return searchId.getTextFromLibReturnByte(lineId, iFileName);

	}

}
