package org.suirui.huijian.tv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class TVPreferenceUtil {

    public static final String isNotDisturb = "isNotDisturb";
    public static final String isAutoAnswer = "isAutoAnswer";
    public static final String serverAddressListHistory = "serverAddressListHistory";

    private static final String PREFERENCE_NAME = "share_preferences_TV_data";

    private static Context mContext;

    public TVPreferenceUtil() {
    }

    public static void initialize(Context context) {
        assert context != null;
        mContext = context;
    }

    public static void removeValue(String key) {
        SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = store.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void saveStringValue(String key, String value) {
        SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = store.edit();
        if (value == null) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }

        editor.commit();
    }

    public static String readStringValue(String key, String defValue) {
        if (key != null && key.length() != 0) {
            SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
            return store.getString(key, defValue);
        } else {
            return null;
        }
    }

    public static void saveBooleanValue(String key, boolean value) {
        SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = store.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean readBooleanValue(String key, boolean defValue) {
        if (key != null && key.length() != 0) {
            SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
            return store.getBoolean(key, defValue);
        } else {
            return defValue;
        }
    }

    public static void saveLongValue(String key, long value) {
        SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = store.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static Long readLongValue(String key, long defValue) {
        if (key != null && key.length() != 0) {
            SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
            return Long.valueOf(store.getLong(key, defValue));
        } else {
            return null;
        }
    }

    public static void saveIntValue(String key, int value) {
        SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = store.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static Integer readIntValue(String key, int defValue) {
        if (key != null && key.length() != 0) {
            SharedPreferences store = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
            return Integer.valueOf(store.getInt(key, defValue));
        } else {
            return null;
        }
    }

}
