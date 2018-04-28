package com.cnlaunch.mycar.common.ui;

import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cnlaunch.mycar.R;

/**
 * <功能简述> 自定义一个控件实现文本竖排 <功能详细描述> android原生控件TextView并不支持竖排，扩展View实现一个
 * 可以竖排的控件，其中中文和其他语言采用不同的竖排形式
 * @author xiangyuanmao
 * @version 1.0 2012-7-24
 * @since DBS V100
 */
public class VerticalTextView extends TextView
{
    private Paint paint; // 画笔
    Context mContext;

    /**
     * 构造器 类初始化时调用的构造器
     * @param context 应用本控件的上下文
     * @param attrs 属性参数集合
     * @since DBS V100
     */
    public VerticalTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        paint = new Paint(); // 初始化画笔
        paint.setColor(R.color.main_in_diagonse);// 定义字体颜色
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.main_in_diagonse_text_size));// 定义字体大小
        paint.setAntiAlias(true);
    }

    /**
     * 重写onDraw方法，绘制控件到屏幕
     * @param canvas 画布
     * @see android.view.View#onDraw(android.graphics.Canvas)
     * @since DBS V100
     */
    @Override
    protected void onDraw(Canvas canvas)
    {

        String language = Locale.getDefault().getLanguage();
        if (!language.equals("zh"))
        {
            canvas.rotate(90);
        }
        canvas.drawText(getText().toString(), 10, -10, paint);
        super.onDraw(canvas);

    }

    private int mTextWidth;
    private int mTextHeight;

    private void measureTextWidthAndHeight(String text)
    {
        Rect rect = new Rect();

        paint.setTextSize(mContext.getResources().getDimension(R.dimen.main_in_diagonse_text_size));
        paint.getTextBounds(text, 0, text.length(), rect);
        mTextWidth = rect.right - rect.left;
        mTextHeight = rect.bottom - rect.top;
    }

}