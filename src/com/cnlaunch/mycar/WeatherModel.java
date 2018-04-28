package com.cnlaunch.mycar;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author xuzhuowei
 *天气预报数据窗口
 */
public class WeatherModel {

	private static String city = "深圳";
	private static int weatherIconId = -1;
	private static int weatherSmallIconResId = -1;
	private static int weatherMidIconResId = -1;
	private static String weatherInfo = "";
	private static String temprature = "";
	private static String tourInfo = "";
	private static int tourIconId = -1;
	private static String washCarInfo = "";
	private static int tourIconResId = -1;
	private static int washCarIconId = -1;
	private static int washCarIconResId = -1;
	private static String general;
	
	private static HashMap<Integer, Integer> weatherSmallIconResMap;
	private static HashMap<Integer, Integer> weatherMidIconResMap;
	private static HashMap<Integer, Integer> tourIconResMap;
	private static HashMap<Integer, Integer> washCarIconResMap;

	//weatherData数据示例
	/*
	{
		"Province":"广东",
		"City":"深圳",
		"CityCode":"59493",
		"UpdateTime":"2011-12-22 9:04:43",
		"Air":"12℃/19℃",
		"General":"12月22日 多云",
		"Wind":"北风3-4级",
		"SynopticChart":"今日天气实况：气温：15℃；风向/风力：北风 2级；湿度：57%；空气质量：良；紫外线强度：弱",
		"LifeIndex":"穿衣指数：建议着薄型套装或牛仔衫裤等春秋过渡装。年老体弱者宜着套装、夹克衫等。\n感冒指数：相对今天出现了较大幅度降温，较易发生感冒，体质较弱的朋友请注意适当防护。\n运动指数：天气较好，较适宜开展户内运动，考虑风力较强且气温较低，户外运动注意防风并适当增减衣物。\n洗车指数：适宜洗车，未来持续两天无雨天气较好，适合擦洗汽车，蓝天白云、风和日丽将伴您的车子连日洁净。\n晾晒指数：天气不错，适宜晾晒。赶紧把久未见阳光的衣物搬出来吸收一下太阳的味道吧！\n旅游指数：白云飘飘，温度适宜，但风稍微有点大。这样的天气很适宜旅游，您可以尽情地享受大自然的无限风光。\n路况指数：天气较好，路面比较干燥，路况较好。\n舒适度指数：温度适宜，风力不大，您在这样的天气条件下，会感到比较清爽和舒适。\n空气污染指数：气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。\n紫外线指数：紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。",
		"SecondAir":"10℃/17℃",
		"SecondGeneral":"12月23日 多云",
		"SecondWind":"北风3-4级",
		"ThirdAir":"8℃/17℃",
		"ThirdGeneral":"12月24日 晴",
		"ThirdWind":"北风3-4级转无持续风向微风",
		"CitySynopsis":"深圳市位于广东省中南沿海地区，珠江入海口之东偏北。深圳市地处中华人民共和国广东省中南沿海，陆域位置东经113°46′至114°37′，北纬22°27′至22°52′。东西长81.4公里，南北宽（最短处）为10.8公里，东临大鹏湾，西连珠江口，南邻香港，与九龙半岛接壤，与香港新界一河之隔，被称为“香港的后花园”。深圳这座新兴的城市整洁美丽，四季草木葱笼，当地政府因地制宜地开发了不少旅游景点，将自然风光与人工建筑巧妙结合。深圳历史悠久，文化发达，旅游资源也十分丰富，保存在地上、地下的文物古迹十分丰富。80年代深圳博物馆考古人员进行了文物普查，发现了一大批颇有价值的古建筑、古遗址、古墓葬、古寺庙、古城址和风景名胜等。深圳市人民政府于1983年先后公布了两批重点文物保护单位，并对名胜古迹作了修复，再现了原有的风貌，以供游人观赏。深圳地处北回归线以南,
		属亚热带海洋性气候,
		气候温和,
		雨量充沛,
		日照时间长。夏无酷暑,
		时间长达6个月。春秋冬三季气候温暖,
		无寒冷之忧。年平均气温为22.3℃。景观：锦绣中华、世界之窗、明思克航母世界、欢乐谷",
		"CleanCarNumber":"1",
		"Icon1":"1",
		"Icon2":"1",
		"SecondIcon1":"1",
		"SecondIcon2":"1",
		"ThirdIcon1":"0",
		"ThirdIcon2":"0",
		"OutGoNumber":"1"
	}
	*/
	
	
	public static void setWeatherData(String weatherData) {

		try {
			JSONObject jsonObj = new JSONObject(weatherData);
			city=jsonObj.getString("City");
			weatherIconId = jsonObj.getInt("Icon1");
			weatherSmallIconResId = getWeatherSmallIconResId(weatherIconId);
			weatherMidIconResId = getWeatherMidIconResId(weatherIconId);
			
			tourIconId = jsonObj.getInt("OutGoNumber");
			tourIconResId = getTourIconResId(tourIconId);
			washCarIconId = jsonObj.getInt("CleanCarNumber");
			washCarIconResId = getWashCarIconResId(washCarIconId);
			washCarInfo = getWashCarInfo(washCarIconId);
			tourInfo = getTourInfo(tourIconId);
			temprature = jsonObj.getString("Air");
		
			general = jsonObj.getString("General");
		} catch (JSONException e) {
			//do onthing
		}

	}

