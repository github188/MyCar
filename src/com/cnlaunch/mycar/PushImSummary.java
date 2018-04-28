package com.cnlaunch.mycar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.cnlaunch.mycar.common.utils.UserSession;
import com.cnlaunch.mycar.im.ImLoginActivity;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.database.ImData.LastChat;

/**
 * 我的社区推送的消息
 * 
 * @author xuzhuowei
 * 
 */
public class PushImSummary implements IPushSummary {

	@Override
	public void push(String cc, Context context) {

		final DBSCarSummaryInfo infoList = new DBSCarSummaryInfo(context);

		final Context mContext = context;

		new Thread() {
			public void run() {
				if (UserSession.IsSomeoneLogined()){
					int mMsgCount = 0;

					ContentResolver resolver = mContext.getContentResolver();
					Cursor c = null;
					try {
						c = resolver
								.query(LastChat.CONTENT_URI,
										new String[] { "sum( "
												+ LastChat.UNREAD_COUNT
												+ ") as CountTotal" }, null,
										null, null);
						if (c != null && c.moveToNext()) {
							mMsgCount = c.getInt(0);
						}
					} finally {
						if (c != null) {
							c.close();
						}
					}

					if (mMsgCount > 0) {

						DBSCarInfo infoItem = new DBSCarInfo(
								mContext.getString(R.string.im_push_str),
								String.valueOf(mMsgCount),
								mContext.getString(R.string.im_push_unit_tiao));
						DBSCarSummaryInfo.IDBSCarObserve infoItemObserve = new DBSCarSummaryInfo.IDBSCarObserve() {
							@Override
							public void execute() {
								Intent intent = new Intent(mContext,
										ImLoginActivity.class);
								intent.putExtra(ImConstant.STR_IM_JUMP_TARGET,
										ImConstant.JUMP_TARGET_CHAT_LOG);
								mContext.startActivity(intent);

							}
						};
						infoList.register(PushKeys.KEY_IM_PREFIX, infoItem,
								infoItemObserve);
					} else {
						infoList.unRegister(PushKeys.KEY_IM_PREFIX);
					}
				} else {
					infoList.unRegister(PushKeys.KEY_IM_PREFIX);
				}
			};
		}.start();

	}

}
