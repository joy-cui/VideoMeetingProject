package org.suirui.huijian.tv;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.serenegiant.usb.UVCCamera;
import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.version.util.InstallUtil;
import com.suirui.pub.business.SRIMLoginClient;
import com.suirui.pub.business.contant.SRIMConfigure;
import com.suirui.srpaas.base.NetBean;
import com.suirui.srpaas.base.util.BaseUtil;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.receiver.NetChangeObserver;
import com.suirui.srpaas.base.util.receiver.NetStateReceiver;
import com.suirui.srpaas.common.http.okhttp.utils.NetworkUtil;
import com.suirui.srpaas.video.contant.Configure;
import com.suirui.srpaas.video.event.ShareEvent;
import com.suirui.srpaas.video.passsdk.manages.plug.VideoConfiguration;
import com.suirui.srpaas.video.passsdk.manages.plug.VideoPlugManage;
import com.suirui.srpaas.video.third.ThirdApi;
import com.suirui.srpaas.video.third.VideoProperties;
import com.tencent.bugly.crashreport.CrashReport;

import org.suirui.huijian.tv.activity.LogoActivity;
import org.suirui.huijian.tv.activity.SettingActivity;
import org.suirui.huijian.tv.activity.login.FragmentFail;
import org.suirui.huijian.tv.activity.login.FragmentLogo;
import org.suirui.huijian.tv.activity.login.FragmentMange;
import org.suirui.huijian.tv.activity.login.FragmentNetFail;
import org.suirui.huijian.tv.activity.setting.BaseFragment;
import org.suirui.huijian.tv.activity.setting.FragmentSettingNet;
import org.suirui.huijian.tv.util.AppUtil;
import org.suirui.huijian.tv.util.LoginUtil;
import org.suirui.huijian.tv.util.TVNetworkUtil;
import org.suirui.huijian.tv.util.TVPreferenceUtil;
import org.suirui.huijian.tv.util.TVStringUtil;
import org.suirui.huijian.tv.util.VersionUpdateUtil;
import org.suirui.huijian.tv.widget.ConfrimDialog;
import org.suirui.srpaas.contant.SRPaaSdkConfigure;
import org.suirui.srpaas.sdk.SRPaasSDK;
import org.suirui.transfer.SRSdkType;
import org.suirui.transfer.SRTransfer;

/**
 * Created by cui.li on 2017/5/23.
 */

public class TVAppApplication extends Application{
    private static final SRLog log = new SRLog(TVAppApplication.class.getName(), TVAppConfigure.LOG_LEVE);
    private static TVAppApplication instance;
    public int count = 0;
    private NetChangeObserver mNetChangeObserver;
    private Activity mCurActivity;
    private Handler mWorkHandler;
    private HandlerThread mWorkThread = new HandlerThread(
            TVAppApplication.class.getName()+" work thread");

    private static final int net_Connected_From_PingIP = 100;
    private static final int check_sdcard = 101;

    public static TVAppApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

//   IM错误码文档官网地址:  http://www.yuntongxun.com/doc/sdk/sdkabout/2_1_1_3.html
    @Override
    public void onCreate() {
//        MultiDex.install(this);
        super.onCreate();
        TVStringUtil.initWriteToFile();
        registerReceiver();
        log.E("TVAppApplication..>>>>>>>>>>>>loadServer_URL>>>>>>>onCreate()..");
        if(AppUtil.getInstance().isShowRootUI()){
            TVNetworkUtil.getInstance(this).setWifiApOpen(true);
            TVNetworkUtil.getInstance(this).createWifiAP(TVAppConfigure.WIFI_AP_NAME,TVAppConfigure.WIFI_AP_PWD);
        }
        SRTransfer.getInstance().setSDKType(SRSdkType.SRSdkType_SDK);
        NetStateReceiver.registerNetworkStateReceiver(this);
        netChangeRealization();
//        netConnectedFromPingIP();
        if (!mWorkThread.isAlive()) {
            startWorkThread();
        }
        checkSDcard();
        instance = this;
        CrashReport.initCrashReport(getApplicationContext(), "e19044424a", false);
        SRPaasSDK.getInstance().initOkHttpSSL();//https获取证书问题
        String doMain = ThirdApi.getIntance(this).initVideoProperties(TVAppConfigure.HTTPREQUEST, TVAppConfigure.SERVER_ADRESS);//初始化配置文件地址
        TVSrHuiJianProperties.loadProperties(doMain,this,null);//加载会见的配置文件
        VideoProperties.loadProperties(doMain);//加载video配置文件
        log.E("TVAppApplication..>>>>>>>>>>>>>>>>>>>onCreate().."+TVAppConfigure.SERVER_ADRESS);
        SRIMConfigure.isIMLogin=false;
        SRIMLoginClient.init(this);
//        LoginUtil.getInstance().initWebSocket("ws://192.168.61.39:8088");
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>onActivityCreated  ...activity:" + activity);
                mCurActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    Configure.isForntOrBack = true;
                    ShareEvent.getInstance().onForntOrBackApp(true);
//                    log.E("TVAppApplication..>>>>>>>>>>>>>>>>>>>切到前台  lifecycle...activity:" + activity);
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
//                log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>onActivityResumed  ...activity:" + activity);
                mCurActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
//                log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>onActivityPaused  ...activity:" + activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    Configure.isForntOrBack = false;
                    ShareEvent.getInstance().onForntOrBackApp(false);
//                    log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>切到后台  lifecycle...activity:" + activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>onActivitySaveInstanceState  ...activity:" + activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                log.E("TVAppApplication...>>>>>>>>>>>>>>>>>>>>>>>>onActivityDestroyed........activity:" + activity);
            }
        });
        TVPreferenceUtil.initialize(this);
