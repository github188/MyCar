package com.cnlaunch.mycar.common.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.cnlaunch.mycar.R;

public class FuelAnalysisBar extends View {
	/**
	 * 1级刻度数
	 */
	private int mFirstScaleCount = 5;
	/**
	 * 2级刻度数
	 */
	private int mSecondScaleCount = 5;
	/**
	 * 1级度数值开始
	 */
	private float mScaleStart = 0;

	/**
	 * 刻度值精度
	 */
	private int mScalePrecision = 0;
	/**
	 * 1级刻度步长
	 */
	private float mScaleStep = 10;

	/**
	 * 1级刻度值显示间隙
	 */
	private float mScaleValueStep;

	/**
	 * 辅助示意线背景颜色
	 */
	private int mSignalLineBackgroundColor = 0xFF333333;

	private String mGroup1ItemName = "0~5min";
	private String mGroup2ItemName = "0~30min";
	private String mGroup3ItemName = "0~3hour";
	private final int mGroup1BarCount = 5;
	private final int mGroup2BarCount = 6;
	private final int mGroup3BarCount = 6;
	private float mItemNameTextSize;
	private int mMainColor = 0xFFFFFFFF;

	private Paint mPaint;
	private Paint mBarPaint;
	private Paint mBarPaintBackground;
	private Paint mTextPaint;
	private int mBarWidth;
	private int mTextHeight;

	private final int BAR_COUNT = 21;
	private final int TEXT_AREA_HEIGHT = 20;
	private final int TEXT_PADDING = 8;
	private final int SCALE_PADDING = 20;
	private final int FIRST_SCALE_WIDTH = 4;
	private final int BAR_PADDING = 2;

	private float mMetricsRate = 1;

	private int mWidth;
	private int mHeight;

