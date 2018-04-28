package com.cnlaunch.mycar.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ChatMessageUtil;

public class FacesActivity extends ImBaseActivity {
	private GridView gridview_face_board;
	private List<HashMap<String, Integer>> mFaceList = new ArrayList<HashMap<String, Integer>>();
	public final static String FACE_NAME = "face_name";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.im_face_board);

		findView();
		addListener();
	}

	private void findView() {
		gridview_face_board = (GridView) findViewById(R.id.gridview_face_board);
	}

	private void addListener() {
		for (Integer faceId : ChatMessageUtil.FACE_IDS) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map.put("faceResId", FaceManager.getChatFace(faceId));
			mFaceList.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(FacesActivity.this,
				mFaceList, R.layout.im_face_board_item,
				new String[] { "faceResId" },
				new int[] { R.id.imageview_chat_face }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((ImageView) v.findViewById(R.id.imageview_chat_face))
						.setImageResource((Integer) mFaceList.get(position)
								.get("faceResId"));
				return v;
			}

		};
		gridview_face_board.setAdapter(adapter);
		gridview_face_board.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("IM", "gridview_face_board.setOnItemClickListener()");
				String faceName = ChatMessageUtil.FACES_NAMES[position];

				setResult(RESULT_OK, new Intent().putExtra(FACE_NAME, faceName));
				FacesActivity.this.finish();
			}
		});

	}
}