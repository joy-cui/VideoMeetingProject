package org.suirui.huijian.box.html.callback;

import android.content.Context;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.pub.business.util.LoginSharePreferencesUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.util.StringUtil;
import com.suirui.srpaas.video.util.TvToolsUtil;


import org.suirui.huijian.box.AppApplication;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.bean.HistoryBean;
import org.suirui.huijian.box.html.interaction.ToJson;
import org.suirui.huijian.box.html.util.ToolsUtil;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.huijian.box.util.HistoryUtil;
import org.suirui.huijian.box.util.VersionUpdateUtil;
import org.suirui.transfer.SRSdkType;
import org.suirui.transfer.SRTransfer;

import java.util.List;

/**
 * //必须和js函数名字一致，注册好具体执行回调函数，类似java实现类。
 *
 * @authordingna
 * @date2017-08-05
 **/
public class JSCallbackNativeHandler {
    private static final String TAG = JSCallbackNativeHandler.class.getName();
    private static final SRLog log = new SRLog(TAG, AppConfigure.LOG_LEVE);
    private static String uid = "", nikeName = "", phone = "";
    public static void registerHandler(final Context context, String op, String data, CallBackFunction function) {
        if (context == null) return;
        switch (op) {
            case AppConfigure.MethodParam.getServerInfo:
                log.E("获取服务器地址");
                CallJsUtil.getInstance().onGetServerInfo(context,data,function);
                break;
            case AppConfigure.MethodParam.startMeeting:
                log.E("开始会议....");

                CallNativeUtil.getInstance().startMeeting(context, data, function);
                break;
            case AppConfigure.MethodParam.joinMeeting:
                log.E("加入会议....");
                CallNativeUtil.getInstance().joinMeeting(context, data, function);
                break;
            case AppConfigure.MethodParam.meetingList:
//                context.startActivity(new Intent(context, MeetingListActivity.class));

                    break;
            case AppConfigure.MethodParam.restart://重启
                CallJsUtil.getInstance().restart(context);
                break;
            case AppConfigure.MethodParam.shutDown://关机
                CallJsUtil.getInstance().shutDown();
                break;


            case AppConfigure.MethodParam.startAudioMeeting:
                log.E("开始音频会议....");
                uid = SRMiddleManage.getInstance().getSuid();
                nikeName = SRMiddleManage.getInstance().getNickName();
                ThirdApi.getIntance(context).startAudioMeeting(context, AppConfigure.pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN), AppConfigure.CONNECT_JOIN_URL, uid, nikeName, "", "");
                break;

            case AppConfigure.MethodParam.getHistory:
                uid = SRMiddleManage.getInstance().getSuid();
                log.E("历史记录....suid:" + uid);
                List<HistoryBean> historyList = HistoryUtil.getInstance(context).getHistoryListBySuid(uid);
//                List<HistoryBean> historyList = HistoryUtil.getInstance(context).getHistoryList();
                function.onCallBack(ToJson.getInstance().ToJsonListAddOP(AppConfigure.MethodParam.getHistory, new Gson().toJson(historyList), true));
                break;

            case AppConfigure.MethodParam.checkVersion:
                log.E("检查是否有版本更新");
                if (AppConfigure.VERSION_UPDATE)//配置文件配置，是否带更新功能
                    VersionUpdateUtil.getInstance().doAppUpdate(context, true);
                break;

            case AppConfigure.MethodParam.Login:
                IMiddleManage.getInstance().initServerAddr(AppApplication.getInstance());
                String pwd = ToolsUtil.getJsonValue(data, AppConfigure.RegisterMethod.password);
                log.E("登录..pwd:" + pwd);
                String phone = LoginSharePreferencesUtil.getLoginAccount(context);
                SRIMLoginClient.getAddAuthService().reqLogin(phone, pwd, AppConfigure.appId, null, SRIMConfigure.Login.HUIJIAN, false,null);
                break;

//            case AppConfigure.MethodParam.setParticipantOrderMeet:
//                String memberId = TvToolsUtil.getJsonData(data, AppConfigure.RegisterMethod.data, AppConfigure.RegisterMethod.memberId);
//                String memberName = TvToolsUtil.getJsonData(data, AppConfigure.RegisterMethod.data, AppConfigure.RegisterMethod.memberName);
//                String memberType = TvToolsUtil.getJsonData(data, AppConfigure.RegisterMethod.data, AppConfigure.RegisterMethod.memberType);
//                boolean memberChecked = TvToolsUtil.getJsonBooleanData(data, AppConfigure.RegisterMethod.data, AppConfigure.RegisterMethod.memberChecked);
//                log.E("设置点对点预约会议（JSCallbackNativeHandler）..memberId:" + memberId + " memberName:" + memberName + " memberType:" + memberType + "  memberChecked:" + memberChecked);
//                SharePreferencesUtil.setMember(context, memberId, memberName, memberType, memberChecked);
//                break;

//            case AppConfigure.MethodParam.getParticipantOrderMeet:
//                String members = SharePreferencesUtil.getMember(context);
//                log.E("获取点对点预约会议..members:" + members);
//                function.onCallBack(ToJson.getInstance().ToJsonObjectAddOP(AppConfigure.MethodParam.getParticipantOrderMeet, members, false));
//                break;

            case AppConfigure.MethodParam.clearHistoryList:
                log.E("清除历史记录");
                uid = SRMiddleManage.getInstance().getSuid();
                HistoryUtil.getInstance(context).clearHistoryListBySuid(uid);
//                HistoryUtil.getInstance(context).clearHistoryList();
                break;

            case AppConfigure.MethodParam.getCurrentVersion:
                String version = com.suirui.srpaas.video.util.ToolsUtil.getCurrentVersionNum();
                if (!StringUtil.isEmpty(version)) {
                    log.E("获取当前的版本号...version:" + version);
                    function.onCallBack(ToJson.getInstance().ToJsonKeyValue(AppConfigure.MethodParam.getCurrentVersion, AppConfigure.RegisterMethod.currentVersion, version));
                }
                break;
            default:
                log.E("default....op:" + op);
                SRTransfer.getInstance().callNative(context, SRSdkType.SRSdkType_SDK, data, function);
                break;
        }
    }
}

