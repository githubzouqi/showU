package com.mushiny.www.showU.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * SharePreference 工具类
 */

public class SPUtil {

    private static final String IMXIAOQI = "SPUTIL_IMXIAOQI";
    private static SPUtil spUtil;
    private SharedPreferences sharedPreferences;
    private Context context;

    public SPUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(IMXIAOQI, Context.MODE_PRIVATE);
    }

    public static SPUtil newInstance(Context context){
        if (spUtil == null){
            synchronized (SPUtil.class){
                if (spUtil == null){
                    spUtil = new SPUtil(context.getApplicationContext());
                }
            }
        }
        return spUtil;
    }

    public void putInt(String key, int value){
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue)throws ClassCastException{
        int intValue = sharedPreferences.getInt(key, defValue);
        return intValue;
    }

    public void putString(String key, String value){
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue)throws ClassCastException{
        String strValue = sharedPreferences.getString(key, defValue);
        return strValue;
    }

    public void putBoolean(String key, Boolean value){
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, Boolean defValue)throws ClassCastException{
        boolean boolValue = sharedPreferences.getBoolean(key, defValue);
        return boolValue;
    }

    public void putFloat(String key, float value){
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float defValue)throws ClassCastException{
        float floatValue = sharedPreferences.getFloat(key, defValue);
        return floatValue;
    }

    public void putLong(String key, long value){
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defValue)throws ClassCastException{
        long longValue = sharedPreferences.getLong(key, defValue);
        return longValue;
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }
}
