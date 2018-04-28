package com.cnlaunch.mycar.diagnose.loveCarHealth;


import com.cnlaunch.mycar.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@Deprecated
public class Main extends Activity implements OnClickListener {
	private Intent serviceIntent;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnStartService:
			startService(serviceIntent);
			Toast.makeText(getApplicationContext(), "statrt", 1);
			break;
		case R.id.btnStopService:
			stopService(serviceIntent);
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lovecar_main);
		Button btnStartService = (Button) findViewById(R.id.btnStartService);
		Button btnStopService = (Button) findViewById(R.id.btnStopService);
		serviceIntent = new Intent(this, LoveCarService.class);
		btnStartService.setOnClickListener(this);
		btnStopService.setOnClickListener(this);
	}
}