//        initVideoPlugin();//测试插件配置
        boolean isMediaCodecHard=AppUtil.getInstance().isSupportMediaCodecHardDecoder();
        log.E("TVAppApplication...isSupportMediaCodecHardDecoder。。。。isMediaCodecHard:"+isMediaCodecHard);
        if(isMediaCodecHard) {
            VideoPlugManage.getManager().setVideoCode(true);//H264 是否采取硬解
        }
//        VideoPlugManage.getManager().setCaptureFps(16);//设置采集帧率,默认15帧
        VideoPlugManage.getManager().setCameraCaptureSize(1280,720);//设置采集大小1920*1080
        VideoPlugManage.getManager().setSelectType(TVAppConfigure.VideoType.SR_CFG_VIDEO_SIZE_720P.getValue(), TVAppConfigure.VideoType.SR_CFG_VIDEO_SIZE_360P.getValue());//设置大小选流
        VideoPlugManage.getManager().setDeviveType(CameraEntry.DeviceType.box);//设置当前版本的设备(box启动相机的时候，直接打开dev/video,mobile采用自带的camera)
        VideoPlugManage.getManager().setPlatformType(SRPaaSdkConfigure.SREnginePlatFormType.SR_PLATFORM_TYPE_ANDROID_BOX);//平台类型
        VideoPlugManage.getManager().setUsbCamera(false);//是否打开usb摄像头(4k的盒子不支持usb摄像头，false采用的自带的API打开)
        VideoPlugManage.getManager().setUsbCameraPreviewType(UVCCamera.PIXEL_FORMAT_YUV420SP);//UVCCamera.PIXEL_FORMAT_MJPEG  UVCCamera.PIXEL_FORMAT_YUV420SP
    }

    /**
     * 程序退出，是否清除数据
     *
     * @param isClear
     */
    public void release(boolean isClear) {
        log.E("TVAppApplication....release..." + android.os.Process.myPid());
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

    private int connectFlag = 0; //解决多次接收网络状态导致页面多次显示重叠
    //网络监听
    protected void netChangeRealization() {
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetBean type) {
                super.onNetConnected(type);

                TVStringUtil.writeToFile("TVApplication","TVApplication。。NetStateReceiver....网络连接成功");
                log.E("。。NetStateReceiver....网络连接成功");
                if(isInSettingNet()){
                    return;
                }
                connectFlag = 1;
                enterActivity(true);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                TVStringUtil.writeToFile("TVApplication","TVApplication。。onNetworkDisConnected....网络断开");
                log.E("。。onNetworkDisConnected....网络断开");
                if(isInSettingNet()){
                    return;
                }
                if(connectFlag == 2){
                    log.E("。。onNetworkDisConnected....网络断开-----已经接收了");
                    return;
                }
                LoginUtil.getInstance().setLoginStatus(getApplicationContext(),SRIMConfigure.State.hj_Login_fail);
                connectFlag = 2;
                enterActivity(false);


            }
        };
        NetStateReceiver.registerObserver(mNetChangeObserver);
    }

    private void enterActivity(boolean isConnected) {
        TVStringUtil.writeToFile("TVApplication", "TVApplication。。enterActivity....网络：" + isConnected );
        if (ThirdApi.getIntance(this).getMeetingState()) {
            return;
        }

        if (Configure.isForntOrBack) {
            TVStringUtil.writeToFile("TVApplication", "TVApplication。。enterActivity....网络：" + isConnected + " mCurActivity:" + mCurActivity);
            TVStringUtil.writeToFile("TVApplication", "TVApplication。。enterActivity....mCurActivity instanceof LogoActivity：" + (mCurActivity instanceof LogoActivity));
            if (isConnected) {
                if (mCurActivity instanceof LogoActivity) {
                    final BaseFragment ft = (BaseFragment) ((LogoActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
                    TVStringUtil.writeToFile("TVApplication", "TVApplication。11111。enterActivity...：" + " BaseFragment:" + ft);
                    TVStringUtil.writeToFile("TVApplication", "TVApplication。11111。enterActivity....ft instanceof FragmentLogo：" + (ft instanceof FragmentLogo));
                    if (ft instanceof FragmentLogo) {
                        TVStringUtil.writeToFile("TVApplication", "TVApplication。1111----in fragmentlogo-----");
                    } else {
                        TVStringUtil.writeToFile("TVApplication", "TVApplication。11111----连接成功跳转logo-----");
                        mCurActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentMange.getInstance().showFragmentLogo(ft,
                                        LoginUtil.getInstance().getHttp(TVAppApplication.this),
                                        LoginUtil.getInstance().getHost(TVAppApplication.this),
                                        LoginUtil.getInstance().getNickName(TVAppApplication.this)
                                );
                            }
                        });

                    }
                }

            } else {

                if (mCurActivity instanceof LogoActivity) {
                    BaseFragment ft = (BaseFragment) ((LogoActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
                    TVStringUtil.writeToFile("TVApplication", "TVApplication。2222。enterActivity...：" + " BaseFragment:" + ft);
                    TVStringUtil.writeToFile("TVApplication", "TVApplication。2222。enterActivity....ft instanceof FragmentLogo：" + (ft instanceof FragmentLogo));
                    if (ft instanceof FragmentNetFail || ft instanceof FragmentSettingNet) {

                    } else {
                        TVStringUtil.writeToFile("TVApplication", "TVApplication。--------------------------------");
                        FragmentMange.getInstance().showFragmentNetFail(ft);
                    }
                }else  if (mCurActivity instanceof SettingActivity) {
                    BaseFragment ft = (BaseFragment) ((SettingActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
                    if (ft instanceof FragmentSettingNet) {

                    }else {
                        Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
                        intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_NET_FAIL);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent);
                    }
                }else {
                    TVStringUtil.writeToFile("TVApplication", "TVApplication。2222。enterActivity....skip LogoActivity");
                    Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
                    intent.putExtra("type", FragmentLogo.ShowUIStatus.SHOW_NET_FAIL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(intent);
                }

            }
        }

    }

    private boolean isInSettingNet(){
        if(mCurActivity == null){
            return false;
        }
        if (mCurActivity instanceof LogoActivity){
            BaseFragment ft = (BaseFragment) ((LogoActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
            if (ft instanceof FragmentSettingNet) {
                return true;
            }
        }else if (mCurActivity instanceof SettingActivity){
            BaseFragment ft = (BaseFragment) ((SettingActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
            if (ft instanceof FragmentSettingNet) {
                return true;
            }
        }
        return false;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log.E("mReceiver Action " + action);
            if (EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                    /*接收到以太网状态改变的广播*/
                int EtherState = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
                handleEtherStateChange(EtherState);
            }
        }
    };

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    /*
     *
    */
    private void handleEtherStateChange(int EtherState) {
        log.E("TVApplication---handleEtherStateChange" + EtherState);
        String mStatusString = "";
        TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。-------");
        if(isInSettingNet()){
            return;
        }
        switch (EtherState) {
            case EthernetManager.ETHER_STATE_DISCONNECTED:
                mStatusString = "网络断开。。。";
                TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。----网络断开。。---");
                Toast.makeText(this,"网络断开",Toast.LENGTH_SHORT).show();
                break;
            case EthernetManager.ETHER_STATE_CONNECTING:
                mStatusString = this.getResources().getString(R.string.ethernet_info_getting);
                TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。---正在连接----");
                Toast.makeText(this,"正在连接",Toast.LENGTH_SHORT).show();
                break;
            case EthernetManager.ETHER_STATE_CONNECTED:
                TVStringUtil.writeToFile("TVApplication", "handleEtherStateChange。----连接成功---");
                log.E("。。NetStateReceiver....网络连接成功");
                Toast.makeText(this,"网络连接成功",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void netConnectedFromPingIP(){
        if (!mWorkThread.isAlive()) {
            log.D("TVApplication......netConnectedFromPingIP():-");
            startWorkThread();
        }
        mWorkHandler.sendEmptyMessage(net_Connected_From_PingIP);
    }

    private boolean isPingIPPreFlag = false;//标记pingip结果
    private void pingIP(){
        if (ThirdApi.getIntance(this).getMeetingState()) {
            return;
        }
        boolean isConnected = NetworkUtil.isEthernetConnected(TVAppApplication.this);
//        Toast.makeText(this,"网络isConnected:"+isConnected,Toast.LENGTH_SHORT).show();
        String ip = LoginUtil.getInstance().getHost(TVAppApplication.this);
//        boolean isConnected = TVNetworkUtil.getInstance(TVAppApplication.this).ping(ip,1);
        log.E("。。netConnectedFromPingIP():ip:"+ ip + " mCurActivity:"+mCurActivity +"  isConnected:"+isConnected);

        if(!isConnected){
            isPingIPPreFlag = isConnected;
            LoginUtil.getInstance().setLoginStatus(getApplicationContext(),SRIMConfigure.State.hj_Login_fail);
            if (mCurActivity != null) {
                if (mCurActivity instanceof LogoActivity) {
                    BaseFragment ft = (BaseFragment) ((LogoActivity) mCurActivity).getSupportFragmentManager().findFragmentById(R.id.main_content);
                    if (ft instanceof FragmentFail) {

                    } else {

                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
                    intent.putExtra("type", FragmentLogo.ShowUIStatus.SERVER_CONNECT_FAIL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(intent);
                }
            }
        }else{
//                            NetUtils.getNetType(TVAppApplication.this);
            if(!isPingIPPreFlag){
                enterActivity(true);
//                mNetChangeObserver.onNetConnected(TVNetworkUtil.getNetType(TVAppApplication.this));
            }
            isPingIPPreFlag = isConnected;
        }
        mWorkHandler.removeMessages(net_Connected_From_PingIP);
        mWorkHandler.sendEmptyMessageDelayed(net_Connected_From_PingIP,5000);
    }
    ConfrimDialog mSdcardWarningDialog;
    private void checkSDcard(){
        try {
            if (BaseUtil.checkSDCard()) {
                long available = BaseUtil.getAvailableExternalMemorySize();
                if (available < 1024 * 1024 ) {
                    if(mSdcardWarningDialog == null && mCurActivity != null){
                        mSdcardWarningDialog = new ConfrimDialog(mCurActivity);
                    }
                    if(mSdcardWarningDialog != null){
                        if(!mSdcardWarningDialog.isShowing()) {
                            mSdcardWarningDialog.show();
                        }
                        mSdcardWarningDialog.setWarningMsg(R.string.m_sdcard_size_not_enough_text);


                        mSdcardWarningDialog.setButton1(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSdcardWarningDialog.dismiss();
                            }
                        });

                        mSdcardWarningDialog.setButton2(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppUtil.getInstance().skipSet(mCurActivity);
                                mSdcardWarningDialog.dismiss();
                            }
                        });
                    }

                }else{
                    if(mSdcardWarningDialog != null && mSdcardWarningDialog.isShowing()){
                        mSdcardWarningDialog.dismiss();
                    }
                }
            } else {
                if(mSdcardWarningDialog == null  && mCurActivity != null) {
                    mSdcardWarningDialog = new ConfrimDialog(mCurActivity);
                }

                if(mSdcardWarningDialog != null){
                    if (!mSdcardWarningDialog.isShowing()) {
                        mSdcardWarningDialog.show();
                    }
                    mSdcardWarningDialog.setWarningMsg(R.string.m_no_sdcard_text);
                    mSdcardWarningDialog.setButton1(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSdcardWarningDialog.dismiss();
                        }
                    });

                    mSdcardWarningDialog.setButton2(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VersionUpdateUtil.getInstance(mCurActivity).doDownloadVersion(getString(R.string.m_no_available_sdcard_text), false);
                            mSdcardWarningDialog.dismiss();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWorkHandler.removeMessages(check_sdcard);
        mWorkHandler.sendEmptyMessageDelayed(check_sdcard,5000);
    }

    private void startWorkThread() {
        log.D("startWorkThread()");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case net_Connected_From_PingIP:
                        pingIP();
                        break;
                    case check_sdcard:
                        checkSDcard();
                        break;

                }
            }
        };
    }
}
