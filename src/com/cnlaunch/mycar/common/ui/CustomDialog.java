package com.cnlaunch.mycar.common.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;

public class CustomDialog extends Dialog {
	private Context mContext;
	private View mRootView;
	private TextView mTitleView;
	private TextView mContentView;
	private View mNegativeButtonArea;
	private View mPositiveButtonArea;
	private Button mNegativeButton;
	private Button mPositiveButton;
	private ImageView mIcon;
	private ViewGroup mContentArea;

	public CustomDialog(Context context) {
		super(context, R.style.CustomDialog);
		mContext = context;
		LayoutInflater inflater = this.getLayoutInflater();
		mRootView = inflater.inflate(R.layout.custom_dialog, null);
		mTitleView = (TextView) mRootView
				.findViewById(R.id.custom_dialog_title);
		mContentView = (TextView) mRootView
				.findViewById(R.id.custom_dialog_content);
		mNegativeButton = (Button) mRootView
				.findViewById(R.id.custom_dialog_button_negative);
		mPositiveButton = (Button) mRootView
				.findViewById(R.id.custom_dialog_button_positive);
		mNegativeButtonArea = mRootView
				.findViewById(R.id.custom_dialog_button_negative_area);
		mPositiveButtonArea = mRootView
				.findViewById(R.id.custom_dialog_button_positive_area);
		mIcon = (ImageView) mRootView.findViewById(R.id.custom_dialog_icon);
		mContentArea = (ViewGroup) mRootView
				.findViewById(R.id.custom_dialog_content_area);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mRootView);
		setMarginLeftAndRight(30);
	}

	private void setMarginLeftAndRight(int padding) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = mContext.getApplicationContext().getResources()
				.getDisplayMetrics();
		int width = dm.widthPixels - padding;
		this.getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
	}

	protected void onStop() {

	}

	public CustomDialog setTitle(String title) {
		mTitleView.setText(title);
		return this;
	}

	public void setTitle(int stringResId) {
		mTitleView.setText(mContext.getString(stringResId));
	}

	public CustomDialog setMessage(String message) {
		mContentView.setText(message);
		return this;
	}

	public CustomDialog setMessage(int resId) {
		return setMessage(mContext.getResources().getString(resId));
	}

	public CustomDialog setNegativeButton(String buttonText,
			android.view.View.OnClickListener l) {
		mNegativeButton.setOnClickListener(l);
		mNegativeButton.setText(buttonText);
		mNegativeButtonArea.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomDialog setNegativeButton(int resId,
			android.view.View.OnClickListener l) {
		return setNegativeButton(mContext.getResources().getString(resId), l);
	}

	public CustomDialog setPositiveButton(String buttonText,
			android.view.View.OnClickListener l) {
		mPositiveButton.setOnClickListener(l);
		mPositiveButton.setText(buttonText);
		mPositiveButtonArea.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomDialog setPositiveButton(int resId,
			android.view.View.OnClickListener l) {
		return setPositiveButton(mContext.getResources().getString(resId), l);
	}

	public CustomDialog setIcon(int resId) {
		mIcon.setImageResource(resId);
		mIcon.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomDialog setView(View view) {
		mContentArea.removeViews(0, mContentArea.getChildCount());
		mContentArea.addView(view);
		view.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return this;
	}

	public CustomDialog setItems(String[] items,
			final DialogInterface.OnClickListener l) {
		if (items == null || l == null) {
			return null;
		}

		if (items.length == 0) {
			return this;
		}

		ListView listView = new ListView(mContext);
		setView(listView);

		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (String item : items) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("itemName", item);
			data.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(mContext, data,
				R.layout.custom_dialog_list_item, new String[] { "itemName" },
				new int[] { R.id.custom_dialog_list_itemName });
		listView.setCacheColorHint(0x00000000);
		listView.setAdapter(adapter);
		listView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setDivider(mContext.getResources().getDrawable(
				R.drawable.main_divider));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				l.onClick(CustomDialog.this, arg2);
				CustomDialog.this.dismiss();
			}
		});
		return this;
	}

}