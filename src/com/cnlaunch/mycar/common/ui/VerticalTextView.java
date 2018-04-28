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
 * <���ܼ���> �Զ���һ���ؼ�ʵ���ı����� <������ϸ����> androidԭ���ؼ�TextView����֧�����ţ���չViewʵ��һ��
 * �������ŵĿؼ����������ĺ��������Բ��ò�ͬ��������ʽ
 * @author xiangyuanmao
 * @version 1.0 2012-7-24
 * @since DBS V100
 */
public class VerticalTextView extends TextView
{
    private Paint paint; // ����
    Context mContext;

    /**
     * ������ ���ʼ��ʱ���õĹ�����
     * @param context Ӧ�ñ��ؼ���������
     * @param attrs ���Բ�������
     * @since DBS V100
     */
    public VerticalTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        paint = new Paint(); // ��ʼ������
        paint.setColor(R.color.main_in_diagonse);// ����������ɫ
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.main_in_diagonse_text_size));// ���������С
        paint.setAntiAlias(true);
    }

    /**
     * ��дonDraw���������ƿؼ�����Ļ
     * @param canvas ����
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