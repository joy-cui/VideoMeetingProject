package org.suirui.huijian.box.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.event.DataEvent;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.server.ConfServer;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.VideoProperties;
import com.suirui.srpaas.video.ui.activity.SRVideoActivity;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.TokenUtil;
import com.suirui.srpaas.video.yueyun.im.ImMeetManage;

import org.suirui.huijian.box.AppApplication;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.MainActivity;
import org.suirui.huijian.box.R;
import org.suirui.huijian.box.SrHuiJianProperties;
import org.suirui.huijian.box.dialog.SettingServerDialog;
import org.suirui.huijian.box.util.HistoryUtil;
import org.suirui.huijian.box.util.VersionUpdateUtil;
import org.suirui.huijian.box.html.HomeActivity;
import org.suirui.immedia.manage.util.JsonUtil;
import org.suirui.srpaas.entry.MeetingInfo;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static com.suirui.srpaas.video.third.ThirdApi.getIntance;

/**
 * @authordingna
 * @date2017-06-08
 **/
public class SRMiddleManage implements Observer {
    private static final SRLog log = new SRLog(SRMiddleManage.class.getName(), AppConfigure.LOG_LEVE);
    private static SRMiddleManage instance;
    private Context mContext;
    private String pass_url_root = "";
    private String appid = "";
    private String secretKey = "";
    private String domain = "";
    private String invite_url = "";
    private String uid = "";
    private String nikename = "";
    private String meeting_number = "";
    private String pwd = "";
    private int th_type = AppConfigure.URLMeet.NO_TYPE;
    private String Domain = "";
    private String secretKeyTh = "";
    private int meetingState = 0;
    JsonUtil jsonUtil = JsonUtil.getInstance();
    public SRMiddleManage() {
        IMiddleManage.getInstance().addObserver(this);
        DataEvent.getInstance().addObserver(this);
    }

    public synchronized static SRMiddleManage getInstance() {
        if (instance == null) {
            instance = new SRMiddleManage();
        }
        return instance;
    }

    public void init(Context context,boolean isIMLogin) {
        this.mContext = context;
        SRIMConfigure.isIMLogin=isIMLogin;
        //初始化登录模块
//        int loginType = LoginSharePreferencesUtil.getLoginType(context);
//        switch (loginType) {
//            case SRIMConfigure.Login.HUIJIAN://会见登录
//                log.E("SRMiddleManage...会见登录");
                SRIMLoginClient.init(mContext);
//                break;
//            case SRIMConfigure.Login.YUEYUN://阅云登录
//                log.E("SRMiddleManage...阅云登录");
//                YueyunClient.init(mContext.getApplicationContext());
//                break;
//        }
//        if(isIMLogin){//IM初始化
//            SRIMManage.getInstance().initIM(mContext);
//        }
    }



    //注销
    public void onLoginOut() {
        LoginSharePreferencesUtil.setHJLoginState(mContext, SRIMConfigure.State.s_no_Login);
        log.E("SRMiddleManage...退出登录状态");
        clearJoinUrlConf();
//        if(SRIMConfigure.isIMLogin){
//            log.E("IM退出");
//            SDKCoreHelper.logout();
//        }
    }

    public void clear() {
        SRIMLoginClient.getRemoveAuthService();
        clearJoinUrlConf();
        IMiddleManage.getInstance().deleteObserver(this);
        DataEvent.getInstance().deleteObserver(this);
    }

