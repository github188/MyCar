package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;

public class DiagnoseQuestionCategoryAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<HashMap<String, List<DiagnoseQuestionCategory>>> listItems;
	private SimpleAdapter adapter;
	public DiagnoseQuestionCategoryAdapter(Context context,
			List<HashMap<String, List<DiagnoseQuestionCategory>>> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.listItems = list;
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TextView titleView;
		LinearLayout detailList;
//		View view2;
		view=null;

		if (view == null) {
			view = (View) inflater.inflate(
					R.layout.diagnose_simple_report_question_item, null);
//			view2 = (View) inflater.inflate(
//					R.layout.diagnose_simple_report_question_detail, null);
		}

		titleView = (TextView) view
				.findViewById(R.id.simple_report_question_detail_text);
		detailList = (LinearLayout) view
				.findViewById(R.id.simple_report_question_detail_list);		
		String title="";
		String docParentId="";
		List<DiagnoseQuestionCategory> questonList = listItems.get(position).get(DiagnoseConstant.V_DOC_PIDID);
		if (questonList != null && questonList.size() > 0) {		
        		DiagnoseQuestionCategory info=(DiagnoseQuestionCategory)questonList.get(0);
        		if(title.equals("")){
        			//类别父类标题
        			title=info.getCategoryParentStr();
        		}
        		if(docParentId.equals("")){
        			//类别父类ID
        			docParentId=info.getCategoryParentId();
        		}	
		}
		//添加故障码详细信息
		  if (questonList != null && questonList.size() > 0) {
				for(DiagnoseQuestionCategory info : questonList){	
					//动态生成故障码页面布局
					LinearLayout layout2=new LinearLayout(context);  
					layout2.setOrientation(LinearLayout.HORIZONTAL);
					//控件位置与大小设置
					LinearLayout.LayoutParams param1 =
			        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			        TextView simple_report_questionNum=new TextView(context);  
			        simple_report_questionNum.setTextSize(13);
			        simple_report_questionNum.setTextColor(Color.BLACK);
			        simple_report_questionNum.setText(info.getQuestionNum());  
			        param1.leftMargin=15;
			        param1.bottomMargin=5;
			        param1.topMargin=5;
			        layout2.addView(simple_report_questionNum,param1);
			       //控件位置与大小设置
			    	LinearLayout.LayoutParams param2 =
					        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			        TextView simple_report_questionInfo2=new TextView(context);			      
			        simple_report_questionInfo2.setTextColor(Color.BLACK);
			        simple_report_questionInfo2.setTextSize(13);			       
			        simple_report_questionInfo2.setText(info.getQuestionInfo());  
			        param2.leftMargin=15;
			        param2.topMargin=5;
			        param2.bottomMargin=5;
			        layout2.addView(simple_report_questionInfo2,param2);  
			        detailList.addView(layout2);
				}
		}
		titleView.setText(title);
//		docIDInfo.clear();
		return view;
	}

}
