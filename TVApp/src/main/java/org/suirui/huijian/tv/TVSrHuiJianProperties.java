package org.suirui.huijian.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.suirui.pub.business.manage.VpnEvent;
import com.suirui.srpaas.base.ConfProPertiesBean;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.OkHttpUtils;
import com.suirui.srpaas.common.http.okhttp.builder.PostFormBuilder;
import com.suirui.srpaas.common.http.okhttp.callback.GenericsCallback;
import com.suirui.srpaas.common.http.okhttp.callback.JsonGenericsSerializator;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.VideoProperties;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.ToolsUtil;
import com.suirui.srpaas.video.util.TvToolsUtil;

import org.suirui.huijian.tv.util.TVPreferenceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cui.li on 2017/6/19.
 */

public class TVSrHuiJianProperties {
    private static final String TAG = TVSrHuiJianProperties.class.getName();
    //    public static final String SERVER_ADRESS = "SERVER_ADRESS";
    public static final String appId = "appId";
    public static final String secretKey = "secretKey";
    private static final SRLog log = new SRLog(TAG, TVAppConfigure.LOG_LEVE);
    private static final String LOG_LEVE = "LOG_LEVE";
    private static final String isLoginUI = "isLoginUI";
    private static final String isShowRootUI = "isShowRootUI";
    private static final String VERSION_UPDATE = "VERSION_UPDATE";
    private static final String isAudioMeet = "isAudioMeet";
    private static final String isVpnModule = "isVpnModule";
    //    private static final String http_quest = "HTTP";
    private static final String propertiesPath = "/assets/srhuijian_properties.properties";
    private static final String localPropertiesPath = "/assets/srhuijian_local_properties.properties";//模块化配置(本地)
    private static boolean isSuccess = false;//是否获取到配置文件（解决链接入会异步的问题）
    private static Context mContext = null;
    private static SharedPreferences sp = null;
    private static final String LOG_STATE = "LOG_STATE";
//    private static String loadServerUrl="";
//    private static String loadServerType="";
//    private static String loaddoMain="";