    public void onjoinUrlConf(Bundle b) {
        if (b == null)
            return;
        pass_url_root = b.getString(SRIMConfigure.SPData.PASS_URL_ROOT, "");
        appid = b.getString(SRIMConfigure.SPData.APP_ID, "");
        secretKey = b.getString(SRIMConfigure.SPData.SECRET_KEY, "");
        domain = b.getString(SRIMConfigure.SPData.DO_MAIN, "");
        invite_url = b.getString(SRIMConfigure.SPData.INVITE_URL, "");
        uid = b.getString(SRIMConfigure.SPData.UID, "");
        nikename = b.getString(SRIMConfigure.SPData.NICKNAME, "");
        meeting_number = b.getString(SRIMConfigure.SPData.MEETING_NUMBER, "");
        pwd = b.getString(SRIMConfigure.SPData.PWD, "");
        th_type = b.getInt(SRIMConfigure.SPData.TH_TYPE, 0);
        if (StringUtil.isEmpty(nikename)) {
            String loginNickName = getNickName();
            if (!StringUtil.isEmpty(loginNickName) && !loginNickName.equals(nikename)) {
                nikename = loginNickName;
            }
        }
        log.E("SRMiddleManage...登录后，链接入会....th_type:" + th_type + "  domain:" + domain + "  meeting_number:" + meeting_number + "  nikename:" + nikename);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IMiddleManage) {
            if (data instanceof IMiddleManage.NotifyCmd) {
                final IMiddleManage.NotifyCmd cmd = (IMiddleManage.NotifyCmd) data;
                switch (cmd.type) {
                    case SET_LOGIN_TOKEN:
                        setLoginToken((String) cmd.data);
                        break;

                    case LOGIN_SUCCESS://登录成功后跳转到首页
//                        Activity activity = (Activity) cmd.data;//by test20171218,Activity为了解决小米5手机登录成功后finish时，Activity界面变形问题
//                        if (activity != null) {
//                            Intent intent = null;
//                            if (!AppConfigure.isHtml) {
//                                intent = new Intent(activity, MainActivity.class);
//                            } else {
//                                intent = new Intent(activity, HomeActivity.class);
//                            }
//                            activity.startActivity(intent);
//                            activity.finish();
//                        }
                        break;
                    case SET_SERVER_ADDR://设置服务器地址
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                final Context context = (Context) cmd.data;
                                final SettingServerDialog settingServerDialog = new SettingServerDialog(context, R.style.sr_custom_dialog);
                                settingServerDialog.show();
                                log.E("SettingServerDialog......show()...");
                                settingServerDialog.setClicklistener(new SettingServerDialog.ClickListenerInterface() {
                                    @Override
                                    public void onComplete(String addressName, String httpEquest, boolean isDefalut) {
//                                        String doMain = ThirdApi.getIntance(context).getDoMain();
                                        String doMain = ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN);
                                        String newDoMain = httpEquest + Configure.SPRITR + addressName;
                                        log.E("SettingServerDialog...onComplete...doMain:" + doMain + "  newDoMain:" + newDoMain);
                                        if (!StringUtil.isEmpty(doMain) && !StringUtil.isEmpty(newDoMain) && !doMain.equals(newDoMain)) {
                                            //获取新服务器的配置文件
                                            SrHuiJianProperties.loadProperties(newDoMain, context);//加载会见的配置文件
                                            VideoProperties.loadProperties(newDoMain);//加载video配置文件
                                            //保存新设置的服务器地址
                                            ThirdApi.getIntance(context).updateAdressUrl(addressName, httpEquest, isDefalut);
                                            //检查版本更新
                                            if (AppConfigure.VERSION_UPDATE)
                                                VersionUpdateUtil.getInstance().doAppUpdate(context, false);
                                        }
                                        settingServerDialog.dismiss();
                                    }
                                });
                            }
                        });
                        break;

                    case JOIN_URL_MEETING:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Context context = (Context) cmd.data;
                                //如果有登陆的则需要登陆成功后的uid入会
                                uid = getSuid();
                                log.E("SRMiddleManage..开始连接入会..pass_url_root:" + pass_url_root + " appid:" + appid + " domain:" + domain + " invite_url:" + invite_url
                                        + " uid:" + uid + "  nikename:" + nikename + " meeting_number:" + meeting_number);
                                getIntance(context).joinUrlConf(context, pass_url_root, appid, secretKey, domain, invite_url, uid, nikename, meeting_number, pwd, th_type);
                            }
                        }, 10);
                        break;

                    case CLEAR_JOIN_URL_CONF:
                        clearJoinUrlConf();
                        break;

                    case INIT_SERVER_ADDR:
                        log.E("SRMiddleManage...INIT_SERVER_ADDR.." + AppConfigure.appId + " secretKey:" + AppConfigure.secretKey + "  DO_MAIN：" + AppConfigure.DO_MAIN);
                        Context context = (Context) cmd.data;
                        getIntance(context).initServerAddr(context, AppConfigure.login_pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN), AppConfigure.CONNECT_JOIN_URL);
                        LoginSharePreferencesUtil.setAppId(context, AppConfigure.appId);
                        break;
                    case ON_LOGIN_BACK:
                        AppApplication.getInstance().release(false);
                        break;
                }
            }

        } else if (observable instanceof DataEvent) {
            if (data instanceof DataEvent.NotifyCmd) {
                final DataEvent.NotifyCmd cmd = (DataEvent.NotifyCmd) data;
                switch (cmd.type) {
                    case ADD_HISTORY_MEET:
//                        HistoryUtil.getInstance(mContext).saveHistoryMeet((MeetingInfo) cmd.data);
                        HistoryUtil.getInstance(mContext).saveHistoryMeetBySuid((MeetingInfo) cmd.data, getSuid());
                        break;
                    case MEETING_STATE:
                        this.meetingState = (int) cmd.data;
                        break;
                    case TEMP_MEET_CONFID:
//                        HistoryUtil.getInstance(mContext).saveTempJoinConfid((String) cmd.data);
                        HistoryUtil.getInstance(mContext).saveTempJoinConfid((String) cmd.data, getSuid());
                        break;
                    case UPDATE_HISTORY_LIST:
                        IMiddleManage.getInstance().updateHistoryList();
                        break;
                    case CLEAR_TERM_JOIN_CONFID:
                        HistoryUtil.getInstance(mContext).saveTempJoinConfid("", "");
                        break;
                    case UPDATE_CURRENT_MEETINFO:
                        IMiddleManage.getInstance().updateMeetInfo((String) cmd.data);
                        break;
                }
            }
        }

    }


    /**
     * 登录成功设置token
     */
    private void setLoginToken(String token) {
        //初始化，IM邀请入会用的
        ImMeetManage.getInstance().init(mContext, AppConfigure.pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(mContext).getSharePreferDoMain(AppConfigure.DO_MAIN), AppConfigure.CONNECT_JOIN_URL);
        log.E("SRMiddleManage..登录成功.有链接入会..nikename:" + nikename + "  uid:" + uid + " token:" + token);
        if (!StringUtil.isEmpty(token)) {
            TokenUtil.getIntance(mContext).setToken(token);
        }
        //给链接入会复制昵称
        if (StringUtil.isEmpty(nikename)) {
            nikename = getNickName();
        }
        if (StringUtil.isEmpty(uid)) {
            uid = getSuid();
        }
        log.E("SRMiddleManage..登录成功...nickName:" + nikename + "  uid:" + uid);
    }

    //清除连接入会的信息
    private void clearJoinUrlConf() {
        pass_url_root = "";
        appid = "";
        secretKey = "";
        domain = "";
        invite_url = "";
        uid = "";
        nikename = "";
        meeting_number = "";
        pwd = "";
        th_type = AppConfigure.URLMeet.NO_TYPE;
        log.E("SRMiddleManage...清除连接入会的信息");
    }

    /**
     * 登录成功的昵称
     *
     * @return
     */
    public String getNickName() {
        String nickName = LoginSharePreferencesUtil.getLoginNikeName(mContext);
        if (StringUtil.isEmpty(nickName)) {
            nickName = "sr" + (int) (Math.random() * 10);
            LoginSharePreferencesUtil.setLoginNikeName(mContext, nickName);
            log.E("SRMiddleManage..自动生成..getNickName():" + nickName);
        } else {
            log.E("SRMiddleManage...登录返回.getNickName():" + nickName);
        }
        return nickName;
    }

    /**
     * 获取suid
     *
     * @return
     */
    public String getSuid() {
        String suid = LoginSharePreferencesUtil.getLoginSuid(mContext);
        if (StringUtil.isEmpty(suid)) {
            int termId = (int) (Math.random() * 10000000);
            suid = String.valueOf(termId);
            LoginSharePreferencesUtil.setLoginSuid(mContext, suid);
            log.E("SRMiddleManage..自动生成..getSuid():" + suid);
        } else {
            log.E("SRMiddleManage.登录返回...getSuid():" + suid);
        }
        return suid;
    }

    public int getMeetingState() {
        log.E("SRMiddleManage...当前会议的状态.getMeetingState():" + meetingState);
        return meetingState;
    }

    /**
     * 获取临时的昵称和suid
     */
    public void getTempNickNameOrUid() {
        String temp_nickName = LoginSharePreferencesUtil.getTempNikeName(mContext);
        String temp_suid = LoginSharePreferencesUtil.getTempSuid(mContext);
        if (StringUtil.isEmpty(temp_nickName)) {
            temp_nickName = "sr" + (int) (Math.random() * 10);
            log.E("SRMiddleManage..自动生成..temp_nickName:" + temp_nickName);
        }
        if (StringUtil.isEmpty(temp_suid)) {
            int termId = (int) (Math.random() * 10000000);
            temp_suid = String.valueOf(termId);
            log.E("SRMiddleManage..自动生成..temp_suid:" + temp_suid);
        }
        LoginSharePreferencesUtil.setTempNikeName(mContext, temp_nickName);
        LoginSharePreferencesUtil.setTempSuid(mContext, temp_suid);
        LoginSharePreferencesUtil.setLoginNikeName(mContext, temp_nickName);
        LoginSharePreferencesUtil.setLoginSuid(mContext, temp_suid);
    }
}
