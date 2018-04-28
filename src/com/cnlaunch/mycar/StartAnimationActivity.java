package com.cnlaunch.mycar;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

/**
 * @description 开机启动动画
 * @author 向远茂
 * @date：2012-4-6
 */
public class StartAnimationActivity extends Activity
{
    // 显示动画的组件
    private ImageView imgeView;
    // Frame动画
    private AnimationDrawable animDrawbble;

    /* 启动动画相关代码 */
    Timer timerStartAnim = new Timer();
    Timer timerStopAnim = new Timer();
    private SharedPreferences sp; // 取得系统SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 去掉标题，全屏显示启动动画
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        // 实例化组件
        imgeView = (ImageView) findViewById(R.id.image_splash);

        // 获得背景（图片形成的动画）
        animDrawbble = (AnimationDrawable) this.imgeView.getBackground();

        // 动画需要事件触发，使用定时器来触发。
        timerStartAnim.schedule(taskStartAnim, 500);

        // 这个定时器用来显示主界面视图，另外也有延迟的作用，因为onCreate函数里面代码执行的速度太快的话，根本看不到启动动画。
        timerStopAnim.schedule(taskStopAnim, 2000);
    }

    TimerTask taskStartAnim = new TimerTask()
    {
        public void run()
        {
            Message message = new Message();
            message.what = UsercenterConstants.START_ANIMATION;
            mHandler.sendMessage(message);
            // 在启动动画的同时
            // 取得存储器
            sp = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        }
    };

    TimerTask taskStopAnim = new TimerTask()
    {
        public void run()
        {
            Message message = new Message();
            message.what = UsercenterConstants.STOP_ANIMATION;
            mHandler.sendMessage(message);

        }
    };

    /* 启动动画 */
    public void startAnim()
    {

        animDrawbble.start();
    }

    /**
     * 停止动画
     */
    public void stopAnim()
    {

        animDrawbble.stop();
        timerStartAnim.cancel();
        timerStopAnim.cancel();
        // 加载主界面前的引导流程
        guideFlow();
        this.finish();
    }

    // 处理登录消息
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case UsercenterConstants.START_ANIMATION:
                    startAnim();
                    break;

                case UsercenterConstants.STOP_ANIMATION:
                    stopAnim();
                    break;
            }
        }
    };

    /**
     * 加载主界面前的引导流程
     */
    private void guideFlow()
    {
        // 如果是第一次使用本系统
        if (sp.getBoolean(Constants.IS_FIRST_USE, true))
        {

            // 设置是否第一次使用本系统为：否
            if (sp != null)
            {
                sp.edit().putBoolean(Constants.IS_FIRST_USE, true).commit();
            }
            displayLegalTerms();
        }
        else
        // 如果不是第一次使用本系统
        {
            // 是否同意法律条款
            if (!sp.getBoolean(Constants.IS_AGREE_LEGAL_TERMS, false))
            {
                // 显示法律条款
                displayLegalTerms();
            }
            else
            // 如果已经同意了法律条款直接进入主界面
            {
                Intent intent;
                if (getAccounts() == null)
                {
                    // 启动
                    intent = new Intent(this, LoginActivity.class);
                }
                else
                {
                    intent = new Intent(this, MyCarActivity.class);
                }
                // 启动主界面
                startActivity(intent);
            }
        }
    }

    /**
     * 显示法律条款
     */
    public void displayLegalTerms()
    {
        // 启动法律条款界面
        Intent intent = new Intent(this, DisplayLegalTermsActivity.class);
        startActivity(intent);
    }

    /**
     * 从SharedPreferences里面取出登录记录，key是CC号码，value就是密码
     * @return
     */
    private String[] getAccounts()
    {
        // 从SharedPreferences里面取出登录记录
        SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        Map accountsMap = loginSP.getAll(); // 得到所有的登录记录
        String[] accountsArray;
        // key是CC号码，value就是密码
        Set accountsSet = accountsMap.keySet();
        if (accountsSet != null && accountsSet.size() > 0)
        {
            accountsArray = new String[accountsSet.size()];
            Iterator it = accountsSet.iterator();
            int i = 0;
            while (it.hasNext())
            {
                accountsArray[i++] = it.next().toString();
            }
            return accountsArray;
        }
        return null;
    }
}
