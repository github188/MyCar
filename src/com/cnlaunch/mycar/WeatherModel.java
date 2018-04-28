package com.cnlaunch.mycar;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author xuzhuowei
 *����Ԥ�����ݴ���
 */
public class WeatherModel {

	private static String city = "����";
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

	//weatherData����ʾ��
	/*
	{
		"Province":"�㶫",
		"City":"����",
		"CityCode":"59493",
		"UpdateTime":"2011-12-22 9:04:43",
		"Air":"12��/19��",
		"General":"12��22�� ����",
		"Wind":"����3-4��",
		"SynopticChart":"��������ʵ�������£�15�棻����/���������� 2����ʪ�ȣ�57%����������������������ǿ�ȣ���",
		"LifeIndex":"����ָ���������ű�����װ��ţ������ȴ������װ������������������װ���п����ȡ�\n��ðָ������Խ�������˽ϴ���Ƚ��£����׷�����ð�����ʽ�����������ע���ʵ�������\n�˶�ָ���������Ϻã������˿�չ�����˶������Ƿ�����ǿ�����½ϵͣ������˶�ע����粢�ʵ��������\nϴ��ָ��������ϴ����δ�������������������Ϻã��ʺϲ�ϴ������������ơ���������������ĳ������սྻ��\n��ɹָ������������������ɹ���Ͻ��Ѿ�δ�������������������һ��̫����ζ���ɣ�\n����ָ��������ƮƮ���¶����ˣ�������΢�е���������������������Σ������Ծ�������ܴ���Ȼ�����޷�⡣\n·��ָ���������Ϻã�·��Ƚϸ��·���Ϻá�\n���ʶ�ָ�����¶����ˣ����������������������������£���е��Ƚ���ˬ�����ʡ�\n������Ⱦָ�����������������ڿ�����Ⱦ��ϡ�͡���ɢ����������������������\n������ָ����������ǿ�Ƚ������������ǰͿ��SPF��12-15֮�䡢PA+�ķ�ɹ����Ʒ��",
		"SecondAir":"10��/17��",
		"SecondGeneral":"12��23�� ����",
		"SecondWind":"����3-4��",
		"ThirdAir":"8��/17��",
		"ThirdGeneral":"12��24�� ��",
		"ThirdWind":"����3-4��ת�޳�������΢��",
		"CitySynopsis":"������λ�ڹ㶫ʡ�����غ��������齭�뺣��֮��ƫ���������еش��л����񹲺͹��㶫ʡ�����غ���½��λ�ö���113��46����114��37�䣬��γ22��27����22��52�䡣������81.4����ϱ�����̴���Ϊ10.8������ٴ����壬�����齭�ڣ�������ۣ�������뵺������������½�һ��֮��������Ϊ����۵ĺ�԰���������������˵ĳ��������������ļ���ľ��������������������˵ؿ����˲������ξ��㣬����Ȼ������˹����������ϡ�������ʷ�ƾã��Ļ����������ԴҲʮ�ַḻ�������ڵ��ϡ����µ�����ż�ʮ�ַḻ��80������ڲ���ݿ�����Ա�����������ղ飬������һ�������м�ֵ�ĹŽ���������ַ����Ĺ�ᡢ�������ų�ַ�ͷ羰��ʤ�ȡ�����������������1983���Ⱥ󹫲��������ص����ﱣ����λ��������ʤ�ż������޸���������ԭ�еķ�ò���Թ����˹��͡����ڵش����ع�������,
		�����ȴ�����������,
		�����º�,
		��������,
		����ʱ�䳤�����޿���,
		ʱ�䳤��6���¡����ﶬ����������ů,
		�޺���֮�ǡ���ƽ������Ϊ22.3�档���ۣ������л�������֮������˼�˺�ĸ���硢���ֹ�",
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
			return "�����˳���";
		case 4:
			return "���˳���";
		case 3:
			return "�����˳���";
		case 2:
			return "�ϲ��˳���";
		case 1:
			return "���˳���";
		default:
			return null;
		}
	}

	private static String getWashCarInfo(int vWashCarIconId) {
		switch(vWashCarIconId){
		case 4:
			return "����ϴ��";
		case 3:
			return "������ϴ��";
		case 2:
			return "�ϲ���ϴ��";
		case 1:
			return "����ϴ��";
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
