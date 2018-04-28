package com.cnlaunch.mycar.common.ui;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.cnlaunch.mycar.R;

public class EnginePointer extends View {
	/**
	 * 1级刻度数
	 */
	private int mFirstScaleCount;
	/**
	 * 2级刻度数
	 */
	private int mSecondScaleCount;
	/**
	 * 1级度数值开始
	 */
	private float mScaleStart;

	/**
	 * 刻度值精度
	 */
	private int mScalePrecision;
	/**
	 * 1级刻度步长
	 */
	private float mScaleStep;

	/**
	 * 1级刻度值显示间隙
	 */
	private float mScaleValueStep;

	/**
	 * 刻度尺背景颜色
	 */
	private int mScaleBackgroundColor;

	/**
	 * 刻度值文字大小
	 */
	private float mScalePlateTextSize;

	/**
	 * 游标线背景色
	 */
	private int mVernierBackgroundColor;
	/**
	 * 游标与刻度之间的间距
	 */
	private float mVernierMarginForScale;
	/**
	 * 游标与辅助SignalPointer之间的间距
	 */
	private float mVernierMarginForSignalPointer;
	/**
	 * 辅助示意指针滑动所在的线条的颜色
	 */
	private int mSignalPointerTrackColor;
	/**
	 * 辅助示意指针高度
	 */
	private float mSignalPointerHeight;

	/**
	 * 当前值1
	 */
	private float mValueUpSide;
	/**
	 * 当前值2
	 */
	private float mValueDownSide;
	/**
	 * 上面的辅助示意指针的颜色
	 */
	private int mSignalPointerColorUpSide;
	/**
	 * 下面的辅助示意指针2的颜色
	 */
	private int mSignalPointerColorDownSide;
	

	private int mWidth;
	private int mHeight;
	private Paint mPaint;
	private Paint mVernierPaint;
	private Paint mTextPaint;
	private Paint mZoomDescriptionPaint;

	private String mDecimalFormatPattern = "#0";

	private final int FIRST_SCALE_WIDTH = 12;
	private final int SECOND_SCALE_INDENT = 3;;
	private final int SCALE_PADDING = 20;
	private final float TEXT_MARGIN_HORIZONTAL = 8;
	private float mMetricsRate = 1;

	private float mBaseLineHeight = 0;

	public EnginePointer(Context context) {
		super(context);
	}

	public EnginePointer(final Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EnginePointer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		DisplayMetrics dm = context.getApplicationContext().getResources()
				.getDisplayMetrics();
		mMetricsRate = dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT;

		// 读取刻度线通用属性
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ScalePlate, defStyle, 0);

		mFirstScaleCount = a.getInt(R.styleable.ScalePlate_firstScaleCount, 10);
		mSecondScaleCount = a
				.getInt(R.styleable.ScalePlate_secondScaleCount, 5);
		mScaleStart = a.getFloat(R.styleable.ScalePlate_scaleStart, 0);
		mScaleStep = a.getFloat(R.styleable.ScalePlate_scaleStep, 1);
		mScaleValueStep = a.getInt(R.styleable.ScalePlate_scaleValueStep, 1);
		mScalePrecision = a.getInt(R.styleable.ScalePlate_scalePrecision, 1);
		if (mScalePrecision == 0) {
			mDecimalFormatPattern = "#0";
		} else {
			StringBuilder sb = new StringBuilder("#0.");
			for (int i = 0; i < mScalePrecision; i++) {
				sb.append("0");
			}
			mDecimalFormatPattern = sb.toString();
		}
		mScaleBackgroundColor = a.getResourceId(
				R.styleable.ScalePlate_scaleBackgroundColor, 0);

		mScalePlateTextSize = a.getDimension(
				R.styleable.ScalePlate_scalePlateTextSize, mMetricsRate * 16);
		mVernierBackgroundColor = a.getColor(
				R.styleable.ScalePlate_vernierBackgroundColor, 0xFFFFFFFF);
		mVernierMarginForScale = a
				.getDimension(R.styleable.ScalePlate_vernierMarginForScale,
						10 * mMetricsRate);

		// 读取发动机指示器控件的属性
		TypedArray aa = context.obtainStyledAttributes(attrs,
				R.styleable.EnginePointer, defStyle, 0);
		mVernierMarginForSignalPointer = aa.getDimension(
				R.styleable.EnginePointer_vernierMarginForSignalPointer,
				10 * mMetricsRate);
		mSignalPointerTrackColor = aa.getColor(
				R.styleable.EnginePointer_signalPointerTrackColor, 0xFF333333);
		mSignalPointerHeight = aa.getDimension(
				R.styleable.EnginePointer_signalPointerHeight, 15);
		mSignalPointerColorUpSide = aa.getColor(
				R.styleable.EnginePointer_signalPointerColorUpSide, 0xFFFF0000);
		mSignalPointerColorDownSide = aa.getColor(
				R.styleable.EnginePointer_signalPointerColorDownSide, 0xFFFF0000);
		mValueUpSide = aa.getFloat(R.styleable.EnginePointer_valueUpSide, 0);
		mValueDownSide = aa.getFloat(R.styleable.EnginePointer_valueDownSide, 0);

