package org.suirui.huijian.box.prestener.Impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.ThirdApiListener;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.TokenUtil;

import org.suirui.huijian.box.AppApplication;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.BuildConfig;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.prestener.IConfigurePrestener;
import org.suirui.huijian.box.view.IMainView;
import org.suirui.login.huijian.util.VpnSetUtil;

import static org.suirui.huijian.box.AppConfigure.CONNECT_JOIN_URL;

/**
 * 配置文件业务
 *
 * @authordingna
 * @date2017-06-16
 **/
public class ConfigurePrestenerImpl implements IConfigurePrestener, ThirdApiListener {
    private static final String TAG = ConfigurePrestenerImpl.class.getName();
    private static final SRLog log = new SRLog(TAG, AppConfigure.LOG_LEVE);
    private Context mContext;
    private SharedPreferences userPf = null;
    private IMainView mainView;
    private int TH_TYPE = AppConfigure.URLMeet.NO_TYPE; //1 start开始会议 2 加入会议  3 结束会议  4 连接入会
    private String MeetingNumber = "";
    private String pwd = "";
    private String appid = "";
    private String NickName = "";
    private String uid = "";
    private String Domain = "";
    private String secretKeyTh = "";
    private boolean isTaskRoot = false;
    private Handler handler = new Handler();
    private boolean isTempLogin = false;
    private String T = "";
    private String token = "";
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            log.E("ConfigurePrestenerImpl.....1秒到了..");
            doActivity();
        }
    };


    public ConfigurePrestenerImpl(Context context, IMainView view, boolean isTaskRoot) {
        this.mContext = context.getApplicationContext();
        this.mainView = view;
        this.isTaskRoot = isTaskRoot;
        //初始化ThirdApi
        ThirdApi.getIntance(mContext).cleanSharedPreference(mContext, BuildConfig.VERSION_CODE);
        ThirdApi.getIntance(mContext).addThirdApiListener(this);
    }

    @Override
    public void getMainIntent(Intent mainIntent) {
        if (mainIntent == null)
            return;
        String action = mainIntent.getAction();
        Uri uri = mainIntent.getData();
        log.E("ConfigurePrestenerImpl...uri:" + uri);
        if (uri == null) {
            handler.postDelayed(runnable, 1 * 1000);
            return;
        }
        String path = uri.getPath();
        log.E("ConfigurePrestenerImpl...path:" + path);
        if (path != null) {
            if (path.contains(AppConfigure.URLMeet.START)) {
                TH_TYPE = ConfigureModelImpl.JoinType.TH_START_TYPE;
                getUriParams(uri, TH_TYPE);
            } else if (path.contains(AppConfigure.URLMeet.JOIN)) {
                TH_TYPE = ConfigureModelImpl.JoinType.TH_JOIN_TYPE;
                getUriParams(uri, TH_TYPE);
            } else if (path.contains(AppConfigure.URLMeet.END)) {
                TH_TYPE = ConfigureModelImpl.JoinType.TH_END_TYPE;
                getUriParams(uri, TH_TYPE);
            } else if (path.contains(AppConfigure.URLMeet.JM)) {
                TH_TYPE = ConfigureModelImpl.JoinType.TH_JM_TYPE;
                getUriParams(uri, TH_TYPE);
            } else {
                TH_TYPE = AppConfigure.URLMeet.NO_TYPE;
            }
        }
        isUrlJoinMeet();
    }

    private void getUriParams(Uri uri, int type) {
        if (uri != null && type != AppConfigure.URLMeet.NO_TYPE) {
            if (type == ConfigureModelImpl.JoinType.TH_JM_TYPE) {
                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.ID);
                pwd = uri.getQueryParameter(AppConfigure.URLMeet.pwd);
                T = uri.getQueryParameter(AppConfigure.URLMeet.T);
                log.E("ConfigurePrestenerImpl..链接入会...ThirdApi...MeetingNumber:" + MeetingNumber + "  pwd:" + pwd + " T:" + T);
            } else if (type == ConfigureModelImpl.JoinType.TH_START_TYPE || type == ConfigureModelImpl.JoinType.TH_JOIN_TYPE) {
                //加入会议或者开始会议
                appid = uri.getQueryParameter(AppConfigure.URLMeet.appid);
                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.MeetingNumber);
                NickName = uri.getQueryParameter(AppConfigure.URLMeet.NickName);
                pwd = uri.getQueryParameter(AppConfigure.URLMeet.pwd);
                uid = uri.getQueryParameter(AppConfigure.URLMeet.uid);
                Domain = uri.getQueryParameter(AppConfigure.URLMeet.Domain);
                secretKeyTh = uri.getQueryParameter(AppConfigure.URLMeet.secretKey);
                token = uri.getQueryParameter(AppConfigure.URLMeet.token);
                log.E("ConfigurePrestenerImpl..加入会议或者开始会议.ThirdApi..appid:" + appid + "  MeetingNumber: " + MeetingNumber + "  NickName: " + NickName + " pwd:" + pwd + " uid:" + uid + " Domain:" + Domain + "  token:" + token);
            } else if (type == ConfigureModelImpl.JoinType.TH_END_TYPE) {
                //结束会议
                MeetingNumber = uri.getQueryParameter(AppConfigure.URLMeet.MeetingNumber);
            }
        }

    }

    //是否是连接入会
    private void isUrlJoinMeet() {
        if (!isTaskRoot) {
            log.E("ConfigurePrestenerImpl....isUrlJoinMeet()..isjoinMeet:" + isjoinMeet());
            if (!isjoinMeet()) {
                mainView.finishView();
                return;
            } else {
                //连接入会
                joinMeet();
//                mainView.finishView();
                return;
            }
        } else {
            joinMeet();
        }
    }

    @Override
    public boolean isjoinMeet() {
        if (TH_TYPE != AppConfigure.URLMeet.NO_TYPE) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUrlMeet() {
        if (TH_TYPE == ConfigureModelImpl.JoinType.TH_JM_TYPE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogin() {
        int loginState = LoginSharePreferencesUtil.getHJLoginState(mContext);
        log.E("ConfigurePrestenerImpl...isLogin()..loginState:" + loginState);
        if (loginState == SRIMConfigure.State.hj_Login_success || loginState == SRIMConfigure.State.hj_tempLogin_success) {
            return true;
        }
        return false;
    }

    @Override
    public void joinMeet() {
        log.E("ConfigurePrestenerImpl.....isJoinMeet:" + isJoinMeet());
        if (isJoinMeet()) {
            doActivity();
            //连接入会
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (TH_TYPE == ConfigureModelImpl.JoinType.TH_END_TYPE) {//结束会议
                        ThirdApi.getIntance(mContext).endUrlConf(mContext, MeetingNumber);
                    } else {
                        setDoMain();
                        setNickNameUid();
                        setAppId();
                        setSecretKey();
//                        if (isAudio()) {
//                            ThirdApi.getIntance(mContext).joinUrlAudioConf(mContext, AppConfigure.pass_url_root, appid, secretKeyTh, Domain, CONNECT_JOIN_URL, uid, NickName, MeetingNumber, pwd, ConfigureModelImpl.JoinType.LINK_JOIN_AUDIO_CONF);
//                        } else {
                            ThirdApi.getIntance(mContext).joinUrlConf(mContext, AppConfigure.pass_url_root, appid, secretKeyTh, Domain, CONNECT_JOIN_URL, uid, NickName, MeetingNumber, pwd, TH_TYPE);
//                        }
                    }
                }
            }, 10);

        } else {
            if (AppConfigure.isLoginUI) {
                if (!isLogin() && isjoinMeet()) {
                    setDoMain();
                    setAppId();
                    setSecretKey();
                    setNickNameUid();
                    joinUrlConf();
                }
            }
            handler.postDelayed(runnable, 1 * 1000);
        }
    }

    @Override
    public int joinType() {
        return this.TH_TYPE;
    }

    @Override
    public void clear() {
        if (handler != null)
            handler.removeCallbacks(runnable);
        mainView = null;
        mContext = null;
    }

    private boolean isJoinMeet() {
        if (AppConfigure.isLoginUI) {
            if (StringUtil.isEmpty(token)) {
                if (TH_TYPE != AppConfigure.URLMeet.NO_TYPE) {
                    if (isLogin()) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return isjoinMeet();
        }
    }

    private void setDoMain() {
        if (StringUtil.isEmpty(Domain)) {
            Domain = ThirdApi.getIntance(mContext).getSharePreferDoMain(AppConfigure.DO_MAIN);
            log.E("ConfigurePrestenerImpl...setDoMain.....Domain:" + Domain);
        }
    }

    private void setNickNameUid() {
        if (StringUtil.isEmpty(NickName)) {
            NickName = SRMiddleManage.getInstance().getNickName();
        }
        if (StringUtil.isEmpty(uid)) {
            uid = SRMiddleManage.getInstance().getSuid();
        }
    }

    private void setAppId() {
        if (StringUtil.isEmpty(appid)) {
            userPf = getSharePerference();
            if (userPf != null) {
                String shareAppId = userPf.getString(ConfigureModelImpl.SPData.APP_ID, "");
                if (!StringUtil.isEmpty(shareAppId)) {
                    appid = shareAppId;
                    return;
                }
            }
            appid = AppConfigure.appId;
        }
    }

    private SharedPreferences getSharePerference() {
        SharedPreferences sharedPreferences = null;
        if (mContext != null) {
            if (sharedPreferences == null) {
                sharedPreferences = mContext.getSharedPreferences(ConfigureModelImpl.SPData.SPDATA, Context.MODE_MULTI_PROCESS);
            }
        }
        return sharedPreferences;
    }

    private void setSecretKey() {
        if (StringUtil.isEmpty(secretKeyTh)) {
            userPf = getSharePerference();
            if (userPf != null) {
                String sharesecretKeyTh = userPf.getString(ConfigureModelImpl.SPData.SECRET_KEY, "");
                if (!StringUtil.isEmpty(sharesecretKeyTh)) {
                    secretKeyTh = sharesecretKeyTh;
                    return;
                }
            }
            secretKeyTh = AppConfigure.secretKey;
        }
    }

    /**
     * 是否是音频会议
     *
     * @return
     */
    private boolean isAudio() {
        if (StringUtil.isEmpty(T)) {//默认是视频会议
            return false;
        } else {
            try {
                int type = Integer.parseInt(T);
                if (type == 0) {//音频会议
                    return true;
                } else if (type == 1) {//视频会议
                    return false;
                } else if (type == 2) {//音视频会议
                    return false;
                } else {
                    return false;
                }
            } catch (NumberFormatException e) {
                StringUtil.Exceptionex(TAG, "***isAudio***NumberFormatException***", e);
            }
            return false;
        }
    }

    private void joinUrlConf() {
        Bundle b = new Bundle();
        b.putString(SRIMConfigure.SPData.PASS_URL_ROOT, AppConfigure.pass_url_root);
        b.putString(SRIMConfigure.SPData.APP_ID, appid);
        b.putString(SRIMConfigure.SPData.SECRET_KEY, secretKeyTh);
        b.putString(SRIMConfigure.SPData.DO_MAIN, Domain);
        b.putString(SRIMConfigure.SPData.INVITE_URL, CONNECT_JOIN_URL);
        b.putString(SRIMConfigure.SPData.UID, uid);
        b.putString(SRIMConfigure.SPData.NICKNAME, NickName);
        b.putString(SRIMConfigure.SPData.MEETING_NUMBER, MeetingNumber);
        b.putString(SRIMConfigure.SPData.PWD, pwd);
        log.E("ConfigurePrestenerImpl...joinUrlConf()...pass_url_root:" + AppConfigure.pass_url_root + " appid:" + appid + " Domain:" + Domain + " uid:" + uid + " NickName:" + NickName + " MeetingNumber:" + MeetingNumber + " pwd:" + pwd);
        if (isAudio()) {
            b.putInt(SRIMConfigure.SPData.TH_TYPE, ConfigureModelImpl.JoinType.LINK_JOIN_AUDIO_CONF);
        } else {
            b.putInt(SRIMConfigure.SPData.TH_TYPE, TH_TYPE);
        }
        SRMiddleManage.getInstance().onjoinUrlConf(b);
    }

    @Override
    public void onJoinError(int code, String errMsg) {
        mainView.onJoinError(code, errMsg);
    }

    private void doActivity() {
        if (AppConfigure.isVpnModule) {
            VpnSetUtil.getInstance(mContext, AppApplication.getInstance()).vpnLogin();
        }

        if (!StringUtil.isEmpty(token)) {//有token时，表示第三方app去调用我们的会见入会
            TokenUtil.getIntance(mContext).setThirdToken(token);
            int meetState = SRMiddleManage.getInstance().getMeetingState();
            if (meetState == ConfigureModelImpl.MeetState.MEETING_ING) {
                mainView.doActivity(AppConfigure.UI.MEET_AT, null);
            } else {
                int loginState = LoginSharePreferencesUtil.getHJLoginState(mContext);
                if (loginState == SRIMConfigure.State.hj_Login_success || loginState == SRIMConfigure.State.hj_tempLogin_success) {
                    mainView.doActivity(AppConfigure.UI.MAIN_AT, null);
                } else {
                    doLoginActivity(AppConfigure.URLMeet.NO_TYPE);
                }
            }

        } else {//没有token时，默认走会见的流程
            TokenUtil.getIntance(mContext).setThirdToken(token);
            if (AppConfigure.isLoginUI) {//有登录界面
                LoginSharePreferencesUtil.setTempNikeName(mContext, "");
                LoginSharePreferencesUtil.setTempSuid(mContext, "");
                if (isLogin()) {//已经登录了
                    doLogin();
                    int meetState = SRMiddleManage.getInstance().getMeetingState();//by test20170705,会议中后台，链接入会异常问题
                    if (meetState == ConfigureModelImpl.MeetState.MEETING_ING) {
                        mainView.doActivity(AppConfigure.UI.MEET_AT, null);
                    } else {
                        mainView.doActivity(AppConfigure.UI.MAIN_AT, null);
                    }
                } else {
                    doLoginActivity(TH_TYPE);
                }
            } else {//没有登录界面
                LoginSharePreferencesUtil.setHJLoginState(mContext, SRIMConfigure.State.s_no_Login);
                LoginSharePreferencesUtil.setLoginNikeName(mContext, "");
                LoginSharePreferencesUtil.setLoginSuid(mContext, "");
                SRMiddleManage.getInstance().getTempNickNameOrUid();
                TokenUtil.getIntance(mContext).setToken("");
                int meetState = SRMiddleManage.getInstance().getMeetingState();//by test20170705,会议中后台，链接入会异常问题
                if (meetState == ConfigureModelImpl.MeetState.MEETING_ING) {
                    mainView.doActivity(AppConfigure.UI.MEET_AT, null);
                } else {
                    mainView.doActivity(AppConfigure.UI.MAIN_AT, null);
                }
            }
        }
//        mainView.finishView();
    }

    //跳转到登录界面
    private void doLoginActivity(int joinType) {
        if (AppConfigure.isLoginUI) {
//            setLoginType();//设置登录状态
            setTempLogin();//设置是否临时号登录
            Bundle b = new Bundle();
//            b.putInt(SRIMConfigure.SRIMData.LOGIN_TYPE, loginType);
            b.putBoolean(SRIMConfigure.SRIMData.TEMP_LOGIN, isTempLogin);
            b.putInt(SRIMConfigure.SRIMData.TH_TYPE, joinType);
            b.putBoolean(AppConfigure.VPN_MODULE, AppConfigure.isVpnModule);
            mainView.doActivity(AppConfigure.UI.LOGIN_AT, b);
        }
    }

    private void doLogin() {
        IMiddleManage.getInstance().initServerAddr(AppApplication.getInstance());
        String phone = LoginSharePreferencesUtil.getLoginAccount(mContext);
        String pwd = LoginSharePreferencesUtil.getPassword(mContext);
        String appid = LoginSharePreferencesUtil.getAppId(mContext);
        boolean isTemp = LoginSharePreferencesUtil.getLoginTemp(mContext);
        log.E("ConfigurePrestenerImpl..自动登录..phone：" + phone + " pwd:" + pwd + "  appid:" + appid + " isTemp:" + isTemp);
        SRIMLoginClient.getAddAuthService().reqLogin(phone, pwd, appid, null, SRIMConfigure.Login.HUIJIAN, isTemp,"");
    }

    private void setTempLogin() {
        this.isTempLogin = AppConfigure.isTempLogin;
    }

}
