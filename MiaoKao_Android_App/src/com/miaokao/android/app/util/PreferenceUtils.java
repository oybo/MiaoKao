package com.miaokao.android.app.util;

import com.miaokao.android.app.AppContext;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  shaPreference 工具类
 *  @author: ouyangbo
 *  @date:   2014-12-25
 *  @email:  ouyangbo@kingnode.com
 *  @remark: 
 *  @modify by:
 */
public class PreferenceUtils {

	/**
	 * 保存Preference的name
	 */
	public static String PREFERENCE_NAME = AppContext.getInstance().getPackageName();
	private static SharedPreferences mSharedPreferences;
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private PreferenceUtils() {
		mSharedPreferences = AppContext.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * 单例模式，获取instance实例
	 * 
	 * @return
	 */
	public static PreferenceUtils getInstance() {
		if (mPreferenceUtils == null) {
			mPreferenceUtils = new PreferenceUtils();
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}

	public void putString(String key, String value){
		editor.putString(key, value);
		editor.commit();
	}
	
	public void putBoolen(String key, boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putInt(String key, int value){
		editor.putInt(key, value);
		editor.commit();
	}
	
	public String getString(String key, String defValue){
		return mSharedPreferences.getString(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue){
		return mSharedPreferences.getBoolean(key, defValue);
	}

	public int getInt(String key, int defValue){
		return mSharedPreferences.getInt(key, defValue);
	}
	
	public void remove(String key) {
		if(mSharedPreferences.contains(key)) {
			editor.remove(key);
			editor.commit();
		}
	}
	
	public boolean isContains(String key) {
		return mSharedPreferences.contains(key);
	}
	
	public void clear() {
		editor.clear().commit();
	}

}
