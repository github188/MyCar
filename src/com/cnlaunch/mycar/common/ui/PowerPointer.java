package com.cnlaunch.mycar.common.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.cnlaunch.mycar.R;

public class PowerPointer extends View {

	private float mPointerOffset = 0;
	private int mPointerImageResId = 0;
	private int mMaskImageResId = 0;
	private Bitmap mPointerImage = null;
	private Bitmap mMaskImage = null;
	private Bitmap mCache = null;
	private float mValue = 0;

	private int mPointerOritation = 1;

	private int mHeight = 0;
	private int mWidth = 0;

	private Paint mPaint;

	public PowerPointer(Context context) {
		super(context);
	}

	public PowerPointer(final Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PowerPointer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		DisplayMetrics dm = context.getApplicationContext().getResources()
				.getDisplayMetrics();

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PowerPointer, defStyle, 0);

		mPointerOffset = a.getDimension(R.styleable.PowerPointer_pointerOffset,
/*hwy 2012-11-23*/				/*50*/0);
		mValue = a.getFraction(R.styleable.PowerPointer_pointerValue, 100, 100,
				0);

		if (mValue > 100) {
			mValue = 100;
		}
		if (mValue < 0) {
			mValue = 0;
		}

		mMaskImageResId = a
				.getResourceId(R.styleable.PowerPointer_maskImage, 0);
		mPointerImageResId = a.getResourceId(
				R.styleable.PowerPointer_pointerImage, 0);
		mPointerOritation = a.getInt(R.styleable.PowerPointer_pointerOritation,
				1);

		if (mMaskImageResId <= 0) {
			throw new IllegalArgumentException("maskImage属性不能为空");
		}

		if (mPointerImageResId <= 0) {
			throw new IllegalArgumentException("pointerImage属性不能为空");
		}

		mMaskImage = BitmapFactory.decodeResource(context.getResources(),
				mMaskImageResId);
		mPointerImage = BitmapFactory.decodeResource(context.getResources(),
				mPointerImageResId);

		if (mMaskImage.getHeight() != mPointerImage.getHeight()
				|| mMaskImage.getWidth() != mPointerImage.getWidth()) {
			throw new IllegalArgumentException(
					"pointerImage与maskImage所使用的图片长宽不一致");
		}

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		int w = mMaskImage.getWidth();
		int h = mMaskImage.getHeight();
		mCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mWidth = this.getWidth();
		mHeight = this.getHeight();
		
		int w = mMaskImage.getWidth();
		int h = mMaskImage.getHeight();

		int pointerWidth = (int) ((w - mPointerOffset) * (mValue / 100) + mPointerOffset);
		if (pointerWidth > w) {
			pointerWidth = w;
		}

		if (pointerWidth < mPointerOffset) {
			pointerWidth = (int) mPointerOffset;
		}

		int i = 0;
		for (i = 0; i < pointerWidth; i++) {
			for (int j = 0; j < h; j++) {
				
				int maskColor = mMaskImage.getPixel(i, j);
				int pointerColor = mPointerImage.getPixel(w - pointerWidth + i, j);
				if (Color.alpha(maskColor) == 0) {
					mCache.setPixel(i, j, pointerColor);
				} else {
					mCache.setPixel(i, j, maskColor);
				}

			}
		}

		for (; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int maskColor = mMaskImage.getPixel(i, j);
				if (Color.alpha(maskColor) == 0) {
					mCache.setPixel(i, j, Color.GRAY);
				} else {
					mCache.setPixel(i, j, maskColor);
				}
			}
		}

		canvas.drawBitmap(mCache, 0, 0, mPaint);
	}

	public void setValue(int value) {
		if (value <= 100) {
			this.mValue = value;
		} else {
			this.mValue = 100;
		}
		this.invalidate();
	}

}
