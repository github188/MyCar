package com.cnlaunch.mycar.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnlaunch.mycar.R;

public class CustomProgressDialog extends Dialog {
	private Context mContext;
	private View mRootView;
	private TextView mTitleView;
	private TextView mContentView;
	private View mNegativeButtonArea;
	private View mPositiveButtonArea;
	private Button mNegativeButton;
	private Button mPositiveButton;
	private ProgressBar mProgressBar;
	private View mProgressBarArea;
	private ProgressBar mProgressCycle;
	private TextView mProgressText;
	private TextView mProgressTextDetail;
	private int mProgress = 0;
	private String mSizeRate = "";

	public CustomProgressDialog(Context context) {
		super(context, R.style.CustomDialog);
		mContext = context;
		LayoutInflater inflater = this.getLayoutInflater();
		mRootView = inflater.inflate(R.layout.custom_progress_dialog, null);
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
		mProgressTextDetail = (TextView) mRootView
				.findViewById(R.id.custom_dialog_progress_detail);
		mProgressText = (TextView) mRootView
				.findViewById(R.id.custom_dialog_progress);
		mProgressBar = (ProgressBar) mRootView
				.findViewById(R.id.custom_dialog_progress_bar);
		mProgressCycle = (ProgressBar) mRootView
				.findViewById(R.id.custom_dialog_progress_cycle);
		mProgressBarArea = mRootView
				.findViewById(R.id.custom_dialog_progress_bar_area);

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

	public CustomProgressDialog setTitle(String title) {
		mTitleView.setText(title);
		return this;
	}

	public CustomProgressDialog setMessage(String message) {
		mContentView.setText(message);
		mContentView.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomProgressDialog setMessage(int resId) {
		return setMessage(mContext.getResources().getString(resId));
	}

	public CustomProgressDialog setNegativeButton(String buttonText,
			android.view.View.OnClickListener l) {
		mNegativeButton.setOnClickListener(l);
		mNegativeButton.setText(buttonText);
		mNegativeButtonArea.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomProgressDialog setNegativeButton(int resId,
			android.view.View.OnClickListener l) {
		return setNegativeButton(mContext.getResources().getString(resId), l);
	}

	public CustomProgressDialog setPositiveButton(String buttonText,
			android.view.View.OnClickListener l) {
		mPositiveButton.setOnClickListener(l);
		mPositiveButton.setText(buttonText);
		mPositiveButtonArea.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomProgressDialog setPositiveButtonEnabled(boolean enabled) {
		mPositiveButton.setEnabled(enabled);
		return this;
	}

	public CustomProgressDialog setNegativeButtonEnabled(boolean enabled) {
		mNegativeButton.setEnabled(enabled);
		return this;
	}

	public CustomProgressDialog setPositiveButton(int resId,
			android.view.View.OnClickListener l) {
		return setPositiveButton(mContext.getResources().getString(resId), l);
	}

	public CustomProgressDialog setProgress(int progress) {
		mProgressBar.setProgress(progress);
		mProgressText.setText(String.valueOf(progress) + "%");
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressText.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomProgressDialog setProgressDetail(String progressDetail) {
		mProgressTextDetail.setText(progressDetail);
		mProgressTextDetail.setVisibility(View.VISIBLE);
		return this;
	}

	public void setStyle(boolean isCycle) {
		if (isCycle) {
			mProgressCycle.setVisibility(View.VISIBLE);
			mProgressBarArea.setVisibility(View.GONE);
		} else {
			mProgressCycle.setVisibility(View.GONE);
			mProgressBarArea.setVisibility(View.VISIBLE);
		}
	}

}