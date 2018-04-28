package com.cnlaunch.mycar.manager;
import android.content.Context; 
import android.util.AttributeSet; 
import android.widget.TextView;

public class TextButton extends TextView {

    public TextButton(Context context) { 
        super(context); 
    }

    public TextButton(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    }

    public TextButton(final Context context, AttributeSet attrs) { 
        this(context, attrs, 0);
    }

}