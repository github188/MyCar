package com.cnlaunch.mycar;

import android.util.Log;

public class TestUserOnlineState implements IUserOnlineState
{

    @Override
    public void loginSuccess()
    {
        // TODO Auto-generated method stub

        Log.d("���Ե�¼״̬�ı�-------------", "��¼�ɹ���CC�ţ�" + MyCarActivity.cc);
    }

    @Override
    public void logout()
    {
        // TODO Auto-generated method stub
        Log.d("���Ե�¼״̬�ı�-------------", "ע����¼�ɹ���");
    }

}
