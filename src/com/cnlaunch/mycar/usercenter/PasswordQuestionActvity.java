package com.cnlaunch.mycar.usercenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
/**
 * @description 
 * @author ��Զï
 * @date��2012-4-17
 */
public class PasswordQuestionActvity extends BaseActivity 
	implements OnItemSelectedListener, OnClickListener
{
    // ����log��Ϣtarget
    private static final String TAG = "PasswordQuestionActvity";
    private static final boolean D = true;
    private Resources resources;
    private Spinner question1;
    private Spinner question2;
    private Spinner question3;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private EditText etAnswer3;
    private Button btnOk;
    private Button btnCancel;
    private ArrayAdapter<String> adapterQuestion;
    private ArrayList<String> questionList = new ArrayList<String>();
    private CustomProgressDialog pdlg; // ���ȶԻ���
    private boolean isOpenProgress = false;   // �Ƿ�򿪽��ȿ�
    private LinkedHashMap questionMap = new LinkedHashMap();
    private SecurityAnswerDTO sadto1;
    private SecurityAnswerDTO sadto2;
    private SecurityAnswerDTO sadto3;
    private Set keySet;
    private Set checkRepeat = new HashSet(); // ������ţ�����У���û��Ƿ��ظ��ش�����
	private Integer questionId1;
	private Integer questionId2;
	private Integer questionId3;
	private String answer1;
	private String answer2;
	private String answer3;
	private String cc = null;
	private String password;
    private final static int WS_RESPONSE_GETQUESTION_LIST = 0;
    private final static int WS_RESPONSE_SET_QUESTION = 1;
    private final static int WS_RESPONSE_GET_USER_QUESTION = 2;
    private final static int WS_RESPONSE_RETRIEVED_PASSWORD = 3;
	public static final String ORIGINAL_ACTIVITY = "originalActivity";
	public static final int ACTIVITY_LOGIN = 1;
	public static final int ACTIVITY_USERINFO = 2;
	int original = -2;
	String loginKey ;
	String language ; 
    private boolean isSet = false; // �Ƿ�����������ʾ����
	@Override
	protected void onStart() {
		super.onStart();
		//registerTitleReceive();
		isOpenProgress = true;
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		isOpenProgress = true;
	}
	@Override
	protected void onStop() {
		stopProgressDialog();
		super.onStop();
	}
	/**
	 * �����˻���ķ���
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_password_question, R.layout.custom_title);
		language = Locale.getDefault().getLanguage();
		if (language.equals("zh"))
		{
		    language = "zh-cn";
		}
		Intent intent = getIntent();// 
		original = intent.getIntExtra(ORIGINAL_ACTIVITY, 0);
		// ����Ǵӵ�¼���������
		if (original == ACTIVITY_LOGIN)
		{
			setCustomeTitleLeft(R.string.uc_retrieved_password);
			// ȡ�õ�¼�ؼ���
			loginKey = intent.getStringExtra("loginKey");
			new GetUserQuestionThread(loginKey).start();
		}
		else // ���û���Ϣ�������
		{
			isSet = true;
			setCustomeTitleLeft(R.string.uc_set_password_question);
			syncQuestionListFromService();
			
		}
		setCustomeTitleRight("");
		resources = getResources();
		isOpenProgress = true;
		startProgressDialog(resources.getString(R.string.uc_get_question_list));
	  	initViews(); // ��ʼ���б�
	}
	
	private void initViews() {
		question1 = (Spinner) findViewById(R.id.sp_password_question_1);
		question2 = (Spinner) findViewById(R.id.sp_password_question_2);
		question3 = (Spinner) findViewById(R.id.sp_password_question_3);
		etAnswer1 = (EditText) findViewById(R.id.et_answer_1);
		etAnswer2 = (EditText) findViewById(R.id.et_answer_2);
		etAnswer3 = (EditText) findViewById(R.id.et_answer_3);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		adapterQuestion = new ArrayAdapter<String>(this,
				R.layout.spinner_textview, questionList);
		adapterQuestion
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		question1.setAdapter(adapterQuestion);
		question1.setOnItemSelectedListener(this);
		question1.showContextMenu();
		question2.setAdapter(adapterQuestion);
		question2.setOnItemSelectedListener(this);
		question2.showContextMenu();
		question3.setAdapter(adapterQuestion);
		question3.setOnItemSelectedListener(this);
		question3.showContextMenu();
	}

	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch(parent.getId())
		{
		case R.id.sp_password_question_1:

			if (position > 0)
			{
				//checkRepeat.add(position);
				questionId1 = new Integer(keySet.toArray()[position - 1].toString());
			}
			break;
		case R.id.sp_password_question_2:
			if (position > 0)
			{
				//checkRepeat.add(position);
				questionId2 = new Integer(keySet.toArray()[position - 1].toString());
			}
			break;
		case R.id.sp_password_question_3:
			if (position > 0)
			{
				//checkRepeat.add(position);
				questionId3 = new Integer(keySet.toArray()[position - 1].toString());
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "û��ѡ����ǣ�" + parent.getId());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_ok:
			checkAnswer();
			break;
		case R.id.btn_cancel:
			PasswordQuestionActvity.this.finish();
			break;
		}
	}
	
	// ���ڱ���������ţ����û�ѡ�񲢻ش�ĳ�������ͼ��룬����ж�set�����Ƿ�
	private boolean checkAnswer()
	{

		answer1 = etAnswer1.getText().toString().trim();
		answer2 = etAnswer2.getText().toString().trim();
		answer3 = etAnswer3.getText().toString().trim();
		int position1=question1.getSelectedItemPosition();
		int position2=question2.getSelectedItemPosition();
		int position3=question3.getSelectedItemPosition();		
		checkRepeat.clear();
		if (position1 > 0)
		{
			checkRepeat.add(position1);
		}
		if (position2 > 0)
		{
			checkRepeat.add(position2);
		}
		if (position3 > 0)
		{
			checkRepeat.add(position3);
		}
		if (answer1.equals("") || answer2.equals("") || answer3.equals(""))
		{
			dialog(resources.getString(R.string.uc_answer_is_null),false);
			return false;
		}
		else
		{
			sadto1 = new SecurityAnswerDTO(questionId1, LoginThread.getMd5Pawword(answer1));
			sadto2 = new SecurityAnswerDTO(questionId2, LoginThread.getMd5Pawword(answer2));
			sadto3 = new SecurityAnswerDTO(questionId3, LoginThread.getMd5Pawword(answer3));
			List<SecurityAnswerDTO> securityAnswerList = new ArrayList<SecurityAnswerDTO>();
			securityAnswerList.add(sadto1);
			securityAnswerList.add(sadto2);
			securityAnswerList.add(sadto3);
			if (isSet)
			{
				if (checkRepeat.size() < 3)
				{
					dialog(resources.getString(R.string.uc_answer_repeat_or_less),false);
					return false;
				}
				new SetPasswordQuestionThread(securityAnswerList).start();
				startProgressDialog(resources.getString(R.string.uc_setting_question));
			}
			else
			{
				new RetrievedPasswordThread(securityAnswerList).start();
				startProgressDialog(resources.getString(R.string.uc_retriefeing_question));
			}
			
		}
		return true;
	}
	
	/**
	 * �һ�����
	 * @author xiangyuanmao
	 *
	 */
	class RetrievedPasswordThread extends Thread
	{
		List<SecurityAnswerDTO> securityAnswerList;
		RetrievedPasswordThread(List<SecurityAnswerDTO> securityAnswerList)
		{
				this.securityAnswerList = securityAnswerList;
		}
		@Override
    	public void run()
        {
			try {
    			// ��װ�������
    			TreeMap paraMap = new TreeMap();
    			paraMap.put("loginKey", loginKey);
    			WebServiceManager.setObjectArrayParameter(paraMap, securityAnswerList, null,"securityAnswerList");
    			RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USER_SECURITY, "retrievePassword", null, paraMap,false);
    			WebServiceManager wsm = new WebServiceManager(requestParameter);
                WSBaseResult wSBaseResult = (WSBaseResult)wsm.execute();  
                Object obj = wSBaseResult.object;
                String message = null;
                String code = null;

                
                if (obj != null && obj instanceof SoapObject)
                {
                	
                	SoapObject so = (SoapObject)((SoapObject)obj).getProperty(0);
                	code = so.hasProperty("code") ? so.getProperty("code").toString():"-1";
                	if (code.equals("0"))
                	{
                		cc = so.hasProperty("cc") ? so.getProperty("cc").toString():"";
                		password = so.hasProperty("password") ? so.getProperty("password").toString():"";
                		message = resources.getString(R.string.uc_retrieved_password_success_and_new) + password;
                		
                	}
                	else
                	{
                		message = UserCenterCommon.getWebserviceResponseMessage(resources, new Integer(code));               		
                	}
                }
                else
                {
                	message = UserCenterCommon.getWebserviceResponseMessage(resources, new Integer(-1));
                }
                if (wSBaseResult.responseCode == 0)
                {
                	// ���óɹ�
        			mHandler.obtainMessage(WS_RESPONSE_RETRIEVED_PASSWORD, 
        					new Integer(code), 0, message)
        					.sendToTarget();
                }
                // �����쳣
                else 
                {
                	stopProgressDialog();
        		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
    		        mHandler.obtainMessage(WS_RESPONSE_RETRIEVED_PASSWORD, 
    		        		UsercenterConstants.RESULT_EXCEPTION, 0, message)
    		        .sendToTarget();
                }
    		}
    		catch (Exception e)
    		{
    		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
            	stopProgressDialog();
    			// ֪ͨUI���̵߳�¼���
		        mHandler.obtainMessage(WS_RESPONSE_RETRIEVED_PASSWORD, 
		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
		        .sendToTarget();
    		}	
        }
	}

	private void reset()
	{
		SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
		loginSP.edit().remove(loginKey).commit();
		loginSP.edit().putString(loginKey, password)
		.commit(); // ��¼��¼
	}
	/**
	 * ������������
	 * @author xiangyuanmao
	 *
	 */
	class SetPasswordQuestionThread extends Thread
	{
	    List <SecurityAnswerDTO> securityAnswerList;
		SetPasswordQuestionThread(List<SecurityAnswerDTO> securityAnswerList)
		{
				this.securityAnswerList = securityAnswerList;
		}
		@Override
    	public void run()
        {
			try {
    			// ��װ�������
    			TreeMap paraMap = new TreeMap();
    			paraMap.put("cc", MyCarActivity.cc);
    			WebServiceManager.setObjectArrayParameter(paraMap, securityAnswerList, null,"securityAnswerList");
    			RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USERCENTER, "setupSecurityInfos", null, paraMap,true);
    			WebServiceManager wsm = new WebServiceManager(requestParameter);
                WSBaseResult wSBaseResult = (WSBaseResult)wsm.execute();  
                Object obj = wSBaseResult.object;
                String message = null;
                String code = null;
                if (obj != null && obj instanceof SoapObject)
                {
                	SoapObject so = (SoapObject)((SoapObject)obj).getProperty(0);
                	code = so.hasProperty("code") ? so.getProperty("code").toString():"-1";
                	if (code.equals("0"))
                	{
                		message = resources.getString(R.string.uc_set_password_question_success);                		
                	}
                	else
                	{
                		message = UserCenterCommon.getWebserviceResponseMessage(resources, new Integer(code));
                	}
                }
                else
                {
                	message = UserCenterCommon.getWebserviceResponseMessage(resources, new Integer(-1));
                }
                
                if (wSBaseResult.responseCode == 0)
                {
                	// ���óɹ�
                	mHandler.obtainMessage(WS_RESPONSE_SET_QUESTION, 
                			new Integer(code), 0, message)
                			.sendToTarget();
                	
                }
                // �����쳣
                else 
                {
                	stopProgressDialog();
        		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
        		    if (message != null)
        		    {
        		    	
        		    	mHandler.obtainMessage(WS_RESPONSE_SET_QUESTION, 
        		    			UsercenterConstants.RESULT_EXCEPTION, 0, message)
        		    			.sendToTarget();
        		    }
        		    else
        		    {
        		    	mHandler.obtainMessage(WS_RESPONSE_SET_QUESTION, 
        		    			UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
        		    			.sendToTarget();
        		    	        		    	
        		    }
                }
    		}
    		catch (Exception e)
    		{
    		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
            	stopProgressDialog();
    			// ֪ͨUI���̵߳�¼���
		        mHandler.obtainMessage(WS_RESPONSE_SET_QUESTION, 
		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
		        .sendToTarget();
    		}	
    	}
	}
	/**
	 * ͬ������˵�������ʾ�����б�����
	 */
	private void syncQuestionListFromService()
	{
		
        new Thread()
        {
            @Override
        	public void run()
            {
				try {
	    			// ��װ�������
	    			TreeMap paraMap = new TreeMap();
	    			paraMap.put("language", language);
	    			RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USER_SECURITY, "getSecurityQuestionListByLang", null, paraMap,false);
	    			WebServiceManager wsm = new WebServiceManager(requestParameter);
                    SoapObject result;
                    WSBaseResult wSBaseResult = (WSBaseResult)wsm.execute();  
                    if (wSBaseResult.responseCode == 0)
                    {
            			if (wSBaseResult.object instanceof SoapObject)
            			{
            				result = (SoapObject)wSBaseResult.object;
    		    			if (result != null && result.getProperty(0) != null)
    		    			{
    		    				
    		    				SoapObject so = (SoapObject)result.getProperty(0);
    		    				if (so != null && so.getPropertyCount() > 0)
    		    				{
    		    					
    		    					for (int i = 1; i < so.getPropertyCount(); i++)
    		    					{
    		    						SoapObject question = (SoapObject)so.getProperty(i);
    		    						if (question != null)
    		    						{
    		    							if (question.hasProperty("questionId"))
    		    							{

    		    								questionMap.put(question.getProperty("questionId"), question.getProperty("questionDesc"));
    		    							}
    		    						}
    		    					}
    		    				}
    		    				// ��ʾ����������ʾ�б�
    		    				mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, wSBaseResult.responseCode, 0, questionMap).sendToTarget();;
    	        			}
    	        			else
    	        			{
    	        		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
    	        		        		UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
    	        		        .sendToTarget();
    	        			}
            			}
                    }
                    // ����IO�쳣
                    else if (wSBaseResult.responseCode == 2)
                    {
                    	stopProgressDialog();
            		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
        		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
        		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
        		        .sendToTarget();
                    }
                    // ����xml�����쳣
                    else if (wSBaseResult.responseCode == 3)
                    {
                    	stopProgressDialog();
            		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣʱ����Xml�����쳣");
            			// ֪ͨUI���̵߳�¼���
        		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
        		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
        		        .sendToTarget();
                    }
        		}
        		catch (Exception e)
        		{
        		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
                	stopProgressDialog();
        			// ֪ͨUI���̵߳�¼���
    		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
    		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
    		        .sendToTarget();
        		}	
        	}
         }.start();
	 }
	
	 // ������Ϣ
	 private   final Handler mHandler = new Handler() 
	 {
       @Override
       public void handleMessage(Message msg) 
       {
    	   stopProgressDialog();
           switch (msg.what) 
           {
           case WS_RESPONSE_RETRIEVED_PASSWORD:
        	   switch (msg.arg1)
        	   {
        	   case UsercenterConstants.RESULT_SUCCESS:
        		   dialog(msg.obj.toString(),true);
        		   break;
        	   case UsercenterConstants.RESULT_FAIL:
        		   dialog(msg.obj.toString(),false);
        		   break;
    		   default:
    			   dialog(msg.obj.toString(),false);
    			   break;
        	   }
        	
        	   break;
           case WS_RESPONSE_SET_QUESTION: // ������������
        	   switch (msg.arg1)
        	   {
        	   case UsercenterConstants.RESULT_SUCCESS:
        		   dialog(msg.obj.toString(),true);
        		   break;
        	   case UsercenterConstants.RESULT_FAIL:
        		   dialog(msg.obj.toString(),false);
        		   break;
    		   default:
    			   dialog(msg.obj.toString(),false);
        			   break;
        	   }
        	 //  dialog(msg.obj.toString(),false); 
        	   break;
           case WS_RESPONSE_GETQUESTION_LIST: // ��ȡ���������б�
        	 
        	   switch(msg.arg1)
        	   {
        	   case UsercenterConstants.RESULT_SUCCESS:
        		   
        		   // ͬ���ɹ�
        		   keySet = questionMap.keySet();
        		   adapterQuestion.clear();
        		   adapterQuestion.add(resources.getString(R.string.uc_select_question));
        		   if (keySet != null && keySet.size() > 0)
        		   {
        			    for (Object object : keySet) 
        			    {
        				     adapterQuestion.add(questionMap.get(object).toString());
						
					    }
        		   }
        		   else
        		   {
        		       if (original == ACTIVITY_LOGIN)
        		       {
        		           dialog(resources.getString(R.string.usercenter_have_not_setting_quesetion), true);
        		       }
        		       else
        		       {
        		           dialog(resources.getString(R.string.usercenter_retrieve_password_question_fial), true);
        		       }
        		       return;
        		   }
        		   
        		   adapterQuestion.notifyDataSetChanged();
        		   if (!isSet) // ����Ǵӵ�½���������
        		   {
        			   question1.setSelection(1);
        			   question2.setSelection(2);
        			   question3.setSelection(3);
        			   question1.setEnabled(false);
        			   question2.setEnabled(false);
        			   question3.setEnabled(false);
        		   }
        		   break;
        	   case UsercenterConstants.RESULT_EXCEPTION:
        		   dialog(msg.obj.toString(),false); 
        		   break;
        	   }
           	break;
           }
       }
   };
	/**
	 * �رս��ȶԻ���
	 */
	private void stopProgressDialog()
	{
		if(D) Log.d(TAG, "stopProgressDialog come in ");
		if (pdlg == null)
		{		
			if(D) Log.d(TAG, "pdlg == null ");
			return ;
		}
		else
		{
			if(D) Log.d(TAG, pdlg.toString());
			if (pdlg.isShowing())
			{
				if(D) Log.d(TAG, "pdlg is showing");
				pdlg.dismiss();
			}
		}
	}
	/**
	 * �������ȶԻ���
	 * @param pdlg
	 * @param message
	 */

	private void startProgressDialog(String message)
	{
		if (isOpenProgress)
		{
			if (pdlg == null)
			{
				// ʵ����һ�����ȿ�
				pdlg = new CustomProgressDialog(this);
				pdlg.setTitle(resources.getString(R.string.uc_notice));
				pdlg.setStyle(true);
			}
			else if (pdlg.isShowing())
			{
				pdlg.dismiss();
			}
			pdlg.setMessage(message);
			pdlg.show();
		}
	}
	
	/**
	 * ͨ����¼�ؼ���ȡ���û����õ�������ʾ����
	 * @author xiangyuanmao
	 *
	 */
	class GetUserQuestionThread extends Thread
	{
	    String loginKey;
	    GetUserQuestionThread(String loginKey)
		{
				this.loginKey = loginKey;
	
		}
		@Override
    	public void run()
        {
			try {
    			// ��װ�������
    			TreeMap paraMap = new TreeMap();
    			paraMap.put("loginKey", loginKey);
    			paraMap.put("language", language);
    			RequestParameter requestParameter = new RequestParameter(Constants.SERVICE_USER_SECURITY, "getSecurityQuestionListByLoginKeyAndLang", null, paraMap,false);
    			WebServiceManager wsm = new WebServiceManager(requestParameter);
                SoapObject result;
                WSBaseResult wSBaseResult = (WSBaseResult)wsm.execute();  
                if (wSBaseResult.responseCode == 0)
                {
        			if (wSBaseResult.object instanceof SoapObject)
        			{
        				result = (SoapObject)wSBaseResult.object;
		    			if (result != null && result.getProperty(0) != null)
		    			{
		    				
		    				SoapObject so = (SoapObject)result.getProperty(0);
		    				if (so != null && so.getPropertyCount() > 0)
		    				{
		    					for (int i = 1; i < so.getPropertyCount(); i++)
		    					{
		    						SoapObject question = (SoapObject)so.getProperty(i);
		    						if (question != null)
		    						{
		    							if (question.hasProperty("questionId"))
		    							{

		    								questionMap.put(question.getProperty("questionId"), question.getProperty("questionDesc"));
		    							}
		    						}
		    					}
		    				}
		    				// ��ʾ����������ʾ�б�
		    				mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, wSBaseResult.responseCode, 0, questionMap).sendToTarget();;
	        			}
	        			else
	        			{
	        		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
	        		        		UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, new Integer(-1)))
	        		        .sendToTarget();
	        			}
        			}
                }
                // ����IO�쳣
                else if (wSBaseResult.responseCode == 2)
                {
                	stopProgressDialog();
        		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ����IO�쳣");
    		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
    		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
    		        .sendToTarget();
                }
                // ����xml�����쳣
                else if (wSBaseResult.responseCode == 3)
                {
                	stopProgressDialog();
        		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣʱ����Xml�����쳣");
        			// ֪ͨUI���̵߳�¼���
    		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
    		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
    		        .sendToTarget();
                }

    		}
    		catch (Exception e)
    		{
    		    if (D) Log.d(TAG, "�ӷ�����ͬ���û���Ϣ�����쳣");
            	stopProgressDialog();
    			// ֪ͨUI���̵߳�¼���
		        mHandler.obtainMessage(WS_RESPONSE_GETQUESTION_LIST, 
		        		UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.intenet_invalid))
		        .sendToTarget();
    		}	
    	}
	}
	
	/**
	 * ��������Ի���
	 * @param message
	 */
	protected void dialog(String message,final boolean isClose) 
	{
		
		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
		customAlertDialog.setMessage(message);
		customAlertDialog.setTitle(resources.getString(R.string.uc_notice));
		customAlertDialog.setPositiveButton(
				resources.getString(R.string.manager_ensure), new OnClickListener() {

					@Override
					public void onClick(View v) {
						customAlertDialog.dismiss();
						if (isClose)
						{
							PasswordQuestionActvity.this.finish();
						}
					}
				});
		customAlertDialog.show();
	}
	
}



