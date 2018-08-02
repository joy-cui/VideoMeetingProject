package org.suirui.transfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.pub.business.manage.IMiddleManage;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.model.impl.ConfigureModelImpl;
import com.suirui.srpaas.video.third.ThirdApi;

import org.json.JSONObject;
import org.suirui.transfer.audio.MediaRecordMicTest;
import org.suirui.transfer.audio.MicTest;
import org.suirui.transfer.util.ToolsUtil;

/**
 * Created by hh on 2018/3/26.
 */

public class SRSdk implements SRSdkType{
    private static SRLog log = new SRLog(SRSdk.class.getName(), Config.LOG_LEVE);
    private static SRSdk mSRSdk;
    private static String uid = "", nikeName = "", phone = "";


    public static synchronized SRSdk getInstance() {
        if (mSRSdk == null) {
            mSRSdk = new SRSdk();
        }
        return mSRSdk;
    }

    public void registerHandler(final Context context,int interfaceType,String data, CallBackFunction function) {
        if (context == null) return;
        String op = ToolsUtil.getJsonValue(data, MethodName.OP);
        if (op == null) return;
        log.E("CallSDK handler+  op:" + op+"  data:= " + data );
        switch (op) {
            case MethodName.startMeeting:
//                log.E("开始会议....");
                startMeeting(context,interfaceType,data);
                break;
            case MethodName.startAudioMeeting:
                log.E("开始音频会议....");
//                uid = SRMiddleManage.getInstance().getSuid();
//                nikeName = SRMiddleManage.getInstance().getNickName();
//                ThirdApi.getIntance(context).startAudioMeeting(context, AppConfigure.pass_url_root, AppConfigure.appId, AppConfigure.secretKey, ThirdApi.getIntance(context).getSharePreferDoMain(AppConfigure.DO_MAIN), AppConfigure.CONNECT_JOIN_URL, uid, nikeName, "", "");
                break;
            case MethodName.joinMeeting:
                joinMeeting(context,interfaceType,data);
                break;
            case MethodName.Login:
                login(context,interfaceType,data);
               break;
            case MethodName.startTestingMic:
                startTestingMic(interfaceType,data,function);
                break;
            case MethodName.stopTestingMic:
                stopTestingMic(interfaceType,data,function);
                break;
            case MethodName.setMicVolume:
                stopTestingMic(interfaceType,data,function);
                break;
            default:
                log.E("default....op:" + op);
                break;
        }
    }

    MediaRecordMicTest mMediaRecordMicTest;

//------------------------------------------------------------------------------
    MicTest mMicTest;

    private void startTestingMic(int interfaceType,String data,CallBackFunction function){
        if(interfaceType == SRSdkType_SDK){
//            mMicTest = new MicTest();
//            mMicTest.start();

            mMediaRecordMicTest = new MediaRecordMicTest();
            mMediaRecordMicTest.startRecord();
//            try {
//                JSONObject obj = new JSONObject(data);
//                obj.put("status",true);
//                obj.put("msg","操作成功");
//                function.onCallBack(obj.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }else if(interfaceType == SRSdkType_UBOX){

        }
    }

    private void stopTestingMic(int interfaceType,String data,CallBackFunction function){
        if(interfaceType == SRSdkType_SDK) {
//            if (mMicTest != null) {
//                mMicTest.pause();
//            }
            if(mMediaRecordMicTest != null){
                mMediaRecordMicTest.stopRecord();
            }
//            try {
//                JSONObject obj = new JSONObject(data);
//                obj.put("status",true);
//                obj.put("msg","操作成功");
//                function.onCallBack(obj.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }else if(interfaceType == SRSdkType_UBOX){

        }

    }

    private void setAudioVolume(Context context,int volume){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
//        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,volume,0);
    }

    private void joinMeeting(Context context,int interfaceType,String data){
        try {
            JSONObject obj = new JSONObject(data);
            String confid = obj.getString("meetingID");
            String confPwd = obj.getString("password");

            String uid = obj.getString("uid");
            String nickName = obj.getString("nickName");
            String passUrlRoot = obj.getString("passUrlRoot");
            String appId = obj.getString("appId");
            String secretKey = obj.getString("secretKey");
            String joinUrl = obj.getString("joinUrl");
            String domain = obj.getString("domain");
            log.E("加入会议....uid:" + uid + "  confid:" + confid + " nickname:" + nikeName + " confPwd:" + confPwd);
            if(interfaceType == SRSdkType_SDK) {
                ThirdApi.getIntance(context).joinMeeting(context,
                        passUrlRoot, appId, secretKey,
                        ThirdApi.getIntance(context).getSharePreferDoMain(domain),
                        joinUrl, uid, nickName, confid, confPwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMeeting(Context context,int interfaceType,String data){
        try {
            JSONObject obj = new JSONObject(data);
            String uid = obj.getString("uid");
            String nickName = obj.getString("nickName");
            String passUrlRoot = obj.getString("passUrlRoot");
            String appId = obj.getString("appId");
            String secretKey = obj.getString("secretKey");
            String joinUrl = obj.getString("joinUrl");
            String domain = obj.getString("domain");
            if(interfaceType == SRSdkType_SDK) {
                ThirdApi.getIntance(context).startMeeting(context,
                        passUrlRoot, appId, secretKey,
                        ThirdApi.getIntance(context).getSharePreferDoMain(domain),
                        joinUrl, uid, nickName, "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(Context context,int interfaceType,String data){
        String pwd = ToolsUtil.getJsonValue(data, MethodName.Param.password);
        String phone = ToolsUtil.getJsonValue(data, MethodName.Param.account);
        String serverType = ToolsUtil.getJsonValue(data, MethodName.Param.serverType);
        String loginPassUrlRoot = ToolsUtil.getJsonValue(data, "loginPassUrlRoot");
        String appId = ToolsUtil.getJsonValue(data, "appId");
        String secretKey = ToolsUtil.getJsonValue(data, "secretKey");
        String joinUrl = ToolsUtil.getJsonValue(data, "joinUrl");
//        String domain = TvToolsUtil.getJsonValue(data, "domain");
        String serverhost = ToolsUtil.getJsonValue(data, MethodName.Param.serverhost);
        String domain = serverType + Configure.SPRITR + serverhost;
        //保存新设置的服务器地址
        if(interfaceType == SRSdkType_SDK) {
//            ThirdApi.getIntance(context).onLogin(context, phone, pwd, appId, secretKey, domain, passUrlRoot, joinUrl);
            ThirdApi.getIntance(context).updateAdressUrl(serverhost, serverType, true);
            ThirdApi.getIntance(context).initServerAddr(context, loginPassUrlRoot, appId, secretKey, domain, joinUrl);
            SRIMLoginClient.getAddAuthService().reqLogin(phone, pwd, Config.appId, null, SRIMConfigure.Login.HUIJIAN, false,"");
        }
    }
}
