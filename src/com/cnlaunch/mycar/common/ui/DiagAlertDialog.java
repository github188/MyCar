package com.cnlaunch.mycar.common.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
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

public class DiagAlertDialog{
	
	private final static boolean D = true;
	private final static String TAG = "DiagAlertDialog";
	
	private AlertDialog alertDialog;
	private ViewGroup alertViewGroup;
	private Activity activity;
	private int width = 0;
	
	public DiagAlertDialog(Activity activity){
		this.activity = activity;
		Log.e(TAG,"call DiagAlertDialog()");
	    WindowManager manager = activity.getWindowManager();
	    Display display = manager.getDefaultDisplay();
	    width = display.getWidth() - 30;

	    	    
	    LayoutInflater inflater = activity.getLayoutInflater();
	    alertViewGroup = (ViewGroup) inflater.inflate(R.layout.diagnose_alert_dialog, null);
	    alertDialog = new AlertDialog.Builder(activity).create();
	    alertDialog.show();
	    TextView textview = (TextView)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_content);
	    textview.setMovementMethod(ScrollingMovementMethod.getInstance());
	   
	}
	//设置对话框模式 1--无按钮；  2--一个按钮 ；  3--两个按钮
	public DiagAlertDialog SetShowMode(int mode)
	{
		if(mode == 1)  // 1--无按钮
		{
			Button btn_close = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_close);
			btn_close.setVisibility(View.VISIBLE);
			btn_close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
		}
		else if(mode == 2)	// 2--一个按钮
		{
			Button btn_ok = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_positive);
			btn_ok.setVisibility(View.VISIBLE);
		}
		else if(mode == 3)  //3--两个按钮
		{
			Button btn_no = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_negative);
			btn_no.setVisibility(View.VISIBLE);
			Button btn_yes = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_positive);
			btn_yes.setVisibility(View.VISIBLE);
		}
		else
		{
			
		}
		return this;
	}
	
	public DiagAlertDialog setTitle(String title){
		TextView titleView = (TextView)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_title);
		titleView.setText(title);
		return this;
	}
	
	public DiagAlertDialog setTitle(int resId){
		return setTitle(activity.getResources().getString(resId));
	}
	
	
	public DiagAlertDialog setMessage(String message){
		TextView titleView = (TextView)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_content);
		titleView.setText(message);
		return this;
	}
	
	public DiagAlertDialog setMessage(int resId){
		return setMessage(activity.getResources().getString(resId));
	}
	
	public DiagAlertDialog setNegativeButton(String buttonText,OnClickListener l){
		Button negativeButton = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_negative);
		negativeButton.setOnClickListener(l);
		negativeButton.setText(buttonText);
		alertViewGroup.findViewById(R.id.diagnose_alert_dialog_negative).setVisibility(View.VISIBLE);
		return this;
	}
	
	public DiagAlertDialog setNegativeButton(int resId,OnClickListener l){
		return setNegativeButton(activity.getResources().getString(resId),l);
	}
	
	public DiagAlertDialog setPositiveButton(String buttonText,OnClickListener l){
		Button positiveButton = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_positive);
		positiveButton.setOnClickListener(l);
		positiveButton.setText(buttonText);
		alertViewGroup.findViewById(R.id.diagnose_alert_dialog_positive).setVisibility(View.VISIBLE);
		return this;
	}
	
	public DiagAlertDialog setPositiveButton(int resId,OnClickListener l){
		return setPositiveButton(activity.getResources().getString(resId),l);
	}
	
	public DiagAlertDialog setCloseButton(String buttonText,OnClickListener l){
		Button positiveButton = (Button)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_close);
		positiveButton.setOnClickListener(l);
		positiveButton.setText(buttonText);
		alertViewGroup.findViewById(R.id.diagnose_alert_dialog_close).setVisibility(View.VISIBLE);
		return this;
	}
	
	public DiagAlertDialog setCloseButton(int resId,OnClickListener l){
		return setCloseButton(activity.getResources().getString(resId),l);
	}
	
	public DiagAlertDialog setCloseButtonEnable(boolean enable)
	{
		if(enable)
			alertViewGroup.findViewById(R.id.diagnose_alert_dialog_close).setVisibility(View.VISIBLE);
		else
			alertViewGroup.findViewById(R.id.diagnose_alert_dialog_close).setVisibility(View.GONE);
		return this;
	}
	
	public void setOnKeyListener(DialogInterface.OnKeyListener keyListener)
	{
		alertDialog.setOnKeyListener(keyListener);
	}
	
	public DiagAlertDialog setCancelable(boolean flag){
		alertDialog.setCancelable(flag);
		return this;
	}
	
	public void cancel(){
		alertDialog.cancel();
	}
	
	public void dismiss(){
		alertDialog.dismiss();
	}
	 
//	public DiagAlertDialog setIcon(int resId){
//		ImageView icon = (ImageView)alertViewGroup.findViewById(R.id.custom_dialog_icon);
//		icon.setImageResource(resId);
//		icon.setVisibility(View.VISIBLE);
//		return this;
//	}
	
	public DiagAlertDialog setView(View view){
		ViewGroup viewGroup = (ViewGroup)alertViewGroup.findViewById(R.id.diagnose_alert_dialog_content);
		viewGroup.removeViews(0, viewGroup.getChildCount());
		viewGroup.addView(view);
		view.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return this;
	}
	
	public DiagAlertDialog setItems(String[] items, DialogInterface.OnClickListener l){
		
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
