package com.cnlaunch.mycar.common.webservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.common.utils.FileUtils;
import com.cnlaunch.mycar.common.utils.md5.Md5Helper;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

/**
 * WebService������
 * ˵�����ṩ�ֻ���WebService���õĻ���������Ŀǰ֧�ֶ����б��������ļ�����ͨ�����ϴ������������
 * �򷵻�ֵ�в�ȷ���ԣ������׳��󣬹ʷ���ֵ�ɸ���ҵ��ģ�������ֹ�����
 * @author xiangyuanmao
 *
 */
public class WebServiceManager {

	// ����log��Ϣtarget
	private static final String TAG = "WebServiceManager";
	private static final boolean D = true;
	
	// �����������
	private RequestParameter requestParameter;
	
	// �����ģ�ĳ������WebService��ҵ��ģ��������ģ�
	private Context mContext; 
	
	// SoapObject�������ڷ�װ�������
	private SoapObject soapObject;

	public WebServiceManager(RequestParameter requestParameter)
	{
		this.requestParameter = requestParameter;
	}
	
	
	public WebServiceManager(Context context, RequestParameter requestParameter)
	{
		this.requestParameter = requestParameter;
		mContext = context;
		// �����ж��û��Ƿ��¼
		if (MyCarActivity.isLogin)
		{
			Log.i(TAG, "�ѵ�¼ ");
		}
		else // δ��¼������Զ���¼
		{
			Log.i(TAG, "δ��¼ ");
			login();
		}
	}
	
