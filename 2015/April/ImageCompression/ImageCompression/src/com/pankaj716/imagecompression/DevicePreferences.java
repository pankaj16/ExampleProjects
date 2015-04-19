package com.pankaj716.imagecompression;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;


public class DevicePreferences {


	public SharedPreferences get(Context context) {
		return context.getSharedPreferences("ImageCompression",
				Context.MODE_PRIVATE);
	}

	
	public void addKey(Context context, String key, String value) {
		SharedPreferences settings = get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}



	public void addKey(Context context, String key, int value) {
		SharedPreferences settings = get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void addKey(Context context, String key, Boolean value) {
		SharedPreferences settings = get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	
	public Boolean getBoolean(Context context, String key) {
		return getBoolean(context, key, false);
	}

	public Boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences prefs = get(context);
		return prefs.getBoolean(key, defValue);
	}


	public void removeKey(Context context, String key) {
		SharedPreferences settings = get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}

	public int getInt(Context context, String key, int defValue) {
		try{
		SharedPreferences prefs = get(context);
		return prefs.getInt(key, defValue);
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	
	public String getKey(Context context, String key) {
		SharedPreferences prefs = get(context);
		return prefs.getString(key, null);
	}

	
	public String getString(Context context, String key) {
		SharedPreferences prefs = get(context);
		return prefs.getString(key, null);
	}

	public String getString(Context context, String key, String defValue) {
		SharedPreferences prefs = get(context);
		return prefs.getString(key, defValue);
	}
	
	
}
