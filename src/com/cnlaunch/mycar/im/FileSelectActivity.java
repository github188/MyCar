package com.cnlaunch.mycar.im;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.im.common.ImConstant;

public class FileSelectActivity extends BaseActivity {
	private ListView listview_files;
	private Button button_parent_folder;
	private File mCurrentFolder;
	private File mSdcardFolder = Environment.getExternalStorageDirectory();
	private List<File> mFileList = new ArrayList<File>();
	private ArrayAdapter<File> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_file_select, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_choose_file);
		setCustomeTitleRight("");

		findView();
		addListener();
	}

	public void onStart() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			freshList(mSdcardFolder);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.im_error_no_sdcard_so_fail_to_choose_file);
			builder.setPositiveButton(R.string.im_ensure,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							FileSelectActivity.this.finish();
						}
					});
		}

		super.onStart();
	}

	private void findView() {
		listview_files = (ListView) findViewById(R.id.listview_files);
		button_parent_folder = (Button) findViewById(R.id.button_parent_folder);
	}

	private void addListener() {
		button_parent_folder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentFolder != null
						&& mCurrentFolder.getParentFile() != null
						&& !mCurrentFolder.getAbsolutePath().equals(
								mSdcardFolder.getAbsolutePath())) {
					freshList(mCurrentFolder.getParentFile());
				} else {
					Toast.makeText(FileSelectActivity.this, R.string.im_is_root_folder,
							Toast.LENGTH_LONG);
				}
			}
		});

		mAdapter = new ArrayAdapter<File>(FileSelectActivity.this,
				R.layout.im_file_select_list_item, R.id.textview_file_name,
				mFileList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View v = super.getView(position, convertView, parent);
				File file = mFileList.get(position);
				((TextView) (v.findViewById(R.id.textview_file_name)))
						.setText(file.getName());
				ImageView imageview_icon = (ImageView) v
						.findViewById(R.id.imageview_icon);
				if (file.isFile()) {
					imageview_icon.setImageResource(R.drawable.im_ic_file);
				} else {
					imageview_icon.setImageResource(R.drawable.im_ic_folder);
				}
				return v;
			}
		};

		listview_files.setAdapter(mAdapter);
		listview_files.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File file = mFileList.get(position);
				if (file.isDirectory()) {
					freshList(file);
				} else {
					setResult(file);
				}

			}
		});

	}

	private void freshList(File file) {
		if (file == null) {
			return;
		}
		mCurrentFolder = file;
		mFileList.clear();
		File[] files = file.listFiles();
		for (File f : files) {
			mFileList.add(f);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void setResult(File file) {
		if (file.length() >= ImConstant.UPLOAD_FILE_SIZE_MAX) {
			Toast.makeText(this, R.string.im_file_size_limited, Toast.LENGTH_SHORT).show();
			return;
		}

		mCurrentFolder = file;
		Intent intent = new Intent();
		intent.putExtra("data", file.getAbsolutePath());
		setResult(RESULT_OK, intent);
		this.finish();

	}
}