	//TODO
	private String getMd5Pawword(String password)
	{
		MessageDigest messageDigest = null;
        try {
        	messageDigest  = MessageDigest.getInstance("MD5");
	        if (StringUtils.isNotEmpty(password))
	        {
	
				messageDigest.update((password).getBytes("UTF-8"));
	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return Md5Helper.byteArrayToHexString(messageDigest.digest());
	}
	
	/**
	 * �������ģ�����WebService�ӿ�ʱû�е�¼������������¼
	 * 
	 */
	private void login()
	{


		// ȡ��ȫ�ֵ��˺š�����
		String account = MyCarActivity.account;
		String password = MyCarActivity.password;
		// �����ȡʧ�ܣ����ǽ��ֶ���¼
		if (account != null && password != null)
		{

			// ʵ����SoapObject����
			this.soapObject = new SoapObject(Constants.WEBSERVICE_NAME_SPACE,  Constants.SERVICE_LOGIN_METHOD_NAME);
			
			// ���ò���
			soapObject.addProperty("loginKey", account);
			soapObject.addProperty("mobileAppVersion", Constants.MYCAR_VERSION);
			soapObject.addProperty("password", getMd5Pawword(password));
		
			
			// ����ŷ����
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

			// TODO
			envelope.bodyOut = soapObject;
			
			// �����Ƿ���.NetΪ��
			envelope.dotNet = false;	
			
			envelope.setOutputSoapObject(soapObject);
			
			// ʵ����������󣬴˴�ʹ��HttpTransportSE�����࣬�������ó�ʱ�쳣Ϊ15��
			MyCarHttpTransportSE ht = new MyCarHttpTransportSE(Constants.SERVICE_LOGIN_URL_USERCENTER);
			// TODO
			ht.debug = true;
			try 
			{
				ht.call(null, envelope, null);
			
				SoapObject result = (SoapObject)envelope.bodyIn;
	
				
				// �õ���¼״̬
				if (result != null && result.getProperty(0) != null)
				{
					SoapObject so = (SoapObject)result.getProperty(0);
					
					// �Ƿ�ɹ�
					int isSuccess = new Integer(so.hasProperty("code") 
							? so.getProperty("code").toString(): "-1").intValue();
					
					Log.i(TAG, "����˷��صĵ�¼��Ϣ��" + (so.hasProperty("message") ? so.getProperty("message").toString() : ""));
					
					if (isSuccess == UsercenterConstants.RESULT_SUCCESS)
					{
						Log.i(TAG, "��¼�ɹ���");
						
						MyCarActivity.cc = so.hasProperty("cc")
								?  so.getProperty("cc").toString() : ""; // CC����
						this.requestParameter.cc = MyCarActivity.cc;
						// ��ʱ��Ҫ�������������ļ�
						MyCarConfig.currentCCToDbName = MyCarActivity.cc + ".db";
						MyCarActivity.isLogin = true; // �Ƿ��¼�� ��
						
						MyCarActivity.token = so.hasProperty("token") ?  so.getProperty("token").toString() : ""; // ����
						Log.i(TAG, "����˷��ص�Token��" + MyCarActivity.token);
						MyCarActivity.csInterval = new Date().getTime()
								- (so.hasProperty("serverSystemTime")
								? new Long(so.getProperty("serverSystemTime").toString()).longValue() : 0L); // �ͻ��˺ͷ���˵�ʱ����
					}
					else
					{
						// ������¼����
						manualLogin();
					    if (D) Log.d(TAG, "��¼�쳣");
						throw new Exception(
								"��¼�쳣");
					
					}
				}
				else
				{
					manualLogin();
				    if (D) Log.d(TAG, "��¼�쳣");
								throw new Exception(
										"��¼�쳣");
								
				}
			}
			catch (Exception e)
			{
				manualLogin();
				e.printStackTrace();
			    if (D) Log.d(TAG, "��¼�쳣");
			}	
		}
		else // ���ȡ�����˺š�������ֶ���¼
		{
			// ������¼����
			manualLogin();
		}
	}
	
	private void manualLogin()
	{
//		Intent intent = new Intent(mContext, LoginActivity.class);
//		((Activity)mContext).setResult(Activity.RESULT_OK, intent);
//		((Activity)mContext).startActivityForResult(intent, UsercenterConstants.REQUEST_CODE_WEBSERVICE);
		
	}

	/**
	 * ִ�м���
	 * @author luxingsong
	 */
	public interface OnWebSeriveExecuteListener
	{
		public final static int ERROR_IO = 1;
		public final static int ERROR_XML_PARSING = 2;
		public void onStart();
		public void onResult(WSBaseResult result);
		public void onExceptions(int error_code,String detail);
	}
	
	private OnWebSeriveExecuteListener executeListener;
	
	public void setExecuteListener(OnWebSeriveExecuteListener el)
	{
		this.executeListener  = el;
	}
	
	private final void notifyExecuteStart()
	{
		if (executeListener!=null)
		{
			executeListener.onStart();
		}
	}
	
	private final void notifyExecuteGetResult(WSBaseResult result)
	{
		if (executeListener!=null)
		{
			executeListener.onResult(result);
		}
	}
	
	private final void notifyExecuteExeption(int err_code,String detail)
	{
		if (executeListener!=null)
		{
			executeListener.onExceptions(err_code, detail);
		}
	}
	
	/**
	 * ִ��WebService����
	 * @return
	 * @author xiangyuanmao
	 */
	public WSBaseResult execute()
	{
		notifyExecuteStart();
		// ��������ֵ����
		WSBaseResult wSBaseResult = new WSBaseResult();
		
		// ʵ����SoapObject����
		this.soapObject = new SoapObject(requestParameter.nameSpace, requestParameter.methodName);
	
		// ���ò���
		if (requestParameter.paraMap != null && !requestParameter.paraMap.isEmpty())
		{
			Set paraKeySet = requestParameter.paraMap.keySet();
	
			for (Object paraName : paraKeySet) 
			{

				// ����������Ƶĺ�׺�ǡ�SO����˵����SoapObject��������ʹ��addSoapObject()������Ӳ���
				if (paraName.toString().endsWith("SO"))//TODO
				{
					soapObject.addSoapObject((SoapObject)requestParameter.paraMap.get(paraName));
				}
				else // ����������ͨ�����������ʹ��addProperty()������Ӳ���
				{
					
					soapObject.addProperty((String)paraName, requestParameter.paraMap.get(paraName));					
				}	
			}
		}
		StringBuilder sb = new StringBuilder();
		getSignString( sb , soapObject);
		StringBuilder tempSb = new StringBuilder();
		tempSb.append(sb);
		Log.i(TAG, "sb---->�� " + sb);
		// ����ŷ����
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		// ��װ����ͷ��Ϣ
		if (requestParameter.isSign)
		{			
			if (MyCarActivity.isLogin)
			{
				requestParameter.sign = requestParameter.getSign(tempSb);
				envelope.headerOut = soapHeader(requestParameter.cc, requestParameter.sign);
			}
			else
			{
				wSBaseResult.responseCode = -1;//TODO
				return wSBaseResult ;
			}
		}
		envelope.bodyOut = soapObject; //TODO
		
		// ����Ԥ������Ĵ�����.Netƽ̨�ģ��˴�Ҫ���������
		if (requestParameter.wsUrl.equals(Constants.SERVICE_WEATHER_URL))
		{
			envelope.dotNet = true;
		}
		else
		{
			envelope.dotNet = false;	
		} 
		
		envelope.setOutputSoapObject(soapObject);
		
		// ʵ����������󣬴˴�ʹ��HttpTransportSE�����࣬�������ó�ʱ�쳣Ϊ15��
		MyCarHttpTransportSE ht = new MyCarHttpTransportSE(requestParameter.wsUrl);
		
		boolean errorHappened = false;
		
		ht.debug = true; //TODO
		try {
			ht.call(requestParameter.action, envelope, null);

			wSBaseResult.responseCode = 0; //TODO
			// ȡ����Ӧͷ����Ϣ
			Element[] headerIn = envelope.headerIn;
			if (headerIn != null && headerIn.length > 0) 
			{
				

				// ��ȡ��Ӧͷ�е�code�ֶΣ��ж��Ƿ�ɹ�
				for (Element element : headerIn)
				{
					// ȡ����Ӧͷ��code
				    String responseHeaderCode = ((Element)element.getChild(0)).getChild(0).toString();
				    
					Log.i(TAG, "�������˷�����Ϣ��" + ((Element)element.getChild(1)).getChild(0).toString());
				   
					// �����Ӧͷ��Ϣ��-1��˵��tokenʧЧ����Ҫ���µ�½
				    if (responseHeaderCode.equals("-1") && requestParameter.count++ < 1)
				    {
				    	login();
						// ���¼���ǩ����
						Log.i(TAG, "���¼���ǩ����֮ǰ��ǩ���� " + this.requestParameter.sign);
						if (this.requestParameter.sign != null)
						{
						    sb = new StringBuilder();
					        getSignString( sb , soapObject);
							tempSb = new StringBuilder();
							tempSb.append(sb);
							this.requestParameter.sign = requestParameter.getSign(tempSb);//�˴��滻�µ�ǩ������ this.requestParameter.generateDigitalSignature(this.requestParameter.paraMap);
							Log.i(TAG, "���¼���ǩ����֮���ǩ���� " + this.requestParameter.sign);
						}
						if (requestParameter.isSign)
						{			
							envelope.headerOut = soapHeader(requestParameter.cc, requestParameter.sign);
						}
						ht.call(requestParameter.action, envelope, null);
				    }
				}
			}

			
		} catch (IOException e) { // TODO
			Log.d(TAG, "����IO�쳣��Message---> " + e.getMessage());
			Log.d(TAG, "����IO�쳣��Cause by---> " + e.getCause());
			wSBaseResult.responseCode = 2;// ��㶨���һ���쳣����
			e.printStackTrace();
			errorHappened = true;
			notifyExecuteExeption(OnWebSeriveExecuteListener.ERROR_IO, "����IO�쳣��Message---> "+e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d(TAG, "����Xml�ļ������쳣��Message---> " + e.getMessage());
			Log.d(TAG, "����Xml�ļ������쳣��Cause by---> " + e.getCause());
			wSBaseResult.responseCode = 3;// ��㶨���һ���쳣����
			e.printStackTrace();
			errorHappened = true;
			notifyExecuteExeption(OnWebSeriveExecuteListener.ERROR_XML_PARSING,"����Xml�ļ������쳣��"+e.getMessage());
		}
		
		if (errorHappened)
		{
			return null;
		}
		
		wSBaseResult.object = envelope.bodyIn; 
		notifyExecuteGetResult(wSBaseResult);
		return wSBaseResult;
	}
	
	/**
	 * 
	 * @return �������ͷ��Ϣ
	 * @author xiangyuanmao
	 */
	private Element[] soapHeader(String cc, String sign)
    {
	    // ����Authenticate�������ͽڵ㣬�����ռ�Ϊȫ�� http://www.x431.com 
	    // AuthenticateΪ����������˲�Ϊ��
	    Element Author = new Element().createElement(Constants.WEBSERVICE_NAME_SPACE,"authenticate");
	    
	    // ����Authenticate�ӽڵ�Sign(ǩ��)�������ٸ���namespace�����Դ˴���һ������Ϊ������
	    Element Sign = new Element().createElement("", "sign");
	
	    // ���ýڵ㣬Ȼ��ֵ���ýڵ�,
	    // Ϊ��ôSign�����Ѿ���STRING��NEW����Ϊ���Ǹ���XML��ǰ�ڵ��TYPEΪSTRING
	    Sign.addChild(Node.TEXT, sign);

	    Log.i(TAG, "sign-->" + sign);
	    // ����Authenticate�ӽڵ�cc
	    Element CC = new Element().createElement("", "cc");
	    CC.addChild(Node.TEXT, cc);
	
	    // ����Authenticate�ӽڵ������ʱ��serviceTime
	    Element serviceTime = new Element().createElement("", "serviceTime");
	    serviceTime.addChild(Node.TEXT, (new Long (new Date().getTime() + MyCarActivity.csInterval)).toString());
	    
	    // Ƕ�׽�Authenticate�����л�Authenticate
	    Author.addChild(Node.ELEMENT, Sign);
	    Author.addChild(Node.ELEMENT, CC);
	    //Author.addChild(Node.ELEMENT, serviceTime);
	
	    // ��װ��SoapHeader()��Ҫ��Element[] ����
	    Element[] Header = new Element[1];
	
	    // ����Authenticate�ڵ�
	    Header[0]=Author;
	
	    // ���ط�װ�����������
	    return Header;
    }
	
	/**
	 * ���ö������ݲ�������Ҫ��Զ��������wu
	 * @param treeMap ����Map
	 * @param list ��������
	 * @param nameSpace ���ƿռ�
	 * @param listName ����˶���ķ����еĶ������ݲ���������
	 * @author xiangyuanmao
	 */
    public static void setObjectArrayParameter(TreeMap treeMap, List list, String nameSpace, String listName)
    {
    	int count = 0; // ����һ��������
    	
    	// �������ж������ݵĶ���ͨ������ĵ���Ӧ�����ԣ���������getXXX������ȡ����ֵ
    	for (Object object : list) 
    	{
    		// ���������Ҫ��װ��SoapObject����
			SoapObject so = new SoapObject(nameSpace, listName);
			
			// �õ����������
			Class classType = object.getClass();
			
			// �õ�object�������б�
			Field[] fields = classType.getDeclaredFields();
			
			// ��������������ֶ�
			for (Field field : fields) 
			{
				// �ֶ�����
				String fieldName = field.getName();
				
				// Ϊ��ƴװgetXX��������Ҫȡ���ֶ����Ƶĵ�һ����ĸ��������ת��Ϊ��д
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				
				// ƴװ�ֶε�getXXX����
				String getMethodName = "get" + firstLetter + fieldName.substring(1);
				try {
					
					// ��ø��ֶζ�Ӧ��getXXX��������
					Method getMethod = classType.getMethod(getMethodName, new Class[]{});
					
					// �õ�getXXX������ֵ����ʵ���Ǹ������ֶε�ֵ
					Object value = getMethod.invoke(object, new Object[]{});
					
					// ��ӵ�SoapOjbect������
					so.addProperty(fieldName, value);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// �ڲ���Map�����øշ�װ�õ�SoapObject����
			count++;
			treeMap.put(count + "SO", so);
		}
    }	
  
    public interface HttpDownloadListener
    {
    	public void onHttpDownloadStart(String url,Object params);
    	public void onHttpDownloadProgress(int percent,int restHours,int restMinutes,int restSeconds);
    	public void onHttpDownloadFinished(File target,Object extra);
    	public void onHttpDownloadException(Object detail);
    }
    
    /**
     * ����HTTP��POST����
     * @return
     */
    // TODO
    public WSBaseResult executeHttpPost(HttpDownloadListener listener)
    {
		WSBaseResult wsBaseResult = new WSBaseResult();
	    String end = "\r\n";   
	    String twoHyphens = "--";               //  �������ַ�   
	    String boundary = "******";             //  �ֽ�����ַ���   
	    try  
	    {   
	        URL url = new URL(requestParameter.action); 
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();   
	        //  Ҫ��ʹ��InputStream��OutputStream������ʹ���������д���   
	        httpURLConnection.setDoInput(true);   
	        httpURLConnection.setDoOutput(true);   
	        httpURLConnection.setUseCaches(false);   
	        //  ����HTTP���󷽷��������������д�����磬GET��POST   
	        httpURLConnection.setRequestMethod("POST");   
	        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");   
	        httpURLConnection.setRequestProperty("Charset", "UTF-8");   
	        
	        //  ������Content-Type����ͷ��ָ���ֽ���е������ַ���   
	        httpURLConnection.setRequestProperty("Content-Type",   
	                "multipart/form-data;boundary=" + boundary);   
	        // ������ͷ�м���ǩ��
	        //httpURLConnection.setRequestProperty("sign", "c74e7718cbca91f6e2ce06326cee35ae");
	        httpURLConnection.setRequestProperty("sign", requestParameter.sign);
			Log.i(TAG, "�����������ǩ�����Ϊ�� " + requestParameter.sign);
			httpURLConnection.setRequestProperty("cc", MyCarActivity.cc);
			
	        //  ���OutputStream����׼���ϴ��ļ�   
	        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());   

	        //  �������ϴ��ļ���ص���Ϣ   
	        StringBuffer formDataSB = new StringBuffer();

	      	        
			// ���������б���ӵ�ǩ����
			if (requestParameter.paraMap != null && requestParameter.paraMap.size() > 0)
			{
				Set set = requestParameter.paraMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// ����������
					String formDataName = it.next().toString();
					// ������ֵ
					String formDataValue = requestParameter.paraMap.get(formDataName).toString();
			        //  ���÷ֽ������end��ʾΪ����һ��   
					formDataSB.append(twoHyphens + boundary + end);   
			        formDataSB.append("Content-Disposition: form-data;"); 
			        formDataSB.append(" name=" + formDataName+ end); 
			        formDataSB.append("Content-Type: text/plain; charset=UTF-8" + end); 
			        formDataSB.append("Content-Transfer-Encoding: 8bit" + end); 
			        formDataSB.append(end);
				    formDataSB.append( formDataValue);
				    formDataSB.append(end);
				}
			}
			dos.write(formDataSB.toString().getBytes("UTF-8"));
			Log.i(TAG, "������Ϊ�� " + formDataSB.toString());
			MessageDigest messageDigest = null;
	        if (requestParameter.file != null)
	        {
		        //  ���ϴ��ļ���Ϣ���ļ�����֮�������һ������   
		        dos.writeBytes(end);   
		        //  ��ʼ�ϴ��ļ�     
		        messageDigest = MessageDigest.getInstance("MD5");
		        FileInputStream fis = new FileInputStream(requestParameter.file);   
		        byte[] buffer = new byte[8192]; // 8k   
		        int count = 0;   
		        //  ��ȡ�ļ����ݣ���д��OutputStream����     
		        while ((count = fis.read(buffer)) != -1)   
		        {   
		            dos.write(buffer, 0, count);  
		            messageDigest.update(buffer, 0, count);
		        }   
		        fis.close();   
	            String localSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
		        //  ����һ��    
		        dos.writeBytes(end);   

	        }
	        //  ���ý������ţ��ڷֽ��������������ַ���   
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);   
	        dos.flush();   
	        //  ��ʼ��ȡ�ӷ���˴���������Ϣ   
	        InputStream is = httpURLConnection.getInputStream();   
	        String code = httpURLConnection.getHeaderField("code");
	        String initFileLen = httpURLConnection.getHeaderField("iniFileLength");
	        Log.e(TAG,"InitFileLen"+initFileLen);
	        long fileLength = httpURLConnection.getContentLength();
	    	Log.d(TAG, "�����ļ�����Content Length Ϊ :-----> " + fileLength);
	    	
	    	// �������ݳ���Ϊ 0,��ʾ����
	    	if(fileLength == 0)
	    	{
	    		if(listener!=null)
	    		{
	    			listener.onHttpDownloadException("Cannot download , content length is zero!");
	    			return null;
	    		}
	    	}
	    	Log.d(TAG, "��Ӧ��codeΪ :-----> " + code);
	        String sign = httpURLConnection.getHeaderField("sign");

	        Log.d(TAG, "��Ӧ��signΪ :-----> " + sign);
	        String contentDispostion = httpURLConnection.getHeaderField("content-disposition");
	        String fileName = contentDispostion.substring(contentDispostion.indexOf("\"") + 1,contentDispostion.length() - 1);
	        fileName = new String(fileName.getBytes("ISO8859-1"), "UTF-8");
	        Log.d(TAG, "���ص����ļ�����Ϊ :-----> " + fileName);
	        // �Ƿ���Ӧ�ɹ�
	        if (code != null && !code.equals(""))
	        {
	         	// �������ɹ�
	         	if (code.equals("0"))
	         	{
	         		wsBaseResult.responseCode = 0;
	         		// �鿴�Ƿ���ǩ��������У�˵���������ļ�
	         		if (sign != null)
	         		{
	         			if(listener!=null)
	         			{
	         				listener.onHttpDownloadStart(url.getPath(), "��ʼд���ļ�...");
	         			}
	         			String destDir = requestParameter.downloadDir;
	         			File targetDir = new File(destDir);
	         			if(!targetDir.exists())
	         			{
	         				targetDir.mkdirs();
	         			}
	         			
	         			File targetFile = new File(destDir+ File.separator+fileName);
	         			targetFile.delete();
	         			targetFile.createNewFile();
	         			
	         			FileOutputStream fous = new FileOutputStream(targetFile);
	         			
	         			int writeLen = 0;
	         			int readLen = 0;
	         			byte[] buffer = new byte[4*1024];
	         			while((readLen=is.read(buffer))!=-1)
	         			{
	         				writeLen += readLen;
	         				fous.write(buffer,0,readLen);
	         				if(listener!=null)
	         				{
	         					if(fileLength > 0)
	         					{
	         						int percent = (int) (100*writeLen / fileLength);
	         						listener.onHttpDownloadProgress(percent, 0, 0, 0);	         						
	         					}
	         				}
	         				fous.flush();
	         			}
	         			fous.close();
	         			is.close();
	         			if(listener!=null)
	         			{
	         				listener.onHttpDownloadFinished(targetFile,initFileLen);
	         			}
	        	         // У��ǩ��
	        	         messageDigest = MessageDigest.getInstance("MD5");
	        	         RequestParameter.signFile(messageDigest, new File[]{targetFile});
	        	         String validSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
	        	         Log.d(TAG, "���ؼ����ǩ�� :-----> " + validSign);
	        	         wsBaseResult.object = targetFile;//����������ص��ļ�
	        	         Log.d(TAG, "�ļ����سɹ�������·�� :" + targetFile.getAbsolutePath());
	        	         // �����֤�ɹ�
	        	         if (sign.equals(validSign))
	        	         {
	        	        		wsBaseResult.responseCode = 0;
	        	         }
	         		}
	         	}
	         	else if (code.equals("-1") && requestParameter.count++ < 1) // ǩ����֤ʧ��
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // ��ʾ�к�
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "��Ӧ�Ľ��Ϊ :-----> " + message);
	         		// ���µ�½
	    	     	
	         		try {
						login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				     	Log.d(TAG, "��¼�쳣������ʧ�� :-----> ");
		    	    	if(listener!=null)
		    	    	{
		    	    		listener.onHttpDownloadException(message);
		    	    	}
					} 
	         		// ���¼���ǩ��
	         		requestParameter.sign = requestParameter.generateUploadDigitalSignature(requestParameter.file);
	         		// �ٴη�������
	         	}
	         	else if (code.equals("400"))
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // ��ʾ�к�
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "��Ӧ�Ľ��Ϊ :-----> " + message);
	    	    	if(listener!=null)
	    	    	{
	    	    		listener.onHttpDownloadException(message);
	    	    	}
	         	}
	        }
	        dos.close();   
	        is.close();   
	    }   
	    catch (Exception e)   
	    {   
	    	Log.d(TAG, "�����ļ��쳣��Ϣ:-----> " + e.getMessage());
	    	wsBaseResult.responseCode = -100;// ��㶨���һ���쳣����
	    	wsBaseResult.object  = e.getMessage();// �����쳣��Ϣ�����ö�
	    	if(listener!=null)
	    	{
	    		listener.onHttpDownloadException(e.getMessage());
	    	}
	    }
    	return wsBaseResult;
    }
    
    /**
     * �ļ��ϴ��ӿ�
     * @param downloadListener
     * @return WSBaseResult
     */
    public WSBaseResult executeHttpUpload(FileUtils.FileDownloadListener downloadListener)
    {
    	WSBaseResult wsBaseResult = new WSBaseResult();

	    String end = "\r\n";   
	    String twoHyphens = "--";               //  �������ַ�   
	    String boundary = "**2dca164168984798a25b****";             //  �ֽ�����ַ���   
	    try  
	    {   
	        URL url = new URL(requestParameter.action);   
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();   
	        //  Ҫ��ʹ��InputStream��OutputStream������ʹ���������д���   
	        httpURLConnection.setDoInput(true);   
	        httpURLConnection.setDoOutput(true);   
	        httpURLConnection.setUseCaches(false);   
	        //  ����HTTP���󷽷��������������д�����磬GET��POST   
	        httpURLConnection.setRequestMethod("POST");   
	        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");   
	        httpURLConnection.setRequestProperty("Charset", "UTF-8");   
	        
	        //  ������Content-Type����ͷ��ָ���ֽ���е������ַ���   
	        httpURLConnection.setRequestProperty("Content-Type",   
	                "multipart/form-data;boundary=" + boundary);   
	        // ������ͷ�м���ǩ��
	        httpURLConnection.setRequestProperty("sign", requestParameter.sign);
			Log.i(TAG, "����ǩ�����Ϊ�� " + requestParameter.sign);
			httpURLConnection.setRequestProperty("cc", MyCarActivity.cc);
			
	        //  ���OutputStream����׼���ϴ��ļ�   
	        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());  
	        
	        StringBuffer formDataSB = new StringBuffer();
			// ���������б���ӵ�ǩ����
			if (requestParameter.paraMap != null && requestParameter.paraMap.size() > 0)
			{
				Set set = requestParameter.paraMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// ����������
					String formDataName = it.next().toString();
					// ������ֵ
					String formDataValue = requestParameter.paraMap.get(formDataName).toString();
			        //  ���÷ֽ������end��ʾΪ����һ��   
					formDataSB.append(twoHyphens + boundary + end);   
			        formDataSB.append("Content-Disposition: form-data;"); 
			        formDataSB.append(" name=" + formDataName+ end); 
			        formDataSB.append("Content-Type: text/plain; charset=UTF-8" + end); 
			        formDataSB.append("Content-Transfer-Encoding: 8bit" + end); 
			        formDataSB.append(end);
				    formDataSB.append( formDataValue);
				    formDataSB.append(end);
				}
			}
			dos.write(formDataSB.toString().getBytes("UTF-8"));
			Log.i(TAG, "������Ϊ�� " + formDataSB.toString());

	        //  ���÷ֽ������end��ʾΪ����һ��   
	        dos.writeBytes(twoHyphens + boundary + end);   
	        //  �������ϴ��ļ���ص���Ϣ   
	        StringBuffer fileAttributeSB = new StringBuffer();
	        fileAttributeSB.append("Content-Disposition: form-data");
	      	        
			// ���������б���ӵ�ǩ����
			if (requestParameter.fileMap != null && requestParameter.fileMap.size() > 0)
			{
				Set set = requestParameter.fileMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// ����������
					String formDataName = it.next().toString();
					// ������ֵ
					String formDataValue = requestParameter.fileMap.get(formDataName).toString();
					fileAttributeSB.append("; " + formDataName + "=\""+ formDataValue + "\"");
				}
			}
			fileAttributeSB.append(end);
			dos.write(fileAttributeSB.toString().getBytes("UTF-8"));
			Log.i(TAG, "�ļ�����Ϊ�� " + fileAttributeSB.toString());
			
	        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + end); 
	        dos.writeBytes("Content-Transfer-Encoding: 8bit" + end); 

	        //  ���ϴ��ļ���Ϣ���ļ�����֮�������һ������   
	        dos.writeBytes(end);   
	        //  ��ʼ�ϴ��ļ�     
	        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	        if (requestParameter.file != null)
	        {
		        FileInputStream fis = new FileInputStream(requestParameter.file);   
		        byte[] buffer = new byte[8192]; // 8k   
		        int count = 0;   
		        //  ��ȡ�ļ����ݣ���д��OutputStream����     
		        while ((count = fis.read(buffer)) != -1)   
		        {   
		            dos.write(buffer, 0, count);  
		            messageDigest.update(buffer, 0, count);
		        }   
		        fis.close();   
	        }
            String localSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
            Log.d(TAG, "���ؼ����ǩ�� :-----> " + localSign);
	        //  ����һ��    
	        dos.writeBytes(end);   
	        //  ���ý������ţ��ڷֽ��������������ַ���   
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);   
	        dos.flush();   
	        //  ��ʼ��ȡ�ӷ���˴���������Ϣ   
	        InputStream is = httpURLConnection.getInputStream();   
	        String code = httpURLConnection.getHeaderField("code");
	    	Log.d(TAG, "��Ӧ��codeΪ :-----> " + code);
	        String sign = httpURLConnection.getHeaderField("sign");
	        Log.d(TAG, "��Ӧ��signΪ :-----> " + sign);
	        String  fileSize = httpURLConnection.getHeaderField("fileSize");// ��ȡ�ļ�������Ϣ
	        long fileLength = Long.valueOf(fileSize);
	        Log.d(TAG, "�����ļ�����Ϊ :-----> " + fileLength);
	        String contentDispostion = httpURLConnection.getHeaderField("content-disposition");
	        String fileName = contentDispostion.substring(contentDispostion.indexOf("\"") + 1,contentDispostion.length() - 1);
	        Log.d(TAG, "���ص����ļ�����Ϊ :-----> " + fileName);
	        // �Ƿ���Ӧ�ɹ�
	        if (code != null && !code.equals(""))
	        {
	         	// �������ɹ�
	         	if (code.equals("0"))
	         	{
	         		wsBaseResult.responseCode = 0;
	         		// �鿴�Ƿ���ǩ��������У�˵���������ļ�
	         		if (sign != null)
	         		{
	         			 FileUtils fu = new FileUtils();
	        	         fu.writeToSDFromInput(requestParameter.downloadDir+ File.separator ,fileName,is,fileLength,downloadListener);
	        	        
	        	         // У��ǩ��
	        	         messageDigest = MessageDigest.getInstance("MD5");
	        	         RequestParameter.signFile(messageDigest, new File[]{fu.getFile(requestParameter.downloadDir + File.separator + fileName)});
	        	         String validSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
	        	         Log.d(TAG, "���ؼ����ǩ�� :-----> " + validSign);
	        	         // �����֤�ɹ�
	        	         if (sign.equals(validSign))
	        	         {
	        	        		wsBaseResult.responseCode = 0;
	        	        		 Log.d(TAG, "�ļ����سɹ�������·�� :" + requestParameter.downloadDir + File.separator + fileName);
	        	         }
	        	         else if (requestParameter.count++ < 1)
	        	         {
	        	        	 // ��������
//	        	        	 executeHttpPost(downloadListener);
	        	         }
	         		}
	         	}
	         	else if (code.equals("-1") && requestParameter.count++ < 1) // ǩ����֤ʧ��
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // ��ʾ�к�
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "��Ӧ�Ľ��Ϊ :-----> " + message);
	         		// ���µ�½
	    	     	
	         		try {
						login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				     	Log.d(TAG, "��¼�쳣������ʧ�� :-----> ");
					} 
	         		// ���¼���ǩ��
	         		requestParameter.sign = requestParameter.generateUploadDigitalSignature(requestParameter.file);
	         		// �ٴη�������
