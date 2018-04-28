package com.cnlaunch.mycar.common.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnlaunch.mycar.R;

public class CustomAlertDialog {
	
	private final static boolean D = true;
	private final static String TAG = "CustomAlertDialog";
	
	private AlertDialog alertDialog;
	private ViewGroup alertViewGroup;
	private Activity activity;
	private int width = 0;
	
	public CustomAlertDialog(Activity activity){
		this.activity = activity;
		Log.e(TAG,"call CustomAlertDialog()");
	    WindowManager manager = activity.getWindowManager();
	    Display display = manager.getDefaultDisplay();
	    width = display.getWidth() - 30;		
	    
	    LayoutInflater inflater = activity.getLayoutInflater();
	    alertViewGroup = (ViewGroup) inflater.inflate(R.layout.custom_dialog, null);
	    alertDialog = new AlertDialog.Builder(activity).create();
	    alertDialog.show();
	}
	
	public CustomAlertDialog setTitle(String title){
		TextView titleView = (TextView)alertViewGroup.findViewById(R.id.custom_dialog_title);
		titleView.setText(title);
		return this;
	}
	
	public CustomAlertDialog setTitle(int resId){
		return setTitle(activity.getResources().getString(resId));
	}
	
	
	public CustomAlertDialog setMessage(String message){
		TextView titleView = (TextView)alertViewGroup.findViewById(R.id.custom_dialog_content);
		titleView.setText(message);
		return this;
	}
	
	public CustomAlertDialog setMessage(int resId){
		return setMessage(activity.getResources().getString(resId));
	}
	
	public CustomAlertDialog setNegativeButton(String buttonText,OnClickListener l){
		Button negativeButton = (Button)alertViewGroup.findViewById(R.id.custom_dialog_button_negative);
		negativeButton.setOnClickListener(l);
		negativeButton.setText(buttonText);
		alertViewGroup.findViewById(R.id.custom_dialog_button_negative_area).setVisibility(View.VISIBLE);
		return this;
	}
	
	public CustomAlertDialog setNegativeButton(int resId,OnClickListener l){
		return setNegativeButton(activity.getResources().getString(resId),l);
	}
	
	public CustomAlertDialog setPositiveButton(String buttonText,OnClickListener l){
		Button positiveButton = (Button)alertViewGroup.findViewById(R.id.custom_dialog_button_positive);
		positiveButton.setOnClickListener(l);
		positiveButton.setText(buttonText);
		alertViewGroup.findViewById(R.id.custom_dialog_button_positive_area).setVisibility(View.VISIBLE);
		return this;
	}
	
	public CustomAlertDialog setPositiveButton(int resId,OnClickListener l){
		return setPositiveButton(activity.getResources().getString(resId),l);
	}
	
	public void setOnKeyListener(DialogInterface.OnKeyListener keyListener)
	{
		alertDialog.setOnKeyListener(keyListener);
	}
	
	public CustomAlertDialog setCancelable(boolean flag){
		alertDialog.setCancelable(flag);
		return this;
	}
	
	public void cancel(){
		alertDialog.cancel();
	}
	
	public void dismiss(){
		alertDialog.dismiss();
	}
	 
	public CustomAlertDialog setIcon(int resId){
		ImageView icon = (ImageView)alertViewGroup.findViewById(R.id.custom_dialog_icon);
		icon.setImageResource(resId);
		icon.setVisibility(View.VISIBLE);
		return this;
	}
	
	public CustomAlertDialog setView(View view){
		ViewGroup viewGroup = (ViewGroup)alertViewGroup.findViewById(R.id.custom_dialog_content_area);
		viewGroup.removeViews(0, viewGroup.getChildCount());
		viewGroup.addView(view);
		view.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return this;
	}
	
	public CustomAlertDialog setItems(String[] items, DialogInterface.OnClickListener l){
		
		return this;
	}
	
	public void show(){    
	    alertDialog.getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
	    alertDialog.getWindow().setContentView(alertViewGroup);  
	}
	
	public AlertDialog getAlertDialog(){
		return alertDialog;
	}

}
