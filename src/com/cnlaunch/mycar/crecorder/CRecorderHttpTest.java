package com.cnlaunch.mycar.crecorder;

import java.io.File;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
public class CRecorderHttpTest extends Activity implements OnClickListener{
	
	
	//final CRecorderHttpTest crd=new CRecorderHttpTest();
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.crecorder_read_data_layout);
		Button btn=(Button)findViewById(R.id.crecorder_btn);
		Button btnupload=(Button)findViewById(R.id.crecorder_upload);
		btn.setOnClickListener(this);
		btnupload.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		TextView tv=(TextView)findViewById(R.id.crecorder_tv);
		switch(v.getId()){
		case R.id.crecorder_btn:
			tv.setText(CRecorderDownload().toString());
			break;
		case R.id.crecorder_upload:
			tv.setText(CRecorderUpload().toString());
			break;
		default:
			break;
		}
	}
	/**
	 * crecorder 数据下载
	 * @return
	 * 
	 */
	public  String CRecorderDownload()
	{
		
		
		   //下载部分
	     TreeMap map = new TreeMap();
	     map.put("dataId", "141");
	     String url =  "http://192.168.16.65:8080/MyCar/android/diag/downloadDiagDataFile4Android.action" ;
	     RequestParameter requestParameter = new RequestParameter(url, null, map);
	     requestParameter.downloadDir = File.separator +"MyCar" + File.separator + "CRecorder";
	     WebServiceManager wsm = new WebServiceManager(requestParameter);
//	     wsm.executeHttpPost();
//	     return wsm.executeHttpPost().toString();
	     return null;
	     
	}
	/**
	 * crecorder数据上传
	 * @return
	 */
	public  String CRecorderUpload()
	{		
		//上传

			/**
		     * 
		     * 上传部分
		     *  请注意，上传文件时，和文件有关的参数必须有name、filename、size三个字段，而且命名方式也必须
		     *  严格按照这个来，放在requestParameter对象的fileMap属性中
		     *  普通的参数放在requestParameter对象的dataMap属性中
		     *  
		     */
		    TreeMap dataMap = new TreeMap();
		    dataMap.put("description", "x431文件");
		    dataMap.put("fileName", "itemtest.x431");
		    TreeMap fileMap = new TreeMap();  
		    fileMap.put("name", "upload");//改成自己的key
		    fileMap.put("filename", "itemtest.x431");//改成自己的key
		    fileMap.put("size", "13082");  
		    String fileUrl = Environment.getExternalStorageDirectory()+ 
		    		File.separator+ "MyCar"+File.separator+"crecorder"+File.separator+"itemtest.x431";
		       String url =  "http://192.168.16.65:8080/MyCar/android/diag/uploadDiagDataFile4Andriod.action" ;
		    RequestParameter requestParameter = new RequestParameter(url, new File(fileUrl), dataMap,fileMap);
		    WebServiceManager wsm = new WebServiceManager(requestParameter);
//		    WSBaseResult ws = wsm.executeHttpUpload();
//		    return "" + ws.responseCode;
		    return null;
	}
		
//		http://192.168.16.65:8080/MyCar/mycar/diag/downloadDiagDataFile4Android.action
//		RequestParameter request=new RequestParameter(Constants.SERVICE_BLACK_BOX, 
//				methodName, soapAction, paraMap, isSign);
//		WebServiceManager webServiceManager=new WebServiceManager(request);
//		WSBaseResult wSBaseResult = new WSBaseResult();
//		wSBaseResult=webServiceManager.execute();
//		Log.e("历史记录", "12");
//		System.out.println("wSBaseResult.responseCode为"+wSBaseResult.responseCode);
//	    System.out.println("object为"+wSBaseResult.object);.
//		SoapObject ret = (SoapObject) wSBaseResult.object;
//		SoapObject result = (SoapObject)ret.getProperty(0);
//		SoapObject fileSo = (SoapObject)result.getProperty(1);
//		String name = fileSo.hasProperty("fileName") ? ((SoapPrimitive)fileSo.getProperty("fileName")).toString() : "";
//		String content = fileSo.hasProperty("handler") ? ((SoapPrimitive) fileSo.getProperty("handler")).toString() : "";
//		  
//		FileOutputStream fos = null;
//		 byte[] buffer = new Base64().decode(content);
//		  try {
//		  
//		   //new FileUtils().writeToSDFromInput(name, content);
//		   fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + name.substring(0,name.length() - 8) + ".txt");
//		   fos.write(buffer);
//		   fos.flush();
//		   fos.close();
//		   
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				try {
//					fos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		  
//			BufferedReader bufferedReader = null;
//			try {
//				bufferedReader = new BufferedReader(new FileReader(
//						Environment.getExternalStorageDirectory() + File.separator + name.substring(0,name.length() - 8) + ".txt"));
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			String line;
//			StringBuffer sb = new StringBuffer();
//			try {
//				while ((line = bufferedReader.readLine()) != null) {
//					sb.append(line + "\n");
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//			try {
//				bufferedReader.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			return sb.toString(); 
		//Base64.decode(arg0)
		
//		if(ret!=null&&ret.getProperty(s0)!=null)
//		{
//			SoapObject so=(SoapObject)ret.getProperty(0);
//			int description = new Integer(so.getProperty("description") == null 
//					? "-1" : so.getProperty("description").toString()).intValue();
//		}
//		else
//		{
//			Log.e("upc_ws","查询不到升级信息");
//		}
//		
		  
//		  String r = null;
//		try {
//			r = new String ((new String(buffer).getBytes("utf-8")));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return r;
		}
//		StringBuffer sb=new StringBuffer();
//		StringBuilder line=null;
//		InputStream in=new InputStream() {
//			
//			@Override
//			public int read() throws IOException {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//		};
//		InputStreamReader isr=new InputStreamReader(in);

	

//	String methodName="downloadHisDiagData";
//	String soapAction=null;
//	TreeMap paraMap = new TreeMap<String,Object>();
////	paraMap.put("dataId","261");
//	boolean isSign=false;
//	public void CRecorderDownload(){
//		RequestParameter request=new RequestParameter(Constants.SERVICE_BLACK_BOX, 
//				methodName, soapAction, paraMap, isSign);
//		WebServiceManager webServiceManager=new WebServiceManager(request);
//		WSBaseResult wSBaseResult = new WSBaseResult();
//		wSBaseResult=webServiceManager.execute();
//		Log.e("历史记录", "12");
//		System.out.println("wSBaseResult.responseCode为"+wSBaseResult.responseCode);
//	    System.out.println("object为"+wSBaseResult.object);
//	}
//	private URL url=null;//私有的url对象
//	StringBuffer sb=new StringBuffer();
//	BufferedReader buffer=null;
//	String line=null;
//	public String CRecoderHttpDownLoad(String urlStr){
//		try{
//		url=new URL(urlStr);
//		HttpURLConnection urlconn=(HttpURLConnection)url.openConnection();
//		buffer=new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
//		while((line=buffer.readLine()) != null){
//			sb.append(line);
//		}
//	
//		}
//		catch(MalformedURLException e){
//			e.printStackTrace();
//		}catch(IOException e){
//			e.printStackTrace();
//		}finally {
//			try {
//				buffer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		Log.d("System.out", sb.toString());
//		return sb.toString();
//		
//	}