	private static String getTourInfo(int vTourIconId) {
		switch(vTourIconId){
		case 5:
			return "很适宜出行";
		case 4:
			return "适宜出行";
		case 3:
			return "较适宜出行";
		case 2:
			return "较不宜出行";
		case 1:
			return "不宜出行";
		default:
			return null;
		}
	}

	private static String getWashCarInfo(int vWashCarIconId) {
		switch(vWashCarIconId){
		case 4:
			return "适宜洗车";
		case 3:
			return "较适宜洗车";
		case 2:
			return "较不宜洗车";
		case 1:
			return "不宜洗车";
		default:
			return null;
		}
	}

	private static int getWeatherSmallIconResId(int vWeatherIconId) {
		if (weatherSmallIconResMap == null) {
			weatherSmallIconResMap = new HashMap<Integer, Integer>();
			weatherSmallIconResMap.put(0, R.drawable.weather_small_0);
			weatherSmallIconResMap.put(1, R.drawable.weather_small_1);
			weatherSmallIconResMap.put(2, R.drawable.weather_small_2);
			weatherSmallIconResMap.put(3, R.drawable.weather_small_3);
			weatherSmallIconResMap.put(4, R.drawable.weather_small_4);
			weatherSmallIconResMap.put(5, R.drawable.weather_small_5);
			weatherSmallIconResMap.put(6, R.drawable.weather_small_6);
			weatherSmallIconResMap.put(7, R.drawable.weather_small_7);
			weatherSmallIconResMap.put(8, R.drawable.weather_small_8);
			weatherSmallIconResMap.put(9, R.drawable.weather_small_9);
			weatherSmallIconResMap.put(10, R.drawable.weather_small_10);
			weatherSmallIconResMap.put(11, R.drawable.weather_small_11);
			weatherSmallIconResMap.put(12, R.drawable.weather_small_12);
			weatherSmallIconResMap.put(13, R.drawable.weather_small_13);
			weatherSmallIconResMap.put(14, R.drawable.weather_small_14);
			weatherSmallIconResMap.put(15, R.drawable.weather_small_15);
			weatherSmallIconResMap.put(16, R.drawable.weather_small_16);
			weatherSmallIconResMap.put(17, R.drawable.weather_small_17);
			weatherSmallIconResMap.put(18, R.drawable.weather_small_18);
			weatherSmallIconResMap.put(19, R.drawable.weather_small_19);
			weatherSmallIconResMap.put(20, R.drawable.weather_small_20);
			weatherSmallIconResMap.put(21, R.drawable.weather_small_21);
			weatherSmallIconResMap.put(22, R.drawable.weather_small_22);
			weatherSmallIconResMap.put(23, R.drawable.weather_small_23);
			weatherSmallIconResMap.put(24, R.drawable.weather_small_24);
			weatherSmallIconResMap.put(25, R.drawable.weather_small_25);
			weatherSmallIconResMap.put(26, R.drawable.weather_small_26);
			weatherSmallIconResMap.put(27, R.drawable.weather_small_27);
			weatherSmallIconResMap.put(28, R.drawable.weather_small_28);
			weatherSmallIconResMap.put(29, R.drawable.weather_small_29);
			weatherSmallIconResMap.put(30, R.drawable.weather_small_30);
		}
		if (weatherSmallIconResMap.containsKey(vWeatherIconId)) {
			return weatherSmallIconResMap.get(vWeatherIconId).intValue();
		} else {
			return -1;
		}
	}

	private static int getWeatherMidIconResId(int vWeatherIconId) {
		if (weatherMidIconResMap == null) {
			weatherMidIconResMap = new HashMap<Integer, Integer>();
			weatherMidIconResMap.put(0, R.drawable.weather_mid_0);
			weatherMidIconResMap.put(1, R.drawable.weather_mid_1);
			weatherMidIconResMap.put(2, R.drawable.weather_mid_2);
			weatherMidIconResMap.put(3, R.drawable.weather_mid_3);
			weatherMidIconResMap.put(4, R.drawable.weather_mid_4);
			weatherMidIconResMap.put(5, R.drawable.weather_mid_5);
			weatherMidIconResMap.put(6, R.drawable.weather_mid_6);
			weatherMidIconResMap.put(7, R.drawable.weather_mid_7);
			weatherMidIconResMap.put(8, R.drawable.weather_mid_8);
			weatherMidIconResMap.put(9, R.drawable.weather_mid_9);
			weatherMidIconResMap.put(10, R.drawable.weather_mid_10);
			weatherMidIconResMap.put(11, R.drawable.weather_mid_11);
			weatherMidIconResMap.put(12, R.drawable.weather_mid_12);
			weatherMidIconResMap.put(13, R.drawable.weather_mid_13);
			weatherMidIconResMap.put(14, R.drawable.weather_mid_14);
			weatherMidIconResMap.put(15, R.drawable.weather_mid_15);
			weatherMidIconResMap.put(16, R.drawable.weather_mid_16);
			weatherMidIconResMap.put(17, R.drawable.weather_mid_17);
			weatherMidIconResMap.put(18, R.drawable.weather_mid_18);
			weatherMidIconResMap.put(19, R.drawable.weather_mid_19);
			weatherMidIconResMap.put(20, R.drawable.weather_mid_20);
			weatherMidIconResMap.put(21, R.drawable.weather_mid_21);
			weatherMidIconResMap.put(22, R.drawable.weather_mid_22);
			weatherMidIconResMap.put(23, R.drawable.weather_mid_23);
			weatherMidIconResMap.put(24, R.drawable.weather_mid_24);
			weatherMidIconResMap.put(25, R.drawable.weather_mid_25);
			weatherMidIconResMap.put(26, R.drawable.weather_mid_26);
			weatherMidIconResMap.put(27, R.drawable.weather_mid_27);
			weatherMidIconResMap.put(28, R.drawable.weather_mid_28);
			weatherMidIconResMap.put(29, R.drawable.weather_mid_29);
			weatherMidIconResMap.put(30, R.drawable.weather_mid_30);
		}
		if (weatherMidIconResMap.containsKey(vWeatherIconId)) {
			return weatherMidIconResMap.get(vWeatherIconId).intValue();
		} else {
			return -1;
		}
	}

