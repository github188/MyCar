package launch.obd2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import com.cnlaunch.dbs.SearchId;
import com.cnlaunch.mycar.R;
import android.content.Context;

public class OBD2SearchIdUtils {
	private static SearchId searchId = null;
	public static int iRet;
	public static FileOutputStream fos;
	public InputStream is;
	static {
		System.loadLibrary("SearchId");
		searchId = new SearchId();
		// iRet = searchId.ggpOpen(path);
	}

	public OBD2SearchIdUtils(Context context) {
		super();
		File dir = context.getDir("obdfile", Context.MODE_WORLD_WRITEABLE);
		String ggpFileName = context.getResources().getString(
				R.string.obdGgpFileName);
		try {
			File file = null;
			if ("obd2_en.ggp".equals(ggpFileName)) {
				is = context.getResources().openRawResource(R.raw.obd2_en);
				file = new File(dir, "obd2_en.ggp");
			}
			if ("obd2_cn.ggp".equals(ggpFileName)) {
				is = context.getResources().openRawResource(R.raw.obd2_cn);
				file = new File(dir, "obd2_cn.ggp");
			}
			if ("obd2_es.ggp".equals(ggpFileName)) {
				is = context.getResources().openRawResource(R.raw.obd2_es);
				file = new File(dir, "obd2_es.ggp");
			}

			if (!file.exists()) {
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			}
			searchId.ggpOpen(dir + "/" + file.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}

	public String getMessage(int lineId, int iFileName) {
		byte[] mes = searchId.getTextFromLibReturnByte(lineId, iFileName);
		String message = "";
		try {
			message = new String(mes, "gb2312");
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
