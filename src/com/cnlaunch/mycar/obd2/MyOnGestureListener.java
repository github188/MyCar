package com.cnlaunch.mycar.obd2;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class MyOnGestureListener extends SimpleOnGestureListener implements
		OnTouchListener {

	Context context;
	GestureDetector gDetector;
	public static final int FLING_MIN_DISTANCE = 90;
	public static final int FLING_MIN_VELOCITY = 0;
	
	public MyOnGestureListener() {
		super();
	}

	public MyOnGestureListener(Context context) {
		this(context, null);
	}

	public MyOnGestureListener(Context context, GestureDetector gDetector) {

		if (gDetector == null)
			gDetector = new GestureDetector(context, this);

		this.context = context;
		this.gDetector = gDetector;
	}

	public void flingLeft() {
		// need to override
	}

	public void flingRight() {
		// need to override
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1 == null || e2 == null) {
			return false;
		}

		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			flingLeft();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			flingRight();
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gDetector.onTouchEvent(event);
	}
}
