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
 * 设置天气信息，所针对的城市
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
				// 显示清除按钮
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
	 * 根据输入关键字查询城市
	 * 
	 * @param s
	 */
	protected void searchCity(CharSequence s) {
		weather_search_result_msg.setText(s);
		searchKeyword = s.toString();
		freshResultListview();
	}

	/**
	 * 刷新查询结果
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
	 * 将结果返回
	 * 
	 * @param cityName
	 */
	protected void returnCity(String cityName) {
		new WeatherThread(this, cityName).start();
		WeatherSetCityActivity.this.finish();
	}

	/**
	 * 从数据源向list_model_city_all中填充数据
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

	// 数据源
	final String[] hotCities = new String[] { "北京", "上海", "广州", "深圳", "武汉",
			"南京", "杭州", "西安", "郑州", "成都", "东莞", "沈阳", "天津", "哈尔滨", "长沙", "福州",
			"石家庄", "苏州", "重庆", "无锡", "济南", "大连", "佛山", "厦门", "南昌", "太原", "长春",
			"合肥", "浦东", "青岛", "汕头", "昆明", "南宁" };

	static final String[][] city_all = new String[][] {
			{ "1", "北京", "北京", "beijing", "beijing" },
			{ "2", "上海", "上海", "shanghai", "beijing" },
			{ "3", "天津", "天津", "tianjin", "beijing" },
			{ "4", "重庆", "重庆", "zhongqing", "zhongqing" },
			{ "5", "香港", "香港", "xianggang", "xianggang" },
			{ "6", "澳门", "澳门", "aomen", "aomen" },
			{ "7", "哈尔滨", "黑龙江", "haerbin", "heilongjiang" },
			{ "8", "齐齐哈尔", "黑龙江", "qiqihaer", "heilongjiang" },
			{ "9", "牡丹江", "黑龙江", "mudanjiang", "heilongjiang" },
			{ "10", "大庆", "黑龙江", "daqing", "heilongjiang" },
			{ "11", "伊春", "黑龙江", "yichun", "heilongjiang" },
			{ "12", "双鸭山", "黑龙江", "shuangyashan", "heilongjiang" },
			{ "13", "鹤岗", "黑龙江", "hegang", "heilongjiang" },
			{ "14", "鸡西", "黑龙江", "jixi", "heilongjiang" },
			{ "15", "佳木斯", "黑龙江", "jiamusi", "heilongjiang" },
			{ "16", "七台河", "黑龙江", "qitaihe", "heilongjiang" },
			{ "17", "黑河", "黑龙江", "heihe", "heilongjiang" },
			{ "18", "绥化", "黑龙江", "suihua", "heilongjiang" },
			{ "19", "大兴安岭", "黑龙江", "daxinganling", "heilongjiang" },
			{ "20", "长春", "吉林", "changchun", "jilin" },
			{ "21", "吉林", "吉林", "jilin", "jilin" },
			{ "22", "白山", "吉林", "baishan", "jilin" },
			{ "23", "白城", "吉林", "baicheng", "jilin" },
			{ "24", "四平", "吉林", "siping", "jilin" },
			{ "25", "松原", "吉林", "songyuan", "jilin" },
			{ "26", "辽源", "吉林", "liaoyuan", "jilin" },
			{ "27", "大安", "吉林", "daan", "jilin" },
			{ "28", "通化", "吉林", "tonghua", "jilin" },
			{ "29", "沈阳", "辽宁", "shenyang", "liaoning" },
			{ "30", "大连", "辽宁", "dalian", "liaoning" },
			{ "31", "葫芦岛", "辽宁", "huludao", "liaoning" },
			{ "32", "旅顺", "辽宁", "lvshun", "liaoning" },
			{ "33", "本溪", "辽宁", "benxi", "liaoning" },
			{ "34", "抚顺", "辽宁", "fushun", "liaoning" },
			{ "35", "铁岭", "辽宁", "tieling", "liaoning" },
			{ "36", "辽阳", "辽宁", "liaoyang", "liaoning" },
			{ "37", "营口", "辽宁", "yingkou", "liaoning" },
			{ "38", "阜新", "辽宁", "fuxin", "liaoning" },
			{ "39", "朝阳", "辽宁", "chaoyang", "liaoning" },
			{ "40", "锦州", "辽宁", "jinzhou", "liaoning" },
			{ "41", "丹东", "辽宁", "dandong", "liaoning" },
			{ "42", "鞍山", "辽宁", "anshan", "liaoning" },
			{ "43", "呼和浩特", "内蒙古", "huhehaote", "neimenggu" },
			{ "44", "锡林浩特", "内蒙古", "xilinhaote", "neimenggu" },
			{ "45", "包头", "内蒙古", "baotou", "neimenggu" },
			{ "46", "赤峰", "内蒙古", "chifeng", "neimenggu" },
			{ "47", "海拉尔", "内蒙古", "hailaer", "neimenggu" },
			{ "48", "乌海", "内蒙古", "wuhai", "neimenggu" },
			{ "49", "鄂尔多斯", "内蒙古", "eerduosi", "neimenggu" },
			{ "50", "锡林浩特", "内蒙古", "xilinhaote", "neimenggu" },
			{ "51", "通辽", "内蒙古", "tongliao", "neimenggu" },
			{ "52", "石家庄", "河北", "shijiazhuang", "hebei" },
			{ "53", "唐山", "河北", "tangshan", "hebei" },
			{ "54", "张家口", "河北", "zhangjiakou", "hebei" },
			{ "55", "廊坊", "河北", "langfang", "hebei" },
			{ "56", "邢台", "河北", "xingtai", "hebei" },
			{ "57", "邯郸", "河北", "handan", "hebei" },
			{ "58", "沧州", "河北", "cangzhou", "hebei" },
			{ "59", "衡水", "河北", "hengshui", "hebei" },
			{ "60", "承德", "河北", "chengde", "hebei" },
			{ "61", "保定", "河北", "baoding", "hebei" },
			{ "62", "秦皇岛", "河北", "qinhuangdao", "hebei" },
			{ "63", "郑州", "河南", "zhengzhou", "henan" },
			{ "64", "开封", "河南", "kaifeng", "henan" },
			{ "65", "洛阳", "河南", "luoyang", "henan" },
			{ "66", "平顶山", "河南", "pingdingshan", "henan" },
			{ "67", "焦作", "河南", "jiaozuo", "henan" },
			{ "68", "鹤壁", "河南", "hebi", "henan" },
			{ "69", "新乡", "河南", "xinxiang", "henan" },
			{ "70", "安阳", "河南", "anyang", "henan" },
			{ "71", "濮阳", "河南", "puyang", "henan" },
			{ "72", "许昌", "河南", "xuchang", "henan" },
			{ "73", "漯河", "河南", "luohe", "henan" },
			{ "74", "三门峡", "河南", "sanmenxia", "henan" },
			{ "75", "南阳", "河南", "nanyang", "henan" },
			{ "76", "商丘", "河南", "shangqiu", "henan" },
			{ "77", "信阳", "河南", "xinyang", "henan" },
			{ "78", "周口", "河南", "zhoukou", "henan" },
			{ "79", "驻马店", "河南", "zhumadian", "henan" },
			{ "80", "济南", "山东", "jinan", "shandong" },
			{ "81", "青岛", "山东", "qingdao", "shandong" },
			{ "82", "淄博", "山东", "zibo", "shandong" },
			{ "83", "威海", "山东", "weihai", "shandong" },
			{ "84", "曲阜", "山东", "qufu", "shandong" },
			{ "85", "临沂", "山东", "linyi", "shandong" },
			{ "86", "烟台", "山东", "yantai", "shandong" },
			{ "87", "枣庄", "山东", "zaozhuang", "shandong" },
			{ "88", "聊城", "山东", "liaocheng", "shandong" },
			{ "89", "济宁", "山东", "jining", "shandong" },
			{ "90", "菏泽", "山东", "heze", "shandong" },
			{ "91", "泰安", "山东", "taian", "shandong" },
			{ "92", "日照", "山东", "rizhao", "shandong" },
			{ "93", "东营", "山东", "dongying", "shandong" },
			{ "94", "德州", "山东", "dezhou", "shandong" },
			{ "95", "滨州", "山东", "binzhou", "shandong" },
			{ "96", "莱芜", "山东", "laiwu", "shandong" },
			{ "97", "潍坊", "山东", "weifang", "shandong" },
			{ "98", "太原", "山西", "taiyuan", "shanxi" },
			{ "99", "阳泉", "山西", "yangquan", "shanxi" },
			{ "100", "晋城", "山西", "jincheng", "shanxi" },
			{ "101", "晋中", "山西", "jinzhong", "shanxi" },
			{ "102", "临汾", "山西", "linfen", "shanxi" },
			{ "103", "运城", "山西", "yuncheng", "shanxi" },
			{ "104", "长治", "山西", "changzhi", "shanxi" },
			{ "105", "朔州", "山西", "shuozhou", "shanxi" },
			{ "106", "忻州", "山西", "xinzhou", "shanxi" },
			{ "107", "大同", "山西", "datong", "shanxi" },
			{ "108", "吕梁", "山西", "lvliang", "shanxi" },
			{ "109", "南京", "江苏", "nanjing", "jiangsu" },
			{ "110", "苏州", "江苏", "suzhou", "jiangsu" },
			{ "111", "昆山", "江苏", "kunshan", "jiangsu" },
			{ "112", "南通", "江苏", "nantong", "jiangsu" },
			{ "113", "太仓", "江苏", "taicang", "jiangsu" },
			{ "114", "吴县", "江苏", "wuxian", "jiangsu" },
			{ "115", "徐州", "江苏", "xuzhou", "jiangsu" },
			{ "116", "宜兴", "江苏", "yixing", "jiangsu" },
			{ "117", "镇江", "江苏", "zhenjiang", "jiangsu" },
			{ "118", "淮安", "江苏", "huaian", "jiangsu" },
			{ "119", "常熟", "江苏", "changshu", "jiangsu" },
			{ "120", "盐城", "江苏", "yancheng", "jiangsu" },
			{ "121", "泰州", "江苏", "taizhou", "jiangsu" },
			{ "122", "无锡", "江苏", "wuxi", "jiangsu" },
			{ "123", "连云港", "江苏", "lianyungang", "jiangsu" },
			{ "124", "扬州", "江苏", "yangzhou", "jiangsu" },
			{ "125", "常州", "江苏", "changzhou", "jiangsu" },
			{ "126", "宿迁", "江苏", "suqian", "jiangsu" },
			{ "127", "合肥", "安徽", "hefei", "anhui" },
			{ "128", "巢湖", "安徽", "chaohu", "anhui" },
			{ "129", "蚌埠", "安徽", "bangbu", "anhui" },
			{ "130", "安庆", "安徽", "anqing", "anhui" },
			{ "131", "六安", "安徽", "liuan", "anhui" },
			{ "132", "滁州", "安徽", "chuzhou", "anhui" },
			{ "133", "马鞍山", "安徽", "maanshan", "anhui" },
			{ "134", "阜阳", "安徽", "fuyang", "anhui" },
			{ "135", "宣城", "安徽", "xuancheng", "anhui" },
			{ "136", "铜陵", "安徽", "tongling", "anhui" },
			{ "137", "淮北", "安徽", "huaibei", "anhui" },
			{ "138", "芜湖", "安徽", "wuhu", "anhui" },
			{ "139", "宿州", "安徽", "suzhou", "anhui" },
			{ "140", "淮南", "安徽", "huainan", "anhui" },
			{ "141", "池州", "安徽", "chizhou", "anhui" },
			{ "142", "西安", "陕西", "xian", "shanxi" },
			{ "143", "韩城", "陕西", "hancheng", "shanxi" },
			{ "144", "安康", "陕西", "ankang", "shanxi" },
			{ "145", "汉中", "陕西", "hanzhong", "shanxi" },
			{ "146", "宝鸡", "陕西", "baoji", "shanxi" },
			{ "147", "咸阳", "陕西", "xianyang", "shanxi" },
			{ "148", "榆林", "陕西", "yulin", "shanxi" },
			{ "149", "渭南", "陕西", "weinan", "shanxi" },
			{ "150", "商洛", "陕西", "shangluo", "shanxi" },
			{ "151", "铜川", "陕西", "tongchuan", "shanxi" },
			{ "152", "延安", "陕西", "yanan", "shanxi" },
			{ "153", "银川", "宁夏", "yinchuan", "ningxia" },
			{ "154", "固原", "宁夏", "guyuan", "ningxia" },
			{ "155", "中卫", "宁夏", "zhongwei", "ningxia" },
			{ "156", "石嘴山", "宁夏", "shizuishan", "ningxia" },
			{ "157", "吴忠", "宁夏", "wuzhong", "ningxia" },
			{ "158", "兰州", "甘肃", "lanzhou", "gansu" },
			{ "159", "白银", "甘肃", "baiyin", "gansu" },
			{ "160", "庆阳", "甘肃", "qingyang", "gansu" },
			{ "161", "酒泉", "甘肃", "jiuquan", "gansu" },
			{ "162", "天水", "甘肃", "tianshui", "gansu" },
			{ "163", "武威", "甘肃", "wuwei", "gansu" },
			{ "164", "张掖", "甘肃", "zhangye", "gansu" },
			{ "165", "甘南", "甘肃", "gannan", "gansu" },
			{ "166", "临夏", "甘肃", "linxia", "gansu" },
			{ "167", "平凉", "甘肃", "pingliang", "gansu" },
			{ "168", "定西", "甘肃", "dingxi", "gansu" },
			{ "169", "金昌", "甘肃", "jinchang", "gansu" },
			{ "170", "西宁", "青海", "xining", "qinghai" },
			{ "171", "海北", "青海", "haibei", "qinghai" },
			{ "172", "海西", "青海", "haixi", "qinghai" },
			{ "173", "黄南", "青海", "huangnan", "qinghai" },
			{ "174", "果洛", "青海", "guoluo", "qinghai" },
			{ "175", "玉树", "青海", "yushu", "qinghai" },
			{ "176", "海东", "青海", "haidong", "qinghai" },
			{ "177", "海南", "青海", "hainan", "qinghai" },
			{ "178", "武汉", "湖北", "wuhan", "hubei" },
			{ "179", "宜昌", "湖北", "yichang", "hubei" },
			{ "180", "黄冈", "湖北", "huanggang", "hubei" },
			{ "181", "恩施", "湖北", "enshi", "hubei" },
			{ "182", "荆州", "湖北", "jingzhou", "hubei" },
			{ "183", "神农架", "湖北", "shennongjia", "hubei" },
			{ "184", "十堰", "湖北", "shiyan", "hubei" },
			{ "185", "咸宁", "湖北", "xianning", "hubei" },
			{ "186", "襄樊", "湖北", "xiangfan", "hubei" },
			{ "187", "孝感", "湖北", "xiaogan", "hubei" },
			{ "188", "随州", "湖北", "suizhou", "hubei" },
			{ "189", "黄石", "湖北", "huangshi", "hubei" },
			{ "190", "荆门", "湖北", "jingmen", "hubei" },
			{ "191", "鄂州", "湖北", "ezhou", "hubei" },
			{ "192", "长沙", "湖南", "changsha", "hunan" },
			{ "193", "邵阳", "湖南", "shaoyang", "hunan" },
			{ "194", "常德", "湖南", "changde", "hunan" },
			{ "195", "郴州", "湖南", "chenzhou", "hunan" },
			{ "196", "吉首", "湖南", "jishou", "hunan" },
			{ "197", "株洲", "湖南", "zhuzhou", "hunan" },
			{ "198", "娄底", "湖南", "loudi", "hunan" },
			{ "199", "湘潭", "湖南", "xiangtan", "hunan" },
			{ "200", "益阳", "湖南", "yiyang", "hunan" },
			{ "201", "永州", "湖南", "yongzhou", "hunan" },
			{ "202", "岳阳", "湖南", "yueyang", "hunan" },
			{ "203", "衡阳", "湖南", "hengyang", "hunan" },
			{ "204", "怀化", "湖南", "huaihua", "hunan" },
			{ "205", "韶山", "湖南", "shaoshan", "hunan" },
			{ "206", "张家界", "湖南", "zhangjiajie", "hunan" },
			{ "207", "杭州", "浙江", "hangzhou", "zhejiang" },
			{ "208", "湖州", "浙江", "huzhou", "zhejiang" },
			{ "209", "金华", "浙江", "jinhua", "zhejiang" },
			{ "210", "宁波", "浙江", "ningbo", "zhejiang" },
			{ "211", "丽水", "浙江", "lishui", "zhejiang" },
			{ "212", "绍兴", "浙江", "shaoxing", "zhejiang" },
			{ "213", "衢州", "浙江", "quzhou", "zhejiang" },
			{ "214", "嘉兴", "浙江", "jiaxing", "zhejiang" },
			{ "215", "台州", "浙江", "taizhou", "zhejiang" },
			{ "216", "舟山", "浙江", "zhoushan", "zhejiang" },
			{ "217", "温州", "浙江", "wenzhou", "zhejiang" },
			{ "218", "南昌", "江西", "nanchang", "jiangxi" },
			{ "219", "萍乡", "江西", "pingxiang", "jiangxi" },
			{ "220", "九江", "江西", "jiujiang", "jiangxi" },
			{ "221", "上饶", "江西", "shangrao", "jiangxi" },
			{ "222", "抚州", "江西", "fuzhou", "jiangxi" },
			{ "223", "吉安", "江西", "jian", "jiangxi" },
			{ "224", "鹰潭", "江西", "yingtan", "jiangxi" },
			{ "225", "宜春", "江西", "yichun", "jiangxi" },
			{ "226", "新余", "江西", "xinyu", "jiangxi" },
			{ "227", "景德镇", "江西", "jingdezhen", "jiangxi" },
			{ "228", "赣州", "江西", "ganzhou", "jiangxi" },
			{ "229", "福州", "福建", "fuzhou", "fujian" },
			{ "230", "厦门", "福建", "xiamen", "fujian" },
			{ "231", "龙岩", "福建", "longyan", "fujian" },
			{ "232", "南平", "福建", "nanping", "fujian" },
			{ "233", "宁德", "福建", "ningde", "fujian" },
			{ "234", "莆田", "福建", "putian", "fujian" },
			{ "235", "泉州", "福建", "quanzhou", "fujian" },
			{ "236", "三明", "福建", "sanming", "fujian" },
			{ "237", "漳州", "福建", "zhangzhou", "fujian" },
			{ "238", "贵阳", "贵州", "guiyang", "guizhou" },
			{ "239", "安顺", "贵州", "anshun", "guizhou" },
			{ "240", "赤水", "贵州", "chishui", "guizhou" },
			{ "241", "遵义", "贵州", "zunyi", "guizhou" },
			{ "242", "铜仁", "贵州", "tongren", "guizhou" },
			{ "243", "六盘水", "贵州", "liupanshui", "guizhou" },
			{ "244", "毕节", "贵州", "bijie", "guizhou" },
			{ "245", "凯里", "贵州", "kaili", "guizhou" },
			{ "246", "都匀", "贵州", "duyun", "guizhou" },
			{ "247", "成都", "四川", "chengdu", "sichuan" },
			{ "248", "泸州", "四川", "luzhou", "sichuan" },
			{ "249", "内江", "四川", "neijiang", "sichuan" },
			{ "250", "凉山", "四川", "liangshan", "sichuan" },
			{ "251", "阿坝", "四川", "aba", "sichuan" },
			{ "252", "巴中", "四川", "bazhong", "sichuan" },
			{ "253", "广元", "四川", "guangyuan", "sichuan" },
			{ "254", "乐山", "四川", "leshan", "sichuan" },
			{ "255", "绵阳", "四川", "mianyang", "sichuan" },
			{ "256", "德阳", "四川", "deyang", "sichuan" },
			{ "257", "攀枝花", "四川", "panzhihua", "sichuan" },
			{ "258", "雅安", "四川", "yaan", "sichuan" },
			{ "259", "宜宾", "四川", "yibin", "sichuan" },
			{ "260", "自贡", "四川", "zigong", "sichuan" },
			{ "261", "甘孜州", "四川", "ganzizhou", "sichuan" },
			{ "262", "达州", "四川", "dazhou", "sichuan" },
			{ "263", "资阳", "四川", "ziyang", "sichuan" },
			{ "264", "广安", "四川", "guangan", "sichuan" },
			{ "265", "遂宁", "四川", "suining", "sichuan" },
			{ "266", "眉山", "四川", "meishan", "sichuan" },
			{ "267", "南充", "四川", "nanchong", "sichuan" },
			{ "268", "广州", "广东", "guangzhou", "guangdong" },
			{ "269", "深圳", "广东", "shenzhen", "guangdong" },
			{ "270", "潮州", "广东", "chaozhou", "guangdong" },
			{ "271", "韶关", "广东", "shaoguan", "guangdong" },
			{ "272", "湛江", "广东", "zhanjiang", "guangdong" },
			{ "273", "惠州", "广东", "huizhou", "guangdong" },
			{ "274", "清远", "广东", "qingyuan", "guangdong" },
			{ "275", "东莞", "广东", "dongguan", "guangdong" },
			{ "276", "江门", "广东", "jiangmen", "guangdong" },
			{ "277", "茂名", "广东", "maoming", "guangdong" },
			{ "278", "肇庆", "广东", "zhaoqing", "guangdong" },
			{ "279", "汕尾", "广东", "shanwei", "guangdong" },
			{ "280", "河源", "广东", "heyuan", "guangdong" },
			{ "281", "揭阳", "广东", "jieyang", "guangdong" },
			{ "282", "梅州", "广东", "meizhou", "guangdong" },
			{ "283", "中山", "广东", "zhongshan", "guangdong" },
			{ "284", "德庆", "广东", "deqing", "guangdong" },
			{ "285", "阳江", "广东", "yangjiang", "guangdong" },
			{ "286", "云浮", "广东", "yunfu", "guangdong" },
			{ "287", "珠海", "广东", "zhuhai", "guangdong" },
			{ "288", "汕头", "广东", "shantou", "guangdong" },
			{ "289", "佛山", "广东", "foshan", "guangdong" },
			{ "290", "南宁", "广西", "nanning", "guangxi" },
			{ "291", "桂林", "广西", "guilin", "guangxi" },
			{ "292", "阳朔", "广西", "yangshuo", "guangxi" },
			{ "293", "柳州", "广西", "liuzhou", "guangxi" },
			{ "294", "梧州", "广西", "wuzhou", "guangxi" },
			{ "295", "玉林", "广西", "yulin", "guangxi" },
			{ "296", "桂平", "广西", "guiping", "guangxi" },
			{ "297", "贺州", "广西", "hezhou", "guangxi" },
			{ "298", "钦州", "广西", "qinzhou", "guangxi" },
			{ "299", "贵港", "广西", "guigang", "guangxi" },
			{ "300", "防城港", "广西", "fangchenggang", "guangxi" },
			{ "301", "百色", "广西", "baise", "guangxi" },
			{ "302", "北海", "广西", "beihai", "guangxi" },
			{ "303", "河池", "广西", "hechi", "guangxi" },
			{ "304", "来宾", "广西", "laibin", "guangxi" },
			{ "305", "崇左", "广西", "chongzuo", "guangxi" },
			{ "306", "昆明", "云南", "kunming", "yunnan" },
			{ "307", "保山", "云南", "baoshan", "yunnan" },
			{ "308", "楚雄", "云南", "chuxiong", "yunnan" },
			{ "309", "德宏", "云南", "dehong", "yunnan" },
			{ "310", "红河", "云南", "honghe", "yunnan" },
			{ "311", "临沧", "云南", "lincang", "yunnan" },
			{ "312", "怒江", "云南", "nujiang", "yunnan" },
			{ "313", "曲靖", "云南", "qujing", "yunnan" },
			{ "314", "思茅", "云南", "simao", "yunnan" },
			{ "315", "文山", "云南", "wenshan", "yunnan" },
			{ "316", "玉溪", "云南", "yuxi", "yunnan" },
			{ "317", "昭通", "云南", "zhaotong", "yunnan" },
			{ "318", "丽江", "云南", "lijiang", "yunnan" },
			{ "319", "大理", "云南", "dali", "yunnan" },
			{ "320", "海口", "海南", "haikou", "hainan" },
			{ "321", "三亚", "海南", "sanya", "hainan" },
			{ "322", "儋州", "海南", "danzhou", "hainan" },
			{ "323", "琼山", "海南", "qiongshan", "hainan" },
			{ "324", "通什", "海南", "tongshi", "hainan" },
			{ "325", "文昌", "海南", "wenchang", "hainan" },
			{ "326", "乌鲁木齐", "新疆", "wulumuqi", "xinjiang" },
			{ "327", "阿勒泰", "新疆", "aletai", "xinjiang" },
			{ "328", "阿克苏", "新疆", "akesu", "xinjiang" },
			{ "329", "昌吉", "新疆", "changji", "xinjiang" },
			{ "330", "哈密", "新疆", "hami", "xinjiang" },
			{ "331", "和田", "新疆", "hetian", "xinjiang" },
			{ "332", "喀什", "新疆", "kashi", "xinjiang" },
			{ "333", "克拉玛依", "新疆", "kelamayi", "xinjiang" },
			{ "334", "石河子", "新疆", "shihezi", "xinjiang" },
			{ "335", "塔城", "新疆", "tacheng", "xinjiang" },
			{ "336", "库尔勒", "新疆", "kuerle", "xinjiang" },
			{ "337", "吐鲁番", "新疆", "tulufan", "xinjiang" },
			{ "338", "伊宁", "新疆", "yining", "xinjiang" },
			{ "339", "拉萨", "西藏", "lasa", "xicang" },
			{ "340", "阿里", "西藏", "ali", "xicang" },
			{ "341", "昌都", "西藏", "changdu", "xicang" },
			{ "342", "那曲", "西藏", "naqu", "xicang" },
			{ "343", "日喀则", "西藏", "rikaze", "xicang" },
			{ "344", "山南", "西藏", "shannan", "xicang" },
			{ "345", "林芝", "西藏", "linzhi", "xicang" },
			{ "346", "台北", "台湾", "taibei", "taiwan" },
			{ "347", "高雄", "台湾", "gaoxiong", "taiwan" },

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
