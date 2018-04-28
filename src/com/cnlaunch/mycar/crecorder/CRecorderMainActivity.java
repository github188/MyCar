package com.cnlaunch.mycar.crecorder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
/**
 * crecorder���ܴ���
 * 
 * @author huanglixin
 * 
 */
public class CRecorderMainActivity extends BaseActivity implements OnClickListener{
	/** Called when the activity is first created. */
	private final static String tag = "crecorderMain";
	ArrayList<RowView> mListViewData;
	AlertDialog mDataSynOpsDialog = null;
	int mSyncOpsSelectedIndex;
	boolean D = true;//for debug
	int long_pressed_index;//for listview
	ListView lv_crecorder_file;
	TextView tv_sdcard_data_not_found;
	LinearLayout tips_block;
	Button   bt_refresh_file_list;
	File working_dir;//����Ŀ¼
	File sdPath = Environment.getExternalStorageDirectory();//sdcardĿ¼
	private static final String workingPath = "cnlaunch/mycar/crecorder/data";
    Dialog dlg = null;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��ֹ��Ļ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		 ȫ��Ļ
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.crecorder_layout,R.layout.custom_title);
        setCustomeTitleLeft("��ϻ��");
        setCustomeTitleRight("");
        initViews();
        loadFileList();
   }//endof onCreate()
    
    /**@author luxingsong
     * ��ʼ������ؼ�
     * */
    private void initViews()
    {
		 Button bt_crecorder_back=(Button)findViewById(R.id.crecorder_main_goback);
	     Button bt_crecorder_getdata=(Button)findViewById(R.id.crecorder_main_bt_comm);
	     Button bt_crecorder_data_sync=(Button)findViewById(R.id.crecorder_main_data_sync);
	     bt_crecorder_back.setOnClickListener(this);
	     bt_crecorder_getdata.setOnClickListener(this);
	     bt_crecorder_data_sync.setOnClickListener(this);
	        	         
	     tv_sdcard_data_not_found = (TextView)findViewById(R.id.crecorder_sdcard_data_not_found);
	     tips_block = (LinearLayout)findViewById(R.id.crecorder_center_tip_block);
	     bt_refresh_file_list = (Button) findViewById(R.id.crecoder_main_bt_refresh);
	     bt_refresh_file_list.setOnClickListener(new OnClickListener()
		 {
			@Override
			public void onClick(View v)
			{
				loadFileList();//���¼����ļ��б�
			}
		 });
	     // �ļ��б�ؼ�
	     lv_crecorder_file=(ListView)findViewById(R.id.crecorder_lv);
	     registerForContextMenu(lv_crecorder_file);
	     mListViewData = new ArrayList<RowView>();
	     FileListAdapter adapter = new FileListAdapter(this,mListViewData);
	     lv_crecorder_file.setAdapter(adapter);
	 	 lv_crecorder_file.setOnItemClickListener(new OnItemClickListener()
	 	 {
	 		@Override
	 		public void onItemClick(AdapterView<?> view, View arg1, int position,
	 				long arg3) 
	 		{
	 		}
	 	 });
	 	 lv_crecorder_file.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// TODO Auto-generated method stub
				long_pressed_index = position;//���ݳ�����index
				return false;
			}
		});
    }
    
    /**
     * ��ȡ�ļ�ListAdapter
     * */
    private FileListAdapter getFileListAdapter()
    {
    	return (FileListAdapter) lv_crecorder_file.getAdapter();
    }
   /**@author luxingsong
    * ��ʼ�������ļ��У��ļ������������
    * ·��:/sdcard/crecorder/data/*.x431
    * --local  �����ļ�
    * --server �������ļ�
    * --bluetooth �����ļ�
    ***/ 
   private void loadFileList()
   {
	    working_dir =  new File(sdPath.toString()+File.separator+workingPath);
		FilenameFilter x431Filefilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				Log.e(tag,"��·��:"+dir.getPath()+"��·��:"+name);
				File cur = new File(dir,name);
				if(cur.isFile()&& name.indexOf(".x431")!=-1){
					return true;
				}else{
					return false;
				}
			}
		};
    	if(working_dir.exists())
    	{
    		if(lv_crecorder_file!=null)
    		{
    			FileListAdapter adp = (FileListAdapter) lv_crecorder_file.getAdapter();
    			File[] files = working_dir.listFiles(x431Filefilter);//Ŀ¼�µ��ļ��б�
    			if(files.length > 0)
    			{
    				for(File f : files)
    				{		
    					Log.e(tag,"load file list :"+f.getName());
    					adp.addDataItemFromHead(f);
    				}
    			}else//�ļ�����û�������ļ�
    			{
    			}
    		}
    	}else//·��������,ѯ���û��Ƿ񴴽���ͬ����
    	{
    		Log.e(tag,"/sdcard/mycard/crecorder/data �ļ��в� ����");
    		working_dir.mkdirs();
//    		File newfile = new File(working_dir.toString()+File.separator+"V2.x431");
//    		try {
//				newfile.createNewFile();
//			} catch (IOException e) {
//				Log.e(tag,"����  /sdcard/mycard/crecorder/data/v2.x431�ļ�ʧ��"+e.getMessage());
//			}
//    		Log.e(tag,"����  /sdcard/mycard/crecorder/data �ļ���");
    	}
   }
   
   /**
    * @author luxingsong
    * ��ǰ����Ŀ¼���Ƿ��������ļ�
    * */
   private boolean dataFileExist()
   {
	   return working_dir.listFiles().length > 0;
   }
   
  /**
   * x431�ļ������������Ĳ˵�
   */
	@Override
   public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo)
   {
	// TODO Auto-generated method stub
	switch(v.getId())
	{
		case R.id.crecorder_lv:
			MenuInflater inf = getMenuInflater();
			menu.setHeaderTitle("�ļ�����");
			inf.inflate(R.menu.crecorder_x431ops_context_menu, menu);
			break;
		default:
			break;
	}
	super.onCreateContextMenu(menu, v, menuInfo);
  }
	
