package com.cnlaunch.mycar.common.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author zhangweiwei
 * @version 2011-11-24����5:29:10 ��˵��:�Զ���һ��TextView��ʵ������Ƶ�Ч��
 */
public class MarqueTextView extends TextView {
	public MarqueTextView(Context con) {
		super(con);
	}

	public MarqueTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
	}
}