    private static void getSRhuiJianProperties(Properties pro,OnLoadHJPropertiesListener onLoadHJPropertiesListener) {
        try {
            InputStream in = TVSrHuiJianProperties.class.getResourceAsStream(propertiesPath);
            pro.load(in);
            getProperties(pro,onLoadHJPropertiesListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 本地模块化配置
     */
    private static void getLocalSRHuiJianProperties() {
        try {
            Properties localProperties = new Properties();
            InputStream ins = TVSrHuiJianProperties.class.getResourceAsStream(localPropertiesPath);
            localProperties.load(ins);
            if (localProperties != null) {
                TVAppConfigure.isLoginUI = getBoolean(localProperties.getProperty(isLoginUI), true);
            }
            if (!TVAppConfigure.isLoginUI) {
                TVAppConfigure.pass_url_root = TVAppConfigure.pass_url_root_NoLogin;
            }
            log.E("org.suirui.TVSrHuiJianProperties...onResponse...TVAppConfigure.isLoginUI:" + TVAppConfigure.isLoginUI + "  :" + TVAppConfigure.pass_url_root );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static boolean getIsShowRootUI() {
        boolean flag = false;
        try {
            Properties localProperties = new Properties();
            InputStream ins = TVSrHuiJianProperties.class.getResourceAsStream(localPropertiesPath);
            localProperties.load(ins);
            if (localProperties != null) {
                flag  = getBoolean(localProperties.getProperty(isShowRootUI), false);
            }
            log.E("org.suirui.TVSrHuiJianProperties...TVAppConfigure.getIsShowRootUI:" + flag);
            return flag;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flag;
    }

    private static void getServerConf(String doMain, String urlPath, final Properties pro, final OnLoadHJPropertiesListener onLoadHJPropertiesListener) {
        try {
            String confServerAdress=doMain+urlPath;
            if (confServerAdress == null || confServerAdress.equals("")) {
                getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                return;
            }

            HashMap<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("clientversion", ToolsUtil.getCurrentVersionNum());
            paramsMap.put("clienttype", "android");
            paramsMap.put("clientmodel", "all");
            log.E("org.suirui.TVSrHuiJianProperties...post...getVerSion:" + ToolsUtil.getCurrentVersionNum());
            final String adressIpUrl = VideoProperties.getServer(doMain);
            ((PostFormBuilder) OkHttpUtils.post().url(confServerAdress)).params(paramsMap).build().execute(new GenericsCallback<ConfProPertiesBean>(new JsonGenericsSerializator()) {
                public void onError(Call call, Exception e, int i) {
                    getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                }

                @Override
                public void onResponse(ConfProPertiesBean jsonBean, int id) {
                    log.E("org.suirui.TVSrHuiJianProperties...onResponse...jsonBean:" + jsonBean);


                    try {
                        if (jsonBean == null || jsonBean.url == null || jsonBean.url.equals("")) {
                            getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                            return;
                        }
                        String downLoadUrl=VideoProperties.downLoadUrl(jsonBean.url,adressIpUrl);

                        log.E("downLoadUrl  TVSrHuiJianProperties: "+downLoadUrl);
                        Request request = new Request.Builder().url(downLoadUrl).build();
                        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //失敗讀取本地的
                                getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                                log.E("org.suirui.TVSrHuiJianProperties...onFailure...e:" + e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                try {
                                    InputStream in = response.body().byteStream();
                                    pro.load(in);
                                    String app_id = pro.getProperty(appId);
                                    log.E("org.suirui.TVSrHuiJianProperties...onResponse...app_id:" + app_id);
                                    if (app_id == null || app_id.equals("")) {
                                        getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                                    } else {
                                        getProperties(pro,onLoadHJPropertiesListener);
                                    }

                                } catch (Exception e) {
                                    getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                                    StringUtil.Exceptionex(TAG, "***getSRhuiJianProperties***Exception***", e);
                                    log.E("org.suirui.TVSrHuiJianProperties...Exception...e:" + e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        getSRhuiJianProperties(pro,onLoadHJPropertiesListener);
                        e.printStackTrace();
                        log.E("org.suirui.TVSrHuiJianProperties...Exception...e:" + e);
                    }

                }

            });
        } catch (Exception var17) {
            var17.printStackTrace();
        }


    }


    public static void loadProperties(String doMain, Context context,OnLoadHJPropertiesListener onLoadHJPropertiesListener) {
        mContext = context.getApplicationContext();
        getLocalSRHuiJianProperties();
        Properties properties = new Properties();
        if (StringUtil.isEmpty(doMain)) {
            getProperties(properties,onLoadHJPropertiesListener);
        } else {
            //配置服務器獲取配置文件
            getServerConf(doMain , Configure.confServerAdress, properties,onLoadHJPropertiesListener);
        }
    }

    private static void getProperties(Properties properties,OnLoadHJPropertiesListener onLoadHJPropertiesListener) {
        Log.e(TAG,"setupWebSocket...getProperties");
        if (properties != null) {
            //是否支持IM(会议中打开IM功能)
            TVAppConfigure.VERSION_UPDATE = getBoolean(properties.getProperty(VERSION_UPDATE), false);
            TVAppConfigure.appId = properties.getProperty(appId);
            TVAppConfigure.secretKey = properties.getProperty(secretKey);
            TVAppConfigure.LOG_LEVE = getInt(properties.getProperty(LOG_LEVE), 0);
            TVAppConfigure.isAudioMeet = getBoolean(properties.getProperty(isAudioMeet), false);
            TVAppConfigure.isVpnModule = getBoolean(properties.getProperty(isVpnModule), false);
            //设置控制台的log是否显示
            if (TVAppConfigure.LOG_LEVE == SRLog.DebugType.N) {
                SRLog.DebugType.setSRLog(false);
            } else {
                SRLog.DebugType.setSRLog(true);
            }
            if (mContext != null) {
                if (sp == null) {
                    sp = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
                }
                setConfigure(sp);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(ConfigureModelImpl.SPData.APP_ID, TVAppConfigure.appId);
                editor.putString(ConfigureModelImpl.SPData.SECRET_KEY, TVAppConfigure.secretKey);
                editor.commit();
            }

        }


        log.E("org.suirui.TVSrHuiJianProperties...loadServer_URL:" + TVAppConfigure.DO_MAIN + " appId: " + TVAppConfigure.appId + " secretKey: " + TVAppConfigure.secretKey + "  LOG_LEVE:" + TVAppConfigure.LOG_LEVE + "  HTTPREQUEST:" + TVAppConfigure.HTTPREQUEST+ " SERVER_ADRESS:" +TVAppConfigure.SERVER_ADRESS);

        VpnEvent.getInstance().updateVpnView(TVAppConfigure.isVpnModule);//解决初次安装设置服务器地址时，没有及时的更新vpn模块
//        getLocalSRHuiJianProperties();
        isSuccess = true;
        if(onLoadHJPropertiesListener!=null) {
            onLoadHJPropertiesListener.onLoadSuccess();
        }
    }
    //设置常量
    public static void setConfigure(SharedPreferences sp){
        if (sp == null) {
            if(mContext!=null) {
                sp = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
            }
        }
        if(sp!=null) {
            String server_url=sp.getString(ConfigureModelImpl.SPData.SERVER_ADRESS, "");
            String doMain= sp.getString(ConfigureModelImpl.SPData.DO_MAIN, "");
            log.E("setupWebSocket....getProperties:  "+doMain);
            String httpType= sp.getString(ConfigureModelImpl.SPData.REQUEST_HTTP_HTTPS, "");
            if(!StringUtil.isEmpty(server_url)) {
                TVAppConfigure.SERVER_ADRESS =server_url;
            }
            if(!StringUtil.isEmpty(doMain)) {
                TVAppConfigure.DO_MAIN =doMain;
            }
            if(!StringUtil.isEmpty(httpType)) {
                TVAppConfigure.HTTPREQUEST =httpType;
            }
            TVAppConfigure.RESFUL_URL = TVAppConfigure.DO_MAIN + TVAppConfigure.pass_url_root;
            log.E("setupWebSocket,..load..."+server_url+" doMain: "+doMain+" : "+TVAppConfigure.RESFUL_URL);
        }
    }

    public static void setNotDisturb(boolean isNotDisturb){
        TVPreferenceUtil.saveBooleanValue(TVPreferenceUtil.isNotDisturb,isNotDisturb);
    }

    public static boolean isNotDisturb(){
        return TVPreferenceUtil.readBooleanValue(TVPreferenceUtil.isNotDisturb,false);
    }
    public static void setAutoAnswer(boolean isAutoAnswer){
        TVPreferenceUtil.saveBooleanValue(TVPreferenceUtil.isAutoAnswer,isAutoAnswer);
    }
    public static boolean isAutoAnswer(){
        return TVPreferenceUtil.readBooleanValue(TVPreferenceUtil.isAutoAnswer,false);
    }

    private static boolean getBoolean(final String key, final boolean def) {
        try {
            if (key.equals("true")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return def;
        }
    }

    private static int getInt(final String key, final int def) {
        try {
            return Integer.parseInt(key);
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean getPropertState() {
        return isSuccess;
    }

    public abstract static class OnLoadHJPropertiesListener {
        public void onLoadSuccess() {}
    }


}
