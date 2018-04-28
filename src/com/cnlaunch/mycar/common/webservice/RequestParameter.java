package com.cnlaunch.mycar.common.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.os.Environment;
import android.util.Log;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.md5.MD5;
import com.cnlaunch.mycar.common.utils.md5.Md5Helper;


/**
 * 请求参数对象
 * 提炼各自业务模块的各种WebService请求统一规范，
 * 包含建立WebService通讯的所有参数，同时提供安全策略实现
 * @author xiangyuanmao
 *
 */
public class RequestParameter {
    // 调试log信息target
    private static final String TAG = "RequestParameter";
    private static final boolean D = true;
	public String nameSpace; // 名称空间
	public String wsUrl; // webservice的URL
	public String action; // Action
	public String methodName; // 方法名称  
	public TreeMap paraMap; // 请求参数
	public TreeMap fileMap; // 文件相关的参数
	public String cc; // cc号
	public String sign; // 签名码
	public File file; // 文件
	public String downloadFileName = "default.mex";
	File sdPath = Environment.getExternalStorageDirectory();
	public String downloadDir = "/mycar";
	public boolean isSign = false; // 是否需要签名
	int count = 0; // 访问计数器
    
	/**
     * 构造方法
     * @param serviceType
     * @param methodName
     * @param paraMap
     * @param isSign
     */
	public RequestParameter(int serviceType,
			String methodName, String action,TreeMap paraMap, boolean isSign) {

		this.action = action;
		this.methodName = methodName;
		this.paraMap = paraMap;
		this.isSign = isSign;
		init(serviceType);
		// 如果需要签名
		if (isSign)
		{
			this.cc = MyCarActivity.cc;
			this.sign = generateDigitalSignature(paraMap);
		}
	}

	/**
	 * 构造方法
	 * @param nameSpace
	 * @param url
	 * @param methodName
	 * @param soapAction
	 * @param paraMap
	 * @param isSign
	 */
	public RequestParameter(String nameSpace,String url,
			String methodName, String action,TreeMap paraMap, boolean isSign) {
        this.nameSpace = nameSpace;
        this.wsUrl = url;
		this.action = action;
		this.methodName = methodName;
		this.paraMap = paraMap;
		// 如果需要签名
		if (isSign)
		{
			this.cc = MyCarActivity.cc;
			this.sign = generateDigitalSignature(paraMap);
		}
	}
	
