package com.cnlaunch.mycar.rescuevehicles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.usercenter.UserCenterCommon;
import com.j256.ormlite.dao.Dao;


/**
 *@author zhangweiwei
 *@version 2011-11-8����4:56:07
 *��˵��
 */
public class EmergencyPhoneActivity extends BaseActivity {
    private Resources resources;
	//---------------------------- ����log��Ϣtarget-------------------------------------
	private static final String TAG = "EmergencyPhoneActivity";
	private static final boolean D = true;
	
	//---------------------------- ����ؼ� ----------------------------------------------
	private Button btnBack;                                              // ���ذ�ť
	private Button btnAdd;                                               // ��ӽ����绰
	private ListView lvEmergencyTele;                                    // �����绰�б�
    private EmergencyTelephoneListAdapter emergencyTelephoneListAdapter; // �����绰�б�������
	private HashMap<String, String> EmergencyTelephoneMap;               // �����绰�б�Ԫ��
	private List<HashMap<String,String>> mData = new ArrayList<HashMap<String, String>>(); //list����ʾ��������Դ	
	private static final String DEPARTMENT_NAME = "departmentName";
	private static final String DEPARTMENT_TELEPHONE = "departmentTelephone";
	private static  String[] DEPARTMENT_NAME_LIST ;
    List<EmergencyTelephone> etList;
    String language;
	private static  String[] DEPARTMENT_TELEPHONE_LIST ;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	language = Locale.getDefault().getLanguage();
        resources = getResources();
        if (language.equals("zh"))
        {
            DEPARTMENT_NAME_LIST = resources.getStringArray(R.array.rescue_vehicle_emergency_phone);
            DEPARTMENT_TELEPHONE_LIST = new String[]{"110","119","120","122","12395","95119","999","95518","95519"};
        }
        else
        {
            DEPARTMENT_NAME_LIST = new String[]{"911"};
            DEPARTMENT_TELEPHONE_LIST = new String[]{"911"};
        }
    	// ͳһ�ı���������
		setContentView(R.layout.rescue_vehicles_emergency_telephone, R.layout.custom_title);
		setCustomeTitleLeft(R.string.rv_rescue_vehicles);
		setCustomeTitleRight(R.string.rv_emergency_phone);
		
