package com.cnlaunch.mycar.diagnose.loveCarHealth;

import java.util.Date;

import com.cnlaunch.mycar.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
/**
 * …Ë÷√ ±º‰
 * @author huangweiyong
 *
 */
public class TimePreferenceEnd extends DialogPreference {
	private TimePicker mPicker;
	private long mValue;

	public TimePreferenceEnd(Context context, AttributeSet attrs) {
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
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		long value;
		if (restorePersistedValue)
			value = getPersistedLong(0);
		else {
			value = Long.parseLong(defaultValue.toString());
		}
		setValue(value);
	}
}