	private float[] mValue = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
			0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f

	};

	public FuelAnalysisBar(Context context) {
		super(context);
	}

	public FuelAnalysisBar(final Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FuelAnalysisBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		DisplayMetrics dm = context.getApplicationContext().getResources()
				.getDisplayMetrics();
		mMetricsRate = dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT;

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ScalePlate, defStyle, 0);

		mGroup1ItemName = a
				.getString(R.styleable.FuelAnalysisBar_group1ItemName);
		mGroup2ItemName = a
				.getString(R.styleable.FuelAnalysisBar_group2ItemName);
		mGroup3ItemName = a
				.getString(R.styleable.FuelAnalysisBar_group3ItemName);
		mItemNameTextSize = a
				.getDimension(R.styleable.FuelAnalysisBar_itemNameTextSize,
						16 * mMetricsRate);

		if (mGroup1ItemName == null) {
			mGroup1ItemName = "0~5min";
		}
		if (mGroup2ItemName == null) {
			mGroup2ItemName = "0~30min";
		}
		if (mGroup3ItemName == null) {
			mGroup3ItemName = "0~3hour";
		}

		mPaint = new Paint();
		mPaint.setColor(mMainColor);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(2);

		mBarPaint = new Paint();
		mBarPaint.setColor(Color.GREEN);
		mBarPaint.setAntiAlias(true);
		mBarPaint.setStyle(Paint.Style.FILL);
		mBarPaint.setStrokeWidth(2);

		mBarPaintBackground = new Paint();
		mBarPaintBackground.setColor(mSignalLineBackgroundColor);
		mBarPaintBackground.setAntiAlias(true);
		mBarPaintBackground.setStyle(Paint.Style.FILL);
		mBarPaintBackground.setStrokeWidth(2);

		mTextPaint = new Paint();
		mTextPaint.setColor(mMainColor);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStrokeWidth(1);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(mItemNameTextSize);
		mTextPaint.setStrokeJoin(Join.ROUND);
		mTextPaint
				.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));

		final String sampleScalePlateText = "00.0";
		Rect rect = new Rect();

		mTextPaint.getTextBounds(sampleScalePlateText, 0,
				sampleScalePlateText.length(), rect);
		mTextHeight = rect.bottom - rect.top;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = this.getWidth();
		mHeight = this.getHeight();

		mBarWidth = mWidth / BAR_COUNT;

		drawScale(canvas);
		drawBar(canvas);
		drawLine(canvas);
		drawText(canvas);

	}

	private void drawText(Canvas canvas) {
		canvas.drawText(mGroup1ItemName, mBarWidth * (1 + mGroup1BarCount / 2),
				mHeight - TEXT_AREA_HEIGHT + TEXT_PADDING + mTextHeight,
				mTextPaint);
		canvas.drawText(mGroup2ItemName, mBarWidth
				* (2 + mGroup1BarCount + mGroup2BarCount / 2), mHeight
				- TEXT_AREA_HEIGHT + TEXT_PADDING + mTextHeight, mTextPaint);
		canvas.drawText(
				mGroup3ItemName,
				mBarWidth
						* (3 + mGroup1BarCount + mGroup2BarCount + mGroup3BarCount / 2),
				mHeight - TEXT_AREA_HEIGHT + TEXT_PADDING + mTextHeight,
				mTextPaint);
	}

	private void drawLine(Canvas canvas) {
		canvas.drawLine(0, mHeight - TEXT_AREA_HEIGHT, mWidth, mHeight
				- TEXT_AREA_HEIGHT, mPaint);
	}

	private void drawBar(Canvas canvas) {
		int x = mBarWidth;
		float totalLen = (mHeight - TEXT_AREA_HEIGHT - SCALE_PADDING);
		int count = 0;
		for (int i = 0; i < mGroup1BarCount; i++) {
			float rate = (mValue[count] - mScaleStart)
					/ (mScaleStep * mFirstScaleCount);
			float valueLen = rate * totalLen;
			if (valueLen > totalLen) {
				valueLen = totalLen;
			}
			float y = mHeight - TEXT_AREA_HEIGHT;
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- totalLen, mBarPaintBackground);
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- valueLen, mBarPaint);
			x += mBarWidth;
			count++;
		}
		x += mBarWidth;

		for (int i = 0; i < mGroup2BarCount; i++) {
			float rate = (mValue[count] - mScaleStart)
					/ (mScaleStep * mFirstScaleCount);
			float valueLen = rate * totalLen;
			if (valueLen > totalLen) {
				valueLen = totalLen;
			}
			float y = mHeight - TEXT_AREA_HEIGHT;
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- totalLen, mBarPaintBackground);
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- valueLen, mBarPaint);
			x += mBarWidth;
			count++;
		}
		x += mBarWidth;

		for (int i = 0; i < mGroup3BarCount; i++) {
			float rate = (mValue[count] - mScaleStart)
					/ (mScaleStep * mFirstScaleCount);
			float valueLen = rate * totalLen;
			if (valueLen > totalLen) {
				valueLen = totalLen;
			}
			float y = mHeight - TEXT_AREA_HEIGHT;
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- totalLen, mBarPaintBackground);
			canvas.drawRect(x + BAR_PADDING, y, x + mBarWidth - BAR_PADDING, y
					- valueLen, mBarPaint);
			x += mBarWidth;
			count++;
		}
	}

	/**
	 * 刻度尺
	 * 
	 * @param canvas
	 */
	private void drawScale(Canvas canvas) {

		float len = mHeight - SCALE_PADDING * 2;
		float firstScaleSpace = len / mFirstScaleCount;

		for (int i = 0; i <= mFirstScaleCount; i++) {

			float y = SCALE_PADDING + firstScaleSpace * i;
			float x = 0;
			float indent = (mBarWidth - FIRST_SCALE_WIDTH) / 2;
			indent = indent < 0 ? 0 : indent;
			canvas.drawLine(x + indent, y, x - indent + mBarWidth, y, mPaint);
			x += (1 + mGroup1BarCount) * mBarWidth;
			canvas.drawLine(x + indent, y, x - indent + mBarWidth, y, mPaint);
			x += (1 + mGroup2BarCount) * mBarWidth;
			canvas.drawLine(x + indent, y, x - indent + mBarWidth, y, mPaint);
			x += (1 + mGroup3BarCount) * mBarWidth;
			canvas.drawLine(x + indent, y, x - indent + mBarWidth, y, mPaint);
		}
	}

	/**
	 * 数组value不能为null，且长度必须为17
	 * 
	 * @param value
	 */
	public void setValue(float[] value) {
		if (value == null || value.length != mValue.length) {
			throw new IllegalArgumentException("数组value不能为null，且长度必须为17");
		} else {
			synchronized (mValue) {
				mValue = value.clone();
			}
		}
		invalidate();
	}
}
