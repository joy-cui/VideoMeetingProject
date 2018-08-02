package org.suirui.huijian.tv.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRErrorCode;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.CommonUtils;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.common.permission.AndPermission;
import com.suirui.srpaas.common.permission.Rationale;
import com.suirui.srpaas.common.permission.RationaleListener;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.VideoProperties;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.AcceptPushService;
import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.TVSrHuiJianProperties;
import org.suirui.login.huijian.ui.LoginActivity;
import org.suirui.srpaas.sdk.SRPaasSDK;
import org.suirui.srpaas.util.MD5;
import org.suirui.websocket.client.manager.WsClientManager;

/**
 * Created by hh on 2018/4/10.
 */

public class LoginUtil {
    private static final SRLog log = new SRLog(AppUtil.class.getName(), TVAppConfigure.LOG_LEVE);
    private static LoginUtil instance = null;
    private static String mMacAddr = "";

    public static synchronized LoginUtil getInstance() {
        if (instance == null) {
            instance = new LoginUtil();
        }

        return instance;
    }

    public void login(final Context context, final String account, final String pwd, final String serverHost, final String serverType){
        //保存服务地址
        setAddressServer(context, serverHost, serverType, new OnLoadPropertiesListener() {
            @Override
            public void onLoadSuccess() {
                log.E("login login  : "+TVAppConfigure.SERVER_ADRESS);

                CallNativeUtil.getInstance().login(context,account,pwd,serverHost,serverType);
            }
        });
    }

    public void register(Context context,String mServerAddress,String mNickName) {
        String cpuNo = "";
        String diskNo = "";
        String uuid = "";
        String nickName = mNickName;
        String macAddr = createMacUnique(context);

        if(TVAppConfigure.isTestAccountInit){
            if(StringUtil.isEmpty(mMacAddr)){
                mMacAddr = macAddr;
            }
        }else{
            filePermission(context);
            mMacAddr = WirteUnique.getInstance().readUnique();
            if(StringUtil.isEmpty(mMacAddr)){
                mMacAddr = createMacUnique(context);;
                WirteUnique.getInstance().wirteUnique(mMacAddr);
            }
//            mMacAddr =  macAddr;
        }

        SRIMLoginClient.getAddAuthService().register(TVAppConfigure.appId,mServerAddress + "/api/v2",mMacAddr.trim(),TVAppConfigure.USER_TYPE,mNickName);
    }

    public void loginAndChangeServer(final Context context, final String serverHost,final String serverType,final String mServerAddress) {
        log.E("loginAndChangeServer():");
        setAddressServer(context, serverHost, serverType, new OnLoadPropertiesListener() {
            @Override
            public void onLoadSuccess() {
//                log.E("login getServerAdress: "+ThirdApi.getIntance(context).getServerAdress());
//                IMiddleManage.getInstance().initServerAddr(context);
//                ThirdApi.getIntance(context).updateAdressUrl(serverHost, serverType, true);
                ThirdApi.getIntance(context).initServerAddr(context,mServerAddress+"/api/v2",TVAppConfigure.appId,TVAppConfigure.appId,getServerAddress(context),"");
                login(context);
            }
        });
//        IMiddleManage.getInstance().initServerAddr(context);
//        SRPaasSDK srPaasSDK = SRPaasSDK.getInstance();
//        srPaasSDK.setPassUrl(mServerAddress+"/api/v2");
//        SRIMLoginClient.getAddAuthService().reqLogin(TVAppConfigure.appId,mMacAddr,TVAppConfigure.USER_TYPE,null);

    }

