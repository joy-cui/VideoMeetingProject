package org.suirui.huijian.box.html.callback;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.suirui.srpaas.base.util.log.SRLog;

import org.json.JSONObject;
import org.suirui.huijian.box.AppConfigure;
import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.transfer.Config;
import org.suirui.transfer.SRSdkType;
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

    public void startMeeting(Context context,String data, CallBackFunction function){
        try {
            String uid = SRMiddleManage.getInstance().getSuid();
            String nikeName = SRMiddleManage.getInstance().getNickName();
            String pass_url_root = AppConfigure.pass_url_root;
            String appId = AppConfigure.appId;
            String secretKey = AppConfigure.secretKey;
            String joinurl = AppConfigure.CONNECT_JOIN_URL;
            String domain = AppConfigure.DO_MAIN;

            JSONObject obj = new JSONObject(data);
            obj.put("uid", uid);
            obj.put("nickName",nikeName);
            obj.put("passUrlRoot", pass_url_root);
            obj.put("appId",appId);
            obj.put("secretKey", secretKey);
            obj.put("joinUrl",joinurl);
            obj.put("domain",domain);
            SRTransfer.getInstance().callNative(context, SRSdkType.SRSdkType_SDK, obj.toString(), function);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinMeeting(Context context, String data, CallBackFunction function){
        try {
            String uid = SRMiddleManage.getInstance().getSuid();
            String nickname = SRMiddleManage.getInstance().getNickName();
            String pass_url_root = AppConfigure.pass_url_root;
            String appId = AppConfigure.appId;
            String secretKey = AppConfigure.secretKey;
            String joinurl = AppConfigure.CONNECT_JOIN_URL;
            String domain = AppConfigure.DO_MAIN;

            JSONObject obj = new JSONObject(data);
            obj.put("uid", uid);
            obj.put("nickName", nickname);
            obj.put("passUrlRoot", pass_url_root);
            obj.put("appId",appId);
            obj.put("secretKey", secretKey);
            obj.put("joinUrl",joinurl);
            obj.put("domain",domain);

            SRTransfer.getInstance().callNative(context, SRSdkType.SRSdkType_SDK, obj.toString(), function);
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


}
