package org.suirui.huijian.box;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.srpaas.capture.constant.CameraEntry;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.event.ShareEvent;
import com.suirui.srpaas.video.passsdk.manages.plug.VideoConfiguration;
import com.suirui.srpaas.video.passsdk.manages.plug.VideoPlugManage;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.VideoProperties;
import com.tencent.bugly.crashreport.CrashReport;

import org.suirui.huijian.box.manage.SRMiddleManage;
import org.suirui.srpaas.sdk.SRPaasSDK;

//import android.support.multidex.MultiDex;

/**
 * Created by cui.li on 2017/5/23.
 */

public class AppApplication extends Application {
    private static final SRLog log = new SRLog(AppApplication.class.getName(), AppConfigure.LOG_LEVE);
    private static AppApplication instance;
    public int count = 0;

    public static AppApplication getInstance() {
        return instance;
    }

//   IM错误码文档官网地址:  http://www.yuntongxun.com/doc/sdk/sdkabout/2_1_1_3.html
    @Override
    public void onCreate() {
//        MultiDex.install(this);
        super.onCreate();
        log.E("AppApplication..>>>>>>>>>>>>>>>>>>>onCreate()..");
        NetStateReceiver.registerNetworkStateReceiver(this);
        instance = this;
        CrashReport.initCrashReport(getApplicationContext(), "e19044424a", false);
        SRPaasSDK.getInstance().initOkHttpSSL();//https获取证书问题
        String doMain = ThirdApi.getIntance(this).initVideoProperties(AppConfigure.HTTPREQUEST, AppConfigure.SERVER_ADRESS);//初始化配置文件地址
        SrHuiJianProperties.loadProperties(doMain,this);//加载会见的配置文件
        VideoProperties.loadProperties(doMain);//加载video配置文件
        SRMiddleManage.getInstance().init(getApplicationContext(), false);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                log.E("AppApplication...>>>>>>>>>>>>>>>>>>>onActivityCreated  ...activity:" + activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    Configure.isForntOrBack = true;
                    ShareEvent.getInstance().onForntOrBackApp(true);
                    log.E("AppApplication..>>>>>>>>>>>>>>>>>>>切到前台  lifecycle...activity:" + activity);
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                log.E("AppApplication...>>>>>>>>>>>>>>>>>>>onActivityResumed  ...activity:" + activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                log.E("AppApplication...>>>>>>>>>>>>>>>>>>>onActivityPaused  ...activity:" + activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    Configure.isForntOrBack = false;
                    ShareEvent.getInstance().onForntOrBackApp(false);
                    log.E("AppApplication...>>>>>>>>>>>>>>>>>>>切到后台  lifecycle...activity:" + activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                log.E("AppApplication...>>>>>>>>>>>>>>>>>>>onActivitySaveInstanceState  ...activity:" + activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                log.E("AppApplication...>>>>>>>>>>>>>>>>>>>>>>>>onActivityDestroyed........activity:" + activity);
            }
        });
//        PreferenceUtil.initialize(this);
//        initVideoPlugin();//测试插件配置
//        VideoPlugManage.getManager().setDeviveType(CameraEntry.DeviceType.box);//设置当前版本的设备
        VideoPlugManage.getManager().setH264Decode(true);
        VideoPlugManage.getManager().setVideoCode(true);//H264 是否采取硬边
        VideoPlugManage.getManager().setCameraCaptureSize(1280,720);//设置采集大小1920*1080
        VideoPlugManage.getManager().setSelectType(AppConfigure.VideoType.SR_CFG_VIDEO_SIZE_720P.getValue(),AppConfigure.VideoType.SR_CFG_VIDEO_SIZE_360P.getValue());//设置大小选流
//        VideoPlugManage.getManager().setOnKey(true);
    }

    /**
     * 程序退出，是否清除数据
     *
     * @param isClear
     */
    public void release(boolean isClear) {
        if (isClear) {
            SRMiddleManage.getInstance().clear();
        }
        log.E("AppApplication....release..." + android.os.Process.myPid());
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    private void initVideoPlugin(){
        VideoConfiguration videoConfiguration = new VideoConfiguration.VideoConfigBuilder(this)
                .notifyIcon(R.drawable.ic_launcher)//消息通知图标
                .notifAppName("会见")//app名称
                .setInvite(false)//是否有邀请功能
                .setSRLog(false)//是否打开log
                .setSRMessageAlert(false)//是否显示入会/离开/结束会议的提示信息
                .setSREndMeetingDialog(false)//是否显示离开结束会议的弹框
                .build();
        VideoPlugManage.getManager().init(videoConfiguration);

    }
}
