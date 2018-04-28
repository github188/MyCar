package com.cnlaunch.mycar.im;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImConstant.FriendKeys;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.model.ConditionGetFriendInfoModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.UserInfoComModel;

public class ImFriendInfoActivity extends ImBaseActivity {
	private Button button_del_friend;
	private TextView textview_ccno;
	private TextView textview_nickname;
	private TextView textview_age;
	private TextView textview_area;
	private ImageView imageview_userhead;
	private CustomProgressDialog mGetFriendInfoProgressDialog;

	private ImMsgObserver mFriendDelObserver;

	private String mTargetUserUid = null;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_friend_info, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_friend_info);
		setCustomeTitleRight("");
		mContext = this;

		if (getIntent() != null
				&& getIntent().hasExtra(ImConstant.FriendKeys.USERUID)) {
			mTargetUserUid = getIntent().getStringExtra(
					ImConstant.FriendKeys.USERUID);
		}
		if (mTargetUserUid == null) {
			this.finish();
			return;
		}

		findView();
		addListener();
		getFriendInfoFromWeb();
		createMsgObserver();

	}

	@Override
	public void onPause() {
		unRegisterMsgObserver();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerMsgObserver();
		super.onResume();
	}

	private void createMsgObserver() {
		mFriendDelObserver = new ImMsgObserver(ImMsgIds.REPLY_DEL_FRIEND, this) {

			@Override
			public void dealMessage(Message msg) {
				ImFriendInfoActivity.this.finish();
			}
		};
	}

	private void registerMsgObserver() {
		unRegisterMsgObserver();
		ImMsgQueue.getInstance().registerObserver(mFriendDelObserver);
	}

	private void unRegisterMsgObserver() {
		ImMsgQueue.getInstance().unRegisterObserver(mFriendDelObserver);
	}

	private void getFriendInfoFromWeb() {
		showFriendInfoProgressDialog();
		final ConditionGetFriendInfoModel model = new ConditionGetFriendInfoModel();
		model.setAppID("");
		model.setSign("");
		model.setAskForUserUID(mTargetUserUid);
		model.setSourceUserUID(ImSession.getInstence().getUseruid());
		new Thread() {
			@Override
			public void run() {
				HttpPost httpRequest = new HttpPost(
						ImConstant.WEB_SERVER_GET_USER_INFO);
				try {
					httpRequest.setEntity(new UrlEncodedFormEntity(model
							.getNameValuePairList(), HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient()
							.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String result = EntityUtils.toString(httpResponse
								.getEntity());

						final UserInfoComModel userInfoComModel = JsonConvert
								.fromJson(result, UserInfoComModel.class);

						new Handler(getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								updateUserInfo(userInfoComModel);
							}

						});

					} else {
						Log.e("AddFriend",
								mContext.getResources()
										.getString(
												R.string.im_net_error_can_not_get_friend_list)
										+ "code:"
										+ httpResponse.getStatusLine()
												.getStatusCode());
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void updateUserInfo(UserInfoComModel userInfoComModel) {
		textview_ccno.setText(userInfoComModel.getCcno());
		textview_nickname.setText(userInfoComModel.getNickName());
		textview_age.setText(String.valueOf(userInfoComModel.getOld()));
		StringBuilder sb = new StringBuilder();
		String country = userInfoComModel.getCommonCountryName();
		String province = userInfoComModel.getCommonProvinceName();
		String city = userInfoComModel.getCommonCityName();
		if (country != null && country.length() > 0 && !country.equals("null")) {
			sb.append(country);
			if (province != null && province.length() > 0
					&& !province.equals("null")) {
				sb.append("-" + province);
				if (city != null && city.length() > 0 && !city.equals("null")) {
					sb.append("-" + city);
				}
			}
		}

		textview_area.setText(sb.toString());
		imageview_userhead.setImageResource(FaceManager
				.getUserFace(userInfoComModel.getFaceId()));
		hideFriendInfoProgressDialog();
	}

	private void hideFriendInfoProgressDialog() {
		if (mGetFriendInfoProgressDialog != null) {
			mGetFriendInfoProgressDialog.dismiss();
		}
	}

	private void showFriendInfoProgressDialog() {
		mGetFriendInfoProgressDialog = new CustomProgressDialog(this);
		mGetFriendInfoProgressDialog.setStyle(true);
		mGetFriendInfoProgressDialog.setMessage(mContext.getResources()
				.getString(R.string.im_getting_friend_info));
		mGetFriendInfoProgressDialog.setTitle(R.string.im_notice);
		mGetFriendInfoProgressDialog.show();
	}

	private void findView() {
		button_del_friend = (Button) findViewById(R.id.button_del_friend);
		textview_ccno = (TextView) findViewById(R.id.textview_ccno);
		textview_nickname = (TextView) findViewById(R.id.textview_nickname);
		textview_age = (TextView) findViewById(R.id.textview_age);
		textview_area = (TextView) findViewById(R.id.textview_area);
		imageview_userhead = (ImageView) findViewById(R.id.imageview_userhead);
	}

	private void addListener() {
		button_del_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = ImMsgIds.ORDER_DEL_FRIEND;
				Bundle data = new Bundle();
				data.putString(FriendKeys.USERUID, mTargetUserUid);
				msg.setData(data);
				ImMsgQueue.getInstance().addMessage(msg);

			}
		});

	}

}