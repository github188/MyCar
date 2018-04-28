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
 * �����������
 * ��������ҵ��ģ��ĸ���WebService����ͳһ�淶��
 * ��������WebServiceͨѶ�����в�����ͬʱ�ṩ��ȫ����ʵ��
 * @author xiangyuanmao
 *
 */
public class RequestParameter {
    // ����log��Ϣtarget
    private static final String TAG = "RequestParameter";
    private static final boolean D = true;
	public String nameSpace; // ���ƿռ�
	public String wsUrl; // webservice��URL
	public String action; // Action
	public String methodName; // ��������  
	public TreeMap paraMap; // �������
	public TreeMap fileMap; // �ļ���صĲ���
	public String cc; // cc��
	public String sign; // ǩ����
	public File file; // �ļ�
	public String downloadFileName = "default.mex";
	File sdPath = Environment.getExternalStorageDirectory();
	public String downloadDir = "/mycar";
	public boolean isSign = false; // �Ƿ���Ҫǩ��
	int count = 0; // ���ʼ�����
    
	/**
     * ���췽��
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
		// �����Ҫǩ��
		if (isSign)
		{
			this.cc = MyCarActivity.cc;
			this.sign = generateDigitalSignature(paraMap);
		}
	}

	/**
	 * ���췽��
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
		// �����Ҫǩ��
		if (isSign)
		{
			this.cc = MyCarActivity.cc;
			this.sign = generateDigitalSignature(paraMap);
		}
	}
	
	/**
	 * �����ļ�ʱʹ�øù��췽��
	 * @param action  ����·����������jspҳ��ı���action��ֵ��Ҳ�����ύ���ĵط�
	 * @param file  Ҫ�ϴ����ļ�
	 * @param paraMap  �����б���Ҫע����ǣ�������ϴ��ļ�����ôӦ���и�filename�Ĳ���
	 */
	public RequestParameter(String action, File file, TreeMap paraMap)
	{
		this.action = action;
		this.paraMap = paraMap;
		this.file = file;
		this.sign = generateUploadDigitalSignature(file);
	}
	/**
	 * �ϴ��ļ�ʱʹ�øù��췽��
	 * @param action  ����·����������jspҳ��ı���action��ֵ��Ҳ�����ύ���ĵط�
	 * @param file  Ҫ�ϴ����ļ�
	 * @param paraMap  �����б���Ҫע����ǣ�������ϴ��ļ�����ôӦ���и�filename�Ĳ���
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
	 * �÷��������ڼ���ǩ��ʱ�����������ת��Ϊ�ַ���
	 * @param treeMap
	 * @return
	 */
	public String appendStringParameter(TreeMap treeMap)
	{
		StringBuffer sb = new StringBuffer();
		// ���������б���ӵ�ǩ����
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
	 * �÷��������ڼ���ǩ��ʱ�����ļ�ת��Ϊ�ַ���
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
		 //����������
        FileInputStream fis = null;                  
        try
        {
            //����������
            fis = new FileInputStream(uploadFile);
            //��ȡ���ݣ�������ȡ�������ݴ洢��������
            byte[] data = new byte[(int)uploadFile.length()]; //���ݴ洢������
            int i = 0; //��ǰ�±�
            //��ȡ���еĵ�һ���ֽ�����
            int n = fis.read();
            //���ζ�ȡ����������
            while(n != -1){ //δ��������ĩβ
                     //����Ч���ݴ洢��������
                     data[i] = (byte)n;
                     //�±�����
                     i++;
                     //��ȡ��һ���ֽڵ�����
                     n = fis.read();
            }
            
            String s = new String(data,0,i);
            //��������
            sb.append(s);
            //����ַ���
        	 Log.i(TAG, "�ϴ����ļ����ֽ�����Ϊ :-----> " + s);
        } 
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
        finally
        {
        	try
        	{      
        		//�ر������ͷ���Դ
        		fis.close();
            }
        	catch(Exception e)
        	{
        		
        	}
        }
		return sb.toString();
	}
	
	
	/**
	 * �����ϴ��ļ�ʱ��ǩ����
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
		 * ���ڲ�����ʹ�÷�����ʱ�����ǩ������	
		 *      // ������ʱ�� = ����ʱ�� + ʱ����
		Date serviceTime = new Date(new Date().getTime() + MyCarActivity.csInterval);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// ������ʱ��,��һ���ݲ�������
		signBuffer.append(sdf.format(serviceTime)); */
		// ����
		Log.i(TAG, "����ǩ�������Token��" + MyCarActivity.token);
        return md5Hex(params.toString(),new File[]{file},MyCarActivity.token);
	}
	
	/**
	 * ���ݲ�ͬ�ķ����ʼ��
	 * @param serviceType
	 */
	private void init(int serviceType)
	{
		switch (serviceType)
		{
		case Constants.SERVICE_LOGIN: // ��¼����
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_LOGIN_URL_USERCENTER;
			break;
		case Constants.SERVICE_BUSINESS: // ҵ�����
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_BUSINESS_URL;
			break;
		case Constants.SERVICE_MANAGER_ACCOUNT: // �����ܼ�_����
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_ACCOUNT_URL;
			break;
		case Constants.SERVICE_MANAGER_BILL_CATEGORY: // �����ܼ�_�Զ������
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_CATEGORY_URL;
			break;
		case Constants.SERVICE_MANAGER_OIL: // �����ܼ�_�ͺ�
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_OIL_URL;
			break;
		case Constants.SERVICE_MANAGER_USER_CAR: // �����ܼ�_�û�����
			this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
			this.wsUrl = Constants.SERVICE_MANAGER_USER_CAR_URL;
			break;
		case Constants.SERVICE_MANAGER_MANAGER_SETTING: // �����ܼ�_�û�����
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
       case Constants.SERVICE_PRODUCT: // ��Ʒ����
           this.nameSpace = Constants.WEBSERVICE_NAME_SPACE;
           this.wsUrl = Constants.SERVICE_PRODUCT_SERVICE;
           break;      
       case Constants.SERVICE_PUBLIC_SOFT: // �����������
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
	 * Ϊ��֤�ͻ������������ͨѶʱ�İ�ȫ����ÿ��WebService�������е����һ������������Ϊ����ǩ������
	 * (��������˵���ĺ�����)�����������˽��յ���������Ȼ���֤ǩ�����Ƿ���ȷ���������ȷ������ܾ�����
     * ����ǩ�����������ɷ�����
     *  1. ��ȡ���ƣ�����Ϊguid��ʽ��36λ�ַ��������ͻ��˵�¼ϵͳ�ɹ��󼴿ɻ�á�
     *  2. ��ȡʱ����: ���ͻ��˵�¼ϵͳ�ɹ��󼴿ɻ�õ�ǰ������ϵͳʱ�䣬ͨ���뱾��ʱ����㣬�ó�ʱ������
     *  3. ����ַ���������1+����2 ����+����n + ������ϵͳʱ��(��ʽΪyyyy-MM-dd HH:mm:ss��
     *     ͨ������ʱ��+ʱ�����������) + ����
     *  4. ����һ����ϵ��ַ�������MD5ת������������ǩ���롣
	 */
	public String generateDigitalSignature(TreeMap paraMap)
	{

		StringBuffer signBuffer = new StringBuffer();
		
		// ���������б���ӵ�ǩ����
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
		 * ���ڲ�����ʹ�÷�����ʱ�����ǩ������	
		 *      // ������ʱ�� = ����ʱ�� + ʱ����
		Date serviceTime = new Date(new Date().getTime() + MyCarActivity.csInterval);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// ������ʱ��,��һ���ݲ�������
		signBuffer.append(sdf.format(serviceTime)); */

		// ����
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
		Log.i(TAG, "����ǩ�������Token��" + MyCarActivity.token);
		Log.i(TAG, "signBuffer :-----> " + signBuffer);
		Log.i(TAG, "sign------->" + sign);
        return sign;
		//return Md5Helper.fastMd5AsHex(signBuffer.toString());
		
	}
	@Deprecated
	public String getSign(StringBuilder sb)
	{
		// ����
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
		Log.i(TAG, "����ǩ�������Token��" + MyCarActivity.token);
		Log.i(TAG, "signBuffer :-----> " + sb);
		Log.i(TAG, "sign------->" + sign);
        return sign;
	}
	/**
	 * �õ�ĳ�ַ�����MD5��ʹ��Fast MD5 Implementation in
	 * Java(http://www.twmacinta.com/myjava/fast_md5.php)
	 * 
	 * @param s Ҫ����MD5���ַ���
	 * @return MD5�����16�����ִ�����������ĸ��ϵ���ʽ
	 */
	@Deprecated
	public static String fastMd5AsHex(String s) {
		MD5 md5 = new MD5();
		try {
			md5.Update(s, null);
		} catch (UnsupportedEncodingException ex) {
		      if(D) Log.i(TAG, "��֧�ֵı���: " + s);
		}
		return md5.asHex();
	}
}