	/**
	 * 下载文件时使用该构造方法
	 * @param action  请求路径，相似于jsp页面的表单的action的值，也就是提交到的地方
	 * @param file  要上传的文件
	 * @param paraMap  参数列表，需要注意的是，如果是上传文件，那么应该有个filename的参数
	 */
	public RequestParameter(String action, File file, TreeMap paraMap)
	{
		this.action = action;
		this.paraMap = paraMap;
		this.file = file;
		this.sign = generateUploadDigitalSignature(file);
	}
	/**
	 * 上传文件时使用该构造方法
	 * @param action  请求路径，相似于jsp页面的表单的action的值，也就是提交到的地方
	 * @param file  要上传的文件
	 * @param paraMap  参数列表，需要注意的是，如果是上传文件，那么应该有个filename的参数
	 */
	public RequestParameter(String action, File file, TreeMap paraMap , TreeMap fileMap)
	{
		this.fileMap = fileMap;
		this.action = action;
		this.paraMap = paraMap;
		this.file = file;
		this.sign = generateUploadDigitalSignature(file);
	}
	/**
	 * 该方法用于在计算签名时，把请求参数转换为字符串
	 * @param treeMap
	 * @return
	 */
	public String appendStringParameter(TreeMap treeMap)
	{
		StringBuffer sb = new StringBuffer();
		// 遍历参数列表，添加到签名中
		if (paraMap != null && paraMap.size() > 0)
		{
			Set set = paraMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext())
			{
				String key = it.next().toString();
				Object value = paraMap.get(key);
				if (value != null && !key .equals(Constants.UPLOAD_FILE_NAME) 
						&& !key.equals(Constants.UPLOAD_FILE_FILENAME) 
						&& !key.equals(Constants.UPLOAD_FILE_SIZE))
				{					
					sb.append(value);
				}
			}
		}
		return sb.toString();
	}
	
	 public static String md5Hex(String params, File[] fileParts, String token)
	    {
	        try
	        {
	            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	            if (StringUtils.isNotEmpty(params))
	            {
	               messageDigest.update(params.getBytes("UTF-8"));
	            }
	            messageDigest = signFile( messageDigest, fileParts);
	            if (StringUtils.isNotEmpty(token))
	            {
	               messageDigest.update(token.getBytes("UTF-8"));
	            }
	            
	            return Md5Helper.byteArrayToHexString(messageDigest.digest());
	        }
	        catch (GeneralSecurityException e)
	        {
	            throw  new RuntimeException(e); 
	        }
	        catch (IOException e)
	        {
	            throw new RuntimeException(e); 
	        }
	    }
	 
	 public static MessageDigest signFile(MessageDigest messageDigest,File[] fileParts)
	 {
	   
         if (!ArrayUtils.isEmpty(fileParts) && fileParts.length > 0)
         {
             for (int i = 0; i < fileParts.length; i++)
             {
             	if (fileParts[i] != null )
             	{
             	     try
         	        {
             		FileInputStream input = new FileInputStream(fileParts[i]);
             		//TODO
	                    try
	                    {
	                    	//TODO
	                    	int bufferLength = 1024;
	                        byte[] buffer = new byte[bufferLength];
	                        int read = input.read(buffer, 0, bufferLength);
	                        while (read > -1)
	                        {
	                            messageDigest.update(buffer, 0, read);
	                            read = input.read(buffer, 0, bufferLength);
	                        }
	                    }
	                    finally
	                    {
	                        input.close();
	                    }
         	        }
        	        catch (IOException e)
        	        {
        	            throw new RuntimeException(e); 
        	        }
             	}
             }
         }
		 return messageDigest;
	 }
	/**
	 * 该方法用于在计算签名时，把文件转换为字符串
	 * @param uploadFile
	 * @return
	 */
	public String appendFileParameter(File uploadFile)
	{
		if (uploadFile == null)
		{
			return null;
		}
		StringBuffer sb = new StringBuffer();
		 //声明流对象
        FileInputStream fis = null;                  
        try
        {
            //创建流对象
            fis = new FileInputStream(uploadFile);
            //读取数据，并将读取到的数据存储到数组中
            byte[] data = new byte[(int)uploadFile.length()]; //数据存储的数组
            int i = 0; //当前下标
            //读取流中的第一个字节数据
            int n = fis.read();
            //依次读取后续的数据
            while(n != -1){ //未到达流的末尾
                     //将有效数据存储到数组中
                     data[i] = (byte)n;
                     //下标增加
                     i++;
                     //读取下一个字节的数据
                     n = fis.read();
            }
            
            String s = new String(data,0,i);
            //解析数据
            sb.append(s);
            //输出字符串
        	 Log.i(TAG, "上传的文件的字节序列为 :-----> " + s);
        } 
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
        finally
        {
        	try
        	{      
        		//关闭流，释放资源
        		fis.close();
            }
        	catch(Exception e)
        	{
        		
        	}
        }
		return sb.toString();
	}
	
	
	/**
	 * 计算上传文件时的签名码
	 * @param file
	 * @return
	 */
	//TODO
	@Deprecated
	public String generateUploadDigitalSignature(File file)
	{
		StringBuffer params = new StringBuffer();
		if (paraMap != null && paraMap.size() > 0)
		{
			params.append(appendStringParameter(paraMap));		
		}
		/**
		 * @author xiangyuanmao
		 * 本期不考虑使用服务器时间参与签名计算	
		 *      // 服务器时间 = 本地时间 + 时间间隔
		Date serviceTime = new Date(new Date().getTime() + MyCarActivity.csInterval);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// 服务器时间,第一期暂不做考虑
		signBuffer.append(sdf.format(serviceTime)); */
		// 令牌
		Log.i(TAG, "参与签名计算的Token：" + MyCarActivity.token);
        return md5Hex(params.toString(),new File[]{file},MyCarActivity.token);
	}
	
	/**
	 * 根据不同的服务初始化
	 * @param serviceType
	 */
	private void init(int serviceType)
	{
		switch (serviceType)
		{
		case Constants.SERVICE_LOGIN: // 登录服务
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_LOGIN_URL_USERCENTER;
			break;
		case Constants.SERVICE_BUSINESS: // 业务服务
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_BUSINESS_URL;
			break;
		case Constants.SERVICE_MANAGER_ACCOUNT: // 车辆管家_记账
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_ACCOUNT_URL;
			break;
		case Constants.SERVICE_MANAGER_BILL_CATEGORY: // 车辆管家_自定义类别
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_CATEGORY_URL;
			break;
		case Constants.SERVICE_MANAGER_OIL: // 车辆管家_油耗
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_OIL_URL;
			break;
		case Constants.SERVICE_MANAGER_USER_CAR: // 车辆管家_用户车辆
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_USER_CAR_URL;
			break;
		case Constants.SERVICE_MANAGER_MANAGER_SETTING: // 车辆管家_用户设置
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_MANAGER_SETTING_URL;
			break;
		case Constants.SERVICE_USERCENTER:
		    this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
		    this.wsUrl = Constants.SERVICE_USERCENTER_URL_USERCENTER;
		    break;
		case Constants.SERVICE_RESCUE_VEHICLES:
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_RESCUE_VEHICLES_SERVICEMERCHANT_URL;
			break;
		case Constants.SERVICE_BLACK_BOX:
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_BLACK_BOX_SERVICEMERCHANT_URL;
			break;
		case Constants.SERVICE_CLASS_ONE_KEY_DIAG:
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_ONE_KEY_DIAG;
			break;
		case Constants.SERVICE_EXT_USER_INFO: 
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_EXT_USER_INFO_URL;
			break;
		case Constants.SERVICE_USER_SECURITY: 
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_USERCENTER_URL_USER_SECURITY;
			break;
       case Constants.SERVICE_BIND_EMAIL: 
            this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
            this.wsUrl = Constants.SERVICE_USERCENTER_URL_USER_BIND;
            break;		
       case Constants.SERVICE_PRODUCT: // 产品管理
           this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
           this.wsUrl = Constants.SERVICE_PRODUCT_SERVICE;
           break;      
       case Constants.SERVICE_PUBLIC_SOFT: // 公共软件管理
           this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
           this.wsUrl = Constants.SERVICE_PUBLIC_SOFT_SERVICE;
           break;      
           
		default :
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_LOGIN_URL_USERCENTER;
			break;
		}
	}
	/**
	 * 为保证客户端与服务器端通讯时的安全，在每个WebService函数的中的最后一个参数都定义为数字签名参数
	 * (除了特殊说明的函数外)。当服务器端接收到请求后，首先会验证签名码是否正确，如果不正确，将会拒绝服务。
     * 数字签名参数的生成方法：
     *  1. 获取令牌，令牌为guid格式，36位字符串，当客户端登录系统成功后即可获得。
     *  2. 获取时间间隔: 当客户端登录系统成功后即可获得当前服务器系统时间，通过与本地时间计算，得出时间间隔。
     *  3. 组成字符串：参数1+参数2 ……+参数n + 服务器系统时间(格式为yyyy-MM-dd HH:mm:ss，
     *     通过本地时间+时间间隔计算出来) + 令牌
     *  4. 将上一步组合的字符串进行MD5转换，即生成了签名码。
	 */
	public String generateDigitalSignature(TreeMap paraMap)
	{

		StringBuffer signBuffer = new StringBuffer();
		
		// 遍历参数列表，添加到签名中
		if (paraMap != null && paraMap.size() > 0)
		{
			Set set = paraMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext())
			{
				Object value = paraMap.get(it.next());
				if (value != null)
				{					
					signBuffer.append(value);
				}
			}
		}
		
		/**
		 * @author xiangyuanmao
		 * 本期不考虑使用服务器时间参与签名计算	
		 *      // 服务器时间 = 本地时间 + 时间间隔
		Date serviceTime = new Date(new Date().getTime() + MyCarActivity.csInterval);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// 服务器时间,第一期暂不做考虑
		signBuffer.append(sdf.format(serviceTime)); */

		// 令牌
		signBuffer.append(MyCarActivity.token);
		MessageDigest messageDigest = null;
        try {
        	messageDigest  = MessageDigest.getInstance("MD5");
	        if (StringUtils.isNotEmpty(signBuffer))
	        {
	
				messageDigest.update((signBuffer.toString()).getBytes("UTF-8"));
	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String sign = Md5Helper.byteArrayToHexString(messageDigest.digest());
		Log.i(TAG, "参与签名计算的Token：" + MyCarActivity.token);
		Log.i(TAG, "signBuffer :-----> " + signBuffer);
		Log.i(TAG, "sign------->" + sign);
        return sign;
		//return Md5Helper.fastMd5AsHex(signBuffer.toString());
		
	}
	@Deprecated
	public String getSign(StringBuilder sb)
	{
		// 令牌
		sb.append(MyCarActivity.token);
		MessageDigest messageDigest = null;
        try {
        	messageDigest  = MessageDigest.getInstance("MD5");
	        if (StringUtils.isNotEmpty(sb))
	        {
	
				messageDigest.update((sb.toString()).getBytes("UTF-8"));
	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String sign = Md5Helper.byteArrayToHexString(messageDigest.digest());
		Log.i(TAG, "参与签名计算的Token：" + MyCarActivity.token);
		Log.i(TAG, "signBuffer :-----> " + sb);
		Log.i(TAG, "sign------->" + sign);
        return sign;
	}
	/**
	 * 得到某字符串的MD5，使用Fast MD5 Implementation in
	 * Java(http://www.twmacinta.com/myjava/fast_md5.php)
	 * 
	 * @param s 要生成MD5的字符串
	 * @return MD5结果的16进制字串，即数字字母混合的形式
	 */
	@Deprecated
	public static String fastMd5AsHex(String s) {
		MD5 md5 = new MD5();
		try {
			md5.Update(s, null);
		} catch (UnsupportedEncodingException ex) {
		      if(D) Log.i(TAG, "不支持的编码: " + s);
		}
		return md5.asHex();
	}
}