	private static int getTourIconResId(int vTourIconId) {
		if (tourIconResMap == null) {
			tourIconResMap = new HashMap<Integer, Integer>();
			tourIconResMap.put(1, R.drawable.weather_tour_1);
			tourIconResMap.put(2, R.drawable.weather_tour_2);
			tourIconResMap.put(3, R.drawable.weather_tour_3);
			tourIconResMap.put(4, R.drawable.weather_tour_4);
			tourIconResMap.put(5, R.drawable.weather_tour_5);
		}
		if (tourIconResMap.containsKey(vTourIconId)) {
			return tourIconResMap.get(vTourIconId).intValue();
		} else {
			return -1;
		}
	}

	private static int getWashCarIconResId(int vWashCarIconId) {
		if (washCarIconResMap == null) {
			washCarIconResMap = new HashMap<Integer, Integer>();
			washCarIconResMap.put(1, R.drawable.weather_washcar_1);
			washCarIconResMap.put(2, R.drawable.weather_washcar_2);
			washCarIconResMap.put(3, R.drawable.weather_washcar_3);
			washCarIconResMap.put(4, R.drawable.weather_washcar_4);
		}
		if (washCarIconResMap.containsKey(vWashCarIconId)) {
			return washCarIconResMap.get(vWashCarIconId).intValue();
		} else {
			return -1;
		}
	}


	
	public static String getCity() {
		return city;
	}

	public static void setCity(String city) {
		WeatherModel.city = city;
	}

	public static int getWeatherIconId() {
		return weatherIconId;
	}

	
	
	public static int getWeatherSmallIconResId() {
		return weatherSmallIconResId;
	}

	public static void setWeatherSmallIconResId(int weatherSmallIconResId) {
		WeatherModel.weatherSmallIconResId = weatherSmallIconResId;
	}

	public static int getWeatherMidIconResId() {
		return weatherMidIconResId;
	}

	public static void setWeatherMidIconResId(int weatherMidIconResId) {
		WeatherModel.weatherMidIconResId = weatherMidIconResId;
	}

	public static void setWeatherIconId(int weatherIconId) {
		WeatherModel.weatherIconId = weatherIconId;
	}

	public static int getTourIconId() {
		return tourIconId;
	}

	public static void setTourIconId(int tourIconId) {
		WeatherModel.tourIconId = tourIconId;
	}

	public static int getWashCarIconId() {
		return washCarIconId;
	}

	public static void setWashCarIconId(int washCarIconId) {
		WeatherModel.washCarIconId = washCarIconId;
	}

	public static String getTemprature() {
		return temprature;
	}

	public static void setTemprature(String temprature) {
		WeatherModel.temprature = temprature;
	}

	public static String getWeatherInfo() {
		return weatherInfo;
	}

	public static void setWeatherInfo(String weatherInfo) {
		WeatherModel.weatherInfo = weatherInfo;
	}

	public static String getTourInfo() {
		return tourInfo;
	}

	public static void setTourInfo(String tourInfo) {
		WeatherModel.tourInfo = tourInfo;
	}

	public static String getWashCarInfo() {
		return washCarInfo;
	}

	public static void setWashCarInfo(String washCarInfo) {
		WeatherModel.washCarInfo = washCarInfo;
	}

	public static int getTourIconResId() {
		return tourIconResId;
	}

	public static void setTourIconResId(int tourIconResId) {
		WeatherModel.tourIconResId = tourIconResId;
	}

	public static int getWashCarIconResId() {
		return washCarIconResId;
	}

	public static void setWashCarIconResId(int washCarIconResId) {
		WeatherModel.washCarIconResId = washCarIconResId;
	}

	public static String getGeneral() {
		return general;
	}

	public static void setGeneral(String general) {
		WeatherModel.general = general;
	}
	
	
}