/**
 * �����Ĳ˵��¼�����
 * */
@Override
public boolean onContextItemSelected(MenuItem item)
{
	// TODO Auto-generated method stub
	    Intent intent=null;
		switch(item.getItemId())
		{
			case R.id.crecorder_context_menu_check_file://�鿴�ļ���Ϣ
	 			intent=new Intent(CRecorderMainActivity.this,CRecorderDataLayoutActivity.class);
	 			RowView row = (RowView)getFileListAdapter().getItem(long_pressed_index);
	 			intent.putExtra("file", row.getFileName());
	 			startActivity(intent);
				break;
			case R.id.crecorder_context_menu_rename_file://�������ļ�
				getFileListAdapter().renameFileItemAt(long_pressed_index, "Renamed File.x431");
				break;
			case R.id.crecorder_context_menu_delete_file://ɾ���ļ�
				getFileListAdapter().deleteFileItemAt(long_pressed_index);
				break;
			case R.id.crecorder_context_menu_upload_file://�ϴ���ǰ�������ļ�
				break;
			default:
				break;
		}
	    return super.onContextItemSelected(item);
}

/**@author luxingsong
 * �ϴ������ļ���ͬ���������ͱ��ص�����
 * */
private void dataSyncOperations()
{
	if(!dataFileExist())return;
	final CharSequence[] listItems = new CharSequence[]{"ȫ���ϴ�","�ϴ�ѡ����","ͬ��������"};
	mDataSynOpsDialog = new AlertDialog.Builder(this)
	        .setSingleChoiceItems(listItems, 0, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					mSyncOpsSelectedIndex = which;
				}
			})
			.setTitle("����ͬ��")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					mDataSynOpsDialog.dismiss();
					switch(mSyncOpsSelectedIndex)//data sync operations
					{
						case 0://upload_all
							break;
						case 1://upload_selected items
							break;
						case 2://sync files to local 
							break;
						default:
							break;
					}
				}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					mDataSynOpsDialog.dismiss();
					mSyncOpsSelectedIndex=0;
				}
			})
			.create();
			mDataSynOpsDialog.show();
}

@Override
public void onClick(View v) 
{
	// TODO Auto-generated method stub
	Intent intent = null;
	switch(v.getId())
	{
	case R.id.crecorder_main_goback:
		Log.e(tag,"����MyCarActivity");
		this.finish();
		break;
	case R.id.crecorder_main_bt_comm:
		Log.e(tag,"������ͨ��");
		intent = new Intent(CRecorderMainActivity.this,CRecorderBluetoothOperationActivity.class);
		startActivityForResult(intent, 0);
		break;
	case R.id.crecorder_main_data_sync:
		Log.e(tag,"����ͬ������");
		dataSyncOperations();
		break;
	default:
		break;
	}
}

