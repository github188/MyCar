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
 * WebService管理类
 * 说明：提供手机端WebService调用的基本方法，目前支持对象列表、二进制文件、普通对象上传（请求参数）
 * 因返回值有不确定性，不容易抽象，故返回值由各自业务模块自行手工解析
 * @author xiangyuanmao
 *
 */
public class WebServiceManager {

	// 调试log信息target
	private static final String TAG = "WebServiceManager";
	private static final boolean D = true;
	
	// 请求参数对象
	private RequestParameter requestParameter;
	
	// 上下文（某个调用WebService的业务模块的上下文）
	private Context mContext; 
	
	// SoapObject对象，用于封装请求参数
	private SoapObject soapObject;

	public WebServiceManager(RequestParameter requestParameter)
	{
		this.requestParameter = requestParameter;
	}
	
	
	public WebServiceManager(Context context, RequestParameter requestParameter)
	{
		this.requestParameter = requestParameter;
		mContext = context;
		// 首先判断用户是否登录
		if (MyCarActivity.isLogin)
		{
			Log.i(TAG, "已登录 ");
		}
		else // 未登录的情况自动登录
		{
			Log.i(TAG, "未登录 ");
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
	 * 如果其他模块调用WebService接口时没有登录，将会启动登录
	 * 
	 */
	private void login()
	{


		// 取得全局的账号、密码
		String account = MyCarActivity.account;
		String password = MyCarActivity.password;
		// 如果获取失败，我们将手动登录
		if (account != null && password != null)
		{

			// 实例化SoapObject对象
			this.soapObject = new SoapObject(Constants.WEBSERVICE_NAME_SPACE,  Constants.SERVICE_LOGIN_METHOD_NAME);
			
			// 设置参数
			soapObject.addProperty("loginKey", account);
			soapObject.addProperty("mobileAppVersion", Constants.MYCAR_VERSION);
			soapObject.addProperty("password", getMd5Pawword(password));
		
			
			// 获得信封对象
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

			// TODO
			envelope.bodyOut = soapObject;
			
			// 设置是否是.Net为否
			envelope.dotNet = false;	
			
			envelope.setOutputSoapObject(soapObject);
			
			// 实例化传输对象，此处使用HttpTransportSE的子类，用于设置超时异常为15秒
			MyCarHttpTransportSE ht = new MyCarHttpTransportSE(Constants.SERVICE_LOGIN_URL_USERCENTER);
			// TODO
			ht.debug = true;
			try 
			{
				ht.call(null, envelope, null);
			
				SoapObject result = (SoapObject)envelope.bodyIn;
	
				
				// 得到登录状态
				if (result != null && result.getProperty(0) != null)
				{
					SoapObject so = (SoapObject)result.getProperty(0);
					
					// 是否成功
					int isSuccess = new Integer(so.hasProperty("code") 
							? so.getProperty("code").toString(): "-1").intValue();
					
					Log.i(TAG, "服务端返回的登录信息：" + (so.hasProperty("message") ? so.getProperty("message").toString() : ""));
					
					if (isSuccess == UsercenterConstants.RESULT_SUCCESS)
					{
						Log.i(TAG, "登录成功！");
						
						MyCarActivity.cc = so.hasProperty("cc")
								?  so.getProperty("cc").toString() : ""; // CC号码
						this.requestParameter.cc = MyCarActivity.cc;
						// 此时需要更新数据配置文件
						MyCarConfig.currentCCToDbName = MyCarActivity.cc + ".db";
						MyCarActivity.isLogin = true; // 是否登录： 是
						
						MyCarActivity.token = so.hasProperty("token") ?  so.getProperty("token").toString() : ""; // 令牌
						Log.i(TAG, "服务端返回的Token：" + MyCarActivity.token);
						MyCarActivity.csInterval = new Date().getTime()
								- (so.hasProperty("serverSystemTime")
								? new Long(so.getProperty("serverSystemTime").toString()).longValue() : 0L); // 客户端和服务端的时间间隔
					}
					else
					{
						// 启动登录画面
						manualLogin();
					    if (D) Log.d(TAG, "登录异常");
						throw new Exception(
								"登录异常");
					
					}
				}
				else
				{
					manualLogin();
				    if (D) Log.d(TAG, "登录异常");
								throw new Exception(
										"登录异常");
								
				}
			}
			catch (Exception e)
			{
				manualLogin();
				e.printStackTrace();
			    if (D) Log.d(TAG, "登录异常");
			}	
		}
		else // 如果取不到账号、密码就手动登录
		{
			// 启动登录画面
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
	 * 执行监听
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
	 * 执行WebService调用
	 * @return
	 * @author xiangyuanmao
	 */
	public WSBaseResult execute()
	{
		notifyExecuteStart();
		// 声明返回值对象
		WSBaseResult wSBaseResult = new WSBaseResult();
		
		// 实例化SoapObject对象
		this.soapObject = new SoapObject(requestParameter.nameSpace, requestParameter.methodName);
	
		// 设置参数
		if (requestParameter.paraMap != null && !requestParameter.paraMap.isEmpty())
		{
			Set paraKeySet = requestParameter.paraMap.keySet();
	
			for (Object paraName : paraKeySet) 
			{

				// 如果参数名称的后缀是”SO“，说明是SoapObject对象，我们使用addSoapObject()方法添加参数
				if (paraName.toString().endsWith("SO"))//TODO
				{
					soapObject.addSoapObject((SoapObject)requestParameter.paraMap.get(paraName));
				}
				else // 别的情况是普通对象对象，我们使用addProperty()方法添加参数
				{
					
					soapObject.addProperty((String)paraName, requestParameter.paraMap.get(paraName));					
				}	
			}
		}
		StringBuilder sb = new StringBuilder();
		getSignString( sb , soapObject);
		StringBuilder tempSb = new StringBuilder();
		tempSb.append(sb);
		Log.i(TAG, "sb---->： " + sb);
		// 获得信封对象
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		// 封装请求头信息
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
		
		// 天气预报服务的代码是.Net平台的，此处要做相关设置
		if (requestParameter.wsUrl.equals(Constants.SERVICE_WEATHER_URL))
		{
			envelope.dotNet = true;
		}
		else
		{
			envelope.dotNet = false;	
		} 
		
		envelope.setOutputSoapObject(soapObject);
		
		// 实例化传输对象，此处使用HttpTransportSE的子类，用于设置超时异常为15秒
		MyCarHttpTransportSE ht = new MyCarHttpTransportSE(requestParameter.wsUrl);
		
		boolean errorHappened = false;
		
		ht.debug = true; //TODO
		try {
			ht.call(requestParameter.action, envelope, null);

			wSBaseResult.responseCode = 0; //TODO
			// 取到响应头的信息
			Element[] headerIn = envelope.headerIn;
			if (headerIn != null && headerIn.length > 0) 
			{
				

				// 提取响应头中的code字段，判断是否成功
				for (Element element : headerIn)
				{
					// 取得响应头的code
				    String responseHeaderCode = ((Element)element.getChild(0)).getChild(0).toString();
				    
					Log.i(TAG, "服务器端返回信息：" + ((Element)element.getChild(1)).getChild(0).toString());
				   
					// 如果响应头信息是-1，说明token失效，需要重新登陆
				    if (responseHeaderCode.equals("-1") && requestParameter.count++ < 1)
				    {
				    	login();
						// 重新计算签名码
						Log.i(TAG, "重新计算签名码之前的签名： " + this.requestParameter.sign);
						if (this.requestParameter.sign != null)
						{
						    sb = new StringBuilder();
					        getSignString( sb , soapObject);
							tempSb = new StringBuilder();
							tempSb.append(sb);
							this.requestParameter.sign = requestParameter.getSign(tempSb);//此处替换新的签名方法 this.requestParameter.generateDigitalSignature(this.requestParameter.paraMap);
							Log.i(TAG, "重新计算签名码之后的签名： " + this.requestParameter.sign);
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
			Log.d(TAG, "发生IO异常：Message---> " + e.getMessage());
			Log.d(TAG, "发生IO异常：Cause by---> " + e.getCause());
			wSBaseResult.responseCode = 2;// 随便定义的一个异常代码
			e.printStackTrace();
			errorHappened = true;
			notifyExecuteExeption(OnWebSeriveExecuteListener.ERROR_IO, "发生IO异常：Message---> "+e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d(TAG, "发生Xml文件解析异常：Message---> " + e.getMessage());
			Log.d(TAG, "发生Xml文件解析异常：Cause by---> " + e.getCause());
			wSBaseResult.responseCode = 3;// 随便定义的一个异常代码
			e.printStackTrace();
			errorHappened = true;
			notifyExecuteExeption(OnWebSeriveExecuteListener.ERROR_XML_PARSING,"发生Xml文件解析异常："+e.getMessage());
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
	 * @return 获得请求头信息
	 * @author xiangyuanmao
	 */
	private Element[] soapHeader(String cc, String sign)
    {
	    // 定义Authenticate数据类型节点，命名空间为全局 http://www.x431.com 
	    // Authenticate为数据类型因此不为空
	    Element Author = new Element().createElement(Constants.WEBSERVICE_NAME_SPACE,"authenticate");
	    
	    // 定义Authenticate子节点Sign(签名)，无需再附带namespace，所以此处第一个参数为“”；
	    Element Sign = new Element().createElement("", "sign");
	
	    // 完结该节点，然后赋值给该节点,
	    // 为甚么Sign变量已经是STRING还NEW，因为这是告诉XML当前节点的TYPE为STRING
	    Sign.addChild(Node.TEXT, sign);

	    Log.i(TAG, "sign-->" + sign);
	    // 定义Authenticate子节点cc
	    Element CC = new Element().createElement("", "cc");
	    CC.addChild(Node.TEXT, cc);
	
	    // 定义Authenticate子节点服务器时间serviceTime
	    Element serviceTime = new Element().createElement("", "serviceTime");
	    serviceTime.addChild(Node.TEXT, (new Long (new Date().getTime() + MyCarActivity.csInterval)).toString());
	    
	    // 嵌套进Authenticate，序列化Authenticate
	    Author.addChild(Node.ELEMENT, Sign);
	    Author.addChild(Node.ELEMENT, CC);
	    //Author.addChild(Node.ELEMENT, serviceTime);
	
	    // 封装成SoapHeader()需要的Element[] 数组
	    Element[] Header = new Element[1];
	
	    // 挂载Authenticate节点
	    Header[0]=Author;
	
	    // 返回封装后的数据类型
	    return Header;
    }
	
	/**
	 * 设置对象数据参数，主要针对对象数组的wu
	 * @param treeMap 参数Map
	 * @param list 对象数组
	 * @param nameSpace 名称空间
	 * @param listName 服务端定义的方法中的对象数据参数的名称
	 * @author xiangyuanmao
	 */
    public static void setObjectArrayParameter(TreeMap treeMap, List list, String nameSpace, String listName)
    {
    	int count = 0; // 设置一个计数器
    	
    	// 遍历所有对象数据的对象通过反射的到对应的属性，并调用其getXXX方法获取它的值
    	for (Object object : list) 
    	{
    		// 对象参数需要封装成SoapObject对象
			SoapObject so = new SoapObject(nameSpace, listName);
			
			// 得到对象的类型
			Class classType = object.getClass();
			
			// 得到object的属性列表
			Field[] fields = classType.getDeclaredFields();
			
			// 遍历对象的属性字段
			for (Field field : fields) 
			{
				// 字段名称
				String fieldName = field.getName();
				
				// 为了拼装getXX方法，需要取到字段名称的第一个字母，并将其转换为大写
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				
				// 拼装字段的getXXX方法
				String getMethodName = "get" + firstLetter + fieldName.substring(1);
				try {
					
					// 获得该字段对应的getXXX方法对象
					Method getMethod = classType.getMethod(getMethodName, new Class[]{});
					
					// 得到getXXX方法的值，其实就是该属性字段的值
					Object value = getMethod.invoke(object, new Object[]{});
					
					// 添加到SoapOjbect对象中
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
			// 在参数Map里设置刚封装好的SoapObject对象
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
     * 发送HTTP的POST请求
     * @return
     */
    // TODO
    public WSBaseResult executeHttpPost(HttpDownloadListener listener)
    {
		WSBaseResult wsBaseResult = new WSBaseResult();
	    String end = "\r\n";   
	    String twoHyphens = "--";               //  两个连字符   
	    String boundary = "******";             //  分界符的字符串   
	    try  
	    {   
	        URL url = new URL(requestParameter.action); 
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();   
	        //  要想使用InputStream和OutputStream，必须使用下面两行代码   
	        httpURLConnection.setDoInput(true);   
	        httpURLConnection.setDoOutput(true);   
	        httpURLConnection.setUseCaches(false);   
	        //  设置HTTP请求方法，方法名必须大写，例如，GET、POST   
	        httpURLConnection.setRequestMethod("POST");   
	        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");   
	        httpURLConnection.setRequestProperty("Charset", "UTF-8");   
	        
	        //  必须在Content-Type请求头中指定分界符中的任意字符串   
	        httpURLConnection.setRequestProperty("Content-Type",   
	                "multipart/form-data;boundary=" + boundary);   
	        // 在请求头中加入签名
	        //httpURLConnection.setRequestProperty("sign", "c74e7718cbca91f6e2ce06326cee35ae");
	        httpURLConnection.setRequestProperty("sign", requestParameter.sign);
			Log.i(TAG, "本地请求参数签名结果为： " + requestParameter.sign);
			httpURLConnection.setRequestProperty("cc", MyCarActivity.cc);
			
	        //  获得OutputStream对象，准备上传文件   
	        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());   

	        //  设置与上传文件相关的信息   
	        StringBuffer formDataSB = new StringBuffer();

	      	        
			// 遍历参数列表，添加到签名中
			if (requestParameter.paraMap != null && requestParameter.paraMap.size() > 0)
			{
				Set set = requestParameter.paraMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// 表单数据名称
					String formDataName = it.next().toString();
					// 表单数据值
					String formDataValue = requestParameter.paraMap.get(formDataName).toString();
			        //  设置分界符，加end表示为单独一行   
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
			Log.i(TAG, "表单数据为： " + formDataSB.toString());
			MessageDigest messageDigest = null;
	        if (requestParameter.file != null)
	        {
		        //  在上传文件信息与文件内容之间必须有一个空行   
		        dos.writeBytes(end);   
		        //  开始上传文件     
		        messageDigest = MessageDigest.getInstance("MD5");
		        FileInputStream fis = new FileInputStream(requestParameter.file);   
		        byte[] buffer = new byte[8192]; // 8k   
		        int count = 0;   
		        //  读取文件内容，并写入OutputStream对象     
		        while ((count = fis.read(buffer)) != -1)   
		        {   
		            dos.write(buffer, 0, count);  
		            messageDigest.update(buffer, 0, count);
		        }   
		        fis.close();   
	            String localSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
		        //  新起一行    
		        dos.writeBytes(end);   

	        }
	        //  设置结束符号（在分界符后面加两个连字符）   
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);   
	        dos.flush();   
	        //  开始读取从服务端传过来的信息   
	        InputStream is = httpURLConnection.getInputStream();   
	        String code = httpURLConnection.getHeaderField("code");
	        String initFileLen = httpURLConnection.getHeaderField("iniFileLength");
	        Log.e(TAG,"InitFileLen"+initFileLen);
	        long fileLength = httpURLConnection.getContentLength();
	    	Log.d(TAG, "下载文件长度Content Length 为 :-----> " + fileLength);
	    	
	    	// 数据内容长度为 0,提示出错
	    	if(fileLength == 0)
	    	{
	    		if(listener!=null)
	    		{
	    			listener.onHttpDownloadException("Cannot download , content length is zero!");
	    			return null;
	    		}
	    	}
	    	Log.d(TAG, "响应的code为 :-----> " + code);
	        String sign = httpURLConnection.getHeaderField("sign");

	        Log.d(TAG, "响应的sign为 :-----> " + sign);
	        String contentDispostion = httpURLConnection.getHeaderField("content-disposition");
	        String fileName = contentDispostion.substring(contentDispostion.indexOf("\"") + 1,contentDispostion.length() - 1);
	        fileName = new String(fileName.getBytes("ISO8859-1"), "UTF-8");
	        Log.d(TAG, "下载到的文件名称为 :-----> " + fileName);
	        // 是否响应成功
	        if (code != null && !code.equals(""))
	        {
	         	// 如果请求成功
	         	if (code.equals("0"))
	         	{
	         		wsBaseResult.responseCode = 0;
	         		// 查看是否有签名，如果有，说明是下载文件
	         		if (sign != null)
	         		{
	         			if(listener!=null)
	         			{
	         				listener.onHttpDownloadStart(url.getPath(), "开始写入文件...");
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
	        	         // 校验签名
	        	         messageDigest = MessageDigest.getInstance("MD5");
	        	         RequestParameter.signFile(messageDigest, new File[]{targetFile});
	        	         String validSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
	        	         Log.d(TAG, "本地计算的签名 :-----> " + validSign);
	        	         wsBaseResult.object = targetFile;//返回这个下载的文件
	        	         Log.d(TAG, "文件下载成功，保存路径 :" + targetFile.getAbsolutePath());
	        	         // 如果验证成功
	        	         if (sign.equals(validSign))
	        	         {
	        	        		wsBaseResult.responseCode = 0;
	        	         }
	         		}
	         	}
	         	else if (code.equals("-1") && requestParameter.count++ < 1) // 签名验证失败
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // 显示行号
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "响应的结果为 :-----> " + message);
	         		// 重新登陆
	    	     	
	         		try {
						login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				     	Log.d(TAG, "登录异常，请求失败 :-----> ");
		    	    	if(listener!=null)
		    	    	{
		    	    		listener.onHttpDownloadException(message);
		    	    	}
					} 
	         		// 重新计算签名
	         		requestParameter.sign = requestParameter.generateUploadDigitalSignature(requestParameter.file);
	         		// 再次发送请求
	         	}
	         	else if (code.equals("400"))
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // 显示行号
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "响应的结果为 :-----> " + message);
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
	    	Log.d(TAG, "下载文件异常信息:-----> " + e.getMessage());
	    	wsBaseResult.responseCode = -100;// 随便定义的一个异常代码
	    	wsBaseResult.object  = e.getMessage();// 返回异常信息给调用端
	    	if(listener!=null)
	    	{
	    		listener.onHttpDownloadException(e.getMessage());
	    	}
	    }
    	return wsBaseResult;
    }
    
    /**
     * 文件上传接口
     * @param downloadListener
     * @return WSBaseResult
     */
    public WSBaseResult executeHttpUpload(FileUtils.FileDownloadListener downloadListener)
    {
    	WSBaseResult wsBaseResult = new WSBaseResult();

	    String end = "\r\n";   
	    String twoHyphens = "--";               //  两个连字符   
	    String boundary = "**2dca164168984798a25b****";             //  分界符的字符串   
	    try  
	    {   
	        URL url = new URL(requestParameter.action);   
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();   
	        //  要想使用InputStream和OutputStream，必须使用下面两行代码   
	        httpURLConnection.setDoInput(true);   
	        httpURLConnection.setDoOutput(true);   
	        httpURLConnection.setUseCaches(false);   
	        //  设置HTTP请求方法，方法名必须大写，例如，GET、POST   
	        httpURLConnection.setRequestMethod("POST");   
	        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");   
	        httpURLConnection.setRequestProperty("Charset", "UTF-8");   
	        
	        //  必须在Content-Type请求头中指定分界符中的任意字符串   
	        httpURLConnection.setRequestProperty("Content-Type",   
	                "multipart/form-data;boundary=" + boundary);   
	        // 在请求头中加入签名
	        httpURLConnection.setRequestProperty("sign", requestParameter.sign);
			Log.i(TAG, "本地签名结果为： " + requestParameter.sign);
			httpURLConnection.setRequestProperty("cc", MyCarActivity.cc);
			
	        //  获得OutputStream对象，准备上传文件   
	        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());  
	        
	        StringBuffer formDataSB = new StringBuffer();
			// 遍历参数列表，添加到签名中
			if (requestParameter.paraMap != null && requestParameter.paraMap.size() > 0)
			{
				Set set = requestParameter.paraMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// 表单数据名称
					String formDataName = it.next().toString();
					// 表单数据值
					String formDataValue = requestParameter.paraMap.get(formDataName).toString();
			        //  设置分界符，加end表示为单独一行   
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
			Log.i(TAG, "表单数据为： " + formDataSB.toString());

	        //  设置分界符，加end表示为单独一行   
	        dos.writeBytes(twoHyphens + boundary + end);   
	        //  设置与上传文件相关的信息   
	        StringBuffer fileAttributeSB = new StringBuffer();
	        fileAttributeSB.append("Content-Disposition: form-data");
	      	        
			// 遍历参数列表，添加到签名中
			if (requestParameter.fileMap != null && requestParameter.fileMap.size() > 0)
			{
				Set set = requestParameter.fileMap.keySet();
				Iterator it = set.iterator();
				while (it.hasNext())
				{
					// 表单数据名称
					String formDataName = it.next().toString();
					// 表单数据值
					String formDataValue = requestParameter.fileMap.get(formDataName).toString();
					fileAttributeSB.append("; " + formDataName + "=\""+ formDataValue + "\"");
				}
			}
			fileAttributeSB.append(end);
			dos.write(fileAttributeSB.toString().getBytes("UTF-8"));
			Log.i(TAG, "文件属性为： " + fileAttributeSB.toString());
			
	        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + end); 
	        dos.writeBytes("Content-Transfer-Encoding: 8bit" + end); 

	        //  在上传文件信息与文件内容之间必须有一个空行   
	        dos.writeBytes(end);   
	        //  开始上传文件     
	        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	        if (requestParameter.file != null)
	        {
		        FileInputStream fis = new FileInputStream(requestParameter.file);   
		        byte[] buffer = new byte[8192]; // 8k   
		        int count = 0;   
		        //  读取文件内容，并写入OutputStream对象     
		        while ((count = fis.read(buffer)) != -1)   
		        {   
		            dos.write(buffer, 0, count);  
		            messageDigest.update(buffer, 0, count);
		        }   
		        fis.close();   
	        }
            String localSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
            Log.d(TAG, "本地计算的签名 :-----> " + localSign);
	        //  新起一行    
	        dos.writeBytes(end);   
	        //  设置结束符号（在分界符后面加两个连字符）   
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);   
	        dos.flush();   
	        //  开始读取从服务端传过来的信息   
	        InputStream is = httpURLConnection.getInputStream();   
	        String code = httpURLConnection.getHeaderField("code");
	    	Log.d(TAG, "响应的code为 :-----> " + code);
	        String sign = httpURLConnection.getHeaderField("sign");
	        Log.d(TAG, "响应的sign为 :-----> " + sign);
	        String  fileSize = httpURLConnection.getHeaderField("fileSize");// 获取文件长度信息
	        long fileLength = Long.valueOf(fileSize);
	        Log.d(TAG, "下载文件长度为 :-----> " + fileLength);
	        String contentDispostion = httpURLConnection.getHeaderField("content-disposition");
	        String fileName = contentDispostion.substring(contentDispostion.indexOf("\"") + 1,contentDispostion.length() - 1);
	        Log.d(TAG, "下载到的文件名称为 :-----> " + fileName);
	        // 是否响应成功
	        if (code != null && !code.equals(""))
	        {
	         	// 如果请求成功
	         	if (code.equals("0"))
	         	{
	         		wsBaseResult.responseCode = 0;
	         		// 查看是否有签名，如果有，说明是下载文件
	         		if (sign != null)
	         		{
	         			 FileUtils fu = new FileUtils();
	        	         fu.writeToSDFromInput(requestParameter.downloadDir+ File.separator ,fileName,is,fileLength,downloadListener);
	        	        
	        	         // 校验签名
	        	         messageDigest = MessageDigest.getInstance("MD5");
	        	         RequestParameter.signFile(messageDigest, new File[]{fu.getFile(requestParameter.downloadDir + File.separator + fileName)});
	        	         String validSign = Md5Helper.byteArrayToHexString(messageDigest.digest());
	        	         Log.d(TAG, "本地计算的签名 :-----> " + validSign);
	        	         // 如果验证成功
	        	         if (sign.equals(validSign))
	        	         {
	        	        		wsBaseResult.responseCode = 0;
	        	        		 Log.d(TAG, "文件下载成功，保存路径 :" + requestParameter.downloadDir + File.separator + fileName);
	        	         }
	        	         else if (requestParameter.count++ < 1)
	        	         {
	        	        	 // 重新下载
//	        	        	 executeHttpPost(downloadListener);
	        	         }
	         		}
	         	}
	         	else if (code.equals("-1") && requestParameter.count++ < 1) // 签名验证失败
	         	{
	                InputStreamReader isr = new InputStreamReader(is, "utf-8");   
	    	        BufferedReader br = new BufferedReader(isr);   
	    	        StringBuffer responseBuffer = new StringBuffer();
	    	        String result; 
	    	        while ((result = br.readLine()) != null) 
	    	        {
	                    // 显示行号
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "响应的结果为 :-----> " + message);
	         		// 重新登陆
	    	     	
	         		try {
						login();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				     	Log.d(TAG, "登录异常，请求失败 :-----> ");
					} 
	         		// 重新计算签名
	         		requestParameter.sign = requestParameter.generateUploadDigitalSignature(requestParameter.file);
	         		// 再次发送请求
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
	                    // 显示行号
	    	        	responseBuffer.append(result);
	                }
	    	        result = responseBuffer.toString();
	    	        String message = result.substring(result.indexOf("<message>"), result.indexOf("</message>"));
	    	        wsBaseResult.object = message;
	    	     	Log.d(TAG, "响应的结果为 :-----> " + message);
	         	}
	        }
	        
	        dos.close();   
	        is.close();   
	    }   
	    catch (Exception e)   
	    {   
	    	Log.d(TAG, "上传文件异常信息:-----> " + e.getMessage());
	    }   
    	return wsBaseResult;
    }
    
    
    /**
     * 计算签名，当传递ArrayList 的对象时，用原有的签名计算方式里面包含有对象属性的信息，
     * 再次我们从请求体里面提炼出请求Body里面的值组成字符串参与签名计算。
     */
    public StringBuilder getSignString(StringBuilder sb ,SoapObject soapObject)
    {

    	// 判断soapobject对象里面是否还有嵌套，如果有就遍历出来
    	if (soapObject.getNestedSoapCount() > 0)
    	{
    		for (int i = 0; i < soapObject.getNestedSoapCount(); i++)
    		{
    			// 取到嵌套对象，递归调用
    			SoapObject so = (SoapObject)soapObject.getNestedSoap(i);
    		 	if (so.getNestedSoapCount() > 0)
    	    	{
    	    		getSignString( sb , soapObject);
    	    	}
    		 	// 如果没有嵌套对象，就是最底层的
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
   
    	// 判断soap对象里面是否有有property
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
