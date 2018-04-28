package com.cnlaunch.mycar.common.ui;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.cnlaunch.mycar.R;

public class ScalePlate extends View {

	public final int SCALE_ORITATION_VERTICAL = 0;
	public final int SCALE_ORITATION_HORIZONTAL = 1;
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
	 * 刻度尺方向，0水平，1垂直
	 */
	private int mScaleOritation;
	/**
	 * 游标线背景色
	 */
	private int mVernierBackgroundColor;
	/**
	 * 游标图片资源ID
	 */
	private int mVernierImageResId;
	/**
	 * 游标与刻度之间的间距
	 */
	private float mVernierMarginForScale;
	/**
	 * 游标与辅助示意线之间的间距
	 */
	private float mVernierMarginForSignalLine;
	/**
	 * 辅助示意线颜色
	 */
	private int mSignalLineColor;
	/**
	 * 辅助示意线背景颜色
	 */
	private int mSignalLineBackgroundColor;
	/**
	 * 辅助示意线粗细
	 */
	private float mSignalLineWeight;
	/**
	 * 辅助示意线缩放描述
	 */
	private String mZoomDescription;
	/**
	 * 是否隐藏游标
	 */
	private boolean mIsHideScale;
	/**
	 * 是否隐藏辅助线
	 */
	private boolean mIsHideSignalLine;

	/**
	 * 当前值
	 */
	private float mValue;

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
	private float mTextWidth = 30;
	private float mTextHeight = 20;
	private final float TEXT_MARGIN = 3;
	private final float TEXT_MARGIN_HORIZONTAL = 1;
	private float mMetricsRate = 1;

	public ScalePlate(Context context) {
		super(context);
	}

	public ScalePlate(final Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScalePlate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		DisplayMetrics dm = context.getApplicationContext().getResources()
				.getDisplayMetrics();
		mMetricsRate = dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
		// mMetricsRate = 1;

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

		mScaleOritation = a.getInt(R.styleable.ScalePlate_scaleOritation, 0);

		mVernierBackgroundColor = a.getColor(
				R.styleable.ScalePlate_vernierBackgroundColor, 0xFFFFFFFF);
		mVernierImageResId = a.getResourceId(
				R.styleable.ScalePlate_vernierImage, 0);
		mVernierMarginForScale = a
				.getDimension(R.styleable.ScalePlate_vernierMarginForScale,
						10 * mMetricsRate);
		mVernierMarginForSignalLine = a.getDimension(
				R.styleable.ScalePlate_vernierMarginForSignalLine,
				10 * mMetricsRate);
		mSignalLineColor = a.getColor(R.styleable.ScalePlate_signalLineColor,
				0xFF00FF00);
		mSignalLineBackgroundColor = a.getColor(
				R.styleable.ScalePlate_signalLineBackgroundColor, 0xFF333333);
		mSignalLineWeight = a.getDimension(
				R.styleable.ScalePlate_signalLineWeight, 10);
		mZoomDescription = a.getString(R.styleable.ScalePlate_zoomDescription);
		mIsHideScale = a.getBoolean(R.styleable.ScalePlate_isHideScale, false);
		mIsHideSignalLine = a.getBoolean(
				R.styleable.ScalePlate_isHideSignalLine, false);

		mValue = a.getFloat(R.styleable.ScalePlate_value, 0);

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
		if (mScaleOritation == SCALE_ORITATION_VERTICAL) {
			mZoomDescriptionPaint.setTextAlign(Paint.Align.LEFT);
		} else {
			mZoomDescriptionPaint.setTextAlign(Paint.Align.RIGHT);
		}
		mZoomDescriptionPaint.setStrokeWidth(1);
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
		mTextWidth = rect.right - rect.left;
		mTextHeight = rect.bottom - rect.top;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = this.getWidth();
		mHeight = this.getHeight();

		if (mScaleOritation == SCALE_ORITATION_VERTICAL) {
			drawScale(canvas);
			drawVernier(canvas);
			drawSignalLine(canvas);
			drawDescription(canvas);
		} else {
			drawScaleHorizontal(canvas);
			drawVernierHorizontal(canvas);
			drawSignalLineHorizontal(canvas);
			drawDescriptionHorizontal(canvas);
		}
	}