/**
 * Activity ���ؽ������
 * **/
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(D)Log.e(tag,"requestCode:"+requestCode);
	if(D)Log.e(tag,"resultCode:"+resultCode);
	switch(requestCode)
	{
		case 0://��BluetoothOperation��ȡ���ؽ��
			if(data!=null)
			{
				Bundle b = data.getExtras();
				if(b!=null)
				{
					File ret = (File) data.getExtras().get("return");
					if(ret !=null)
					{
						if(D)Log.e(tag,"Activity ���ؽ��:"+ret);
						FileListAdapter adp = (FileListAdapter) lv_crecorder_file.getAdapter();
						adp.addDataItemAtTail(ret);
					}
				}
			}
			break;
		default:
			break;
	}
}
/**
 * �ļ��б����ʾ�ļ���Ϣ
 * ��:
 * �ļ������ļ���С������ʱ���
 * */
public final class RowView 
{
	private File file;
	public RowView(File f)
	{
		file = f;
	}
	public File getFile()
	{
		return file;
	}
	public void setFileName(File f)
	{
		file = f;
	}
	public String getFileName()
	{
		return file.getName();
	}
	public String getFileAbsolutePath()
	{
		return file.getAbsolutePath();
	}
}
//�ļ��б��������
public class FileListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private ArrayList<RowView> mData = new ArrayList<RowView>();;
	private Context context;
	dataChangeListener dataListener = new SDCardDataListener();//����SDcard������
	public FileListAdapter(Context cont,ArrayList<RowView> data)
	{
		context = cont;
		mData = data;
		this.mInflater = LayoutInflater.from(context);
	}
	public ArrayList<RowView> getDataSet()
	{
		return mData;
	}
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) 
	{
		// TODO Auto-generated method stub
		return arg0;
	}
	public void addDataItemFromHead(File file)
	{
    	RowView row = new RowView(file);
		mData.add(0,row);
		notifyDataSetChanged();
		dataListener.checkDataSize(mData.size());
	}
	public void addDataItemAtTail(File file)
	{
    	RowView row = new RowView(file);
		mData.add(0,row);
		notifyDataSetChanged();
		dataListener.checkDataSize(mData.size());
	}
	private void deleteDataItemAt(int pos)
	{
		if(mData.size()> 0)
		{
			if(pos>=0 && pos<mData.size())
			{
				mData.remove(pos);
				notifyDataSetChanged();
				dataListener.checkDataSize(mData.size());
			}
		}
	}
	public void deleteFileItemAt(int pos)
	{
		if(mData.size()> 0)
		{
			if(pos>=0 && pos<mData.size())
			{
				File f = mData.get(pos).getFile();
				if(D)Log.e(tag,"ɾ���ļ� file:"+f.getAbsolutePath());
				if(f.exists())
				{
					f.delete();//delete file 
				}
				mData.remove(pos);
				notifyDataSetChanged();
				dataListener.checkDataSize(mData.size());
			}
		}
	}
	public void renameFileItemAt(int pos,String name)
	{
		if(mData.size()> 0)
		{
			if(pos>=0 && pos<mData.size())
			{
				File f = mData.get(pos).getFile();
				if(D)Log.e(tag,"�������ļ� file:"+f.getAbsolutePath());
				if(f.exists())
				{
					f.renameTo(new File(name));
					notifyDataSetChanged();
					dataListener.checkDataSize(mData.size());
				}
			}
		}
	}
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView == null) 
		{
			convertView = mInflater.inflate(R.layout.crecorder_item_layout, null);
		}		
		TextView file = (TextView)convertView.findViewById(R.id.crecorder_x431file_name);
		file.setText(mData.get(position).getFileName());
		return convertView;
	}
}
/**
 * ��ӡ������Ϣ
 * */
public void showToast(String what)
{
	Toast.makeText(CRecorderMainActivity.this, what, Toast.LENGTH_LONG);
}
/**@author luxingsong
 * ���ݼ����ӿڣ�FileList����
 * */
public interface dataChangeListener
{
	public void checkDataSize(int size);
}
private class SDCardDataListener implements dataChangeListener
{
	@Override
	public void checkDataSize(int size)
	{
		// TODO Auto-generated method stub
		if(size>0){
			tips_block.setVisibility(View.INVISIBLE);
		}else{//��ʾû�������ļ�
			tips_block.setVisibility(View.VISIBLE);
		}
	}
}
}//end of file
