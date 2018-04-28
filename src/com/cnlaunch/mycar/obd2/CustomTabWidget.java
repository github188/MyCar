package com.cnlaunch.mycar.obd2;

import com.cnlaunch.mycar.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CustomTabWidget
{
    private int mTabNameResId;
    private int mTabImageResId;
    private boolean mIsChecked = false;
    private Context mContext;
    private LinearLayout mView;

    public CustomTabWidget(Context context, int tabNameResId, int tabImageResId, boolean isChecked)
    {
        this.mTabNameResId = tabNameResId;
        this.mTabImageResId = tabImageResId;
        this.mIsChecked = isChecked;
        this.mContext = context;
        mView = create();
    }

    private LinearLayout create()
    {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        if (mIsChecked)
        {
            layout.setBackgroundResource(R.drawable.tab_bar_button_bg_pressed);
        }
        else
        {
            layout.setBackgroundResource(R.drawable.tab_bar_button_bg_normal);
        }

        ImageView iv = new ImageView(mContext);
        iv.setImageResource(mTabImageResId);
        layout.addView(iv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // TextView tv = new TextView(mContext);
        // tv.setGravity(Gravity.CENTER);
        // tv.setSingleLine(true);
        // tv.setText(mTabNameResId);
        // layout.addView(tv, new LinearLayout.LayoutParams(
        // LinearLayout.LayoutParams.FILL_PARENT,
        // LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    public CustomTabWidget checked(boolean isChecked)
    {
        mIsChecked = isChecked;

        if (mIsChecked)
        {
            mView.setBackgroundResource(R.drawable.tab_bar_button_bg_pressed);
        }
        else
        {
            mView.setBackgroundResource(R.drawable.tab_bar_button_bg_normal);
        }
        return this;
    }

    public View getView()
    {
        return mView;
    }
}