	/**
	 * 缩放比例描述
	 * 
	 * @param canvas
	 */
	private void drawDescription(Canvas canvas) {
		if (mZoomDescription != null) {
			canvas.drawText(mZoomDescription, mTextWidth + TEXT_MARGIN
					+ FIRST_SCALE_WIDTH + mVernierMarginForScale
					+ mVernierMarginForSignalLine, SCALE_PADDING + mTextHeight,
					mZoomDescriptionPaint);
		}

	}

	/**
	 * 游标指示线
	 * 
	 * @param canvas
	 */
	private void drawVernier(Canvas canvas) {
		canvas.save();

		final int MARK_RADIUS = 5;

		float totalLen = mHeight - SCALE_PADDING * 2;
		canvas.translate(mTextWidth + TEXT_MARGIN + FIRST_SCALE_WIDTH
				+ mVernierMarginForScale, SCALE_PADDING);
		canvas.drawLine(0, 0, 0, totalLen, mVernierPaint);

		float rate = (mValue - mScaleStart) / (mScaleStep * mFirstScaleCount);
		if (rate > 1) {
			rate = 1;
		}
		if (rate < 0) {
			rate = 0;
		}
		float valueLen = totalLen * rate;
		if (valueLen <= 0) {
			valueLen = 1;
		}

		canvas.drawLine(MARK_RADIUS * -1, totalLen - valueLen - MARK_RADIUS,
				MARK_RADIUS, totalLen - valueLen + MARK_RADIUS, mPaint);
		canvas.drawLine(MARK_RADIUS * -1, totalLen - valueLen + MARK_RADIUS,
				MARK_RADIUS, totalLen - valueLen - MARK_RADIUS, mPaint);

		canvas.restore();
	}

	/**
	 * 刻度尺
	 * 
	 * @param canvas
	 */
	private void drawScale(Canvas canvas) {

		float scaleValue = mScaleStart;

		float len = mHeight - SCALE_PADDING * 2;
		float firstScaleSpace = len / mFirstScaleCount;
		float secondScaleSpace = firstScaleSpace / mSecondScaleCount;

		for (int i = 0; i <= mFirstScaleCount; i++) {

			// 一级刻度线
			float y = SCALE_PADDING + firstScaleSpace * i;
			canvas.drawLine(mTextWidth + TEXT_MARGIN, y, FIRST_SCALE_WIDTH
					+ mTextWidth + TEXT_MARGIN, y, mPaint);

			// 二级刻度线
			if (i < mFirstScaleCount) {
				for (int j = 1; j < mSecondScaleCount; j++) {
					float y2 = y + secondScaleSpace * j;
					canvas.drawLine(SECOND_SCALE_INDENT + mTextWidth
							+ TEXT_MARGIN, y2, FIRST_SCALE_WIDTH
							- SECOND_SCALE_INDENT + mTextWidth + TEXT_MARGIN,
							y2, mPaint);
				}
			}

			// 刻度值
			if (i % mScaleValueStep == 0) {
				mTextPaint.setTextAlign(Align.RIGHT);
				DecimalFormat f = new java.text.DecimalFormat(
						mDecimalFormatPattern);
				scaleValue = mScaleStep * (mFirstScaleCount - i) + mScaleStart;
				canvas.drawText(f.format(scaleValue), mTextWidth, y
						+ mTextHeight / 2, mTextPaint);
			}

		}

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

		canvas.translate(SCALE_PADDING, -mVernierMarginForSignalLine
				- mVernierMarginForScale - mSignalLineWeight);

		for (int i = 0; i <= mFirstScaleCount; i++) {

			// 一级刻度线
			float x = firstScaleSpace * i;
			canvas.drawLine(x, mHeight, x, mHeight - FIRST_SCALE_WIDTH, mPaint);

			// 二级刻度线
			if (i < mFirstScaleCount) {// 最后一个一级刻度线之后，不画二级刻度线
				for (int j = 1; j < mSecondScaleCount; j++) {
					float x2 = x + secondScaleSpace * j;
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
						- FIRST_SCALE_WIDTH - TEXT_MARGIN_HORIZONTAL
						- mVernierMarginForScale + mVernierMarginForSignalLine
						- mSignalLineWeight, mTextPaint);
			}

		}
		canvas.restore();

	}

