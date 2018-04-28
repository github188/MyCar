package com.cnlaunch.mycar.manager;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.cnlaunch.mycar.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DialogDatePickerActivity extends Activity{
	private int actionType = -1;
	private final int GET_DATE_TIME = 0;
	private final int GET_DATE = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent.hasExtra("com.cnlaunch.mycar.action")){
			String action = intent.getStringExtra("com.cnlaunch.mycar.action");
			Date date = null;
			
			if(action.equals("GET_DATE_TIME")){
				actionType = GET_DATE_TIME;
			}else if(action.equals("GET_DATE")){
				actionType = GET_DATE;
			}
			
			if(actionType ==  GET_DATE_TIME)
			{	
				String curreny_date_time = intent.getStringExtra("com.cnlaunch.mycar.curreny_datetime");
				try {
					date = DateUtils.parseDate(curreny_date_time, "yyyy-MM-dd hh:mm:ss");
				} catch (ParseException e) {
					date = new Date();
				}
			}else if(actionType ==  GET_DATE){
				String curreny_date_time = intent.getStringExtra("com.cnlaunch.mycar.curreny_date");
				try {
					date = DateUtils.parseDate(curreny_date_time, "yyyy-MM-dd");
				} catch (ParseException e) {
					date = new Date();
				}
			}
			setContentView(R.layout.manager_dialog_date_picker);

			
			
		}else{
			this.finish();
		}
		
	}

	
}