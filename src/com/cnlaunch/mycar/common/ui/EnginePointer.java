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
	 * 1���̶���
	 */
	private int mFirstScaleCount;
	/**
	 * 2���̶���
	 */
	private int mSecondScaleCount;
	/**
	 * 1������ֵ��ʼ
	 */
	private float mScaleStart;

	/**
	 * �̶�ֵ����
	 */
	private int mScalePrecision;
	/**
	 * 1���̶Ȳ���
	 */
	private float mScaleStep;

	/**
	 * 1���̶�ֵ��ʾ��϶
	 */
	private float mScaleValueStep;

	/**
	 * �̶ȳ߱�����ɫ
	 */
	private int mScaleBackgroundColor;

	/**
	 * �̶�ֵ���ִ�С
	 */
	private float mScalePlateTextSize;

	/**
	 * �α��߱���ɫ
	 */
	private int mVernierBackgroundColor;
	/**
	 * �α���̶�֮��ļ��
	 */
	private float mVernierMarginForScale;
	/**
	 * �α��븨��SignalPointer֮��ļ��
	 */
	private float mVernierMarginForSignalPointer;
	/**
	 * ����ʾ��ָ�뻬�����ڵ���������ɫ
	 */
	private int mSignalPointerTrackColor;
	/**
	 * ����ʾ��ָ��߶�
	 */
	private float mSignalPointerHeight;

	/**
	 * ��ǰֵ1
	 */
	private float mValueUpSide;
	/**
	 * ��ǰֵ2
	 */
	private float mValueDownSide;
	/**
	 * ����ĸ���ʾ��ָ�����ɫ
	 */
	private int mSignalPointerColorUpSide;
	/**
	 * ����ĸ���ʾ��ָ��2����ɫ
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

		// ��ȡ�̶���ͨ������
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

		// ��ȡ������ָʾ���ؼ�������
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

		// �����׼�߾��±߿�ľ���
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
	 * �̶ȳ�
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

			// һ���̶���
			float x = firstScaleSpace * i;
			canvas.drawLine(x, mHeight - mBaseLineHeight * 2, x, mHeight
					- mBaseLineHeight * 2 + FIRST_SCALE_WIDTH, mPaint);
			canvas.drawLine(x, mHeight, x, mHeight - FIRST_SCALE_WIDTH, mPaint);

			// �����̶���
			if (i < mFirstScaleCount) {// ���һ��һ���̶���֮�󣬲��������̶���
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

			// �̶�ֵ
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
	 * ����ʾ��ָ��
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


		// ��ʾ��ָ��������ڵ�����
		Paint p = new Paint(mPaint);
		p.setColor(mSignalPointerTrackColor);
		Rect r = new Rect(SCALE_PADDING, mHeight - (int) mBaseLineHeight,
				(int) totalLen + SCALE_PADDING, mHeight - (int) mBaseLineHeight
						);
		canvas.drawRect(r, p);

		p.setColor(mSignalPointerColorUpSide);
		p.setStrokeJoin(Join.MITER);

		float h = mSignalPointerHeight;
		// �����ϵ�ָ��
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

		// �����µ�ָ��
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
	 * �α�ָʾ��
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

		// ���Ϸ�����
		canvas.translate(SCALE_PADDING, mHeight - mBaseLineHeight * 2
				+ mVernierMarginForScale + FIRST_SCALE_WIDTH);
		canvas.drawLine(0, 0, totalLen, 0, mVernierPaint);

		canvas.drawLine(valueLen1 - MARK_RADIUS, MARK_RADIUS * -1, valueLen1
				+ MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.drawLine(valueLen1 + MARK_RADIUS, MARK_RADIUS * -1, valueLen1
				- MARK_RADIUS, MARK_RADIUS, mPaint);
		canvas.restore();

		// ���·�����
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
