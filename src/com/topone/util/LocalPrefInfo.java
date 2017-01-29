package com.topone.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalPrefInfo {

	
	public final static String BT_ADDRESS="bt_address";
	//final static String SEARCH_END=112;
	//final static String START_BOUND=113;
	//final static String RUNNING_BOUND=114;
	
    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "local_pref";
    private static SharedPreferences mSharedPreferences;
    private static LocalPrefInfo mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private LocalPrefInfo( Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,  Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     * 
     * @param cxt
     * @return
     */
    public static LocalPrefInfo getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new LocalPrefInfo(cxt);
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    //
    public void setPrefInfo(String str_name, String str_value) {

        editor.putString(str_name, str_value);
        editor.commit();
        
    }

    public String getPrefInfo(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }

    public void clearUserInfo()
    {
    	editor.clear();
    	editor.commit();
    }
    
   }
