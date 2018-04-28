package com.cnlaunch.mycar.common.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BarChart extends View {

	private int mWidth;
	private int mHeight;
	private int mXScaleMarkInterval;
	private int mYScaleMarkInterval;
	private int mItemWidth;
	private String[] mItemLables;
	private int[] mItemValues;
	private int mItemValueUnit;
	private int mItemCount = 0;
	private int mXScaleCount = 0;
	private int mYScaleCount = 10;
	private int mMainColor = Color.GREEN;
	private int mScaleLength = 10;

	private String mChartTitle;

	private static final int X_AXES_THICK = 50;
	private static final int Y_AXES_THICK = 50;

	private static final int MARGIN = 5;

	private static Paint p = new Paint();
	private Canvas mCanvas;

	public BarChart(Context context) {
		super(context);
	}

	public BarChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BarChart(final Context context, AttributeSet attrs) {
		this(context, attrs, 0);

		// setItemValues(new String[] { "0时", "1时", "2时", "3时", "4时", "5时",
		// "6时",
		// "7时", "8时", "9时", "10时", "11时", "12时", "13时", "14时", "15时",
		// "16时", "17时", "18时", "19时", "20时", "21时", "22时", "23时" },
		// new int[] { 55, 26, 12, 8, 11, 15, 23, 42, 47, 49, 60, 62, 66,
		// 51, 48, 37, 73, 54, 44, 61, 63, 71, 73, 56 }, 10);
		// setTitle("按小时统计");
	}

	public void setTitle(String chartTitle) {
		mChartTitle = chartTitle;

	}

	public void setItemValues(String[] itemLables, int[] itemValues) {
		if (itemLables.length == itemValues.length) {
			mItemCount = itemLables.length;
			mItemLables = itemLables;
			mItemValues = itemValues;
			mItemValueUnit = getItemValueUnit(mItemValues);
			fresh();
		} else {
			Log.e("BarChart","lables\'s length not equal values's length");
		}
	}

	private int getItemValueUnit(int[] itemValues) {
		int max = 0;
		for (int i = 0; i < itemValues.length; i++) {
			if (max < itemValues[i]) {
				max = itemValues[i];
			}
		}
		int ret = max / 10;
		int base = 1;

		int temp = ret;
		while (temp / 10 > 0) {
			base *= 10;
			temp /= 10;
		}

		if (base == 1) {
			ret = 10;
		} else if (ret % base != 0) {
			ret = (ret / base + 1) * base;
		}

		return ret;
	}

	public void fresh() {
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvas = canvas;
		p.setColor(mMainColor);
		mWidth = this.getWidth();
		mHeight = this.getHeight();
		mXScaleCount = mItemCount;

		if (mXScaleCount == 0) {
			mCanvas.drawText("NO DATA", 100, 100, p);
			return;
		}

		mXScaleMarkInterval = (mWidth - X_AXES_THICK) / (mXScaleCount + 1);
		mYScaleMarkInterval = (mHeight - Y_AXES_THICK) / (mYScaleCount + 1);
		mItemWidth = mXScaleMarkInterval * 2 / 3;

		drawAxes(mWidth, mHeight);
	}

	private void drawAxes(int width, int height) {
		// D.e(mWidth + "--" + mHeight);

		// x轴线
		p.setColor(Color.GREEN);
		drawTitle();
		drawLine(X_AXES_THICK, mHeight - Y_AXES_THICK, mWidth, mHeight
				- Y_AXES_THICK);
		// 画箭头
		drawLine(mWidth, mHeight - Y_AXES_THICK, mWidth - 8, mHeight
				- Y_AXES_THICK - 6);
		drawLine(mWidth, mHeight - Y_AXES_THICK, mWidth - 8, mHeight
				- Y_AXES_THICK + 6);
		for (int i = 0; i < mXScaleCount; i++) {
			int startX = X_AXES_THICK + mXScaleMarkInterval * (i + 1);
			int startY = mHeight - Y_AXES_THICK;
			int endX = X_AXES_THICK + mXScaleMarkInterval * (i + 1);
			int endY = mHeight - Y_AXES_THICK - mScaleLength;
			p.setColor(Color.GREEN);
			// 刻度
			drawLine(startX, startY, endX, endY);
			// 文字
			p.setTextAlign(Paint.Align.CENTER);
			mCanvas.drawText(mItemLables[i], startX, startY + Y_AXES_THICK / 2,
					p);

			// 柱
			float itemHeight = (float) mItemValues[i] / mItemValueUnit
					* mYScaleMarkInterval;
			float left = startX - mItemWidth / 2;
			float top = startY - itemHeight;
			float rigth = startX + mItemWidth / 2;
			float bottom = startY;

			p.setColor(Color.GREEN);
			mCanvas.drawRect(left, top, rigth, bottom, p);

			// 柱上方的数值
			p.setTextAlign(Paint.Align.CENTER);
			p.setColor(Color.RED);
			mCanvas.drawText(String.valueOf(mItemValues[i]), startX, top
					- MARGIN, p);
			// D.e(left + "-" + top + "-" + rigth + "-" + bottom + "-" +
			// itemHeight);

		}

		// y轴线
		p.setColor(Color.GREEN);
		drawLine(X_AXES_THICK, mHeight - Y_AXES_THICK, X_AXES_THICK, 0);
		// 画箭头
		drawLine(X_AXES_THICK, 0, X_AXES_THICK - 6, 8);
		drawLine(X_AXES_THICK, 0, X_AXES_THICK + 6, 8);
		for (int i = 0; i < mYScaleCount; i++) {
			int startX = X_AXES_THICK;
			int startY = mHeight - Y_AXES_THICK - mYScaleMarkInterval * (i + 1);
			int endX = X_AXES_THICK + mScaleLength;
			int endY = mHeight - Y_AXES_THICK - mYScaleMarkInterval * (i + 1);
			// 刻度
			p.setColor(Color.GREEN);
			drawLine(startX, startY, endX, endY);

			// 文字
			p.setColor(Color.GREEN);
			p.setTextAlign(Paint.Align.RIGHT);
			mCanvas.drawText(String.valueOf((i + 1) * mItemValueUnit), startX
					- MARGIN, startY + MARGIN, p);
		}

	}

	private void drawTitle() {
		p.setTextAlign(Paint.Align.CENTER);
		mCanvas.drawText(mChartTitle, mWidth / 2, MARGIN * 3, p);

	}

	private void drawLine(int startX, int startY, int endX, int endY) {
		mCanvas.drawLine(startX, startY, endX, endY, p);
	}

}
