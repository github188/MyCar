package com.cnlaunch.mycar.diagnose.loveCarHealth;

import java.util.Date;

import com.cnlaunch.mycar.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreferenceBegin extends DialogPreference{
	
	private TimePicker mPicker;
	private long mValue;
	//private long beginTime;
	private long endTime;
	SharedPreferences sharedPreferences = null;
	private static long beginTime;
	
	
	public TimePreferenceBegin(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.lovecar_time_preference);
	}

	public void setValue(long value) {
		final boolean wasBlocking = shouldDisableDependents();

		mValue = value;
		persistLong(value);

		final boolean isBlocking = shouldDisableDependents();
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	};
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		//switch (which) {
		//case -1:
		if(which == -1){
			System.out.println("确定");
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			beginTime = sharedPreferences.getLong("time_test_begin", 10000);
			Date date = new Date(beginTime);
			System.out.println(date.getHours() + ":" + date.getMinutes());
		}
//
//			break;
//		case -2:
//			System.out.println("取消");
//			break;
//		default:
//			break;
//		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mPicker = (TimePicker) view.findViewById(R.id.timePicker_preference);
		if (mPicker != null) {
			mPicker.setIs24HourView(true);
			long value = mValue;
			Date d = new Date(value);
			mPicker.setCurrentHour(d.getHours());
			mPicker.setCurrentMinute(d.getMinutes());
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			Date d = new Date(0, 0, 0, mPicker.getCurrentHour(),
					mPicker.getCurrentMinute(), 0);
			long value = d.getTime();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		long value;
		if (restorePersistedValue)
			value = getPersistedLong(0);
		else {
			value = Long.parseLong(defaultValue.toString());
		}
		setValue(value);
	}
}
