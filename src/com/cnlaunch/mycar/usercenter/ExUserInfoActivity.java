package com.cnlaunch.mycar.usercenter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.usercenter.database.ExUser;
import com.cnlaunch.mycar.usercenter.database.UsercenterDao;
import com.j256.ormlite.dao.Dao;

/**
 * @description �û���չ��Ϣ
 * @author ��Զï
 * @date��2012-4-16
 */
public class ExUserInfoActivity extends BaseActivity {
    // ����log��Ϣtarget
    private static final String TAG = "ExUserInfoActivity";
    private static final boolean D = true;
    
    Resources resources;
    TextView tvUsername;
    TextView tvEmail;
    Button btnSync;
	private ListView listView; // �豸��Ϣ���б�ListView����
	private ExUserInfoAdapter adapter; // �豸��Ϣ�б��Adapter
	private ArrayList<HashMap<String,Object>> exUserinfoList = new ArrayList<HashMap<String,Object>>(); // �豸��Ϣ����
	private String[] labels ;
	Dao<ExUser, Integer> exUserDao;
	ProgressDialog pdlg; // �Ի��򣬵�������ͨ��ʱ���ڸ����û�����Ľ��ȶԻ���
    private boolean isOpenProgress = false;   // �Ƿ�򿪽��ȿ�
	ExUser exUser;
	/**
	 * �����˻���ķ���
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_ex_info, R.layout.custom_title);
		setCustomeTitleLeft(R.string.ex_userinfo);
		setCustomeTitleRight("");
		resources = getResources();
		labels = resources.getStringArray(R.array.ex_userinfo_labels);
		isOpenProgress = true;
		if (MyCarActivity.isLogin)
		{
			TreeMap map = new TreeMap();
			map.put("cc", MyCarActivity.cc);
			new SyncExUserInfoFromWSThread(map, mHandler, ExUserInfoActivity.this).start();
			startProgressDialog(resources.getString(R.string.uc_syncing_ex_userinfo_from_ws));
		}
		else
		{
			Intent intent = new Intent(ExUserInfoActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}

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
	/**
	 * ��ʼ���б�
	 */
	private void initViews(){
    	
		/*
		 *UIԪ�س�ʼ�� 
		 */
		listView = (ListView)this.findViewById(R.id.lv_ex_userinfo); // ��ȡListView������
    	exUserinfoList = new ArrayList<HashMap<String,Object>>();   // ��ʼ���洢�û���Ϣ��ArrayList
    	btnSync = (Button) findViewById(R.id.uc_sync_to_ws);
    	btnSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startProgressDialog(resources.getString(R.string.uc_syncing_ex_userinfo_to_ws));
				TreeMap paraMap = new TreeMap();
				paraMap.put("userExtDTO", exUser.getUserExtInfo());
				new SyncExUserInfoToWSThread(paraMap, mHandler, ExUserInfoActivity.this).start();
			}
		});
    	List<ExUser> exUserList; 
    	try {
    		exUserList = exUserDao.queryForAll(); 
	    	if (exUserList != null && exUserList.size() > 0)
	    	{
	    		exUser = exUserList.get(0);
	    		for (int i =0; i < exUser.getCount(); i++)
	    		{	    			
	    			HashMap<String,Object> userMap = new HashMap<String,Object>();
	    			userMap.put(UsercenterConstants.USERCENTER_USERINFO_LABEL, labels[i]);
	    			userMap.put(UsercenterConstants.USERCENTER_USERINFO_VALUE, exUser.getPropertyByIndex(i));
	    			exUserinfoList.add(userMap);
	    		}
	    	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    	// ��ʼ��Adapter
    	adapter = new ExUserInfoAdapter(this,exUserinfoList);
    	
    	// ����Adapter��ListView����ʱ�û���Ϣ�б���ֵ��������ʾ����Ļ��Ҳ����ͨ������adapterˢ����Ļ
    	listView.setAdapter(adapter); 

    }
	/**
	 * �û���ϢAdapter����Ҫ���ڼ����û���ϸ��Ϣ
	 * @author xiangyuanmao
	 *
	 */
    class ExUserInfoAdapter extends BaseAdapter{
    	private Context context;
    	private LayoutInflater inflater;
    	private ArrayList<HashMap<String,Object>> listItems;
    	
    	public ExUserInfoAdapter(Context c,ArrayList<HashMap<String,Object>> list){
    		context = c;
    		inflater = LayoutInflater.from(c);
    		listItems = list;
    	}
		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
         
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final TextView tvLabel; // ��ǩ
			final TextView tvValue; // ����
			final int item = position;
			ImageView ivEdit;
			if (view == null)
			{
				view = this.inflater.inflate(R.layout.usercenter_lv_userinfo, null);
			}
			tvLabel = (TextView) view.findViewById(R.id.usercenter_tv_userinfo_label);
			tvValue = (TextView) view.findViewById(R.id.usercenter_tv_userinfo_value);
			ivEdit = (ImageView) view.findViewById(R.id.userinfo_edit);
			tvLabel.setText(listItems.get(position).get(UsercenterConstants.USERCENTER_USERINFO_LABEL).toString());
			Object value = listItems.get(position).get(UsercenterConstants.USERCENTER_USERINFO_VALUE);
			tvValue.setText(value == null ? "" : value.toString());
//			if (item == 13)
//			{
//				ivEdit.setVisibility(View.INVISIBLE);
//			}
			ivEdit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showUpdate(labels[item], item) ;
				}
			});
			return view;
		}
    	
    }
    /**
	 * ��ʾ�޸���ʾ
	 */
	protected void showUpdate(String attName,final int position) 
	{

		String label = labels[position];
		Object value = exUserinfoList.get(position).get(UsercenterConstants.USERCENTER_USERINFO_VALUE);
		final String strValue = value == null ? "" : value.toString(); // ��ʾֵ
		final EditText etValue = new EditText(ExUserInfoActivity.this); // ʵ����һ���༭�����ڽ����û��޸ĵ���Ϣ
		etValue.setText(strValue);
		etValue.setBackgroundDrawable(resources.getDrawable(R.drawable.main_edit));
		final CustomDialog customDialog = new CustomDialog(this);
		customDialog.setTitle(label); // ����
		customDialog.setIcon(android.R.drawable.ic_dialog_info); // ͼƬ
		customDialog.setView(etValue); // �༭��
		customDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �����û���Ϣ
				Object value = etValue.getText();
				if(value == null || value.equals(""))
				{
					dialog(labels[position] + resources.getString(R.string.must_be_not_blank));
				}
				else
				{
					customDialog.dismiss();
					updateDatabase(position, value.toString());
				}
			}
		});
		customDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
			}
		});
		customDialog.show();
		   
	}
	/**
	 * �������ݿ�
	 * @param itemId
	 * @param label
	 * @param value
	 * @param updatePosition
	 * @param userAttName
	 */
	private void updateDatabase(int position, String value)
	{

		exUser.setPropertyByIndex(position, value);
		try 
		{
			// �������ݿ�
			exUserDao.createOrUpdate(exUser);
			
			// ˢ��UI
			HashMap<String,Object> item = exUserinfoList.get(position);
			HashMap<String,Object> newItem = new HashMap<String, Object>();
		    newItem.put(UsercenterConstants.USERCENTER_USERINFO_LABEL, labels[position]);
			newItem.put(UsercenterConstants.USERCENTER_USERINFO_VALUE, value);
			exUserinfoList.remove(item); // ���б������Ƴ�
			exUserinfoList.add(position, newItem); // ���Ƴ���λ���������
//			listView.setAdapter(adapter); // ˢ��UI
			adapter.notifyDataSetChanged();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	            case UsercenterConstants.SYNC_USERINFO_TO_SERVICE_RESULT: // ͬ�����ݵ�������
	            	
                	dialog(msg.obj.toString());
	            	break;
	            case UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT: // �ӷ����ͬ���û���Ϣ���ֻ�
	                switch(msg.arg1)
	                {
	                case UsercenterConstants.RESULT_SUCCESS: // ��ȡ�û����ϳɹ�
	                	
	                	// ��ô�WebService���ص��û���Ϣ�������
	                	ExUser wsUser = (ExUser)msg.obj;
	                	
	                	// ˢ�±������ݿ��е�User��
	                	exUserDao = getHelper().getDao(ExUser.class);
	                	UsercenterDao.updateExUser(exUserDao,wsUser); 
	                	
	                    // ˢ��UI
	                	initViews();
	                    break;
	                case UsercenterConstants.RESULT_FAIL: // ��ȡ�û�����ʧ��
	                	// �����Ի�����ʾ�û�ʧ��ԭ��
	
	                	dialog(resources.getString(R.string.usercenter_service_exception));
	                	break;
	                case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
	                	// �����Ի�����ʾ�û�ʧ��ԭ��
	                    dialog(resources.getString(R.string.usercenter_service_exception));
	                	break;
	                }
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
					pdlg = new ProgressDialog(this);
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
		 * ��¼�쳣�����Ի���
		 * @param message
		 */
		protected void dialog(String message) 
		{
			
			final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
			customAlertDialog.setMessage(message);
			customAlertDialog.setTitle(resources.getString(R.string.uc_notice));
			customAlertDialog.setPositiveButton(
					resources.getString(R.string.manager_ensure), new OnClickListener() {

						@Override
						public void onClick(View v) {
							customAlertDialog.dismiss();
						}
					});
			customAlertDialog.show();
		}
}



