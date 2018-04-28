package com.cnlaunch.mycar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * ����������Ϣ������Եĳ���
 * 
 * @author xuzhuowei
 * 
 */
public class WeatherSetCityActivity extends Activity {
	static final String RESULT_WEATHER_CITY_NAME = "com.cnlaunch.mycar.weather_city";

	EditText weather_edittext_search_city;
	TextView weather_search_result_msg;
	GridView weather_gridView_hot_city;
	ImageView weather_imageview_search_clean;
	View weather_frame_hot_city;
	View weather_frame_search_city;
	ArrayAdapter<String> gridviewAdapter;

	SimpleAdapter listviewAdapter;
	static ListView weather_listview_search_result;
	List<CityProviceModel> list_model_city_all;
	List<HashMap<String, String>> list_map = new ArrayList<HashMap<String, String>>();

	String searchKeyword;
	List<CityProviceModel> list_model_city_search_result = new ArrayList<CityProviceModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_set_city);

		fillCityValue();
		weather_gridView_hot_city = (GridView) findViewById(R.id.weather_gridView_hot_city);
		weather_listview_search_result = (ListView) findViewById(R.id.weather_listview_search_result);
		weather_edittext_search_city = (EditText) findViewById(R.id.weather_edittext_search_city);
		weather_search_result_msg = (TextView) findViewById(R.id.weather_search_result_msg);
		weather_imageview_search_clean = (ImageView) findViewById(R.id.weather_imageview_search_clean);

		weather_frame_hot_city = findViewById(R.id.weather_frame_hot_city);
		weather_frame_search_city = findViewById(R.id.weather_frame_search_city);

		weather_frame_hot_city.setVisibility(View.VISIBLE);
		weather_frame_search_city.setVisibility(View.GONE);

		gridviewAdapter = new ArrayAdapter<String>(WeatherSetCityActivity.this,
				R.layout.weather_gridview_item, hotCities);

		weather_gridView_hot_city.setAdapter(gridviewAdapter);

		weather_gridView_hot_city
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						returnCity(hotCities[(int) id]);

					}
				});

		weather_imageview_search_clean
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						weather_edittext_search_city.setText("");
					}
				});

		weather_edittext_search_city.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ��ʾ�����ť
				if (s.length() > 0) {
					weather_imageview_search_clean.setVisibility(View.VISIBLE);
					weather_frame_hot_city.setVisibility(View.GONE);
					weather_frame_search_city.setVisibility(View.VISIBLE);
					searchCity(s);
				} else {
					searchClean();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		weather_edittext_search_city.setFocusable(true);

		listviewAdapter = new SimpleAdapter(this, list_map,
				R.layout.weather_listview_item, new String[] { "city",
						"province" }, new int[] {
						R.id.weather_listview_item_city,
						R.id.weather_listview_item_province });
		weather_listview_search_result.setAdapter(listviewAdapter);
		weather_listview_search_result
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						returnCity(list_map.get((int) id).get("city"));
					}
				});

	}

	private void searchClean() {
		weather_imageview_search_clean.setVisibility(View.GONE);
		weather_frame_hot_city.setVisibility(View.VISIBLE);
		weather_frame_search_city.setVisibility(View.GONE);
	}

	/**
	 * ��������ؼ��ֲ�ѯ����
	 * 
	 * @param s
	 */
	protected void searchCity(CharSequence s) {
		weather_search_result_msg.setText(s);
		searchKeyword = s.toString();
		freshResultListview();
	}

	/**
	 * ˢ�²�ѯ���
	 */
	private void freshResultListview() {

		if (searchKeyword != null) {

			if (list_model_city_search_result != null) {
				list_model_city_search_result.clear();
			}
			if (list_map != null) {
				list_map.clear();
			}

			if (searchKeyword.length() > 0) {
				list_model_city_search_result = getSearchResult();
				for (int i = 0; i < list_model_city_search_result.size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("city", list_model_city_search_result.get(i).city);
					map.put("province",
							list_model_city_search_result.get(i).provice);
					list_map.add(map);
				}
			}
			listviewAdapter.notifyDataSetChanged();
		}

	}

	private List<CityProviceModel> getSearchResult() {
		List<CityProviceModel> ret = new ArrayList<CityProviceModel>();
		for (int i = 0; i < list_model_city_all.size(); i++) {
			if (list_model_city_all.get(i).city.contains(searchKeyword)
					|| list_model_city_all.get(i).cityLetter
							.indexOf(searchKeyword) == 0) {
				ret.add(new CityProviceModel(list_model_city_all.get(i)));
			}
		}
		return ret;
	}

	/**
	 * ���������
	 * 
	 * @param cityName
	 */
	protected void returnCity(String cityName) {
		new WeatherThread(this, cityName).start();
		WeatherSetCityActivity.this.finish();
	}

	/**
	 * ������Դ��list_model_city_all���������
	 */
	private void fillCityValue() {
		if (list_model_city_all == null) {
			list_model_city_all = new ArrayList<CityProviceModel>();
			for (int i = 0; i < city_all.length; i++) {
				list_model_city_all.add(new CityProviceModel(city_all[i][1],
						city_all[i][2], city_all[i][3], city_all[i][4]));
			}
		}

	}

	// ����Դ
	final String[] hotCities = new String[] { "����", "�Ϻ�", "����", "����", "�人",
			"�Ͼ�", "����", "����", "֣��", "�ɶ�", "��ݸ", "����", "���", "������", "��ɳ", "����",
			"ʯ��ׯ", "����", "����", "����", "����", "����", "��ɽ", "����", "�ϲ�", "̫ԭ", "����",
			"�Ϸ�", "�ֶ�", "�ൺ", "��ͷ", "����", "����" };

	static final String[][] city_all = new String[][] {
			{ "1", "����", "����", "beijing", "beijing" },
			{ "2", "�Ϻ�", "�Ϻ�", "shanghai", "beijing" },
			{ "3", "���", "���", "tianjin", "beijing" },
			{ "4", "����", "����", "zhongqing", "zhongqing" },
			{ "5", "���", "���", "xianggang", "xianggang" },
			{ "6", "����", "����", "aomen", "aomen" },
			{ "7", "������", "������", "haerbin", "heilongjiang" },
			{ "8", "�������", "������", "qiqihaer", "heilongjiang" },
			{ "9", "ĵ����", "������", "mudanjiang", "heilongjiang" },
			{ "10", "����", "������", "daqing", "heilongjiang" },
			{ "11", "����", "������", "yichun", "heilongjiang" },
			{ "12", "˫Ѽɽ", "������", "shuangyashan", "heilongjiang" },
			{ "13", "�׸�", "������", "hegang", "heilongjiang" },
			{ "14", "����", "������", "jixi", "heilongjiang" },
			{ "15", "��ľ˹", "������", "jiamusi", "heilongjiang" },
			{ "16", "��̨��", "������", "qitaihe", "heilongjiang" },
			{ "17", "�ں�", "������", "heihe", "heilongjiang" },
			{ "18", "�绯", "������", "suihua", "heilongjiang" },
			{ "19", "���˰���", "������", "daxinganling", "heilongjiang" },
			{ "20", "����", "����", "changchun", "jilin" },
			{ "21", "����", "����", "jilin", "jilin" },
			{ "22", "��ɽ", "����", "baishan", "jilin" },
			{ "23", "�׳�", "����", "baicheng", "jilin" },
			{ "24", "��ƽ", "����", "siping", "jilin" },
			{ "25", "��ԭ", "����", "songyuan", "jilin" },
			{ "26", "��Դ", "����", "liaoyuan", "jilin" },
			{ "27", "��", "����", "daan", "jilin" },
			{ "28", "ͨ��", "����", "tonghua", "jilin" },
			{ "29", "����", "����", "shenyang", "liaoning" },
			{ "30", "����", "����", "dalian", "liaoning" },
			{ "31", "��«��", "����", "huludao", "liaoning" },
			{ "32", "��˳", "����", "lvshun", "liaoning" },
			{ "33", "��Ϫ", "����", "benxi", "liaoning" },
			{ "34", "��˳", "����", "fushun", "liaoning" },
			{ "35", "����", "����", "tieling", "liaoning" },
			{ "36", "����", "����", "liaoyang", "liaoning" },
			{ "37", "Ӫ��", "����", "yingkou", "liaoning" },
			{ "38", "����", "����", "fuxin", "liaoning" },
			{ "39", "����", "����", "chaoyang", "liaoning" },
			{ "40", "����", "����", "jinzhou", "liaoning" },
			{ "41", "����", "����", "dandong", "liaoning" },
			{ "42", "��ɽ", "����", "anshan", "liaoning" },
			{ "43", "���ͺ���", "���ɹ�", "huhehaote", "neimenggu" },
			{ "44", "���ֺ���", "���ɹ�", "xilinhaote", "neimenggu" },
			{ "45", "��ͷ", "���ɹ�", "baotou", "neimenggu" },
			{ "46", "���", "���ɹ�", "chifeng", "neimenggu" },
			{ "47", "������", "���ɹ�", "hailaer", "neimenggu" },
			{ "48", "�ں�", "���ɹ�", "wuhai", "neimenggu" },
			{ "49", "������˹", "���ɹ�", "eerduosi", "neimenggu" },
			{ "50", "���ֺ���", "���ɹ�", "xilinhaote", "neimenggu" },
			{ "51", "ͨ��", "���ɹ�", "tongliao", "neimenggu" },
			{ "52", "ʯ��ׯ", "�ӱ�", "shijiazhuang", "hebei" },
			{ "53", "��ɽ", "�ӱ�", "tangshan", "hebei" },
			{ "54", "�żҿ�", "�ӱ�", "zhangjiakou", "hebei" },
			{ "55", "�ȷ�", "�ӱ�", "langfang", "hebei" },
			{ "56", "��̨", "�ӱ�", "xingtai", "hebei" },
			{ "57", "����", "�ӱ�", "handan", "hebei" },
			{ "58", "����", "�ӱ�", "cangzhou", "hebei" },
			{ "59", "��ˮ", "�ӱ�", "hengshui", "hebei" },
			{ "60", "�е�", "�ӱ�", "chengde", "hebei" },
			{ "61", "����", "�ӱ�", "baoding", "hebei" },
			{ "62", "�ػʵ�", "�ӱ�", "qinhuangdao", "hebei" },
			{ "63", "֣��", "����", "zhengzhou", "henan" },
			{ "64", "����", "����", "kaifeng", "henan" },
			{ "65", "����", "����", "luoyang", "henan" },
			{ "66", "ƽ��ɽ", "����", "pingdingshan", "henan" },
			{ "67", "����", "����", "jiaozuo", "henan" },
			{ "68", "�ױ�", "����", "hebi", "henan" },
			{ "69", "����", "����", "xinxiang", "henan" },
			{ "70", "����", "����", "anyang", "henan" },
			{ "71", "���", "����", "puyang", "henan" },
			{ "72", "���", "����", "xuchang", "henan" },
			{ "73", "���", "����", "luohe", "henan" },
			{ "74", "����Ͽ", "����", "sanmenxia", "henan" },
			{ "75", "����", "����", "nanyang", "henan" },
			{ "76", "����", "����", "shangqiu", "henan" },
			{ "77", "����", "����", "xinyang", "henan" },
			{ "78", "�ܿ�", "����", "zhoukou", "henan" },
			{ "79", "פ���", "����", "zhumadian", "henan" },
			{ "80", "����", "ɽ��", "jinan", "shandong" },
			{ "81", "�ൺ", "ɽ��", "qingdao", "shandong" },
			{ "82", "�Ͳ�", "ɽ��", "zibo", "shandong" },
			{ "83", "����", "ɽ��", "weihai", "shandong" },
			{ "84", "����", "ɽ��", "qufu", "shandong" },
			{ "85", "����", "ɽ��", "linyi", "shandong" },
			{ "86", "��̨", "ɽ��", "yantai", "shandong" },
			{ "87", "��ׯ", "ɽ��", "zaozhuang", "shandong" },
			{ "88", "�ĳ�", "ɽ��", "liaocheng", "shandong" },
			{ "89", "����", "ɽ��", "jining", "shandong" },
			{ "90", "����", "ɽ��", "heze", "shandong" },
			{ "91", "̩��", "ɽ��", "taian", "shandong" },
			{ "92", "����", "ɽ��", "rizhao", "shandong" },
			{ "93", "��Ӫ", "ɽ��", "dongying", "shandong" },
			{ "94", "����", "ɽ��", "dezhou", "shandong" },
			{ "95", "����", "ɽ��", "binzhou", "shandong" },
			{ "96", "����", "ɽ��", "laiwu", "shandong" },
			{ "97", "Ϋ��", "ɽ��", "weifang", "shandong" },
			{ "98", "̫ԭ", "ɽ��", "taiyuan", "shanxi" },
			{ "99", "��Ȫ", "ɽ��", "yangquan", "shanxi" },
			{ "100", "����", "ɽ��", "jincheng", "shanxi" },
			{ "101", "����", "ɽ��", "jinzhong", "shanxi" },
			{ "102", "�ٷ�", "ɽ��", "linfen", "shanxi" },
			{ "103", "�˳�", "ɽ��", "yuncheng", "shanxi" },
			{ "104", "����", "ɽ��", "changzhi", "shanxi" },
			{ "105", "˷��", "ɽ��", "shuozhou", "shanxi" },
			{ "106", "����", "ɽ��", "xinzhou", "shanxi" },
			{ "107", "��ͬ", "ɽ��", "datong", "shanxi" },
			{ "108", "����", "ɽ��", "lvliang", "shanxi" },
			{ "109", "�Ͼ�", "����", "nanjing", "jiangsu" },
			{ "110", "����", "����", "suzhou", "jiangsu" },
			{ "111", "��ɽ", "����", "kunshan", "jiangsu" },
			{ "112", "��ͨ", "����", "nantong", "jiangsu" },
			{ "113", "̫��", "����", "taicang", "jiangsu" },
			{ "114", "����", "����", "wuxian", "jiangsu" },
			{ "115", "����", "����", "xuzhou", "jiangsu" },
			{ "116", "����", "����", "yixing", "jiangsu" },
			{ "117", "��", "����", "zhenjiang", "jiangsu" },
			{ "118", "����", "����", "huaian", "jiangsu" },
			{ "119", "����", "����", "changshu", "jiangsu" },
			{ "120", "�γ�", "����", "yancheng", "jiangsu" },
			{ "121", "̩��", "����", "taizhou", "jiangsu" },
			{ "122", "����", "����", "wuxi", "jiangsu" },
			{ "123", "���Ƹ�", "����", "lianyungang", "jiangsu" },
			{ "124", "����", "����", "yangzhou", "jiangsu" },
			{ "125", "����", "����", "changzhou", "jiangsu" },
			{ "126", "��Ǩ", "����", "suqian", "jiangsu" },
			{ "127", "�Ϸ�", "����", "hefei", "anhui" },
			{ "128", "����", "����", "chaohu", "anhui" },
			{ "129", "����", "����", "bangbu", "anhui" },
			{ "130", "����", "����", "anqing", "anhui" },
			{ "131", "����", "����", "liuan", "anhui" },
			{ "132", "����", "����", "chuzhou", "anhui" },
			{ "133", "��ɽ", "����", "maanshan", "anhui" },
			{ "134", "����", "����", "fuyang", "anhui" },
			{ "135", "����", "����", "xuancheng", "anhui" },
			{ "136", "ͭ��", "����", "tongling", "anhui" },
			{ "137", "����", "����", "huaibei", "anhui" },
			{ "138", "�ߺ�", "����", "wuhu", "anhui" },
			{ "139", "����", "����", "suzhou", "anhui" },
			{ "140", "����", "����", "huainan", "anhui" },
			{ "141", "����", "����", "chizhou", "anhui" },
			{ "142", "����", "����", "xian", "shanxi" },
			{ "143", "����", "����", "hancheng", "shanxi" },
			{ "144", "����", "����", "ankang", "shanxi" },
			{ "145", "����", "����", "hanzhong", "shanxi" },
			{ "146", "����", "����", "baoji", "shanxi" },
			{ "147", "����", "����", "xianyang", "shanxi" },
			{ "148", "����", "����", "yulin", "shanxi" },
			{ "149", "μ��", "����", "weinan", "shanxi" },
			{ "150", "����", "����", "shangluo", "shanxi" },
			{ "151", "ͭ��", "����", "tongchuan", "shanxi" },
			{ "152", "�Ӱ�", "����", "yanan", "shanxi" },
			{ "153", "����", "����", "yinchuan", "ningxia" },
			{ "154", "��ԭ", "����", "guyuan", "ningxia" },
			{ "155", "����", "����", "zhongwei", "ningxia" },
			{ "156", "ʯ��ɽ", "����", "shizuishan", "ningxia" },
			{ "157", "����", "����", "wuzhong", "ningxia" },
			{ "158", "����", "����", "lanzhou", "gansu" },
			{ "159", "����", "����", "baiyin", "gansu" },
			{ "160", "����", "����", "qingyang", "gansu" },
			{ "161", "��Ȫ", "����", "jiuquan", "gansu" },
			{ "162", "��ˮ", "����", "tianshui", "gansu" },
			{ "163", "����", "����", "wuwei", "gansu" },
			{ "164", "��Ҵ", "����", "zhangye", "gansu" },
			{ "165", "����", "����", "gannan", "gansu" },
			{ "166", "����", "����", "linxia", "gansu" },
			{ "167", "ƽ��", "����", "pingliang", "gansu" },
			{ "168", "����", "����", "dingxi", "gansu" },
			{ "169", "���", "����", "jinchang", "gansu" },
			{ "170", "����", "�ຣ", "xining", "qinghai" },
			{ "171", "����", "�ຣ", "haibei", "qinghai" },
			{ "172", "����", "�ຣ", "haixi", "qinghai" },
			{ "173", "����", "�ຣ", "huangnan", "qinghai" },
			{ "174", "����", "�ຣ", "guoluo", "qinghai" },
			{ "175", "����", "�ຣ", "yushu", "qinghai" },
			{ "176", "����", "�ຣ", "haidong", "qinghai" },
			{ "177", "����", "�ຣ", "hainan", "qinghai" },
			{ "178", "�人", "����", "wuhan", "hubei" },
			{ "179", "�˲�", "����", "yichang", "hubei" },
			{ "180", "�Ƹ�", "����", "huanggang", "hubei" },
			{ "181", "��ʩ", "����", "enshi", "hubei" },
			{ "182", "����", "����", "jingzhou", "hubei" },
			{ "183", "��ũ��", "����", "shennongjia", "hubei" },
			{ "184", "ʮ��", "����", "shiyan", "hubei" },
			{ "185", "����", "����", "xianning", "hubei" },
			{ "186", "�差", "����", "xiangfan", "hubei" },
			{ "187", "Т��", "����", "xiaogan", "hubei" },
			{ "188", "����", "����", "suizhou", "hubei" },
			{ "189", "��ʯ", "����", "huangshi", "hubei" },
			{ "190", "����", "����", "jingmen", "hubei" },
			{ "191", "����", "����", "ezhou", "hubei" },
			{ "192", "��ɳ", "����", "changsha", "hunan" },
			{ "193", "����", "����", "shaoyang", "hunan" },
			{ "194", "����", "����", "changde", "hunan" },
			{ "195", "����", "����", "chenzhou", "hunan" },
			{ "196", "����", "����", "jishou", "hunan" },
			{ "197", "����", "����", "zhuzhou", "hunan" },
			{ "198", "¦��", "����", "loudi", "hunan" },
			{ "199", "��̶", "����", "xiangtan", "hunan" },
			{ "200", "����", "����", "yiyang", "hunan" },
			{ "201", "����", "����", "yongzhou", "hunan" },
			{ "202", "����", "����", "yueyang", "hunan" },
			{ "203", "����", "����", "hengyang", "hunan" },
			{ "204", "����", "����", "huaihua", "hunan" },
			{ "205", "��ɽ", "����", "shaoshan", "hunan" },
			{ "206", "�żҽ�", "����", "zhangjiajie", "hunan" },
			{ "207", "����", "�㽭", "hangzhou", "zhejiang" },
			{ "208", "����", "�㽭", "huzhou", "zhejiang" },
			{ "209", "��", "�㽭", "jinhua", "zhejiang" },
			{ "210", "����", "�㽭", "ningbo", "zhejiang" },
			{ "211", "��ˮ", "�㽭", "lishui", "zhejiang" },
			{ "212", "����", "�㽭", "shaoxing", "zhejiang" },
			{ "213", "����", "�㽭", "quzhou", "zhejiang" },
			{ "214", "����", "�㽭", "jiaxing", "zhejiang" },
			{ "215", "̨��", "�㽭", "taizhou", "zhejiang" },
			{ "216", "��ɽ", "�㽭", "zhoushan", "zhejiang" },
			{ "217", "����", "�㽭", "wenzhou", "zhejiang" },
			{ "218", "�ϲ�", "����", "nanchang", "jiangxi" },
			{ "219", "Ƽ��", "����", "pingxiang", "jiangxi" },
			{ "220", "�Ž�", "����", "jiujiang", "jiangxi" },
			{ "221", "����", "����", "shangrao", "jiangxi" },
			{ "222", "����", "����", "fuzhou", "jiangxi" },
			{ "223", "����", "����", "jian", "jiangxi" },
			{ "224", "ӥ̶", "����", "yingtan", "jiangxi" },
			{ "225", "�˴�", "����", "yichun", "jiangxi" },
			{ "226", "����", "����", "xinyu", "jiangxi" },
			{ "227", "������", "����", "jingdezhen", "jiangxi" },
			{ "228", "����", "����", "ganzhou", "jiangxi" },
			{ "229", "����", "����", "fuzhou", "fujian" },
			{ "230", "����", "����", "xiamen", "fujian" },
			{ "231", "����", "����", "longyan", "fujian" },
			{ "232", "��ƽ", "����", "nanping", "fujian" },
			{ "233", "����", "����", "ningde", "fujian" },
			{ "234", "����", "����", "putian", "fujian" },
			{ "235", "Ȫ��", "����", "quanzhou", "fujian" },
			{ "236", "����", "����", "sanming", "fujian" },
			{ "237", "����", "����", "zhangzhou", "fujian" },
			{ "238", "����", "����", "guiyang", "guizhou" },
			{ "239", "��˳", "����", "anshun", "guizhou" },
			{ "240", "��ˮ", "����", "chishui", "guizhou" },
			{ "241", "����", "����", "zunyi", "guizhou" },
			{ "242", "ͭ��", "����", "tongren", "guizhou" },
			{ "243", "����ˮ", "����", "liupanshui", "guizhou" },
			{ "244", "�Ͻ�", "����", "bijie", "guizhou" },
			{ "245", "����", "����", "kaili", "guizhou" },
			{ "246", "����", "����", "duyun", "guizhou" },
			{ "247", "�ɶ�", "�Ĵ�", "chengdu", "sichuan" },
			{ "248", "����", "�Ĵ�", "luzhou", "sichuan" },
			{ "249", "�ڽ�", "�Ĵ�", "neijiang", "sichuan" },
			{ "250", "��ɽ", "�Ĵ�", "liangshan", "sichuan" },
			{ "251", "����", "�Ĵ�", "aba", "sichuan" },
			{ "252", "����", "�Ĵ�", "bazhong", "sichuan" },
			{ "253", "��Ԫ", "�Ĵ�", "guangyuan", "sichuan" },
			{ "254", "��ɽ", "�Ĵ�", "leshan", "sichuan" },
			{ "255", "����", "�Ĵ�", "mianyang", "sichuan" },
			{ "256", "����", "�Ĵ�", "deyang", "sichuan" },
			{ "257", "��֦��", "�Ĵ�", "panzhihua", "sichuan" },
			{ "258", "�Ű�", "�Ĵ�", "yaan", "sichuan" },
			{ "259", "�˱�", "�Ĵ�", "yibin", "sichuan" },
			{ "260", "�Թ�", "�Ĵ�", "zigong", "sichuan" },
			{ "261", "������", "�Ĵ�", "ganzizhou", "sichuan" },
			{ "262", "����", "�Ĵ�", "dazhou", "sichuan" },
			{ "263", "����", "�Ĵ�", "ziyang", "sichuan" },
			{ "264", "�㰲", "�Ĵ�", "guangan", "sichuan" },
			{ "265", "����", "�Ĵ�", "suining", "sichuan" },
			{ "266", "üɽ", "�Ĵ�", "meishan", "sichuan" },
			{ "267", "�ϳ�", "�Ĵ�", "nanchong", "sichuan" },
			{ "268", "����", "�㶫", "guangzhou", "guangdong" },
			{ "269", "����", "�㶫", "shenzhen", "guangdong" },
			{ "270", "����", "�㶫", "chaozhou", "guangdong" },
			{ "271", "�ع�", "�㶫", "shaoguan", "guangdong" },
			{ "272", "տ��", "�㶫", "zhanjiang", "guangdong" },
			{ "273", "����", "�㶫", "huizhou", "guangdong" },
			{ "274", "��Զ", "�㶫", "qingyuan", "guangdong" },
			{ "275", "��ݸ", "�㶫", "dongguan", "guangdong" },
			{ "276", "����", "�㶫", "jiangmen", "guangdong" },
			{ "277", "ï��", "�㶫", "maoming", "guangdong" },
			{ "278", "����", "�㶫", "zhaoqing", "guangdong" },
			{ "279", "��β", "�㶫", "shanwei", "guangdong" },
			{ "280", "��Դ", "�㶫", "heyuan", "guangdong" },
			{ "281", "����", "�㶫", "jieyang", "guangdong" },
			{ "282", "÷��", "�㶫", "meizhou", "guangdong" },
			{ "283", "��ɽ", "�㶫", "zhongshan", "guangdong" },
			{ "284", "����", "�㶫", "deqing", "guangdong" },
			{ "285", "����", "�㶫", "yangjiang", "guangdong" },
			{ "286", "�Ƹ�", "�㶫", "yunfu", "guangdong" },
			{ "287", "�麣", "�㶫", "zhuhai", "guangdong" },
			{ "288", "��ͷ", "�㶫", "shantou", "guangdong" },
			{ "289", "��ɽ", "�㶫", "foshan", "guangdong" },
			{ "290", "����", "����", "nanning", "guangxi" },
			{ "291", "����", "����", "guilin", "guangxi" },
			{ "292", "��˷", "����", "yangshuo", "guangxi" },
			{ "293", "����", "����", "liuzhou", "guangxi" },
			{ "294", "����", "����", "wuzhou", "guangxi" },
			{ "295", "����", "����", "yulin", "guangxi" },
			{ "296", "��ƽ", "����", "guiping", "guangxi" },
			{ "297", "����", "����", "hezhou", "guangxi" },
			{ "298", "����", "����", "qinzhou", "guangxi" },
			{ "299", "���", "����", "guigang", "guangxi" },
			{ "300", "���Ǹ�", "����", "fangchenggang", "guangxi" },
			{ "301", "��ɫ", "����", "baise", "guangxi" },
			{ "302", "����", "����", "beihai", "guangxi" },
			{ "303", "�ӳ�", "����", "hechi", "guangxi" },
			{ "304", "����", "����", "laibin", "guangxi" },
			{ "305", "����", "����", "chongzuo", "guangxi" },
			{ "306", "����", "����", "kunming", "yunnan" },
			{ "307", "��ɽ", "����", "baoshan", "yunnan" },
			{ "308", "����", "����", "chuxiong", "yunnan" },
			{ "309", "�º�", "����", "dehong", "yunnan" },
			{ "310", "���", "����", "honghe", "yunnan" },
			{ "311", "�ٲ�", "����", "lincang", "yunnan" },
			{ "312", "ŭ��", "����", "nujiang", "yunnan" },
			{ "313", "����", "����", "qujing", "yunnan" },
			{ "314", "˼é", "����", "simao", "yunnan" },
			{ "315", "��ɽ", "����", "wenshan", "yunnan" },
			{ "316", "��Ϫ", "����", "yuxi", "yunnan" },
			{ "317", "��ͨ", "����", "zhaotong", "yunnan" },
			{ "318", "����", "����", "lijiang", "yunnan" },
			{ "319", "����", "����", "dali", "yunnan" },
			{ "320", "����", "����", "haikou", "hainan" },
			{ "321", "����", "����", "sanya", "hainan" },
			{ "322", "����", "����", "danzhou", "hainan" },
			{ "323", "��ɽ", "����", "qiongshan", "hainan" },
			{ "324", "ͨʲ", "����", "tongshi", "hainan" },
			{ "325", "�Ĳ�", "����", "wenchang", "hainan" },
			{ "326", "��³ľ��", "�½�", "wulumuqi", "xinjiang" },
			{ "327", "����̩", "�½�", "aletai", "xinjiang" },
			{ "328", "������", "�½�", "akesu", "xinjiang" },
			{ "329", "����", "�½�", "changji", "xinjiang" },
			{ "330", "����", "�½�", "hami", "xinjiang" },
			{ "331", "����", "�½�", "hetian", "xinjiang" },
			{ "332", "��ʲ", "�½�", "kashi", "xinjiang" },
			{ "333", "��������", "�½�", "kelamayi", "xinjiang" },
			{ "334", "ʯ����", "�½�", "shihezi", "xinjiang" },
			{ "335", "����", "�½�", "tacheng", "xinjiang" },
			{ "336", "�����", "�½�", "kuerle", "xinjiang" },
			{ "337", "��³��", "�½�", "tulufan", "xinjiang" },
			{ "338", "����", "�½�", "yining", "xinjiang" },
			{ "339", "����", "����", "lasa", "xicang" },
			{ "340", "����", "����", "ali", "xicang" },
			{ "341", "����", "����", "changdu", "xicang" },
			{ "342", "����", "����", "naqu", "xicang" },
			{ "343", "�տ���", "����", "rikaze", "xicang" },
			{ "344", "ɽ��", "����", "shannan", "xicang" },
			{ "345", "��֥", "����", "linzhi", "xicang" },
			{ "346", "̨��", "̨��", "taibei", "taiwan" },
			{ "347", "����", "̨��", "gaoxiong", "taiwan" },

	};

}

class CityProviceModel {
	public CityProviceModel(CityProviceModel cityProviceModel) {
		this.city = cityProviceModel.city;
		this.provice = cityProviceModel.provice;
		this.cityLetter = cityProviceModel.cityLetter;
		this.proviceLetter = cityProviceModel.proviceLetter;
	}

	public CityProviceModel(String city, String provice, String cityLetter,
			String proviceLetter) {
		this.city = city;
		this.provice = provice;
		this.cityLetter = cityLetter;
		this.proviceLetter = proviceLetter;
	}

	public String city;
	public String provice;
	public String cityLetter;
	public String proviceLetter;
}
