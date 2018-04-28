package com.cnlaunch.mycar.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.usercenter.UserInfoActivity;

public class ImBaseActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ImSession.isExit()) {
			this.finish();
		}
	}

	public void setContentView(int layoutResID, int layoutTitleResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		getWindow()
				.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutTitleResID);
	}

	protected void setCustomeTitleLeft(int resourceId) {
		TextView textView = (TextView) findViewById(R.id.title_left_text);
		if (textView != null) {
			textView.setText(getText(resourceId));
		}
	}

	protected void setCustomeTitleRight(int resourceId) {
		TextView textView = (TextView) findViewById(R.id.title_right_text);
		if (textView != null) {
			textView.setText(getText(resourceId));
		}
	}

	protected void setCustomeTitleLeft(CharSequence charStr) {
		TextView textView = (TextView) findViewById(R.id.title_left_text);
		if (textView != null) {
			textView.setText(charStr);
		}
	}

	protected void setCustomeTitleRight(CharSequence charStr) {
		TextView textView = (TextView) findViewById(R.id.title_right_text);
		if (textView != null) {
			textView.setText(charStr);
		}
	}

	@Override
	public void onResume() {
		if (ImSession.isExit()) {
			this.finish();
		} else if (!ImSession.isLogined()) {
			showReloginDailog();
		}
		super.onResume();
	}

	protected void showReloginDailog() {
		Intent intent = new Intent();
		intent.setClass(this, ReLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	protected boolean toggleSound() {
		SharedPreferences store = getSharedPreferences(
				ImConstant.IM_SHARED_PREFERENCES_NAME
						+ ImSession.getInstence().getUseruid(),
				Context.MODE_PRIVATE);
		boolean soundFlag = store.getBoolean(ImConstant.CONFIG_SOUND, true);
		Editor editor = store.edit();
		soundFlag = !soundFlag;
		editor.putBoolean(ImConstant.CONFIG_SOUND, soundFlag);
		editor.commit();
		return soundFlag;
	}

	/**
	 * 响应底部菜单按钮事件
	 * 
	 * @param v
	 */
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.im_textbutton_menu_chatlog:
			showChatLog();
			break;
		case R.id.im_textbutton_menu_friend:
			showFriend();
			break;
		case R.id.im_textbutton_menu_sendfile:
			showSendFile();
			break;
		case R.id.im_textbutton_menu_add_friend:
			showAddFriend();
			break;
		default:
			break;
		}
	}

	private void showAddFriend() {
		if (!AddFriendActivity.class.isInstance(this)) {
			startActivity(new Intent(this, AddFriendActivity.class));
			this.finish();
			overridePendingTransition(0, 0);
		}
	}

	private void showSendFile() {
		if (!SendFileActivity.class.isInstance(this)) {
			startActivity(new Intent(this, SendFileActivity.class));
			this.finish();
			overridePendingTransition(0, 0);
		}
	}

	private void showFriend() {
		if (!FriendListActivity.class.isInstance(this)) {
			startActivity(new Intent(this, FriendListActivity.class));
			this.finish();
			overridePendingTransition(0, 0);
		}
	}

	private void showChatLog() {
		if (!ChatLogActivity.class.isInstance(this)) {
			startActivity(new Intent(this, ChatLogActivity.class));
			this.finish();
			overridePendingTransition(0, 0);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.e("IM","in menu");
		menu.clear();
		if (isSoundOn()) {
			menu.add(0, 1, 1, R.string.im_sound_off);
		} else {
			menu.add(0, 1, 1, R.string.im_sound_on);
		}
		menu.add(0, 2, 2, R.string.im_profile_eidt);
		menu.add(0, 3, 3, R.string.im_logout);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			toggleSound();
			if (isSoundOn()) {
				item.setTitle(R.string.im_sound_off);
			} else {
				item.setTitle(R.string.im_sound_on);
			}
			break;
		case 2:
			startActivity(new Intent(this, UserInfoActivity.class));
			break;
		case 3:
			ImMsgQueue.addMessage(ImMsgIds.ORDER_LOGOUT);
			this.finish();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	protected boolean isSoundOn() {

		SharedPreferences store = getSharedPreferences(
				ImConstant.IM_SHARED_PREFERENCES_NAME
						+ ImSession.getInstence().getUseruid(),
				Context.MODE_PRIVATE);
		return store.getBoolean(ImConstant.CONFIG_SOUND, true);
	}

}