package com.cnlaunch.mycar;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.manager.UserSettingsActivity;
import com.cnlaunch.mycar.updatecenter.DiagSoftConfigureActivity;
import com.cnlaunch.mycar.updatecenter.LocalUpdateManager;
import com.cnlaunch.mycar.updatecenter.UpdateCenterMainActivity;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;

/**
 * 
 * <功能简述> 设置界面，统一所有模块设置功能 <功能详细描述> 界面是一个listView，各自的模块添加到本界面，从本界面链接过去
 * 
 * @author xiangyuanmao
 * @version 1.0 2012-6-13
 * @since DBS V100
 */
public class SettingActivity extends BaseActivity {
	private ArrayList<MenuListItem> mMenuListItems;
	private ListView mMenuList = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_center_main_page, R.layout.custom_title);
		setCustomeTitleLeft(R.string.setting_titile);
		setCustomeTitleRight("");
		initViews();
	}

	/**
	 * 初始化listView
	 * 
	 * @since DBS V100
	 */
	private void initViews() {
		mMenuListItems = new ArrayList<MenuListItem>();

		/*
		 * 请各位同事仿照这个例子添加自己的模块，不要忘记在MenuItemClickListener.java类中 添加自己的点击事件
		 */
		if (MyCarActivity.isLogin)
		{
		    
		    mMenuListItems.add(new MenuListItem(R.drawable.ic_setting_manager,
		    getResources().getText(R.string.car_setting).toString(), R.drawable.upc_list_triangle));
		}
//        mMenuListItems.add(new MenuListItem(
//            R.drawable.upc_diag_card_update_icon,
//            getString(R.string.upc_device_update),
//            R.drawable.upc_list_triangle
//            ));
		mMenuList = (ListView) findViewById(R.id.upc_main_menu_list);
		mMenuList.setAdapter(new MenuListAdpater(this, mMenuListItems));
		mMenuList.setOnItemClickListener(new MenuItemClickListener());

	}

	/**
	 * 元素的点击事件 <功能简述> <功能详细描述>
	 * 
	 * @author xiangyuanmao
	 * @version 1.0 2012-6-13
	 * @since DBS V100
	 */
	private class MenuItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (view.getId()) {
			case R.drawable.ic_setting_manager:// 
				startActivity(new Intent(SettingActivity.this,
						UserSettingsActivity.class));
				break;
            case R.drawable.upc_diag_card_update_icon:// 
                startActivity(new Intent(SettingActivity.this,LocalUpdateManager.class));
                break;
			default:
				break;
			}// end switch
		}
	}

	private class MenuListItem {
		int mIconId;
		String mText;
		int mTriangle;
		public int getIconId()
		{
		    return this.mIconId;
		}
		public MenuListItem(int mIconId, String mText, int mTriangle) {
			this.mIconId = mIconId;
			this.mText = mText;
			this.mTriangle = mTriangle;
		}
	}

	private class MenuListAdpater extends BaseAdapter {
		Context mCont;
		LayoutInflater mInflator;
		ArrayList<MenuListItem> mData;

		@Override
		public int getCount() {
			return mData.size();
		}

		public MenuListAdpater(Context cont, ArrayList<MenuListItem> data) {
			this.mCont = cont;
			this.mData = data;
			mInflator = LayoutInflater.from(mCont);
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mData.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflator.inflate(
						R.layout.update_center_menulist_item, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.update_center_menu_item_icon);
			icon.setBackgroundResource(mData.get(position).mIconId);
			TextView menuText = (TextView) convertView
					.findViewById(R.id.update_center_menu_item_text);
			menuText.setText(mData.get(position).mText);

			ImageView trangle = (ImageView) convertView
					.findViewById(R.id.update_center_menu_triangle);
			trangle.setBackgroundResource(mData.get(position).mTriangle);
			convertView.setId(mData.get(position).getIconId());
			return convertView;
		}
	}
}
