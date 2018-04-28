package com.cnlaunch.mycar;

import android.util.Log;

public class TestUserOnlineState implements IUserOnlineState
{

    @Override
    public void loginSuccess()
    {
        // TODO Auto-generated method stub

        Log.d("²âÊÔµÇÂ¼×´Ì¬¸Ä±ä-------------", "µÇÂ¼³É¹¦£¬CCºÅ£º" + MyCarActivity.cc);
    }

    @Override
    public void logout()
    {
        // TODO Auto-generated method stub
        Log.d("²âÊÔµÇÂ¼×´Ì¬¸Ä±ä-------------", "×¢ÏúµÇÂ¼³É¹¦£¡");
    }

}
