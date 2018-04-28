package com.cnlaunch.mycar.updatecenter.http;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;

public class DefaultHttpListener implements HttpListener
{

	@Override
	public void onHttpStart(String url)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onHttpDownloadProgress(int percent, int speed,
			int restHours, int restMinutes, int restSeconds)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onHttpUploadProgress(int percent, int speed,
			int restHours, int restMinutes, int restSeconds)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onHttpException(Object reason)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onHttpFinish(Object target,Object extra)
	{
		// TODO Auto-generated method stub

	}
    protected void showErrorInfoDialog(String message, Activity activity)
    {
        final CustomAlertDialog dlg = new CustomAlertDialog(activity);
        dlg.setTitle(R.string.upc_error);
        dlg.setMessage(message);
        dlg.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
            {
                if(aKeyCode==KeyEvent.KEYCODE_BACK)
                {
                    dlg.dismiss();
                }
                return false;
            }
        });
        dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
        {
            @Override
            public void onClick(View aV)
            {
                dlg.dismiss();
            }
        });
        dlg.show();
    }
}
