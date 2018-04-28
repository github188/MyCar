package com.cnlaunch.mycar.im.receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.ImSession;

public class LSCallSomeoneFriendHimSelfOnlineReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.SERVER_CALLSOMEONEFRIEND_HIMSELFONLINE;
	private Context mContext;
	private static MediaPlayer mMediaPlayer = null;

	public LSCallSomeoneFriendHimSelfOnlineReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		String userUid = letter.getContent();
		if (userUid != null) {
			ImSession.getInstence().addOnlineFriend(userUid);
			playSound();
		}
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
			mMediaPlayer = MediaPlayer
					.create(mContext, R.raw.im_someone_online);
			mMediaPlayer.start();
		}
	}
}