	/**
	 * 辅助示意线
	 * 
	 * @param canvas
	 */
	private void drawSignalLineHorizontal(Canvas canvas) {
		float totalLen = mWidth - SCALE_PADDING * 2;
		float rate = (mValue - mScaleStart) / (mScaleStep * mFirstScaleCount);
		if (rate > 1) {
			rate = 1;
		}
		if (rate < 0) {
			rate = 0;
		}
		float valueLen = totalLen * rate;
		if (valueLen <= 0) {
			valueLen = 1;
		}
		Paint p = new Paint(mPaint);
		p.setColor(mSignalLineBackgroundColor);

		Rect r = new Rect(SCALE_PADDING, mHeight - (int) mSignalLineWeight,
				(int) totalLen + SCALE_PADDING, mHeight);
		canvas.drawRect(r, p);

		r = new Rect(SCALE_PADDING, mHeight - (int) mSignalLineWeight,
				(int) valueLen + SCALE_PADDING, mHeight);
		p.setColor(mSignalLineColor);
		canvas.drawRect(r, p);
	}

	/**
	 * 缩放比例描述
	 * 
	 * @param canvas
	 */
	private void drawDescriptionHorizontal(Canvas canvas) {

		if (mZoomDescription != null && mZoomDescription.length() > 0) {

			canvas.drawText(mZoomDescription, mWidth - SCALE_PADDING, mHeight
					- (int) mSignalLineWeight + (int) mSignalLineWeight,
					mZoomDescriptionPaint);
		}

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
		canvas.translate(SCALE_PADDING, mHeight - mVernierMarginForSignalLine
				- mSignalLineWeight);
		canvas.drawLine(0, 0, totalLen, 0, mVernierPaint);

		float rate = (mValue - mScaleStart) / (mScaleStep * mFirstScaleCount);
		if (rate > 1) {
			rate = 1;
		}
		if (rate < 0) {
			rate = 0;
		}
		float valueLen = totalLen * rate;
		if (valueLen <= 0) {
			valueLen = 1;
		}
		canvas.drawLine(valueLen - MARK_RADIUS, MARK_RADIUS * -1, valueLen
				+ MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.drawLine(valueLen + MARK_RADIUS, MARK_RADIUS * -1, valueLen
				- MARK_RADIUS, MARK_RADIUS, mPaint);

		canvas.restore();
	}

	/**
	 * 辅助示意线
	 * 
	 * @param canvas
	 */
	private void drawSignalLine(Canvas canvas) {
		canvas.save();
		float totalLen = mHeight - SCALE_PADDING * 2;
		float rate = (mValue - mScaleStart) / (mScaleStep * mFirstScaleCount);
		if (rate > 1) {
			rate = 1;
		}
		if (rate < 0) {
			rate = 0;
		}
		float valueLen = totalLen * rate;
		if (valueLen <= 0) {
			valueLen = 1;
		}
		Paint p = new Paint(mPaint);
		p.setColor(mSignalLineBackgroundColor);

		canvas.translate(mTextWidth + TEXT_MARGIN + FIRST_SCALE_WIDTH
				+ mVernierMarginForScale + mVernierMarginForSignalLine,
				SCALE_PADDING);

		Rect r = new Rect(0, 0, (int) mSignalLineWeight, (int) totalLen);
		canvas.drawRect(r, p);

		r = new Rect(0, (int) (totalLen - valueLen), (int) mSignalLineWeight,
				(int) totalLen);
		p.setColor(mSignalLineColor);
		canvas.drawRect(r, p);
		canvas.restore();
	}

	public void setValue(float value) {
		this.mValue = value;
		this.invalidate();
	}

}
