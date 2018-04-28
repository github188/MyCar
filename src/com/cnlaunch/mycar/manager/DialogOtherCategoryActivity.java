package com.cnlaunch.mycar.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.manager.bll.CategoryBll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class DialogOtherCategoryActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manager_other_category_dialog);
		setTitle(R.string.manager_otherCategory);

		String[] custom_category = getIntent().getStringArrayExtra(
				"com.cnlaunch.mycar.manager.CustomCategorys");
		if(custom_category==null){
			this.finish();
			return;
		}
		//显示处定义项目
		showGridView(custom_category);

		//显示保存按钮
		showSaveButton();
	}
	
	private void showSaveButton() {
		Button button_save = (Button)findViewById(R.id.button_manager_custom_category);
		final EditText edittext_manager_custom_category = (EditText)findViewById(R.id.edittext_manager_custom_category);
		button_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String,String> categroyMap = (new CategoryBll(DialogOtherCategoryActivity.this,
						getHelper())).getAllLocalCategory(DialogOtherCategoryActivity.this);
				String key = edittext_manager_custom_category.getText().toString().trim();
				boolean flag = true;
				if(categroyMap.containsKey(key)){
					flag = false;
					edittext_manager_custom_category.setText(null);
					edittext_manager_custom_category.setHint(R.string.manager_custom_category);
					edittext_manager_custom_category.setFocusable(true);					
				}
				if(flag){
					setResultValue(StringUtil.filterBlank(key));
				}else{
					displayToast(R.string.manager_custom_category_exist);;
				}
			}
		});
		
	}

	/**展示按钮列表
	 * @param custom_category 支出类别数组
	 */
	private void showGridView(String[] custom_category) {
		GridView gridveiw_other_category_button_area = (GridView) findViewById(R.id.gridview_other_category_button_area);

		ArrayList<HashMap<String, Object>> list_button_value = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < custom_category.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemValue", custom_category[i]);
			map.put("ItemText", custom_category[i]);
			list_button_value.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, list_button_value,
				R.layout.manager_other_category_dialog_button_list_item, new String[] { "ItemText" },
				new int[] {R.id.gridview_other_category_button}){

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						@SuppressWarnings("unchecked")
						final HashMap<String, Object> map = (HashMap<String, Object>) this
								.getItem(position);
						Button button = (Button) view
								.findViewById(R.id.gridview_other_category_button);

						button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String value = map.get("ItemValue").toString();
								setResultValue(value);
							}

						});
						return view;
					}
		};
		
		gridveiw_other_category_button_area.setAdapter(adapter);	}

	/**将用户选择的结果返回到记账主页面
	 * @param value
	 */
	private void setResultValue(String value) {
		Intent intent = new Intent(DialogOtherCategoryActivity.this,BillingAddActivity.class);
		intent.putExtra("com.cnlaunch.mycar.manager.OtherCategoryChoosed", value);
		DialogOtherCategoryActivity.this.setResult(RESULT_OK, intent);
		DialogOtherCategoryActivity.this.finish();
	}

}