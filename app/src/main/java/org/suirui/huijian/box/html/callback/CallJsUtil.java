package org.suirui.huijian.box.html.callback;

import android.content.Context;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.TokenUtil;

import org.json.JSONObject;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.html.entry.ServerInfo;
import org.suirui.huijian.box.html.interaction.ToJson;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.transfer.Config;
import org.suirui.transfer.MethodName;
import org.suirui.transfer.SRTransfer;

/**
 * Created by hh on 2018/3/26.
 */

public class CallJsUtil {
    private static SRLog log = new SRLog(CallJsUtil.class.getName(), Config.LOG_LEVE);
    private static CallJsUtil mCallJsUtil;

    public static synchronized CallJsUtil getInstance() {
        if (mCallJsUtil == null) {
            mCallJsUtil = new CallJsUtil();
        }
        return mCallJsUtil;
    }

    public void onLoginStatus(BridgeWebView mWebView,boolean isSuccess){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.onLoginStatus);
            obj.put("status",isSuccess);
            SRTransfer.getInstance().callJs(mWebView,obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginFail(BridgeWebView mWebView,int code,String msg){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.onLoginFail);
            obj.put("code",code);
            obj.put("msg", msg);
            SRTransfer.getInstance().callJs(mWebView,obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGetServerInfo( Context context,String data,CallBackFunction function){
        try {
            ServerInfo serverInfo = new ServerInfo();
            String token = TokenUtil.getIntance(context).getToken();
            String uid = SRMiddleManage.getInstance().getSuid();
            String doMain = ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN);
            String nikeName = SRMiddleManage.getInstance().getNickName();
            String phone = LoginSharePreferencesUtil.getPhone(context);
            String email = LoginSharePreferencesUtil.getEmail(context);
            String userpassword = LoginSharePreferencesUtil.getPassword(context);
            serverInfo.setServerInfo(AppConfigure.appId, AppConfigure.secretKey, token, uid, doMain, nikeName, phone, email, userpassword);
            String json= ToJson.getInstance().ToJsonObjectAddOP(AppConfigure.MethodParam.getServerInfo, new Gson().toJson(serverInfo), true);
            function.onCallBack(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void restart(Context context){
        try {
//            PowerManager pManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            pManager.reboot("reboot");
            Runtime.getRuntime().exec("su -c \"/system/bin/reboot\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void shutDown(){
        try {
//            Runtime.getRuntime().exec("su -c \"/system/bin/shutdown\"");
            Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"});  //关机
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//
//            //获得ServiceManager类
//            Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
//
//            //获得ServiceManager的getService方法
//            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
//
//            //调用getService获取RemoteService
//            Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
//
//            //获得IPowerManager.Stub类
//            Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
//            //获得asInterface方法
//            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
//            //调用asInterface方法获取IPowerManager对象
//            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
//            //获得shutdown()方法
//            Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
//            //调用shutdown()方法
//            shutdown.invoke(oIPowerManager,false,true);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onTestMicNotify(BridgeWebView mWebView,int volume) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", AppConfigure.MethodParam.testingMicNotify);
            obj.put("volume",volume);
            SRTransfer.getInstance().callJs(mWebView,obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
