package com.cnlaunch.mycar.im.common;

import com.cnlaunch.mycar.im.action.FaceManager;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

public class ChatMessageUtil {

	public static void putInTextView(final TextView textview, String chatMessage) {

		if (textview == null || chatMessage == null) {
			return;
		}

		textview.setText("");
		textview.setText(Html.fromHtml(toHtml(chatMessage), new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = FaceManager.getChatFace(Integer.parseInt(source));

				Drawable d = textview.getContext().getResources()
						.getDrawable(id);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		}, null));
	}

	private static String toHtml(String chatMessage) {
		if (chatMessage == null) {
			return chatMessage;
		}

		StringBuilder sb = new StringBuilder();
		int len = chatMessage.length();
		int lastPosition = 0;
		int position = chatMessage.indexOf('/');
		final int FACE_NAME_LEN = 2;
		while (position != -1 && position + FACE_NAME_LEN < len) {
			String faceName = chatMessage.substring(position, position + 1
					+ FACE_NAME_LEN);
			boolean isMatch = false;
			for (int i = 0; i < FACE_IDS.length; i++) {
				if (faceName.equals(FACES_NAMES[i])) {
					sb.append(chatMessage.subSequence(lastPosition,
							position));
					sb.append("<img src='" + FACE_IDS[i] + "'/>");
					position += FACE_NAME_LEN + 1;
					isMatch = true;
					break;
				}
			}
			if (!isMatch) {
				sb.append(chatMessage.subSequence(lastPosition, ++position));
			}

			lastPosition = position;
			position = chatMessage.indexOf('/', lastPosition);
		}
		sb.append(chatMessage.subSequence(lastPosition, chatMessage.length()));

		return sb.toString();
	}

	public static final int[] FACE_IDS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
			28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
			45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
			62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
			79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
			96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
			110, 111, 112, 113, 114, 115 };

	public static final String[] FACES_NAMES = { "/微笑", "/害怕", "/好色", "/流血",
			"/墨镜", "/哭泣", "/害羞", "/闭嘴", "/瞌睡", "/大哭", "/惭愧", "/愤怒", "/调皮",
			"/开心", "/好奇", "/悲伤", "/装酷", "/流汗", "/生气", "/呕吐", "/偷笑", "/脸红",
			"/装蒜", "/蔑嘴", "/无聊", "/发困", "/惊讶", "/无奈", "/同意", "/小兵", "/奋斗",
			"/大骂", "/问号", "/小声", "/头晕", "/咆哮", "/炸黑", "/骷髅", "/打头", "/拜拜",
			"/蔑视", "/挖鼻", "/鼓掌", "/无语", "/奸诈", "/左理", "/右理", "/哈欠", "/鄙视",
			"/无辜", "/小哭", "/奸笑", "/嘟嘴", "/啊啊", "/可怜", "/菜刀", "/西瓜", "/啤酒",
			"/篮球", "/乒乓", "/咖啡", "/吃饭", "/猪头", "/玫瑰", "/凋谢", "/飞吻", "/爱心",
			"/心碎", "/蛋糕", "/闪电", "/炸弹", "/匕首", "/足球", "/甲虫", "/大便", "/晚安",
			"/太阳", "/礼物", "/拥抱", "/高手", "/低手", "/合作", "/胜利", "/切磋", "/勾引",
			"/拳头", "/尾指", "/摇滚", "/摇指", "/好的", "/感冒", "/猫头", "/狗头", "/金钱",
			"/灯泡", "/香槟", "/音乐", "/药片", "/亲嘴", "/聚餐", "/电话", "/时间", "/信息",
			"/电视", "/海豹", "/女孩", "/男孩", "/歌手", "/香槟", "/可乐", "/感冒", "/晴天",
			"/雪人", "/星星", "/女生", "/男生" };
}
