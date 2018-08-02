package org.suirui.huijian.box;

import android.content.Context;
import android.content.SharedPreferences;

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

public class SrHuiJianProperties {
    private static final String TAG = SrHuiJianProperties.class.getName();
    //    public static final String SERVER_ADRESS = "SERVER_ADRESS";
    public static final String appId = "appId";
    public static final String secretKey = "secretKey";
    private static final SRLog log = new SRLog(TAG, AppConfigure.LOG_LEVE);
    private static final String LOG_LEVE = "LOG_LEVE";
    private static final String isLoginUI = "isLoginUI";
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

    private static void getSRhuiJianProperties(Properties pro) {
        try {
            InputStream in = SrHuiJianProperties.class.getResourceAsStream(propertiesPath);
            pro.load(in);
            getProperties(pro);
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
            InputStream ins = SrHuiJianProperties.class.getResourceAsStream(localPropertiesPath);
            localProperties.load(ins);
            if (localProperties != null) {
                AppConfigure.isLoginUI = getBoolean(localProperties.getProperty(isLoginUI), true);
            }
            if (!AppConfigure.isLoginUI) {
                AppConfigure.pass_url_root = AppConfigure.pass_url_root_NoLogin;
            }
            log.E("org.suirui.SrHuiJianProperties...onResponse...AppConfigure.isLoginUI:" + AppConfigure.isLoginUI + "  :" + AppConfigure.pass_url_root );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getServerConf(String doMain, String urlPath, final Properties pro) {
        try {
            String confServerAdress=doMain+urlPath;
            if (confServerAdress == null || confServerAdress.equals("")) {
                getSRhuiJianProperties(pro);
                return;
            }

            HashMap<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("clientversion", ToolsUtil.getCurrentVersionNum());
            paramsMap.put("clienttype", "android");
            paramsMap.put("clientmodel", "all");
            log.E("org.suirui.SrHuiJianProperties...post...getVerSion:" + ToolsUtil.getCurrentVersionNum());
            final String adressIpUrl = VideoProperties.getServer(doMain);
            ((PostFormBuilder) OkHttpUtils.post().url(confServerAdress)).params(paramsMap).build().execute(new GenericsCallback<ConfProPertiesBean>(new JsonGenericsSerializator()) {
                public void onError(Call call, Exception e, int i) {
                    getSRhuiJianProperties(pro);
                }

                @Override
                public void onResponse(ConfProPertiesBean jsonBean, int id) {
                    log.E("org.suirui.SrHuiJianProperties...onResponse...jsonBean:" + jsonBean);


                    try {
                        if (jsonBean == null || jsonBean.url == null || jsonBean.url.equals("")) {
                            getSRhuiJianProperties(pro);
                            return;
                        }
                        String downLoadUrl=VideoProperties.downLoadUrl(jsonBean.url,adressIpUrl);

                        log.E("downLoadUrl  SrHuiJianProperties: "+downLoadUrl);
                        Request request = new Request.Builder().url(downLoadUrl).build();
                        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //失敗讀取本地的
                                getSRhuiJianProperties(pro);
                                log.E("org.suirui.SrHuiJianProperties...onFailure...e:" + e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                try {
                                    InputStream in = response.body().byteStream();
                                    pro.load(in);
                                    String app_id = pro.getProperty(appId);
                                    log.E("org.suirui.SrHuiJianProperties...onResponse...app_id:" + app_id);
                                    if (app_id == null || app_id.equals("")) {
                                        getSRhuiJianProperties(pro);
                                    } else {
                                        getProperties(pro);
                                    }

                                } catch (Exception e) {
                                    getSRhuiJianProperties(pro);
                                    StringUtil.Exceptionex(TAG, "***getSRhuiJianProperties***Exception***", e);
                                    log.E("org.suirui.SrHuiJianProperties...Exception...e:" + e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        getSRhuiJianProperties(pro);
                        e.printStackTrace();
                        log.E("org.suirui.SrHuiJianProperties...Exception...e:" + e);
                    }

                }

            });
        } catch (Exception var17) {
            var17.printStackTrace();
        }


    }


    public static void loadProperties(String doMain, Context context) {
        mContext = context.getApplicationContext();
        getLocalSRHuiJianProperties();
        Properties properties = new Properties();
        if (StringUtil.isEmpty(doMain)) {
            getProperties(properties);
        } else {
            //配置服務器獲取配置文件
            getServerConf(doMain , Configure.confServerAdress, properties);
        }
    }

    private static void getProperties(Properties properties) {
        if (properties != null) {
            //是否支持IM(会议中打开IM功能)
            AppConfigure.VERSION_UPDATE = getBoolean(properties.getProperty(VERSION_UPDATE), false);
            AppConfigure.appId = properties.getProperty(appId);
            AppConfigure.secretKey = properties.getProperty(secretKey);
            AppConfigure.LOG_LEVE = getInt(properties.getProperty(LOG_LEVE), 0);
            AppConfigure.isAudioMeet = getBoolean(properties.getProperty(isAudioMeet), false);
            AppConfigure.isVpnModule = getBoolean(properties.getProperty(isVpnModule), false);
            log.E("org.suirui.SrHuiJianProperties...VERSION_UPDATE:" + AppConfigure.VERSION_UPDATE + " appId: " + AppConfigure.appId + " secretKey: " + AppConfigure.secretKey + "  LOG_LEVE:" + AppConfigure.LOG_LEVE + "  isAudioMeet:" + AppConfigure.isAudioMeet + " isVpnModule:" + AppConfigure.isVpnModule);
            //设置控制台的log是否显示
            if (AppConfigure.LOG_LEVE == SRLog.DebugType.N) {
                SRLog.DebugType.setSRLog(false);
            } else {
                SRLog.DebugType.setSRLog(true);
            }

            if (mContext != null) {
                if (sp == null) {
                    sp = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(ConfigureModelImpl.SPData.APP_ID, AppConfigure.appId);
                editor.putString(ConfigureModelImpl.SPData.SECRET_KEY, AppConfigure.secretKey);
                editor.commit();
            }
        }
        VpnEvent.getInstance().updateVpnView(AppConfigure.isVpnModule);//解决初次安装设置服务器地址时，没有及时的更新vpn模块
//        getLocalSRHuiJianProperties();
        isSuccess = true;
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
}
