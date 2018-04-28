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
 * @description ������������
 * @author ��Զï
 * @date��2012-4-6
 */
public class StartAnimationActivity extends Activity
{
    // ��ʾ���������
    private ImageView imgeView;
    // Frame����
    private AnimationDrawable animDrawbble;

    /* ����������ش��� */
    Timer timerStartAnim = new Timer();
    Timer timerStopAnim = new Timer();
    private SharedPreferences sp; // ȡ��ϵͳSharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // ȥ�����⣬ȫ����ʾ��������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        // ʵ�������
        imgeView = (ImageView) findViewById(R.id.image_splash);

        // ��ñ�����ͼƬ�γɵĶ�����
        animDrawbble = (AnimationDrawable) this.imgeView.getBackground();

        // ������Ҫ�¼�������ʹ�ö�ʱ����������
        timerStartAnim.schedule(taskStartAnim, 500);

        // �����ʱ��������ʾ��������ͼ������Ҳ���ӳٵ����ã���ΪonCreate�����������ִ�е��ٶ�̫��Ļ�����������������������
        timerStopAnim.schedule(taskStopAnim, 2000);
    }

    TimerTask taskStartAnim = new TimerTask()
    {
        public void run()
        {
            Message message = new Message();
            message.what = UsercenterConstants.START_ANIMATION;
            mHandler.sendMessage(message);
            // ������������ͬʱ
            // ȡ�ô洢��
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

    /* �������� */
    public void startAnim()
    {

        animDrawbble.start();
    }

    /**
     * ֹͣ����
     */
    public void stopAnim()
    {

        animDrawbble.stop();
        timerStartAnim.cancel();
        timerStopAnim.cancel();
        // ����������ǰ����������
        guideFlow();
        this.finish();
    }

    // �����¼��Ϣ
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
     * ����������ǰ����������
     */
    private void guideFlow()
    {
        // ����ǵ�һ��ʹ�ñ�ϵͳ
        if (sp.getBoolean(Constants.IS_FIRST_USE, true))
        {

            // �����Ƿ��һ��ʹ�ñ�ϵͳΪ����
            if (sp != null)
            {
                sp.edit().putBoolean(Constants.IS_FIRST_USE, true).commit();
            }
            displayLegalTerms();
        }
        else
        // ������ǵ�һ��ʹ�ñ�ϵͳ
        {
            // �Ƿ�ͬ�ⷨ������
            if (!sp.getBoolean(Constants.IS_AGREE_LEGAL_TERMS, false))
            {
                // ��ʾ��������
                displayLegalTerms();
            }
            else
            // ����Ѿ�ͬ���˷�������ֱ�ӽ���������
            {
                Intent intent;
                if (getAccounts() == null)
                {
                    // ����
                    intent = new Intent(this, LoginActivity.class);
                }
                else
                {
                    intent = new Intent(this, MyCarActivity.class);
                }
                // ����������
                startActivity(intent);
            }
        }
    }

    /**
     * ��ʾ��������
     */
    public void displayLegalTerms()
    {
        // ���������������
        Intent intent = new Intent(this, DisplayLegalTermsActivity.class);
        startActivity(intent);
    }

    /**
     * ��SharedPreferences����ȡ����¼��¼��key��CC���룬value��������
     * @return
     */
    private String[] getAccounts()
    {
        // ��SharedPreferences����ȡ����¼��¼
        SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
        Map accountsMap = loginSP.getAll(); // �õ����еĵ�¼��¼
        String[] accountsArray;
        // key��CC���룬value��������
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
