package com.cnlaunch.mycar.im.receiver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.UserModel;

public class LSCallAllSomeoneOnlineReceiver  extends LetterReceiver {

	private final int mId = ImConstant.Reciver.SERVER_CALLALL_SOMEONEONLINE;
	private Context mContext;
	private static MediaPlayer mMediaPlayer = null;

	public LSCallAllSomeoneOnlineReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		UserModel userModel = JsonConvert.fromJson(letter.getContent(), UserModel.class);
		ImSession.getInstence().addOnlineUsers(userModel);
		
//		ContentValues values = new ContentValues();
//		values.put(Friends.GROUPNAME, Friends.GROUP_NAME_STRANGER);
//		values.put(Friends.GROUPUID, Friends.GROUP_UID_STRANGER);
//		values.put(Friends.USERUID, userModel.getUseruid());
//		values.put(Friends.NICKNAME, userModel.getNickname());
//		values.put(Friends.FACEID, userModel.getFaceId());
//		values.put(Friends.CCNO, userModel.getCcno());
//		
//		ContentResolver resolver = mContext.getContentResolver();
//		resolver.bulkInsert(Friends.CONTENT_URI, new ContentValues[]{values});
		playSound();
	}
	
	private void playSound() {
		SharedPreferences store = mContext.getSharedPreferences(
				ImConstant.IM_SHARED_PREFERENCES_NAME
						+ ImSession.getInstence().getUseruid(),
				Context.MODE_PRIVATE);
		boolean soundFlag = store.getBoolean(ImConstant.CONFIG_SOUND, true);
		if (!soundFlag) {
			return;
		}
		if (ImSession.getInstence().getCurrentActivity() == ImSession.ACTIVITY_OTHER) {
			// 播放好友上线提示音
			if (mMediaPlayer != null) {
				mMediaPlayer.release();
			}
			mMediaPlayer = null;
			mMediaPlayer = MediaPlayer.create(mContext, R.raw.im_someone_online);
			mMediaPlayer.start();
		}
	}

}
