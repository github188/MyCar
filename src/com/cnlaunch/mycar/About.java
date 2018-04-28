package com.cnlaunch.mycar;

import com.cnlaunch.mycar.common.config.Constants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
/**
 * @author luxingsong
 * ����Ĺ�����Ϣ
 * �����汾�������;˵������˾��վ����
 * **/
public class About extends Activity implements OnClickListener {
	private final static String TAG = "About";
	private TextView m_version = null;
	private TextView m_builder_version = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        View okButton = findViewById(R.id.about_ok);
        okButton.setOnClickListener(this);
        View projectUrl = findViewById(R.id.project_url);
        projectUrl.setOnClickListener(this);
        m_version = (TextView)findViewById(R.id.mycar_about_version);
        m_version.setText(Constants.MYCAR_VERSION);/*�ͻ�������ĵ�ǰ�汾*/
        m_builder_version = (TextView)findViewById(R.id.mycar_about_builder_version);
        m_builder_version.setText(Constants.MYCAR_BUILDER_VERSION);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.about_ok:
            finish();
            break;
        case R.id.project_url:
//          openProjectUrlInBrowser();
            break;
        }
    }
    
    private void openProjectUrlInBrowser() {
    	Log.d(TAG,"��ʼ����mycar url");
        Uri uri = Uri.parse(getResources().getString(R.string.mycar_about_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