		// ��ʼ�������ؼ�
		initData();
    }

    /**
     * ��ʼ�������ؼ�
     */
	private void initData() {
		// TODO Auto-generated method stub
		btnBack = (Button) findViewById(R.id.btn_back);                      // ���ذ�ť
		// Ϊ���ذ�ť��ӵ����¼�����
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EmergencyPhoneActivity.this.finish();
				
			}
		});
		btnAdd = (Button) findViewById(R.id.btn_add_emergency_telephonse);  // ��ӽ����绰
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addEmergncyTelephone();
			}
		});
		mData.clear();
		lvEmergencyTele = (ListView)findViewById(R.id.lv_emergency_telephone);// �����绰�б�
		for (int i = 0; i < DEPARTMENT_NAME_LIST.length; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DEPARTMENT_NAME, DEPARTMENT_NAME_LIST[i]);
			map.put(DEPARTMENT_TELEPHONE, DEPARTMENT_TELEPHONE_LIST[i]);
			mData.add(map);
		}
		emergencyTelephoneListAdapter = new EmergencyTelephoneListAdapter(this, mData);
		lvEmergencyTele.setAdapter(emergencyTelephoneListAdapter);
		Dao<EmergencyTelephone, Integer> dao = getHelper().getDao(EmergencyTelephone.class);
		try {
			etList = dao.queryForAll();
			if (etList != null && etList.size() > 0)
			{
				for (int i = 0; i < etList.size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(DEPARTMENT_NAME, etList.get(i).getLabel());
					map.put(DEPARTMENT_TELEPHONE, etList.get(i).getValue());
					mData.add(map);
				}
				emergencyTelephoneListAdapter.notifyDataSetChanged();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �޸Ľ�����ϵ��
	 * @param name ��ϵ������
	 * @param number ��ϵ�˺���
	 * @param index �ü�¼������
	 * @since DBS V100
	 */
    private void updateEmergncyTelephone(final String name,final String number,final int index)
    {
        if (index < DEPARTMENT_TELEPHONE_LIST.length)
        {
            return;
        }
        final Dao<EmergencyTelephone, Integer> dao = getHelper().getDao(EmergencyTelephone.class);
        LayoutInflater layoutInflater  = LayoutInflater.from(EmergencyPhoneActivity.this);
        final View convertView = layoutInflater.inflate(R.layout.rescue_vehicles_emergency_contact_editor2, null);
        // ͨ���Ի������ʽ�ṩ�޸��û�����
        final CustomDialog customDialog=new CustomDialog(EmergencyPhoneActivity.this);
        customDialog.setTitle(resources.getString(R.string.rescue_vehicle_update_emergency_phone));                               // ����
        customDialog.setIcon(android.R.drawable.ic_dialog_info);            // ͼƬ
        customDialog.setView(convertView); // �༭��
        Button btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
        Button btnEnsure = (Button) convertView.findViewById(R.id.btn_ensure);
        Button btnCancel = (Button) convertView.findViewById(R.id.btn_cancel);
        final EditText etName = (EditText)convertView.findViewById(R.id.et_rv_contact_name);             // �༭��ϵ������
        final EditText etNumber = (EditText)convertView.findViewById(R.id.et_rv_contact_telephone);      // �༭�绰����
        
        // �����û���Ϣ
        etName.setText(name);
        etNumber.setText(number);
        btnEnsure.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (number.trim().equals(""))
                {
                    dialog(resources.getString(R.string.rescue_vehicle_ilegal_mobile));
                    return;
                 }
      
                EmergencyTelephone emergencyTelephone = new EmergencyTelephone();
                emergencyTelephone.setId(etList.get(index - DEPARTMENT_TELEPHONE_LIST.length).getId());
                emergencyTelephone.setLabel(etName.getText().toString());
                emergencyTelephone.setValue(etNumber.getText().toString());
                try 
                {
                      // �������ݿ�
                      dao.createOrUpdate(emergencyTelephone);
                      
                      etList.clear();
                      etList = dao.queryForAll();
                      // ˢ��UI
                      HashMap<String, String> map = mData.get(index);
                      map.put(DEPARTMENT_NAME, etName.getText().toString()); 
                      map.put(DEPARTMENT_TELEPHONE, etNumber.getText().toString());
                      emergencyTelephoneListAdapter.notifyDataSetChanged(); // ˢ��UI
                      
              } catch (SQLException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
                customDialog.dismiss();
                
            }
        });
        btnCancel.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                customDialog.dismiss();                
            }
        });
        btnDelete.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                try 
                {
                    EmergencyTelephone emergencyTelephone = new EmergencyTelephone();
                    emergencyTelephone.setId(etList.get(index - DEPARTMENT_TELEPHONE_LIST.length).getId());
                    emergencyTelephone.setLabel(name);
                    emergencyTelephone.setValue(number);
                      // �������ݿ�
                      dao.delete(emergencyTelephone);
                      etList.clear();
                      etList = dao.queryForAll();
                      // ˢ��UI
                      mData.remove(index);
                      emergencyTelephoneListAdapter.notifyDataSetChanged(); // ˢ��UI
                      
              } catch (SQLException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
                customDialog.dismiss();
                
            }
        });

        customDialog.show();
       }
	

    /**
     */
    private void addEmergncyTelephone()
    {
    	final Dao<EmergencyTelephone, Integer> dao = getHelper().getDao(EmergencyTelephone.class);
    	LayoutInflater layoutInflater  = LayoutInflater.from(EmergencyPhoneActivity.this);
    	final View convertView = layoutInflater.inflate(R.layout.rescue_vehicles_emergency_contact_editor, null);
		// ͨ���Ի������ʽ�ṩ�޸��û�����
    	final CustomDialog customDialog=new CustomDialog(EmergencyPhoneActivity.this);
    	customDialog.setTitle(resources.getString(R.string.rescue_vehicle_add_emergency_phone));                               // ����
    	customDialog.setIcon(android.R.drawable.ic_dialog_info);            // ͼƬ
    	customDialog.setView(convertView); // �༭��
    	
    	customDialog.setPositiveButton(resources.getString(R.string.ok),
				new OnClickListener() 
		{
			  @Override
			  public void onClick(View v)
			  {
				  EditText etName = (EditText)convertView.findViewById(R.id.et_rv_contact_name);             // �༭��ϵ������
				  EditText etNumber = (EditText)convertView.findViewById(R.id.et_rv_contact_telephone);      // �༭�绰����
				  // �����û���Ϣ
				  String name = etName.getText().toString().trim();
				  String number = etNumber.getText().toString().trim();
				  
				  if (number.trim().equals(""))
				  {
					  dialog(resources.getString(R.string.rescue_vehicle_ilegal_mobile));
					  return;
				   }
		
				  EmergencyTelephone emergencyTelephone = new EmergencyTelephone();
				  emergencyTelephone.setLabel(name);
				  emergencyTelephone.setValue(number);
				  try 
				  {
				        // �������ݿ�
					    dao.createOrUpdate(emergencyTelephone);
					
						// ˢ��UI
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(DEPARTMENT_NAME, name);
						map.put(DEPARTMENT_TELEPHONE, number);
						mData.add(map);
						etList.clear();
						etList = dao.queryForAll();
						emergencyTelephoneListAdapter.notifyDataSetChanged(); // ˢ��UI
			          	
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  customDialog.dismiss();
			}
		})
		.setNegativeButton(resources.getString(R.string.cancel),  new OnClickListener() 
		  {
			  @Override
			  public void onClick(View v) {
				  customDialog.dismiss();
			  }
		  });
    	customDialog.show();
	   }

		/**
		 * ��¼�쳣�����Ի���
		 * @param message
		 */
		protected  void dialog(String message) 
		{
			final CustomDialog customDialog=new CustomDialog(EmergencyPhoneActivity.this);
			customDialog.setMessage(message); // ������Ϣ
			customDialog.setTitle(resources.getString(R.string.uc_notice));
			customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok), new OnClickListener() 
			  {
				  @Override
				  public void onClick(View v) {
					  customDialog.dismiss();
				  }
			  });
			customDialog.show();
		 }	
  

	/**
	 * �Զ����б�����������ʾ�����绰�б� 
	 * @author xiangyuanmao
	 *
	 */
    private class EmergencyTelephoneListAdapter extends BaseAdapter
	{
    	private Context context;
    	private LayoutInflater layoutInflater;
    	private List<HashMap<String,String>> data;//����Դ
    	public EmergencyTelephoneListAdapter(Context context,List<HashMap<String,String>> data){
    		layoutInflater = LayoutInflater.from(context);
    		this.context = context;
    		this.data = data;
    	}

    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return data.size();
    	}
    	@Override
    	public Object getItem(int position) {
    		// TODO Auto-generated method stub
    		return data.get(position);
    	}
    	@Override
    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return position;
    	}
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		convertView = layoutInflater.inflate(R.layout.rescue_vehicles_emergency_contact_list_item, null);
			TextView tv_name = (TextView)convertView.findViewById(R.id.rescue_vehicles_emergency_phone_name);
			final TextView tv_number = (TextView)convertView.findViewById(R.id.rescue_vehicles_emergency_phone_number);
			ImageView img_phone = (ImageView)convertView.findViewById(R.id.rescue_vehicles_emergency_phone_call);
			LinearLayout phone = (LinearLayout) convertView.findViewById(R.id.phone_ll);
			tv_name.setText(data.get(position).get(EmergencyPhoneActivity.DEPARTMENT_NAME));
			tv_number.setText(data.get(position).get(DEPARTMENT_TELEPHONE));
			final int index = position;
			tv_number.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    updateEmergncyTelephone(data.get(index).get(EmergencyPhoneActivity.DEPARTMENT_NAME),
                        data.get(index).get(DEPARTMENT_TELEPHONE),
                        index);
                    
                }
            });
			phone.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//������绰��ͼ����ڲ����绰֮ǰ��һ��ȷ������ʾ
					final CustomDialog customDialog=new CustomDialog(EmergencyPhoneActivity.this);
					customDialog.setTitle(resources.getString(R.string.uc_notice));
					customDialog.setMessage(resources.getString(R.string.rescue_vehicle_ensure_call)+tv_number.getText().toString()+"?");
					customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok), new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tv_number.getText().toString()));
	    				    startActivity(intent);								
						}
					});
					customDialog.setNegativeButton(resources.getString(R.string.bluetoothconnect_input_cancel),  new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							customDialog.cancel();
						}							
					});  
					customDialog.show();
				}
			});   		
		   return convertView;
    	}
	}

}