//	         		executeHttpPost(downloadListener);
	         	}
	         	else if (code.equals("400"))
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // ��ʾ�к�
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "��Ӧ�Ľ��Ϊ :-----> " + message);
	         	}
	        }
	        
	        dos.close();   
	        is.close();   
	    }   
	    catch (Exception e)   
	    {   
	    	Log.d(TAG, "�ϴ��ļ��쳣��Ϣ:-----> " + e.getMessage());
	    }   
    	return wsBaseResult;
    }
    
    
    /**
     * ����ǩ����������ArrayList �Ķ���ʱ����ԭ�е�ǩ�����㷽ʽ��������ж������Ե���Ϣ��
     * �ٴ����Ǵ���������������������Body�����ֵ����ַ�������ǩ�����㡣
     */
    public StringBuilder getSignString(StringBuilder sb ,SoapObject soapObject)
    {

    	// �ж�soapobject���������Ƿ���Ƕ�ף�����оͱ�������
    	if (soapObject.getNestedSoapCount() > 0)
    	{
    		for (int i = 0; i < soapObject.getNestedSoapCount(); i++)
    		{
    			// ȡ��Ƕ�׶��󣬵ݹ����
    			SoapObject so = (SoapObject)soapObject.getNestedSoap(i);
    		 	if (so.getNestedSoapCount() > 0)
    	    	{
    	    		getSignString( sb , soapObject);
    	    	}
    		 	// ���û��Ƕ�׶��󣬾�����ײ��
    		 	else
    		 	{
    		 		for (int k = 0; k < so.getPropertyCount(); k++)
        			{
    		 			sb.append(so.getProperty(k));
        			}
    		 	}
    		}
    	}
//    	else
//    	{
//	 		for (int k = 0; k < soapObject.getPropertyCount(); k++)
//			{
//	 			sb.append(soapObject.getProperty(k));
//			}
//    	}
   
    	// �ж�soap���������Ƿ�����property
    	if (soapObject.getPropertyCount() > 0)
    	{
    		for (int i = 0; i < soapObject.getPropertyCount(); i++)
    		{
    			sb.append(soapObject.getProperty(i));
    		}
    	}
		return sb;
    }
}
