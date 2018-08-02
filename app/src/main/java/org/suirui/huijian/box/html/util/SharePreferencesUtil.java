package org.suirui.huijian.box.html.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.html.entry.Members;

/**
 * Created by cui.li on 2017/8/1.
 */

public class SharePreferencesUtil {
    private static SharedPreferences sp = null;

    public static String getStringSharePre(Context mContext, String key) {
        if (mContext == null) return "";
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        String str = sp.getString(key, "");
        return str;
    }

    public static Boolean getBooleanSharePre(Context mContext, String key) {
        if (mContext == null) return false;
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        boolean str = sp.getBoolean(key, false);
        return str;
    }


    public static int getIntSharePre(Context mContext, String key) {
        if (mContext == null) return 0;
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        int value = sp.getInt(key, 0);
        return value;
    }

    public static void setStringSharePre(Context mContext, String key, String value) {
        if (mContext == null) return;
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    private static void setIntSharePre(Context mContext, String key, int value) {
        if (mContext == null) return;
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setBooleanSharePre(Context mContext, String key, boolean value) {
        if (mContext == null) return;
        if (sp == null)
            sp = mContext.getSharedPreferences(AppConfigure.SPData.SPDATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //设置点对点的用户
    public static void setMember(Context mContext, String memberId, String memberName, String memberType, boolean memberChecked) {
        setStringSharePre(mContext, AppConfigure.RegisterMethod.memberId, memberId);
        setStringSharePre(mContext, AppConfigure.RegisterMethod.memberName, memberName);
        setIntSharePre(mContext, AppConfigure.RegisterMethod.memberType, Integer.parseInt(memberType));
        setBooleanSharePre(mContext, AppConfigure.RegisterMethod.memberChecked, memberChecked);
    }

    //获取点对点用户
    public static String getMember(Context mContext) {
        String memberId = getStringSharePre(mContext, AppConfigure.RegisterMethod.memberId);
        String memberName = getStringSharePre(mContext, AppConfigure.RegisterMethod.memberName);
        int memberType = getIntSharePre(mContext, AppConfigure.RegisterMethod.memberType);
        boolean memberChecked = getBooleanSharePre(mContext, AppConfigure.RegisterMethod.memberChecked);
        Members members = new Members();
        members.setId(memberId);
        members.setName(memberName);
        members.setType(memberType);
        members.setChecked(memberChecked);
        return new Gson().toJson(members);
    }

    public static void setIsOneselfPhone(Context mContext, boolean isOneselfPhone) {
        setBooleanSharePre(mContext, AppConfigure.RegisterMethod.isUseOneselfPhone, isOneselfPhone);
    }

    public static boolean getIsOneselfPhone(Context mContext) {
        return getBooleanSharePre(mContext, AppConfigure.RegisterMethod.isUseOneselfPhone);
    }
}