		// 计算基准线距下边框的距离
		mBaseLineHeight = FIRST_SCALE_WIDTH + mVernierMarginForScale
				+ mVernierMarginForSignalPointer + mSignalPointerHeight;


		mPaint = new Paint();
		mPaint.setColor(mVernierBackgroundColor);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(2);

		mVernierPaint = new Paint();
		mVernierPaint.setColor(Color.GRAY);
		mVernierPaint.setAntiAlias(true);
		mVernierPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mVernierPaint.setStrokeWidth(2);

		mTextPaint = new Paint();
		mTextPaint.setColor(mVernierBackgroundColor);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStrokeWidth(1);
		mTextPaint.setTextAlign(Paint.Align.RIGHT);
		mTextPaint.setTextSize(mScalePlateTextSize);
		mTextPaint.setStrokeJoin(Join.ROUND);
		mTextPaint
				.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));

		mZoomDescriptionPaint = new Paint();
		mZoomDescriptionPaint.setColor(mVernierBackgroundColor);
		mZoomDescriptionPaint.setAntiAlias(true);
		mZoomDescriptionPaint.setStyle(Paint.Style.FILL);
		mZoomDescriptionPaint.setStrokeWidth(1);
		mZoomDescriptionPaint.setTextAlign(Paint.Align.LEFT);
		mZoomDescriptionPaint.setTextSize(mScalePlateTextSize);
		mZoomDescriptionPaint.setStrokeJoin(Join.ROUND);
		mZoomDescriptionPaint.setTypeface(Typeface.create(Typeface.SERIF,
				Typeface.ITALIC));

		measureTextWidthAndHeight();
	}

	private void measureTextWidthAndHeight() {
		final String sampleScalePlateText = "00.0";
		Rect rect = new Rect();

		mTextPaint.setTextSize(mScalePlateTextSize);
		mTextPaint.getTextBounds(sampleScalePlateText, 0,
				sampleScalePlateText.length(), rect);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = this.getWidth();
		mHeight = this.getHeight();

		drawScaleHorizontal(canvas);
		drawVernierHorizontal(canvas);
		drawSignalPointerHorizontal(canvas);
	}

	/**
	 * 刻度尺
	 * 
	 * @param canvas
	 */
	private void drawScaleHorizontal(Canvas canvas) {
		canvas.save();
		float scaleValue = mScaleStart;

		float len = mWidth - SCALE_PADDING * 2;
		float firstScaleSpace = len / mFirstScaleCount;
		float secondScaleSpace = firstScaleSpace / mSecondScaleCount;

		canvas.translate(SCALE_PADDING, 0f);

		for (int i = 0; i <= mFirstScaleCount; i++) {

			// 一级刻度线
			float x = firstScaleSpace * i;
			canvas.drawLine(x, mHeight - mBaseLineHeight * 2, x, mHeight
					- mBaseLineHeight * 2 + FIRST_SCALE_WIDTH, mPaint);
			canvas.drawLine(x, mHeight, x, mHeight - FIRST_SCALE_WIDTH, mPaint);

			// 二级刻度线
			if (i < mFirstScaleCount) {// 最后一个一级刻度线之后，不画二级刻度线
				for (int j = 1; j < mSecondScaleCount; j++) {
					float x2 = x + secondScaleSpace * j;
					canvas.drawLine(x2, mHeight - mBaseLineHeight * 2
							+ SECOND_SCALE_INDENT, x2, mHeight
							- mBaseLineHeight * 2 + FIRST_SCALE_WIDTH
							- SECOND_SCALE_INDENT, mPaint);
					canvas.drawLine(x2, mHeight - SECOND_SCALE_INDENT, x2,
							mHeight - FIRST_SCALE_WIDTH + SECOND_SCALE_INDENT,
							mPaint);
				}
			}

			// 刻度值
			if (i % mScaleValueStep == 0) {
				mTextPaint.setTextAlign(Align.CENTER);
				mTextPaint.setTypeface(Typeface.create(Typeface.SERIF,
						Typeface.ITALIC));
				DecimalFormat f = new java.text.DecimalFormat(
						mDecimalFormatPattern);
				scaleValue = mScaleStep * i + mScaleStart;
				canvas.drawText(f.format(scaleValue), x, mHeight
						- mBaseLineHeight * 2 - TEXT_MARGIN_HORIZONTAL,
						mTextPaint);
			}

		}
		canvas.restore();

	}

	/**
	 * 辅助示意指针
	 * 
	 * @param canvas
	 */
	private void drawSignalPointerHorizontal(Canvas canvas) {
		float totalLen = mWidth - SCALE_PADDING * 2;

		float rate1 = (mValueUpSide - mScaleStart)
				/ (mScaleStep * mFirstScaleCount);
		float rate2 = (mValueDownSide - mScaleStart)
				/ (mScaleStep * mFirstScaleCount);
		if (rate1 > 1) {
			rate1 = 1;
		}
		if (rate2 > 1) {
			rate2 = 1;
		}
		if (rate1 < 0) {
			rate1 = 0;
		}
		if (rate2 < 0) {
			rate2 = 0;
		}
		float valueLen1 = totalLen * rate1;
		if (valueLen1 < 0) {
			valueLen1 = 1;
		}
		float valueLen2 = totalLen * rate2;
		if (valueLen2 < 0) {
			valueLen2 = 1;
		}


		// 画示意指针滚动所在的轴线
		Paint p = new Paint(mPaint);
		p.setColor(mSignalPointerTrackColor);
		Rect r = new Rect(SCALE_PADDING, mHeight - (int) mBaseLineHeight,
				(int) totalLen + SCALE_PADDING, mHeight - (int) mBaseLineHeight
						);
		canvas.drawRect(r, p);

		p.setColor(mSignalPointerColorUpSide);
		p.setStrokeJoin(Join.MITER);

		float h = mSignalPointerHeight;
		// 画向上的指针
		float rootX = SCALE_PADDING + valueLen1;
		float rootY = mHeight - mBaseLineHeight;

		Path path = new Path();
		path.moveTo(rootX - 5f, rootY);
		path.lineTo(rootX, rootY + 5f);
		path.lineTo(rootX + 5f, rootY);
		path.lineTo(rootX + 2f, rootY - 5f);
		path.lineTo(rootX + 2f, rootY - h + 2);
		path.lineTo(rootX, rootY - h);
		path.lineTo(rootX - 2f, rootY - h + 2);
		path.lineTo(rootX - 2f, rootY - 5f);
		path.close();
		canvas.drawPath(path, p);

		// 画向下的指针
		p.setColor(mSignalPointerColorDownSide);
		rootX = SCALE_PADDING + valueLen2;
		rootY = mHeight - mBaseLineHeight;
		path = new Path();
		path.moveTo(rootX - 5f, rootY);
		path.lineTo(rootX, rootY - 5f);
		path.lineTo(rootX + 5f, rootY);
		path.lineTo(rootX + 2f, rootY + 5f);
		path.lineTo(rootX + 2f, rootY + h - 2);
		path.lineTo(rootX, rootY + h);
		path.lineTo(rootX - 2f, rootY + h - 2);
		path.lineTo(rootX - 2f, rootY + 5f);
		path.close();
		canvas.drawPath(path, p);
	}

	/**
	 * 游标指示线
	 * 
	 * @param canvas
	 */
	private void drawVernierHorizontal(Canvas canvas) {
		canvas.save();

		final int MARK_RADIUS = 5;

		float totalLen = mWidth - SCALE_PADDING * 2;

		float rate1 = (mValueUpSide - mScaleStart)
				/ (mScaleStep * mFirstScaleCount);
		if (rate1 > 1) {
			rate1 = 1;
		}
		if (rate1 < 0) {
			rate1 = 0;
		}
		float valueLen1 = totalLen * rate1;
		if (valueLen1 < 0) {
			valueLen1 = 1;
		}

		float rate2 = (mValueDownSide - mScaleStart)
				/ (mScaleStep * mFirstScaleCount);
		if (rate2 > 1) {
			rate2 = 1;
		}
		if (rate2 < 0) {
			rate2 = 0;
		}
		float valueLen2 = totalLen * rate2;
		if (valueLen2 < 0) {
			valueLen2 = 1;
		}

		// 画上方的线
		canvas.translate(SCALE_PADDING, mHeight - mBaseLineHeight * 2
				+ mVernierMarginForScale + FIRST_SCALE_WIDTH);
		canvas.drawLine(0, 0, totalLen, 0, mVernierPaint);

		canvas.drawLine(valueLen1 - MARK_RADIUS, MARK_RADIUS * -1, valueLen1
				+ MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.drawLine(valueLen1 + MARK_RADIUS, MARK_RADIUS * -1, valueLen1
				- MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.restore();

		// 画下方的线
		canvas.save();
		canvas.translate(SCALE_PADDING, mHeight - FIRST_SCALE_WIDTH
				- mVernierMarginForScale);
		canvas.drawLine(0, 0, totalLen, 0, mVernierPaint);

		canvas.drawLine(valueLen2 - MARK_RADIUS, MARK_RADIUS * -1, valueLen2
				+ MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.drawLine(valueLen2 + MARK_RADIUS, MARK_RADIUS * -1, valueLen2
				- MARK_RADIUS, MARK_RADIUS, mPaint);

		canvas.restore();
	}
	
	public void setValue(float valueUpSide,float valueDownSide){
		this.mValueUpSide = valueUpSide;
		this.mValueDownSide = valueDownSide;
		this.invalidate();
	}

}