    public void login( Context context) {
        String cpuNo = "";
        String diskNo = "";
        String uuid = "";
        if(TVAppConfigure.isTestAccountInit){
            if(StringUtil.isEmpty(mMacAddr)){
                String macAddr = NetworkUtil.getInstance(context).getMac();
                int radomNum = (int) (Math.random() * 1000);
                macAddr = macAddr + radomNum;
                mMacAddr = macAddr;
            }

        }else{
//            mMacAddr = NetworkUtil.getInstance(context).getMac();
//            WirteUnique.getInstance().wirteUnique(mMacAddr);
            filePermission(context);
            mMacAddr = WirteUnique.getInstance().readUnique();
            if(StringUtil.isEmpty(mMacAddr)){
                mMacAddr = createMacUnique(context);
                WirteUnique.getInstance().wirteUnique(mMacAddr);
            }
        }

        SRIMLoginClient.getAddAuthService().reqLogin(TVAppConfigure.appId,mMacAddr.trim(),TVAppConfigure.USER_TYPE,null);

    }

    private String  createMacUnique(Context context){
        return NetworkUtil.getInstance(context).getMac()+"-"+System.currentTimeMillis();
    }

    private void filePermission(final Context context){
        if (!AndPermission.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !AndPermission.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AndPermission.with((Activity) context)
                    .requestCode(TVAppConfigure.permission.WRITE_EXTERNAL_STORAGE)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_FORMAT_FILESYSTEMS)
                    .rationale(
                            new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                    AndPermission.rationaleDialog(context, rationale).show();
                                }
                            }
                    ).send();
        } else {
        }
    }

    public void updateUser(Context context,String nickName){
        SRIMLoginClient.getAddAuthService().updateUser(TVAppConfigure.appId,TVAppConfigure.USER_TYPE,getToken(context),nickName);
    }

    public void updateServer(final Context context, final String serverHost, final String serverType ){
        final String doMain = ThirdApi.getIntance(context).getSharePreferDoMain(TVAppConfigure.DO_MAIN);
        final String newDoMain = serverType + Configure.SPRITR + serverHost;
        log.E("updateServer...doMain:" + doMain + "  newDoMain:" + newDoMain);
        if (StringUtil.isEmpty(doMain) || !doMain.equals(newDoMain)) {
            ThirdApi.getIntance(context).updateAdressUrl(serverHost, serverType, true);
        }
    }

    private void setAddressServer(final Context context, final String serverHost, final String serverType , final OnLoadPropertiesListener  onLoadPropertiesListener){
        final String doMain = ThirdApi.getIntance(context).getSharePreferDoMain(TVAppConfigure.DO_MAIN);
        final String newDoMain = serverType + Configure.SPRITR + serverHost;
        log.E("SettingServerDialog.setupWebSocket..onComplete...doMain:" + doMain + "  newDoMain:" + newDoMain);
        if (StringUtil.isEmpty(doMain) || !doMain.equals(newDoMain)) {
            log.E("SettingServerDialog.setupWebSocket...doMain:" + doMain + "  newDoMain:" + newDoMain);
            //获取新服务器的配置文件
            ThirdApi.getIntance(context).updateAdressUrl(serverHost, serverType, true);
            TVSrHuiJianProperties.loadProperties(newDoMain, context, new TVSrHuiJianProperties.OnLoadHJPropertiesListener() {
                @Override
                public void onLoadSuccess() {
                    //加载会见的配置文件
//                    log.E("loadProperties...TV配置:" + doMain + "  newDoMain:" + newDoMain);
                    VideoProperties.loadProperties(newDoMain, new VideoProperties.OnLoadVideoPropertiesListener() {
                        @Override
                        public void onLoadSuccess() {
                            //保存新设置的服务器地址
                            log.E("loadProperties...video配置:" + doMain + "  newDoMain:" + newDoMain);
                            if(onLoadPropertiesListener!=null){
                                onLoadPropertiesListener.onLoadSuccess();
                            }
                        }
                    });//加载video配置文件
                }
            });


        }else{
            if(onLoadPropertiesListener!=null){
                onLoadPropertiesListener.onLoadSuccess();
            }
        }

    }

    public void setLoginStatus(Context context,int status){
        LoginSharePreferencesUtil.setHJLoginState(context,status);
    }

    public boolean isLogin(Context context) {
        int loginState = LoginSharePreferencesUtil.getHJLoginState(context);
        log.E("ConfigurePrestenerImpl...isLogin()..loginState:" + loginState);
        if (loginState == SRIMConfigure.State.hj_Login_success || loginState == SRIMConfigure.State.hj_tempLogin_success) {
            return true;
        }
        return false;
    }

    public String getToken(Context context) {
        return LoginSharePreferencesUtil.getLoginToken(context);
    }

    /**
     * 登录成功的昵称
     *
     * @return
     */
    public String getNickName(Context context) {
        String nickName = LoginSharePreferencesUtil.getLoginNikeName(context);
        return nickName;
    }

    public void setNickName(Context context,String nick) {
       LoginSharePreferencesUtil.setLoginNikeName(context,nick);

    }

    public static void setSuid(Context mContext, String suid) {
        LoginSharePreferencesUtil.setLoginSuid(mContext,suid);
        log.E("LoginSharePreferencesUtil....setSuid:" + suid + "    getLoginSuid:"+LoginSharePreferencesUtil.getLoginSuid(mContext));
    }

    /**
     * 获取suid
     *
     * @return
     */
    public String getSuid(Context context) {
        String suid = LoginSharePreferencesUtil.getLoginSuid(context);
        return suid;
    }

    public String getHttp(Context context) {
        return ThirdApi.getIntance(context).getServerHttp();
    }

    public String getHost(Context context) {
        return ThirdApi.getIntance(context).getServerAdress();
    }

    public String getServerAddress(Context context){
        return getHttp(context)+"://"+getHost(context);
    }

    public void ShowLoginFail(Context context,int eType){
        String msg = "";
        if (eType == SRErrorCode.LoginCode.LOGIN_TIME_OUT.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_time_out);

        } else if (eType == SRErrorCode.LoginCode.SERVER_ERROR.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_server_error);

        } else if (eType == SRErrorCode.LoginCode.invalid_TOKEN.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_invalid_token);

        } else if (eType == SRErrorCode.LoginCode.PWD_ERROR.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_pwd_error);

        } else if (eType == SRErrorCode.LoginCode.Account_freeze.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_account_freeze);

        } else if (eType == SRErrorCode.LoginCode.USER_Identification_is_null.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_identification_is_null);

        } else if (eType == SRErrorCode.LoginCode.USER_does_not_exist.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_user_not_exist);

        } else if (eType == SRErrorCode.LoginCode.SERVER_FIELD_ERROR.getCode()) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_server_field_error);

        }
        if (CommonUtils.isEmpty(msg)) {
            msg = context.getResources().getString(org.suirui.login.huijian.R.string.sr_login_fail);
        }
        log.E("WelcomeActivity...LOGIN_FAIL..."+eType+"  msg:" + msg);
        AppUtil.getInstance().showWaringDialog(context,msg);
