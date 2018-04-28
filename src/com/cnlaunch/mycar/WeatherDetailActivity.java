package com.cnlaunch.mycar;

import java.lang.reflect.Field;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;


/**
 *@author zhangweiwei
 *@version 2011-11-14下午4:40:58
 *类说明
 */
public class WeatherDetailActivity extends Activity{
	TextView tvDisplayDetailWeatherInfo;
    Button btSet;
    private String city;
    private SoapObject sobResultWeather;
    //处理子线程发过来的消息
    private Handler messageHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				displayWeatherInfo();
				break;

			default:
				break;
			}
		}
    	
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		city = MyCarActivity.city;

		tvDisplayDetailWeatherInfo = (TextView)findViewById(R.id.weather_tv_displayDetailWeatherInfo);
		displayWeatherInfo();
		btSet = (Button)findViewById(R.id.weather_bt_set);
		btSet.setOnClickListener(new btSetListener());
	}
	private void displayWeatherInfo(){
		   String image1 = "weather_a_"+sobResultWeather.getProperty(8).toString();
	       image1 = image1.substring(0, image1.length()-4);
	       String image2 = "weather_a_"+sobResultWeather.getProperty(9).toString();
	       image2 = image2.substring(0, image2.length()-4);
	       String image3 = "weather_a_"+sobResultWeather.getProperty(15).toString();
	       image3 = image3.substring(0, image3.length()-4);
	       String image4 = "weather_a_"+sobResultWeather.getProperty(16).toString();
	       image4 = image4.substring(0, image4.length()-4);
	       String image5 = "weather_a_"+sobResultWeather.getProperty(20).toString();
	       image5 = image5.substring(0, image5.length()-4);
	       String image6 = "weather_a_"+sobResultWeather.getProperty(21).toString();
	       image6 = image6.substring(0, image6.length()-4);
	       String firstDay = "最新天气实况（"+getInfo(4)+"发布）<br></br>"+"城市："+getInfo(1)+"<br></br><br></br>"+getInfo(6)+"<br></br>白天"+"<img src="+image1+"></image>"+"夜晚"+"<img src="+image2+"></image>"+"<br></br>最低温/最高温："+getInfo(5)+"<br></br>"+getInfo(7)+"<br></br><br></br>";
	       String seconday = getInfo(13)+"<br></br>白天"+"<img src="+image3+"></image>"+"夜晚"+"<img src="+image4+"></image>"+"<br></br>最低温/最高温："+getInfo(12)+"<br></br>"+getInfo(14)+"<br></br><br></br>";
	       String thirday  = getInfo(18)+"<br></br>白天"+"<img src="+image5+"></image>"+"夜晚"+"<img src="+image6+"></image>"+"<br></br>最低温/最高温："+getInfo(17)+"<br></br>"+getInfo(19)+"<br></br><br></br>";
	       String source = firstDay+seconday+thirday+getInfo(11).toString().replace("\n", "<br></br><br></br>");
	       ImageGetter imageGetter = new Html.ImageGetter() {
	           public Drawable getDrawable(String source) 
	           {
	              Drawable drawable = null;
	              int id = getResourceId(source);
	              //根据id从资源文件中获取图片对象
	              drawable = getResources().getDrawable(id);
	              drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
	                         .getIntrinsicHeight());
	              return drawable;
	             }
	         };

	       Spanned text = Html.fromHtml(source,imageGetter,null);
	       tvDisplayDetailWeatherInfo.setText(text); 
	   }
	   //name参数表示res/drawable中的图像文件名（不含扩展名）
	   public int getResourceId(String name)
	   {
		   try 
		   {
			   //根据资源ID的变量名获得Field对象
			   Field field = R.drawable.class.getField(name);
			   //取得并返回资源ID
			   return Integer.parseInt(field.get(null).toString());
		   } 
		   catch (Exception e) {
			// TODO: handle exception
			   return 0;
		}
	   }
	   public String getInfo(int i){
		   return sobResultWeather.getProperty(i).toString();
	   }
    class btSetListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(WeatherDetailActivity.this,WeatherSetCityActivity.class);
			startActivity(intent);
			
		}
    	
    }

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Thread checkCityThread = new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(city!=getCity()){
		        	city = getCity();
		    		//若用户更改了城市，则重新获取数据
		        	String nameSpace = "http://WebXml.com.cn/";
//		            String wsUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
		            String methodName = "getWeatherbyCityName";
		            String soapAction = nameSpace+methodName; 	        	
		            TreeMap paraMap = new TreeMap<String, Object>();
		            paraMap.put("theCityName", city);
		        	RequestParameter mRequestParameter = new RequestParameter(Constants.SERVICE_WEATHER, methodName, soapAction, paraMap,false);
		    		WebServiceManager mWebServiceManager=new WebServiceManager(mRequestParameter);
		    		sobResultWeather = (SoapObject) mWebServiceManager.execute().object;
		    		sobResultWeather = (SoapObject)sobResultWeather.getProperty(0);
		    		Message message = Message.obtain();
		    		message.what = 1;
		    		messageHandler.sendMessage(message);
		    		
		        }
			}
		});
		checkCityThread.start();
	}

	public String getCity(){
		SharedPreferences mSharedPreferences = getSharedPreferences("PrefsFileOfWeatherSet", Context.MODE_WORLD_READABLE);
		String mycity = mSharedPreferences.getString("City", "北京");
		mycity = mycity.substring(0,mycity.length()-7);
		return mycity;
	}
}
