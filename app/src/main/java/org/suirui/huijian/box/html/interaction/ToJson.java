package org.suirui.huijian.box.html.interaction;

import com.google.gson.Gson;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.html.entry.BaseJson;
import org.suirui.huijian.box.html.util.ToolsUtil;

/**
 * Created by cui.li on 2017/8/3.
 * native与js交互接口与参数
 */

public class ToJson {
    private static ToJson instance;
    private static final String TAG = ToJson.class.getName();
    private SRLog log = new SRLog(TAG, AppConfigure.LOG_LEVE);
    public ToJson() {
    }

    public static ToJson getInstance() {
        if (instance == null) {
            synchronized (ToJson.class) {
                if (instance == null) {
                    instance = new ToJson();
                }
            }
        }
        return instance;
    }

    public String NativeJsBaseJson(int status, String opType) {
        try {
            BaseJson baseJson = new BaseJson();
            baseJson.status = status;
            baseJson.op = opType;
            return new Gson().toJson(baseJson);
        } catch (Exception e) {
            StringUtil.Exceptionex(TAG, "***NativeJsBaseJson***Exception***", e);
        }
        return "";
    }

    /**
     * op协议
     *
     * @param value
     * @return
     */
    public String ToJsonOP(String value) {
        String str = "";
        if (!ToolsUtil.isEmpty(value)) {
            str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + value + "\"}";
        }
        log.E("ToJsonKeyValue:" + str);
        return str;
    }
    /**
     * 一个参数的json字符串
     *
     * @param value1
     * @param key2
     * @param value2
     * @return
     */
    public String ToJsonKeyValue(String value1, String key2, String value2) {
        String str = "";
        if (!ToolsUtil.isEmpty(value1) && !ToolsUtil.isEmpty(key2) && !ToolsUtil.isEmpty(value2)) {
            str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + value1 + "\",\"" + key2 + "\":\"" + value2 + "\"}";
        }
        log.E("ToJsonKeyValue:" + str);
        return str;

    }

    /**
     * 带一个int类型装json字符串
     *
     * @param value1
     * @param key2
     * @param value2
     * @return
     */
    public String ToJsonIntKeyValue(String value1, String key2, int value2) {
        String str = "";
        if (!ToolsUtil.isEmpty(value1) && !ToolsUtil.isEmpty(key2)) {
            str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + value1 + "\",\"" + key2 + "\":" + value2 + "}";
        }
        log.E("ToJsonIntKeyValue:" + str);
        return str;

    }

    public <T> T fromJsonObject(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }


    /**
     * String中加消息类型op
     *
     * @return
     */
    public String ToJsonObjectAddOP(String op, String json, boolean isCode) {
        String str = "";
        if (!ToolsUtil.isEmpty(json)) {
            String _str = json.substring(1, json.length() - 1);//去掉json前后的大括号
            if (isCode) {
                str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + op + "\",\"" + AppConfigure.MethodParam.Code + "\":\"" + "200" + "\"," + _str + "}";
            } else {
                str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + op + "\"," + _str + "}";
            }
        }
        log.E("ToJsonAddOP..." + str);
        return str;
    }

    /**
     * String类型的json加消息类型op
     *
     * @param op
     * @param listJson
     * @return
     */
    public String ToJsonListAddOP(String op, String listJson, boolean isCode) {
        String str = "";
        if (!ToolsUtil.isEmpty(listJson)) {
            if (isCode) {
                str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + op + "\",\"" + AppConfigure.MethodParam.Code + "\":\"" + "200" + "\",\"" + AppConfigure.MethodParam.data + "\":" + listJson + "}";
            } else {
                str = "{\"" + AppConfigure.MethodParam.OP + "\":\"" + op + "\",\"" + AppConfigure.MethodParam.data + "\":" + listJson + "}";
            }
        }
        log.E("ToJsonListAddOP..." + str);
        return str;
    }
}
