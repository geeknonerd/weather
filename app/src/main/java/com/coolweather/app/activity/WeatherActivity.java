package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.ImageLoader;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private ImageView weatherIcon;
	private TextView weatherDespText;
	private TextView weatherDesp2Text;
	private TextView weatherDesp3Text;
	private TextView weatherDesp4Text;
	private TextView weatherDesp5Text;
	private TextView weatherDesp6Text;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView temp3Text;
	private TextView temp4Text;
	private TextView temp5Text;
	private TextView temp6Text;
	private TextView currentDateText;
	private TextView weatherD2Text;
	private TextView weatherD3Text;
	private TextView weatherD4Text;
	private TextView weatherD5Text;
	private TextView weatherD6Text;
	private Button switchCity;
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText =(TextView) findViewById(R.id.publish_text);
		weatherIcon = (ImageView) findViewById(R.id.weather_icon);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		weatherDesp2Text = (TextView) findViewById(R.id.weather_desp2);
		weatherDesp3Text = (TextView) findViewById(R.id.weather_desp3);
		weatherDesp4Text = (TextView) findViewById(R.id.weather_desp4);
		weatherDesp5Text = (TextView) findViewById(R.id.weather_desp5);
		weatherDesp6Text = (TextView) findViewById(R.id.weather_desp6);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		temp3Text = (TextView) findViewById(R.id.temp3);
		temp4Text = (TextView) findViewById(R.id.temp4);
		temp5Text = (TextView) findViewById(R.id.temp5);
		temp6Text = (TextView) findViewById(R.id.temp6);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherD2Text = (TextView) findViewById(R.id.weather_d2);
		weatherD3Text = (TextView) findViewById(R.id.weather_d3);
		weatherD4Text = (TextView) findViewById(R.id.weather_d4);
		weatherD5Text = (TextView) findViewById(R.id.weather_d5);
		weatherD6Text = (TextView) findViewById(R.id.weather_d6);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else {
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	

	private void queryWeatherCode(String countycode){
		String address = "http://www.weather.com.cn/data/list3/city"+countycode+".xml";
		queryFromServer(address,"countyCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://api.k780.com:88/?app=weather.future&weaid="+weatherCode+"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
		queryFromServer(address, "weatherCode");
	}
	
	
	private void queryFromServer(final String address,final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array!=null&&array.length==2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
					publishText.setText("同步失败...");	
					}
				});
			}
		});
	}
	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		new ImageLoader().showImageByThread(weatherIcon,prefs.getString("weather_icon",""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		temp3Text.setText(prefs.getString("temp3", ""));
		temp4Text.setText(prefs.getString("temp4", ""));
		temp5Text.setText(prefs.getString("temp5", ""));
		temp6Text.setText(prefs.getString("temp6", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		weatherDesp2Text.setText(prefs.getString("weather_desp2", ""));
		weatherDesp3Text.setText(prefs.getString("weather_desp3", ""));
		weatherDesp4Text.setText(prefs.getString("weather_desp4", ""));
		weatherDesp5Text.setText(prefs.getString("weather_desp5", ""));
		weatherDesp6Text.setText(prefs.getString("weather_desp6", ""));
		publishText.setText(prefs.getString("current_date", "")+"发布");
		currentDateText.setText("今天");
		weatherD2Text.setText("明天");
		weatherD3Text.setText("后天");
		weatherD4Text.setText(prefs.getString("weather_4", ""));
        weatherD5Text.setText(prefs.getString("weather_5", ""));
		weatherD6Text.setText(prefs.getString("weather_6", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
}
