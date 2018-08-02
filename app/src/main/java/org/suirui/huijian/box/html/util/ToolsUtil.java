package org.suirui.huijian.box.html.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.suirui.srpaas.base.util.log.SRLog;

import org.json.JSONObject;
import org.suirui.huijian.box.AppConfigure;

import java.lang.reflect.Type;

/**
 * 工具类
 *
 * @authordingna
 * @date2017-08-02
 **/
public class ToolsUtil {
    private static SRLog log = new SRLog(ToolsUtil.class.toString(), AppConfigure.LOG_LEVE);

    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isEqualStr(String str1, String str2) {
        if (str1 != null && str2 != null && str1.equals(str2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * int 转string
     *
     * @param data
     * @return
     */
    public static String toString(int data) {
        try {
            return String.valueOf(data);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * Object转json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null)
            return "";
        return new Gson().toJson(object);
    }

    /**
     * json转List
     *
     * @param json
     * @param object (new TypeToken<List<Object>>(){}.getType();)
     * @return
     */
    public static Object toList(String json, Type object) {
        if (TextUtils.isEmpty(json))
            return null;
        if (object == null)
            return null;
        return new Gson().fromJson(json, object);
    }

    /**
     * json字符串获取某个string对象
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJsonValue(String json, String key) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(json);
            JsonObject element = root.getAsJsonObject();
            JsonPrimitive nameJson = element.getAsJsonPrimitive(key);
            return nameJson.getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * json字符串获取某个int对象
     *
     * @param json
     * @param key
     * @return
     */
    public static int getJsonIntValue(String json, String key) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(json);
            JsonObject element = root.getAsJsonObject();
            JsonPrimitive nameJson = element.getAsJsonPrimitive(key);
            return nameJson.getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * json字符串获取某个boolean对象
     *
     * @param json
     * @param key
     * @return
     */
    public static boolean getJsonBooleanValue(String json, String key) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(json);
            JsonObject element = root.getAsJsonObject();
            JsonPrimitive nameJson = element.getAsJsonPrimitive(key);
            return nameJson.getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * int 转 boolean
     *
     * @param key
     * @return
     */
    public static boolean IntToBoolean(int key) {
        return (key == 1) ? true : false;
    }

    /**
     * boolean 转 int
     *
     * @param value
     * @return
     */
    public static int BooleanToInt(boolean value) {
        return (value) ? 1 : 0;
    }

    /**
     * Sting 转 boolean
     *
     * @param str
     * @return
     */
    public static boolean StrToBoolean(String str) {
        if (!isEmpty(str)) {
            int value = Integer.parseInt(str);
            return IntToBoolean(value);
        } else {
            return false;
        }
    }

    /**
     * json字符串获取某个data对象中的某个boolean值
     *
     * @param json
     * @param key
     * @return
     */
    public static boolean getJsonBooleanData(String json, String data, String key) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // 获得 data 节点的值，data 节点为Object数据节点
            JsonObject dataJson = jsonObject.getAsJsonObject(data);
            JsonPrimitive namePrimitive = dataJson.getAsJsonPrimitive(key);
            return namePrimitive.getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * json字符串获取某个data对象中的某个值
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJsonData(String json, String data, String key) {
        try {
            JSONObject array = new JSONObject(json.toString()).getJSONObject(data);
            return array.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