//        CallJsUtil.getInstance().onLoginFail(webView,eType,msg);
    }
    //websocket连接
//    public  void initWebSocket(Context context,String webSocketUrl){
//        log.E("LoginActivity..setupWebSocket..........initWebSocket."+"  TVAppConfigure.SERVER_ADRESS: "+TVAppConfigure.SERVER_ADRESS);
//
//        WsClientManager.getInstance(context).initWebSocketClient(TVAppConfigure.SERVER_ADRESS);
//
//    }

    public void loginWebSocket(Context mContext){
      String pwd=  LoginSharePreferencesUtil.getPassword(mContext);
      String suid=LoginSharePreferencesUtil.getLoginSuid(mContext);
        String pwdMd5=MD5.getMD5Str(pwd);
        log.E("loginWebSocket.setupWebSocket...suid:"+suid+" pwd: "+pwd);
        JSONObject param = new JSONObject();
        param.put("userid", suid);
        param.put("pwd", pwdMd5);
        param.put("op", "login");
        param.put("clienttype", "ubox");
        WsClientManager.getInstance(mContext).sendText(param.toJSONString());
        //启动server
        Intent intent=new Intent(mContext, AcceptPushService.class);
        intent.setPackage(String.valueOf(AcceptPushService.class.getPackage()));
        mContext.startService(intent);




    }
    public abstract static class OnLoadPropertiesListener {
        public void onLoadSuccess() {}
    }
}
