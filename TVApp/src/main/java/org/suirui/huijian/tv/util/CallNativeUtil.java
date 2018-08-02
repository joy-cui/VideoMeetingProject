package org.suirui.huijian.tv.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.json.JSONObject;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.transfer.Config;
import org.suirui.transfer.MethodName;
import org.suirui.transfer.SRTransfer;

/**
 * Created by hh on 2018/3/26.
 */

public class CallNativeUtil {
    private static SRLog log = new SRLog(CallNativeUtil.class.getName(), Config.LOG_LEVE);

    private Activity mActivity;
    private static CallNativeUtil mCallNativeUtil;
    public static synchronized CallNativeUtil getInstance() {
        if (mCallNativeUtil == null) {
            mCallNativeUtil = new CallNativeUtil();
        }
        return mCallNativeUtil;
    }

    public void startTestMic(Context context){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.startTestingMic);
            SRTransfer.getInstance().callNative(context, obj.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTestMic(Context context){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.stopTestingMic);
            SRTransfer.getInstance().callNative(context, obj.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(Context context,String account,String pwd,String serverHost,String serverType){
        try {
//            {"op":"login","account":"1511111111112","password":"123456",
//               "serverHost":"192.168.61.39","serverType":"http","serverip":""}
            JSONObject obj = new JSONObject();
            obj.put("op", "login");
            obj.put("account", account);
            obj.put("password",pwd);
            obj.put("serverHost", serverHost);
            obj.put("serverType",serverType);
            obj.put("serverip","");
            login(context, obj.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(Context context,String data, CallBackFunction function){
        try {
            String loginPassUrlRoot = TVAppConfigure.login_pass_url_root;
            String appId = TVAppConfigure.appId;
            String secretKey = TVAppConfigure.secretKey;
            String joinurl = TVAppConfigure.CONNECT_JOIN_URL;


            JSONObject obj = new JSONObject(data);
//            obj.put("op", "login");
//            obj.put("account", account);
//            obj.put("password",pwd);
//            obj.put("serverHost", serverHost);
//            obj.put("serverType",serverType);
//            obj.put("serverip","");
            obj.put("loginPassUrlRoot", loginPassUrlRoot);
            obj.put("appId",appId);
            obj.put("secretKey", secretKey);
            obj.put("joinUrl",joinurl);
//            obj.put("domain",domain);
            SRTransfer.getInstance().callNative(context, obj.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMeeting(Context context){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.startMeeting);
            startMeeting(context, obj.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMeeting(Context context,String data, CallBackFunction function){
        try {
            String uid = LoginUtil.getInstance().getSuid(context);
            String nikeName = LoginUtil.getInstance().getNickName(context);
            String pass_url_root = TVAppConfigure.pass_url_root;
            String appId = TVAppConfigure.appId;
            String secretKey = TVAppConfigure.secretKey;
            String joinurl = TVAppConfigure.CONNECT_JOIN_URL;
            String domain = TVAppConfigure.DO_MAIN;

            JSONObject obj = new JSONObject(data);
            obj.put("uid", uid);
            obj.put("nickName",nikeName);
            obj.put("passUrlRoot", pass_url_root);
            obj.put("appId",appId);
            obj.put("secretKey", secretKey);
            obj.put("joinUrl",joinurl);
            obj.put("domain",domain);
            SRTransfer.getInstance().callNative(context, obj.toString(), function);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinMeeting(Context context,String meetId,String meetPwd){
        try {
            JSONObject obj = new JSONObject();
            obj.put("op", MethodName.joinMeeting);
            obj.put("meetingID", meetId);
            obj.put("password", meetPwd);
            log.E("joinMeeting...."+obj.toString());
            joinMeeting(context, obj.toString(), (CallBackFunction)null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinMeeting(Context context, String data, CallBackFunction function){
        try {
            String uid = LoginUtil.getInstance().getSuid(context);
            String nikeName = LoginUtil.getInstance().getNickName(context);
            String pass_url_root = TVAppConfigure.pass_url_root;
            String appId = TVAppConfigure.appId;
            String secretKey = TVAppConfigure.secretKey;
            String joinurl = TVAppConfigure.CONNECT_JOIN_URL;
            String domain = TVAppConfigure.DO_MAIN;
           log.E("joinMeeting..加入会议.."+data);

            JSONObject obj = new JSONObject(data);
            obj.put("uid", uid);
            obj.put("nickName", nikeName);
            obj.put("passUrlRoot", pass_url_root);
            obj.put("appId",appId);
            obj.put("secretKey", secretKey);
            obj.put("joinUrl",joinurl);
            obj.put("domain",domain);

            SRTransfer.getInstance().callNative(context,obj.toString(), function);
           } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isHaveCamera(Context context){
        PackageManager pm = context.getPackageManager();
        // FEATURE_CAMERA - 后置相机
        // FEATURE_CAMERA_FRONT - 前置相机
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    public void setAudioVolume(Context context,int volume){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
//        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,volume,0);
    }

    public void setMusicVolume(Context context,int volume){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
//        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
    }

    public int getMusicMaxVolume(Context context){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        return max;
    }
    public int getMusicCurVolume(Context context){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        return current;
    }

    public void getAudioVolume(Context context){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //通话音量
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
        log.E("getAudioVolume():STREAM_VOICE_CALL  max : " + max + " current : " + current);

        //系统音量
        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
        current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        log.E("getAudioVolume(): STREAM_SYSTEM max :" + max + " current : " + current);

        //铃声音量
        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING );
        current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );
        log.E("getAudioVolume(): STREAM_RING max : " + max + " current :" + current);

        //音乐音量
        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        log.E("getAudioVolume(): STREAM_MUSIC max : " + max + " current : " + current);

        //提示声音音量
        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM );
        current = mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM );
        log.E("getAudioVolume():  STREAM_ALARM max : " + max + " current : " + current);
    }


    public void restart(){
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
    }
}
