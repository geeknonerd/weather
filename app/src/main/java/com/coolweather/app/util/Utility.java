package com.coolweather.app.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	
	public synchronized static boolean handleProincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces!=null&&allProvinces.length>0) {
				for(String p:allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities!=null&&allCities.length>0) {
				for(String c:allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties!=null&&allCounties.length>0) {
				for(String c:allCounties){
					String[] array = c.split("\\|"); 
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray weatherInfo = jsonObject.getJSONArray("result");
			String cityName = weatherInfo.getJSONObject(0).getString("citynm");
			String weatherCode = weatherInfo.getJSONObject(0).getString("cityid");
			String weatherIcon = weatherInfo.getJSONObject(0).getString("weather_icon");
			String temp1 = weatherInfo.getJSONObject(0).getString("temperature");
			String temp2 = weatherInfo.getJSONObject(1).getString("temperature");
			String temp3 = weatherInfo.getJSONObject(2).getString("temperature");
			String temp4 = weatherInfo.getJSONObject(3).getString("temperature");
			String temp5 = weatherInfo.getJSONObject(4).getString("temperature");
			String temp6 = weatherInfo.getJSONObject(5).getString("temperature");
			String weatherDesp = weatherInfo.getJSONObject(0).getString("weather");
			String weatherDesp2 = weatherInfo.getJSONObject(1).getString("weather");
			String weatherDesp3 = weatherInfo.getJSONObject(2).getString("weather");
			String weatherDesp4 = weatherInfo.getJSONObject(3).getString("weather");
			String weatherDesp5 = weatherInfo.getJSONObject(4).getString("weather");
			String weatherDesp6 = weatherInfo.getJSONObject(5).getString("weather");
			String publishTime = weatherInfo.getJSONObject(0).getString("days");
			saveWeatherInfo(context,cityName,weatherCode,weatherIcon,temp1,temp2,temp3,temp4,temp5,temp6,weatherDesp,weatherDesp2,weatherDesp3,weatherDesp4,weatherDesp5,weatherDesp6,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private static void saveWeatherInfo(Context context, String cityName, String weatherCode,String weatherIcon, String temp1,
			String temp2,String temp3,String temp4,String temp5,String temp6,String weatherDesp,String weatherDesp2,String weatherDesp3,String weatherDesp4,String weatherDesp5,String weatherDesp6, String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("M/d",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("weather_icon", weatherIcon);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("temp3", temp3);
		editor.putString("temp4", temp4);
		editor.putString("temp5", temp5);
		editor.putString("temp6", temp6);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("weather_desp2", weatherDesp2);
		editor.putString("weather_desp3", weatherDesp3);
		editor.putString("weather_desp4", weatherDesp4);
		editor.putString("weather_desp5", weatherDesp5);
		editor.putString("weather_desp6", weatherDesp6);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.putString("weather_2", sdf.format(nowDate(1)));
		editor.putString("weather_3", sdf.format(nowDate(2)));
		editor.putString("weather_4", sdf.format(nowDate(3)));
		editor.putString("weather_5", sdf.format(nowDate(4)));
		editor.putString("weather_6", sdf.format(nowDate(5)));
		editor.commit();
	}

	private static Date nowDate(int day) {
		Date date = new Date();
		long time = date.getTime()+24*60*60*1000*day;
		date = new Date(time);
		return date;
	}
	
	
	
	
}
